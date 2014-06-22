/*
 * Guanghui Chen Working on
 * Used to import CSV file
 */
package main;

import core.COINCOMOAdaptationAndReuse;
import core.COINCOMOComponent;
import core.COINCOMOComponentParameters;
import core.COINCOMOConstants.CalculationMethod;
import core.COINCOMOConstants.RatioType;
import core.COINCOMOSubComponent;
import core.COINCOMOSubSystem;
import core.COINCOMOSystem;
import database.COINCOMOAdaptationAndReuseManager;
import database.COINCOMOComponentManager;
import database.COINCOMOSubComponentManager;
import database.COINCOMOSubSystemManager;
import database.COINCOMOSystemManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Guanghui Chen
 */
public class devImport {

    static String line;
    static String lineString = null;

    static COINCOMOSystem menuImportItemMethod() {
        File f = new File("test1.csv");
        parseCSV(f);
        return null;
        //return devImport.importSCV(f);
    }

    static boolean blank_line(String line) {
        for (int i = 0; i < line.length(); ++i) {
            if (!Character.isWhitespace(line.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    static void parseCSV(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            int lineCounter = 0;

            lineString = br.readLine();
            lineCounter++;
            if (lineString == null || !lineString.trim().toUpperCase().equals("COINCOMO")) {
                sb.append("Expecting 'COINCOMO' on line " + lineCounter + "', found '" + lineString);
                sb.append("\n");
            }
            
            lineString = br.readLine();
            lineCounter++;
            if (lineString == null || !lineString.trim().equals("")) {
                sb.append("Expecting empty line on line " + lineCounter + "', found '" + lineString);
                sb.append("\n");
            }
           
            while ((line = br.readLine()) != null) {
                lineCounter++;
                if (lineString == null || !lineString.trim().equals("System")) {
                    sb.append("Expecting 'System' on line " + lineCounter + "', found '" + lineString);
                    sb.append("\n");
                }
            }
            
            System.out.println("Error Log: \b" + sb.toString());
        } catch (FileNotFoundException ex) {
            // do nothing
        } catch (IOException ex) {
            // do nothing
        }
    }
    
    static COINCOMOSystem importSCV(File file) {
        COINCOMOSystem system = null;
        try {

            BufferedReader br = new BufferedReader(new FileReader(file));
            String delim = ",";
            //String line = null;
            String[] dataArr;
            br.readLine();
            br.readLine();
            br.readLine();
            br.readLine();
            line = br.readLine();
            dataArr = line.split(delim, 5);
            if (COINCOMOSystemManager.hasSystemName(dataArr[1])) {
                br.close();
                return null;
            } else {
                system = COINCOMOSystemManager.insertSystem();
                system.setName(dataArr[1].trim());
            }
            br.readLine();
            line = br.readLine();

            devImport.populateCSV_System(br, system, "");
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(devImport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return system;
    }

    static void populateCSV_System(BufferedReader br, COINCOMOSystem system, String version) throws IOException {
        String delim = ",";
        //String line;
        int cnt = 0;
        String[] dataArr;

        while (line != null) {
            if (devImport.blank_line(line)) {
                line = br.readLine();
                continue;
            }
            dataArr = line.split(delim, 6);
            if (dataArr.length < 2 || dataArr[1].indexOf("SubSystem", 0) == -1) {
                if (cnt == 0) {
                    //System.out.println("No subsystem found, structure invalid.");
                }
                break;
            }
            br.readLine();
            /*if (line == null || devImport.blank_line(line)) {
             break;
             }*/
            line = br.readLine();
            COINCOMOSubSystem subsys = COINCOMOSubSystemManager.insertSubSystem(system);
            system.addSubUnit(subsys);
            dataArr = line.split(delim, 6);
            subsys.setName(dataArr[2].trim());
            br.readLine();
            line = br.readLine();
            devImport.populateCSV_SubSystem(br, subsys, "");
            cnt++;
        }
        COINCOMOSystemManager.updateSystem(system);
    }

    static void populateCSV_SubSystem(BufferedReader br, COINCOMOSubSystem subsys, String version) throws IOException {
        String delim = ",";
        //String line = null;
        String[] dataArr = null;
        int cnt = 0;
        while (line != null) {
            if (devImport.blank_line(line)) {
                line = br.readLine();
                continue;
            }
            dataArr = line.split(delim, 6);
            if (dataArr.length < 3 || dataArr[2].indexOf("Component", 0) == -1) {
                if (cnt == 0) {
                    //System.out.println("No components found.");
                }
                break;
            }
            br.readLine();
            line = br.readLine();
            COINCOMOComponent comp = COINCOMOComponentManager.insertComponent(subsys);
            dataArr = line.split(delim, 15);
            comp.setName(dataArr[3]);
            COINCOMOComponentParameters parameters = comp.getParameters();
            parameters.setWorkHours(Double.parseDouble(dataArr[14]));
            devImport.populateCSV_Component(br, comp, version);
            cnt++;
        }
        COINCOMOSubSystemManager.updateSubSystem(subsys, false);
    }

    static void populateCSV_Component(BufferedReader br, COINCOMOComponent comp, String version) throws IOException {
        String delim = ",";
        //String line = null;
        String[] dataArr = null;

        //Inception
        br.readLine();
        br.readLine();
        line = br.readLine();

        //Elaboration
        br.readLine();
        br.readLine();
        line = br.readLine();

        //Construction
        br.readLine();
        br.readLine();
        line = br.readLine();

        //Transition
        br.readLine();
        br.readLine();
        line = br.readLine();

        int cnt = 0;
        line = br.readLine();
        while (line != null) {
            if (devImport.blank_line(line)) {
                line = br.readLine();
                continue;
            }
            dataArr = line.split(delim, 4);
            if (dataArr.length < 4 || dataArr[3].indexOf("SubComponent", 0) == -1) {
                if (cnt == 0) {
                    //System.out.println("No subcomponents found.");
                }
                break;
            }
            br.readLine();
            line = br.readLine();
            dataArr = line.split(delim, 16);
            COINCOMOSubComponent subcomp = COINCOMOSubComponentManager.insertSubComponent(comp);
            subcomp.setName(dataArr[4]);
            subcomp.setNewSLOC(Long.parseLong(dataArr[6].trim()));
            subcomp.setLaborRate(Double.parseDouble(dataArr[7].trim()));
            subcomp.setEAF(Double.parseDouble(dataArr[8].trim()));
            subcomp.setNominalEffort(Double.parseDouble(dataArr[9].trim()));
            subcomp.setEstimatedEffort(Double.parseDouble(dataArr[10].trim()));
            subcomp.setProductivity(Double.parseDouble(dataArr[11].trim()));
            subcomp.setCost(Double.parseDouble(dataArr[12].trim()));
            subcomp.setInstructionCost(Double.parseDouble(dataArr[13].trim()));
            subcomp.setStaff(Double.parseDouble(dataArr[14].trim()));
            subcomp.setRisk(Double.parseDouble(dataArr[15].trim()));
            subcomp.setLanguage(dataArr[5].trim());
            br.readLine();
            devImport.populateCSV_SubComponent(br, subcomp, version);
            cnt++;
        }
        COINCOMOComponentManager.updateComponent(comp, false);
    }

    static void populateCSV_SubComponent(BufferedReader br, COINCOMOSubComponent subcomp, String version)
            throws IOException {
        String delim = ",";
        //String line = null;
        String[] dataArr = null;

        //REVL
        br.readLine();
        line = br.readLine();
        dataArr = line.split(delim, 8);
        subcomp.setREVL(Double.parseDouble(dataArr[5]));

        br.readLine();

        //New SLOC
        br.readLine();
        line = br.readLine();
        dataArr = line.split(delim, 7);
        //subcomp.setNewSLOC(Long.parseLong(dataArr[6]));

        br.readLine();

        //Function Points
        br.readLine();
        line = br.readLine();
        dataArr = line.split(delim, 16);
        //subcomp.setRatioType(RatioType.valueOf(dataArr[6]));
        //subcomp.setMultiplier(Integer.parseInt(dataArr[7]));
        //subcomp.setCalculationMethod(CalculationMethod.valueOf(dataArr[8]));

        br.readLine();

        line = br.readLine();
        while (line != null) {
            if (devImport.blank_line(line)) {
                line = br.readLine();
                continue;
            }
            if (!devImport.blank_line(dataArr[0])
                    || !devImport.blank_line(dataArr[1])
                    || !devImport.blank_line(dataArr[2])
                    || !devImport.blank_line(dataArr[3])) {
                break;
            }
            //Adaptation and Reuse     
            dataArr = line.split(delim, 17);
            COINCOMOAdaptationAndReuse a = COINCOMOAdaptationAndReuseManager.insertAdaptationAndReuse(subcomp);
            a.setName(dataArr[6]);
            a.setAdaptedSLOC(Long.parseLong(dataArr[7]));
            a.setDesignModified(Double.parseDouble(dataArr[8]));
            a.setCodeModified(Double.parseDouble(dataArr[9]));
            a.setIntegrationModified(Double.parseDouble(dataArr[10]));
            a.setSoftwareUnderstanding(Double.parseDouble(dataArr[11]));
            a.setAssessmentAndAssimilation(Double.parseDouble(dataArr[12]));
            a.setUnfamiliarityWithSoftware(Double.parseDouble(dataArr[13]));
            a.setAutomaticallyTranslated(Double.parseDouble(dataArr[14]));
            a.setAutomaticTranslationProductivity(Double.parseDouble(dataArr[15]));
            a.setEquivalentSLOC(Long.parseLong(dataArr[16]));
            devImport.populateCSV_AdaptationAndReuse(br, a, version);
        }
        COINCOMOSubComponentManager.updateSubComponent(subcomp, false);
    }

    static void populateCSV_AdaptationAndReuse(BufferedReader br, COINCOMOAdaptationAndReuse ar, String version)
            throws IOException {
        COINCOMOAdaptationAndReuseManager.updateAdaptationAndReuse(ar, false);
        line = br.readLine();
    }
}
