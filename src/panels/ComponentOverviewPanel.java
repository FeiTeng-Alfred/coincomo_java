/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package panels;

import core.COINCOMOComponent;
import core.COINCOMOConstants;
import core.COINCOMOConstants.Scenario;
import core.COINCOMOSubComponent;
import core.COINCOMOUnit;
import database.COINCOMOComponentManager;
import database.COINCOMOSubComponentManager;
import dialogs.ComponentScaleFactorsDialog;
import dialogs.ComponentScheduleDriverDialog;
import dialogs.RiskDialog;
import dialogs.SubComponentEAFDialog;
import dialogs.SubComponentSizeDialog;
import extensions.COINCOMOCheckBoxCellEditor;
import extensions.COINCOMOCheckBoxTableCellRenderer;
import extensions.COINCOMOClefTableCellRenderer;
import extensions.COINCOMOClefTableHeaderRenderer;
import extensions.COINCOMOComboBoxCellEditor;
import extensions.COINCOMOFixedTable;
import extensions.COINCOMOVector;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import main.COINCOMO;
import main.GlobalMethods;
import main.Icons;

/**
 *
 * @author Raed Shomali
 */
public class ComponentOverviewPanel extends JPanel implements ActionListener, ItemListener, MouseListener {
    // Component Being Analyzed ...

    private COINCOMO coincomo = null;
    private COINCOMOComponent component = null;
    // Buttons
    private JButton addSubComponentButton = new JButton("Subcomponent");
    private JButton deleteSubComponentButton = new JButton("Subcomponent(s)");
    private JButton scaleFactorsButton = new JButton("Scale Factor");
    private JButton scheduleButton = new JButton("Schedule (SCED)");
    // Combo Box
    private JComboBox comboBox = new JComboBox();
    // Tables
    private DefaultTableModel clefTableModel = new DefaultTableModel();
    private COINCOMOFixedTable clefTable;
    private JTextPane estimationTextPane = new JTextPane();
    private JScrollPane estimationScroller = new JScrollPane(estimationTextPane);
    private JSplitPane splitPane = new JSplitPane();
    private static DecimalFormat format1Decimal = new DecimalFormat("0.0");
    private static DecimalFormat format1DecimalWithComma = new DecimalFormat("#,##0.0");
    private static DecimalFormat format2Decimals = new DecimalFormat("0.00");
    private static DecimalFormat format2DecimalWithComma = new DecimalFormat("#,##0.00");
    //private UpdateEstimationReport reportUpdater = null;
    //private RefreshSubComponents refreshThread = null;
    private boolean isLoading = true;
    private OverviewsAndGraphsPanel overviewsAndGraphsPanel = null;
    // Hold temporary new name / new labor rate value
    private String tempName = null;
    private String tempRate = null;

    /**
     *
     * @param component is used to view its content on the panel
     */
    public ComponentOverviewPanel(COINCOMO coincomo, OverviewsAndGraphsPanel oAGPanel, COINCOMOComponent component) {

        this.coincomo = coincomo;
        this.overviewsAndGraphsPanel = oAGPanel;

        this.component = component;

        this.clefTable = new COINCOMOFixedTable(clefTableModel);

        // ComboBox      
        comboBox.addItemListener(this);

        comboBox.setPreferredSize(new Dimension(125, 25));

        comboBox.setBackground(Color.WHITE);
        comboBox.setRenderer(new COINCOMOComboBoxCellEditor());

        comboBox.addItem(" More Actions ...");
        comboBox.addItem("SEPARATOR");
        comboBox.addItem(" Select All");
        comboBox.addItem(" Toggle");
        comboBox.addItem(" Unselect All");
        comboBox.addItem(" Copy ");
        comboBox.addItem(" Paste ");

        // Buttons
        scaleFactorsButton = new JButton("Scale Factor");
        scaleFactorsButton.setText("Scale Factor " + format2Decimals.format(component.getSF()));

        addSubComponentButton.setIcon(Icons.ADD_SUBCOMPONENT_ICON);
        deleteSubComponentButton.setIcon(Icons.DELETE_SUBCOMPONENT_ICON);

        addSubComponentButton.addActionListener(this);
        deleteSubComponentButton.addActionListener(this);
        scaleFactorsButton.addActionListener(this);
        scheduleButton.addActionListener(this);

        addSubComponentButton.setFocusable(false);
        deleteSubComponentButton.setFocusable(false);
        scaleFactorsButton.setFocusable(false);
        scheduleButton.setFocusable(false);

        addSubComponentButton.setPreferredSize(new Dimension(150, 25));
        deleteSubComponentButton.setPreferredSize(new Dimension(160, 25));
        scaleFactorsButton.setPreferredSize(new Dimension(150, 25));
        scheduleButton.setPreferredSize(new Dimension(130, 25));

        // Split Pane
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(195);

        // Text Pane
        TitledBorder dictionaryTitleBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Estimation");
        dictionaryTitleBorder.setTitleColor(Color.BLUE);
        dictionaryTitleBorder.setTitlePosition(TitledBorder.BELOW_TOP);
        dictionaryTitleBorder.setTitleJustification(TitledBorder.CENTER);
        estimationTextPane.setEditable(false);
        estimationScroller.setBorder(dictionaryTitleBorder);
        estimationTextPane.setContentType("text/html");
        estimationTextPane.setMargin(new Insets(10, 10, 10, 10));

        // To Avoid Unnecessary Refreshes
        isLoading = true;

        updateEstimationTextPane(false);

        isLoading = false;

        // Clear
        clefTableModel = new DefaultTableModel();
        clefTable = new COINCOMOFixedTable(clefTableModel);

        // Table
        clefTable.setRowSelectionAllowed(false);
        clefTable.addMouseListener(this);

        // Html Was added to be able to make it Multiline ..
        clefTableModel.addColumn("X");
        clefTableModel.addColumn("Name");
        clefTableModel.addColumn("Size");
        clefTableModel.addColumn("<html><body style='text-align:center'>Labor<br />Rate<br />($/Month)</body></html>");
        clefTableModel.addColumn("EAF");
        clefTableModel.addColumn("Language");
        clefTableModel.addColumn("<html><body style='text-align:center'>NOM<br />Effort<br />DEV</body></html>");
        clefTableModel.addColumn("<html><body style='text-align:center'>EST<br />Effort<br />DEV</body></html>");
        clefTableModel.addColumn("PROD");
        clefTableModel.addColumn("COST");
        clefTableModel.addColumn("<html><body style='text-align:center'>INST<br />COST</body></html>");
        clefTableModel.addColumn("Staff");
        clefTableModel.addColumn("Risk");

        COINCOMOClefTableHeaderRenderer multiLineTableHeaderRenderer = new COINCOMOClefTableHeaderRenderer(3.5);
        COINCOMOClefTableCellRenderer colorfulTableCellRenderer = new COINCOMOClefTableCellRenderer(4);

        Enumeration<TableColumn> columns = clefTable.getColumnModel().getColumns();

        // Go Through All Columns ..
        while (columns.hasMoreElements()) {
            // Set Each with Our Table Header Renderer ...
            TableColumn column = (TableColumn) columns.nextElement();
            column.setHeaderRenderer(multiLineTableHeaderRenderer);
            column.setCellRenderer(colorfulTableCellRenderer);
        }

        // Setting the Width of Some Columns ...
        clefTable.getColumnModel().getColumn(0).setPreferredWidth(15);
        clefTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        clefTable.getColumnModel().getColumn(6).setPreferredWidth(50);
        clefTable.getColumnModel().getColumn(7).setPreferredWidth(50);
        clefTable.getColumnModel().getColumn(8).setPreferredWidth(50);
        clefTable.getColumnModel().getColumn(9).setPreferredWidth(50);
        clefTable.getColumnModel().getColumn(10).setPreferredWidth(50);
        clefTable.getColumnModel().getColumn(11).setPreferredWidth(50);
        clefTable.getColumnModel().getColumn(12).setPreferredWidth(50);

        clefTable.getColumnModel().getColumn(0).setCellRenderer(new COINCOMOCheckBoxTableCellRenderer());
        clefTable.getColumnModel().getColumn(0).setCellEditor(new COINCOMOCheckBoxCellEditor());

        // Design Gui of Layouts
        FlowLayout rightFlow = new FlowLayout();
        FlowLayout leftFlow = new FlowLayout();

        rightFlow.setAlignment(FlowLayout.RIGHT);
        leftFlow.setAlignment(FlowLayout.LEFT);

        // First of Two Halves of the North Panel
        JPanel upperNorthPanel = new JPanel(rightFlow);
        upperNorthPanel.add(scaleFactorsButton);
        upperNorthPanel.add(scheduleButton);

        // Second of two Halves of the North Panel
        JPanel lowerNorthPanel = new JPanel(leftFlow);
        lowerNorthPanel.add(new JLabel("  "));
        lowerNorthPanel.add(addSubComponentButton);
        lowerNorthPanel.add(deleteSubComponentButton);
        lowerNorthPanel.add(comboBox);

        JPanel northPanel = new JPanel(new GridLayout(2, 1));
        northPanel.add(upperNorthPanel);
        northPanel.add(lowerNorthPanel);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(estimationScroller);
        southPanel.add(new JLabel("    "), BorderLayout.NORTH);
        southPanel.add(new JLabel("    "), BorderLayout.SOUTH);
        southPanel.add(new JLabel("    "), BorderLayout.EAST);
        southPanel.add(new JLabel("    "), BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(clefTable));
        centerPanel.add(new JLabel("    "), BorderLayout.NORTH);
        centerPanel.add(new JLabel("    "), BorderLayout.SOUTH);
        centerPanel.add(new JLabel("    "), BorderLayout.EAST);
        centerPanel.add(new JLabel("    "), BorderLayout.WEST);

        // Load the Sub Components ...
        this.updateClefTable();

        splitPane.setTopComponent(centerPanel);
        splitPane.setBottomComponent(southPanel);

        this.setLayout(new BorderLayout());
        this.add(northPanel, BorderLayout.NORTH);
        this.add(splitPane);
    }

    public JButton getScaleFactorsButton() {
        return this.scaleFactorsButton;
    }

    public JTextPane getEstimationTextPane() {
        return this.estimationTextPane;
    }

    public COINCOMOComponent getCOINCOMOComponent() {
        return this.component;
    }

    public void updateClefTable() {
        ArrayList<COINCOMOUnit> orderedVector = component.getListOfSubUnits();

        this.clefTableModel.setRowCount(0);

        for (int i = 0; i < orderedVector.size(); i++) {
            COINCOMOSubComponent tempSubComponent = (COINCOMOSubComponent) orderedVector.get(i);

            // Create a Row ...
            COINCOMOVector<String> tableRowVector = new COINCOMOVector<String>();

            tableRowVector.add("false");
            tableRowVector.add(tempSubComponent.getName());
            tableRowVector.add(GlobalMethods.FormatLongWithComma(tempSubComponent.getSLOC()) + "");

            tableRowVector.add(format1DecimalWithComma.format(GlobalMethods.roundOff(tempSubComponent.getLaborRate(), 1)));

            tableRowVector.add(format2Decimals.format(GlobalMethods.roundOff(tempSubComponent.getEAF(), 2)));
            tableRowVector.add(tempSubComponent.getLanguage());
            tableRowVector.add(format2Decimals.format(GlobalMethods.roundOff(tempSubComponent.getNominalEffort(), 2)));
            tableRowVector.add(format2Decimals.format(GlobalMethods.roundOff(tempSubComponent.getEstimatedEffort(), 2)));

            tableRowVector.add(format2Decimals.format(GlobalMethods.roundOff(tempSubComponent.getProductivity(), 2)));
            tableRowVector.add(format2Decimals.format(GlobalMethods.roundOff(tempSubComponent.getCost(), 2)));
            tableRowVector.add(format2Decimals.format(GlobalMethods.roundOff(tempSubComponent.getInstructionCost(), 2)));
            tableRowVector.add(format1Decimal.format(GlobalMethods.roundOff(tempSubComponent.getStaff(), 1)));
            //Changed by Roopa Dharap----------------------
            tableRowVector.add(format1Decimal.format(GlobalMethods.roundOff(tempSubComponent.getRisk(), 1)));
            //End Change-----------------------------------

            // Set Same ID
            tableRowVector.setRowID(i);

            // Add a New Row To Table ...
            clefTableModel.addRow(tableRowVector);

        }

        GlobalMethods.updateStatusBar("Subcomponents have been loaded.", coincomo);
    }

    public void updateEstimationTextPane(final boolean needToUpdateComponent) {
        estimationTextPane.setText("Loading ...");

        if (!isLoading) {
            // If Already Exists ..
            /*if (refreshThread != null) {
             // Kill it 
             refreshThread.interrupt();
             }*/

            /*refreshThread = new RefreshSubComponents(this);
             refreshThread.start();
            
             // Already Exists One
             if (reportUpdater != null) {
             // Kill It
             reportUpdater.interrupt();
             }*/
            this.refreshSubComponents();

        }

        // Create a New One ...
        //reportUpdater = new UpdateEstimationReport(this, needToUpdateComponent);
        //reportUpdater.start();
        this.updateEstimationReport(/*needToUpdateComponent*/);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addSubComponentButton) {
            addSubComponentButton.setEnabled(false);
            COINCOMOSubComponent subComponent = COINCOMOSubComponentManager.insertSubComponent(component);
            this.updateClefTable();
            addSubComponentButton.setEnabled(true);

            // Update Report
            updateEstimationTextPane(true);

            GlobalMethods.updateStatusBar("One Subcomponent has been added.", coincomo);

            // Find out the name column pointer for the newly added subcomponent on the clefTable and fire an mouse click event.
            final int newRow = clefTable.getRowCount()-1;
            Rectangle subComponentNameRect = clefTable.getCellRect(newRow, 1, true);
            //System.out.println("subComponentNameRect: " + subComponentNameRect);

            final MouseEvent me = new MouseEvent(this, 0, System.currentTimeMillis(), 0, subComponentNameRect.x, subComponentNameRect.y, 1, false);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JViewport viewport = (JViewport) clefTable.getParent();
                    Point point = viewport.getViewPosition();
                    Rectangle rectangle = clefTable.getCellRect(newRow, 0, true);
                    rectangle.setLocation(rectangle.x - point.x, rectangle.y - point.y);
                    viewport.scrollRectToVisible(rectangle);
                    mouseClicked(me);
                }
            });
        } else if (e.getSource() == deleteSubComponentButton) {
            // Get Selected Row's Index
            final ArrayList<Integer> selectedRowsIndexes = new ArrayList<Integer>();

            for (int i = 0; i < clefTable.getRowCount(); i++) {
                // If Selected ...
                if (clefTable.getValueAt(i, 0).equals("true")) {
                    // Add to the list of rows
                    selectedRowsIndexes.add(i);
                }
            }


            // No Row Selected
            if (selectedRowsIndexes.isEmpty()) {
                //GlobalMethods.updateStatusBar("No Subcomponents were Selected.", Color.RED, coincomo);
                JOptionPane.showMessageDialog(coincomo, "No Subcomponent selected for delete.", "DELETE ERROR", 0);

                return;
            }
            coincomo.getCurrentSystem().setDirty();
            // Contains the Sub components deletion message ..
            JTextArea textArea = new JTextArea(8, 40);

            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setMargin(new Insets(10, 10, 10, 10));
            textArea.setFont(new Font("courier", 0, 12));

            textArea.append("Are you sure you would like to delete the following subcomponents ? \n\n");

            // Print Out Selected Sub components for deletion ..
            for (int i = 0; i < selectedRowsIndexes.size(); i++) {
                textArea.append("\t" + (i + 1) + ") " + clefTable.getValueAt(Integer.parseInt(selectedRowsIndexes.get(i) + ""), 1) + "\n");
            }

            // Put cursor at the beginning ...
            textArea.setCaretPosition(0);

            // Confirm Deletion ...
            int confirmationAnswer = JOptionPane.showConfirmDialog(coincomo, new JScrollPane(textArea), "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            // Delete Only if Yes is Chosen ...
            if (confirmationAnswer == JOptionPane.YES_OPTION) {
                deleteSubComponentButton.setEnabled(false);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ArrayList<COINCOMOSubComponent> subComponentsToBeDeleted = new ArrayList<COINCOMOSubComponent>();

                        // Delete Rows ...
                        for (int i = 0; i < selectedRowsIndexes.size(); i++) {
                            COINCOMOSubComponent deleteSubComponent;

                            // get the Vector of Vectors ..
                            Vector<COINCOMOVector> vectorOfRows = (Vector<COINCOMOVector>) clefTableModel.getDataVector();

                            // Get Row Vector
                            COINCOMOVector selectedRowVector = (COINCOMOVector) vectorOfRows.get(selectedRowsIndexes.get(i) - i);

                            // Remove Internally ..
                            deleteSubComponent = (COINCOMOSubComponent) component.getListOfSubUnits().get(selectedRowVector.getRowID());

                            // If the sub-component to be deleted is currently in the copy buffer, make sure to remove it as well.
                            if (coincomo.getSubComponentBuffer() == deleteSubComponent) {
                                coincomo.clearSubComponentBuffer();
                            }

                            subComponentsToBeDeleted.add(deleteSubComponent);

                            // Remove From Table (have to take already deleted rows into account)
                            clefTableModel.removeRow(selectedRowsIndexes.get(i) - i);
                        }
                        //setting flag as false will indicate that project has been changed
                        //  component.getParent().getParent().setChangeFlag(false);

                        // Remove From Database
                        if (subComponentsToBeDeleted != null) {
                            COINCOMOSubComponentManager.deleteSubComponents(subComponentsToBeDeleted);

                            //Update database
                            COINCOMOComponentManager.updateComponent(component, true);
                        }

                        deleteSubComponentButton.setEnabled(true);

                        // Update Report
                        updateEstimationTextPane(true);

                        //coincomo.getOverviewsAndGraphsPanel().getCOPSEMOPanel().updateCOMPSEMO();  
                        coincomo.refresh();

                        GlobalMethods.updateStatusBar(selectedRowsIndexes.size() + " Subcomponents were deleted.", ComponentOverviewPanel.this.coincomo);
                    }
                });
            }
        } else if (e.getSource() == scaleFactorsButton) {
            new ComponentScaleFactorsDialog(coincomo, this);
        } else {
            new ComponentScheduleDriverDialog(coincomo, this);
        }
    }

    public void itemStateChanged(ItemEvent e) {
        // Only Check Items When Selected ...
        if (e.getStateChange() == ItemEvent.SELECTED) {
            // Select All
            if (comboBox.getSelectedIndex() == 2) {
                for (int i = 0; i < clefTable.getRowCount(); i++) {
                    clefTable.setValueAt("true", i, 0);
                }
            } // Toggle
            else if (comboBox.getSelectedIndex() == 3) {
                for (int i = 0; i < clefTable.getRowCount(); i++) {
                    clefTable.setValueAt(!clefTable.getValueAt(i, 0).equals("true") + "", i, 0);
                }
            } // Unselect All
            else if (comboBox.getSelectedIndex() == 4) {
                for (int i = 0; i < clefTable.getRowCount(); i++) {
                    clefTable.setValueAt("false", i, 0);
                }
            } // Copy a selected subcomponent 
            else if (comboBox.getSelectedIndex() == 5) {
                // Get Selected Row's Index
                final ArrayList<Integer> selectedRowsIndexes = new ArrayList<Integer>();
                int rowNumber = 0;
                COINCOMOSubComponent subComponentToBeCopied = null;

                for (int i = 0; i < clefTable.getRowCount(); i++) {
                    // If Selected ...
                    if (clefTable.getValueAt(i, 0).equals("true")) {
                        // Add to the list of rows
                        selectedRowsIndexes.add(i);
                        rowNumber = i;
                    }
                }

                // No Row Selected or Multiple Rows Selected
                if (selectedRowsIndexes.isEmpty()) {
                    JOptionPane.showMessageDialog(coincomo, "No Subcomponent was Selected to copy.", "COPY ERROR", 0);
                    comboBox.setSelectedIndex(0);
                    return;
                }

                if (selectedRowsIndexes.size() > 1) {
                    JOptionPane.showMessageDialog(coincomo, "Multiple Subcomponents were selected to copy. Select only one.", "COPY ERROR", 0);
                    comboBox.setSelectedIndex(0);
                    return;
                }
                Vector<COINCOMOVector> vectors = clefTableModel.getDataVector();

                COINCOMOVector clickedRow = vectors.get(rowNumber);

                subComponentToBeCopied = (COINCOMOSubComponent) component.getListOfSubUnits().get(clickedRow.getRowID());
                //Copy the sub component to be copied into the global buffer.
                this.coincomo.setSubComponentBuffer(subComponentToBeCopied);
                GlobalMethods.updateStatusBar(" Copied " + subComponentToBeCopied.getName(), coincomo);
            } // Paste a selected subcomponent
            else if (comboBox.getSelectedIndex() == 6) {
                COINCOMOSubComponent subComponentToBeCopied = this.coincomo.getSubComponentBuffer();
                if (subComponentToBeCopied == null) {
                    JOptionPane.showMessageDialog(coincomo, "Nothing to paste. Copy a Subcomponent before performing paste.", "PASTE ERROR", 0);
                    comboBox.setSelectedIndex(0);
                    return;
                }
                addSubComponentButton.setEnabled(false);
                coincomo.getCurrentSystem().setDirty();

                // Copy the sub-component in the current component or to another component
                COINCOMOSubComponent subComponent = COINCOMOSubComponentManager.copySubComponent(subComponentToBeCopied, component);
                COINCOMOSubComponentManager.updateSubComponent(subComponent, true);

                this.updateClefTable();
                addSubComponentButton.setEnabled(true);
                subComponentToBeCopied = null;

                // Update Report
                updateEstimationTextPane(true);

                GlobalMethods.updateStatusBar(" Pasted a new Subcomponent ", coincomo);
            }

            // Go Back To Default ... "More Actions .."
            comboBox.setSelectedIndex(0);
        }
    }

    public void mouseClicked(MouseEvent e) {
        // translate point to row and column numbers
        final int rowNumber = clefTable.rowAtPoint(e.getPoint());
        final int columnNumber = clefTable.columnAtPoint(e.getPoint());

        Vector<COINCOMOVector> vectors = clefTableModel.getDataVector();

        COINCOMOVector clickedRow = vectors.get(rowNumber);

        final COINCOMOSubComponent subComponent = (COINCOMOSubComponent) component.getListOfSubUnits().get(rowNumber);

        if (columnNumber == 1) {
            String result;
            if (tempName == null) {
                result = JOptionPane.showInputDialog(coincomo, "Please enter the new name:", subComponent.getName());
            } else {
                result = JOptionPane.showInputDialog(coincomo, "Please enter the new name:", tempName);
            }

            //name shouldnt be null
            if (result == null) {
                tempName = null;
                return;
            }

            //name shouldn't contain <, >, /, \ , &
           /* if(result.contains("<") || result.contains(">") || result.contains("/") || result.contains("\\") || result.contains("&")){
             JOptionPane.showMessageDialog(coincomo, "Name shouldn't have <, >, /, \\ and &", "SPECIAL CHARACTERS IN NAME ERROR", 0);
             result = null;
             return;
             }*/
            if (result.contains("<") || result.contains(">") || result.contains("/") || result.contains("\\") || result.contains("&")
                    || result.contains("\n") || result.contains("\r") || result.contains("\t") || result.contains("\0") || result.contains("\f")
                    || result.contains("`") || result.contains("?") || result.contains("*") || result.contains("|") || result.contains("\"")
                    || result.contains(":")) {
                JOptionPane.showMessageDialog(this, "Name shouldn't have <, >, /, \\, &, /, newline, carriage return, tab, null, '\\f', `, ?, *, |, \" and :", "SPECIAL CHARACTERS IN NAME ERROR", 0);
                tempName = result.toString();
                result = null;
                mouseClicked(e);
                return;
            }
            if(result.length() > COINCOMOConstants.NAME_LENGTH) {
                 JOptionPane.showMessageDialog(this, "Name should no more than " + COINCOMOConstants.NAME_LENGTH + " charactors", "EXCEED NAME SIZE BOUNDARY ERROR", 0);
                tempName = result.toString();
                result = null;
                 mouseClicked(e);
                return;
            }
            
            //check if name is duplicated
            ArrayList<COINCOMOUnit> list = component.getListOfSubUnits();
            for(int i = 0;i<list.size();i++) {
                if(list.get(i) != (COINCOMOUnit) subComponent && list.get(i).getName().equals(result)) {
                            JOptionPane.showMessageDialog(this, "Duplicated name in the same level" , "DUPLICATED NAME ERROR", 0);
                            tempName = result.toString();
                            result = null;
                             mouseClicked(e);
                            return;
                 }
             }
            
            // If Not Empty
            if (result != null && !result.trim().equals("")) {
                // Update Name of the SubComponent
                subComponent.setName(result);
                tempName = null;

                // Update Name in the Table
                clefTable.setValueAt(subComponent.getName(), rowNumber, columnNumber);

                // Create a Thread
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            // Rest a Bit to allow GUI to render ...
                            Thread.sleep(100);

                            // Save in Database ...
                            COINCOMOSubComponentManager.updateSubComponentName(subComponent);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ComponentOverviewPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            } else {
                JOptionPane.showMessageDialog(coincomo, "Enter a valid string", "EMPTY STRING ERROR", 0);
                tempName = "";
                result = null;
                mouseClicked(e);
            }
        } else if (columnNumber == 2) {
            new SubComponentSizeDialog(coincomo, this, subComponent/*, rowNumber*/);
        } else if (columnNumber == 3) {
            String result;
            if (tempRate == null) {
                result = JOptionPane.showInputDialog(coincomo, "Please enter the new labor rate:", GlobalMethods.FormatDoubleWithComma(subComponent.getLaborRate()));
            } else {
                result = JOptionPane.showInputDialog(coincomo, "Please enter the new labor rate:", tempRate);
            }

            //rate shouldnt be null
            if (result == null) {
                tempRate = null;
                return;
            }

            // If Not Empty
            if (result != null && !result.trim().equals("")) {
                if (GlobalMethods.isNonNegativeDouble(result)) {
                    // Update Labor Rate of the SubComponent
                    subComponent.setLaborRate(GlobalMethods.ParseDoubleWithComma(result));
                    tempRate = null;

                    GlobalMethods.updateStatusBar("Saving New Labor Rate...", coincomo);

                    estimationTextPane.setText("Loading ...");

                    clefTable.setValueAt(format1DecimalWithComma.format(GlobalMethods.roundOff(subComponent.getLaborRate(), 1)), rowNumber, columnNumber);

                    // Create a Thread
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            try {
                                // Rest a Bit to allow GUI to render ...
                                Thread.sleep(100);

                                // Save in Database ...
                                COINCOMOSubComponentManager.updateSubComponent(subComponent, true);

                                // Update Table ...
                                clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(subComponent.getEAF(), 2)), rowNumber, 4);
                                clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(subComponent.getNominalEffort(), 2)), rowNumber, 6);
                                clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(subComponent.getEstimatedEffort(), 2)), rowNumber, 7);
                                clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(subComponent.getProductivity(), 2)), rowNumber, 8);
                                clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(subComponent.getCost(), 2)), rowNumber, 9);
                                clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(subComponent.getInstructionCost(), 2)), rowNumber, 10);
                                clefTable.setValueAt(format1Decimal.format(GlobalMethods.roundOff(subComponent.getStaff(), 1)), rowNumber, 11);

                                // Update ...
                                updateEstimationTextPane(false);

                                GlobalMethods.updateStatusBar("New Labor Rate has been saved.", coincomo);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(ComponentOverviewPanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                } else {
                    //GlobalMethods.updateStatusBar("Labor Rate Must Be a Non-Negative Value.", Color.RED, coincomo);
                    JOptionPane.showMessageDialog(coincomo, "Labor Rate Must Be a Non-Negative Value.", "LABOR RATE ERROR", 0);
                    tempRate = result.toString();
                    result = null;
                    mouseClicked(e);
                }
            } else {
                JOptionPane.showMessageDialog(coincomo, "Enter a non empty value", "EMPTY STRING ERROR", 0);
                tempRate = "";
                result = null;
                mouseClicked(e);
            }
        } else if (columnNumber == 4) {
            new SubComponentEAFDialog(coincomo, this, subComponent/*, rowNumber*/);
        } else if (columnNumber == 12) {
            //Changed by Roopa Dharap----------------------
            new RiskDialog(coincomo, subComponent);
            //End Change-----------------------------------
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    /*static class RefreshSubComponents extends Thread {
    
     private ComponentOverviewPanel componentOverviewPanel;
    
     public RefreshSubComponents(ComponentOverviewPanel cOPanel) {
     this.componentOverviewPanel = cOPanel;
     this.setPriority(3);
     }
    
     @Override
     public void run() {*/
    public void refreshSubComponents() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                GlobalMethods.updateStatusBar("Refreshing Subcomponents ...", /*componentOverviewPanel.*/ coincomo);

                try {
                    ArrayList<COINCOMOUnit> orderedVector = ComponentOverviewPanel.this.getCOINCOMOComponent().getListOfSubUnits();

                    //COINCOMOSubComponentManager.updateSubComponent( orderedVector );

                    for (int i = 0; i < orderedVector.size(); i++) {
                        COINCOMOSubComponent tempSubComponent = (COINCOMOSubComponent) orderedVector.get(i);

                        /*componentOverviewPanel.*/ clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(tempSubComponent.getEAF(), 2)), i, 4);
                        /*componentOverviewPanel.*/ clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(tempSubComponent.getNominalEffort(), 2)), i, 6);
                        /*componentOverviewPanel.*/ clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(tempSubComponent.getEstimatedEffort(), 2)), i, 7);
                        /*componentOverviewPanel.*/ clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(tempSubComponent.getProductivity(), 2)), i, 8);
                        /*componentOverviewPanel.*/ clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(tempSubComponent.getCost(), 2)), i, 9);
                        /*componentOverviewPanel.*/ clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(tempSubComponent.getInstructionCost(), 2)), i, 10);
                        /*componentOverviewPanel.*/ clefTable.setValueAt(format1Decimal.format(GlobalMethods.roundOff(tempSubComponent.getStaff(), 1)), i, 11);
                        //Changed by Roopa Dharap----------------------
                        /*componentOverviewPanel.*/ clefTable.setValueAt(format1Decimal.format(GlobalMethods.roundOff(tempSubComponent.getRisk(), 1)), i, 12);
                    }   //End Change-----------------------------------
                } catch (Exception e) {
                    e.printStackTrace();
                }
                GlobalMethods.updateStatusBar("Subcomponents have been refreshed.", /*componentOverviewPanel.*/ coincomo);

            }
        });
    }

    /*static class UpdateEstimationReport extends Thread {
    
     private boolean needToUpdateComponent;
     private ComponentOverviewPanel componentOverviewPanel;
     */
    public void updateEstimationReport() {
        final COINCOMOComponent component = this.component;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                /*            this.componentOverviewPanel = cOPanel;
                 this.needToUpdateComponent = needToUpdate;
                
                 // Lowest Priority ...
                 // So User can Play around with the System.
                 this.setPriority(1);
                 }
                
                 @Override
                 public void run() {*/


                GlobalMethods.updateStatusBar("Updating Report ...", /*componentOverviewPanel.*/ coincomo);


                //if (needToUpdate/*Component*/) {
                /*    // Update Database...
                 //COINCOMOComponentManager.updateComponent( /*componentOverviewPanel.*///getCOINCOMOComponent());
                //}
                final double totalEffort = COINCOMOComponentManager.calculateEffort(component);
                final double totalScheduleOptimistic = COINCOMOComponentManager.calculateSchedule(component, Scenario.Optimistic);
                final double totalSchedule = COINCOMOComponentManager.calculateSchedule(component, Scenario.MostLikely);
                final double totalSchedulePessimistic = COINCOMOComponentManager.calculateSchedule(component, Scenario.Pessimistic);
                final double totalProductivity = COINCOMOComponentManager.calculateProductivity(component);
                final double totalCost = COINCOMOComponentManager.calculateCost(component);
                final double totalInstructionCost = COINCOMOComponentManager.calculateInstructionCost(component);
                final double totalStaff = COINCOMOComponentManager.calculateStaff(component);
                final double totalRisk = COINCOMOComponentManager.calculateRisk(component);

                ComponentOverviewPanel.this.overviewsAndGraphsPanel.getCOPSEMOPanel().updateCOPSEMO(getCOINCOMOComponent());

                StringBuilder output = new StringBuilder();

                output.append("<table border = '0' cellpadding = '1'  cellspacing = '1'>");
                output.append("<tr>");
                output.append("<td> <b>Total Lines Of Code:</b> " + GlobalMethods.FormatLongWithComma(component.getSLOC()) + " </td>");
                output.append("<td> &nbsp;&nbsp;&nbsp;&nbsp; </td>");
                output.append("<td> <b>Hours/PM:</b> " + component.getParameters().getWorkHours() + " </td>");
                output.append("</tr>");
                output.append("</table>");

                String color = "DDDDDD";

                output.append("<table border = '1' cellpadding = '1'  cellspacing = '1' width = '100%' align = 'center'>");
                output.append("<tr>");
                output.append("<th bgcolor = " + color + "> Estimated </th>");
                output.append("<th bgcolor = " + color + "> Effort </th>");
                output.append("<th bgcolor = " + color + "> Schedule </th>");
                output.append("<th bgcolor = " + color + "> PROD </th>");
                output.append("<th bgcolor = " + color + "> COST </th>");
                output.append("<th bgcolor = " + color + "> INST </th>");
                output.append("<th bgcolor = " + color + "> Staff </th>");
                output.append("<th bgcolor = " + color + "> Risk </th>");
                output.append("</tr>");
                output.append("<tr>");
                output.append("<th bgcolor = " + color + "> Optimistic </th>");
                output.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(totalEffort / 1.25, 2)) + " </th>");
                output.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(totalScheduleOptimistic, 2)) + " </th>");
                output.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(totalProductivity * 1.25, 2)) + " </th>");
                output.append("<th> " + format2DecimalWithComma.format(GlobalMethods.roundOff(totalCost / 1.25, 2)) + " </th>");
                output.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(totalInstructionCost / 1.25, 2)) + " </th>");
                output.append("<th> " + format1Decimal.format(GlobalMethods.roundOff((totalEffort / 1.25) / totalScheduleOptimistic, 1)) + " </th>");
                output.append("</tr>");
                output.append("<tr>");
                output.append("<th bgcolor = " + color + "> Most Likely </th>");
                output.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(totalEffort, 2)) + " </th>");
                output.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(totalSchedule, 2)) + " </th>");
                output.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(totalProductivity, 2)) + " </th>");
                output.append("<th> " + format2DecimalWithComma.format(GlobalMethods.roundOff(totalCost, 2)) + " </th>");
                output.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(totalInstructionCost, 2)) + " </th>");
                output.append("<th> " + format1Decimal.format(GlobalMethods.roundOff(totalStaff, 1)) + " </th>");
                output.append("<th> " + format1Decimal.format(GlobalMethods.roundOff(totalRisk, 1)) + " </th>"); //Changed by Roopa Dharap
                output.append("</tr>");
                output.append("<tr>");
                output.append("<th bgcolor = " + color + "> Pessimistic </th>");
                output.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(totalEffort * 1.25, 2)) + " </th>");
                output.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(totalSchedulePessimistic, 2)) + " </th>");
                output.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(totalProductivity / 1.25, 2)) + " </th>");
                output.append("<th> " + format2DecimalWithComma.format(GlobalMethods.roundOff(totalCost * 1.25, 2)) + " </th>");
                output.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(totalInstructionCost * 1.25, 2)) + " </th>");
                output.append("<th> " + format1Decimal.format(GlobalMethods.roundOff((totalEffort * 1.25) / totalSchedulePessimistic, 1)) + " </th>");
                output.append("</tr>");
                output.append("</table>");

                /*componentOverviewPanel.*/ getEstimationTextPane().setText(output.toString());

                GlobalMethods.updateStatusBar("Report Updated.", /*componentOverviewPanel.*/ coincomo);
            }
        });
    }
}