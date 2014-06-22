/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package dialogs;

import core.COINCOMOComponent;
import core.COINCOMOComponentParameters;
import core.COINCOMOConstants.LocalCalibrationMode;
import core.COINCOMOLocalCalibration;
import core.COINCOMOLocalCalibrationProject;
import core.COINCOMOSubComponent;
import core.COINCOMOSubSystem;
import core.COINCOMOSystem;
import core.COINCOMOUnit;
import database.COINCOMOComponentManager;
import database.COINCOMOLocalCalibrationManager;
import database.COINCOMOSubComponentManager;
import extensions.COINCOMOCheckBoxCellEditor;
import extensions.COINCOMOCheckBoxTableCellRenderer;
import extensions.COINCOMOClefTableCellRenderer;
import extensions.COINCOMOClefTableHeaderRenderer;
import extensions.COINCOMOFixedTable;
import extensions.COINCOMOVector;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import main.COINCOMO;
import main.Icons;
import dialogs.CalibratedProjectDialog;
import extensions.COINCOMOComboBoxCellEditor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import main.GlobalMethods;

/**
 *
 * @author Justin
 */
public class CalibratedParametersDialog extends JDialog implements ActionListener, MouseListener {

    private JLabel calibrationMethodLabel = new JLabel();
    private JLabel calInstructLabel = new JLabel("<html><body>For less than 10 projects, select<br/>the coefficients only calibration<br/>method.<body></html>");
    private ButtonGroup calibrationMethodBtnGrp = new ButtonGroup();
    private JRadioButton coefficientsOnlyBtn = new JRadioButton("Cofficients ");
    private JRadioButton coefficientsAndExponentsBtn = new JRadioButton("Cofficients and Exponents");
    private JLabel currentLabel = new JLabel();
    private JLabel effortCoefficientLabel = new JLabel("Effort Coefficient");
    private JLabel effortExponentLabel = new JLabel("Effort Exponent");
    private JLabel scheduleCoefficientLabel = new JLabel("Schedule Coefficient");
    private JLabel scheduleExponentLabel = new JLabel("Schedule Exponent");
    private JLabel effortCoefficientValueLabel;
    private JLabel effortExponentValueLabel;
    private JLabel scheduleCoefficientValueLabel;
    private JLabel scheduleExponentValueLabel;
    private JLabel newLabel = new JLabel();
    private JLabel newEffortCoefficientLabel = new JLabel("Effort Coefficient");
    private JLabel newEffortExponentLabel = new JLabel("Effort Exponent");
    private JLabel newScheduleCoefficientLabel = new JLabel("Schedule Coefficient");
    private JLabel newScheduleExponentLabel = new JLabel("Schedule Exponent");
    private JLabel newEffortCoefficientValueLabel;
    private JLabel newEffortExponentValueLabel;
    private JLabel newScheduleCoefficientValueLabel;
    private JLabel newScheduleExponentValueLabel;
    private JButton acceptBtn = new JButton("Apply");
    private JButton cancelBtn = new JButton("Cancel");
    private DefaultTableModel showTableModel = new DefaultTableModel();
    private DefaultTableModel clefTableModel = new DefaultTableModel();
    private COINCOMOFixedTable clefTable;
    private COINCOMOFixedTable showTable;
    private COINCOMOComponent component;
    private COINCOMO currentcoincomo = null;
    private ArrayList<COINCOMOComponent> componentList = new ArrayList<COINCOMOComponent>();
    //private COINCOMOComponent currentunit=null;
    // effort multipler = A, exponent = B
    // schedule multiplier = C, exponent = D
    private COINCOMOLocalCalibration localCalibration = null;
    private double effortCoefficient = 0.0d;
    private double effortExponent = 0.0d;
    private double scheduleCoefficient = 0.0d;
    private double scheduleExponent = 0.0d;
    private String[] selectOptions = {"Quick Select ...", "Select All", "Unselect All"};
    private JComboBox comboBox = new JComboBox(selectOptions);
  

    public CalibratedParametersDialog(COINCOMO coincomo) {
        super();
        currentcoincomo = coincomo;
        LocalCalibrationMode localCalibrationMode = COINCOMOLocalCalibration.getCalibrationMode();

        this.localCalibration = COINCOMO.localCalibration;
        if (coincomo.getCurrentUnit() instanceof COINCOMOComponent) {
            this.component = (COINCOMOComponent) coincomo.getCurrentUnit();
        }

        this.setTitle("Calibrated Parameters");
        this.setModal(true);
        this.setDefaultCloseOperation(CalibratedParametersDialog.DISPOSE_ON_CLOSE);
        
        comboBox.setRenderer(new COINCOMOComboBoxCellEditor());
        comboBox.addActionListener(this);
        //comboBox.addItemListener(this);
        //comboBox.addItemListener(new ItemListener()); 

        //comboBox.setPreferredSize(new Dimension(125, 25));

        comboBox.setBackground(Color.WHITE);
        comboBox.setRenderer(new COINCOMOComboBoxCellEditor());

//        comboBox.addItem(" Quick Select ...");
//        comboBox.addItem(" Select All");
//        comboBox.addItem(" Unselect All");

        calibrationMethodBtnGrp.add(coefficientsOnlyBtn);
        calibrationMethodBtnGrp.add(coefficientsAndExponentsBtn);
        coefficientsAndExponentsBtn.setEnabled(true);
        coefficientsAndExponentsBtn.setEnabled(true);
        coefficientsOnlyBtn.addActionListener(this);
        coefficientsAndExponentsBtn.addActionListener(this);

        this.setLayout(new FlowLayout());
        JPanel panel = new JPanel(null);

        acceptBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        acceptBtn.setFocusable(false);
        cancelBtn.setFocusable(false);

        acceptBtn.setIcon(Icons.SAVE_ICON);
        cancelBtn.setIcon(Icons.CLOSE_ICON);

        if (component != null) {
            final COINCOMOComponentParameters parameters = component.getParameters();
            effortCoefficientValueLabel = new JLabel("" + parameters.getA());
            effortExponentValueLabel = new JLabel("" + parameters.getB());
            scheduleCoefficientValueLabel = new JLabel("" + parameters.getC());
            scheduleExponentValueLabel = new JLabel("" + parameters.getD());
            newEffortCoefficientValueLabel = new JLabel("--");
            newEffortExponentValueLabel = new JLabel("--");
            newScheduleCoefficientValueLabel = new JLabel("--");
            newScheduleExponentValueLabel = new JLabel("--");
        } else {
            effortCoefficientValueLabel = new JLabel("--");
            effortExponentValueLabel = new JLabel("--");
            scheduleCoefficientValueLabel = new JLabel("--");
            scheduleExponentValueLabel = new JLabel("--");
            newEffortCoefficientValueLabel = new JLabel("--");
            newEffortExponentValueLabel = new JLabel("--");
            newScheduleCoefficientValueLabel = new JLabel("--");
            newScheduleExponentValueLabel = new JLabel("--");
        }

        //Top section
        calibrationMethodLabel.setBounds(20, 20, 190, 70);
        calInstructLabel.setBounds(250, 10, 180, 80);
        calibrationMethodLabel.setBorder(BorderFactory.createTitledBorder("Calibration Method"));

        coefficientsOnlyBtn.setBounds(30, 40, 150, 20);
        coefficientsAndExponentsBtn.setBounds(30, 60, 175, 20);
        if (localCalibrationMode == LocalCalibrationMode.COEFFICIENTS_ONLY) {
            coefficientsOnlyBtn.setSelected(true);
        } else if (localCalibrationMode == LocalCalibrationMode.COEFFICIENTS_AND_EXPONENTS) {
            coefficientsAndExponentsBtn.setSelected(true);
        }

        //Current section
        currentLabel.setBounds(20, 100, 400, 70);
        currentLabel.setBorder(BorderFactory.createTitledBorder("Current"));

        effortCoefficientLabel.setBounds(30, 120, 100, 20);
        effortCoefficientValueLabel.setBounds(160, 120, 50, 20);

        effortExponentLabel.setBounds(250, 120, 100, 20);
        effortExponentValueLabel.setBounds(370, 120, 50, 20);

        scheduleCoefficientLabel.setBounds(30, 140, 150, 20);
        scheduleCoefficientValueLabel.setBounds(160, 140, 50, 20);

        scheduleExponentLabel.setBounds(250, 140, 150, 20);
        scheduleExponentValueLabel.setBounds(370, 140, 50, 20);

        //New section
        newLabel.setBounds(20, 180, 400, 70);
        newLabel.setBorder(BorderFactory.createTitledBorder("New"));

        newEffortCoefficientLabel.setBounds(30, 200, 100, 20);
        newEffortCoefficientValueLabel.setBounds(160, 200, 50, 20);

        newEffortExponentLabel.setBounds(250, 200, 150, 20);
        newEffortExponentValueLabel.setBounds(370, 200, 50, 20);

        newScheduleCoefficientLabel.setBounds(30, 220, 150, 20);
        newScheduleCoefficientValueLabel.setBounds(160, 220, 50, 20);

        newScheduleExponentLabel.setBounds(250, 220, 250, 20);
        newScheduleExponentValueLabel.setBounds(370, 220, 50, 20);
        
        comboBox.setBounds(70, 450, 100, 30);
        acceptBtn.setBounds(175, 450, 100, 30);
        cancelBtn.setBounds(280, 450, 100, 30);

        panel.add(calibrationMethodLabel);
        panel.add(calInstructLabel);
        panel.add(coefficientsOnlyBtn);
        panel.add(coefficientsAndExponentsBtn);
        panel.add(currentLabel);
        panel.add(effortCoefficientLabel);
        panel.add(effortExponentLabel);
        panel.add(scheduleCoefficientLabel);
        panel.add(scheduleExponentLabel);
        panel.add(effortCoefficientValueLabel);
        panel.add(effortExponentValueLabel);
        panel.add(scheduleCoefficientValueLabel);
        panel.add(scheduleExponentValueLabel);
        panel.add(newLabel);
        panel.add(newEffortCoefficientLabel);
        panel.add(newEffortExponentLabel);
        panel.add(newScheduleCoefficientLabel);
        panel.add(newScheduleExponentLabel);
        panel.add(newEffortCoefficientValueLabel);
        panel.add(newEffortExponentValueLabel);
        panel.add(newScheduleCoefficientValueLabel);
        panel.add(newScheduleExponentValueLabel);

        //panel.add(acceptBtn);
        //panel.add(cancelBtn);



        clefTableModel = new DefaultTableModel();
        clefTable = new COINCOMOFixedTable(clefTableModel);

        // Table
        clefTable.setRowSelectionAllowed(false);
        clefTable.addMouseListener(this);

        // Html Was added to be able to make it Multiline ..
        //checkBox.setSelected(true);
        clefTableModel.addColumn("X");
        clefTableModel.addColumn("Sub System");
        clefTableModel.addColumn("Component");
        
//        selectBox = new JCheckBox(clefTableModel.getColumnName(0));
//        selectBox.setSelected(true);


        COINCOMOClefTableHeaderRenderer multiLineTableHeaderRenderer = new COINCOMOClefTableHeaderRenderer(1.5);
        COINCOMOClefTableCellRenderer colorfulTableCellRenderer = new COINCOMOClefTableCellRenderer(clefTableModel.getColumnCount() - 2, clefTableModel.getColumnCount() - 1);

        Enumeration<TableColumn> columns = clefTable.getColumnModel().getColumns();

        // Go Through All Columns ..
        while (columns.hasMoreElements()) {
            // Set Each with Our Table Header Renderer ...
            TableColumn column = (TableColumn) columns.nextElement();
            column.setHeaderRenderer(multiLineTableHeaderRenderer);
            column.setCellRenderer(colorfulTableCellRenderer);
        }
 
        // Setting the Width of Some Columns ...
        clefTable.getColumnModel().getColumn(0).setPreferredWidth(20);
        
        clefTable.getColumnModel().getColumn(1).setPreferredWidth(190);
        clefTable.getColumnModel().getColumn(2).setPreferredWidth(190);


        clefTable.getColumnModel().getColumn(0).setCellRenderer(new COINCOMOCheckBoxTableCellRenderer());
        clefTable.getColumnModel().getColumn(0).setCellEditor(new COINCOMOCheckBoxCellEditor());
        JScrollPane scrollbar = new JScrollPane(clefTable);
        scrollbar.setBounds(20, 260, 400, 130);
        //scrollbar.setSize(300, 40);

        panel.add(scrollbar);
        panel.setSize(460, 400);
        panel.setPreferredSize(panel.getSize());

        updateChooseTable();

        this.setLocation(this.getOwner().getX() + 150, this.getOwner().getY() + 150);
        this.setSize(460, 500);
        this.add(panel);
        //this.add(scrollbar);
        
        this.add(comboBox);
        this.add(acceptBtn);
        this.add(cancelBtn);

        calibrate();
        this.setVisible(true);
        this.setResizable(false);
    }

    private void updateChooseTable() {
        if (currentcoincomo != null) {
            COINCOMOSystem system = currentcoincomo.getCurrentSystem();
            

            if (system != null) {
                this.clefTableModel.setRowCount(0);
                int i=0;
                Iterator iter = system.getListOfSubUnits().iterator();
                while (iter.hasNext()) {
                COINCOMOSubSystem subSystemCopy = (COINCOMOSubSystem) iter.next();

                Iterator iter2 = subSystemCopy.getListOfSubUnits().iterator();

                while (iter2.hasNext()) {
                    COINCOMOVector<String> tableRowVector = new COINCOMOVector<String>();
                    COINCOMOComponent componentCopy = (COINCOMOComponent) iter2.next();
                    tableRowVector.add("" + componentCopy.isSelected());
                    tableRowVector.add(subSystemCopy.getName());
                    tableRowVector.add(componentCopy.getName());
                    componentList.add(componentCopy);
                    tableRowVector.setRowID(i);
            // Add a New Row To Table ...
                    clefTableModel.addRow(tableRowVector);
                    i++;

                }
            }
                
            }
            
        }
    }
    

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.acceptBtn) {
            // TODO (Larry) Implement the Accept functionality
            boolean b=apply();
            if(b==true)
            {
            this.dispose();
            }

        } else if (e.getSource() == this.cancelBtn) {
            this.dispose();
        } else if (e.getSource() == coefficientsOnlyBtn) {
            //Update the calibration method
            COINCOMOLocalCalibration.setCalibrationType(LocalCalibrationMode.COEFFICIENTS_ONLY);

            //Recalculate based on selection
            calibrate();
            //COINCOMOManager.get_Local_Calibration(component);
            /*
             newEffortCoefficientValueLabel.setText(component.getParameters().getA() + "");
             newEffortExponentValueLabel.setText(component.getParameters().getB() + "");
             newScheduleCoefficientValueLabel.setText(component.getParameters().getC() + "");
             newScheduleExponentValueLabel.setText(component.getParameters().getD() + "");
             */

        } else if (e.getSource() == coefficientsAndExponentsBtn) {
            //Update the calibration method
            COINCOMOLocalCalibration.setCalibrationType(LocalCalibrationMode.COEFFICIENTS_AND_EXPONENTS);

            //Recalculate based on selection
            calibrate();
            //COINCOMOManager.get_Local_Calibration(component);
            /*
             newEffortCoefficientValueLabel.setText(component.getParameters().getA() + "");
             newEffortExponentValueLabel.setText(component.getParameters().getB() + "");
             newScheduleCoefficientValueLabel.setText(component.getParameters().getC() + "");
             newScheduleExponentValueLabel.setText(component.getParameters().getD() + "");
             */
        }
        else if (e.getSource() == comboBox) {
            String insertOption = comboBox.getSelectedItem().toString();

            if ("Select All".equals(insertOption)) {
                for (int i = 0; i < clefTable.getRowCount(); i++) {
                    clefTable.setValueAt("true", i, 0);
//                    COINCOMOComponent project = (COINCOMOComponent) componentList.get(i);
//                    project.setSelected(true);
                    
                }
                System.out.println("AALL");
            } else if ("Unselect All".equals(insertOption)) {
                for (int i = 0; i < clefTable.getRowCount(); i++) {
                    clefTable.setValueAt("false", i, 0);
//                    COINCOMOComponent project = (COINCOMOComponent) componentList.get(i);
//                    project.setSelected(false);
                   
                }
                System.out.println("noaLL");
            }

            // Go Back To Default ... "Quick Select .."
            comboBox.setSelectedIndex(0);
        }
        
        else {
            this.dispose();
        }
    }
    
    
    private boolean apply() {
        
        //Iterator i = localCalibration.getListOfSubUnits().iterator();
        Iterator i = componentList.iterator();
        ArrayList<COINCOMOComponent> chooseComponents = new ArrayList<COINCOMOComponent>();

            int counter = 0;
            boolean somethingSelected = false;
            while (i.hasNext()) {
                COINCOMOComponent p = ( COINCOMOComponent) i.next();
                p.setSelected(Boolean.parseBoolean(clefTableModel.getValueAt(counter++, 0).toString()));
                if (p.isSelected()) {
                    somethingSelected = true;
                    chooseComponents.add(p);
                    
                }
            }
            if (!somethingSelected) {
                JOptionPane.showMessageDialog(null, "No Project Selected");
                return false;
            }
            else{
                Iterator i2 = chooseComponents.iterator();
                
                LocalCalibrationMode localCalibrationMode = COINCOMOLocalCalibration.getCalibrationMode();
                while(i2.hasNext())
                {
                    COINCOMOComponent currentchoose = ( COINCOMOComponent) i2.next();
                    COINCOMOComponentParameters parameters=currentchoose.getParameters();
                    if (localCalibrationMode == LocalCalibrationMode.COEFFICIENTS_ONLY){
                      parameters.setA(effortCoefficient); 
                      parameters.setC(scheduleCoefficient);
                    }
                    else if (localCalibrationMode == LocalCalibrationMode.COEFFICIENTS_AND_EXPONENTS) {
                    parameters.setA(effortCoefficient);
                    parameters.setB( effortExponent);
                    parameters.setC(scheduleCoefficient);
                    parameters.setD(scheduleExponent);
                    }


                COINCOMOComponentManager.updateComponent(currentchoose, true);
                    
                    
                }
              return true;  
            }  
    }

    private void calibrate() {
        LocalCalibrationMode localCalibrationMode = COINCOMOLocalCalibration.getCalibrationMode();

        if (localCalibrationMode == LocalCalibrationMode.COEFFICIENTS_ONLY) {
            COINCOMOLocalCalibrationManager.calculateCoefficientsOnly(localCalibration);

            effortCoefficient = localCalibration.getEffortCoefficient();
            effortExponent = 0.0d;
            scheduleCoefficient = localCalibration.getScheduleCoefficient();
            scheduleExponent = 0.0d;

            newEffortCoefficientValueLabel.setText("" + effortCoefficient);
            newEffortExponentValueLabel.setText("--");
            newScheduleCoefficientValueLabel.setText("" + scheduleCoefficient);
            newScheduleExponentValueLabel.setText("--");
        } else if (localCalibrationMode == LocalCalibrationMode.COEFFICIENTS_AND_EXPONENTS) {
            COINCOMOLocalCalibrationManager.calculateCoefficientsAndExponents(localCalibration);

            effortCoefficient = localCalibration.getEffortCoefficient();
            effortExponent = localCalibration.getEffortExponent();
            scheduleCoefficient = localCalibration.getScheduleCoefficient();
            scheduleExponent = localCalibration.getScheduleExponent();

            newEffortCoefficientValueLabel.setText("" + effortCoefficient);
            newEffortExponentValueLabel.setText("" + effortExponent);
            newScheduleCoefficientValueLabel.setText("" + scheduleCoefficient);
            newScheduleExponentValueLabel.setText("" + scheduleExponent);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        final int rowNumber = clefTable.rowAtPoint(e.getPoint());
        final int columnNumber = clefTable.columnAtPoint(e.getPoint());

        Vector<COINCOMOVector> vectors = clefTableModel.getDataVector();

        COINCOMOVector clickedRow = vectors.get(rowNumber);

        //final COINCOMOLocalCalibrationProject project = (COINCOMOLocalCalibrationProject) localCalibration.getListOfSubUnits().get(rowNumber);

        final COINCOMOComponent project = (COINCOMOComponent) componentList.get(rowNumber);
        if (columnNumber == 0) {
            System.out.println("Row " + rowNumber + " clicked.");
            System.out.println("Row " + rowNumber + " is " + project.getName());
            project.setSelected(Boolean.parseBoolean(clefTableModel.getValueAt(rowNumber, 0).toString()));
            System.out.println("Row " + rowNumber + " is "+project.isSelected());
        } else if (columnNumber == 1) {
            return;
        } else if (columnNumber == 2) {
            return;
        } 
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

}
