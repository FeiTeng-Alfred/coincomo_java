/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package main;

import core.COINCOMOComponent;
import core.COINCOMOConstants;
import core.COINCOMOConstants.CoincomoMode;
import core.COINCOMOConstants.DatabaseType;
import core.COINCOMOConstants.OperationMode;
import core.COINCOMOLocalCalibration;
import core.COINCOMOSubComponent;
import core.COINCOMOSystem;
import core.COINCOMOUnit;
import extensions.COINCOMOTreeNode;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import panels.ComponentOverviewPanel;
import panels.HierarchyPanel;
import panels.OverviewsAndGraphsPanel;

/**
 *
 * @author Raed Shomali
 */
public class COINCOMO extends JFrame implements ActionListener {

    public final static CoincomoMode DEFAULT_COINCOMO_MODE = CoincomoMode.UNIFIED;
    public final static OperationMode DEFAULT_OPERATION_MODE = OperationMode.DATABASE;
    public final static DatabaseType DEFAULT_DATABASE_TYPE = DatabaseType.PostgreSQL;

    public final static String DEFAULT_COINCOMO_PROPERTIES_FILE = "COINCOMO.properties";
    public final static String DEFAULT_COINCOMO_MODE_KEY = "coincomo_mode";
    public final static String EXTENSION = "cet";

    public static COINCOMOLocalCalibration localCalibration = new COINCOMOLocalCalibration();

    public static Color defaultColor = null;

    // Frame Size
    final Dimension DEFAULT_SIZE = new Dimension(800, 600);
    // Frame Location
    final Point DEFAULT_LOCATION = new Point(100, 100);
    // Application
    //public static COINCOMO application = null;
    // Container
    private Container container = getContentPane();
    // Status Bar Label
    private JLabel statusBar = new JLabel("Done.");
    // Split Pane
    private JSplitPane splitPane = new JSplitPane();
    // Menus
    private JMenu fileMenu = new JMenu("File");
    private JMenu parametersMenu = new JMenu("Parameters");
    private JMenu calibrateMenu = new JMenu("Calibrate");
    private JMenu modeMenu = new JMenu("Mode");
    private JMenu groupMenu = new JMenu("Group");
    private JMenu helpMenu = new JMenu("Help");
    // File Menu Items
    private JMenuItem connectDatabaseMenuItem = new JMenuItem("Connect...");
    private JMenuItem disconnectDatabaseMenuItem = new JMenuItem("Disconnect...");
    private JMenuItem SignupMenuItem = new JMenuItem("Sign up...");
    private JMenuItem LoginMenuItem = new JMenuItem("Log in...");
    private JMenuItem LogoutMenuItem = new JMenuItem("Log out...");
    private JMenuItem viewProjectsMenuItem = new JMenuItem("View Projects...");
    private JMenuItem importProjectMenuItem = new JMenuItem("Import Project...");
    private JMenuItem synchronizeProjectMenuItem = new JMenuItem("Sync Project...");
    private JMenuItem openProjectMenuItem = new JMenuItem("Open Project...");
    private JMenuItem closeProjectMenuItem = new JMenuItem("Close Project...");
    private JMenuItem saveProjectMenuItem = new JMenuItem("Save Project...");
    private JMenuItem saveProjectAsMenuItem = new JMenuItem("Save Project As...");
    private JMenuItem newProjectMenuItem = new JMenuItem("New Project...");
    private JMenuItem importCSVMenuItem = new JMenuItem("Import CSV...");
    private JMenu exportProjectMenu = new JMenu("Export");
    private JMenuItem exportCVSProjectMenuItem = new JMenuItem("Export As CSV...");
    private JMenuItem exportHTMLProjectMenuItem = new JMenuItem("Export As HTML...");
    private JMenuItem exportXMLProjectMenuItem = new JMenuItem("Export As XML...");
    private JMenuItem exitMenuItem = new JMenuItem("Exit");
    // Parameters Menu Items
    private JMenuItem effortAdjustmentFactorsMenuItem = new JMenuItem("Effort Adjustment Factors");
    private JMenuItem scaleFactorsMenuItem = new JMenuItem("Scale Factors");
    private JMenuItem equationEditorMenuItem = new JMenuItem("Equation Editor");
    private JMenuItem functionPointsMenuItem = new JMenuItem("Function Points");
    private JMenuItem personMonthMenuItem = new JMenuItem("Person Month");
    //Calibrate Menu Items
    private JMenuItem loadCalibrationMenuItem = new JMenuItem("Load Calibration");
    private JMenuItem saveCalibrationMenuItem = new JMenuItem("Save Calibration");
    private JMenuItem saveCalibrationAsMenuItem = new JMenuItem("Save Calibration As");
    private JMenuItem projectMenuItem = new JMenuItem("Project");
    private JMenuItem computeMenuItem = new JMenuItem("Compute");
    // Database/Desktop Mode Menu Items
    private ButtonGroup modeGroup = new ButtonGroup();
    private JRadioButtonMenuItem desktopMenuItem = new JRadioButtonMenuItem("Desktop");
    private JRadioButtonMenuItem databaseMenuItem = new JRadioButtonMenuItem("Database");
    //Group Menu Item
    private JMenuItem assigngroupMenuItem = new JMenuItem("Assign Group");
    // Help Menu Item
    private JMenuItem aboutUsMenuItem = new JMenuItem("About Us...");
    private JMenuItem manualMenuItem = new JMenuItem("User Manual");
    // Menu Bar that contains all Menus
    private JMenuBar menuBar = new JMenuBar();
    private HierarchyPanel hierarchyPanel = null;
    private OverviewsAndGraphsPanel overviewsAndGraphsPanel = null;
    private COINCOMOUnit currentCOINCOMOUnit = null;

    private MenuItemMethods menuItemMethods = null;
    // Global Sub Component copy buffer
    private COINCOMOSubComponent subComponentBuffer = null;

    private static Properties properties = null;
    private static CoincomoMode coincomoMode = DEFAULT_COINCOMO_MODE;
    private static OperationMode operationMode = DEFAULT_OPERATION_MODE;
    private static boolean isConnected = false;
    private static boolean isLogedin = false;
    private static boolean ignoreDatabaseMode = false;
    private static DatabaseType databaseType = DEFAULT_DATABASE_TYPE;
    
    public COINCOMO() {
        // Read in the configuration properties file and set COINCOMO mode
        String defaultMode = null;

        if (properties == null) {
            properties = new Properties();
            final String programDir = System.getProperty("user.dir");
            final String propertiesPath = programDir + File.separator + DEFAULT_COINCOMO_PROPERTIES_FILE;
            File propertiesFile = new File(propertiesPath);

            try {
                FileReader propertiesReader = new FileReader(propertiesFile);
                properties.load(propertiesReader);
                defaultMode = properties.getProperty(DEFAULT_COINCOMO_MODE_KEY);
            } catch (IOException e) {
                log(Level.WARNING, "No properties file found for determing coincomoMode. Set to default mode.");
                properties.setProperty(DEFAULT_COINCOMO_MODE_KEY, CoincomoMode.UNIFIED.toString());
            }
        }

        if (CoincomoMode.UNIFIED.toString().equals(defaultMode)) {
            coincomoMode = CoincomoMode.UNIFIED;
        } else if (CoincomoMode.DESKTOP_ONLY.toString().equals(defaultMode)) {
            coincomoMode = CoincomoMode.DESKTOP_ONLY;
            operationMode = OperationMode.DESKTOP;
        } else if (CoincomoMode.DATABASE_ONLY.toString().equals(defaultMode)) {
            coincomoMode = CoincomoMode.DATABASE_ONLY;
            operationMode = OperationMode.DATABASE;
        } else {
            log(Level.WARNING, "No matching key and string in the properties file for determing coincomoMode. Set to default mode.");
            coincomoMode = DEFAULT_COINCOMO_MODE;
        }

        this.setTitle(COINCOMOConstants.COINCOMO_TITLE);
        
        BufferedImage coincomoImage = new BufferedImage(Icons.COINCOMO_PROGRAM_ICON.getIconWidth(),
                                                        Icons.COINCOMO_PROGRAM_ICON.getIconHeight(),
                                                        BufferedImage.TYPE_INT_RGB);
        Graphics graphics = coincomoImage.createGraphics();
        Icons.COINCOMO_PROGRAM_ICON.paintIcon(null, graphics, 0, 0);
        graphics.dispose();
        this.setIconImage(coincomoImage);

        overviewsAndGraphsPanel = new OverviewsAndGraphsPanel(this);
        hierarchyPanel = new HierarchyPanel(this);
        menuItemMethods = new MenuItemMethods(this);

        // ----------------------------------------------------------
        // Label

        // ----------------------------------------------------------
        // Status Bar
        statusBar.setFont(new Font("courier", 1, 12));
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        COINCOMO.defaultColor = statusBar.getForeground();

        // ----------------------------------------------------------
        // Tree

        // ----------------------------------------------------------
        // Split Pane
        splitPane.setDividerLocation(150);
        splitPane.setLeftComponent(this.hierarchyPanel);
        splitPane.setRightComponent(this.overviewsAndGraphsPanel);

        // Refresh Automatically As the Divider is Moving
        splitPane.setContinuousLayout(true);

        // ----------------------------------------------------------
        // Adding Action Listeners To Menu Items
        connectDatabaseMenuItem.addActionListener(this);
        disconnectDatabaseMenuItem.addActionListener(this);
        SignupMenuItem.addActionListener(this);
        LoginMenuItem.addActionListener(this);
        LogoutMenuItem.addActionListener(this);
        viewProjectsMenuItem.addActionListener(this);
        importProjectMenuItem.addActionListener(this);
        synchronizeProjectMenuItem.addActionListener(this);
        openProjectMenuItem.addActionListener(this);
        closeProjectMenuItem.addActionListener(this);
        saveProjectMenuItem.addActionListener(this);
        saveProjectAsMenuItem.addActionListener(this);
        newProjectMenuItem.addActionListener(this);
        importCSVMenuItem.addActionListener(this);
        exportCVSProjectMenuItem.addActionListener(this);
        exportHTMLProjectMenuItem.addActionListener(this);
        exportXMLProjectMenuItem.addActionListener(this);
        exitMenuItem.addActionListener(this);

        effortAdjustmentFactorsMenuItem.addActionListener(this);
        scaleFactorsMenuItem.addActionListener(this);
        equationEditorMenuItem.addActionListener(this);
        functionPointsMenuItem.addActionListener(this);
        personMonthMenuItem.addActionListener(this);

        loadCalibrationMenuItem.addActionListener(this);
        saveCalibrationMenuItem.addActionListener(this);
        saveCalibrationAsMenuItem.addActionListener(this);
        projectMenuItem.addActionListener(this);
        computeMenuItem.addActionListener(this);

        desktopMenuItem.addActionListener(this);
        databaseMenuItem.addActionListener(this);
        
        assigngroupMenuItem.addActionListener(this);

        aboutUsMenuItem.addActionListener(this);
        manualMenuItem.addActionListener(this);

        // Adding Mnemonics, Icons and Keystrokes to Menu Items
        fileMenu.setMnemonic('F');
        // Case DESKTOP:
        openProjectMenuItem.setMnemonic('O');
        openProjectMenuItem.setIcon(Icons.OPEN_ICON);
        openProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        saveProjectMenuItem.setMnemonic('S');
        saveProjectMenuItem.setIcon(Icons.SAVE_ICON);
        saveProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        saveProjectAsMenuItem.setMnemonic('A');
        saveProjectAsMenuItem.setIcon(Icons.SAVE_AS_ICON);
        saveProjectAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.SHIFT_MASK + ActionEvent.ALT_MASK));
        // Case DATABASE:
        connectDatabaseMenuItem.setMnemonic('T');
        connectDatabaseMenuItem.setIcon(Icons.CONNECT_ICON);
        connectDatabaseMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        disconnectDatabaseMenuItem.setMnemonic('D');
        disconnectDatabaseMenuItem.setIcon(Icons.DISCONNECT_ICON);
        disconnectDatabaseMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        SignupMenuItem.setIcon(Icons.OPEN_ICON);
        LoginMenuItem.setIcon(Icons.IMPORT_ICON);
        LogoutMenuItem.setIcon(Icons.CLOSE_ICON);
        importProjectMenuItem.setMnemonic('I');
        importProjectMenuItem.setIcon(Icons.IMPORT_ICON);
        importProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        //synchronizeProjectMenuItem.setMnemonic('I');
        synchronizeProjectMenuItem.setIcon(Icons.SYNCHRONIZE_ICON);
        //synchronizeProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        viewProjectsMenuItem.setMnemonic('W');
        viewProjectsMenuItem.setIcon(Icons.VIEW_ICON);
        viewProjectsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        closeProjectMenuItem.setMnemonic('C');
        closeProjectMenuItem.setIcon(Icons.CLOSE_ICON);
        closeProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.SHIFT_MASK + ActionEvent.ALT_MASK));
        newProjectMenuItem.setMnemonic('N');
        //newProjectMenuItem.setIcon(Icons.NEW_ICON);
        newProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        exportProjectMenu.setMnemonic('E');
        exportProjectMenu.setIcon(Icons.EXPORT_ICON);
        exportCVSProjectMenuItem.setMnemonic('V');
        exportCVSProjectMenuItem.setIcon(Icons.EXPORT_CVS_ICON);
        exportCVSProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.SHIFT_MASK + ActionEvent.ALT_MASK));
        exportHTMLProjectMenuItem.setMnemonic('H');
        exportHTMLProjectMenuItem.setIcon(Icons.EXPORT_HTML_ICON);
        exportHTMLProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.SHIFT_MASK + ActionEvent.ALT_MASK));
        exportXMLProjectMenuItem.setMnemonic('X');
        exportXMLProjectMenuItem.setIcon(Icons.EXPORT_XML_ICON);
        exportXMLProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.SHIFT_MASK + ActionEvent.ALT_MASK));
        exitMenuItem.setMnemonic('X');
        exitMenuItem.setIcon(Icons.EXIT_ICON);
        if (System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
            exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        } else {
            exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
        }

        parametersMenu.setMnemonic('P');
        effortAdjustmentFactorsMenuItem.setMnemonic('E');
        effortAdjustmentFactorsMenuItem.setIcon(Icons.EAF_ICON);
        effortAdjustmentFactorsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        scaleFactorsMenuItem.setMnemonic('S');
        scaleFactorsMenuItem.setIcon(Icons.SCALE_FACTOR_ICON);
        scaleFactorsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        equationEditorMenuItem.setMnemonic('Q');
        equationEditorMenuItem.setIcon(Icons.EQUATION_EDITOR_ICON);
        equationEditorMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
        functionPointsMenuItem.setMnemonic('F');
        functionPointsMenuItem.setIcon(Icons.FUNCTION_POINTS_ICON);
        functionPointsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        personMonthMenuItem.setMnemonic('M');
        personMonthMenuItem.setIcon(Icons.PERSON_MONTH_ICON);
        personMonthMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));

        calibrateMenu.setMnemonic('B');

        modeMenu.setMnemonic('M');
        desktopMenuItem.setIcon(Icons.MODE_ICON_DESKTOP);
        databaseMenuItem.setIcon(Icons.MODE_ICON_DATABASE);
        
        groupMenu.setMnemonic('G');
        assigngroupMenuItem.setIcon(Icons.ADD_SUBSYSTEM_ICON);
        
        helpMenu.setMnemonic('H');
        aboutUsMenuItem.setMnemonic('A');
        aboutUsMenuItem.setIcon(Icons.ABOUT_ICON);
        aboutUsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        manualMenuItem.setMnemonic('M');
        manualMenuItem.setIcon(Icons.MANUAL_ICON);
        manualMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, ActionEvent.ALT_MASK));

        // Add Each Menu Item To Corresponding Menu
        // Menu items under File menu, except the Export Project sub menu, are to be add/set/remove by RestMenuBar() function.
        exportProjectMenu.add(exportCVSProjectMenuItem);
        exportProjectMenu.add(exportHTMLProjectMenuItem);
        exportProjectMenu.add(exportXMLProjectMenuItem);

        parametersMenu.add(effortAdjustmentFactorsMenuItem);
        parametersMenu.add(scaleFactorsMenuItem);
        parametersMenu.add(equationEditorMenuItem);
        parametersMenu.add(functionPointsMenuItem);
        parametersMenu.add(personMonthMenuItem);

//        calibrateMenu.add(loadCalibrationMenuItem);
//        calibrateMenu.add(saveCalibrationMenuItem);
//        calibrateMenu.add(saveCalibrationAsMenuItem);
//        calibrateMenu.add(projectMenuItem);
//        calibrateMenu.add(computeMenuItem);

        modeGroup.add(desktopMenuItem);
        modeMenu.add(desktopMenuItem);
        modeGroup.add(databaseMenuItem);
        modeMenu.add(databaseMenuItem);

        groupMenu.add(assigngroupMenuItem);
        
        helpMenu.add(aboutUsMenuItem);
        helpMenu.add(manualMenuItem);

        // Finally Adding the Menus to the Menu Bar
        menuBar.add(fileMenu);
        menuBar.add(parametersMenu);
//        menuBar.add(calibrateMenu);
        if (coincomoMode == CoincomoMode.UNIFIED) {
            menuBar.add(modeMenu);
        }
        menuBar.add(groupMenu);
        menuBar.add(helpMenu);


        // -------------------------------------------------------------
        // Container
        container.setLayout(new BorderLayout());

        container.add(splitPane);
        container.add(statusBar, BorderLayout.SOUTH);

        // Enable the "Close Button" and Release Memory When Clicked
        this.setDefaultCloseOperation(COINCOMO.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                menuItemMethods.exit();
            }
        });

        // In order not to Hide the Task bar when the Frame is Maximized
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.setMaximizedBounds(env.getMaximumWindowBounds());

        // Add The Menus to the Frame
        this.setJMenuBar(menuBar);
        this.setLocation(DEFAULT_LOCATION);

        // Set The Size and Show the Frame
        this.setMinimumSize(DEFAULT_SIZE);
        this.setSize(DEFAULT_SIZE);
        this.setVisible(true);

        this.resetMenuBar();

        // Open the connect dialog by default if COINCOMO is in database mode
        if (operationMode == OperationMode.DATABASE && !isConnected) {
            menuItemMethods.connectToDatabase();
        }
    }

    public COINCOMO(File f) {
        this();
        this.menuItemMethods.openExistingProject(f);
    }

    public JLabel getStatusBar() {
        return this.statusBar;
    }

    /*public HierarchyPanel getHierarchyPanel()
     {
     return this.hierarchyPanel;
     }*/
    /*public OverviewsAndGraphsPanel getOverviewsAndGraphsPanel()
     {
     return this.overviewsAndGraphsPanel;
     }*/
    public COINCOMOSystem getCurrentSystem() {
        COINCOMOTreeNode root = this.hierarchyPanel.getCOINCOMOTreeRoot();
        if (root != null) {
            return (COINCOMOSystem) root.getCOINCOMOUnit();
        }
        return null;
    }

    public void refresh() {
        if (this.overviewsAndGraphsPanel.getOverviewPanel() instanceof ComponentOverviewPanel) {
            ComponentOverviewPanel cop = (ComponentOverviewPanel) this.overviewsAndGraphsPanel.getOverviewPanel();
            cop.updateEstimationTextPane(true);
        }
        if (this.overviewsAndGraphsPanel.getCOPSEMOPanel().getComponent() != null) {
            overviewsAndGraphsPanel.updateOverviewTabWith(this.overviewsAndGraphsPanel.getCOPSEMOPanel().getComponent());
            this.overviewsAndGraphsPanel.getCOPSEMOPanel().updateCOPSEMO();
        }
    }

    public void clearCurrentSystem() {
        final COINCOMOSystem system = getCurrentSystem();
        MenuItemMethods.removeActiveSystem(system);
        hierarchyPanel.clearHierarchyTree();
        overviewsAndGraphsPanel.clearOverviewTab();
        
        switch (operationMode) {
            case DESKTOP:
                saveProjectMenuItem.setEnabled(false);
                saveProjectAsMenuItem.setEnabled(false);
                break;
            case DATABASE:
                saveProjectAsMenuItem.setEnabled(false);
                break;
        }

        closeProjectMenuItem.setEnabled(false);
        exportProjectMenu.setEnabled(false);
        exportCVSProjectMenuItem.setEnabled(false);
        exportHTMLProjectMenuItem.setEnabled(false);
        exportXMLProjectMenuItem.setEnabled(false);
    }

    public void setCurrentSystem(COINCOMOSystem system) {
        MenuItemMethods.addActiveSystem(this, system);
        hierarchyPanel.makeHierarchyTree(system);
        overviewsAndGraphsPanel.updateOverviewTabWith(system);

        switch (operationMode) {
            case DESKTOP:
                saveProjectMenuItem.setEnabled(true);
                saveProjectAsMenuItem.setEnabled(true);
                break;
            case DATABASE:
                saveProjectAsMenuItem.setEnabled(true);
                break;
        }

        closeProjectMenuItem.setEnabled(true);
        exportProjectMenu.setEnabled(true);
        exportCVSProjectMenuItem.setEnabled(true);
        exportHTMLProjectMenuItem.setEnabled(true);
        exportXMLProjectMenuItem.setEnabled(true);
    }

    public COINCOMOUnit getCurrentUnit() {
        return this.currentCOINCOMOUnit;
    }

    public void setCurrentUnit(COINCOMOUnit unit) {
        this.currentCOINCOMOUnit = unit;
        
        overviewsAndGraphsPanel.updateOverviewTabWith(unit);

        if (unit instanceof COINCOMOComponent) {
            parametersMenu.setEnabled(true);
            effortAdjustmentFactorsMenuItem.setEnabled(true);
            scaleFactorsMenuItem.setEnabled(true);
            equationEditorMenuItem.setEnabled(true);
            functionPointsMenuItem.setEnabled(true);
            personMonthMenuItem.setEnabled(true);
            
            computeMenuItem.setEnabled(true);
        } else {
            parametersMenu.setEnabled(false);
            effortAdjustmentFactorsMenuItem.setEnabled(false);
            scaleFactorsMenuItem.setEnabled(false);
            equationEditorMenuItem.setEnabled(false);
            functionPointsMenuItem.setEnabled(false);
            personMonthMenuItem.setEnabled(false);
            
            computeMenuItem.setEnabled(false);
        }
    }

    public void setSubComponentBuffer(COINCOMOSubComponent subComponent) {
        this.subComponentBuffer = null;
        this.subComponentBuffer = subComponent;
    }

    public COINCOMOSubComponent getSubComponentBuffer() {
        return this.subComponentBuffer;
    }

    public void clearSubComponentBuffer() {
        this.subComponentBuffer = null;
    }

    public void renameNewProject() {
        this.hierarchyPanel.renameNewProject();
    }

    public HierarchyPanel getHierachyPanel() {
        return this.hierarchyPanel;
    }

    public OverviewsAndGraphsPanel getOverviewsAndGraphcsPanel() {
        return this.overviewsAndGraphsPanel;
    }

    public static OperationMode getOperationMode() {
        return operationMode;
    }

    public static boolean getIgnoreDatabaseMode() {
        return ignoreDatabaseMode;
    }

    public static boolean isConnected() {
        return isConnected;
    }

    public static void setConnected() {
        isConnected = true;
    }

    public static void setDisconnected() {
        isConnected = false;
    }
    
    public static boolean isLogedin() {
        return isLogedin;
    }

    public static void setLogedin() {
        isLogedin = true;
    }

    public static void setLogedout() {
        isLogedin = false;
    }
    

    public static void setDatabaseType(DatabaseType databaseType) {
        COINCOMO.databaseType = databaseType;
    }

    public void resetMenuBar() {
        fileMenu.removeAll();

        switch (operationMode) {
            case DESKTOP:
                fileMenu.add(openProjectMenuItem);
                fileMenu.add(saveProjectMenuItem);
                fileMenu.add(saveProjectAsMenuItem);
                fileMenu.add(closeProjectMenuItem);
                newProjectMenuItem.setIcon(Icons.NEW_ICON_DESKTOP);
                break;
            case DATABASE:
                fileMenu.add(connectDatabaseMenuItem);
                fileMenu.add(disconnectDatabaseMenuItem);
                fileMenu.addSeparator();
                fileMenu.add(SignupMenuItem);
                fileMenu.add(LoginMenuItem);
                fileMenu.add(LogoutMenuItem);
                fileMenu.addSeparator();
                fileMenu.add(saveProjectAsMenuItem);
                fileMenu.add(closeProjectMenuItem);
                fileMenu.addSeparator();
                fileMenu.add(viewProjectsMenuItem);
                fileMenu.add(importProjectMenuItem);
                fileMenu.add(synchronizeProjectMenuItem);
                newProjectMenuItem.setIcon(Icons.NEW_ICON_DATABASE);
                break;
        }
        fileMenu.addSeparator();
        fileMenu.add(newProjectMenuItem);
        fileMenu.addSeparator();
//        fileMenu.add(importCSVMenuItem);
//        fileMenu.addSeparator();
        fileMenu.add(exportProjectMenu);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        switch (operationMode) {
            case DESKTOP:
                openProjectMenuItem.setEnabled(true);
                saveProjectMenuItem.setEnabled(false);
                saveProjectAsMenuItem.setEnabled(false);
                newProjectMenuItem.setEnabled(true);
                break;
            case DATABASE:
                if (isConnected) {
                    connectDatabaseMenuItem.setEnabled(false);
                    disconnectDatabaseMenuItem.setEnabled(true);
                    groupMenu.setEnabled(true);
                    if(!isLogedin)
                    {
                        SignupMenuItem.setEnabled(true);
                        LoginMenuItem.setEnabled(true);
                        LogoutMenuItem.setEnabled(false);
                        saveProjectAsMenuItem.setEnabled(false);
                        viewProjectsMenuItem.setEnabled(false);
                        importProjectMenuItem.setEnabled(false);
                        synchronizeProjectMenuItem.setEnabled(false);
                        newProjectMenuItem.setEnabled(false);  
                        assigngroupMenuItem.setEnabled(false);
                    } 
                    else
                    {
                        SignupMenuItem.setEnabled(false);
                        LoginMenuItem.setEnabled(false);
                        LogoutMenuItem.setEnabled(true);
                        saveProjectAsMenuItem.setEnabled(true);
                        viewProjectsMenuItem.setEnabled(true);
                        importProjectMenuItem.setEnabled(true);
                        synchronizeProjectMenuItem.setEnabled(true);
                        newProjectMenuItem.setEnabled(true);
                        
                        assigngroupMenuItem.setEnabled(true);
                    }
                    
                } else {
                    connectDatabaseMenuItem.setEnabled(true);
                    disconnectDatabaseMenuItem.setEnabled(false);
                    SignupMenuItem.setEnabled(false);
                    LoginMenuItem.setEnabled(false);
                    LogoutMenuItem.setEnabled(false);
                    saveProjectAsMenuItem.setEnabled(false);
                    viewProjectsMenuItem.setEnabled(false);
                    importProjectMenuItem.setEnabled(false);
                    synchronizeProjectMenuItem.setEnabled(false);
                    newProjectMenuItem.setEnabled(false);
                    groupMenu.setEnabled(false);
                }
                break;
        }

        closeProjectMenuItem.setEnabled(false);
        exportProjectMenu.setEnabled(false);
        exportCVSProjectMenuItem.setEnabled(false);
        exportHTMLProjectMenuItem.setEnabled(false);
        exportXMLProjectMenuItem.setEnabled(false);

        parametersMenu.setEnabled(false);
        effortAdjustmentFactorsMenuItem.setEnabled(false);
        scaleFactorsMenuItem.setEnabled(false);
        equationEditorMenuItem.setEnabled(false);
        functionPointsMenuItem.setEnabled(false);
        personMonthMenuItem.setEnabled(false);

//        loadCalibrationMenuItem.setEnabled(true);
//        saveCalibrationMenuItem.setEnabled(true);
//        saveCalibrationAsMenuItem.setEnabled(true);
//        projectMenuItem.setEnabled(true);
//        computeMenuItem.setEnabled(true);
//        computeMenuItem.setEnabled(false);
        
        switch (operationMode) {
            case DESKTOP:
                desktopMenuItem.setSelected(true);
                break;
            case DATABASE:
                databaseMenuItem.setSelected(true);
                break;
        }
    }

    public void setOperationMode(OperationMode operationMode) {
        COINCOMO.operationMode = operationMode;

        switch (operationMode) {
            case DESKTOP:
                desktopMenuItem.setSelected(true);
                GlobalMethods.updateStatusBar("Switched to Desktop mode.", this);
                break;
            case DATABASE:
                databaseMenuItem.setSelected(true);
                GlobalMethods.updateStatusBar("Switched to Database mode.", this);
                break;
        }

        resetMenuBar();
    }

    public static void setIgnoreDatabaseMode(boolean ignoreDatabaseMode) {
        COINCOMO.ignoreDatabaseMode = ignoreDatabaseMode;
    }

    public void setIgnoreDatabasMode(boolean ignoreDatabaseMode) {
        COINCOMO.ignoreDatabaseMode = ignoreDatabaseMode;
    }

    public boolean exit() {
        return menuItemMethods.exit();
    }

    public boolean closeProject() {
        return menuItemMethods.closeProject();
    }

    public boolean closeProject(COINCOMOSystem system) {
        return menuItemMethods.closeProject(system);
    }

    public void updateTitle() {
        COINCOMOSystem system = getCurrentSystem();
        if (system == null) {
            setTitle(COINCOMOConstants.COINCOMO_TITLE);
        } else {
            setTitle(COINCOMOConstants.COINCOMO_TITLE + " - " + system.getName());
        }
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        //GlobalMethods.updateStatusBar("Loading ...", this);

        // Create a Thread ...
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (e.getSource() == connectDatabaseMenuItem) {
                    menuItemMethods.connectToDatabase();
                } else if (e.getSource() == disconnectDatabaseMenuItem) {
                    menuItemMethods.disconnectFromDatabase(false);
                } else if(e.getSource() == SignupMenuItem){
                    menuItemMethods.signup();
                } else if(e.getSource() == LoginMenuItem){
                    menuItemMethods.login();
                } else if(e.getSource() == LogoutMenuItem){
                    menuItemMethods.logout();
                } else if (e.getSource() == importProjectMenuItem) {
                    menuItemMethods.importProject();
                } else if (e.getSource() == synchronizeProjectMenuItem) {
                    menuItemMethods.synchronizeProject();
                } else if (e.getSource() == viewProjectsMenuItem) {
                    menuItemMethods.ViewProjects();
                } else if (e.getSource() == openProjectMenuItem) {
                    menuItemMethods.openExistingProject();
                } else if (e.getSource() == saveProjectMenuItem) {
                    menuItemMethods.saveProject();
                } else if (e.getSource() == saveProjectAsMenuItem) {
                    menuItemMethods.saveProjectAs();
                } else if (e.getSource() == closeProjectMenuItem) {
                    menuItemMethods.closeProject();
                } else if (e.getSource() == newProjectMenuItem) {
                    menuItemMethods.createProject(null);
//                } else if (e.getSource() == importCSVMenuItem) {
//                    devImport.menuImportItemMethod();
                } else if (e.getSource() == exportCVSProjectMenuItem) {
                    menuItemMethods.exportProjectAsCSV();
                } else if (e.getSource() == exportHTMLProjectMenuItem) {
                    menuItemMethods.exportProjectAsHTML();
                } else if (e.getSource() == exportXMLProjectMenuItem) {
                    menuItemMethods.exportProjectAsXML();
                } else if (e.getSource() == exitMenuItem) {
                    menuItemMethods.exit();
                } else if (e.getSource() == effortAdjustmentFactorsMenuItem) {
                    menuItemMethods.showEAFDialog();
                } else if (e.getSource() == scaleFactorsMenuItem) {
                    menuItemMethods.showScaleFactorsDialog();
                } else if (e.getSource() == equationEditorMenuItem) {
                    menuItemMethods.showEquationEditorDialog();
                } else if (e.getSource() == functionPointsMenuItem) {
                    menuItemMethods.showFunctionPointsDialog();
                } else if (e.getSource() == personMonthMenuItem) {
                    menuItemMethods.showPersonMonthDialog();
                } else if (e.getSource() == assigngroupMenuItem) {
                    menuItemMethods.assigngroup();
                }else if (e.getSource() == aboutUsMenuItem) {
                    menuItemMethods.showAboutUsDialog();
                } else if (e.getSource() == manualMenuItem) {
                    menuItemMethods.showManual();
//                } else if (e.getSource() == loadCalibrationMenuItem) {
//                    menuItemMethods.loadCalibration();
//                } else if (e.getSource() == saveCalibrationMenuItem) {
//                    menuItemMethods.saveCalibration();
//                } else if (e.getSource() == saveCalibrationAsMenuItem) {
//                    throw new UnsupportedOperationException();
//                } else if (e.getSource() == projectMenuItem) {
//                    menuItemMethods.project();
//                } else if (e.getSource() == computeMenuItem) {
//                    menuItemMethods.compute();
                } else if (e.getSource() == desktopMenuItem) {
                    if (menuItemMethods.switchMode(OperationMode.DESKTOP)) {
                        setOperationMode(OperationMode.DESKTOP);
                    } else {
                        databaseMenuItem.setSelected(true);
                    }
                } else if (e.getSource() == databaseMenuItem) {
                    if (menuItemMethods.switchMode(OperationMode.DATABASE)) {
                        setOperationMode(OperationMode.DATABASE);
                    } else {
                        desktopMenuItem.setSelected(true);
                    }
                }
            }
        });
    }

    public void getFocus() {
        final COINCOMO coincomo = this;
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                coincomo.requestFocus();
                coincomo.toFront();
                coincomo.repaint();
            }
        });
    }

    private static void log(Level level, String message) {
        Logger.getLogger(COINCOMO.class.getName()).log(level, message);
    }

    public static void main(String[] args) {
        // Create a Nice Look and Feel
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Run Application
        COINCOMO coincomo = new COINCOMO();
    }
}
