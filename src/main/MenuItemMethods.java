/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package main;

import core.COINCOMOAdaptationAndReuse;
import core.COINCOMOComponent;
import core.COINCOMOConstants;
import core.COINCOMOConstants.FP;
import core.COINCOMOConstants.OperationMode;
import core.COINCOMOSubComponent;
import core.COINCOMOSubSystem;
import core.COINCOMOSystem;
import core.COINCOMOUnit;
import database.COINCOMOComponentManager;
import database.COINCOMODatabaseManager;
import database.COINCOMOLocalCalibrationManager;
import database.COINCOMOSystemManager;
import dialogs.AboutUsDialog;
import dialogs.CalibratedParametersDialog;
import dialogs.CalibratedProjectDialog;
import dialogs.ConnectDialog;
import dialogs.EAFDialog;
import dialogs.EquationEditorDialog;
import dialogs.FunctionPointsDialog;
import dialogs.ScaleFactorsDialog;
import dialogs.SignupDialog;
import dialogs.ViewProjectsDialog;
import dialogs.showAssignGroupDialog;
import java.awt.Color;
import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import panels.MultipleBuildsGraphPanel;

/**
 *
 * @author Raed Shomali
 */
public class MenuItemMethods {

    public String lastOpenedFileName = null;
    private static DecimalFormat format1Decimal = new DecimalFormat("0.0");
    private static DecimalFormat format2Decimals = new DecimalFormat("0.00");
    private static DecimalFormat format3Decimals = new DecimalFormat("0.000");
    private static DecimalFormat format2DecimalWithComma = new DecimalFormat("#,##0.00");
    private LookAndFeel defaultUI = UIManager.getLookAndFeel();
    private File file;
    private File calFile = null;
    private COINCOMO coincomo = null;
    private static Map<String, COINCOMO> openFiles = Collections.synchronizedMap(new HashMap<String, COINCOMO>());
    private static Map<Long, COINCOMO> openProjects = Collections.synchronizedMap(new HashMap<Long, COINCOMO>());
    private static Map<COINCOMOSystem, COINCOMO> openSystems = Collections.synchronizedMap(new HashMap<COINCOMOSystem, COINCOMO>());
    private static Set<COINCOMO> openCOINCOMOs = Collections.synchronizedSet(new HashSet<COINCOMO>());
    private String hoursPerPersonMonth;
    private double hours = 0.0;
    private File[] roots;
    private Set<String> rootMap = new HashSet<String>();

    public MenuItemMethods(COINCOMO c) {
        roots = File.listRoots();
        int len = roots.length;
        for (int i = 0; i < len; i++) {
            rootMap.add(roots[i].getAbsolutePath());
        }
        this.coincomo = c;
        openCOINCOMOs.add(c);
    }

    public void connectToDatabase() {
        ConnectDialog cd = new ConnectDialog(coincomo);
    }

    public boolean disconnectFromDatabase(boolean bypass) {
        final int systems = getActiveSystems();
        boolean result = true;
        int option = JOptionPane.NO_OPTION;

        if (systems > 0) {
            if (bypass) {
                option = JOptionPane.YES_OPTION;
            } else {
                option = JOptionPane.showConfirmDialog(coincomo, "Disconnecting from the database will close all opened projects. Do you still want to continue?", "Disconnecting from database?", JOptionPane.YES_NO_OPTION);
            }

            if (option == JOptionPane.YES_OPTION) {
                COINCOMO[] coincomos = openCOINCOMOs.toArray(new COINCOMO[0]);
                for (int i = 0; i < coincomos.length; i++) {
                    if (coincomos[i] != coincomo) {
                        result &= coincomos[i].exit();
                    } else {
                        result &= coincomos[i].closeProject();
                    }

                    if (!result) {
                        break;
                    }
                }
            } else {
                result = false;
            }
        }

        if (result) {
            COINCOMODatabaseManager.disconnectAll();
            COINCOMODatabaseManager.deregisterDriver();
            COINCOMO.setDisconnected();
            coincomo.resetMenuBar();

            GlobalMethods.updateStatusBar("Disconnected From Database.", coincomo);
        }

        return result;
    }
    public void signup() {
        SignupDialog su = new SignupDialog(coincomo,1);
        coincomo.resetMenuBar();
    }
    
    public void login() {
        SignupDialog su = new SignupDialog(coincomo,2);
        coincomo.resetMenuBar();
    }
    
    public void logout() {
        boolean result = closeProject();
        if (result) {
            openCOINCOMOs.remove(coincomo);
            //System.out.println("After active systems: " + MenuItemMethods.getActiveSystems());
            if (!openCOINCOMOs.isEmpty())
                coincomo.dispose(); 
        }
        COINCOMO.setLogedout();
        coincomo.resetMenuBar();
        GlobalMethods.updateStatusBar("Loged Out Successfully !", Color.BLACK, coincomo);
    }
    
    public void assigngroup(){
        if(coincomo.getCurrentSystem()==null)
        {
            GlobalMethods.updateStatusBar("Please First Load A Project !", Color.RED, coincomo);
            return;
        }
        new showAssignGroupDialog(coincomo);
        coincomo.resetMenuBar();
    }
    public boolean exit() {
        //System.out.println("Current active systems: " + MenuItemMethods.getActiveSystems());
        boolean result = closeProject();
        if (result) {
            openCOINCOMOs.remove(coincomo);
            //System.out.println("After active systems: " + MenuItemMethods.getActiveSystems());
            if (openCOINCOMOs.isEmpty()) {
                System.exit(0);
            } else {
                coincomo.dispose();
            }
        }

        return result;
    }

    public static COINCOMO hasActiveProject(long databaseID) {
        final OperationMode operationMode = COINCOMO.getOperationMode();
        COINCOMO coincomo = null;

        if (databaseID > 0) {
            if (operationMode == OperationMode.DATABASE) {
                if (openProjects.containsKey(databaseID)) {
                    coincomo = openProjects.get(databaseID);
                }
            }
        }

        return coincomo;
    }

    public static int getActiveSystems() {
        final OperationMode operationMode = COINCOMO.getOperationMode();
        final int systems = openSystems.size();
        final int projects = openProjects.size();
        final int files = openFiles.size();

        if (operationMode == OperationMode.DATABASE) {
            if (systems != projects) {
                log(Level.WARNING, "The number of active projects (" + projects + ") do not match the number of active systems (" + systems + "). They should be equal!");
            }
        } else {
            if (systems > files) {
                log(Level.INFO, "The number of active files (" + files + ") do not match the number of active systems (" + systems + ").");
            } else if (systems < files) {
                log(Level.WARNING, "The number of active files (" + files + ") do not match the number of active systems (" + systems + "). The number of active files should be less or equal to the number of active systems!");
            }
        }

        return systems;
    }

    public static void addActiveSystem(COINCOMO coincomo, COINCOMOSystem system) {
        final OperationMode operationMode = COINCOMO.getOperationMode();

        if (coincomo != null && system != null) {
            if (operationMode == OperationMode.DATABASE) {
                openProjects.put(system.getDatabaseID(), coincomo);
            }

            openSystems.put(system, coincomo);
        }
    }

    public static COINCOMO removeActiveSystem(COINCOMOSystem system) {
        final OperationMode operationMode = COINCOMO.getOperationMode();
        final COINCOMO coincomo = openSystems.remove(system);
        COINCOMO sameCOINCOMO = null;

        if (system != null) {
            if (operationMode == OperationMode.DATABASE) {
                sameCOINCOMO = openProjects.remove(system.getDatabaseID());

                if (coincomo != sameCOINCOMO) {
                    log(Level.WARNING, "Pointers to the same COINCOMO instance do not match!");
                }
            }
        }

        return coincomo;
    }

    //TO DO update the project to reflect new COINCOMOLocalCalibration values
    public void loadCalibration() {
        String ext[] = {"cal"};

        File tempFile = GlobalMethods.getFile(coincomo, null, ext, "Load", false);

        if (tempFile == null) {
            return;
        }

        if (COINCOMOXML.validateCalibrationXML(tempFile)) {
            COINCOMO.localCalibration = null;
            COINCOMO.localCalibration = COINCOMOXML.importCalibrationXML(tempFile);
        } else {
            JOptionPane.showMessageDialog(null, "Supplied CAL file is not valid.", "CAL FILE INVALID ERROR", 0);
        }
        /*
         Properties props = new Properties();
         try {
         props.load(new FileInputStream(tempFile));

         float a, b, c, d;
         a = Float.parseFloat(props.getProperty("A"));
         b = Float.parseFloat(props.getProperty("B"));
         c = Float.parseFloat(props.getProperty("C"));
         d = Float.parseFloat(props.getProperty("D"));

         COINCOMOComponentParameters localCal = ((COINCOMOComponent) this.coincomo.getCurrentUnit()).getParameters();
         localCal.setA(a);
         localCal.setB(b);
         localCal.setC(c);
         localCal.setD(d);

         } catch (IOException ex) {
         JOptionPane.showMessageDialog(coincomo, "Config File Error: " + ex.getMessage(), "Config File Error", JOptionPane.ERROR_MESSAGE);
         }
         */
    }

    //TO DO update the project to reflect new COINCOMOLocalCalibration values
    public boolean saveCalibration() {


        //we need to check if a project is computed to save a calibration file
        //file not even saved once
        if (calFile == null) {
            String ext[] = {"cal"};
            while (true) {
                calFile = GlobalMethods.getFile(coincomo, null, ext, "Save", false);
                //user cancelled save
                if (calFile == null) {
                    return false;
                } else if (calFile.exists()) {
                    //Confirm overwrite?
                    if (JOptionPane.showConfirmDialog(null, "Replace existing file?", "Existing file", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        //Over-write confirmed
                        break;
                    } else {
                        //Cancel over-write, get a different file
                        calFile = null;
                    }
                } else {
                    //User enetered new file, so break
                    break;
                }

            }
        }

        return COINCOMOLocalCalibrationManager.saveLocalCalibrationAsXML(COINCOMO.localCalibration, calFile);
        /*
         COINCOMOComponentParameters localCal = ((COINCOMOComponent) this.coincomo.getCurrentUnit()).getParameters();
         String output = "A=" + localCal.getA() + "\nB=" + localCal.getB() + "\nC=" + localCal.getC() + "\nD=" + localCal.getD() + "\n";

         BufferedWriter out;
         try {
         FileWriter x = new FileWriter(calFile);
         out = new BufferedWriter(x);
         out.write(output);
         out.close();
         x.close();
         } catch (IOException ex) {
         Logger.getLogger(MenuItemMethods.class.getName()).log(Level.SEVERE, null, ex);
         }
         //Dont reset here, need to save in COINCOMOSystem file
         //localCal.resetDirty();
         return true;
         */
        /*   
         String ext[] = {"cal"};

         File tempFile = GlobalMethods.getFile(coincomo, null, ext, "Save");

         if (tempFile == null) {
         return;
         }
        
         COINCOMOLocalCalibration localCal = ((COINCOMOSystem)
         this.coincomo.getHierarchyPanel().getCOINCOMOTreeRoot().getCOINCOMOUnit()).getLocalCalibration();
         String output = "A=" + localCal.getA()
         + "\nB=" + localCal.getB() 
         + "\nC="+localCal.getC()
         + "\nD="+localCal.getD() 
         + "\n";
        
         BufferedWriter out;
         try {
         out = new BufferedWriter(new FileWriter(tempFile));
         out.write(output);
         out.close();
         } catch (IOException ex) {
         Logger.getLogger(MenuItemMethods.class.getName()).log(Level.SEVERE, null, ex);
         }
         */
    }

    //TO DO update the project to reflect new COINCOMOLocalCalibration values
    public void project() {
        //  throw new UnsupportedOperationException();
        /*
         int hashsubcomponents = 0;

         if (this.coincomo.getCurrentSystem() == null) {
         JOptionPane.showMessageDialog(null, "First Create a System");
         return;
         }

         COINCOMOSystem system = this.coincomo.getCurrentSystem();
         ArrayList<COINCOMOUnit> orderedSubSystemsVector = system.getListOfSubUnits();
         for (int i = 0; i < orderedSubSystemsVector.size(); i++) {
         COINCOMOSubSystem subSystem = (COINCOMOSubSystem) orderedSubSystemsVector.get(i);
         ArrayList<COINCOMOUnit> orderedComponentsVector = subSystem.getListOfSubUnits();
         for (int j = 0; j < orderedComponentsVector.size(); j++) {
         COINCOMOComponent component = (COINCOMOComponent) orderedComponentsVector.get(j);
         ArrayList<COINCOMOUnit> orderedSubComponentsVector = component.getListOfSubUnits();
         hashsubcomponents += orderedSubComponentsVector.size();
         }
         }
         if (hashsubcomponents == 0) {
         JOptionPane.showMessageDialog(null, "First Add a Subcomponent to use Local Calibration");
         return;
         } else {
         new CalibratedProjectDialog(coincomo);
         }
         */
        new CalibratedProjectDialog(coincomo);
    }

    //TO DO update the project to reflect new COINCOMOLocalCalibration values
    public void compute() {
        new CalibratedParametersDialog(coincomo);
    }

    //Opens an existing project from a file, and loading into COINCOMO GUI
    public void openExistingProject(File file) {
        // Deal with Windows shortcuts and resolve actual file paths.
        File actFile = null;
        LnkParser lnkParser = null;
        if (file.getName().toLowerCase().endsWith(".lnk")) {
            try {
                lnkParser = new LnkParser(file);
                if (lnkParser.isDirectory()) {
                    JOptionPane.showMessageDialog(null, "Please select a file shortcut instead of folder shortcut.", "OPEN LNK DIRECTORY ERROR", 0);
                    actFile = null;
                    lnkParser = null;
                } else {
                    actFile = new File(lnkParser.getRealFilename());
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Unable to open file shortcut.", "OPEN LNK ERROR", 0);
                actFile = null;
                lnkParser = null;
            }
        } else {
            actFile = file;
        }

        //Validate the file before opening
        if (!COINCOMOXML.validateXML(actFile)) {
            JOptionPane.showMessageDialog(null, "Supplied CET file is not valid.", "CET FILE INVALID ERROR", 0);
        } else {
            //Get the COINCOMOSystem
            COINCOMOSystem system = COINCOMOXML.importXML(actFile);
            //Check if system loaded correctly
            if (system != null) {
                //Create a project based off the system
                createProject(system);

                //Add the opened file to the list of opened files
                try {
                    openFiles.put(actFile.getCanonicalPath(), coincomo);
                    GlobalMethods.setLastUsedDirectory(file.getPath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                //Set the filename
                this.file = actFile;
                this.coincomo.getCurrentSystem().clearDirty();
                this.coincomo.setTitle(COINCOMOConstants.COINCOMO_TITLE + " - " + actFile);
                //setTitleBar(coincomo,1);
            }
        }
    }

    //Opens an existing project and loads into COINCOMO GUI
    public void openExistingProject() {

        //List of valid extensions
        String ext[] = {"cet", "lnk"};

        //Get a temp file with the specified extensions
        File tempFile = GlobalMethods.getFile(coincomo, null, ext, "Open", false);

        //Verify a file was chosen
        if (tempFile != null) {
            COINCOMO c = null;

            //Attempt to get system/COINCOMO (if it is already opened)
            try {
                c = this.openFiles.get(tempFile.getCanonicalPath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            //Check if Project was found opened
            if (c != null) {
                c.getFocus();
            } else {
                //If current COINCOMO window does not have an opened project
                if (this.coincomo.getCurrentSystem() == null) {
                    //Open a project in the current window
                    this.openExistingProject(tempFile);
                } else {
                    //Open project in a new COINCOMO window
                    new COINCOMO(tempFile);
                }
            }
        }
    }
    /*public boolean closeCalibration()
     {
     String x = "Save LocalCalibration?";
     int option = JOptionPane.NO_OPTION;
     try 
     {
     option = JOptionPane.showConfirmDialog(coincomo, x, x, JOptionPane.YES_NO_OPTION);
     }
            
     catch (Exception ex) 
     {
     //Error checking system, ask to save
     option = JOptionPane.showConfirmDialog(coincomo, x, x, JOptionPane.YES_NO_OPTION);
     }

     //Handle save option request
     switch (option) 
     {
     case JOptionPane.YES_OPTION:
     //Attempt to save the project
     if (!saveCalibration()) {
     //User cancelled save action, return cancelled
     return false;
     }
     case JOptionPane.NO_OPTION:
     //Return exited code
     return true;
     case JOptionPane.CANCEL_OPTION:
     default:
     return false;
     }
     }*/
    //Closes a project, attempts to save project, and resets GUI

    public boolean closeProject() {
        System.out.println("Current active systems: " + MenuItemMethods.getActiveSystems());
        //Verify a project is open
        final COINCOMOSystem system = coincomo.getCurrentSystem();

        if (system == null) {
            GlobalMethods.updateStatusBar("NO PROJECT TO CLOSE.", this.coincomo);
            return true;
        }
        //check to save local calibration here
        /*COINCOMOLocalCalibration localCal = ((COINCOMOSystem)
         this.coincomo.getHierarchyPanel().getCOINCOMOTreeRoot().getCOINCOMOUnit()).getLocalCalibration();

         if(localCal.isDirty())
         { 
         //if true then we need to save it  
         closeCalibration();
         }*/

        if (COINCOMO.getOperationMode() == OperationMode.DATABASE) {
            // Nothing
        } else {
            //Verify user wants to close project
            String x = "Save current project?";
            int option = JOptionPane.NO_OPTION;

            //If the system needs to be saved
            if (system.isDirty()) {
                //Ask if project should be saved
                option = JOptionPane.showConfirmDialog(coincomo, x, x, JOptionPane.YES_NO_CANCEL_OPTION);
            }

            //Handle save option request
            switch (option) {
                case JOptionPane.YES_OPTION:
                    //Attempt to save the project
                    if (!saveProject()) {
                        //User cancelled save action, return cancelled
                        System.out.println("After active systems: " + MenuItemMethods.getActiveSystems());
                        return false;
                    } else {
                        //Remember to remove the file from opened file list right after it is saved.
                        if (file != null) {
                            try {
                                MenuItemMethods.openFiles.remove(file.getCanonicalPath());
                            } catch (IOException ex) {
                                log(Level.SEVERE, ex.getLocalizedMessage());
                            }
                        }
                        file = null;
                    }
                    break;
                case JOptionPane.NO_OPTION:
                    //Remove file from opened file list
                    if (file != null) {
                        try {
                            MenuItemMethods.openFiles.remove(file.getCanonicalPath());
                        } catch (IOException ex) {
                            log(Level.SEVERE, ex.getLocalizedMessage());
                        }
                    }
                    file = null;
                    break;
                //Return exited code
                case JOptionPane.CANCEL_OPTION:
                default:
                    System.out.println("After active systems: " + MenuItemMethods.getActiveSystems());
                    return false;
            }
        }

        coincomo.clearCurrentSystem();
        coincomo.setTitle(COINCOMOConstants.COINCOMO_TITLE);
        GlobalMethods.updateStatusBar("Project closed.", coincomo);
        System.out.println("After active systems: " + MenuItemMethods.getActiveSystems());
        return true;
    }

    public boolean closeProject(COINCOMOSystem system) {
        boolean result = true;

        COINCOMO[] coincomos = openCOINCOMOs.toArray(new COINCOMO[0]);
        for (int i = 0; i < coincomos.length; i++) {
            if (COINCOMO.getOperationMode() == OperationMode.DATABASE) {
                if (coincomos[i].getCurrentSystem() == null) {
                    continue;
                } else if (coincomos[i].getCurrentSystem().getDatabaseID() == system.getDatabaseID()) {
                    if (coincomos[i] != coincomo) {
                        result &= coincomos[i].exit();
                    } else {
                        result &= coincomos[i].closeProject();
                    }
                }
            } else {
                // Desktop mode does not call this particular method
            }
        }

        return result;
    }

    public boolean closeProjects() {
        boolean result = true;
        COINCOMO[] coincomos = openCOINCOMOs.toArray(new COINCOMO[0]);
        for (int i = 0; i < coincomos.length; i++) {
            if (coincomos[i] != coincomo) {
                result &= coincomos[i].exit();
            } else {
                result &= coincomos[i].closeProject();
            }

            if (!result) {
                break;
            }
        }

        return result;
    }

    public void ViewProjects() {
        new ViewProjectsDialog(coincomo);
    }

    //Saves the project
    public boolean saveProject() {
        //Verify project is opened
        if (this.coincomo.getCurrentSystem() == null) {
            GlobalMethods.updateStatusBar("NO PROJECT TO SAVE.", this.coincomo);
            return true;
        }

        //first we will check whether the file is saved even once or not if not we will open dialog box else we will just save
        if (file == null) {
            String validExt[] = {COINCOMO.EXTENSION, "lnk"};
            while (true) {
                //Get a file
                if (lastOpenedFileName == null) {
                    file = GlobalMethods.getFile(coincomo, file == null ? "*." + COINCOMO.EXTENSION : file.getAbsolutePath(), validExt, "Save", false);
                } else {
                    file = GlobalMethods.getFile(coincomo, file == null ? lastOpenedFileName : file.getAbsolutePath(), validExt, "Save", false);
                }
                //If user cancelled, stop saving
                if (file == null) {
                    return false;
                } //If file already exists
                else if (file.exists()) {
                    //Confirm overwrite?
                    if (JOptionPane.showConfirmDialog(null, "Replace existing file?", "Existing file", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        //Over-write confirmed
                        break;
                    } else {
                        //Cancel over-write, get a different file
                        file = null;
                    }
                } else {
                    //User entered new file, so break
                    break;
                }
            }
        }

        GlobalMethods.setLastUsedDirectory(file.getPath());
        String fileName = file.getName();
        File temp = new File(file.getParent());
        if (!temp.isDirectory()) {
            JOptionPane.showMessageDialog(null, "File Name shouldn't contain <, >, /, \\, &, /, newline, carriage return, tab, null, '\\f', `, ?, *, |, \" and :", "SAVE ERROR", 0);
            file = null;
            if (!saveProject()) {
                return false;
            }
        }
        // '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '<', '>', '|', '\"', ':'
        if (fileName.contains("/") || fileName.contains("\\")
                || fileName.contains("\n") || fileName.contains("\r")
                || fileName.contains("\t") || fileName.contains("\0")
                || fileName.contains("`") || fileName.contains("\f")
                || fileName.contains("?") || fileName.contains("*")
                || fileName.contains("<") || fileName.contains(">")
                || fileName.contains("|") || fileName.contains("\"")
                || fileName.contains(":") || fileName.toLowerCase().equals(".xml")) {
            //", "SPECIAL CHARACTERS IN NAME ERROR
            JOptionPane.showMessageDialog(null, "File Name shouldn't contain <, >, /, \\, &, /, newline, carriage return, tab, null, '\\f', `, ?, *, |, \" and :", "SAVE ERROR", 0);
            file = null;
            if (!saveProject()) {
                return false;
            }
        }

        // Deal with Windows shortcuts and resolve actual file paths.
        File actFile = null;
        LnkParser lnkParser = null;
        if (file.getName().toLowerCase().endsWith(".lnk")) {
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, "File cannot be saved at shortcut's location. Shortcut does not exist.", "SAVE LNK SHORCUT ERROR", JOptionPane.WARNING_MESSAGE);
                file = null;
                return false;
            }
            try {
                lnkParser = new LnkParser(file);
                if (lnkParser.isDirectory()) {
                    JOptionPane.showMessageDialog(null, "File cannot be saved at shortcut's location. Shortcut is a directory.", "SAVE LNK DIRECTORY ERROR", JOptionPane.WARNING_MESSAGE);
                    file = null;
                    lnkParser = null;
                    return false;
                } else {
                    actFile = new File(lnkParser.getRealFilename());
                    if (!actFile.getName().toLowerCase().endsWith("." + COINCOMO.EXTENSION)) {
                        file = null;
                        actFile = null;
                        lnkParser = null;
                        JOptionPane.showMessageDialog(null, "File cannot be saved at shortcut's location. Shortcut does not point to a CET file.", "SAVE LNK EXTENSION ERROR", JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "File cannot be saved at shortcut's location.", "SAVE LNK ERROR", JOptionPane.WARNING_MESSAGE);
                file = null;
                actFile = null;
                lnkParser = null;
                return false;
            }
        } else {
            actFile = file;
        }

        if (COINCOMO.getOperationMode() == OperationMode.DESKTOP) {
            //Add opened file to list of opened files
            try {
                MenuItemMethods.openFiles.put(actFile.getCanonicalPath(), coincomo);
            } catch (IOException ex) {
                /* (Larry) What are the two line below was trying to do? Temporary commented out.
                 JOptionPane.showMessageDialog(null, "File cannot be saved at root volume level", "SAVE ERROR", JOptionPane.WARNING_MESSAGE);
                 return false;
                 */
            }
        } else {
            // Do nothing for Database mode.
        }

        //Save the project
        COINCOMOSystemManager.saveSystemAsXML(coincomo.getCurrentSystem(), actFile);
        GlobalMethods.updateStatusBar("File Saved.", this.coincomo);

        if (COINCOMO.getOperationMode() == OperationMode.DESKTOP) {
            //sets the flag stating that we have a save copy
            coincomo.getCurrentSystem().clearDirty();
            coincomo.setTitle(COINCOMOConstants.COINCOMO_TITLE + " - " + actFile);
        } else {
            // Do nothing for Database mode.
        }

        return true;
    }

    //Save a copy of the project to a different file
    public boolean saveProjectAs() {
        if (COINCOMO.getOperationMode() == OperationMode.DESKTOP) {
            //Save a copy of the current file
            File tempFile = this.file;
            if (this.file != null) {
                lastOpenedFileName = this.file.getName();
            }

            //Reset current file
            this.file = null;
            try {
                //Attempt to remove file from open projects
                MenuItemMethods.openFiles.remove(tempFile.getCanonicalPath());
            } catch (Exception ex) {
                //ex.printStackTrace();
            }

            //Project was not saved
            if (!this.saveProject()) {
                //Reset filename
                this.file = tempFile;
                //Return error saving
                return false;
            }

            //Add file to list of open files
            try {
                if (null != this.file) {
                    MenuItemMethods.openFiles.put(this.file.getCanonicalPath(), coincomo);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return true;
        } else {
            this.saveProject();

            return true;
        }
    }

    //Creates the graphical elements of a COINCOMOSystem
    public void createProject(COINCOMOSystem system) {
        boolean isNewProject = false;
        //Verify system exists
        if (system == null) {
            //System is null, create a new system
            system = COINCOMOSystemManager.insertSystem();
            isNewProject = true;
        }

        //Verify system was created
        if (system != null) {
            // Get either existing COINCOMO or new COINCOMO whether current COINCOMO already has a project open
            COINCOMO c = coincomo.getCurrentSystem() == null ? coincomo : new COINCOMO();

            c.setCurrentSystem(system);

            GlobalMethods.updateStatusBar("New Project Created.", c);

            this.coincomo.setTitle(COINCOMOConstants.COINCOMO_TITLE + " - Untitled");

            if (isNewProject) {
                //System.out.println("New Project");
                c.renameNewProject();
            } else {
                //System.out.println("Existing Project");
            }
        } else {
            GlobalMethods.updateStatusBar("Could NOT Create a New Project, Please Verify the Parameters in the \'Connect Dialog\'", Color.RED, coincomo);
        }
    }

    public boolean switchMode(OperationMode toOperationMode) {
        final OperationMode operationMode = COINCOMO.getOperationMode();
        final int activeSystems = openSystems.size();
        boolean result = false;

        if (operationMode == toOperationMode) {
            // Ignore the switching if the modes are the same
        } else {
            int option = JOptionPane.NO_OPTION;

            if (activeSystems == 0) {
                option = JOptionPane.YES_OPTION;
            } else {
                switch (toOperationMode) {
                    case DESKTOP:
                        option = JOptionPane.showConfirmDialog(coincomo, "Switching to Desktop mode from Database mode will close all opened projects, do you still want to continue?", "Switching to Desktop mode...", JOptionPane.YES_NO_OPTION);
                        break;
                    case DATABASE:
                        option = JOptionPane.showConfirmDialog(coincomo, "Switching to Database mode from Desktop mode will close all opened projects, do you still want to continue?", "Switching to Database mode...", JOptionPane.YES_NO_OPTION);
                        break;
                }
            }

            if (option == JOptionPane.YES_OPTION) {
                switch (operationMode) {
                    case DESKTOP:
                        result = closeProjects();
                        break;
                    case DATABASE:
                        result = disconnectFromDatabase(true);
                        break;
                }
            }
        }

        return result;
    }

    public void exportProjectAsCSV() {
        if (coincomo.getCurrentSystem() == null) {
            //GlobalMethods.updateStatusBar("No System has been Opened to read from", Color.RED, this.coincomo);
            JOptionPane.showMessageDialog(this.coincomo, "No System has been Opened to read from", "EXPORT ERROR", 0);
            return;
        }

        try {

            // For Example, Windows OS's UI
            //setOperatingSystemUI();

            // Has to be an CSV Document
            String validExt[] = {"csv"};
            File f;

            while (true) {

                f = GlobalMethods.getFile(coincomo, "*." + validExt[0], validExt, "Save", true);

                //user cancelled save
                if (f == null) {
                    return;
                }
                //if / was added to the filename
                if (rootMap.contains(f.getParent())) {
                    int ret = JOptionPane.showConfirmDialog(this.coincomo, "File is being exported to the root directory. Click yes to continue.", "EXPORT WARNING", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (ret == 1) {
                        return;
                    } else {
                    }
                }
                if (f.exists()) {
                    //Confirm overwrite?
                    if (JOptionPane.showConfirmDialog(null, "Replace existing file?", "Existing file", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        //Over-write confirmed
                        break;
                    } else {
                        //Cancel over-write, get a different file
                        f = null;
                    }
                } else {
                    //User enetered new file, so break
                    break;
                }

            }


            // Has to be an XML Document

            BufferedWriter out = new BufferedWriter(new FileWriter(f));

            COINCOMOSystem system = coincomo.getCurrentSystem();

            out.write("COINCOMO\n");
            out.write("\n");

            out.write("System\n");
            out.write("ID , Name , SLOC , Cost , Staff\n");
            out.write(system.getUnitID() + " , " + system.getName() + " , " + system.getSLOC() + " , " + system.getCost() + " , " + format1Decimal.format(GlobalMethods.roundOff(system.getStaff(), 1)) + "\n");
            out.write("\n");

            Iterator subSystemIterator = system.getListOfSubUnits().iterator();
            if (subSystemIterator.hasNext()) {
                out.write(" , SubSystem\n");
                out.write(" , ID , Name , SLOC , Cost , Staff\n");

                while (subSystemIterator.hasNext()) {
                    COINCOMOSubSystem subSystem = (COINCOMOSubSystem) subSystemIterator.next();

                    out.write(" , " + subSystem.getUnitID() + " , " + subSystem.getName() + " , " + subSystem.getSLOC() + " , " + subSystem.getCost() + " , " + format1Decimal.format(GlobalMethods.roundOff(subSystem.getStaff(), 1)) + "\n");
                    out.write("\n");

                    Iterator componentIterator = subSystem.getListOfSubUnits().iterator();
                    if (componentIterator.hasNext()) {
                        out.write(" , , Component\n");
                        out.write(" , , ID , Name , SLOC , Scale Factors , SCED , SCED% , Cost , Staff , Effort , InstCost , PROD , Schedule, Hours/PM\n");

                        while (componentIterator.hasNext()) {
                            COINCOMOComponent component = (COINCOMOComponent) componentIterator.next();

                            out.write(" , , " + component.getUnitID() + " , " + component.getName() + " , " + component.getSLOC() + " , " + component.getSF() + " , " + component.getSCED() + " , " + component.getSCEDPercent() + " , " + component.getCost() + " , " + format1Decimal.format(GlobalMethods.roundOff(component.getStaff(), 1)) + " , " + component.getEffort() + " , " + COINCOMOComponentManager.calculateInstructionCost(component) + " , " + COINCOMOComponentManager.calculateProductivity(component) + " , " + component.getSchedule() + " , " + component.getParameters().getWorkHours() + "\n");
                            out.write(" , , Inception\n");
                            out.write(" , , Effort , Month , Personnel , EffortPercentage , SchedulePercentage\n");
                            out.write(" , , " + component.getInceptionEffort() + " , " + component.getInceptionMonth() + " , " + component.getInceptionPersonnel() + " , " + component.getInceptionEffortPercentage() + " , " + component.getInceptionSchedulePercentage() + "\n");
                            out.write(" , , Elaboration\n");
                            out.write(" , , Effort , Month , Personnel , EffortPercentage , SchedulePercentage\n");
                            out.write(" , , " + component.getElaborationEffort() + " , " + component.getElaborationMonth() + " , " + component.getElaborationPersonnel() + " , " + component.getElaborationEffortPercentage() + " , " + component.getElaborationSchedulePercentage() + "\n");
                            out.write(" , , Construction\n");
                            out.write(" , , Effort , Month , Personnel , EffortPercentage , SchedulePercentage\n");
                            out.write(" , , " + component.getConstructionEffort() + " , " + component.getConstructionMonth() + " , " + component.getConstructionPersonnel() + " , " + component.getConstructionEffortPercentage() + " , " + component.getConstructionSchedulePercentage() + "\n");
                            out.write(" , , Transition\n");
                            out.write(" , , Effort , Month , Personnel , EffortPercentage , SchedulePercentage\n");
                            out.write(" , , " + component.getTransitionEffort() + " , " + component.getTransitionMonth() + " , " + component.getTransitionPersonnel() + " , " + component.getTransitionEffortPercentage() + " , " + component.getTransitionSchedulePercentage() + "\n");
                            out.write("\n");

                            Iterator subComponentIterator = component.getListOfSubUnits().iterator();
                            if (subComponentIterator.hasNext()) {
                                out.write(" , , , SubComponent\n");
                                out.write(" , , , ID , Name , Language , SLOC , Labor Rate , EAF , NomEffort , EstEffort , PROD , Cost , Inst Cost , Staff , RISK\n");

                                while (subComponentIterator.hasNext()) {
                                    COINCOMOSubComponent subComponent = (COINCOMOSubComponent) subComponentIterator.next();

                                    out.write(" , , , " + subComponent.getUnitID() + " , " + subComponent.getName()
                                            + " , " + subComponent.getLanguage() + " , " + subComponent.getSLOC()
                                            + " , " + subComponent.getLaborRate() + " , " + subComponent.getEAF()
                                            + " , " + subComponent.getNominalEffort() + " , " + subComponent.getEstimatedEffort()
                                            + " , " + subComponent.getProductivity() + " , " + subComponent.getCost()
                                            + " , " + subComponent.getInstructionCost() + " , " + format1Decimal.format(GlobalMethods.roundOff(subComponent.getStaff(), 1)) + " , " + subComponent.getRisk()
                                            + "\n");
                                    out.write("\n");

                                    out.write(", , , , Size , REVL , Language , Total SLOC (no REVL)\n");
                                    out.write(", , , , ," + subComponent.getREVL() + " , " + subComponent.getLanguage() + " , " + subComponent.getSumOfSLOCs() + "\n");
                                    out.write("\n");

                                    out.write(", , , , , New , SLOC\n");
                                    out.write(", , , , , , " + subComponent.getNewSLOC() + "\n");
                                    out.write("\n");

                                    out.write(", , , , , Function Points , Ratio Type , Multiplier , Calculation Method , Inputs , Outputs , Files , Interfaces , Queries , Total Unadjusted Function Points , Equivalent SLOC\n");
                                    final int[] subTotals = subComponent.getSubTotals();
                                    out.write(", , , , , ," + subComponent.getRatioType() + " , " + subComponent.getMultiplier() + " , " + subComponent.getCalculationMethod() + " , " + subTotals[FP.EI.ordinal()] + " , " + subTotals[FP.EO.ordinal()] + " , " + subTotals[FP.ILF.ordinal()] + " , " + subTotals[FP.EIF.ordinal()] + " , " + subTotals[FP.EQ.ordinal()] + " , " + subComponent.getTotalUnadjustedFunctionPoints() + " , " + subComponent.getEquivalentSLOC() + "\n");
                                    out.write("\n");

                                    Iterator adaptationIterator = subComponent.getListOfSubUnits().iterator();
                                    if (adaptationIterator.hasNext()) {
                                        out.write(", , , , , AdaptationAndReuse\n");
                                        out.write(", , , , , ID , Name, InitialSLOC, DesignModified, CodeModified, IntegrationModified, SoftwareUnderstanding, AssessmentAssimilation , Unfamiliarity, AutomaticallyTranslated, AutoTranslationProductivity, ASLOC\n");

                                        while (adaptationIterator.hasNext()) {
                                            COINCOMOAdaptationAndReuse aAR = (COINCOMOAdaptationAndReuse) adaptationIterator.next();

                                            out.write(", , , , , " + aAR.getUnitID() + " , " + aAR.getName() + " , " + aAR.getAdaptedSLOC() + " , " + aAR.getDesignModified() + " , " + aAR.getCodeModified() + " , " + aAR.getIntegrationModified() + " , " + aAR.getSoftwareUnderstanding() + " , " + aAR.getAssessmentAndAssimilation() + " , " + aAR.getUnfamiliarityWithSoftware() + " , " + aAR.getAutomaticallyTranslated() + " , " + aAR.getAutomaticTranslationProductivity() + " , " + aAR.getEquivalentSLOC() + "\n");
                                        }

                                        out.write("\n");
                                    } // End of Adaptation and Reuses
                                }

                                out.write("\n");
                            } // End of Sub Components
                        }

                        out.write("\n");
                    } // End of Components
                }

                out.write("\n");
            } // End of Sub Systems

            out.flush();

            // Free Resources ..
            out.close();


            // MultiBuild Report Section
            File multiBuildReport = new File(f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(".")) + "'s Multibuild Report.csv");

            BufferedWriter out2 = new BufferedWriter(new FileWriter(multiBuildReport));

            // Traverse Subsystems ..
            ArrayList<COINCOMOUnit> orderedSubSystemsVector = system.getListOfSubUnits();
            for (int i = 0; i < orderedSubSystemsVector.size(); i++) {
                COINCOMOSubSystem subSystem = (COINCOMOSubSystem) orderedSubSystemsVector.get(i);

                out2.write("Subsystem: " + subSystem.getName() + "\n");
                out2.write("\n");

                ArrayList<COINCOMOUnit> orderedComponentsVector = subSystem.getListOfSubUnits();

                int indents = 0;

                COINCOMOComponent previousComponent = null;

                // Traverse Components ..
                for (int j = 0; j < orderedComponentsVector.size(); j++) {
                    COINCOMOComponent component = (COINCOMOComponent) orderedComponentsVector.get(j);

                    // Except the First Component ...
                    if (j != 0) {
                        // To Determine How Much Indentation is Needed ...

                        // Clear ..
                        int startingPoint = previousComponent.getMultiBuildShift();

                        // Inception
                        int inceptionSize = (int) (previousComponent.getInceptionSchedulePercentage() / 100.0d * previousComponent.getSLOC() * MultipleBuildsGraphPanel.getMinimizeFactor());
                        int inceptionEndPoint = startingPoint + inceptionSize;

                        // Elaboration
                        int elaborationSize = (int) (previousComponent.getElaborationSchedulePercentage() / 100.0d * previousComponent.getSLOC() * MultipleBuildsGraphPanel.getMinimizeFactor());
                        int elaborationEndPoint = startingPoint + inceptionSize + elaborationSize;

                        // Construction
                        int constructionSize = (int) (previousComponent.getConstructionSchedulePercentage() / 100.0d * previousComponent.getSLOC() * MultipleBuildsGraphPanel.getMinimizeFactor());
                        int constructionEndPoint = startingPoint + inceptionSize + elaborationSize + constructionSize;

                        // Transition
                        int transitionSize = (int) (previousComponent.getTransitionSchedulePercentage() / 100.0d * previousComponent.getSLOC() * MultipleBuildsGraphPanel.getMinimizeFactor());
                        int transitionEndPoint = startingPoint + inceptionSize + elaborationSize + constructionSize + transitionSize;

                        if (component.getMultiBuildShift() >= startingPoint && component.getMultiBuildShift() < inceptionEndPoint) {
                            indents = indents + 0;
                        } else if (component.getMultiBuildShift() >= inceptionEndPoint && component.getMultiBuildShift() < elaborationEndPoint) {
                            indents = indents + 1;
                        } else if (component.getMultiBuildShift() >= elaborationEndPoint && component.getMultiBuildShift() < constructionEndPoint) {
                            indents = indents + 2;
                        } else if (component.getMultiBuildShift() >= constructionEndPoint && component.getMultiBuildShift() < transitionEndPoint) {
                            indents = indents + 3;
                        } else {
                            indents = indents + 4;
                        }
                    }

                    StringBuilder indentation = new StringBuilder();

                    // Do the Indentations ...
                    for (int c = 0; c < indents; c++) {
                        indentation.append(",");
                    }

                    out2.write(indentation.toString() + " , " + "Inception , Elaboration , Construction , Transition , Total E&C , Total , , , Component: " + component.getName() + "\n");
                    out2.write(indentation.toString() + "Effort" + " , " + GlobalMethods.roundOff(component.getInceptionEffortPercentage(), 2) + " , "
                            + GlobalMethods.roundOff(component.getElaborationEffortPercentage(), 2) + " , "
                            + GlobalMethods.roundOff(component.getConstructionEffortPercentage(), 2) + " , "
                            + GlobalMethods.roundOff(component.getTransitionEffortPercentage(), 2) + " , "
                            + GlobalMethods.roundOff(component.getTotalEffortPercentageEC(), 2) + " , "
                            + GlobalMethods.roundOff(component.getTotalEffortPercentage(), 2) + " , "
                            + "\n");
                    out2.write(indentation.toString() + "Schedule" + " , " + GlobalMethods.roundOff(component.getInceptionSchedulePercentage(), 2) + " , "
                            + GlobalMethods.roundOff(component.getElaborationSchedulePercentage(), 2) + " , "
                            + GlobalMethods.roundOff(component.getConstructionSchedulePercentage(), 2) + " , "
                            + GlobalMethods.roundOff(component.getTransitionSchedulePercentage(), 2) + " , "
                            + GlobalMethods.roundOff(component.getTotalSchedulePercentageEC(), 2) + " , "
                            + GlobalMethods.roundOff(component.getTotalSchedulePercentage(), 2) + " , "
                            + "\n");
                    out2.write(indentation.toString() + "PM" + " , " + GlobalMethods.roundOff(component.getInceptionEffort(), 2) + " , "
                            + GlobalMethods.roundOff(component.getElaborationEffort(), 2) + " , "
                            + GlobalMethods.roundOff(component.getConstructionEffort(), 2) + " , "
                            + GlobalMethods.roundOff(component.getTransitionEffort(), 2) + " , "
                            + GlobalMethods.roundOff(component.getTotalEffortEC(), 2) + " , "
                            + GlobalMethods.roundOff(component.getTotalEffort(), 2) + " , "
                            + "\n");
                    out2.write(indentation.toString() + "M" + " , " + GlobalMethods.roundOff(component.getInceptionMonth(), 2) + " , "
                            + GlobalMethods.roundOff(component.getElaborationMonth(), 2) + " , "
                            + GlobalMethods.roundOff(component.getConstructionMonth(), 2) + " , "
                            + GlobalMethods.roundOff(component.getTransitionMonth(), 2) + " , "
                            + GlobalMethods.roundOff(component.getTotalMonthEC(), 2) + " , "
                            + GlobalMethods.roundOff(component.getTotalMonth(), 2) + " , "
                            + "\n");
                    out2.write(indentation.toString() + "PM/M" + " , " + GlobalMethods.roundOff(component.getInceptionPersonnel(), 2) + " , "
                            + GlobalMethods.roundOff(component.getElaborationPersonnel(), 2) + " , "
                            + GlobalMethods.roundOff(component.getConstructionPersonnel(), 2) + " , "
                            + GlobalMethods.roundOff(component.getTransitionPersonnel(), 2) + " , "
                            //+ GlobalMethods.roundOff(component.getTotalPersonnelEC(), 2) + " , "
                            //+ GlobalMethods.roundOff(component.getTotalPersonnel(), 2) + " , "
                            + "\n");

                    out2.write("\n");

                    previousComponent = component;
                }

                out2.write("\n");
                out2.write("\n");
            }

            out2.flush();

            // Free Resources ..
            out2.close();


            GlobalMethods.updateStatusBar("Exporting had Finished.", this.coincomo);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this.coincomo, "File couldn't be exported to the selected location. " + e.getMessage(), "EXPORT ERROR", 0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this.coincomo, "Exception occured." + e.getMessage(), "EXPORT ERROR", 0);
        } finally {
            // Go Back to Default
            setJavaUI();
        }
    }

    public void exportProjectAsHTML() {
        if (coincomo.getCurrentSystem() == null) {
            //GlobalMethods.updateStatusBar("No System has been Opened to read from", Color.RED, this.coincomo);
            JOptionPane.showMessageDialog(this.coincomo, "No System has been Opened to read from", "EXPORT ERROR", 0);
            return;
        }

        try {

            // For Example, Windows OS's UI
            //setOperatingSystemUI();

            // Has to be an HTML Document
            String validExt[] = {"html"};
            File f;

            while (true) {
                f = GlobalMethods.getFile(coincomo, "*." + validExt[0], validExt, "Save", true);
                //user cancelled save
                if (f == null) {
                    return;
                }
                //if / was added to the filename
                if (rootMap.contains(f.getParent())) {
                    int ret = JOptionPane.showConfirmDialog(this.coincomo, "File is being exported to the root directory. Click yes to continue.", "EXPORT WARNING", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (ret == 1) {
                        return;
                    } else {
                    }
                }
                if (f.exists()) {
                    //Confirm overwrite?
                    if (JOptionPane.showConfirmDialog(null, "Replace existing file?", "Existing file", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        //Over-write confirmed
                        break;
                    } else {
                        //Cancel over-write, get a different file
                        f = null;
                    }
                } else {
                    //User enetered new file, so break
                    break;
                }

            }

            BufferedWriter out = new BufferedWriter(new FileWriter(f));

            COINCOMOSystem system = coincomo.getCurrentSystem();

            out.write("<html>\n");
            out.write("<head>\n");
            out.write("<title>COINCOMO Project</title>\n");
            out.write("<style type = 'text/css'>\n");
            out.write("table	{ font-size:9pt; }\n");
            out.write("td	{ font-family:courier; }\n");

            out.write("a.system:link			{ color:black; font-size:11pt; text-decoration:underline; }\n");
            out.write("a.system:visited       		{ color:black; font-size:11pt; text-decoration:underline; }\n");
            out.write("a.system:hover         		{ color:black; font-size:11pt; text-decoration:underline; }\n");
            out.write("a.system:active        		{ color:black; font-size:11pt; text-decoration:underline; }\n");

            out.write("a.subsystem:link		{ color:red; font-size:11pt; text-decoration:underline; }\n");
            out.write("a.subsystem:visited       	{ color:red; font-size:11pt; text-decoration:underline; }\n");
            out.write("a.subsystem:hover         	{ color:red; font-size:11pt; text-decoration:underline; }\n");
            out.write("a.subsystem:active        	{ color:red; font-size:11pt; text-decoration:underline; }\n");

            out.write("a.component:link		{ color:green; font-size:11pt; text-decoration:underline; }\n");
            out.write("a.component:visited       	{ color:green; font-size:11pt; text-decoration:underline; }\n");
            out.write("a.component:hover         	{ color:green; font-size:11pt; text-decoration:underline; }\n");
            out.write("a.component:active        	{ color:green; font-size:11pt; text-decoration:underline; }\n");

            out.write("a.subcomponent:link		{ color:blue; font-size:11pt; text-decoration:underline; }\n");
            out.write("a.subcomponent:visited       	{ color:blue; font-size:11pt; text-decoration:underline; }\n");
            out.write("a.subcomponent:hover         	{ color:blue; font-size:11pt; text-decoration:underline; }\n");
            out.write("a.subcomponent:active        	{ color:blue; font-size:11pt; text-decoration:underline; }\n");

            out.write("</style>\n");

            out.write("<script type = 'text/javascript'>\n");

            out.write("function eventTrigger (e) \n");
            out.write("{\n");
            out.write("if( !e )\n");
            out.write("{\n");
            out.write("e = event;\n");
            out.write("}\n");
            out.write("return e.target || e.srcElement;\n");
            out.write("}\n");

            out.write("function displayInformation( e )\n");
            out.write("{\n");
            out.write("var obj = eventTrigger( e );\n");
            out.write("if( obj.tagName != 'A' )\n");
            out.write("{\n");
            out.write("obj = obj.parentNode;\n");
            out.write("}\n");

            out.write("var id = obj.id ;\n");
            out.write("var informationAreaObject = document.getElementById( 'information' ) ;\n");

            out.write("for( var i = 0 ; i < informationAreaObject.childNodes.length ; i ++ )\n");
            out.write("{\n");
            out.write("var temp = informationAreaObject.childNodes[ i ] ;\n");

            out.write("if( temp.tagName == 'DIV' )\n");
            out.write("{\n");
            out.write("temp.style.display = 'none' ;\n");
            out.write("}\n");
            out.write("}\n");

            out.write("if( id.substring( 0 , 6 ) == 'system' )\n");
            out.write("{\n");
            out.write("document.getElementById( 'system' + id.substring( id.indexOf( '_' ) + 1 ) ).style.display = '' ;\n");
            out.write("}\n");
            out.write("else if( id.substring( 0 , 9 ) == 'subsystem' ) \n");
            out.write("{\n");
            out.write("document.getElementById( 'subsystem' + id.substring( id.indexOf( '_' ) + 1 ) ).style.display = '' ;\n");
            out.write("}\n");
            out.write("else if( id.substring( 0 , 9 ) == 'component' )\n");
            out.write("{\n");
            out.write("document.getElementById( 'component' + id.substring( id.indexOf( '_' ) + 1) ).style.display = '' ;\n");
            out.write("}\n");
            out.write("else\n");
            out.write("{\n");
            out.write("document.getElementById( 'subcomponent' + id.substring( id.indexOf( '_' ) + 1 ) ).style.display = '' ;\n");
            out.write("}\n");
            out.write("}\n");

            out.write("</script>\n");

            out.write("</head>\n");

            out.write("<body style = 'font-family:arial'>\n");

            out.write("<center><h3> COINCOMO Project </h3></center>\n");

            out.write("<table border = '1' rules = 'group' width = '95%' align = 'center' cellpadding = '15'>\n");
            out.write("<tr>\n");
            out.write("<td width = '20%' valign = 'top' style = 'font-family:arial'>\n");
            out.write("<ul style = 'padding-left:5px' >\n");

            out.write("<a class = 'system' id = 'system_" + system.getUnitID() + "_" + system.getName() + "' href = '#' onclick = 'return displayInformation( event )'> " + system.getName() + " </a>\n");

            out.write("<ul style = 'padding-left:16px' type = 'disc' >\n");

            ArrayList<COINCOMOUnit> orderedSubSystemsVector = system.getListOfSubUnits();

            for (int i = 0; i < orderedSubSystemsVector.size(); i++) {
                COINCOMOSubSystem subSystem = (COINCOMOSubSystem) orderedSubSystemsVector.get(i);

                out.write("<li><a class = 'subsystem' id = 'subsystem_" + subSystem.getUnitID() + "_" + subSystem.getName() + "' href = '#' onclick = 'return displayInformation( event )'> " + subSystem.getName() + " </a></li>\n");

                out.write("<ul style = 'padding-left:16px' type = 'square' >\n");


                ArrayList<COINCOMOUnit> orderedComponentsVector = subSystem.getListOfSubUnits();

                for (int j = 0; j < orderedComponentsVector.size(); j++) {
                    COINCOMOComponent component = (COINCOMOComponent) orderedComponentsVector.get(j);

                    out.write("<li><a class = 'component' id = 'component_" + component.getUnitID() + "_" + component.getName() + "' href = '#' onclick = 'return displayInformation( event )'> " + component.getName() + " </a> </li>\n");

                    out.write("<ul style = 'padding-left:16px' type = 'circle' >\n");

                    ArrayList<COINCOMOUnit> orderedSubComponentsVector = component.getListOfSubUnits();

                    for (int c = 0; c < orderedSubComponentsVector.size(); c++) {
                        COINCOMOSubComponent subComponent = (COINCOMOSubComponent) orderedSubComponentsVector.get(c);
                        out.write("<li><a class = 'subcomponent' id = 'subcomponent_" + subComponent.getUnitID() + "_" + subComponent.getName() + "' href = '#' onclick = 'return displayInformation( event )'> " + subComponent.getName() + " </a></li>\n");
                    }

                    out.write("</ul>\n");
                }

                out.write("</ul>\n");
            }

            out.write("</ul>\n");
            out.write("</ul>\n");

            out.write("</td>\n");

            out.write("<td width = '80%' style = 'font-family:arial'>\n");

            out.write("<div id = 'information' >\n");

            out.write("<div style = 'display:none;' id = 'system" + system.getUnitID() + "_" + system.getName() + "' >\n");
            out.write("<center><h2> System : " + system.getName() + " </h2></center>\n");

            out.write("<table border = '1' width = '90%' align = 'center'>\n");
            ArrayList<COINCOMOUnit> orderedVector = system.getListOfSubUnits();
            long totalSLOC = 0;
            double totalCost = 0;
            double totalStaff = 0;
            double totalEffort = 0;
            double totalSchedule = 0;
            out.append("<tr>");
            out.append("<th bgcolor = '#CCCCCC'> Name </th>");
            out.append("<th bgcolor = '#CCCCCC'> Size SLOC </th>");
            out.append("<th bgcolor = '#CCCCCC'> Cost </th>");
            out.append("<th bgcolor = '#CCCCCC'> Staff </th>");
            out.append("<th bgcolor = '#CCCCCC'> Effort </th>");
            out.append("<th bgcolor = '#CCCCCC'> Schedule </th>");
            out.append("</tr>");
            for (int i = 0; i < orderedVector.size(); i++) {
                COINCOMOUnit tempUnit = (COINCOMOUnit) orderedVector.get(i);
                totalSLOC += tempUnit.getSLOC();
                totalCost += tempUnit.getCost();
                totalStaff += tempUnit.getStaff();
                totalEffort += tempUnit.getEffort();
                totalSchedule += tempUnit.getSchedule();
                out.write("<tr>\n");
                out.append("<td>" + tempUnit.getName() + "</td>");
                out.write("<td>" + GlobalMethods.FormatLongWithComma(tempUnit.getSLOC()) + " </td>\n");
                out.append("<td>" + format2Decimals.format(GlobalMethods.roundOff(tempUnit.getCost(), 2)) + "</td>");
                out.append("<td>" + format1Decimal.format(GlobalMethods.roundOff(tempUnit.getStaff(), 1)) + "</td>");
                out.append("<td>" + format2Decimals.format(GlobalMethods.roundOff(tempUnit.getEffort(), 2)) + "</td>");
                out.append("<td>" + format2Decimals.format(GlobalMethods.roundOff(tempUnit.getSchedule(), 2)) + "</td>");
                out.write("</tr>\n");
                if (i == orderedVector.size() - 1) {
                    out.append("<tr align = 'center'>");
                    out.append("<th bgcolor = '#CCCCCC'>" + "Total" + "</th>");
                    out.append("<th>" + GlobalMethods.FormatLongWithComma(totalSLOC) + "</th>");
                    out.append("<th> $" + format2Decimals.format(GlobalMethods.roundOff(totalCost, 2)) + "</th>");
                    out.append("<th>   </th>");
                    //out.append("<th>" + format1Decimal.format(GlobalMethods.roundOff(totalStaff, 1)) + "</th>");
                    out.append("<th>" + format2Decimals.format(GlobalMethods.roundOff(totalEffort, 2)) + "</th>");
                    // out.append("<th>" + format2Decimals.format(GlobalMethods.roundOff(totalSchedule, 2)) + "</th>");
                    out.append("<th> </th>");
                    out.append("</tr>");
                }
            }

            out.write("</table>");

            out.write("</div>\n");

            for (int i = 0; i < orderedSubSystemsVector.size(); i++) {
                COINCOMOSubSystem subSystem = (COINCOMOSubSystem) orderedSubSystemsVector.get(i);

                out.write("<div style = 'display:none' id = 'subsystem" + subSystem.getUnitID() + "_" + subSystem.getName() + "' >\n");
                out.write("<center><h2> SubSystem : " + subSystem.getName() + " </h2></center>\n");//rebecca
                /*out.write("<table border = '1' width = '90%' align = 'center'>\n");

                 out.write("<tr>\n");
                 out.write("<th width = '30%' bgcolor = '#CCCCCC' > ID </th>\n");
                 out.write("<td align = 'center' > " + subSystem.getUnitID() + " </td>\n");
                 out.write("</tr>\n");
                 out.write("<tr>\n");
                 out.write("<th width = '30%' bgcolor = '#CCCCCC' > Name </th>\n");
                 out.write("<td align = 'center' > " + subSystem.getName() + " </td>\n");
                 out.write("</tr>\n");
                 out.write("<tr>\n");
                 out.write("<th width = '30%' bgcolor = '#CCCCCC' > SLOC </th>\n");
                 out.write("<td align = 'center' > " + COINCOMOSubSystemManager.get_Final_SLOC_With_REVL(subSystem) + " </td>\n");
                 out.write("</tr>\n");
                 out.write("<tr>\n");
                 out.write("<th width = '30%' bgcolor = '#CCCCCC' > Staff </th>\n");
                 out.write("<td align = 'center' > " + format1Decimal.format(GlobalMethods.roundOff(COINCOMOSystemManager.get_SystemOverview_Staff(subSystem),1)) + " </td>\n");
                 out.write("</tr>\n");
                 out.write("<tr>\n");
                 out.write("<th width = '30%' bgcolor = '#CCCCCC' > Cost </th>\n");
                 out.write("<td align = 'center' > $" + format2Decimals.format(GlobalMethods.roundOff(COINCOMOSubSystemManager.get_Cost(subSystem), 2)) + " </td>\n");
                 out.write("</tr>\n");

                 out.write("</table>\n");*/

                out.write("<table border = '1' width = '90%' align = 'center'>\n");
                ArrayList<COINCOMOUnit> orderedSubSysVector = subSystem.getListOfSubUnits();
                totalSLOC = 0;
                totalCost = 0;
                totalStaff = 0;
                totalEffort = 0;
                totalSchedule = 0;
                out.append("<tr>");
                out.append("<th bgcolor = '#CCCCCC'> Name </th>");
                out.append("<th bgcolor = '#CCCCCC'> Size SLOC </th>");
                out.append("<th bgcolor = '#CCCCCC'> Cost </th>");
                out.append("<th bgcolor = '#CCCCCC'> Staff </th>");
                out.append("<th bgcolor = '#CCCCCC'> Effort </th>");
                out.append("<th bgcolor = '#CCCCCC'> Schedule </th>");
                out.append("</tr>");
                for (int c = 0; c < orderedSubSysVector.size(); c++) {
                    COINCOMOUnit tempUnit = (COINCOMOUnit) orderedSubSysVector.get(c);
                    totalSLOC += tempUnit.getSLOC();
                    totalCost += tempUnit.getCost();
                    totalStaff += tempUnit.getStaff();
                    totalEffort += tempUnit.getEffort();
                    totalSchedule += tempUnit.getSchedule();
                    out.write("<tr>\n");
                    out.append("<td>" + tempUnit.getName() + "</td>");
                    out.write("<td>" + GlobalMethods.FormatLongWithComma(tempUnit.getSLOC()) + " </td>\n");
                    out.append("<td>" + format2Decimals.format(GlobalMethods.roundOff(tempUnit.getCost(), 2)) + "</td>");
                    out.append("<td>" + format1Decimal.format(GlobalMethods.roundOff(tempUnit.getStaff(), 1)) + "</td>");
                    out.append("<td>" + format2Decimals.format(GlobalMethods.roundOff(tempUnit.getEffort(), 2)) + "</td>");
                    out.append("<td>" + format2Decimals.format(GlobalMethods.roundOff(tempUnit.getSchedule(), 2)) + "</td>");
                    out.write("</tr>\n");
                    if (c == orderedSubSysVector.size() - 1) {
                        out.append("<tr align = 'center'>");
                        out.append("<th bgcolor = '#CCCCCC'>" + "Total" + "</th>");
                        out.append("<th>" + GlobalMethods.FormatLongWithComma(totalSLOC) + "</th>");
                        out.append("<th> $" + format2Decimals.format(GlobalMethods.roundOff(totalCost, 2)) + "</th>");
                        //out.append("<th>" + format1Decimal.format(GlobalMethods.roundOff(totalStaff, 1)) + "</th>");
                        out.append("<th> </th>");
                        out.append("<th>" + format2Decimals.format(GlobalMethods.roundOff(totalEffort, 2)) + "</th>");
                        //out.append("<th>" + format2Decimals.format(GlobalMethods.roundOff(totalSchedule, 2)) + "</th>");
                        out.append("<th> </th>");
                        out.append("</tr>");
                    }
                }

                out.write("</table>");
                out.write("</div>\n");

                ArrayList<COINCOMOUnit> orderedComponentsVector = subSystem.getListOfSubUnits();

                for (int j = 0; j < orderedComponentsVector.size(); j++) {
                    COINCOMOComponent component = (COINCOMOComponent) orderedComponentsVector.get(j);

                    out.write("<div style = 'display:none' id = 'component" + component.getUnitID() + "_" + component.getName() + "' >\n");
                    out.write("<center><h2> Component : " + component.getName() + " </h2></center>\n");//rebecca
                    out.write("<table border = '1' width = '90%' align = 'center'>\n");

                    out.append("<tr>");
                    out.append("<th bgcolor = '#CCCCCC'> ID </th>");
                    out.append("<th bgcolor = '#CCCCCC'> Name </th>");
                    out.append("<th bgcolor = '#CCCCCC'> SLOC </th>");
                    out.append("<th bgcolor = '#CCCCCC'> Scale Factor </th>");
                    out.append("<th bgcolor = '#CCCCCC'> SCED </th>");
                    out.append("<th bgcolor = '#CCCCCC'> SCED% </th>");
                    out.append("<th bgcolor = '#CCCCCC'> Cost </th>");
                    out.append("<th bgcolor = '#CCCCCC'> Staff </th>");
                    out.append("<th bgcolor = '#CCCCCC'> Total Effort </th>");
                    out.append("<th bgcolor = '#CCCCCC'> Total inst Cost</th>");
                    out.append("<th bgcolor = '#CCCCCC'> PROD </th>");
                    out.append("<th bgcolor = '#CCCCCC'> Total Schedule </th>");
                    out.append("<th bgcolor = '#CCCCCC'> hours/PM </th>");
                    out.append("</tr>");
                    out.write("<tr>");
                    out.write("<td align = 'center' > " + component.getUnitID() + " </td>");
                    out.write("<td align = 'center' > " + component.getName() + " </td>");
                    out.write("<td align = 'center' > " + GlobalMethods.FormatLongWithComma(component.getSLOC()) + " </td>");
                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getSF(), 2)) + " </td>");
                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getSCED(), 2)) + " </td>");
                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getSCEDPercent(), 2)) + " </td>");
                    out.write("<td align = 'center' > $" + format2Decimals.format(GlobalMethods.roundOff(component.getCost(), 2)) + " </td>");
                    out.write("<td align = 'center' > " + format1Decimal.format(GlobalMethods.roundOff(component.getStaff(), 1)) + " </td>");
                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getEffort(), 2)) + " </td>");
                    out.write("<td align = 'center' > $" + format2Decimals.format(GlobalMethods.roundOff(COINCOMOComponentManager.calculateInstructionCost(component), 2)) + " </td>");
                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(COINCOMOComponentManager.calculateProductivity(component), 2)) + " </td>");
                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getSchedule(), 2)) + " </td>");
                    out.write("<td align = 'center' > " + component.getParameters().getWorkHours() + " </td>");
                    out.write("</tr>");

//                    out.write("<tr>\n");
//                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > ID </th>\n");
//                    out.write("<td align = 'center' > " + component.getUnitID() + " </td>\n");
//                    out.write("</tr>\n");
//                    out.write("<tr>\n");
//                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > Name </th>\n");
//                    out.write("<td align = 'center' > " + component.getName() + " </td>\n");
//                    out.write("</tr>\n");
//                    out.write("<tr>\n");
//                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > SLOC </th>\n");
//                    out.write("<td align = 'center' > " + GlobalMethods.FormatLongWithComma(component.getSLOC()) + " </td>\n");
//                    out.write("</tr>\n");
//                    out.write("<tr>\n");
//                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > Scale Factor </th>\n");
//                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getSF(), 2)) + " </td>\n");
//                    out.write("</tr>\n");
//                    out.write("<tr>\n");
//                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > SCED </th>\n");
//                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getSCED(), 2)) + " </td>\n");
//                    out.write("</tr>\n");
//                    out.write("<tr>\n");
//                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > SCED% </th>\n");
//                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getSCEDPercent(), 2)) + " </td>\n");
//                    out.write("</tr>\n");
//                    out.write("<tr>\n");
//                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > Cost </th>\n");
//                    out.write("<td align = 'center' > $" + format2Decimals.format(GlobalMethods.roundOff(component.getCost(), 2)) + " </td>\n");
//                    out.write("</tr>\n");
//                    out.write("<tr>\n");
//                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > Staff </th>\n");
//                    out.write("<td align = 'center' > " + format1Decimal.format(GlobalMethods.roundOff(component.getStaff(), 1)) + " </td>\n");
//                    out.write("</tr>\n");
//                    out.write("<tr>\n");
//                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > Total Effort </th>\n");
//                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getEffort(), 2)) + " </td>\n");
//                    out.write("</tr>\n");
//                    out.write("<tr>\n");
//                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > Total Inst Cost </th>\n");
//                    out.write("<td align = 'center' > $" + format2Decimals.format(GlobalMethods.roundOff(COINCOMOComponentManager.calculateInstructionCost(component), 2)) + " </td>\n");
//                    out.write("</tr>\n");
//                    out.write("<tr>\n");
//                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > PROD </th>\n");
//                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(COINCOMOComponentManager.calculateProductivity(component), 2)) + " </td>\n");
//                    out.write("</tr>\n");
//                    out.write("<tr>\n");
//                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > Total Schedule </th>\n");
//                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getSchedule(), 2)) + " </td>\n");
//                    out.write("</tr>\n");
//                    out.write("<tr>\n");
//                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > Hours/PM </th>\n");
//                    out.write("<td align = 'center' > " + component.getParameters().getWorkHours() + " </td>\n");
//                    out.write("</tr>\n");

                    out.write("</table>\n");

                    out.write("<br />\n");

                    out.write("<table border = '0' width = '90%' align = 'center'>\n");

                    out.write("<tr>\n");
                    out.write("<td style = 'font-family:arial'>\n");
                    out.write("<h4 align = 'center'> Scale Factor " + component.getSF() + "</h4>\n");
                    out.write("<table border = '1' width = '100%' align = 'center'>\n");
                    // out.write("<caption> Size </caption>\n");
                    final COINCOMOConstants.Rating sfRatings[] = component.getSFRatings();
                    final COINCOMOConstants.Increment sfIncrements[] = component.getSFIncrements();

                    out.append("<tr>");
                    out.append("<th  bgcolor = '#CCCCCC' >  </th>\n");
                    out.append("<th bgcolor = '#CCCCCC'> Precedentedness (REVL) </th>");
                    out.append("<th bgcolor = '#CCCCCC'> Development Flexibility (FLEX) </th>");
                    out.append("<th bgcolor = '#CCCCCC'> Risk Resolution (RESL) </th>");
                    out.append("<th bgcolor = '#CCCCCC'> Team Cohesion (TEAM) </th>");
                    out.append("<th bgcolor = '#CCCCCC'> Process Maturity (PMAT) </th>");
                    out.append("</tr>");

                    out.write("<tr>\n");
                    out.write("<th bgcolor = '#CCCCCC' > Rating </th>\n");
                    for (int k = 0; k < COINCOMOConstants.SFS.length; k++) {
                        out.write("<td align = 'center' > " + sfRatings[k].toString() + " </td>\n");
                    }
                    out.write("</tr>\n");
                    out.write("<tr>\n");
                    out.write("<th  bgcolor = '#CCCCCC' > % Incr </th>\n");
                    for (int k = 0; k < COINCOMOConstants.SFS.length; k++) {
                        out.write("<td align = 'center' > " + sfIncrements[k].toString() + " </td>\n");
                    }
                    out.write("</tr>\n");

                    out.write("</table>\n");
                    out.write("</td>\n");

                    out.write("<td style = 'font-family:arial'>\n");
                    out.write("<h4 align = 'center'> Schedule (SCED) </h4>\n");
                    out.write("<table border = '1' width = '100%' align = 'center'>\n");
                    //out.write("<caption> Function Points </caption>\n");

                    out.append("<tr>");
                    out.append("<th bgcolor = '#CCCCCC'>  </th>");
                    out.append("<th bgcolor = '#CCCCCC'> Schedule (SCED)</th>");
                    out.append("<th bgcolor = '#CCCCCC'> Schedule% (SCED%)</th>");

                    out.append("</tr>");

                    out.write("<tr>");
                    out.write("<th bgcolor = '#CCCCCC' > Factor </th>\n");
                    
       
                    out.write("<td align = 'center' > " + component.getSCED()+ " </td>");
                    out.write("<td align = 'center' > " + component.getSCEDPercent() + " </td>");
                    
                    out.write("</tr>");
                    out.write("<tr>");
                    out.write("<th bgcolor = '#CCCCCC' > Rating </th>\n");
                    
       
                    out.write("<td align = 'center' > " + component.getSCEDRating().toString()+ " </td>");
                    out.write("<td align = 'center' >   </td>");
                    
                    out.write("</tr>");
                    out.write("<tr>");
                    out.write("<th bgcolor = '#CCCCCC' > %Incr </th>\n");
                    
       
                    out.write("<td align = 'center' > " +  component.getSCEDIncrement().toString()+ " </td>");
                    out.write("<td align = 'center' >  </td>");
                    
                    out.write("</tr>");

                    out.write("</table>\n");
                    out.write("</td>\n");
                    out.write("</table>\n");

                    out.write("<br />\n");

                    out.write("<h4 align = 'center'> " + component.getName() + "'s Overview </h4>\n");

                    out.write("<table border = '1' width = '90%' align = 'center'>\n");
                    // out.write("<caption> " + component.getName() + "'s Overview </caption>\n");
                    out.write("<tr>\n");
                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > Name </th>\n");
                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > Size </th>\n");
                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > Labor Rate ($/Month) </th>\n");
                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > EAF </th>\n");
                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > Language </th>\n");
                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > NOM Effort Dev </th>\n");
                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > EST Effort Dev </th>\n");
                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > PROD </th>\n");
                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > COST </th>\n");
                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > INST COST </th>\n");
                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > Staff </th>\n");
                    out.write("<th width = '30%' bgcolor = '#CCCCCC' > Risk </th>\n");
                    out.write("</tr>\n");
                    ArrayList<COINCOMOUnit> orderedSubComponentsVector = component.getListOfSubUnits();
                    for (int c = 0; c < orderedSubComponentsVector.size(); c++) {
                        COINCOMOSubComponent subComponent = (COINCOMOSubComponent) orderedSubComponentsVector.get(c);
                        out.write("<tr>\n");
                        out.write("<td align = 'center' > " + subComponent.getName() + " </td>\n");
                        out.write("<td align = 'center' > " + GlobalMethods.FormatLongWithComma(subComponent.getSLOC()) + " </td>\n");
                        out.write("<td align = 'center' > " + subComponent.getLaborRate() + " </td>\n");
                        out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(subComponent.getEAF(), 2)) + " </td>\n");
                        out.write("<td align = 'center' > " + subComponent.getLanguage() + " </td>\n");
                        out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(subComponent.getNominalEffort(), 2)) + " </td>\n");
                        out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(subComponent.getEstimatedEffort(), 2)) + " </td>\n");
                        out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(subComponent.getProductivity(), 2)) + " </td>\n");
                        out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(subComponent.getCost(), 2)) + " </td>\n");
                        out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(subComponent.getInstructionCost(), 2)) + " </td>\n");
                        out.write("<td align = 'center' > " + format1Decimal.format(GlobalMethods.roundOff(subComponent.getStaff(), 1)) + " </td>\n");
                        out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(subComponent.getRisk(), 2)) + " </td>");
                        //out.write("<td align = 'center' >  </td>\n");
                        out.write("</tr>\n");
                    }
                    out.write("</table>\n");

                    out.write("<br />\n");

                    out.write("<h4 align = 'center'> Estimation </h4>\n");

                    out.write("<table border = '1' width = '90%' align = 'center'>\n");
                    // out.write("<caption> Estimation </caption>\n");
                     double cpntTotalEffort = COINCOMOComponentManager.calculateEffort(component);
                    double cpntTotalScheduleOptimistic = COINCOMOComponentManager.calculateSchedule(component, COINCOMOConstants.Scenario.Optimistic);
                    double cpntTotalSchedule = COINCOMOComponentManager.calculateSchedule(component, COINCOMOConstants.Scenario.MostLikely);
                    double cpntTotalSchedulePessimistic = COINCOMOComponentManager.calculateSchedule(component, COINCOMOConstants.Scenario.Pessimistic);
                    double cpntTotalProductivity = COINCOMOComponentManager.calculateProductivity(component);
                    double cpntTotalCost = COINCOMOComponentManager.calculateCost(component);
                    double cpntTotalInstructionCost = COINCOMOComponentManager.calculateInstructionCost(component);
                    double cpntTotalStaff = COINCOMOComponentManager.calculateStaff(component);
                    double cpntTotalRisk = COINCOMOComponentManager.calculateRisk(component);

                    out.append("<tr>");
                    out.append("<th bgcolor = '#CCCCCC' > Estimated </th>");
                    out.append("<th bgcolor = '#CCCCCC'> Effort </th>");
                    out.append("<th bgcolor = '#CCCCCC'> Schedule </th>");
                    out.append("<th bgcolor = '#CCCCCC'> PROD </th>");
                    out.append("<th bgcolor = '#CCCCCC'> COST </th>");
                    out.append("<th bgcolor = '#CCCCCC'> INST </th>");
                    out.append("<th bgcolor = '#CCCCCC'> Staff </th>");
                    out.append("<th bgcolor = '#CCCCCC'> Risk </th>");
                    out.append("</tr>");
                    out.append("<tr>");
                    out.append("<th bgcolor = '#CCCCCC'> Optimistic </th>");
                    out.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(cpntTotalEffort / 1.25, 2)) + " </th>");
                    out.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(cpntTotalScheduleOptimistic, 2)) + " </th>");
                    out.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(cpntTotalProductivity * 1.25, 2)) + " </th>");
                    out.append("<th> " + format2DecimalWithComma.format(GlobalMethods.roundOff(cpntTotalCost / 1.25, 2)) + " </th>");
                    out.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(cpntTotalInstructionCost / 1.25, 2)) + " </th>");
                    out.append("<th> " + format1Decimal.format(GlobalMethods.roundOff((cpntTotalEffort / 1.25) / cpntTotalScheduleOptimistic, 1)) + " </th>");
                    out.append("</tr>");
                    out.append("<tr>");
                    out.append("<th bgcolor = '#CCCCCC'> Most Likely </th>");
                    out.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(cpntTotalEffort, 2)) + " </th>");
                    out.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(cpntTotalSchedule, 2)) + " </th>");
                    out.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(cpntTotalProductivity, 2)) + " </th>");
                    out.append("<th> " + format2DecimalWithComma.format(GlobalMethods.roundOff(cpntTotalCost, 2)) + " </th>");
                    out.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(cpntTotalInstructionCost, 2)) + " </th>");
                    out.append("<th> " + format1Decimal.format(GlobalMethods.roundOff(cpntTotalStaff, 1)) + " </th>");
                    out.append("<th> " + format1Decimal.format(GlobalMethods.roundOff(cpntTotalRisk, 1)) + " </th>"); //Changed by Roopa Dharap
                    out.append("</tr>");
                    out.append("<tr>");
                    out.append("<th bgcolor = '#CCCCCC'> Pessimistic </th>");
                    out.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(cpntTotalEffort * 1.25, 2)) + " </th>");
                    out.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(cpntTotalSchedulePessimistic, 2)) + " </th>");
                    out.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(cpntTotalProductivity / 1.25, 2)) + " </th>");
                    out.append("<th> " + format2DecimalWithComma.format(GlobalMethods.roundOff(cpntTotalCost * 1.25, 2)) + " </th>");
                    out.append("<th> " + format2Decimals.format(GlobalMethods.roundOff(cpntTotalInstructionCost * 1.25, 2)) + " </th>");
                    out.append("<th> " + format1Decimal.format(GlobalMethods.roundOff((cpntTotalEffort * 1.25) / cpntTotalSchedulePessimistic, 1)) + " </th>");
                    out.append("</tr>");
                    out.append("</table>");
                    
                    
                    out.write("<h4 align = 'center'> COPSEMO </h4>\n");

                    out.write("<table border = '0' width = '90%' align = 'center'>\n");

                    out.write("<tr>\n");
                    out.write("<td style = 'font-family:arial'>\n");

                    out.write("<table border = '1' width = '100%' align = 'center'>\n");

                    out.write("<caption> Inception </caption>\n");

                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Effort </th>\n");
                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getInceptionEffort(), 2)) + " </td>\n");
                    out.write("</tr>\n");
                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Month </th>\n");
                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getInceptionMonth(), 2)) + " </td>\n");
                    out.write("</tr>\n");
                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Personnel </th>\n");
                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getInceptionPersonnel(), 2)) + " </td>\n");
                    out.write("</tr>\n");
                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Effort% </th>\n");
                    out.write("<td align = 'center' > " + component.getInceptionEffortPercentage() + " </td>\n");
                    out.write("</tr>\n");
                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Schedule% </th>\n");
                    out.write("<td align = 'center' > " + component.getInceptionSchedulePercentage() + " </td>\n");
                    out.write("</tr>\n");

                    out.write("</table>\n");
                    out.write("</td>\n");
                    out.write("<td style = 'font-family:arial'>\n");

                    out.write("<table border = '1' width = '100%' align = 'center'>\n");

                    out.write("<caption> Elaboration </caption>\n");

                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Effort </th>\n");
                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getElaborationEffort(), 2)) + " </td>\n");
                    out.write("</tr>\n");
                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Month </th>\n");
                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getElaborationMonth(), 2)) + " </td>\n");
                    out.write("</tr>\n");
                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Personnel </th>\n");
                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getElaborationPersonnel(), 2)) + " </td>\n");
                    out.write("</tr>\n");
                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Effort% </th>\n");
                    out.write("<td align = 'center' > " + component.getElaborationEffortPercentage() + " </td>\n");
                    out.write("</tr>\n");
                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Schedule% </th>\n");
                    out.write("<td align = 'center' > " + component.getElaborationSchedulePercentage() + " </td>\n");
                    out.write("</tr>\n");

                    out.write("</table>\n");
                    out.write("</td>\n");
                    out.write("<td style = 'font-family:arial'>\n");

                    out.write("<table border = '1' width = '100%' align = 'center'>\n");

                    out.write("<caption> Construction </caption>\n");

                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Effort </th>\n");
                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getConstructionEffort(), 2)) + " </td>\n");
                    out.write("</tr>\n");
                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Month </th>\n");
                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getConstructionMonth(), 2)) + " </td>\n");
                    out.write("</tr>\n");
                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Personnel </th>\n");
                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getConstructionPersonnel(), 2)) + " </td>\n");
                    out.write("</tr>\n");
                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Effort% </th>\n");
                    out.write("<td align = 'center' > " + component.getConstructionEffortPercentage() + " </td>\n");
                    out.write("</tr>\n");
                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Schedule% </th>\n");
                    out.write("<td align = 'center' > " + component.getConstructionSchedulePercentage() + " </td>\n");
                    out.write("</tr>\n");

                    out.write("</table>\n");
                    out.write("</td>\n");
                    out.write("<td style = 'font-family:arial'>\n");

                    out.write("<table border = '1' width = '100%' align = 'center'>\n");

                    out.write("<caption> Transition </caption>\n");

                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Effort </th>\n");
                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getTransitionEffort(), 2)) + " </td>\n");
                    out.write("</tr>\n");
                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Month </th>\n");
                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getTransitionMonth(), 2)) + " </td>\n");
                    out.write("</tr>\n");
                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Personnel </th>\n");
                    out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(component.getTransitionPersonnel(), 2)) + " </td>\n");
                    out.write("</tr>\n");
                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Effort% </th>\n");
                    out.write("<td align = 'center' > " + component.getTransitionEffortPercentage() + " </td>\n");
                    out.write("</tr>\n");
                    out.write("<tr>\n");
                    out.write("<th width = '50%' bgcolor = '#CCCCCC' > Schedule% </th>\n");
                    out.write("<td align = 'center' > " + component.getTransitionSchedulePercentage() + " </td>\n");
                    out.write("</tr>\n");
                    out.write("</table>\n");
                    out.write("</td>\n");
                    out.write("</tr>\n");
                    out.write("</table>\n");

                   

                    out.write("<br />\n");


                    out.write("</div>\n");

                    for (int c = 0; c < orderedSubComponentsVector.size(); c++) {
                        COINCOMOSubComponent subComponent = (COINCOMOSubComponent) orderedSubComponentsVector.get(c);

                        out.write("<div style = 'display:none' id = 'subcomponent" + subComponent.getUnitID() + "_" + subComponent.getName() + "' >\n");
                        out.write("<center><h2> SubComponent : " + subComponent.getName() + " </h2></center>\n");
                        out.write("<table border = '1' width = '90%' align = 'center'>\n");

                        out.append("<tr>");
                        out.append("<th bgcolor = '#CCCCCC'> ID </th>");
                        out.append("<th bgcolor = '#CCCCCC'> Name </th>");
                        out.append("<th bgcolor = '#CCCCCC'> SLOC </th>");
                        out.append("<th bgcolor = '#CCCCCC'> Labor Rate </th>");
                        out.append("<th bgcolor = '#CCCCCC'> EAF </th>");//no language rebecca
                        out.append("<th bgcolor = '#CCCCCC'> Language </th>");
                        out.append("<th bgcolor = '#CCCCCC'> Nominal Effort </th>");
                        out.append("<th bgcolor = '#CCCCCC'> Estimated Effort </th>");
                        out.append("<th bgcolor = '#CCCCCC'> PROD </th>");
                        out.append("<th bgcolor = '#CCCCCC'> Cost </th>");
                        out.append("<th bgcolor = '#CCCCCC'> INST Cost</th>");
                        out.append("<th bgcolor = '#CCCCCC'> Staf </th>");
                        out.append("<th bgcolor = '#CCCCCC'> Risk </th>");
                        out.append("</tr>");

                        out.write("<tr>");
                        out.write("<td align = 'center' > " + subComponent.getUnitID() + " </td>");
                        out.write("<td align = 'center' > " + subComponent.getName() + " </td>");
                        out.write("<td align = 'center' > " + GlobalMethods.FormatLongWithComma(subComponent.getSLOC()) + " </td>");
                        out.write("<td align = 'center' > $" + subComponent.getLaborRate() + " </td>");
                        out.write("<td align = 'center' > " + subComponent.getEAF() + " </td>\n");
                        out.write("<td align = 'center' > " + subComponent.getLanguage() + " </td>\n");
                        out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(subComponent.getNominalEffort(), 2)) + " </td>");
                        out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(subComponent.getEstimatedEffort(), 2)) + " </td>");
                        out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(subComponent.getProductivity(), 2)) + " </td>");
                        out.write("<td align = 'center' > $" + format2Decimals.format(GlobalMethods.roundOff(subComponent.getCost(), 2)) + " </td>");
                        out.write("<td align = 'center' > $" + format2Decimals.format(GlobalMethods.roundOff(subComponent.getInstructionCost(), 2)) + " </td>");
                        out.write("<td align = 'center' > " + format1Decimal.format(GlobalMethods.roundOff(subComponent.getStaff(), 1)) + " </td>");
                        out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(subComponent.getRisk(), 2)) + " </td>");
                        out.write("</tr>");
                        out.write("</table>\n");
                        out.write("<br />\n");


//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > ID </th>\n");
//                        out.write("<td align = 'center' > " + subComponent.getUnitID() + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Name </th>\n");
//                        out.write("<td align = 'center' > " + subComponent.getName() + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > SLOC </th>\n");
//                        out.write("<td align = 'center' > " + GlobalMethods.FormatLongWithComma(subComponent.getSLOC()) + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Labor Rate ($/Month) </th>\n");
//                        out.write("<td align = 'center' > $" + subComponent.getLaborRate() + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > EAF </th>\n");
//                        out.write("<td align = 'center' > " + subComponent.getEAF() + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Language </th>\n");
//                        out.write("<td align = 'center' > " + subComponent.getLanguage() + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Nominal Effort </th>\n");
//                        out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(subComponent.getNominalEffort(), 2)) + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Estimated Effort </th>\n");
//                        out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(subComponent.getEstimatedEffort(), 2)) + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > PROD </th>\n");
//                        out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(subComponent.getProductivity(), 2)) + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Cost </th>\n");
//                        out.write("<td align = 'center' > $" + format2Decimals.format(GlobalMethods.roundOff(subComponent.getCost(), 2)) + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > INST Cost </th>\n");
//                        out.write("<td align = 'center' > $" + format2Decimals.format(GlobalMethods.roundOff(subComponent.getInstructionCost(), 2)) + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Staff </th>\n");
//                        out.write("<td align = 'center' > " + format1Decimal.format(GlobalMethods.roundOff(subComponent.getStaff(), 1)) + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Risk </th>\n");
//                        out.write("<td align = 'center' > " + format2Decimals.format(GlobalMethods.roundOff(subComponent.getRisk(), 2)) + " </td>\n");
//                        out.write("</tr>\n");

                        final COINCOMOConstants.Rating[] eafRatings = subComponent.getEAFRatings();
                        final COINCOMOConstants.Increment[] eafIncrements = subComponent.getEAFIncrements();
                        out.write("<h4 align = 'center'> EAF </h4>\n");
                        out.write("<table border = '0' width = '90%' align = 'center'>\n");

                        out.write("<tr>\n");
                        out.write("<td style = 'font-family:arial'>\n");

                        out.write("<table border = '1' width = '100%' align = 'center'>\n");
                        out.write("<caption> Product </caption>\n");
                        out.write("<tr>\n");
                        out.write("<th  bgcolor = '#CCCCCC' >  </th>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > RELY </th>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > DATA </th>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > DOCU </th>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > CPLX </th>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > RUSE </th>\n");
                        out.write("</tr>\n");
                        out.write("<tr>\n");
                        out.write("<th bgcolor = '#CCCCCC' > Rating </th>\n");
                        for (int l = 0; l < 5; l++) {
                            out.write("<td align = 'center' > " + eafRatings[l].toString() + " </td>\n");
                        }
                        out.write("</tr>\n");
                        out.write("<tr>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > % Incr </th>\n");
                        for (int l = 0; l < 5; l++) {
                            out.write("<td align = 'center' > " + eafIncrements[l].toString() + " </td>\n");
                        }
                        out.write("</tr>\n");
                        out.write("</table>\n");

                        out.write("</td>\n");

                        out.write("<td style = 'font-family:arial'>\n");

                        out.write("<table border = '1' width = '100%' align = 'center'>\n");

                        out.write("<caption> Platform</caption>\n");
                        out.write("<tr>\n");
                        out.write("<th  bgcolor = '#CCCCCC' >  </th>\n");
                        out.write("<th bgcolor = '#CCCCCC' > TIME </th>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > STOR </th>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > PVOL </th>\n");
                        out.write("</tr>\n");
                        out.write("<tr>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > Rating </th>\n");
                        for (int l = 5; l < 8; l++) {
                            out.write("<td align = 'center' > " + eafRatings[l].toString() + " </td>\n");
                        }
                        out.write("</tr>\n");
                        out.write("<tr>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > % Incr </th>\n");
                        for (int l = 5; l < 8; l++) {
                            out.write("<td align = 'center' > " + eafIncrements[l].toString() + " </td>\n");
                        }

                        out.write("</tr>\n");

                        out.write("</table>\n");
                        out.write("</td>\n");
                        out.write("</tr>\n");
                        out.write("</table>\n");
//
                        out.write("<table border = '0' width = '90%' align = 'center'>\n");
                        out.write("<tr>\n");
                        out.write("<td style = 'font-family:arial'>\n");


                        out.write("<table border = '1' width = '100%' align = 'center'>\n");

                        out.write("<caption> Personel </caption>\n");
                        out.write("<tr>\n");
                        out.write("<th  bgcolor = '#CCCCCC' >  </th>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > ACAP </th>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > APEX </th>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > PCAP </th>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > PLEX </th>\n");
                        out.write("<th bgcolor = '#CCCCCC' > LTEX </th>\n");
                        out.write("<th bgcolor = '#CCCCCC' > PCON </th>\n");
                        out.write("</tr>\n");

                        out.write("<tr>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > Rating </th>\n");
                        for (int l = 8; l < 14; l++) {
                            out.write("<td align = 'center' > " + eafRatings[l].toString() + " </td>\n");
                        }
                        out.write("</tr>\n");
                        out.write("<tr>\n");
                        out.write("<th bgcolor = '#CCCCCC' > % Incr </th>\n");
                        for (int l = 8; l < 14; l++) {
                            out.write("<td align = 'center' > " + eafIncrements[l].toString() + " </td>\n");
                        }
                        out.write("</tr>\n");

                        out.write("</table>\n");
                        out.write("</td>\n");
                        out.write("<td style = 'font-family:arial'>\n");

                        out.write("<table border = '1' width = '100%' align = 'center'>\n");

                        out.write("<caption> Project </caption>\n");

                        out.write("<tr>\n");
                        out.write("<th  bgcolor = '#CCCCCC' >  </th>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > TOOL </th>\n");
                        out.write("<th bgcolor = '#CCCCCC' > SITE </th>\n");
                        out.write("</tr>\n");
                        out.write("<tr>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > Rating </th>\n");
                        for (int l = 14; l < 16; l++) {
                            out.write("<td align = 'center' > " + eafRatings[l].toString() + " </td>\n");
                        }
                        out.write("</tr>\n");
                        out.write("<tr>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > % Incr </th>\n");
                        for (int l = 14; l < 16; l++) {
                            out.write("<td align = 'center' > " + eafIncrements[l].toString() + " </td>\n");
                        }
                        out.write("</tr>\n");

                        out.write("</table>\n");
                        out.write("</td>\n");
                        out.write("<td style = 'font-family:arial'>\n");

                        out.write("<table border = '1' width = '100%' align = 'center'>\n");

                        out.write("<caption> User </caption>\n");
                        out.write("<tr>\n");
                        out.write("<th bgcolor = '#CCCCCC' >  </th>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > USR1 </th>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > USR2 </th>\n");
                        out.write("</tr>\n");

                        out.write("<tr>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > Rating </th>\n");
                        for (int l = 16; l <= 17; l++) {
                            out.write("<td align = 'center' > " + eafRatings[l].toString() + " </td>\n");
                        }
                        out.write("</tr>\n");
                        out.write("<tr>\n");
                        out.write("<th  bgcolor = '#CCCCCC' > % Incr </th>\n");
                        for (int l = 16; l <= 17; l++) {
                            out.write("<td align = 'center' > " + eafIncrements[l].toString() + " </td>\n");
                        }
                        out.write("</tr>\n");

                        out.write("</table>\n");
                        out.write("</td>\n");
                        out.write("</tr>\n");
                        out.write("</table>\n");

                        out.write("<br />\n");
                        out.write("<table border = '0' width = '90%' align = 'center'>\n");

                        out.write("<tr>\n");
                        out.write("<td style = 'font-family:arial'>\n");
                        out.write("<h4 align = 'center'> Size </h4>\n");
                        out.write("<table border = '1' width = '100%' align = 'center'>\n");
                        // out.write("<caption> Size </caption>\n");

                        out.append("<tr>");
                        out.append("<th bgcolor = '#CCCCCC'> Breakage (REVL) </th>");
                        //out.append("<th bgcolor = '#CCCCCC'> Language </th>");
                        out.append("<th bgcolor = '#CCCCCC'> New SLOC </th>");
                        out.append("<th bgcolor = '#CCCCCC'> Total SLOC (without REVL) </th>");
                        out.append("</tr>");

                        out.write("<tr>");
                        out.write("<td align = 'center' > " + subComponent.getREVL() + " </td>");
                        //out.write("<td align = 'center' > " + subComponent.getLanguage() + " </td>");
                        out.write("<td align = 'center' > " + GlobalMethods.FormatLongWithComma(subComponent.getNewSLOC()) + " </td>");
                        out.write("<td align = 'center' > $" + GlobalMethods.FormatLongWithComma(subComponent.getSumOfSLOCs()) + " </td>");
                        out.write("</tr>");

                        out.write("</table>\n");
                        out.write("</td>\n");

//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Breakage (REVL) </th>\n");
//                        out.write("<td align = 'center' > " + subComponent.getREVL() + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Language </th>\n");
//                        out.write("<td align = 'center' > " + subComponent.getLanguage() + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Total SLOC (without REVL) </th>\n");
//                        out.write("<td align = 'center' > " + GlobalMethods.FormatLongWithComma(subComponent.getSumOfSLOCs()) + " </td>\n");
//                        out.write("</tr>\n");


//                        out.write("<td style = 'font-family:arial'>\n");
//                        out.write("<table border = '1' width = '90%' align = 'center'>\n");
//                        out.write("<caption> New </caption>\n");
//
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > SLOC </th>\n");
//                        out.write("<td align = 'center' > " + GlobalMethods.FormatLongWithComma(subComponent.getNewSLOC()) + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("</table>\n");
//                        out.write("</td>\n");

                        out.write("<td style = 'font-family:arial'>\n");
                        out.write("<h4 align = 'center'> Function Points </h4>\n");
                        out.write("<table border = '1' width = '100%' align = 'center'>\n");
                        //out.write("<caption> Function Points </caption>\n");

                        final int[] subTotals = subComponent.getSubTotals();

                        out.append("<tr>");
                        out.append("<th bgcolor = '#CCCCCC'> Ratio </th>");
                        out.append("<th bgcolor = '#CCCCCC'> Multiplier </th>");
                        out.append("<th bgcolor = '#CCCCCC'> Calculation Method </th>");
                        out.append("<th bgcolor = '#CCCCCC'> External Inputs </th>");
                        out.append("<th bgcolor = '#CCCCCC'> External Outputs </th>");
                        out.append("<th bgcolor = '#CCCCCC'> Internal Files </th>");
                        out.append("<th bgcolor = '#CCCCCC'> External Files </th>");
                        out.append("<th bgcolor = '#CCCCCC'> External Inquiries </th>");
                        out.append("<th bgcolor = '#CCCCCC'> Total Unadjusted Function Points </th>");
                        out.append("<th bgcolor = '#CCCCCC'> Equivalent SLOC </th>");
                        out.append("</tr>");

                        out.write("<tr>");
                        out.write("<td align = 'center' > " + subComponent.getRatioType() + " </td>");
                        out.write("<td align = 'center' > " + subComponent.getMultiplier() + " </td>");
                        out.write("<td align = 'center' > " + subComponent.getCalculationMethod() + " </td>");
                        out.write("<td align = 'center' > " + subTotals[FP.EI.ordinal()] + " </td>");
                        out.write("<td align = 'center' > " + subTotals[FP.EO.ordinal()] + " </td>");
                        out.write("<td align = 'center' > " + subTotals[FP.ILF.ordinal()] + " </td>");
                        out.write("<td align = 'center' > " + subTotals[FP.EIF.ordinal()] + " </td>");
                        out.write("<td align = 'center' > " + subTotals[FP.EQ.ordinal()] + " </td>");
                        out.write("<td align = 'center' > " + GlobalMethods.FormatLongWithComma(subComponent.getTotalUnadjustedFunctionPoints()) + " </td>");
                        out.write("<td align = 'center' > $" + GlobalMethods.FormatLongWithComma(subComponent.getEquivalentSLOC()) + " </td>");
                        out.write("</tr>");

                        out.write("</table>\n");
                        out.write("</td>\n");
                        out.write("</table>\n");

                        out.write("<br />\n");

                        

//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Ratio </th>\n");
//                        out.write("<td align = 'center' > " + subComponent.getRatioType() + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Multiplier </th>\n");
//                        out.write("<td align = 'center' > " + subComponent.getMultiplier() + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Calculation Method </th>\n");
//                        out.write("<td align = 'center' > " + subComponent.getCalculationMethod() + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > External Inputs </th>\n");
//                        out.write("<td align = 'center' > " + subTotals[FP.EI.ordinal()] + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > External Outputs </th>\n");
//                        out.write("<td align = 'center' > " + subTotals[FP.EO.ordinal()] + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Internal Files </th>\n");
//                        out.write("<td align = 'center' > " + subTotals[FP.ILF.ordinal()] + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > External Files </th>\n");
//                        out.write("<td align = 'center' > " + subTotals[FP.EIF.ordinal()] + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > External Inquiries </th>\n");
//                        out.write("<td align = 'center' > " + subTotals[FP.EQ.ordinal()] + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Total Unadjusted Function Points </th>\n");
//                        out.write("<td align = 'center' > " + GlobalMethods.FormatLongWithComma(subComponent.getTotalUnadjustedFunctionPoints()) + " </td>\n");
//                        out.write("</tr>\n");
//                        out.write("<tr>\n");
//                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Equivalent SLOC </th>\n");
//                        out.write("<td align = 'center' > " + GlobalMethods.FormatLongWithComma(subComponent.getEquivalentSLOC()) + " </td>\n");
//                        out.write("</tr>\n");

                        

                        out.write("<table border = '0' width = '90%' align = 'center'>\n");

                        out.write("<tr>\n");
                        out.write("<td style = 'font-family:arial'>\n");
                        out.write("<h4 align = 'center'> Adaptation and Reuse </h4>\n");
                        out.write("<table border = '1' width = '100%' align = 'center'>\n");
                        //out.write("<caption> Adaptation and Reuse </caption>\n");
                        out.write("<tr>\n");
                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > ID </th>\n");
                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Name </th>\n");
                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Equivalent SLOC </th>\n");
                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Adapted SLOC </th>\n");
                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Design Modified </th>\n");
                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Code Modified </th>\n");
                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Integration Modified </th>\n");
                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Software Understanding </th>\n");
                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Assessment Assimilation </th>\n");
                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Unfamiliarity with Software </th>\n");
                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Automatically Translated </th>\n");
                        out.write("<th width = '30%' bgcolor = '#CCCCCC' > Automatic Translation Productivity </th>\n");
                        out.write("</tr>\n");

                        Iterator iter = subComponent.getListOfSubUnits().iterator();
                        while (iter.hasNext()) {
                            COINCOMOAdaptationAndReuse aAR = (COINCOMOAdaptationAndReuse) iter.next();
                            out.write("<tr>\n");
                            out.write("<td align = 'center' > " + aAR.getUnitID() + " </td>\n");
                            out.write("<td align = 'center' > " + aAR.getName() + " </td>\n");
                            out.write("<td align = 'center' > " + GlobalMethods.FormatLongWithComma(aAR.getEquivalentSLOC()) + " </td>\n");
                            out.write("<td align = 'center' > " + GlobalMethods.FormatLongWithComma(aAR.getAdaptedSLOC()) + " </td>\n");
                            out.write("<td align = 'center' > " + aAR.getDesignModified() + " </td>\n");
                            out.write("<td align = 'center' > " + aAR.getCodeModified() + " </td>\n");
                            out.write("<td align = 'center' > " + aAR.getIntegrationModified() + " </td>\n");
                            out.write("<td align = 'center' > " + aAR.getSoftwareUnderstanding() + " </td>\n");
                            out.write("<td align = 'center' > " + aAR.getAssessmentAndAssimilation() + " </td>\n");
                            out.write("<td align = 'center' > " + aAR.getUnfamiliarityWithSoftware() + " </td>\n");
                            out.write("<td align = 'center' > " + aAR.getAutomaticallyTranslated() + " </td>\n");
                            out.write("<td align = 'center' > " + aAR.getAutomaticTranslationProductivity() + " </td>\n");
                            out.write("</tr>\n");
                        }
                        out.write("</table>\n");
                        out.write("</td>\n");
                        out.write("</tr>\n");
                        out.write("</table>\n");
                        out.write("</div>\n");
                        /*out.write("<th width = '30%' bgcolor = '#CCCCCC' > Code Modified </th>\n");
                         out.write("<td align = 'center' > %" + subComponent.getCodeModified() + " </td>\n");
                         out.write("</tr>\n");
                         out.write("<tr>\n");
                         out.write("<th width = '30%' bgcolor = '#CCCCCC' > Design Modified </th>\n");
                         out.write("<td align = 'center' > %" + subComponent.getDesignModified() + " </td>\n");
                         out.write("</tr>\n");
                         out.write("<tr>\n");
                         out.write("<th width = '30%' bgcolor = '#CCCCCC' > Integration Modified </th>\n");
                         out.write("<td align = 'center' > %" + subComponent.getIntegrationModified() + " </td>\n");
                         out.write("</tr>\n");
                         out.write("<tr>\n");
                         out.write("<th width = '30%' bgcolor = '#CCCCCC' > Software Understanding </th>\n");
                         out.write("<td align = 'center' > " + subComponent.getSoftwareUnderstanding() + " </td>\n");
                         out.write("</tr>\n");
                         out.write("<tr>\n");
                         out.write("<th width = '30%' bgcolor = '#CCCCCC' > Unfamiliarity </th>\n");
                         out.write("<td align = 'center' > " + subComponent.getUnfamiliarity() + " </td>\n");
                         out.write("</tr>\n")*/;
                    }
                }
            }

            out.write("</div>\n");

            out.write("</td>\n");
            out.write("</tr>\n");
            out.write("</table>\n");

            out.write("<center><h6> University of Southern California, All Rights Reserved. </h6></center>\n");

            out.write("</body>\n");

            out.write("</html>\n");

            out.flush();

            // Free Resources ..
            out.close();

            GlobalMethods.updateStatusBar("Exporting had Finished.", this.coincomo);

        } catch (IOException e) {
            //e.printStackTrace();
            JOptionPane.showMessageDialog(this.coincomo, "File couldn't be exported to the selected location. " + e.getMessage(), "EXPORT ERROR", 0);
        } finally {
            // Go Back to Default
            setJavaUI();
        }

    }

    public void exportProjectAsXML() {
        if (coincomo.getCurrentSystem() == null) {
            //GlobalMethods.updateStatusBar("No System has been Opened to read from", Color.RED, this.coincomo);
            JOptionPane.showMessageDialog(this.coincomo, "No System has been Opened to read from", "EXPORT ERROR", 0);
            return;
        }

        try {

            // For Example, Windows OS's UI
            //setOperatingSystemUI();

            // Has to be an XML Document
            String validExt[] = {"xml"};
            File f;

            while (true) {
                f = GlobalMethods.getFile(coincomo, "*." + validExt[0], validExt, "Save", true);
                //user cancelled save
                if (f == null) {
                    return;
                }
                //if / was added to the filename
                if (rootMap.contains(f.getParent())) {
                    int ret = JOptionPane.showConfirmDialog(this.coincomo, "File is being exported to the root directory. Click yes to continue.", "EXPORT WARNING", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (ret == 1) {
                        return;
                    } else {
                    }
                }
                if (f.exists()) {
                    //Confirm overwrite?
                    if (JOptionPane.showConfirmDialog(null, "Replace existing file?", "Existing file", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        //Over-write confirmed
                        break;
                    } else {
                        //Cancel over-write, get a different file
                        f = null;
                    }
                } else {
                    //User enetered new file, so break
                    //System.out.println(f.getParent());
                    break;
                }

            }

            COINCOMOSystemManager.exportSystemAsXML((COINCOMOSystem) coincomo.getCurrentSystem(), f);

            GlobalMethods.updateStatusBar("Exporting has Finished.", this.coincomo);
            // Go Back to Default
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this.coincomo, "Exception occured." + e.getMessage(), "EXPORT ERROR", 0);
        } finally {
            setJavaUI();
        }
    }

    public void importProject() {
        try {
            JFileChooser fc = new JFileChooser();

            // For Example, Windows OS's UI
            //setOperatingSystemUI();
            fc.setFileView(new IconFileView(new String[]{COINCOMO.EXTENSION}, false));
            fc.updateUI();

            fc.addChoosableFileFilter(new cetFilter());

            GlobalMethods.updateStatusBar("Done.", coincomo);

            // Show open dialog; this method does not return until the dialog is closed
            int result = fc.showOpenDialog(coincomo);

            if (result == JFileChooser.APPROVE_OPTION) {
                // Has to be an XML Document
                File file = fc.getSelectedFile();

                //Validate the XML file.
                if (COINCOMOXML.validateXML(file)) {
                    COINCOMOSystem system = COINCOMOXML.importXML(file);

                    if (system == null) {
                        JOptionPane.showMessageDialog(null, "Unable to import supplied CET file to the database.");
                    } else {
                        JOptionPane.showMessageDialog(null, "System '" + system.getName() + "' has been imported to database.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Supplied CET file is not valid.", "CET FILE INVALID ERROR", 0);
                }
            }
        } catch (Exception e) {
            log(Level.WARNING, e.getLocalizedMessage());
        } finally {
            // Go Back to Default
            setJavaUI();
        }
    }

    public void synchronizeProject() {
        try {
            JFileChooser fc = new JFileChooser();

            // For Example, Windows OS's UI
            //setOperatingSystemUI();
            fc.setFileView(new IconFileView(new String[]{COINCOMO.EXTENSION}, false));
            fc.updateUI();

            fc.addChoosableFileFilter(new cetFilter());

            GlobalMethods.updateStatusBar("Done.", coincomo);

            // Show open dialog; this method does not return until the dialog is closed
            int result = fc.showOpenDialog(coincomo);

            if (result == JFileChooser.APPROVE_OPTION) {
                // Has to be an XML Document
                File file = fc.getSelectedFile();

                //Validate the XML file.
                if (COINCOMOXML.validateXML(file)) {
                    //Temporary setting the operation mode flag to desktop mode to avoid importing the XML into the database
                    COINCOMOSystem system = COINCOMOXML.synchronizeXML(file);

                    if (system == null) {
                        // Unable to synchronize a valid CET file, due to the fact that the project is most likely opened in COINCOMO currently.
                        // Might be other factors in the synchronizeXML() function though.
                        JOptionPane.showMessageDialog(null, "Unable to synchronize supplied CET file with the database.");
                    } else {
                        // Successfully synchronized. Now we need to check against the project being opened already.
                        JOptionPane.showMessageDialog(null, "System '" + system.getName() + "' has been synchronized with the database.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Supplied CET file is not valid.", "CET FILE INVALID ERROR", 0);
                }
            }
        } catch (Exception e) {
            log(Level.WARNING, e.getLocalizedMessage());
        } finally {
            // Go Back to Default
            setJavaUI();
        }
    }

    public void showAboutUsDialog() {
        new AboutUsDialog(coincomo);
    }

    public void showManual() {
        try {
            File manualFile = new File("manual/manual.html");
            Desktop.getDesktop().browse(manualFile.toURI());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(coincomo, "Unable to open manual.", "MANUAL OPEN ERROR", 0);
        }
    }

    //TO DO refresh GUI calculated values
    public void showEAFDialog() {
        if (this.coincomo.getCurrentSystem() == null) {
            return;
        }
        new EAFDialog(coincomo);
    }

    //TO DO refresh GUI calculated values
    public void showScaleFactorsDialog() {
        if (this.coincomo.getCurrentSystem() == null) {
            return;
        }
        new ScaleFactorsDialog(coincomo);
    }

    //TO DO refresh GUI calculated values
    public void showEquationEditorDialog() {
        if (this.coincomo.getCurrentSystem() == null) {
            return;
        }
        new EquationEditorDialog(coincomo);
    }

    //TO DO refresh GUI calculated values
    public void showFunctionPointsDialog() {
        if (this.coincomo.getCurrentSystem() == null) {
            return;
        }
        new FunctionPointsDialog(coincomo);
    }

    //TO DO refresh GUI calculated values
    public void showPersonMonthDialog() {
        if (this.coincomo.getCurrentSystem() == null) {
            return;
        }

        COINCOMOUnit unit = this.coincomo.getCurrentUnit();
        if (unit instanceof COINCOMOComponent) {
            COINCOMOComponent component = (COINCOMOComponent) unit;
            hoursPerPersonMonth = (String) JOptionPane.showInputDialog(coincomo, "Please Enter Hours/Person Month:", component.getParameters().getWorkHours());
            try {
                if (hoursPerPersonMonth != null) {
                    //System.out.println("Work Hours " + hoursPerPersonMonth);                
                    hours = Double.parseDouble(hoursPerPersonMonth);
                    if (hours >= 120.0 && hours <= 184.0) {

                        component.getParameters().setWorkHours(hours);
                        COINCOMOComponentManager.updateComponent(component, true);

                        coincomo.refresh();
                        // System.out.println("Work hours" + component.getWorkHours());

                    } else {
                        JOptionPane.showMessageDialog(null, "Enter value between 120 and 184",
                                "warning", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } catch (Exception e) {
                if (hoursPerPersonMonth == null) {
                    JOptionPane.showMessageDialog(coincomo, "No Value Entered.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(coincomo, "Enter Numeric Value.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                return;
            }
        } else {
            log(Level.SEVERE, "shoePersonMonth() gets called when the current unit selected is not of type COINCOMOComponent! The type is: " + unit.getClass().getName());
        }

        //component.setWorkHours(hours);
    }

    // -----------------------------------------------
    // Private Methods ...
    private void setOperatingSystemUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setJavaUI() {
        try {
            UIManager.setLookAndFeel(defaultUI);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*private void setTitleBar(COINCOMO coincomo, int status) {
     //tokenize file
     /*   String title = null;
     String waste = null;
     String file1 = String.valueOf(file); 
        
     int dotindex = file1.lastIndexOf("\\.");
     waste = file1.substring(0,dotindex);
        
     int slashindex = file1.lastIndexOf("\\");
     title = file1.substring(slashindex+1,dotindex);
     */
    /*     if(status == 1)
     coincomo.setTitle("USC COINCOMO 2.0 - "+ file);
     else
     coincomo.setTitle("USC COINCOMO 2.0- Untitled");
     }*/
    private static void log(Level level, String message) {
        Logger.getLogger(MenuItemMethods.class.getName()).log(level, message);
    }
}
