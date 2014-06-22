/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package dialogs;

import core.COINCOMOComponent;
import core.COINCOMOLocalCalibration;
import core.COINCOMOLocalCalibrationProject;
import core.COINCOMOSubSystem;
import core.COINCOMOSystem;
import core.COINCOMOUnit;
import extensions.COINCOMOCheckBoxCellEditor;
import extensions.COINCOMOCheckBoxTableCellRenderer;
import extensions.COINCOMOClefTableCellRenderer;
import extensions.COINCOMOClefTableHeaderRenderer;
import extensions.COINCOMOComboBoxCellEditor;
import extensions.COINCOMOFixedTable;
import extensions.COINCOMOVector;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import main.COINCOMO;
import main.COINCOMOXML;
import main.GlobalMethods;

/**
 *
 * @author Abhishek
 */
public class CalibratedProjectDialog extends JDialog implements ActionListener, MouseListener {

    /*
     * The string array "insertOptions" used for the drop down menu are also used in the if-else statements somewhere in the class.
     * IF you change the wordings, please note that there are other places you need to do the same changes as well.
     */
    private String[] insertOptions = {"Insert From ...", "SEPARATOR", "Current System", "Project File"};
    private JComboBox insertComboBox = new JComboBox(insertOptions);
    private JButton insertButton = new JButton("Insert");
    private JButton deleteButton = new JButton("Delete");
    private JButton computeButton = new JButton("Compute");
    private JButton closeButton = new JButton("Close");
    private JButton helpButton = new JButton("Help");
    private DefaultTableModel clefTableModel = new DefaultTableModel();
    private COINCOMOFixedTable clefTable;
    private DecimalFormat format2Decimals = new DecimalFormat("0.00");
    COINCOMO coincomo = null;
    COINCOMOLocalCalibration localCalibration = null;
    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    public CalibratedProjectDialog(COINCOMO parent) {
        super();
        coincomo = parent;
        localCalibration = COINCOMO.localCalibration;
        this.setTitle("Projects");
        this.setModal(true);
        this.setDefaultCloseOperation(CalibratedProjectDialog.DISPOSE_ON_CLOSE);

        insertComboBox.setRenderer(new COINCOMOComboBoxCellEditor());

        this.setLayout(new FlowLayout());
        JPanel panel = new JPanel(new FlowLayout());
        insertComboBox.addActionListener(this);
//        insertButton.addActionListener(this);
        deleteButton.addActionListener(this);
        computeButton.addActionListener(this);
        closeButton.addActionListener(this);
        helpButton.addActionListener(this);

//        insertButton.setFocusable(false);
        deleteButton.setFocusable(false);
        computeButton.setFocusable(false);
        closeButton.setFocusable(false);
        helpButton.setFocusable(false);

        insertComboBox.setSize(140, 20);
//        insertButton.setBounds(20, 400, 70, 20);
        deleteButton.setSize(140, 20);
//        deleteButton.setBounds(100, 400, 70, 20);
        computeButton.setSize(140, 20);
//        computeButton.setBounds(180, 400, 70, 20);
        closeButton.setSize(140, 20);
//        closeButton.setBounds(260, 400, 70, 20);
        helpButton.setSize(140, 20);
//        helpButton.setBounds(340, 400, 70, 20);

        panel.add(insertComboBox);
        panel.add(deleteButton);
        panel.add(computeButton);
        panel.add(closeButton);
        panel.add(helpButton);

        //  panel.setSize(440, 440);
        //textArea = new JTextArea(100,100);
        // JScrollPane functionScroller = new JScrollPane(textArea);
        // functionScroller.setPreferredSize(new Dimension(440, 330));

        clefTableModel = new DefaultTableModel();
        clefTable = new COINCOMOFixedTable(clefTableModel);

        // Table
        clefTable.setRowSelectionAllowed(false);
        clefTable.addMouseListener(this);

        // Html Was added to be able to make it Multiline ..
        clefTableModel.addColumn("X");
        clefTableModel.addColumn("System");
        clefTableModel.addColumn("Sub System");
        clefTableModel.addColumn("Component");
        clefTableModel.addColumn("EAF");
        clefTableModel.addColumn("Date");
        clefTableModel.addColumn("Effort");
        clefTableModel.addColumn("Schedule");

        COINCOMOClefTableHeaderRenderer multiLineTableHeaderRenderer = new COINCOMOClefTableHeaderRenderer(1.5);
        COINCOMOClefTableCellRenderer colorfulTableCellRenderer = new COINCOMOClefTableCellRenderer(clefTableModel.getColumnCount()-2, clefTableModel.getColumnCount()-1);

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
        clefTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        clefTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        clefTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        clefTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        clefTable.getColumnModel().getColumn(5).setPreferredWidth(65);
        clefTable.getColumnModel().getColumn(6).setPreferredWidth(70);
        clefTable.getColumnModel().getColumn(7).setPreferredWidth(70);

        clefTable.getColumnModel().getColumn(0).setCellRenderer(new COINCOMOCheckBoxTableCellRenderer());
        clefTable.getColumnModel().getColumn(0).setCellEditor(new COINCOMOCheckBoxCellEditor());
        JScrollPane scroll = new JScrollPane(clefTable);
        //scroll.setBounds(10, 50, 350, 170);
        scroll.setSize(510, 350);

        updateClefTable();

        this.setLocation(this.getOwner().getX() + 150, this.getOwner().getY() + 150);
        this.setSize(520, 550);
        this.add(scroll);
        this.add(panel);
        this.setVisible(true);
        this.setResizable(false);
    }

    public void updateClefTable() {
        ArrayList<COINCOMOUnit> orderedVector = localCalibration.getListOfSubUnits();
        
        this.clefTableModel.setRowCount(0);

        for (int i = 0; i < orderedVector.size(); i++) {
            COINCOMOLocalCalibrationProject project = (COINCOMOLocalCalibrationProject) orderedVector.get(i);
            
            // Create a Row ...
            COINCOMOVector<String> tableRowVector = new COINCOMOVector<String>();

            COINCOMOComponent component = project.getComponent();
            COINCOMOSubSystem subSystem = (COINCOMOSubSystem) component.getParent();
            COINCOMOSystem system = (COINCOMOSystem) subSystem.getParent();

            tableRowVector.add("" + project.isSelected());
            tableRowVector.add(system.getName());
            tableRowVector.add(subSystem.getName());
            tableRowVector.add(component.getName());
            tableRowVector.add(format2Decimals.format(project.getEAF()));
            tableRowVector.add(dateFormat.format(project.getDate()));
            tableRowVector.add(format2Decimals.format(project.getEffort()));
            tableRowVector.add(format2Decimals.format(project.getSchedule()));

            tableRowVector.setRowID(i);
            // Add a New Row To Table ...
            clefTableModel.addRow(tableRowVector);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == insertComboBox) {
            String insertOption = insertComboBox.getSelectedItem().toString();

            if ("Current System".equals(insertOption)) {
                insertCurrentSystem();
            } else if ("Project File".equals(insertOption)) {
                insertProjectFile();
            }

            insertComboBox.setSelectedIndex(0);
        } else if (e.getSource() == insertButton) {
            if (coincomo != null) {
                COINCOMOSystem system = coincomo.getCurrentSystem();
                if (system != null) {
                    COINCOMOSystem systemCopy = new COINCOMOSystem();
                    systemCopy.copyUnit(system, false, true);
                    
                    Iterator iter = systemCopy.getListOfSubUnits().iterator();
                    
                    while (iter.hasNext()) {
                        COINCOMOSubSystem subSystemCopy = (COINCOMOSubSystem) iter.next();
                        
                        Iterator iter2 = subSystemCopy.getListOfSubUnits().iterator();
                        
                        while (iter2.hasNext()) {
                            COINCOMOComponent componentCopy = (COINCOMOComponent) iter2.next();

                            COINCOMOLocalCalibrationProject project = new COINCOMOLocalCalibrationProject(componentCopy);
                            localCalibration.addSubUnit(project);
                            project.setSelected(true);
                            project.setDate();
                        }
                    }
                    
                    updateClefTable();
                }
            }

            //throw new UnsupportedOperationException("not done yet.");
            /*
            COINCOMOLocalCalibrationProject project = new COINCOMOLocalCalibrationProject();
            project.setName("name");
            project.setDate();       //to setdate
            COINCOMOVector<String> tableRowVector = new COINCOMOVector<String>();

            tableRowVector.add("false");
            tableRowVector.add(project.getName() + "");
            tableRowVector.add(project.getDate() + "");
            tableRowVector.add(format2Decimals.format(0));
            tableRowVector.add(format2Decimals.format(0));

            tableRowVector.setRowID(rowCounter);
            // Add a New Row To Table ...
            clefTableModel.addRow(tableRowVector);
            rowCounter++;

            component.getParameters().addSubUnit(project);
            */
        } else if (e.getSource() == deleteButton) {
            // Get Selected Row's Index
            final ArrayList<Integer> selectedRowsIndexes = new ArrayList<Integer>();

            for (int i = 0; i < clefTable.getRowCount(); i++) {
                // If Selected ...
                if (clefTable.getValueAt(i, 0).equals("true")) {
                    // Add to the list of rows
                    selectedRowsIndexes.add(i);
                }
            }

            // COINCOMO coincomo = (COINCOMO) this.getParent();
            // No Row Selected
            if (selectedRowsIndexes.isEmpty()) {
                //GlobalMethods.updateStatusBar("No Subcomponents were Selected.", Color.RED,null);
                JOptionPane.showMessageDialog(null, "No component was selected");
                return;
            }
            // Contains the Sub components deletion message ..
            JTextArea textArea = new JTextArea(8, 40);

            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setMargin(new Insets(10, 10, 10, 10));
            textArea.setFont(new Font("courier", 0, 12));

            textArea.append("Are you sure you would like to delete the following component(s) ? \n\n");

            // Print Out Selected components for deletion ..
            for (int i = 0; i < selectedRowsIndexes.size(); i++) {
                textArea.append("\t" + (i + 1) + ") " + clefTable.getValueAt(Integer.parseInt(selectedRowsIndexes.get(i) + ""), 3) + "\n");
            }

            // Put cursor at the beginning ...
            textArea.setCaretPosition(0);

            // Confirm Deletion ...
            int confirmationAnswer = JOptionPane.showConfirmDialog(null, new JScrollPane(textArea), "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            // Delete Only if Yes is Chosen ...
            if (confirmationAnswer == JOptionPane.YES_OPTION) {
                deleteButton.setEnabled(false);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ArrayList<COINCOMOLocalCalibrationProject> projectsToBeDeleted = new ArrayList<COINCOMOLocalCalibrationProject>();

                        // Delete Rows ...                           
                        for (int i = 0; i < selectedRowsIndexes.size(); i++) {
                            COINCOMOLocalCalibrationProject deleteProject;

                            // get the Vector of Vectors ..
                            Vector<COINCOMOVector> vectorOfRows = (Vector<COINCOMOVector>) clefTableModel.getDataVector();

                            // Get Row Vector
                            COINCOMOVector selectedRowVector = (COINCOMOVector) vectorOfRows.get(selectedRowsIndexes.get(i) - i);
                            System.out.println("SelectedRow " + selectedRowVector.getRowID());

                            // Remove Internally ..
                            deleteProject = (COINCOMOLocalCalibrationProject) localCalibration.getListOfSubUnits().get(selectedRowVector.getRowID());
                            System.out.println("Project " + deleteProject.getName() + " is to be deleted.");

                            projectsToBeDeleted.add(deleteProject);

                            // Remove From Table
                            clefTableModel.removeRow(selectedRowsIndexes.get(i) - i);
                        }

                        for (int i = 0; i < projectsToBeDeleted.size(); i++) {
                            localCalibration.removeSubUnit(projectsToBeDeleted.get(i));
                        }

                        //TODO (Larry) Why do we have to update the ClefTable when it should be working as expected. Did the VectorRow.getRowID get changed?
                        updateClefTable();

                        deleteButton.setEnabled(true);
                    }
                });
            }
        } else if (e.getSource() == computeButton) {
            Iterator i = localCalibration.getListOfSubUnits().iterator();

            int counter = 0;
            boolean somethingSelected = false;
            while (i.hasNext()) {
                COINCOMOLocalCalibrationProject p = (COINCOMOLocalCalibrationProject) i.next();
                p.setSelected(Boolean.parseBoolean(clefTableModel.getValueAt(counter++, 0).toString()));
                if (p.isSelected()) {
                    somethingSelected = true;
                }
            }
            if (!somethingSelected) {
                JOptionPane.showMessageDialog(null, "No Project Selected");
                return;
            }

            /*
            double oldA, oldB, oldC, oldD;
            oldA = component.getParameters().getA();
            oldB = component.getParameters().getB();
            oldC = component.getParameters().getC();
            oldD = component.getParameters().getD();

            //Calculate coefficients and exponents
            COINCOMOManager.get_Local_Calibration(component);

            new CalibratedParametersDialog(this, component,
                    component.getParameters().getA(), component.getParameters().getB(),
                    component.getParameters().getC(), component.getParameters().getD());
            */
            new CalibratedParametersDialog(coincomo);

        } else if (e.getSource() == closeButton) {
            this.dispose();
        } else if (e.getSource() == helpButton) {
            JOptionPane.showMessageDialog(this, "Insert a project first and then click display");
        }
    }

    public void mouseClicked(MouseEvent e) {
        // throw new UnsupportedOperationException("Not supported yet.");
        final int rowNumber = clefTable.rowAtPoint(e.getPoint());
        final int columnNumber = clefTable.columnAtPoint(e.getPoint());

        Vector<COINCOMOVector> vectors = clefTableModel.getDataVector();

        COINCOMOVector clickedRow = vectors.get(rowNumber);

        final COINCOMOLocalCalibrationProject project = (COINCOMOLocalCalibrationProject) localCalibration.getListOfSubUnits().get(rowNumber);

        if (columnNumber == 0) {
            System.out.println("Row " + rowNumber + " clicked.");
            System.out.println("Row " + rowNumber + " is " + project.getName());
            project.setSelected(!project.isSelected());
        } else if (columnNumber == 1) {
            return;
        } else if (columnNumber == 2) {
            return;
        } else if (columnNumber == 3) {
            /*
            String result = JOptionPane.showInputDialog(this, "Please enter a name:");
            project.setName(result);
            clefTable.setValueAt(result, rowNumber, columnNumber);
            */
            return;
        } else if (columnNumber == 4) {
            return;
        } else if (columnNumber == 5) {
            return;
        } else if (columnNumber == 6) {
            String result = JOptionPane.showInputDialog(this, "Please enter actual effort:", project.getName(), JOptionPane.INFORMATION_MESSAGE);
            try {
                if (result != null) {
                    double effort = Double.parseDouble(result);
                    project.setEffort(effort);
                    clefTable.setValueAt(project.getEffort(), rowNumber, columnNumber);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Enter Numeric Value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if (columnNumber == 7) {
            String result = JOptionPane.showInputDialog(this, "Please enter actual schedule:", project.getName(), JOptionPane.INFORMATION_MESSAGE);
            try {
                if (result != null) {
                    double schedule = Double.parseDouble(result);
                    project.setSchedule(schedule);
                    clefTable.setValueAt(project.getSchedule(), rowNumber, columnNumber);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Enter Numeric Value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        //  throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseReleased(MouseEvent e) {
        //  throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseEntered(MouseEvent e) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseExited(MouseEvent e) {
        //   throw new UnsupportedOperationException("Not supported yet.");
    }

    //TODO (Larry) Need to re-factor insert(COINCOMOSsytem) method to COINCOMOLocalCalibrationManager class
    private void insert(COINCOMOSystem system) {
        if (system != null) {
            COINCOMOSystem systemCopy = new COINCOMOSystem();
            systemCopy.copyUnit(system, false, true);

            Iterator iter = systemCopy.getListOfSubUnits().iterator();

            while (iter.hasNext()) {
                COINCOMOSubSystem subSystemCopy = (COINCOMOSubSystem) iter.next();

                Iterator iter2 = subSystemCopy.getListOfSubUnits().iterator();

                while (iter2.hasNext()) {
                    COINCOMOComponent componentCopy = (COINCOMOComponent) iter2.next();

                    COINCOMOLocalCalibrationProject project = new COINCOMOLocalCalibrationProject(componentCopy);
                    System.out.println("Project " + project.getName() + " is added.");
                    localCalibration.addSubUnit(project);
                    project.setSelected(true);
                    project.setDate();
                }
            }
            
            updateClefTable();
        }
    }

    //TODO (Larry) Need to re-factor insertCUrrentSystem) method to COINCOMOLocalCalibrationManager class
    private void insertCurrentSystem() {
        if (coincomo != null) {
            COINCOMOSystem system = coincomo.getCurrentSystem();
            if (system != null) {
                insert(system);
            }
        }
    }

    //TODO (Larry) Need to re-factor insertProjectFile method to COINCOMOLocalCalibrationManager class
    private void insertProjectFile() {
        File file = null;
        String validExt[] = {COINCOMO.EXTENSION};
        COINCOMOSystem system = null;

        while (true) {
            file = GlobalMethods.getFile(coincomo, null, validExt, "Insert", false);
            if (file == null) {
                // No file selected, exit
                break;
            } else {
                // A file selected, continue
                if (COINCOMOXML.validateXML(file)) {
                    boolean currentIgnoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
                    coincomo.setIgnoreDatabasMode(true);
                    system = COINCOMOXML.importXML(file);
                    coincomo.setIgnoreDatabasMode(currentIgnoreDatabaseMode);

                    break;
                } else {
                    int choice = JOptionPane.showConfirmDialog(this.getParent(), "The selected COINCOMO file is invalid. Try again?", "Invalid COINCOMO File", JOptionPane.YES_NO_OPTION);

                    if (choice != JOptionPane.YES_OPTION) {
                        break;
                    }
                }
            }
        }

        if (system != null) {
            insert(system);
        }
    }
}
