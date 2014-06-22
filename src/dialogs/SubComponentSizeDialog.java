/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package dialogs;

import core.COINCOMOAdaptationAndReuse;
import core.COINCOMOConstants;
import core.COINCOMOConstants.CalculationMethod;
import core.COINCOMOConstants.FP;
import core.COINCOMOConstants.RatioType;
import core.COINCOMOSubComponent;
import core.COINCOMOUnit;
import database.COINCOMOAdaptationAndReuseManager;
import database.COINCOMOSubComponentManager;
import extensions.COINCOMOCheckBoxCellEditor;
import extensions.COINCOMOCheckBoxTableCellRenderer;
import extensions.COINCOMOClefTableCellRenderer;
import extensions.COINCOMOClefTableHeaderRenderer;
import extensions.COINCOMOFixedTable;
import extensions.COINCOMOVector;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import main.COINCOMO;
import main.GlobalMethods;
import main.Icons;
import panels.ComponentOverviewPanel;

/**
 *
 * @author Raed Shomali
 */
public class SubComponentSizeDialog extends JDialog implements ActionListener, ChangeListener, MouseListener {

    private static final int D_WIDTH = 490;//420
    private static final int D_HEIGHT = 400;//300
    private static final int E_WIDTH = 840;//800
    private DefaultTableModel clefTableModel = new DefaultTableModel();
    private COINCOMOFixedTable clefTable;
    private DecimalFormat format1Decimal = new DecimalFormat("0.0");
    private DecimalFormat format2Decimals = new DecimalFormat("0.00");
    private DecimalFormat formatNoDecimal = new DecimalFormat("0");
    // Languages
    private JLabel languagesLabelNew = new JLabel("Languages:");
    private JLabel languagesLabelFP = new JLabel("Languages:");
    private JLabel languagesLabelAR = new JLabel("Languages:");
    private JScrollPane scroll = new JScrollPane();
    private int adaptSize = 0;
    private String davidLanguages[] = {
        "Non-specified",
        "Basic Assembly",
        "JCL",
        "Macro Assembly",
        "C",
        "COBOL 75(I)",
        "FORTRAN",
        "COBOL 85(II)",
        "PASCAL",
        "PL/1",
        "RPG I",
        "RPG II/III",
        "Natural",
        "C++",
        "Java",
        "DBASE III",
        "FOCUS",
        "Clipper",
        "Oracle",
        "Sybase",
        "DBASE IV",
        "Perl",
        "JavaScript",
        "VB Script",
        "Shell Script",
        "SAS",
        "APL"
    };
    private String jonesLanguages[] = {
        "Non-specified",
        "Access",
        "Ada 83",
        "Ada 95",
        "AI shell",
        "ANSI Basic",
        "ANSI Cobol 85",
        "APL",
        "Basic Assembly",
        "C",
        "C++",
        "Compiled Basic",
        "Database Default",
        "Fifth Generation",
        "First Generation",
        "Forth",
        "FORTRAN 77",
        "FORTRAN 95",
        "Fourth Generation",
        "High Level",
        "HTML 3.0",
        "Interpreted Basic",
        "JAVA",
        "LISP",
        "Machine Code",
        "Macro Assembly",
        "Modula 2",
        "Object Oriented Default",
        "PASCAL",
        "PERL",
        "Power Builder",
        "Procedural Language",
        "PROLOG",
        "Query Default",
        "Report Generator",
        "Second Generation",
        "Simulation Default",
        "Spreadsheet Default",
        "Third Generation",
        "Unix Shell Script",
        "USR 1",
        "USR 2",
        "USR 3",
        "USR 4",
        "USR 5",
        "Visual Basic 5",
        "Visual C++"
    };
    private String davidMultiplier[] = {
        "0",
        "575",
        "400",
        "400",
        "225",
        "220",
        "210",
        "175",
        "160",
        "126",
        "120",
        "110",
        "100",
        "80",
        "80",
        "60",
        "60",
        "60",
        "60",
        "60",
        "55",
        "50",
        "50",
        "50",
        "50",
        "50",
        "50"
    };
    private String jonesMultiplier[] = {
        "0",
        "38",
        "71",
        "49",
        "49",
        "64",
        "91",
        "32",
        "320",
        "128",
        "53",
        "49",
        "40",
        "5",
        "320",
        "49",
        "107",
        "71",
        "20",
        "91",
        "15",
        "32",
        "53",
        "64",
        "640",
        "213",
        "80",
        "29",
        "91",
        "21",
        "16",
        "105",
        "64",
        "13",
        "80",
        "107",
        "46",
        "6",
        "80",
        "21",
        "1",
        "1",
        "1",
        "1",
        "1",
        "29",
        "34"
    };
    private JLabel sizingTypeLabel = new JLabel("Select a sizing type:");
    private JComboBox languagesComboBoxFP = new JComboBox(jonesLanguages);
    private JComboBox languagesComboBoxAR = new JComboBox(jonesLanguages);
    private JComboBox languagesComboBoxNew = new JComboBox(jonesLanguages);
    // Breakage
    private JLabel breakageLabel = new JLabel("");
    private JLabel breakageDescLabel = new JLabel("<html><body>% of Code thrown away due to requirements evolution and volitality</body></html>");
    private JLabel breakageREVLLabel = new JLabel("REVL:");
    private JTextField breakageTextField = new JTextField("0");
    // Sloc
    private JLabel SLOCLabel = new JLabel("SLOC:");
    private JTextField newSLOCTextField = new JTextField("0");
    // Function Points
    private JLabel radioTypeLabel = new JLabel("Ratio Type");
    private JLabel calculationMethodLabel = new JLabel("Calculation Method");
    private JLabel functionTypeLabel = new JLabel("Function Type");
    private JLabel subTotalLabel = new JLabel("Sub Total");
    private JLabel inputLabel = new JLabel("Inputs:");
    private JLabel inputResultsLabel = new JLabel("?");
    private JLabel outputLabel = new JLabel("Outputs:");
    private JLabel outputResultsLabel = new JLabel("?");
    private JLabel fileLabel = new JLabel("Files:");
    private JLabel fileResultsLabel = new JLabel("?");
    private JLabel interfaceLabel = new JLabel("Interfaces:");
    private JLabel interfaceResultsLabel = new JLabel("?");
    private JLabel queryLabel = new JLabel("Queries:");
    private JLabel queryResultsLabel = new JLabel("?");
    private JLabel totalLabel = new JLabel("Total Unadjusted Function Points:");
    private JLabel eSLOCLabel = new JLabel("Equivalent SLOC:");
    private JLabel eSLOCResultsLabel = new JLabel("?");
    private JLabel totalSLOCLabel = new JLabel("Total SLOC (no REVL):");
    private JLabel totalSLOCResultsLabel = new JLabel("?");
    private ButtonGroup ratioTypeBtnGrp = new ButtonGroup();
    private ButtonGroup calculationMethodBtnGrp = new ButtonGroup();
    private JRadioButton ratioTypeRadBtns[] = new JRadioButton[COINCOMOConstants.RatioTypes.length];
    private JRadioButton calculationMethodRadBtns[] = new JRadioButton[COINCOMOConstants.CalculationMethods.length];
    private JTextField fileTextFields[] = new JTextField[COINCOMOConstants.FTS.length-1];
    private JTextField interfaceTextFields[] = new JTextField[COINCOMOConstants.FTS.length-1];
    private JTextField inputTextFields[] = new JTextField[COINCOMOConstants.FTS.length-1];
    private JTextField outputTextFields[] = new JTextField[COINCOMOConstants.FTS.length-1];
    private JTextField queryTextFields[] = new JTextField[COINCOMOConstants.FTS.length-1];
    private JTextField totalTextField = new JTextField();
    // Tabs
    private JTabbedPane tabs = new JTabbedPane();
    // Buttons
    private JButton applyButton = new JButton("Apply");
    private JButton closeButton = new JButton("Close");
    private JButton addAdaptation = new JButton("Reused Module");
    private JButton delAdaptation = new JButton("Reused Module");
    private COINCOMOSubComponent subComponent = null;
    private ComponentOverviewPanel componentOverviewPanel = null;
    //private int rowNumber = 0;
    private int totFunctionPoints = 0;
    //private JLabel changeMultiplierLabel = new JLabel("Multiplier");
    private JTextField changeMultiplierField = new JTextField();
    private JButton changeMultiplierButton = new JButton("Change Multiplier");
    private JPanel functionPointsPanel = new JPanel(null);
    // Hold temporary new name
    private String tempName = null;

    public SubComponentSizeDialog(COINCOMO owner, ComponentOverviewPanel cOPanel, COINCOMOSubComponent subComponent/*, int rowNumber*/) {
        super(owner);

        this.setModal(true);

        this.setTitle("Size - " + subComponent.getName());

        this.componentOverviewPanel = cOPanel;

        //this.rowNumber = rowNumber;
        this.subComponent = subComponent;

        GlobalMethods.updateStatusBar("Done.", owner);

        // Labels
        SLOCLabel.setFont(new Font("arial", 1, 11));
        //languagesLabel.setFont(new Font("arial", 1, 11));
        breakageLabel.setFont(new Font("arial", 1, 11));
        breakageDescLabel.setFont(new Font("arial", 1, 11));
        breakageREVLLabel.setFont(new Font("arial", 1, 11));
        //changeMultiplierLabel.setFont(new Font("arial", 1, 11));

        TitledBorder legendTitleBorder = BorderFactory.createTitledBorder("Breakage");
        legendTitleBorder.setTitleColor(Color.BLUE);
        breakageLabel.setBorder(legendTitleBorder);

        // ComboBox
        //languagesLabel.setFont(new Font("arial", 1, 11));
        //languagesComboBox.setBackground(Color.WHITE);
        //languagesComboBox.setFont(new Font("arial", 1, 12));

        // Buttons
        applyButton.addActionListener(this);
        closeButton.addActionListener(this);

        //Buttons for multiple Adaptation
        addAdaptation.addActionListener(this);
        delAdaptation.addActionListener(this);

        languagesComboBoxFP.addActionListener(this);
        languagesComboBoxNew.addActionListener(this);
        languagesComboBoxAR.addActionListener(this);

        changeMultiplierButton.addActionListener(this);

        applyButton.setFocusable(false);
        //resetButton.setFocusable(false);
        closeButton.setFocusable(false);

        applyButton.setIcon(Icons.SAVE_ICON);
        //resetButton.setIcon(Icons.RESET_ICON);
        closeButton.setIcon(Icons.CLOSE_ICON);

        addAdaptation.setIcon(Icons.ADD_SUBCOMPONENT_ICON);
        delAdaptation.setIcon(Icons.DELETE_SUBCOMPONENT_ICON);
        // Panel Tabs
        JPanel slocPanel = new JPanel(null);

        languagesLabelNew.setFont(new Font("arial", 1, 11));
        languagesComboBoxNew.setBackground(Color.WHITE);
        languagesComboBoxNew.setFont(new Font("arial", 1, 12));

        slocPanel.add(languagesLabelNew);
        slocPanel.add(languagesComboBoxNew);
        slocPanel.add(SLOCLabel);
        slocPanel.add(newSLOCTextField);

        SLOCLabel.setBounds(20, 20, 50, 20);
        newSLOCTextField.setBounds(100, 20, 100, 20);
        languagesLabelNew.setBounds(20, 50, 90, 20);
        languagesComboBoxNew.setBounds(100, 50, 170, 20);

        //resetButton.setBounds(300, 295, 95, 27);

        ratioTypeRadBtns[0] = new JRadioButton(RatioType.Jones.name());
        ratioTypeRadBtns[0].setActionCommand(RatioType.Jones.toString());
        ratioTypeRadBtns[1] = new JRadioButton(RatioType.David.name());
        ratioTypeRadBtns[1].setActionCommand(RatioType.David.toString());
        ratioTypeRadBtns[0].addActionListener(this);
        ratioTypeRadBtns[1].addActionListener(this);
        calculationMethodRadBtns[0] = new JRadioButton(CalculationMethod.UsingTable.toString());
        calculationMethodRadBtns[0].setActionCommand(CalculationMethod.UsingTable.toString());
        calculationMethodRadBtns[1] = new JRadioButton(CalculationMethod.InputCalculatedFunctionPoints.toString());
        calculationMethodRadBtns[1].setActionCommand(CalculationMethod.InputCalculatedFunctionPoints.toString());
        calculationMethodRadBtns[0].addActionListener(this);
        calculationMethodRadBtns[1].addActionListener(this);

        ratioTypeBtnGrp.add(ratioTypeRadBtns[0]);
        ratioTypeBtnGrp.add(ratioTypeRadBtns[1]);
        calculationMethodBtnGrp.add(calculationMethodRadBtns[0]);
        calculationMethodBtnGrp.add(calculationMethodRadBtns[1]);

        radioTypeLabel.setFont(new Font("arial", 1, 11));
        calculationMethodLabel.setFont(new Font("arial", 1, 11));
        for (int i = 0; i < 2; i++) {
            ratioTypeRadBtns[i].setFont(new Font("arial", 1, 11));
            calculationMethodRadBtns[i].setFont(new Font("arial", 1, 11));
        }
        functionTypeLabel.setFont(new Font("arial", 1, 11));
        subTotalLabel.setFont(new Font("arial", 1, 11));
        inputLabel.setFont(new Font("arial", 1, 11));
        inputResultsLabel.setFont(new Font("arial", 1, 11));
        outputLabel.setFont(new Font("arial", 1, 11));
        outputResultsLabel.setFont(new Font("arial", 1, 11));
        fileLabel.setFont(new Font("arial", 1, 11));
        fileResultsLabel.setFont(new Font("arial", 1, 11));
        interfaceLabel.setFont(new Font("arial", 1, 11));
        interfaceResultsLabel.setFont(new Font("arial", 1, 11));
        queryLabel.setFont(new Font("arial", 1, 11));
        queryResultsLabel.setFont(new Font("arial", 1, 11));
        totalLabel.setFont(new Font("arial", 1, 11));
        eSLOCLabel.setFont(new Font("arial", 1, 11));
        eSLOCResultsLabel.setFont(new Font("arial", 1, 11));
        sizingTypeLabel.setFont(new Font("arial", 1, 11));
        totalSLOCLabel.setFont(new Font("arial", 1, 11));
        totalSLOCResultsLabel.setFont(new Font("arial", 1, 11));
        functionTypeLabel.setHorizontalAlignment(JLabel.CENTER);
        fileLabel.setHorizontalAlignment(JLabel.LEFT);
        fileResultsLabel.setHorizontalAlignment(JLabel.CENTER);
        interfaceLabel.setHorizontalAlignment(JLabel.LEFT);
        interfaceResultsLabel.setHorizontalAlignment(JLabel.CENTER);
        inputLabel.setHorizontalAlignment(JLabel.LEFT);
        inputResultsLabel.setHorizontalAlignment(JLabel.CENTER);
        outputLabel.setHorizontalAlignment(JLabel.LEFT);
        outputResultsLabel.setHorizontalAlignment(JLabel.CENTER);
        queryLabel.setHorizontalAlignment(JLabel.LEFT);
        queryResultsLabel.setHorizontalAlignment(JLabel.CENTER);
        eSLOCResultsLabel.setHorizontalAlignment(JLabel.CENTER);
        totalSLOCResultsLabel.setHorizontalAlignment(JLabel.RIGHT);

        functionTypeLabel.setForeground(Color.BLUE);
        subTotalLabel.setForeground(Color.BLUE);

        languagesLabelFP.setFont(new Font("arial", 1, 11));
        languagesComboBoxFP.setBackground(Color.WHITE);
        languagesComboBoxFP.setFont(new Font("arial", 1, 12));

        changeMultiplierButton.setFont(new Font("arial", 1, 11));

        functionPointsPanel.add(languagesLabelFP);
        functionPointsPanel.add(languagesComboBoxFP);

        functionPointsPanel.add(radioTypeLabel);
        functionPointsPanel.add(calculationMethodLabel);
        for (int i = 0; i < 2; i++) {
            functionPointsPanel.add(ratioTypeRadBtns[i]);
            functionPointsPanel.add(calculationMethodRadBtns[i]);
        }

        //functionPointsPanel.add(resetButton);

        functionPointsPanel.add(functionTypeLabel);
        functionPointsPanel.add(subTotalLabel);
        functionPointsPanel.add(fileLabel);
        functionPointsPanel.add(fileResultsLabel);
        functionPointsPanel.add(interfaceLabel);
        functionPointsPanel.add(interfaceResultsLabel);
        functionPointsPanel.add(inputLabel);
        functionPointsPanel.add(inputResultsLabel);
        functionPointsPanel.add(outputLabel);
        functionPointsPanel.add(outputResultsLabel);
        functionPointsPanel.add(queryLabel);
        functionPointsPanel.add(queryResultsLabel);
        functionPointsPanel.add(totalLabel);
        functionPointsPanel.add(eSLOCLabel);
        functionPointsPanel.add(eSLOCResultsLabel);

        functionPointsPanel.add(totalTextField);
        //functionPointsPanel.add(changeMultiplierLabel);
        functionPointsPanel.add(changeMultiplierField);
        functionPointsPanel.add(changeMultiplierButton);

        int yDelta = 50;
        radioTypeLabel.setBounds(30, 10 + yDelta, 100, 20);
        calculationMethodLabel.setBounds(30, 40 + yDelta, 120, 20);

        ratioTypeRadBtns[0].setBounds(140, 10 + yDelta, 100, 20);
        ratioTypeRadBtns[1].setBounds(250, 10 + yDelta, 100, 20);
        calculationMethodRadBtns[0].setBounds(140, 40 + yDelta, 100, 20);
        calculationMethodRadBtns[1].setBounds(250, 40 + yDelta, 230, 20);

        functionTypeLabel.setBounds(30, 70 + yDelta, 100, 20);
        subTotalLabel.setBounds(300, 70 + yDelta, 100, 20);

        fileLabel.setBounds(50, 90 + yDelta, 80, 20);
        fileResultsLabel.setBounds(300, 90 + yDelta, 50, 20);

        interfaceLabel.setBounds(50, 110 + yDelta, 80, 20);
        interfaceResultsLabel.setBounds(300, 110 + yDelta, 50, 20);

        inputLabel.setBounds(50, 130 + yDelta, 80, 20);
        inputResultsLabel.setBounds(300, 130 + yDelta, 50, 20);

        outputLabel.setBounds(50, 150 + yDelta, 80, 20);
        outputResultsLabel.setBounds(300, 150 + yDelta, 50, 20);

        queryLabel.setBounds(50, 170 + yDelta, 80, 20);
        queryResultsLabel.setBounds(300, 170 + yDelta, 50, 20);

        totalLabel.setBounds(60, 200 + yDelta, 200, 20);
        totalTextField.setBounds(280, 200 + yDelta, 80, 20);

        eSLOCLabel.setBounds(60, 220 + yDelta, 100, 20);
        eSLOCResultsLabel.setBounds(280, 220 + yDelta, 80, 20);

        languagesLabelFP.setBounds(15, 15, 90, 20);
        languagesComboBoxFP.setBounds(85, 15, 170, 20);


        //changeMultiplierLabel.setBounds(290, 15, 170, 20);
        //changeMultiplierField.setBounds(370, 15, 50, 20);
        changeMultiplierField.setBounds(410, 15, 50, 20);
        changeMultiplierField.setEditable(false);
        changeMultiplierButton.setBounds(265, 15, 135, 20);

        for (int i = 0; i < COINCOMOConstants.FTS.length-1; i++) {
            fileTextFields[i] = new JTextField();
            interfaceTextFields[i] = new JTextField();
            inputTextFields[i] = new JTextField();
            outputTextFields[i] = new JTextField();
            queryTextFields[i] = new JTextField();
        }

        for (int i = 0; i < COINCOMOConstants.FTS.length-1; i++) {
            // Adding to set focus on text field, on tabbing.
            final int index = i;
            fileTextFields[index].addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent evt) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            fileTextFields[index].selectAll();
                        }
                    });
                }
            });

            interfaceTextFields[index].addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent evt) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            interfaceTextFields[index].selectAll();
                        }
                    });
                }
            });

            inputTextFields[index].addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent evt) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            inputTextFields[index].selectAll();
                        }
                    });
                }
            });

            outputTextFields[index].addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent evt) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            outputTextFields[index].selectAll();
                        }
                    });
                }
            });

            queryTextFields[index].addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent evt) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            queryTextFields[index].selectAll();
                        }
                    });
                }
            });
        }

        fileTextFields[0].setBounds(135, 90 + yDelta, 50, 20);
        fileTextFields[1].setBounds(185, 90 + yDelta, 50, 20);
        fileTextFields[2].setBounds(235, 90 + yDelta, 50, 20);
        interfaceTextFields[0].setBounds(135, 110 + yDelta, 50, 20);
        interfaceTextFields[1].setBounds(185, 110 + yDelta, 50, 20);
        interfaceTextFields[2].setBounds(235, 110 + yDelta, 50, 20);
        inputTextFields[0].setBounds(135, 130 + yDelta, 50, 20);
        inputTextFields[1].setBounds(185, 130 + yDelta, 50, 20);
        inputTextFields[2].setBounds(235, 130 + yDelta, 50, 20);
        outputTextFields[0].setBounds(135, 150 + yDelta, 50, 20);
        outputTextFields[1].setBounds(185, 150 + yDelta, 50, 20);
        outputTextFields[2].setBounds(235, 150 + yDelta, 50, 20);
       
        queryTextFields[0].setBounds(135, 170 + yDelta, 50, 20);
        queryTextFields[1].setBounds(185, 170 + yDelta, 50, 20);
        queryTextFields[2].setBounds(235, 170 + yDelta, 50, 20);

        for (int i = 0; i < COINCOMOConstants.FTS.length-1; i++) {
            functionPointsPanel.add(fileTextFields[i]);
            functionPointsPanel.add(interfaceTextFields[i]);
            functionPointsPanel.add(inputTextFields[i]);
            functionPointsPanel.add(outputTextFields[i]);
            functionPointsPanel.add(queryTextFields[i]);
        };

        JPanel adaptationPanel = new JPanel(null);
        //new adaptationpanel
        addAdaptation.setBounds(30, 45, 150, 25);
        delAdaptation.setBounds(250, 45, 150, 25);

        languagesLabelAR.setFont(new Font("arial", 1, 11));
        languagesComboBoxAR.setBackground(Color.WHITE);
        languagesComboBoxAR.setFont(new Font("arial", 1, 12));

        adaptationPanel.add(languagesLabelAR);
        adaptationPanel.add(languagesComboBoxAR);
        languagesLabelAR.setBounds(30, 15, 90, 20);
        languagesComboBoxAR.setBounds(100, 15, 170, 20);

        adaptationPanel.add(addAdaptation);
        adaptationPanel.add(delAdaptation);
        adaptationPanel.setSize(this.getSize());
        clefTableModel = new DefaultTableModel();
        clefTable = new COINCOMOFixedTable(clefTableModel);

        // Table
        clefTable.setRowSelectionAllowed(false);
        clefTable.addMouseListener(this);

        // Html Was added to be able to make it Multiline ..
        clefTableModel.addColumn("X");
        clefTableModel.addColumn("Name");
        clefTableModel.addColumn("<html><body style='text-align:center'>Equivalent<br />SLOC</body></html>");
        clefTableModel.addColumn("<html><body style='text-align:center'>Adapted<br />(Initial)<br />SLOC</body></html>");
        clefTableModel.addColumn("<html><body style='text-align:center'>Design<br />Modified</body></html>");
        clefTableModel.addColumn("<html><body style='text-align:center'>Code<br />Modified</body></html>");
        clefTableModel.addColumn("<html><body style='text-align:center'>Integration<br />Modified</body></html>");
        clefTableModel.addColumn("<html><body style='text-align:center'>Software<br />Understanding</body></html>");
        clefTableModel.addColumn("<html><body style='text-align:center'>Assessment<br />Assimilation</body></html>");
        clefTableModel.addColumn("Unfamiliarity");
        clefTableModel.addColumn("<html><body style='text-align:center'>Auto<br />Translated</body></html>");
        clefTableModel.addColumn("<html><body style='text-align:center'>AutoTranslation<br />Efficieny</body></html>");

        COINCOMOClefTableHeaderRenderer multiLineTableHeaderRenderer = new COINCOMOClefTableHeaderRenderer(3.5);
        COINCOMOClefTableCellRenderer colorfulTableCellRenderer = new COINCOMOClefTableCellRenderer(11);

        Enumeration<TableColumn> columns = clefTable.getColumnModel().getColumns();

        // Go Through All Columns ..
        while (columns.hasMoreElements()) {
            // Set Each with Our Table Header Renderer ...
            TableColumn column = (TableColumn) columns.nextElement();
            column.setHeaderRenderer(multiLineTableHeaderRenderer);
            column.setCellRenderer(colorfulTableCellRenderer);
        }

        ArrayList<COINCOMOUnit> adaptations = subComponent.getListOfSubUnits();
        for (int i = 0; i < adaptations.size(); i++) {
            //for multiple adaptation and reuse
            COINCOMOAdaptationAndReuse adap = (COINCOMOAdaptationAndReuse) adaptations.get(i);
            COINCOMOVector<String> tableRowVector = new COINCOMOVector<String>();

            tableRowVector.add("false");
            tableRowVector.add(adap.getName());
            tableRowVector.add(GlobalMethods.FormatLongWithComma(adap.getEquivalentSLOC()));
            tableRowVector.add(GlobalMethods.FormatLongWithComma(adap.getAdaptedSLOC()));
            tableRowVector.add(format2Decimals.format(adap.getDesignModified()));
            tableRowVector.add(format2Decimals.format(adap.getCodeModified()));
            tableRowVector.add(format2Decimals.format(adap.getIntegrationModified()));
            tableRowVector.add(format2Decimals.format(adap.getSoftwareUnderstanding()));
            tableRowVector.add(format2Decimals.format(adap.getAssessmentAndAssimilation()));
            tableRowVector.add(format2Decimals.format(adap.getUnfamiliarityWithSoftware()));
            tableRowVector.add(format2Decimals.format(adap.getAutomaticallyTranslated()));
            tableRowVector.add(format2Decimals.format(adap.getAutomaticTranslationProductivity()));

            tableRowVector.setRowID(i);
            // Add a New Row To Table ...
            clefTableModel.addRow(tableRowVector);
        }

        // Setting the Width of Some Columns ...
        clefTable.getColumnModel().getColumn(0).setPreferredWidth(15);
        clefTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        clefTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        clefTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        clefTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        clefTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        clefTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        clefTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        clefTable.getColumnModel().getColumn(8).setPreferredWidth(100);
        clefTable.getColumnModel().getColumn(9).setPreferredWidth(100);
        clefTable.getColumnModel().getColumn(10).setPreferredWidth(100);
        clefTable.getColumnModel().getColumn(0).setCellRenderer(new COINCOMOCheckBoxTableCellRenderer());
        clefTable.getColumnModel().getColumn(0).setCellEditor(new COINCOMOCheckBoxCellEditor());
        //clefTable.setSize(E_WIDTH - 40, 210);
        //scroll.add(clefTable);
        scroll = new JScrollPane(clefTable);
        adaptSize = E_WIDTH;
        //scroll.setBounds(10, 85, E_WIDTH - 30, 170);
        scroll.setBounds(10, 85, E_WIDTH - 40, 210);
        adaptationPanel.add(scroll, BorderLayout.CENTER);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int resize = e.getComponent().getSize().width;
                //clefTable.setSize(resize-40, 210);
                scroll.setSize(resize - 40, 210);
            }
        });

        // Tabs
        tabs.addTab("New", slocPanel);
        tabs.addTab("Function Points", functionPointsPanel);
        tabs.addTab("Adaptation & Reuse", adaptationPanel);
        tabs.setPreferredSize(new Dimension(D_WIDTH, D_HEIGHT - 50));
        tabs.setSize(tabs.getPreferredSize());

        tabs.addChangeListener(this);

        // GUI
        JPanel northPanel = new JPanel(null);
        //northPanel.add(languagesLabel);
        //northPanel.add(languagesComboBox);
        northPanel.add(breakageLabel);
        northPanel.add(breakageDescLabel);
        northPanel.add(breakageREVLLabel);
        northPanel.add(breakageTextField);
        northPanel.add(sizingTypeLabel);

        northPanel.setPreferredSize(new Dimension(100, 155));


        breakageLabel.setBounds(230, 10, 230, 100);
        breakageDescLabel.setBounds(245, 30, 220, 40);
        breakageREVLLabel.setBounds(245, 75, 50, 20);
        breakageTextField.setBounds(295, 75, 150, 20);
        sizingTypeLabel.setBounds(15, 120, 170, 20);

        //Panel for Total SLOC label and text
        JPanel slotPanel = new JPanel();
        slotPanel.add(totalSLOCLabel);
        slotPanel.add(totalSLOCResultsLabel);

        //Panel for Apply/Close buttons
        JPanel southPanel = new JPanel();
        southPanel.add(applyButton);
        southPanel.add(closeButton);

        //BoxLayout for vertical panel flow
        this.setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        this.add(northPanel);
        this.add(tabs);
        this.add(slotPanel);
        this.add(southPanel);

        // Loading ...
        loadParameters();

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(this.getOwner().getX() + 300, this.getOwner().getY() + 10);
        this.setResizable(true);
        this.setSize(D_WIDTH, 580);//480
        this.setMinimumSize(new Dimension(D_WIDTH, 580));
        this.setMaximumSize(new Dimension(1050, 580));//1025,600
        this.setVisible(true);
    }

    public void resizePannel() {
    }

    /**
     *
     */
    public void loadParameters() {
        //tabs.setSelectedIndex(subComponent.getSizingType());

        applyButton.setEnabled(false);
        closeButton.setEnabled(false);

        ratioTypeRadBtns[subComponent.getRatioType().ordinal()].setSelected(true);
        calculationMethodRadBtns[subComponent.getCalculationMethod().ordinal()].setSelected(true);

        if (ratioTypeRadBtns[RatioType.Jones.ordinal()].isSelected()) {
            languagesComboBoxFP.removeActionListener(this);
            languagesComboBoxAR.removeActionListener(this);
            languagesComboBoxNew.removeActionListener(this);

            languagesComboBoxFP.removeAllItems();
            languagesComboBoxAR.removeAllItems();
            languagesComboBoxNew.removeAllItems();
            int i = 0, setIndex = 0;
            for (String jLang : jonesLanguages) {
                languagesComboBoxFP.addItem(jLang);
                languagesComboBoxAR.addItem(jLang);
                languagesComboBoxNew.addItem(jLang);
                if (jonesLanguages[i].equalsIgnoreCase(subComponent.getLanguage())) {
                    setIndex = i;
                }
                i++;
            }
            languagesComboBoxFP.setSelectedIndex(setIndex);
            languagesComboBoxNew.setSelectedIndex(setIndex);
            languagesComboBoxAR.setSelectedIndex(setIndex);
            //SubComponentSizeDialog.this.changeMultiplierField.setText(jonesMultiplier[setIndex]);
            SubComponentSizeDialog.this.changeMultiplierField.setText(String.valueOf(subComponent.getMultiplier()));
            //System.out.print("language list replaced with jones");

            languagesComboBoxFP.addActionListener(this);
            languagesComboBoxAR.addActionListener(this);
            languagesComboBoxNew.addActionListener(this);
        } else if (ratioTypeRadBtns[RatioType.David.ordinal()].isSelected()) {
            languagesComboBoxFP.removeActionListener(this);
            languagesComboBoxAR.removeActionListener(this);
            languagesComboBoxNew.removeActionListener(this);

            languagesComboBoxFP.removeAllItems();
            languagesComboBoxAR.removeAllItems();
            languagesComboBoxNew.removeAllItems();

            int i = 0, setIndex = 0;
            for (String dLang : davidLanguages) {
                languagesComboBoxFP.addItem(dLang);
                languagesComboBoxAR.addItem(dLang);
                languagesComboBoxNew.addItem(dLang);
                if (davidLanguages[i].equalsIgnoreCase(subComponent.getLanguage())) {
                    setIndex = i;
                }
                i++;
            }
            languagesComboBoxFP.setSelectedIndex(setIndex);
            languagesComboBoxNew.setSelectedIndex(setIndex);
            languagesComboBoxAR.setSelectedIndex(setIndex);
            SubComponentSizeDialog.this.changeMultiplierField.setText(String.valueOf(subComponent.getMultiplier()));
            //((COINCOMOComponent)subComponent.getParent()).setChangeMultiplier(Long.parseLong(SubComponentSizeDialog.this.changeMultiplierField.getText()));
            //System.out.print("language list replaced with david");

            languagesComboBoxFP.addActionListener(this);
            languagesComboBoxAR.addActionListener(this);
            languagesComboBoxNew.addActionListener(this);
        }

        if (calculationMethodRadBtns[CalculationMethod.UsingTable.ordinal()].isSelected()) {
            functionTypeLabel.setVisible(true);
            subTotalLabel.setVisible(true);
            inputLabel.setVisible(true);
            inputResultsLabel.setVisible(true);
            outputLabel.setVisible(true);
            outputResultsLabel.setVisible(true);
            fileLabel.setVisible(true);
            fileResultsLabel.setVisible(true);
            interfaceLabel.setVisible(true);
            interfaceResultsLabel.setVisible(true);
            queryLabel.setVisible(true);
            queryResultsLabel.setVisible(true);
            for (int i = 0; i < COINCOMOConstants.FTS.length-1; i++) {
                inputTextFields[i].setVisible(true);
                outputTextFields[i].setVisible(true);
                fileTextFields[i].setVisible(true);
                interfaceTextFields[i].setVisible(true);
                queryTextFields[i].setVisible(true);
            }
            totalTextField.setEditable(false);
        } else if (calculationMethodRadBtns[CalculationMethod.InputCalculatedFunctionPoints.ordinal()].isSelected()) {
            functionTypeLabel.setVisible(false);
            subTotalLabel.setVisible(false);
            inputLabel.setVisible(false);
            inputResultsLabel.setVisible(false);
            outputLabel.setVisible(false);
            outputResultsLabel.setVisible(false);
            fileLabel.setVisible(false);
            fileResultsLabel.setVisible(false);
            interfaceLabel.setVisible(false);
            interfaceResultsLabel.setVisible(false);
            queryLabel.setVisible(false);
            queryResultsLabel.setVisible(false);
            for (int i = 0; i < COINCOMOConstants.FTS.length-1; i++) {
                inputTextFields[i].setVisible(false);
                outputTextFields[i].setVisible(false);
                fileTextFields[i].setVisible(false);
                interfaceTextFields[i].setVisible(false);
                queryTextFields[i].setVisible(false);
            }
            totalTextField.setEditable(true);
        }

        newSLOCTextField.setText(GlobalMethods.FormatLongWithComma(subComponent.getNewSLOC()) + "");

        breakageTextField.setText(subComponent.getREVL() + "");

        for (int i = 0; i < COINCOMOConstants.FTS.length-1; i++) {
            inputTextFields[i].setText("" + subComponent.getExternalInputs()[i]);
            outputTextFields[i].setText("" + subComponent.getExternalOutputs()[i]);
            fileTextFields[i].setText("" + subComponent.getInternalLogicalFiles()[i]);
            interfaceTextFields[i].setText("" + subComponent.getExternalInterfaceFiles()[i]);
            queryTextFields[i].setText("" + subComponent.getExternalInquiries()[i]);
        }

        changeMultiplierField.setText(subComponent.getMultiplier() + "");
        inputResultsLabel.setText("" + subComponent.getExternalInputs()[3]);
        outputResultsLabel.setText("" + subComponent.getExternalOutputs()[3]);
        fileResultsLabel.setText("" + subComponent.getInternalLogicalFiles()[3]);
        interfaceResultsLabel.setText("" + subComponent.getExternalInterfaceFiles()[3]);
        queryResultsLabel.setText("" + subComponent.getExternalInquiries()[3]);
        totalTextField.setText(GlobalMethods.FormatLongWithComma(subComponent.getTotalUnadjustedFunctionPoints()) + "");
        eSLOCResultsLabel.setText(GlobalMethods.FormatLongWithComma(subComponent.getEquivalentSLOC()) + "");
        totalSLOCResultsLabel.setText(GlobalMethods.FormatLongWithComma(subComponent.getSumOfSLOCs()) + "");

        // Set Back To Default ...
        applyButton.setEnabled(true);
        closeButton.setEnabled(true);

        GlobalMethods.updateStatusBar("Parameters Loaded.", (COINCOMO) this.getParent());
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == ratioTypeRadBtns[RatioType.Jones.ordinal()]) {

            // languagesComboBoxFP = new JComboBox(jonesLanguages);
            languagesComboBoxFP.removeActionListener(this);
            languagesComboBoxAR.removeActionListener(this);
            languagesComboBoxNew.removeActionListener(this);

            languagesComboBoxFP.removeAllItems();
            languagesComboBoxAR.removeAllItems();
            languagesComboBoxNew.removeAllItems();
            for (String jLang : jonesLanguages) {
                languagesComboBoxFP.addItem(jLang);
                languagesComboBoxAR.addItem(jLang);
                languagesComboBoxNew.addItem(jLang);
            }
            languagesComboBoxFP.setSelectedIndex(0);
            changeMultiplierField.setText("0");
            //System.out.print("language list replaced with jones");

            languagesComboBoxFP.addActionListener(this);
            languagesComboBoxAR.addActionListener(this);
            languagesComboBoxNew.addActionListener(this);

        } else if (e.getSource() == ratioTypeRadBtns[RatioType.David.ordinal()]) {
            // languagesComboBoxFP = new JComboBox(davidLanguages);
            languagesComboBoxFP.removeActionListener(this);
            languagesComboBoxAR.removeActionListener(this);
            languagesComboBoxNew.removeActionListener(this);

            languagesComboBoxFP.removeAllItems();
            languagesComboBoxAR.removeAllItems();
            languagesComboBoxNew.removeAllItems();
            for (String dLang : davidLanguages) {
                languagesComboBoxFP.addItem(dLang);
                languagesComboBoxAR.addItem(dLang);
                languagesComboBoxNew.addItem(dLang);
            }
            languagesComboBoxFP.setSelectedIndex(0);
            changeMultiplierField.setText("0");
            //System.out.print("language list replaced with david");

            languagesComboBoxFP.addActionListener(this);
            languagesComboBoxAR.addActionListener(this);
            languagesComboBoxNew.addActionListener(this);


        } else if (e.getSource() == calculationMethodRadBtns[CalculationMethod.UsingTable.ordinal()]) {
            functionTypeLabel.setVisible(true);
            subTotalLabel.setVisible(true);
            inputLabel.setVisible(true);
            inputResultsLabel.setVisible(true);
            outputLabel.setVisible(true);
            outputResultsLabel.setVisible(true);
            fileLabel.setVisible(true);
            fileResultsLabel.setVisible(true);
            interfaceLabel.setVisible(true);
            interfaceResultsLabel.setVisible(true);
            queryLabel.setVisible(true);
            queryResultsLabel.setVisible(true);
            for (int i = 0; i < COINCOMOConstants.FTS.length-1; i++) {
                inputTextFields[i].setVisible(true);
                outputTextFields[i].setVisible(true);
                fileTextFields[i].setVisible(true);
                interfaceTextFields[i].setVisible(true);
                queryTextFields[i].setVisible(true);
            }
            totalTextField.setEditable(false);
        } else if (e.getSource() == calculationMethodRadBtns[CalculationMethod.InputCalculatedFunctionPoints.ordinal()]) {
            functionTypeLabel.setVisible(false);
            subTotalLabel.setVisible(false);
            inputLabel.setVisible(false);
            inputResultsLabel.setVisible(false);
            outputLabel.setVisible(false);
            outputResultsLabel.setVisible(false);
            fileLabel.setVisible(false);
            fileResultsLabel.setVisible(false);
            interfaceLabel.setVisible(false);
            interfaceResultsLabel.setVisible(false);
            queryLabel.setVisible(false);
            queryResultsLabel.setVisible(false);
            for (int i = 0; i < COINCOMOConstants.FTS.length-1; i++) {
                inputTextFields[i].setVisible(false);
                outputTextFields[i].setVisible(false);
                fileTextFields[i].setVisible(false);
                interfaceTextFields[i].setVisible(false);
                queryTextFields[i].setVisible(false);
            }
            totalTextField.setEditable(true);
        } else if (e.getSource() == languagesComboBoxFP) {

            String fpLang = languagesComboBoxFP.getSelectedItem().toString();
            languagesComboBoxAR.setSelectedItem(fpLang);
            languagesComboBoxNew.setSelectedItem(fpLang);
            String fpMultiplier = "0";
            subComponent.setLanguage(fpLang);

            for (int i = 0; i < languagesComboBoxFP.getItemCount(); i++) {
                if (ratioTypeRadBtns[RatioType.Jones.ordinal()].isSelected()) {
                    if (jonesLanguages[i].equalsIgnoreCase(fpLang)) {
                        fpMultiplier = jonesMultiplier[i];
                        break;
                    }

                } else {
                    if (i > 26) {
                        break;
                    }
                    if (davidLanguages[i].equalsIgnoreCase(fpLang)) {
                        fpMultiplier = davidMultiplier[i];
                        break;
                    }
                }

            }

            SubComponentSizeDialog.this.changeMultiplierField.setText(fpMultiplier);

            subComponent.setMultiplier(Integer.parseInt(SubComponentSizeDialog.this.changeMultiplierField.getText()));

        } else if (e.getSource() == languagesComboBoxAR) {

            String fpLang = languagesComboBoxAR.getSelectedItem().toString();
            languagesComboBoxFP.setSelectedItem(fpLang);
            String fpMultiplier = "0";
            subComponent.setLanguage(fpLang);

            for (int i = 0; i < languagesComboBoxAR.getItemCount(); i++) {
                if (ratioTypeRadBtns[RatioType.Jones.ordinal()].isSelected()) {
                    if (jonesLanguages[i].equalsIgnoreCase(fpLang)) {
                        fpMultiplier = jonesMultiplier[i];
                        break;
                    }

                } else {
                    if (i > 26) {
                        break;
                    }
                    if (davidLanguages[i].equalsIgnoreCase(fpLang)) {
                        fpMultiplier = davidMultiplier[i];
                        break;
                    }
                }

            }

            SubComponentSizeDialog.this.changeMultiplierField.setText(fpMultiplier);
            subComponent.setMultiplier(Integer.parseInt(SubComponentSizeDialog.this.changeMultiplierField.getText()));

        } else if (e.getSource() == languagesComboBoxNew) {

            String fpLang = languagesComboBoxNew.getSelectedItem().toString();
            languagesComboBoxFP.setSelectedItem(fpLang);
            String fpMultiplier = "0";
            subComponent.setLanguage(fpLang);

            for (int i = 0; i < languagesComboBoxNew.getItemCount(); i++) {
                if (ratioTypeRadBtns[RatioType.Jones.ordinal()].isSelected()) {
                    if (jonesLanguages[i].equalsIgnoreCase(fpLang)) {
                        fpMultiplier = jonesMultiplier[i];
                        break;
                    }

                } else {
                    if (i > 26) {
                        break;
                    }
                    if (davidLanguages[i].equalsIgnoreCase(fpLang)) {
                        fpMultiplier = davidMultiplier[i];
                        break;
                    }
                }

            }

            SubComponentSizeDialog.this.changeMultiplierField.setText(fpMultiplier);
            subComponent.setMultiplier(Integer.parseInt(SubComponentSizeDialog.this.changeMultiplierField.getText()));

        } else if (e.getSource() == changeMultiplierButton) {
            new SubComponentMultiplierDialog(this, subComponent);
        } else if (e.getSource() == applyButton) {
            // Validations
            if (!GlobalMethods.isNonNegativeLong(newSLOCTextField.getText())) {
                //GlobalMethods.updateStatusBar("SLOC Must be a Non Negative Integer Value.", Color.RED, (COINCOMO) this.getParent());
                JOptionPane.showMessageDialog((COINCOMO) this.getParent(), "SLOC Must be a Non Negative Integer Value.", "SLOC ERROR", 0);
                return;
            }

            if (!GlobalMethods.isNonNegativeDouble(breakageTextField.getText())) {
                //GlobalMethods.updateStatusBar("Breakage Must be a Non Negative Float Value.", Color.RED, (COINCOMO) this.getParent());
                JOptionPane.showMessageDialog((COINCOMO) this.getParent(), "Breakage Must be a Non Negative Double Value.", "BREAKAGE ERROR", 0);
                return;
            } else if (Double.parseDouble(breakageTextField.getText()) > 100.0d) {
                //GlobalMethods.updateStatusBar("Breakage Must Less or Equal to 100.", Color.RED, (COINCOMO) this.getParent());
                JOptionPane.showMessageDialog((COINCOMO) this.getParent(), "Breakage Must Less or Equal to 100.", "BREAKAGE ERROR", 0);
                return;
            }

            // Function Points
            for (int i = 0; i < COINCOMOConstants.FTS.length-1; i++) {
                if (!GlobalMethods.isNonNegativeInt(inputTextFields[i].getText()) || !GlobalMethods.isNonNegativeInt(outputTextFields[i].getText())
                        || !GlobalMethods.isNonNegativeInt(fileTextFields[i].getText()) || !GlobalMethods.isNonNegativeInt(interfaceTextFields[i].getText())
                        || !GlobalMethods.isNonNegativeInt(queryTextFields[i].getText())) {
                    //GlobalMethods.updateStatusBar("All Function Points Must be Non-Negative Integers", Color.RED, (COINCOMO) this.getParent());
                    JOptionPane.showMessageDialog((COINCOMO) this.getParent(), "All Function Points Must be Non-Negative Integers", "FUNCTION POINTS ERROR", 0);
                    return;
                }
            }

            if (((Integer.parseInt(inputTextFields[0].getText()) > 0) && Integer.parseInt(inputTextFields[0].getText()) > Integer.parseInt(inputTextFields[1].getText()))
                    || ((Integer.parseInt(inputTextFields[2].getText()) > 0) && Integer.parseInt(inputTextFields[1].getText()) > Integer.parseInt(inputTextFields[2].getText()))) {
                //GlobalMethods.updateStatusBar("Input Function Point Average Must be between its Boundaries.", Color.RED, (COINCOMO) this.getParent());
                JOptionPane.showMessageDialog((COINCOMO) this.getParent(), "Input Function Point Average Must be between its Boundaries.", "INPUT FUNCTION POINT ERROR", 0);
                return;
            }

            if (((Integer.parseInt(outputTextFields[0].getText()) > 0) && Integer.parseInt(outputTextFields[0].getText()) > Integer.parseInt(outputTextFields[1].getText()))
                    || ((Integer.parseInt(outputTextFields[2].getText()) > 0) && Integer.parseInt(outputTextFields[1].getText()) > Integer.parseInt(outputTextFields[2].getText()))) {
                //GlobalMethods.updateStatusBar("Output Function Point Average Must be between its Boundaries.", Color.RED, (COINCOMO) this.getParent());
                JOptionPane.showMessageDialog((COINCOMO) this.getParent(), "Output Function Point Average Must be between its Boundaries.", "OUTPUT FUNCTION POINT ERROR", 0);
                return;
            }

            if (((Integer.parseInt(outputTextFields[0].getText()) > 0) && Integer.parseInt(outputTextFields[0].getText()) > Integer.parseInt(outputTextFields[1].getText()))
                    || ((Integer.parseInt(outputTextFields[2].getText()) > 0) && Integer.parseInt(outputTextFields[1].getText()) > Integer.parseInt(outputTextFields[2].getText()))) {
                //GlobalMethods.updateStatusBar("File Function Point Average Must be between its Boundaries.", Color.RED, (COINCOMO) this.getParent());
                JOptionPane.showMessageDialog((COINCOMO) this.getParent(), "File Function Point Average Must be between its Boundaries.", "FILE FUNCTION POINT ERROR", 0);
                return;
            }

            if (((Integer.parseInt(outputTextFields[0].getText()) > 0) && Integer.parseInt(outputTextFields[0].getText()) > Integer.parseInt(outputTextFields[1].getText()))
                    || ((Integer.parseInt(outputTextFields[2].getText()) > 0) && Integer.parseInt(outputTextFields[1].getText()) > Integer.parseInt(outputTextFields[2].getText()))) {
                //GlobalMethods.updateStatusBar("Interface Function Point Average Must be between its Boundaries.", Color.RED, (COINCOMO) this.getParent());
                JOptionPane.showMessageDialog((COINCOMO) this.getParent(), "Interface Function Point Average Must be between its Boundaries.", "INTERFACE FUNCTION POINT ERROR", 0);
                return;
            }

            if (((Integer.parseInt(outputTextFields[0].getText()) > 0) && Integer.parseInt(outputTextFields[0].getText()) > Integer.parseInt(outputTextFields[1].getText()))
                    || ((Integer.parseInt(outputTextFields[2].getText()) > 0) && Integer.parseInt(outputTextFields[1].getText()) > Integer.parseInt(outputTextFields[2].getText()))) {
                //GlobalMethods.updateStatusBar("Query Function Point Average Must be between its Boundaries.", Color.RED, (COINCOMO) this.getParent());
                JOptionPane.showMessageDialog((COINCOMO) this.getParent(), "Query Function Point Average Must be between its Boundaries.", " QUERY FUNCTION POINT ERROR", 0);
                return;
            }

            if (calculationMethodRadBtns[CalculationMethod.InputCalculatedFunctionPoints.ordinal()].isSelected() && !GlobalMethods.isNonNegativeInt(totalTextField.getText())) {
                JOptionPane.showMessageDialog((COINCOMO) this.getParent(), "Total Unadjusted Function Points Must be Non-Negative Integers", "TOTAL UNADJUSTED FUNCTION POINTS ERROR", 0);
                totalTextField.selectAll();
                return;
            }

            applyButton.setEnabled(false);
            //resetButton.setEnabled(false);
            closeButton.setEnabled(false);

            GlobalMethods.updateStatusBar("Saving ...", (COINCOMO) this.getParent());

            componentOverviewPanel.getEstimationTextPane().setText("Loading ...");

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    subComponent.setREVL(Double.parseDouble(breakageTextField.getText()));
                    subComponent.setLanguage(languagesComboBoxFP.getSelectedItem().toString());
                    
                    // New SLOC
                    subComponent.setNewSLOC(GlobalMethods.ParseLongWithComma(newSLOCTextField.getText()));

                    // Function Points
                    subComponent.setMultiplier(Integer.parseInt(SubComponentSizeDialog.this.changeMultiplierField.getText()));
                    subComponent.setExternalInputs(new int[]{Integer.parseInt(inputTextFields[0].getText()), Integer.parseInt(inputTextFields[1].getText()), Integer.parseInt(inputTextFields[2].getText())});
                    subComponent.setExternalOutputs(new int[]{Integer.parseInt(outputTextFields[0].getText()), Integer.parseInt(outputTextFields[1].getText()), Integer.parseInt(outputTextFields[2].getText())});
                    subComponent.setInternalLogicalFiles(new int[]{Integer.parseInt(fileTextFields[0].getText()), Integer.parseInt(fileTextFields[1].getText()), Integer.parseInt(fileTextFields[2].getText())});
                    subComponent.setExternalInterfaceFiles(new int[]{Integer.parseInt(interfaceTextFields[0].getText()), Integer.parseInt(interfaceTextFields[1].getText()), Integer.parseInt(interfaceTextFields[2].getText())});
                    subComponent.setExternalInquiries(new int[]{Integer.parseInt(queryTextFields[0].getText()), Integer.parseInt(queryTextFields[1].getText()), Integer.parseInt(queryTextFields[2].getText())});
                    subComponent.setRatioType(RatioType.valueOf(ratioTypeBtnGrp.getSelection().getActionCommand()));
                    subComponent.setCalculationMethod(CalculationMethod.getValueOf(calculationMethodBtnGrp.getSelection().getActionCommand()));

                    if (subComponent.getCalculationMethod() == CalculationMethod.InputCalculatedFunctionPoints) {
                        try {
                            totFunctionPoints = GlobalMethods.ParseIntWithComma(totalTextField.getText());
                            subComponent.setTotalUnadjustedFunctionPoints(totFunctionPoints);
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, "Enter Numbers Only", "Data Type Error", JOptionPane.ERROR_MESSAGE);
                        }

                    }

                    // Update Database ...
                    COINCOMOSubComponentManager.updateSubComponent(subComponent, true);

                    // Update Size
                    newSLOCTextField.setText(GlobalMethods.FormatLongWithComma(GlobalMethods.ParseLongWithComma(newSLOCTextField.getText())));

                    // Update Function Points
                    int[] subTotals = subComponent.getSubTotals();
                    inputResultsLabel.setText(GlobalMethods.FormatIntWithComma(subTotals[FP.EI.ordinal()]));
                    outputResultsLabel.setText(GlobalMethods.FormatIntWithComma(subTotals[FP.EO.ordinal()]));
                    fileResultsLabel.setText(GlobalMethods.FormatIntWithComma(subTotals[FP.ILF.ordinal()]));
                    interfaceResultsLabel.setText(GlobalMethods.FormatIntWithComma(subTotals[FP.EIF.ordinal()]));
                    queryResultsLabel.setText(GlobalMethods.FormatIntWithComma(subTotals[FP.EQ.ordinal()]));
                    totalTextField.setText(GlobalMethods.FormatLongWithComma(subComponent.getTotalUnadjustedFunctionPoints()));
                    eSLOCResultsLabel.setText(GlobalMethods.FormatLongWithComma(subComponent.getEquivalentSLOC()));
                    totalSLOCResultsLabel.setText(GlobalMethods.FormatLongWithComma(subComponent.getSumOfSLOCs()));

                    // Update Table ...
                    componentOverviewPanel.updateClefTable();

                    // Update Summary Report
                    SubComponentSizeDialog.this.componentOverviewPanel.updateEstimationTextPane(false);

                    // Set Back To Default ...
                    applyButton.setEnabled(true);
                    //resetButton.setEnabled(true);
                    closeButton.setEnabled(true);

                    //GlobalMethods.updateStatusBar( "Sub Component Saved." );
                }
            });
        } else if (e.getSource() == closeButton) {
            Iterator<COINCOMOUnit> subComponentsIterator = subComponent.getParent().getListOfSubUnits().iterator();
            long totalSLOC = 0;
            while (subComponentsIterator.hasNext()) {
                COINCOMOSubComponent sc = (COINCOMOSubComponent) subComponentsIterator.next();
                //Do we sum up the sloc without REVL or with REVL?
                totalSLOC += sc.getSumOfSLOCs();
            }
            if (totalSLOC < 2000) {
                JOptionPane.showMessageDialog((COINCOMO) this.getParent(), "This model is not calibrated for SLOC < 2000.", "SLOC < 2000", JOptionPane.WARNING_MESSAGE);
            }
            this.setVisible(false);
        } else if (e.getSource() == addAdaptation) {
            //for multiple adaptation and reuse
            COINCOMOAdaptationAndReuse adaptation = COINCOMOAdaptationAndReuseManager.insertAdaptationAndReuse(subComponent);

            COINCOMOVector<String> tableRowVector = new COINCOMOVector<String>();

            tableRowVector.add("false");
            tableRowVector.add(adaptation.getName());
            tableRowVector.add(GlobalMethods.FormatLongWithComma(adaptation.getEquivalentSLOC()));
            tableRowVector.add(GlobalMethods.FormatLongWithComma(adaptation.getAdaptedSLOC()));
            tableRowVector.add(format2Decimals.format(adaptation.getDesignModified()));
            tableRowVector.add(format2Decimals.format(adaptation.getCodeModified()));
            tableRowVector.add(format2Decimals.format(adaptation.getIntegrationModified()));
            tableRowVector.add(format2Decimals.format(adaptation.getSoftwareUnderstanding()));
            tableRowVector.add(format2Decimals.format(adaptation.getAssessmentAndAssimilation()));
            tableRowVector.add(format2Decimals.format(adaptation.getUnfamiliarityWithSoftware()));
            tableRowVector.add(format2Decimals.format(adaptation.getAutomaticallyTranslated()));
            tableRowVector.add(format2Decimals.format(adaptation.getAutomaticTranslationProductivity()));
            tableRowVector.setRowID(clefTable.getRowCount());

            // Add a New Row To Table ...
            clefTableModel.addRow(tableRowVector);

            //new SubComponentAdaptationReuseDialog(this,adap);

            // Find out the name column pointer for the newly added A&R entry on the clefTable and fire an mouse click event.
            final int newRow = clefTable.getRowCount() - 1;
            Rectangle arNameRect = clefTable.getCellRect(newRow, 1, true);

            final MouseEvent me = new MouseEvent(this, 0, System.currentTimeMillis(), 0, arNameRect.x, arNameRect.y, 1, false);
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

            // Scroll to the row so it is visible
            if (clefTable.getParent() instanceof JViewport) {
                JViewport viewport = (JViewport) clefTable.getParent();
                Point point = viewport.getViewPosition();
                arNameRect.setLocation(arNameRect.x - point.x, arNameRect.y - point.y);
                viewport.scrollRectToVisible(arNameRect);
            }
        } else if (e.getSource() == delAdaptation) {   //deleting a particular adaptation and reuse input
            // Get Selected Row's Index
            final ArrayList<Integer> selectedRowsIndexes = new ArrayList<Integer>();

            for (int i = 0; i < clefTable.getRowCount(); i++) {
                // If Selected ...
                if (clefTable.getValueAt(i, 0).equals("true")) {
                    // Add to the list of rows
                    selectedRowsIndexes.add(i);
                }
            }

            COINCOMO coincomo = (COINCOMO) this.getParent();
            // No Row Selected
            if (selectedRowsIndexes.isEmpty()) {
                //GlobalMethods.updateStatusBar("No Subcomponents were Selected.", Color.RED, coincomo);
                JOptionPane.showMessageDialog(coincomo, "No Subcomponent selected for delete", "DELETE ERROR", 0);

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
                this.delAdaptation.setEnabled(false);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ArrayList<COINCOMOAdaptationAndReuse> adaptationsToBeDeleted = new ArrayList<COINCOMOAdaptationAndReuse>();

                        // Delete Rows ...
                        for (int i = 0; i < selectedRowsIndexes.size(); i++) {
                            // get the Vector of Vectors ..
                            Vector<COINCOMOVector> vectorOfRows = (Vector<COINCOMOVector>) clefTableModel.getDataVector();

                            // Get Row Vector
                            COINCOMOVector selectedRowVector = (COINCOMOVector) vectorOfRows.get(selectedRowsIndexes.get(i) - i);

                            // Remove Internally ..
                            adaptationsToBeDeleted.add((COINCOMOAdaptationAndReuse) subComponent.getListOfSubUnits().get(selectedRowVector.getRowID()));

                            // Remove From Table
                            clefTableModel.removeRow(selectedRowsIndexes.get(i) - i);
                        }

                        // Remove From Database
                        if (adaptationsToBeDeleted != null) {
                            COINCOMOAdaptationAndReuseManager.deleteAdaptationAndReuses(adaptationsToBeDeleted);

                            // Update database
                            COINCOMOSubComponentManager.updateSubComponent(subComponent, true);
                        }

                        SubComponentSizeDialog.this.delAdaptation.setEnabled(true);
                    }
                });
            }
        } else {
            // Free Resources ... Close Window
            this.dispose();
        }
    }

    public void stateChanged(ChangeEvent e) {
        JTabbedPane pane = (JTabbedPane) e.getSource();

        if (pane.getSelectedIndex() == 2) {
            this.setSize(E_WIDTH, this.getSize().height);
            //clefTable.setPreferredSize(this.getSize());
            //clefTable.setSize(this.getSize());
        } else if (pane.getSelectedIndex() == 1) {
            this.setSize(D_WIDTH, this.getSize().height);
        } else {
            this.setSize(D_WIDTH, this.getSize().height);
        }
        //subComponent.setSizingType(pane.getSelectedIndex());
    }

    public void mouseClicked(MouseEvent e) {
        // translate point to row and column numbers
        final int rowNumber = clefTable.rowAtPoint(e.getPoint());
        final int columnNumber = clefTable.columnAtPoint(e.getPoint());

        Vector<COINCOMOVector> vectors = clefTableModel.getDataVector();

        // There is no A&R entry, so return.
        if (rowNumber == -1) {
            return;
        }

        COINCOMOVector clickedRow = vectors.get(rowNumber);

        final COINCOMOAdaptationAndReuse adap = (COINCOMOAdaptationAndReuse) subComponent.getListOfSubUnits().get(rowNumber);

        if (columnNumber == 0) {
            return;
        } else if (columnNumber == 1) {
            String result;
            if (tempName == null) {
                result = JOptionPane.showInputDialog(this, "Please enter the new name:", adap.getName());
            } else {
                result = JOptionPane.showInputDialog(this, "Please enter the new name:", tempName);
            }

            if (result == null) {
                return;
            }


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
            ArrayList<COINCOMOUnit> list = adap.getParent().getListOfSubUnits();
            for(int i = 0;i<list.size();i++) {
                if(list.get(i) != (COINCOMOUnit)adap && list.get(i).getName().equals(result)) {
                            JOptionPane.showMessageDialog(this, "Duplicated name in the same level" , "DUPLICATED NAME ERROR", 0);
                            tempName = result.toString();
                            result = null;
                             mouseClicked(e);
                            return;
                 }
             }
            
            
            // If Not Empty
            if (result != null && !result.trim().equals("")) {
                // Update Name of the Adaptation
                adap.setName(result);
                tempName = null;

                // Update the database
                COINCOMOAdaptationAndReuseManager.updateAdaptationAndReuseName(adap);

                // Update Name in the Table
                clefTable.setValueAt(adap.getName(), rowNumber, columnNumber);
            } else {
                JOptionPane.showMessageDialog(this, "Enter a valid string", "EMPTY STRING ERROR", 0);
                tempName = "";
                result = null;
                mouseClicked(e);
            }
        } else {
            new SubComponentAdaptationReuseDialog(this, adap);
        }

        clefTable.setValueAt(adap.getName(), rowNumber, 1);
        clefTable.setValueAt(GlobalMethods.FormatLongWithComma(adap.getEquivalentSLOC()), rowNumber, 2);
        clefTable.setValueAt(GlobalMethods.FormatLongWithComma(adap.getAdaptedSLOC()), rowNumber, 3);
        clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(adap.getDesignModified(), 2)), rowNumber, 4);
        clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(adap.getCodeModified(), 2)), rowNumber, 5);
        clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(adap.getIntegrationModified(), 2)), rowNumber, 6);
        clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(adap.getSoftwareUnderstanding(), 2)), rowNumber, 7);
        clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(adap.getAssessmentAndAssimilation(), 2)), rowNumber, 8);
        clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(adap.getUnfamiliarityWithSoftware(), 2)), rowNumber, 9);
        clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(adap.getAutomaticallyTranslated(), 2)), rowNumber, 10);
        clefTable.setValueAt(format2Decimals.format(GlobalMethods.roundOff(adap.getAutomaticTranslationProductivity(), 2)), rowNumber, 11);
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    protected void Apply() {
        applyButton.doClick();
    }

    protected String getCurrentFPLanaguage() {
        String fpLang = languagesComboBoxFP.getSelectedItem().toString();

        return fpLang;
    }

    protected String getCurrentFPMultiplier() {
        String fpMultiplier = changeMultiplierField.getText();

        return fpMultiplier;
    }

    protected void setCurrentFPMultiplier(String fpMultiplier) {
        changeMultiplierField.setText(fpMultiplier);
    }

    protected String getDefaultLanguageMultiplier(String fpLang) {
        String fpMultiplier = "0";

        for (int i = 0; i < languagesComboBoxFP.getItemCount(); i++) {
            if (ratioTypeRadBtns[RatioType.Jones.ordinal()].isSelected()) {
                if (jonesLanguages[i].equalsIgnoreCase(fpLang)) {
                    fpMultiplier = jonesMultiplier[i];
                    break;
                }

            } else {
                if (i > 26) {
                    break;
                }
                if (davidLanguages[i].equalsIgnoreCase(fpLang)) {
                    fpMultiplier = davidMultiplier[i];
                    break;
                }
            }
        }

        return fpMultiplier;
    }
}