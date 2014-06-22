/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package main;

import core.COINCOMOAdaptationAndReuse;
import core.COINCOMOComponent;
import core.COINCOMOComponentParameters;
import core.COINCOMOConstants;
import core.COINCOMOConstants.CalculationMethod;
import core.COINCOMOConstants.Increment;
import core.COINCOMOConstants.OperationMode;
import core.COINCOMOConstants.Rating;
import core.COINCOMOConstants.RatioType;
import core.COINCOMOLocalCalibration;
import core.COINCOMOLocalCalibrationProject;
import core.COINCOMOSubComponent;
import core.COINCOMOSubSystem;
import core.COINCOMOSystem;
import core.COINCOMOUnit;
import database.COINCOMOAdaptationAndReuseManager;
import database.COINCOMOComponentManager;
import database.COINCOMOSubComponentManager;
import database.COINCOMOSubSystemManager;
import database.COINCOMOSystemManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Larry Chen
 */
public class COINCOMOXML {

    public static String DEFAULT_XML_VERSION = "2.0";
    public static String DEFAULT_XML_SCHEMA = "COINCOMO2.0.xsd";
    public static String DEFAULT_CALIBRATION_XML_VERSION = "2.0";
    public static String DEFAULT_CALIBRATION_XML_SCHEMA = "COINCOMOCalibration2.0.xsd";
    //public static String LEGACY_XML_VERSION = "";
    //public static String LEGACY_XML_SCHEMA = "COINCOMOLegacy.xsd";
    private static final String PRODUCT[] = {"RELY", "DATA", "DOCU", "CPLX", "RUSE"};
    private static final String PLATFORM[] = {"TIME", "STOR", "PVOL"};
    private static final String PERSONNEL[] = {"ACAP", "APEX", "PCAP", "PLEX", "LTEX", "PCON"};
    private static final String PROJECT[] = {"TOOL", "SCED", "SITE"};
    private static final String USER_DEFINED[] = {"USR1", "USR2"};
    private static final String SCALE_FACTORS[] = {"PREC", "FLEX", "RESL", "TEAM", "PMAT"};
    // Legacy XML constants
    private static final String EAF[] = {"VLO", "LO", "NOM", "HI", "VHI", "XHI"};
    private static final String FUNCTION[] = {"Low", "Average", "High"};
    private static final String FUNCTION_POINTS_OLD[] = {"iLogicFiles", "eLogicFiles", "eInputs", "eOutputs", "eInquiries"};

    public static Document parseXML(File file) {
        DocumentBuilderFactory dbf = null;
        DocumentBuilder db = null;
        Document doc = null;

        try {
            // Prepare the XML document.
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            doc = db.parse(file);
            doc.getDocumentElement().normalize();
        } catch (ParserConfigurationException e) {
            log(Level.SEVERE,
                    "parseXML().ParserConfigurationException: " + e.getLocalizedMessage());
            doc = null;
        } catch (SAXException e) {
            log(Level.SEVERE,
                    "parseXML().SAXException: " + e.getLocalizedMessage());
            doc = null;
        } catch (IOException e) {
            log(Level.SEVERE,
                    "parseXML().IOException: " + e.getLocalizedMessage());
            doc = null;
        }

        return doc;
    }

    public static boolean validateXML(File file) {
        final String programDir = System.getProperty("user.dir");
        final String schemaPath = programDir + File.separator + DEFAULT_XML_SCHEMA;

        try {
            // Prepare the XML document.
            Document doc = parseXML(file);

            if (doc != null) {
                // Check the XML document to verify it is indeed COINCOMO file.
                Element rootElement = doc.getDocumentElement();
                if (rootElement.getNodeName().equals("COINCOMO")) {
                    /* 
                     * Check the root element to see if there is a version attribute
                     * to indicate new XML format and proceed with new parsing functions.
                     */
                    if (rootElement.hasAttribute("version")) {
                        // "version" attribute exists, thus 2.0 or newer XML format.
                        String rootVersion = rootElement.getAttribute("version");

                        // For now there is only one new XML format with version="2.0" tag.
                        if (rootVersion.equals(DEFAULT_XML_VERSION)) {
                            try {
                                File schemaFile = new File(schemaPath);
                                Source xmlFile = new StreamSource(file);

                                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                                Schema schema = schemaFactory.newSchema(schemaFile);

                                Validator xmlValidator = schema.newValidator();

                                // Validate XML file against XML schema.
                                xmlValidator.validate(xmlFile);

                                log(Level.INFO,
                                        "COINCOMO XML file <" + file.getCanonicalPath() + "> is validated against <" + DEFAULT_XML_SCHEMA + ">.");

                                // No exception is thrown, thus XML file is valid.
                                return true;
                            } catch (SAXException e) {
                                log(Level.WARNING,
                                        "COINCOMO XML file <" + file.getCanonicalPath() + "> is not valid against <" + DEFAULT_XML_SCHEMA + ">." + "\r\n"
                                        + "\r\n"
                                        + e.getLocalizedMessage());
                            }
                        } else {
                            log(Level.WARNING,
                                    "COINCOMO XML file <" + file.getCanonicalPath() + "> version is not supported.");
                        }
                    } else {
                        // "version" attribute doesn't exist, thus legacy XML format, and assume valid.
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            log(Level.SEVERE,
                    "validateXML().IOException: " + e.getLocalizedMessage());
        }

        return false;
    }

    public static void exportXML(COINCOMOSystem system, File file) throws IOException {
        // Write out the new XML format
        String newLine = "\r\n";
        String systemTabs = "\t";
        String subSystemTabs = "\t\t";
        String componentTabs = "\t\t\t";
        String subComponentTabs = "\t\t\t\t";
        String adaptationTabs = "\t\t\t\t\t";

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        
        DecimalFormat format1Decimal = new DecimalFormat("0.0", symbols);
        DecimalFormat format2Decimals = new DecimalFormat("0.00", symbols);

        BufferedWriter out = new BufferedWriter(new FileWriter(file));

        // COINCOMO level
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + newLine);
        out.write(newLine);
        out.write("<COINCOMO version=\"2.0\"" + newLine);
        out.write("          xmlns=\"http://csse.usc.edu\"" + newLine);
        out.write("          xmlns:nsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + newLine);
        out.write("          nsi:schemaLocation=\"http://csse.usc.edu COINCOMO2.0.xsd\">" + newLine);

        // System level
        out.write(systemTabs + "<System>" + newLine);
        out.write(systemTabs + "\t" + "<ID>" + system.getUnitID() + "</ID>" + newLine);
        out.write(systemTabs + "\t" + "<DatabaseID>" + system.getDatabaseID() + "</DatabaseID>" + newLine);
        out.write(systemTabs + "\t" + "<_Name>" + system.getName() + "</_Name>" + newLine);
        out.write(systemTabs + "\t" + "<SLOC>" + GlobalMethods.FormatLongWithComma(system.getSLOC()) + "</SLOC>" + newLine);
        out.write(systemTabs + "\t" + "<Cost>" + format2Decimals.format(GlobalMethods.roundOff(system.getCost(), 2)) + "</Cost>" + newLine);
        out.write(systemTabs + "\t" + "<Staff>" + format1Decimal.format(GlobalMethods.roundOff(system.getStaff(), 1)) + "</Staff>" + newLine);
        out.write(systemTabs + "\t" + "<Effort>" + format2Decimals.format(GlobalMethods.roundOff(system.getEffort(), 2)) + "</Effort>" + newLine);
        out.write(systemTabs + "\t" + "<Schedule>" + format2Decimals.format(GlobalMethods.roundOff(system.getSchedule(), 2)) + "</Schedule>" + newLine);

        // <SubSystem> sections
        ArrayList<COINCOMOUnit> orderedSubSystemsVector = system.getListOfSubUnits();
        for (int i = 0; i < orderedSubSystemsVector.size(); i++) {
            // Sub System level
            final COINCOMOSubSystem subSystem = (COINCOMOSubSystem) orderedSubSystemsVector.get(i);

            out.write(subSystemTabs + "<SubSystem>" + newLine);

            out.write(subSystemTabs + "\t" + "<ID>" + subSystem.getUnitID() + "</ID>" + newLine);
            out.write(subSystemTabs + "\t" + "<DatabaseID>" + subSystem.getDatabaseID() + "</DatabaseID>" + newLine);
            out.write(subSystemTabs + "\t" + "<_Name>" + subSystem.getName() + "</_Name>" + newLine);
            out.write(subSystemTabs + "\t" + "<SLOC>" + GlobalMethods.FormatLongWithComma(subSystem.getSLOC()) + "</SLOC>" + newLine);
            out.write(subSystemTabs + "\t" + "<Cost>" + format2Decimals.format(GlobalMethods.roundOff(subSystem.getCost(), 2)) + "</Cost>" + newLine);
            out.write(subSystemTabs + "\t" + "<Staff>" + format1Decimal.format(GlobalMethods.roundOff(subSystem.getStaff(), 1)) + "</Staff>" + newLine);
            out.write(subSystemTabs + "\t" + "<Effort>" + format2Decimals.format(GlobalMethods.roundOff(subSystem.getEffort(), 2)) + "</Effort>" + newLine);
            out.write(subSystemTabs + "\t" + "<Schedule>" + format2Decimals.format(GlobalMethods.roundOff(subSystem.getSchedule(), 2)) + "</Schedule>" + newLine);
            out.write(subSystemTabs + "\t" + "<_ZoomLevel>" + subSystem.getZoomLevel() + "</_ZoomLevel>" + newLine);

            // <Component> sections
            ArrayList<COINCOMOUnit> orderedComponentsVector = subSystem.getListOfSubUnits();
            for (int j = 0; j < orderedComponentsVector.size(); j++) {
                // Component level
                final COINCOMOComponent component = (COINCOMOComponent) orderedComponentsVector.get(j);

                out.write(componentTabs + "<Component>" + newLine);

                out.write(componentTabs + "\t" + "<ID>" + component.getUnitID() + "</ID>" + newLine);
                out.write(componentTabs + "\t" + "<DatabaseID>" + component.getDatabaseID() + "</DatabaseID>" + newLine);
                out.write(componentTabs + "\t" + "<_Name>" + component.getName() + "</_Name>" + newLine);
                out.write(componentTabs + "\t" + "<SLOC>" + GlobalMethods.FormatLongWithComma(component.getSLOC()) + "</SLOC>" + newLine);
                out.write(componentTabs + "\t" + "<Cost>" + format2Decimals.format(GlobalMethods.roundOff(component.getCost(), 2)) + "</Cost>" + newLine);
                out.write(componentTabs + "\t" + "<Staff>" + format1Decimal.format(GlobalMethods.roundOff(component.getStaff(), 1)) + "</Staff>" + newLine);
                out.write(componentTabs + "\t" + "<Effort>" + format2Decimals.format(GlobalMethods.roundOff(component.getEffort(), 2)) + "</Effort>" + newLine);
                out.write(componentTabs + "\t" + "<Schedule>" + format2Decimals.format(GlobalMethods.roundOff(component.getSchedule(), 2)) + "</Schedule>" + newLine);
                out.write(componentTabs + "\t" + "<Productivity>" + format2Decimals.format(GlobalMethods.roundOff(COINCOMOComponentManager.calculateProductivity(component), 2)) + "</Productivity>" + newLine);
                out.write(componentTabs + "\t" + "<InstructionCost>" + format2Decimals.format(GlobalMethods.roundOff(COINCOMOComponentManager.calculateInstructionCost(component), 2)) + "</InstructionCost>" + newLine);
                out.write(componentTabs + "\t" + "<Risk>" + "0" + "</Risk>" + newLine);

                // Component level, Parameters (i.e. Local Calibration)
                COINCOMOComponentParameters parameters = component.getParameters();

                if (parameters == null) {
                    log(Level.SEVERE, "COINCOMOComponent " + component.getName() + " has no associated COINCOMOComponentParameters object!");
                    parameters = new COINCOMOComponentParameters(component);
                }
                final double[][] eafWeights = parameters.getEAFWeights();
                final double[][] sfWeights = parameters.getSFWeights();
                final int[][] fpWeights = parameters.getFPWeights();

                out.write(componentTabs + "\t" + "<ParametersSettings>" + newLine);

                // Effort Adjustment Factors section
                out.write(componentTabs + "\t\t" + "<EffortAdjustmentFactors>" + newLine);

                for (int k = 0; k < COINCOMOConstants.EAFS.length; k++) {
                    out.write(componentTabs + "\t\t\t" + "<_" + COINCOMOConstants.EAFS[k] + ">" + newLine);
                    for (int l = 0; l < COINCOMOConstants.Ratings.length; l++) {
                        out.write(componentTabs + "\t\t\t\t" + "<_" + COINCOMOConstants.Ratings[l] + ">" + eafWeights[k][l] + "</_" + COINCOMOConstants.Ratings[l] + ">" + newLine);
                    }
                    out.write(componentTabs + "\t\t\t" + "</_" + COINCOMOConstants.EAFS[k] + ">" + newLine);
                }

                out.write(componentTabs + "\t\t" + "</EffortAdjustmentFactors>" + newLine);

                // Scale Factors section
                out.write(componentTabs + "\t\t" + "<ScaleFactors>" + newLine);

                for (int k = 0; k < COINCOMOConstants.SFS.length; k++) {
                    out.write(componentTabs + "\t\t\t" + "<_" + COINCOMOConstants.SFS[k] + ">" + newLine);
                    for (int l = 0; l < COINCOMOConstants.Ratings.length; l++) {
                        out.write(componentTabs + "\t\t\t\t" + "<_" + COINCOMOConstants.Ratings[l] + ">" + sfWeights[k][l] + "</_" + COINCOMOConstants.Ratings[l] + ">" + newLine);
                    }
                    out.write(componentTabs + "\t\t\t" + "</_" + COINCOMOConstants.SFS[k] + ">" + newLine);
                }

                out.write(componentTabs + "\t\t" + "</ScaleFactors>" + newLine);

                // Equation Editors section
                out.write(componentTabs + "\t\t" + "<EquationEditor>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<_EffortEstimationParameterA>" + parameters.getA() + "</_EffortEstimationParameterA>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<_ExponentParameterB>" + parameters.getB() + "</_ExponentParameterB>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<_ScheduleEstimationParameterC>" + parameters.getC() + "</_ScheduleEstimationParameterC>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<_ScheduleEstimationParameterD>" + parameters.getD() + "</_ScheduleEstimationParameterD>" + newLine);
                out.write(componentTabs + "\t\t" + "</EquationEditor>" + newLine);

                // Function Points section
                out.write(componentTabs + "\t\t" + "<FunctionPoints>" + newLine);

                for (int k = 0; k < COINCOMOConstants.FPS2.length; k++) {
                    out.write(componentTabs + "\t\t\t" + "<_" + COINCOMOConstants.FPS2[k] + ">" + newLine);
                    for (int l = 0; l < COINCOMOConstants.FTS.length - 1; l++) {
                        out.write(componentTabs + "\t\t\t\t" + "<_" + COINCOMOConstants.FTS[l] + ">" + fpWeights[k][l] + "</_" + COINCOMOConstants.FTS[l] + ">" + newLine);
                    }
                    out.write(componentTabs + "\t\t\t" + "</_" + COINCOMOConstants.FPS2[k] + ">" + newLine);
                }

                out.write(componentTabs + "\t\t" + "</FunctionPoints>" + newLine);

                // Person Month section
                out.write(componentTabs + "\t\t" + "<PersonMonth>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<_HoursPerPM>" + parameters.getWorkHours() + "</_HoursPerPM>" + newLine);
                out.write(componentTabs + "\t\t" + "</PersonMonth>" + newLine);

                out.write(componentTabs + "\t" + "</ParametersSettings>" + newLine);

                // Scale Factors Settings section
                out.write(componentTabs + "\t" + "<ScaleFactor>" + component.getSF() + "</ScaleFactor>" + newLine);

                final Rating sfRatings[] = component.getSFRatings();
                final Increment sfIncrements[] = component.getSFIncrements();

                out.write(componentTabs + "\t" + "<ScaleFactorsSettings>" + newLine);
                for (int k = 0; k < COINCOMOConstants.SFS.length; k++) {
                    out.write(componentTabs + "\t\t" + "<_" + COINCOMOConstants.SFS[k] + ">" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<_Rating>" + sfRatings[k].toString() + "</_Rating>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<_Increment>" + sfIncrements[k].toString() + "</_Increment>" + newLine);
                    out.write(componentTabs + "\t\t" + "</_" + COINCOMOConstants.SFS[k] + ">" + newLine);
                }
                out.write(componentTabs + "\t" + "</ScaleFactorsSettings>" + newLine);

                // Schedule Settings section
                out.write(componentTabs + "\t" + "<ScheduleFactor>" + component.getSCED() + "</ScheduleFactor>" + newLine);
                out.write(componentTabs + "\t" + "<SchedulePercentFactor>" + component.getSCEDPercent() + "</SchedulePercentFactor>" + newLine);

                out.write(componentTabs + "\t" + "<ScheduleSettings>" + newLine);
                out.write(componentTabs + "\t\t" + "<_SCED>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<_Rating>" + component.getSCEDRating().toString() + "</_Rating>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<_Increment>" + component.getSCEDIncrement().toString() + "</_Increment>" + newLine);
                out.write(componentTabs + "\t\t" + "</_SCED>" + newLine);
                out.write(componentTabs + "\t" + "</ScheduleSettings>" + newLine);

                // COPSEMO section
                out.write(componentTabs + "\t" + "<COPSEMO>" + newLine);
                out.write(componentTabs + "\t\t" + "<_Inception>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<_EffortPercentage>" + component.getInceptionEffortPercentage() + "</_EffortPercentage>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<_SchedulePercentage>" + component.getInceptionSchedulePercentage() + "</_SchedulePercentage>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<Effort>" + component.getInceptionEffort() + "</Effort>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<Month>" + component.getInceptionMonth() + "</Month>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<Personnel>" + component.getInceptionPersonnel() + "</Personnel>" + newLine);
                out.write(componentTabs + "\t\t" + "</_Inception>" + newLine);
                out.write(componentTabs + "\t\t" + "<_Elaboration>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<_EffortPercentage>" + component.getElaborationEffortPercentage() + "</_EffortPercentage>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<_SchedulePercentage>" + component.getElaborationSchedulePercentage() + "</_SchedulePercentage>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<Effort>" + component.getElaborationEffort() + "</Effort>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<Month>" + component.getElaborationMonth() + "</Month>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<Personnel>" + component.getElaborationPersonnel() + "</Personnel>" + newLine);
                out.write(componentTabs + "\t\t" + "</_Elaboration>" + newLine);
                out.write(componentTabs + "\t\t" + "<_Construction>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<_EffortPercentage>" + component.getConstructionEffortPercentage() + "</_EffortPercentage>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<_SchedulePercentage>" + component.getConstructionSchedulePercentage() + "</_SchedulePercentage>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<Effort>" + component.getConstructionEffort() + "</Effort>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<Month>" + component.getConstructionMonth() + "</Month>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<Personnel>" + component.getConstructionPersonnel() + "</Personnel>" + newLine);
                out.write(componentTabs + "\t\t" + "</_Construction>" + newLine);
                out.write(componentTabs + "\t\t" + "<_Transition>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<_EffortPercentage>" + component.getTransitionEffortPercentage() + "</_EffortPercentage>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<_SchedulePercentage>" + component.getTransitionSchedulePercentage() + "</_SchedulePercentage>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<Effort>" + component.getTransitionEffort() + "</Effort>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<Month>" + component.getTransitionMonth() + "</Month>" + newLine);
                out.write(componentTabs + "\t\t\t" + "<Personnel>" + component.getTransitionPersonnel() + "</Personnel>" + newLine);
                out.write(componentTabs + "\t\t" + "</_Transition>" + newLine);
                out.write(componentTabs + "\t" + "</COPSEMO>" + newLine);

                out.write(componentTabs + "\t" + "<_MultiBuildShift>" + component.getMultiBuildShift() + "</_MultiBuildShift>" + newLine);

                // <SubComponent> sections
                ArrayList<COINCOMOUnit> orderedSubComponentsVector = component.getListOfSubUnits();
                for (int k = 0; k < orderedSubComponentsVector.size(); k++) {
                    // Sub Component level
                    final COINCOMOSubComponent subComponent = (COINCOMOSubComponent) orderedSubComponentsVector.get(k);

                    out.write(subComponentTabs + "<SubComponent>" + newLine);

                    out.write(subComponentTabs + "\t" + "<ID>" + subComponent.getUnitID() + "</ID>" + newLine);
                    out.write(subComponentTabs + "\t" + "<DatabaseID>" + subComponent.getDatabaseID() + "</DatabaseID>" + newLine);
                    out.write(subComponentTabs + "\t" + "<_Name>" + subComponent.getName() + "</_Name>" + newLine);
                    out.write(subComponentTabs + "\t" + "<SLOC>" + GlobalMethods.FormatLongWithComma(subComponent.getSLOC()) + "</SLOC>" + newLine);
                    out.write(subComponentTabs + "\t" + "<Cost>" + subComponent.getCost() + "</Cost>" + newLine);
                    out.write(subComponentTabs + "\t" + "<Staff>" + subComponent.getStaff() + "</Staff>" + newLine);
                    out.write(subComponentTabs + "\t" + "<Effort>" + subComponent.getEffort() + "</Effort>" + newLine);
                    out.write(subComponentTabs + "\t" + "<Schedule>" + subComponent.getSchedule() + "</Schedule>" + newLine);
                    out.write(subComponentTabs + "\t" + "<Productivity>" + subComponent.getProductivity() + "</Productivity>" + newLine);
                    out.write(subComponentTabs + "\t" + "<InstructionCost>" + subComponent.getInstructionCost() + "</InstructionCost>" + newLine);
                    out.write(subComponentTabs + "\t" + "<Risk>" + subComponent.getRisk() + "</Risk>" + newLine);
                    out.write(subComponentTabs + "\t" + "<NominalEffort>" + subComponent.getNominalEffort() + "</NominalEffort>" + newLine);
                    out.write(subComponentTabs + "\t" + "<EstimatedEffort>" + subComponent.getEstimatedEffort() + "</EstimatedEffort>" + newLine);
                    out.write(subComponentTabs + "\t" + "<SLOCWithoutREVL>" + GlobalMethods.FormatLongWithComma(subComponent.getSumOfSLOCs()) + "</SLOCWithoutREVL>" + newLine);
                    out.write(subComponentTabs + "\t" + "<_LaborRate>" + subComponent.getLaborRate() + "</_LaborRate>" + newLine);
                    out.write(subComponentTabs + "\t" + "<_Language>" + subComponent.getLanguage() + "</_Language>" + newLine);

                    // EAF Settings section
                    out.write(subComponentTabs + "\t" + "<EAF>" + subComponent.getEAF() + "</EAF>" + newLine);

                    out.write(subComponentTabs + "\t" + "<EAFSettings>" + newLine);
                    final Rating[] eafRatings = subComponent.getEAFRatings();
                    final Increment[] eafIncrements = subComponent.getEAFIncrements();
                    for (int l = 0; l < COINCOMOConstants.EAFS.length - 1; l++) {
                        out.write(subComponentTabs + "\t\t" + "<_" + COINCOMOConstants.EAFS[l] + ">" + newLine);
                        out.write(subComponentTabs + "\t\t\t" + "<_Rating>" + eafRatings[l].toString() + "</_Rating>" + newLine);
                        out.write(subComponentTabs + "\t\t\t" + "<_Increment>" + eafIncrements[l].toString() + "</_Increment>" + newLine);
                        out.write(subComponentTabs + "\t\t" + "</_" + COINCOMOConstants.EAFS[l] + ">" + newLine);
                    }
                    out.write(subComponentTabs + "\t" + "</EAFSettings>" + newLine);

                    out.write(subComponentTabs + "\t" + "<_Breakage>" + subComponent.getREVL() + "</_Breakage>" + newLine);

                    // New SLOC section
                    out.write(subComponentTabs + "\t" + "<New>" + newLine);
                    out.write(subComponentTabs + "\t\t" + "<_NewSLOC>" + GlobalMethods.FormatLongWithComma(subComponent.getNewSLOC()) + "</_NewSLOC>" + newLine);
                    out.write(subComponentTabs + "\t" + "</New>" + newLine);

                    // Function Points section
                    out.write(subComponentTabs + "\t" + "<FunctionPoints>" + newLine);

                    out.write(subComponentTabs + "\t\t" + "<_Multiplier>" + subComponent.getMultiplier() + "</_Multiplier>" + newLine);
                    out.write(subComponentTabs + "\t\t" + "<_RatioType>" + subComponent.getRatioType().toString() + "</_RatioType>" + newLine);
                    out.write(subComponentTabs + "\t\t" + "<_CalculationMethod>" + subComponent.getCalculationMethod().toString() + "</_CalculationMethod>" + newLine);

                    out.write(subComponentTabs + "\t\t" + "<FunctionTypes>" + newLine);
                    // Internal Logical Files (Files) part
                    out.write(subComponentTabs + "\t\t\t" + "<_InternalLogicalFiles>" + newLine);
                    final int files[] = subComponent.getInternalLogicalFiles();
                    for (int l = 0; l < COINCOMOConstants.FTS.length - 1; l++) {
                        out.write(subComponentTabs + "\t\t\t\t" + "<_" + COINCOMOConstants.FTS[l] + ">" + files[l] + "</_" + COINCOMOConstants.FTS[l] + ">" + newLine);
                    }
                    out.write(subComponentTabs + "\t\t\t\t" + "<" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + files[COINCOMOConstants.FTS.length - 1] + "</" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + newLine);
                    out.write(subComponentTabs + "\t\t\t" + "</_InternalLogicalFiles>" + newLine);
                    // External Interface Files (Interfaces) part
                    out.write(subComponentTabs + "\t\t\t" + "<_ExternalInterfaceFiles>" + newLine);
                    final int interfaces[] = subComponent.getExternalInterfaceFiles();
                    for (int l = 0; l < COINCOMOConstants.FTS.length - 1; l++) {
                        out.write(subComponentTabs + "\t\t\t\t" + "<_" + COINCOMOConstants.FTS[l] + ">" + interfaces[l] + "</_" + COINCOMOConstants.FTS[l] + ">" + newLine);
                    }
                    out.write(subComponentTabs + "\t\t\t\t" + "<" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + interfaces[COINCOMOConstants.FTS.length - 1] + "</" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + newLine);
                    out.write(subComponentTabs + "\t\t\t" + "</_ExternalInterfaceFiles>" + newLine);
                    // External Inputs (Inputs) part
                    out.write(subComponentTabs + "\t\t\t" + "<_ExternalInputs>" + newLine);
                    final int inputs[] = subComponent.getExternalInputs();
                    for (int l = 0; l < COINCOMOConstants.FTS.length - 1; l++) {
                        out.write(subComponentTabs + "\t\t\t\t" + "<_" + COINCOMOConstants.FTS[l] + ">" + inputs[l] + "</_" + COINCOMOConstants.FTS[l] + ">" + newLine);
                    }
                    out.write(subComponentTabs + "\t\t\t\t" + "<" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + inputs[COINCOMOConstants.FTS.length - 1] + "</" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + newLine);
                    out.write(subComponentTabs + "\t\t\t" + "</_ExternalInputs>" + newLine);
                    // External Outputs (Outputs) part
                    out.write(subComponentTabs + "\t\t\t" + "<_ExternalOutputs>" + newLine);
                    final int outputs[] = subComponent.getExternalOutputs();
                    for (int l = 0; l < COINCOMOConstants.FTS.length - 1; l++) {
                        out.write(subComponentTabs + "\t\t\t\t" + "<_" + COINCOMOConstants.FTS[l] + ">" + outputs[l] + "</_" + COINCOMOConstants.FTS[l] + ">" + newLine);
                    }
                    out.write(subComponentTabs + "\t\t\t\t" + "<" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + outputs[COINCOMOConstants.FTS.length - 1] + "</" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + newLine);
                    out.write(subComponentTabs + "\t\t\t" + "</_ExternalOutputs>" + newLine);
                    // External Inquiries (Inquiries) part
                    out.write(subComponentTabs + "\t\t\t" + "<_ExternalInquiries>" + newLine);
                    final int inquiries[] = subComponent.getExternalInquiries();
                    for (int l = 0; l < COINCOMOConstants.FTS.length - 1; l++) {
                        out.write(subComponentTabs + "\t\t\t\t" + "<_" + COINCOMOConstants.FTS[l] + ">" + inquiries[l] + "</_" + COINCOMOConstants.FTS[l] + ">" + newLine);
                    }
                    out.write(subComponentTabs + "\t\t\t\t" + "<" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + inquiries[COINCOMOConstants.FTS.length - 1] + "</" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + newLine);
                    out.write(subComponentTabs + "\t\t\t" + "</_ExternalInquiries>" + newLine);
                    out.write(subComponentTabs + "\t\t" + "</FunctionTypes>" + newLine);

                    out.write(subComponentTabs + "\t\t" + "<TotalUnadjustedFunctionPoints>" + subComponent.getTotalUnadjustedFunctionPoints() + "</TotalUnadjustedFunctionPoints>" + newLine);
                    out.write(subComponentTabs + "\t\t" + "<EquivalentSLOC>" + GlobalMethods.FormatLongWithComma(subComponent.getEquivalentSLOC()) + "</EquivalentSLOC>" + newLine);

                    out.write(subComponentTabs + "\t" + "</FunctionPoints>" + newLine);

                    // <AdaptationAndReuse> sections
                    ArrayList<COINCOMOUnit> orderedAdaptationAndReusesVector = subComponent.getListOfSubUnits();
                    for (int l = 0; l < orderedAdaptationAndReusesVector.size(); l++) {
                        // A&R level
                        final COINCOMOAdaptationAndReuse aAR = (COINCOMOAdaptationAndReuse) orderedAdaptationAndReusesVector.get(l);

                        out.write(adaptationTabs + "<AdaptationAndReuse>" + newLine);

                        out.write(adaptationTabs + "\t" + "<ID>" + aAR.getUnitID() + "</ID>" + newLine);
                        out.write(adaptationTabs + "\t" + "<DatabaseID>" + aAR.getDatabaseID() + "</DatabaseID>" + newLine);
                        out.write(adaptationTabs + "\t" + "<_Name>" + aAR.getName() + "</_Name>" + newLine);
                        out.write(adaptationTabs + "\t" + "<_AdaptedSLOC>" + GlobalMethods.FormatLongWithComma(aAR.getAdaptedSLOC()) + "</_AdaptedSLOC>" + newLine);
                        out.write(adaptationTabs + "\t" + "<_DesignModified>" + aAR.getDesignModified() + "</_DesignModified>" + newLine);
                        out.write(adaptationTabs + "\t" + "<_CodeModified>" + aAR.getCodeModified() + "</_CodeModified>" + newLine);
                        out.write(adaptationTabs + "\t" + "<_IntegrationModified>" + aAR.getIntegrationModified() + "</_IntegrationModified>" + newLine);
                        out.write(adaptationTabs + "\t" + "<_SoftwareUnderstanding>" + aAR.getSoftwareUnderstanding() + "</_SoftwareUnderstanding>" + newLine);
                        out.write(adaptationTabs + "\t" + "<_AssessmentAndAssimilation>" + aAR.getAssessmentAndAssimilation() + "</_AssessmentAndAssimilation>" + newLine);
                        out.write(adaptationTabs + "\t" + "<_UnfamiliarityWithSoftware>" + aAR.getUnfamiliarityWithSoftware() + "</_UnfamiliarityWithSoftware>" + newLine);
                        out.write(adaptationTabs + "\t" + "<_AutomaticallyTranslated>" + aAR.getAutomaticallyTranslated() + "</_AutomaticallyTranslated>" + newLine);
                        out.write(adaptationTabs + "\t" + "<_AutomaticTranslationProductivity>" + aAR.getAutomaticTranslationProductivity() + "</_AutomaticTranslationProductivity>" + newLine);
                        out.write(adaptationTabs + "\t" + "<AdaptationAdjustmentFactor>" + aAR.getAdaptationAdjustmentFactor() + "</AdaptationAdjustmentFactor>" + newLine);
                        out.write(adaptationTabs + "\t" + "<EquivalentSLOC>" + GlobalMethods.FormatLongWithComma(aAR.getEquivalentSLOC()) + "</EquivalentSLOC>" + newLine);

                        // End of A&R level
                        out.write(adaptationTabs + "</AdaptationAndReuse>" + newLine);
                    }

                    // End of Sub Component level
                    out.write(subComponentTabs + "</SubComponent>" + newLine);
                }

                // End of Component level
                out.write(componentTabs + "</Component>" + newLine);
            }

            // End of Sub System level
            out.write(subSystemTabs + "</SubSystem>" + newLine);
        }

        // End of System level
        out.write(systemTabs + "</System>" + newLine);

        // End of COINCOMO level
        out.write("</COINCOMO>");

        // Flush out the buffer
        out.flush();

        // Close the file
        out.close();
    }

    public static COINCOMOSystem synchronizeXML(File file) {
        COINCOMOSystem system = null;

        //Prepare the XML document
        Document doc = parseXML(file);

        if (doc != null) {
            // Retrieve the document element, i.e., <COINCOMO>.
            Element rootElement = doc.getDocumentElement();

            // Check the root element to see if there is a version attribute
            // to indicate new XML format and proceed with new parsing functions.
            if (rootElement.hasAttribute("version")) {
                String rootVersion = rootElement.getAttribute("version");

                // Start parsing the XML file
                Element systemElement = (Element) rootElement.getElementsByTagName("System").item(0);

                // Retrieve the system database ID to check against the database record
                long databaseID = 0;
                if (systemElement.getElementsByTagName("DatabaseID").getLength() > 0) {
                    databaseID = Long.parseLong(systemElement.getElementsByTagName("DatabaseID").item(0).getFirstChild().getNodeValue());
                }

                // Check against currently opened projects before continuing
                COINCOMO coincomo = MenuItemMethods.hasActiveProject(databaseID);
                if (coincomo != null) {
                    return null;
                }

                // Create the system
                system = COINCOMOSystemManager.synchronizeSystemWithXML(databaseID);

                // Populate the system
                if (system != null) {
                    populateSystem(systemElement, system, rootVersion.toString());
                }
            } else {
                //Cast the system node to an element
                Element sysElmnt = (Element) doc.getElementsByTagName("System").item(0);

                //Create the coincomo system
                system = COINCOMOSystemManager.insertSystem();

                //Populate the system
                populateSystem(sysElmnt, system, "");
            }
        }

        return system;
    }

    public static COINCOMOSystem importXML(File file) {
        COINCOMOSystem system = null;

        // Prepare the XML document.
        Document doc = parseXML(file);

        if (doc != null) {
            // Retrieve the document element, i.e., <COINCOMO>.
            Element rootElement = doc.getDocumentElement();

            // Check the root element to see if there is a version attribute
            // to indicate new XML format and proceed with new parsing functions.
            if (rootElement.hasAttribute("version")) {
                String rootVersion = rootElement.getAttribute("version");

                // Start parsing the XML file
                Element systemElement = (Element) rootElement.getElementsByTagName("System").item(0);
                // Check against the database
                if (COINCOMOSystemManager.hasSystemName(systemElement.getElementsByTagName("_Name").item(0).getFirstChild().getNodeValue())) {
                    return null;
                }
                // Create the system
                system = COINCOMOSystemManager.insertSystem();
                if (OperationMode.DESKTOP == COINCOMO.getOperationMode()) {
                    // If the system is belonging to database record, we want to keep the system ID intact while in desktop mode
                    system.setDatabaseID(Long.parseLong(systemElement.getElementsByTagName("DatabaseID").item(0).getFirstChild().getNodeValue()));
                }
                // Populate the system
                populateSystem(systemElement, system, rootVersion.toString());
            } else {
                //Cast the system node to an element
                Element sysElmnt = (Element) doc.getElementsByTagName("System").item(0);
                // Check against the database
                NodeList elmnts;
                elmnts = ((Element) sysElmnt.getElementsByTagName("name").item(0)).getChildNodes();
                if (COINCOMOSystemManager.hasSystemName(elmnts.item(0).getNodeValue())) {
                    return null;
                }
                //Create the coincomo system
                system = COINCOMOSystemManager.insertSystem();

                //Populate the system
                populateSystem(sysElmnt, system, "");
            }
        }

        return system;
    }

    private static void populateSystem(Element sysElmnt, COINCOMOSystem sys, String version) {

        // Check to see if the XML format is 2.0 or not.
        if (version.equals("2.0")) {
            //Populate <Name> node
            sys.setName(sysElmnt.getElementsByTagName("_Name").item(0).getFirstChild().getNodeValue());
            //sys.setDatabaseID(Long.parseLong(sysElmnt.getElementsByTagName("DatabaseID").item(0).getFirstChild().getNodeValue()));

            //Populate <SubSystem> nodes
            NodeList subsystems = sysElmnt.getElementsByTagName("SubSystem");

            for (int i = 0; i < subsystems.getLength(); i++) {
                Element subsystemElement = (Element) subsystems.item(i);
                //Create the subsystem
                COINCOMOSubSystem subsys = COINCOMOSubSystemManager.insertSubSystem(sys);
                //Populate the subsystem
                populateSubSystem(subsystemElement, subsys, version, null);
            }

            // Reflect the changes to underlying database.
            COINCOMOSystemManager.updateSystem(sys);

            return;
        }

        //Get the name node
        NodeList elmnts;
        elmnts = ((Element) sysElmnt.getElementsByTagName("name").item(0)).getChildNodes();
        //Set the system name
        sys.setName(elmnts.item(0).getNodeValue());

        COINCOMOComponentParameters parameters = new COINCOMOComponentParameters(null);

        //Get/Set A
        elmnts = ((Element) sysElmnt.getElementsByTagName("A").item(0)).getChildNodes();
        parameters.setA(Double.parseDouble(elmnts.item(0).getNodeValue()));
        //Get/Set B
        elmnts = ((Element) sysElmnt.getElementsByTagName("B").item(0)).getChildNodes();
        parameters.setB(Double.parseDouble(elmnts.item(0).getNodeValue()));
        //Get/Set C
        elmnts = ((Element) sysElmnt.getElementsByTagName("C").item(0)).getChildNodes();
        parameters.setC(Double.parseDouble(elmnts.item(0).getNodeValue()));
        //Get/Set D
        elmnts = ((Element) sysElmnt.getElementsByTagName("D").item(0)).getChildNodes();
        parameters.setD(Double.parseDouble(elmnts.item(0).getNodeValue()));
        //Get/Set Work Hours
        elmnts = ((Element) sysElmnt.getElementsByTagName("workHours").item(0)).getChildNodes();
        parameters.setWorkHours(Double.parseDouble(elmnts.item(0).getNodeValue()));

        elmnts = sysElmnt.getElementsByTagName("Product");
        for (int i = 0; i < PRODUCT.length; i++) {

            Element sElmnt = (Element) ((Element) elmnts.item(0)).getElementsByTagName(PRODUCT[i]).item(0);

            for (int j = 0; j < EAF.length; j++) {
                NodeList ssElmnts = ((Element) sElmnt.getElementsByTagName(EAF[j]).item(0)).getChildNodes();
                parameters.setProductValue(i, j, Double.parseDouble(ssElmnts.item(0).getNodeValue()));
            }
        }

        elmnts = sysElmnt.getElementsByTagName("Platform");
        for (int i = 0; i < PLATFORM.length; i++) {

            Element sElmnt = (Element) ((Element) elmnts.item(0)).getElementsByTagName(PLATFORM[i]).item(0);

            for (int j = 0; j < EAF.length; j++) {
                NodeList ssElmnts = ((Element) sElmnt.getElementsByTagName(EAF[j]).item(0)).getChildNodes();
                parameters.setPlatformValue(i, j, Double.parseDouble(ssElmnts.item(0).getNodeValue()));
            }
        }

        elmnts = sysElmnt.getElementsByTagName("Personnel");
        for (int i = 0; i < PERSONNEL.length; i++) {

            Element sElmnt = (Element) ((Element) elmnts.item(0)).getElementsByTagName(PERSONNEL[i]).item(0);

            for (int j = 0; j < EAF.length; j++) {
                NodeList ssElmnts = ((Element) sElmnt.getElementsByTagName(EAF[j]).item(0)).getChildNodes();
                parameters.setPersonnelValue(i, j, Double.parseDouble(ssElmnts.item(0).getNodeValue()));
            }
        }

        elmnts = sysElmnt.getElementsByTagName("Project");
        for (int i = 0; i < PROJECT.length; i++) {

            Element sElmnt = (Element) ((Element) elmnts.item(0)).getElementsByTagName(PROJECT[i]).item(0);

            for (int j = 0; j < EAF.length; j++) {
                NodeList ssElmnts = ((Element) sElmnt.getElementsByTagName(EAF[j]).item(0)).getChildNodes();
                parameters.setProjectValue(i, j, Double.parseDouble(ssElmnts.item(0).getNodeValue()));
            }
        }

        elmnts = sysElmnt.getElementsByTagName("UserDefined");
        for (int i = 0; i < USER_DEFINED.length; i++) {

            Element sElmnt = (Element) ((Element) elmnts.item(0)).getElementsByTagName(USER_DEFINED[i]).item(0);

            for (int j = 0; j < EAF.length; j++) {
                NodeList ssElmnts = ((Element) sElmnt.getElementsByTagName(EAF[j]).item(0)).getChildNodes();
                parameters.setUserDefinedValue(i, j, Double.parseDouble(ssElmnts.item(0).getNodeValue()));
            }
        }

        elmnts = sysElmnt.getElementsByTagName("ScaleFactors");
        for (int i = 0; i < SCALE_FACTORS.length; i++) {

            Element sElmnt = (Element) ((Element) elmnts.item(0)).getElementsByTagName(SCALE_FACTORS[i]).item(0);

            for (int j = 0; j < EAF.length; j++) {
                NodeList ssElmnts = ((Element) sElmnt.getElementsByTagName(EAF[j]).item(0)).getChildNodes();
                parameters.setScaleFactorsValue(i, j, Double.parseDouble(ssElmnts.item(0).getNodeValue()));
            }
        }

        elmnts = sysElmnt.getElementsByTagName("FunctionPoints");
        for (int i = 0; i < FUNCTION_POINTS_OLD.length; i++) {

            Element sElmnt = (Element) ((Element) elmnts.item(0)).getElementsByTagName(FUNCTION_POINTS_OLD[i]).item(0);

            for (int j = 0; j < FUNCTION.length; j++) {
                NodeList ssElmnts = ((Element) sElmnt.getElementsByTagName(FUNCTION[j]).item(0)).getChildNodes();
                parameters.setFunctionPointsValue(i, j, (int) Double.parseDouble(ssElmnts.item(0).getNodeValue()));
            }
        }

        //Populate the subsystem
        elmnts = sysElmnt.getElementsByTagName("SubSystem");
        for (int i = 0; i < elmnts.getLength(); i++) {

            //Get each subsystem
            Element subsysElmnt = (Element) elmnts.item(i);

            //Create the subsystem
            COINCOMOSubSystem subsys = COINCOMOSubSystemManager.insertSubSystem(sys);

            //add the subsystem to the system
            sys.addSubUnit(subsys);

            //Populate the subsystem
            populateSubSystem(subsysElmnt, subsys, "", parameters);
        }

        COINCOMOSystemManager.updateSystem(sys);
    }

    private static void populateSubSystem(Element subsysElmnt, COINCOMOSubSystem subsys, String version, COINCOMOComponentParameters parameters) {

        // Check to see if the XML format is 2.0 or not.
        if (version.equals("2.0")) {
            //Populate <Name> node
            subsys.setName(subsysElmnt.getElementsByTagName("_Name").item(0).getFirstChild().getNodeValue());
            //subsys.setDatabaseID(Long.parseLong(subsysElmnt.getElementsByTagName("DatabaseID").item(0).getFirstChild().getNodeValue()));

            //Populate <ZoomLevel> node
            subsys.setZoomLevel(Integer.parseInt(subsysElmnt.getElementsByTagName("_ZoomLevel").item(0).getFirstChild().getNodeValue()));

            //Populate <Component> nodes
            NodeList components = subsysElmnt.getElementsByTagName("Component");

            for (int i = 0; i < components.getLength(); i++) {
                Element componentElement = (Element) components.item(i);
                //Create the component
                COINCOMOComponent component = COINCOMOComponentManager.insertComponent(subsys);
                //Populate the component
                populateComponent(componentElement, component, version, null);
            }

            // Reflect the changes to underlying database.
            COINCOMOSubSystemManager.updateSubSystem(subsys, false);

            return;
        }

        //Get the name node
        NodeList elmnts = ((Element) subsysElmnt.getElementsByTagName("name").item(0)).getChildNodes();
        //Set the name
        subsys.setName(elmnts.item(0).getNodeValue());

        //Get/Set the zoom level
        elmnts = ((Element) subsysElmnt.getElementsByTagName("zoomLevel").item(0)).getChildNodes();
        subsys.setZoomLevel(Integer.parseInt(elmnts.item(0).getNodeValue()));

        //Populate the subsystem
        elmnts = subsysElmnt.getElementsByTagName("Component");
        for (int i = 0; i < elmnts.getLength(); i++) {

            //Get each subsystem
            Element compElmnt = (Element) elmnts.item(i);

            //Create/Add the subsystem
            COINCOMOComponent comp = COINCOMOComponentManager.insertComponent(subsys);

            //Populate the subsystem
            populateComponent(compElmnt, comp, "", parameters);
        }

        COINCOMOSubSystemManager.updateSubSystem(subsys, false);
    }

    private static void populateComponent(Element compElmnt, COINCOMOComponent comp, String version, COINCOMOComponentParameters params) {

        // Check to see if the XML format is 2.0 or not.
        if (version.equals("2.0")) {
            //Populate <Name> node
            comp.setName(compElmnt.getElementsByTagName("_Name").item(0).getFirstChild().getNodeValue());
            //comp.setDatabaseID(Long.parseLong(compElmnt.getElementsByTagName("DatabaseID").item(0).getFirstChild().getNodeValue()));

            //Populate the Local Calibration parameters <EffortAdjustmentFactors>, <ScaleFactors>, <EquationEditors>, <FunctionPoints>, <PersonMonth>
            COINCOMOComponentParameters parameters = comp.getParameters();

            //Populate <EffortAdjustmentFactors> section
            Element EAFs = (Element) compElmnt.getElementsByTagName("EffortAdjustmentFactors").item(0);
            double[][] eafWeights = new double[COINCOMOConstants.EAFS.length][COINCOMOConstants.Ratings.length];

            for (int i = 0; i < COINCOMOConstants.EAFS.length; i++) {
                Element eaf = (Element) EAFs.getElementsByTagName("_" + COINCOMOConstants.EAFS[i]).item(0);

                for (int j = 0; j < COINCOMOConstants.Ratings.length; j++) {
                    eafWeights[i][j] = Double.parseDouble(eaf.getElementsByTagName("_" + COINCOMOConstants.Ratings[j]).item(0).getFirstChild().getNodeValue());
                }
            }
            parameters.setEAFWeights(eafWeights);

            //Populate <ScaleFactors> section
            Element SFs = (Element) compElmnt.getElementsByTagName("ScaleFactors").item(0);
            double[][] sfWeights = new double[COINCOMOConstants.SFS.length][COINCOMOConstants.Ratings.length];

            for (int i = 0; i < COINCOMOConstants.SFS.length; i++) {
                Element sf = (Element) SFs.getElementsByTagName("_" + COINCOMOConstants.SFS[i]).item(0);

                for (int j = 0; j < COINCOMOConstants.Ratings.length; j++) {
                    sfWeights[i][j] = Double.parseDouble(sf.getElementsByTagName("_" + COINCOMOConstants.Ratings[j]).item(0).getFirstChild().getNodeValue());
                }
            }
            parameters.setSFWeights(sfWeights);

            //Populate <EquationEditors> section
            Element EEs = (Element) compElmnt.getElementsByTagName("EquationEditor").item(0);

            parameters.setA(Double.parseDouble(EEs.getElementsByTagName("_EffortEstimationParameterA").item(0).getFirstChild().getNodeValue()));
            parameters.setB(Double.parseDouble(EEs.getElementsByTagName("_ExponentParameterB").item(0).getFirstChild().getNodeValue()));
            parameters.setC(Double.parseDouble(EEs.getElementsByTagName("_ScheduleEstimationParameterC").item(0).getFirstChild().getNodeValue()));
            parameters.setD(Double.parseDouble(EEs.getElementsByTagName("_ScheduleEstimationParameterD").item(0).getFirstChild().getNodeValue()));

            //Populate <FunctionPoints> section
            Element FPs = (Element) compElmnt.getElementsByTagName("FunctionPoints").item(0);
            int[][] fpWeights = new int[COINCOMOConstants.FPS.length][COINCOMOConstants.FTS.length - 1];

            for (int i = 0; i < COINCOMOConstants.FPS2.length; i++) {
                Element sf = (Element) FPs.getElementsByTagName("_" + COINCOMOConstants.FPS2[i]).item(0);

                for (int j = 0; j < COINCOMOConstants.FTS.length - 1; j++) {
                    fpWeights[i][j] = Integer.parseInt(sf.getElementsByTagName("_" + COINCOMOConstants.FTS[j]).item(0).getFirstChild().getNodeValue());
                }
            }
            parameters.setFPWeights(fpWeights);

            //Populate <PersonMonth> section
            Element PM = (Element) compElmnt.getElementsByTagName("PersonMonth").item(0);

            parameters.setWorkHours(Double.parseDouble(PM.getElementsByTagName("_HoursPerPM").item(0).getFirstChild().getNodeValue()));

            //Populate <ScaleFactorsSettings> section
            Element SFSs = (Element) compElmnt.getElementsByTagName("ScaleFactorsSettings").item(0);

            Rating sfRatings[] = new Rating[COINCOMOConstants.SFS.length];
            Increment sfIncrements[] = new Increment[COINCOMOConstants.SFS.length];

            for (int i = 0; i < COINCOMOConstants.SFS.length; i++) {
                Element sf = (Element) SFSs.getElementsByTagName("_" + COINCOMOConstants.SFS[i]).item(0);

                sfRatings[i] = Rating.valueOf(sf.getElementsByTagName("_Rating").item(0).getFirstChild().getNodeValue());
                sfIncrements[i] = Increment.getValueOf(sf.getElementsByTagName("_Increment").item(0).getFirstChild().getNodeValue());
            }

            comp.setSFRatings(sfRatings);
            comp.setSFIncrements(sfIncrements);

            //Populate <ScheduleSettings> section
            Element SSs = (Element) compElmnt.getElementsByTagName("ScheduleSettings").item(0);
            Element s = (Element) SSs.getElementsByTagName("_SCED").item(0);

            comp.setSCEDRating(Rating.valueOf(s.getElementsByTagName("_Rating").item(0).getFirstChild().getNodeValue()));
            comp.setSCEDIncrement(Increment.getValueOf(s.getElementsByTagName("_Increment").item(0).getFirstChild().getNodeValue()));

            //Populate <COPSEMO> nodes
            Element COPSEMO = (Element) compElmnt.getElementsByTagName("COPSEMO").item(0);

            for (int i = 0; i < COINCOMOConstants.COPSEMOS.length; i++) {
                Element stage = (Element) COPSEMO.getElementsByTagName("_" + COINCOMOConstants.COPSEMOS[i]).item(0);

                if (stage.getNodeName().equals("_Inception")) {
                    comp.setInceptionEffortPercentage(Double.parseDouble(stage.getElementsByTagName("_EffortPercentage").item(0).getFirstChild().getNodeValue()));
                    comp.setInceptionSchedulePercentage(Double.parseDouble(stage.getElementsByTagName("_SchedulePercentage").item(0).getFirstChild().getNodeValue()));
                } else if (stage.getNodeName().equals("_Elaboration")) {
                    comp.setElaborationEffortPercentage(Double.parseDouble(stage.getElementsByTagName("_EffortPercentage").item(0).getFirstChild().getNodeValue()));
                    comp.setElaborationSchedulePercentage(Double.parseDouble(stage.getElementsByTagName("_SchedulePercentage").item(0).getFirstChild().getNodeValue()));
                } else if (stage.getNodeName().equals("_Construction")) {
                    comp.setConstructionEffortPercentage(Double.parseDouble(stage.getElementsByTagName("_EffortPercentage").item(0).getFirstChild().getNodeValue()));
                    comp.setConstructionSchedulePercentage(Double.parseDouble(stage.getElementsByTagName("_SchedulePercentage").item(0).getFirstChild().getNodeValue()));
                } else if (stage.getNodeName().equals("_Transition")) {
                    comp.setTransitionEffortPercentage(Double.parseDouble(stage.getElementsByTagName("_EffortPercentage").item(0).getFirstChild().getNodeValue()));
                    comp.setTransitionSchedulePercentage(Double.parseDouble(stage.getElementsByTagName("_SchedulePercentage").item(0).getFirstChild().getNodeValue()));
                } else {
                    System.out.println("COINCOMOSystemManager.populateComponent() i in COINCOMOConstants.COPSEMOS[] out of bound!");
                }
            }

            //Populate <MultiBuildShift> node
            comp.setMultiBuildShift(Integer.parseInt(compElmnt.getElementsByTagName("_MultiBuildShift").item(0).getFirstChild().getNodeValue()));

            //Populate <SubComponent> nodes
            NodeList subComponents = compElmnt.getElementsByTagName("SubComponent");

            for (int i = 0; i < subComponents.getLength(); i++) {
                Element subComponentElement = (Element) subComponents.item(i);
                //Create the component
                COINCOMOSubComponent subComponent = COINCOMOSubComponentManager.insertSubComponent(comp);
                //Populate the component
                populateSubComponent(subComponentElement, subComponent, version);
            }

            // Reflect the changes to underlying database.
            COINCOMOComponentManager.updateComponent(comp, false);

            return;
        }

        // Copy the parameter settings from the system level for backward compatibility.
        COINCOMOComponentParameters parameters = comp.getParameters();
        parameters.copyValues(params);

        //Get the name node
        NodeList elmnts = ((Element) compElmnt.getElementsByTagName("name").item(0)).getChildNodes();
        comp.setName(elmnts.item(0).getNodeValue());

        elmnts = ((Element) compElmnt.getElementsByTagName("multiBuildShift").item(0)).getChildNodes();
        comp.setMultiBuildShift(Integer.parseInt(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) compElmnt.getElementsByTagName("scedBase").item(0)).getChildNodes();
        int scedBase = Integer.parseInt(elmnts.item(0).getNodeValue());
        switch (scedBase) {
            case 1:
                comp.setSCEDRating(Rating.VLO);
                break;
            case 2:
                comp.setSCEDRating(Rating.LO);
                break;
            case 3:
                comp.setSCEDRating(Rating.NOM);
                break;
            case 4:
                comp.setSCEDRating(Rating.HI);
                break;
            case 5:
                comp.setSCEDRating(Rating.VHI);
                break;
            case 6:
                comp.setSCEDRating(Rating.XHI);
                break;
            default:
                comp.setSCEDRating(Rating.NOM);
                break;
        }

        elmnts = ((Element) compElmnt.getElementsByTagName("scedIncr").item(0)).getChildNodes();
        int scedIncr = Integer.parseInt(elmnts.item(0).getNodeValue());
        switch (scedIncr) {
            case 1:
                comp.setSCEDIncrement(Increment.Percent0);
                break;
            case 2:
                comp.setSCEDIncrement(Increment.Percent25);
                break;
            case 3:
                comp.setSCEDIncrement(Increment.Percent50);
                break;
            case 4:
                comp.setSCEDIncrement(Increment.Percent75);
                break;
            default:
                comp.setSCEDIncrement(Increment.Percent0);
                break;
        }

        elmnts = ((Element) (compElmnt.getElementsByTagName("scaleFactorsBase")).item(0)).getElementsByTagName("value");
        int x[] = new int[elmnts.getLength()];
        for (int i = 0; i < elmnts.getLength(); i++) {
            NodeList subElmnt = elmnts.item(i).getChildNodes();
            x[i] = Integer.parseInt(subElmnt.item(0).getNodeValue());
        }
        Rating sfRatings[] = new Rating[COINCOMOConstants.SFS.length];
        for (int i = 0; i < elmnts.getLength(); i++) {
            switch (x[i]) {
                case 1:
                    sfRatings[i] = Rating.VLO;
                    break;
                case 2:
                    sfRatings[i] = Rating.LO;
                    break;
                case 3:
                    sfRatings[i] = Rating.NOM;
                    break;
                case 4:
                    sfRatings[i] = Rating.HI;
                    break;
                case 5:
                    sfRatings[i] = Rating.VHI;
                    break;
                case 6:
                    sfRatings[i] = Rating.XHI;
                    break;
                default:
                    sfRatings[i] = Rating.NOM;
                    break;
            }
        }
        comp.setSFRatings(sfRatings);
        //comp.setScaleFactorsBase(x);

        elmnts = ((Element) (compElmnt.getElementsByTagName("scaleFactorsIncr")).item(0)).getElementsByTagName("value");
        x = new int[elmnts.getLength()];
        for (int i = 0; i < elmnts.getLength(); i++) {
            NodeList subElmnt = elmnts.item(i).getChildNodes();
            x[i] = Integer.parseInt(subElmnt.item(0).getNodeValue());
        }
        Increment sfIncrements[] = new Increment[COINCOMOConstants.SFS.length];
        for (int i = 0; i < elmnts.getLength(); i++) {
            switch (x[i]) {
                case 1:
                    sfIncrements[i] = Increment.Percent0;
                    break;
                case 2:
                    sfIncrements[i] = Increment.Percent25;
                    break;
                case 3:
                    sfIncrements[i] = Increment.Percent50;
                    break;
                case 4:
                    sfIncrements[i] = Increment.Percent75;
                    break;
                default:
                    sfIncrements[i] = Increment.Percent0;
                    break;
            }
        }
        comp.setSFIncrements(sfIncrements);
        //comp.setScaleFactorsIncr(x);

        /*elmnts = ((Element) compElmnt.getElementsByTagName("totalProd").item(0)).getChildNodes();
         comp.setTotalProd(Float.parseFloat(elmnts.item(0).getNodeValue()));
        
         elmnts = ((Element) compElmnt.getElementsByTagName("totalCost").item(0)).getChildNodes();
         comp.setTotalCost(Float.parseFloat(elmnts.item(0).getNodeValue()));
        
         elmnts = ((Element) compElmnt.getElementsByTagName("totalInstCost").item(0)).getChildNodes();
         comp.setTotalInstCost(Float.parseFloat(elmnts.item(0).getNodeValue()));
        
         elmnts = ((Element) compElmnt.getElementsByTagName("totalStaff").item(0)).getChildNodes();
         comp.setTotalStaff(Float.parseFloat(elmnts.item(0).getNodeValue()));
        
         elmnts = ((Element) compElmnt.getElementsByTagName("totalSchedule").item(0)).getChildNodes();
         comp.setTotalSchedule(Float.parseFloat(elmnts.item(0).getNodeValue()));*/

        elmnts = ((Element) compElmnt.getElementsByTagName("inceptionEffortPercentage").item(0)).getChildNodes();
        comp.setInceptionEffortPercentage(Double.parseDouble(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) compElmnt.getElementsByTagName("elaborationEffortPercentage").item(0)).getChildNodes();
        comp.setElaborationEffortPercentage(Double.parseDouble(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) compElmnt.getElementsByTagName("constructionEffortPercentage").item(0)).getChildNodes();
        comp.setConstructionEffortPercentage(Double.parseDouble(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) compElmnt.getElementsByTagName("transitionEffortPercentage").item(0)).getChildNodes();
        comp.setTransitionEffortPercentage(Double.parseDouble(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) compElmnt.getElementsByTagName("inceptionSchedulePercentage").item(0)).getChildNodes();
        comp.setInceptionSchedulePercentage(Double.parseDouble(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) compElmnt.getElementsByTagName("elaborationSchedulePercentage").item(0)).getChildNodes();
        comp.setElaborationSchedulePercentage(Double.parseDouble(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) compElmnt.getElementsByTagName("constructionSchedulePercentage").item(0)).getChildNodes();
        comp.setConstructionSchedulePercentage(Double.parseDouble(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) compElmnt.getElementsByTagName("transitionSchedulePercentage").item(0)).getChildNodes();
        comp.setTransitionSchedulePercentage(Double.parseDouble(elmnts.item(0).getNodeValue()));

        //elmnts = ((Element) compElmnt.getElementsByTagName("schedule").item(0)).getChildNodes();
        //comp.setSchedule(Float.parseFloat(elmnts.item(0).getNodeValue()));

        //Populate the subsystem
        elmnts = compElmnt.getElementsByTagName("SubComponent");
        for (int i = 0; i < elmnts.getLength(); i++) {

            //Get each subsystem
            Element subcompElmnt = (Element) elmnts.item(i);

            //Create/Add the subsystem
            COINCOMOSubComponent subcomp = COINCOMOSubComponentManager.insertSubComponent(comp);

            //Populate the subsystem
            populateSubComponent(subcompElmnt, subcomp, "");
        }

        COINCOMOComponentManager.updateComponent(comp, false);
    }

    private static void populateSubComponent(Element subcompElmnt, COINCOMOSubComponent subcomp, String version) {

        // Check to see if the XML format is 2.0 or not.
        if (version.equals("2.0")) {
            //Populate <Name> node
            subcomp.setName(subcompElmnt.getElementsByTagName("_Name").item(0).getFirstChild().getNodeValue());
            //subcomp.setDatabaseID(Long.parseLong(subcompElmnt.getElementsByTagName("DatabaseID").item(0).getFirstChild().getNodeValue()));

            //Populate <LaborRate> node
            subcomp.setLaborRate(Float.parseFloat(subcompElmnt.getElementsByTagName("_LaborRate").item(0).getFirstChild().getNodeValue()));

            //Populate <Language> node
            subcomp.setLanguage(subcompElmnt.getElementsByTagName("_Language").item(0).getFirstChild().getNodeValue());

            //Populate <EAFSettings> section
            Element EAFs = (Element) subcompElmnt.getElementsByTagName("EAFSettings").item(0);

            Rating eafRatings[] = new Rating[COINCOMOConstants.EAFS.length - 1];
            Increment eafIncrements[] = new Increment[COINCOMOConstants.EAFS.length - 1];

            for (int i = 0; i < COINCOMOConstants.EAFS.length - 1; i++) {
                Element eaf = (Element) EAFs.getElementsByTagName("_" + COINCOMOConstants.EAFS[i]).item(0);

                eafRatings[i] = Rating.valueOf(eaf.getElementsByTagName("_Rating").item(0).getFirstChild().getNodeValue());
                eafIncrements[i] = Increment.getValueOf(eaf.getElementsByTagName("_Increment").item(0).getFirstChild().getNodeValue());
            }

            subcomp.setEAFRatings(eafRatings);
            subcomp.setEAFIncrements(eafIncrements);

            //Populate <Breakage> node
            subcomp.setREVL(Float.parseFloat(subcompElmnt.getElementsByTagName("_Breakage").item(0).getFirstChild().getNodeValue()));

            //Populate <New> section
            Element New = (Element) subcompElmnt.getElementsByTagName("New").item(0);

            subcomp.setNewSLOC(GlobalMethods.ParseLongWithComma(New.getElementsByTagName("_NewSLOC").item(0).getFirstChild().getNodeValue()));

            //Populate <FunctionPoints> section
            Element FunctionPoints = (Element) subcompElmnt.getElementsByTagName("FunctionPoints").item(0);

            //Populate <Multiplier> node
            subcomp.setMultiplier(Integer.parseInt(FunctionPoints.getElementsByTagName("_Multiplier").item(0).getFirstChild().getNodeValue()));

            //Populate <RatioType> node
            Element ratioType = (Element) FunctionPoints.getElementsByTagName("_RatioType").item(0);
            subcomp.setRatioType(RatioType.valueOf(ratioType.getFirstChild().getNodeValue()));

            //Populate <CalculationMethod> node
            Element calculationMethod = (Element) FunctionPoints.getElementsByTagName("_CalculationMethod").item(0);
            subcomp.setCalculationMethod(CalculationMethod.getValueOf(calculationMethod.getFirstChild().getNodeValue()));

            //Populate <FunctionTypes> section
            Element FunctionTypes = (Element) FunctionPoints.getElementsByTagName("FunctionTypes").item(0);

            for (int i = 0; i < COINCOMOConstants.FPS2.length; i++) {
                Element functionType = (Element) FunctionTypes.getElementsByTagName("_" + COINCOMOConstants.FPS2[i]).item(0);

                int values[] = new int[COINCOMOConstants.FTS.length];
                for (int j = 0; j < COINCOMOConstants.FTS.length - 1; j++) {
                    values[j] = Integer.parseInt(functionType.getElementsByTagName("_" + COINCOMOConstants.FTS[j]).item(0).getFirstChild().getNodeValue());
                }
                values[COINCOMOConstants.FTS.length - 1] = Integer.parseInt(functionType.getElementsByTagName(COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1]).item(0).getFirstChild().getNodeValue());

                if (functionType.getNodeName().equals("_InternalLogicalFiles")) {
                    subcomp.setInternalLogicalFiles(values);
                } else if (functionType.getNodeName().equals("_ExternalInterfaceFiles")) {
                    subcomp.setExternalInterfaceFiles(values);
                } else if (functionType.getNodeName().equals("_ExternalInputs")) {
                    subcomp.setExternalInputs(values);
                } else if (functionType.getNodeName().equals("_ExternalOutputs")) {
                    subcomp.setExternalOutputs(values);
                } else if (functionType.getNodeName().equals("_ExternalInquiries")) {
                    subcomp.setExternalInquiries(values);
                } else {
                    System.out.println("COINCOMOSystemManager.populateSubComponent() i in COINCOMOConstants.FPS2[] out of bound!");
                }
            }

            //Populate <AdaptationAndReuse> nodes
            NodeList adaptations = subcompElmnt.getElementsByTagName("AdaptationAndReuse");

            for (int i = 0; i < adaptations.getLength(); i++) {
                Element adaptationElement = (Element) adaptations.item(i);
                //Create the adaptation
                COINCOMOAdaptationAndReuse adaptation = COINCOMOAdaptationAndReuseManager.insertAdaptationAndReuse(subcomp);

                //Populate the adaptation
                populateAdaptationAndReuse(adaptationElement, adaptation, version);
            }

            // Reflect the changes to underlying database.
            COINCOMOSubComponentManager.updateSubComponent(subcomp, false);

            return;
        }


        //Get the name node
        NodeList elmnts = ((Element) subcompElmnt.getElementsByTagName("name").item(0)).getChildNodes();
        subcomp.setName(elmnts.item(0).getNodeValue());

        elmnts = ((Element) subcompElmnt.getElementsByTagName("RatioType").item(0)).getChildNodes();
        if (Integer.parseInt(elmnts.item(0).getNodeValue()) == 0) {
            subcomp.setRatioType(RatioType.Jones);
        } else if (Integer.parseInt(elmnts.item(0).getNodeValue()) == 1) {
            subcomp.setRatioType(RatioType.David);
        } else {
            subcomp.setRatioType(RatioType.Jones);
        }

        elmnts = ((Element) subcompElmnt.getElementsByTagName("CalculationMethod").item(0)).getChildNodes();
        if (Integer.parseInt(elmnts.item(0).getNodeValue()) == 2) {
            subcomp.setCalculationMethod(CalculationMethod.UsingTable);
        } else if (Integer.parseInt(elmnts.item(0).getNodeValue()) == 3) {
            subcomp.setCalculationMethod(CalculationMethod.InputCalculatedFunctionPoints);
        } else {
            subcomp.setCalculationMethod(CalculationMethod.UsingTable);
        }

        elmnts = ((Element) subcompElmnt.getElementsByTagName("language").item(0)).getChildNodes();
        subcomp.setLanguage(elmnts.item(0).getNodeValue());

        elmnts = ((Element) subcompElmnt.getElementsByTagName("laborRate").item(0)).getChildNodes();
        subcomp.setLaborRate(Float.parseFloat(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) subcompElmnt.getElementsByTagName("newSLOC").item(0)).getChildNodes();
        subcomp.setNewSLOC(GlobalMethods.ParseLongWithComma(elmnts.item(0).getNodeValue()));

        //elmnts = ((Element) subcompElmnt.getElementsByTagName("adaptiveSLOC").item(0)).getChildNodes();
        //subcomp.setAdaptiveSLOC(Float.parseFloat(elmnts.item(0).getNodeValue()));

        //elmnts = ((Element) subcompElmnt.getElementsByTagName("eaf").item(0)).getChildNodes();
        //subcomp.setEaf(Float.parseFloat(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) subcompElmnt.getElementsByTagName("breakage").item(0)).getChildNodes();
        subcomp.setREVL(Float.parseFloat(elmnts.item(0).getNodeValue()));

        /*elmnts = ((Element) subcompElmnt.getElementsByTagName("sizingType").item(0)).getChildNodes();
         subcomp.setSizingType(Integer.parseInt(elmnts.item(0).getNodeValue()));*/

        elmnts = ((Element) subcompElmnt.getElementsByTagName("risk").item(0)).getChildNodes();
        subcomp.setRisk(Float.parseFloat(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) subcompElmnt.getElementsByTagName("changeMultiplier").item(0)).getChildNodes();
        subcomp.setMultiplier(Integer.parseInt(elmnts.item(0).getNodeValue()));

        // If FP is using Using Table, read in the <internalLogicalFiles> to <externalInquiries> values
        //if (subcomp.getCalculationMethod() == COINCOMOSubComponent.USING_TABLE) {
        if (true) {
            elmnts = ((Element) (subcompElmnt.getElementsByTagName("internalLogicalFiles")).item(0)).getElementsByTagName("value");
            int x[] = new int[elmnts.getLength()];
            for (int i = 0; i < elmnts.getLength(); i++) {
                NodeList subElmnt = elmnts.item(i).getChildNodes();
                x[i] = Integer.parseInt(subElmnt.item(0).getNodeValue());
            }
            subcomp.setInternalLogicalFiles(x);

            elmnts = ((Element) (subcompElmnt.getElementsByTagName("externalInterfaceFiles")).item(0)).getElementsByTagName("value");
            x = new int[elmnts.getLength()];
            for (int i = 0; i < elmnts.getLength(); i++) {
                NodeList subElmnt = elmnts.item(i).getChildNodes();
                x[i] = Integer.parseInt(subElmnt.item(0).getNodeValue());
            }
            subcomp.setExternalInterfaceFiles(x);

            elmnts = ((Element) (subcompElmnt.getElementsByTagName("externalInputs")).item(0)).getElementsByTagName("value");
            x = new int[elmnts.getLength()];
            for (int i = 0; i < elmnts.getLength(); i++) {
                NodeList subElmnt = elmnts.item(i).getChildNodes();
                x[i] = Integer.parseInt(subElmnt.item(0).getNodeValue());
            }
            subcomp.setExternalInputs(x);

            elmnts = ((Element) (subcompElmnt.getElementsByTagName("externalOutputs")).item(0)).getElementsByTagName("value");
            x = new int[elmnts.getLength()];
            for (int i = 0; i < elmnts.getLength(); i++) {
                NodeList subElmnt = elmnts.item(i).getChildNodes();
                x[i] = Integer.parseInt(subElmnt.item(0).getNodeValue());
            }
            subcomp.setExternalOutputs(x);

            elmnts = ((Element) (subcompElmnt.getElementsByTagName("externalInquiries")).item(0)).getElementsByTagName("value");
            x = new int[elmnts.getLength()];
            for (int i = 0; i < elmnts.getLength(); i++) {
                NodeList subElmnt = elmnts.item(i).getChildNodes();
                x[i] = Integer.parseInt(subElmnt.item(0).getNodeValue());
            }
            subcomp.setExternalInquiries(x);
        }

        elmnts = ((Element) (subcompElmnt.getElementsByTagName("ratings")).item(0)).getElementsByTagName("value");
        int y[] = new int[elmnts.getLength()];
        for (int i = 0; i < elmnts.getLength(); i++) {
            NodeList subElmnt = elmnts.item(i).getChildNodes();
            y[i] = Integer.parseInt(subElmnt.item(0).getNodeValue());
        }
        Rating eafRatings[] = new Rating[COINCOMOConstants.EAFS.length - 1];
        for (int i = 0; i < elmnts.getLength(); i++) {
            switch (y[i]) {
                case 1:
                    eafRatings[i] = Rating.VLO;
                    break;
                case 2:
                    eafRatings[i] = Rating.LO;
                    break;
                case 3:
                    eafRatings[i] = Rating.NOM;
                    break;
                case 4:
                    eafRatings[i] = Rating.HI;
                    break;
                case 5:
                    eafRatings[i] = Rating.VHI;
                    break;
                case 6:
                    eafRatings[i] = Rating.XHI;
                    break;
                default:
                    eafRatings[i] = Rating.NOM;
                    break;
            }
        }
        subcomp.setEAFRatings(eafRatings);
        //subcomp.setRatings(y);

        elmnts = ((Element) (subcompElmnt.getElementsByTagName("percent")).item(0)).getElementsByTagName("value");
        y = new int[elmnts.getLength()];
        for (int i = 0; i < elmnts.getLength(); i++) {
            NodeList subElmnt = elmnts.item(i).getChildNodes();
            y[i] = Integer.parseInt(subElmnt.item(0).getNodeValue());
        }
        Increment eafIncrements[] = new Increment[COINCOMOConstants.EAFS.length - 1];
        for (int i = 0; i < elmnts.getLength(); i++) {
            switch (y[i]) {
                case 1:
                    eafIncrements[i] = Increment.Percent0;
                    break;
                case 2:
                    eafIncrements[i] = Increment.Percent25;
                    break;
                case 3:
                    eafIncrements[i] = Increment.Percent50;
                    break;
                case 4:
                    eafIncrements[i] = Increment.Percent75;
                    break;
                default:
                    eafIncrements[i] = Increment.Percent0;
                    break;
            }
        }
        subcomp.setEAFIncrements(eafIncrements);
        //subcomp.setPercent(y);

        // If FP is using Input Calculated Function Point, read in the <totalUnadjustedFunctionPoints> value
        //if (subcomp.getCalculationMethod() == COINCOMOSubComponent.INPUT_CALC_FUNC_PT) {
        if (true) {
            // Special case for Total Unadjusted Function Points for previously saved XML files for backward compatibility.
            NodeList tmpNdList = subcompElmnt.getElementsByTagName("totalUnadjustedFunctionPoints");
            if (tmpNdList.getLength() == 1) {
                elmnts = ((Element) tmpNdList.item(0)).getChildNodes();
                //subcomp.setTotUnadjustedFunctionPoints(GlobalMethods.ParseLongWithComma(elmnts.item(0).getNodeValue()));
            }
        }

        //Populate the subsystem
        elmnts = subcompElmnt.getElementsByTagName("AdaptationAndReuse");
        for (int i = 0; i < elmnts.getLength(); i++) {

            //Get each subsystem
            Element aARElmnt = (Element) elmnts.item(i);

            //Create/Add the subsystem
            COINCOMOAdaptationAndReuse aAR = COINCOMOAdaptationAndReuseManager.insertAdaptationAndReuse(subcomp);

            //Populate the subsystem
            populateAdaptationAndReuse(aARElmnt, aAR, "");
        }

        COINCOMOSubComponentManager.updateSubComponent(subcomp, false);
    }

    private static void populateAdaptationAndReuse(Element aARElmnt, COINCOMOAdaptationAndReuse aAR, String version) {

        // Check to see if the XML format is 2.0 or not.
        if (version.equals("2.0")) {
            //Populate <Name> node
            aAR.setName(aARElmnt.getElementsByTagName("_Name").item(0).getFirstChild().getNodeValue());
            //aAR.setDatabaseID(Long.parseLong(aARElmnt.getElementsByTagName("DatabaseID").item(0).getFirstChild().getNodeValue()));

            //Populate <InitialSLOC> node
            aAR.setAdaptedSLOC(GlobalMethods.ParseLongWithComma(aARElmnt.getElementsByTagName("_AdaptedSLOC").item(0).getFirstChild().getNodeValue()));

            //Populate <DesignModified> node
            aAR.setDesignModified(Double.parseDouble(aARElmnt.getElementsByTagName("_DesignModified").item(0).getFirstChild().getNodeValue()));

            //Populate <CodeModified> node
            aAR.setCodeModified(Double.parseDouble(aARElmnt.getElementsByTagName("_CodeModified").item(0).getFirstChild().getNodeValue()));

            //Populate <IntegrationModified> node
            aAR.setIntegrationModified(Double.parseDouble(aARElmnt.getElementsByTagName("_IntegrationModified").item(0).getFirstChild().getNodeValue()));

            //Populate <SoftwareUnderstanding> node
            aAR.setSoftwareUnderstanding(Double.parseDouble(aARElmnt.getElementsByTagName("_SoftwareUnderstanding").item(0).getFirstChild().getNodeValue()));

            //Populate <AssessmentAndAssimilation> node
            aAR.setAssessmentAndAssimilation(Double.parseDouble(aARElmnt.getElementsByTagName("_AssessmentAndAssimilation").item(0).getFirstChild().getNodeValue()));

            //Populate <UnfamiliarityWithSoftware> node
            aAR.setUnfamiliarityWithSoftware(Double.parseDouble(aARElmnt.getElementsByTagName("_UnfamiliarityWithSoftware").item(0).getFirstChild().getNodeValue()));

            //Populate <AutomaticTranslation> node
            aAR.setAutomaticallyTranslated(Double.parseDouble(aARElmnt.getElementsByTagName("_AutomaticallyTranslated").item(0).getFirstChild().getNodeValue()));

            //Populate <AutomaticTranslationProductivity> node
            aAR.setAutomaticTranslationProductivity(Double.parseDouble(aARElmnt.getElementsByTagName("_AutomaticTranslationProductivity").item(0).getFirstChild().getNodeValue()));

            //Populate <AdaptationAdjustmentFactor> node
            aAR.setAdaptationAdjustmentFactor(Double.parseDouble(aARElmnt.getElementsByTagName("AdaptationAdjustmentFactor").item(0).getFirstChild().getNodeValue()));

            //Populate <AdaptedSLOC> node (WARNING: This should have been calculated when opened by COINCOMO!)
            aAR.setEquivalentSLOC(GlobalMethods.ParseLongWithComma(aARElmnt.getElementsByTagName("EquivalentSLOC").item(0).getFirstChild().getNodeValue()));

            // Reflect the changes to underlying database.
            COINCOMOAdaptationAndReuseManager.updateAdaptationAndReuse(aAR, false);

            return;
        }

        NodeList elmnts = ((Element) aARElmnt.getElementsByTagName("name").item(0)).getChildNodes();
        aAR.setName(elmnts.item(0).getNodeValue());

        elmnts = ((Element) aARElmnt.getElementsByTagName("ASLOC").item(0)).getChildNodes();
        aAR.setEquivalentSLOC(GlobalMethods.ParseLongWithComma(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) aARElmnt.getElementsByTagName("initialSLOC").item(0)).getChildNodes();
        aAR.setAdaptedSLOC(GlobalMethods.ParseLongWithComma(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) aARElmnt.getElementsByTagName("designModified").item(0)).getChildNodes();
        aAR.setDesignModified(Float.parseFloat(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) aARElmnt.getElementsByTagName("codeModified").item(0)).getChildNodes();
        aAR.setCodeModified(Float.parseFloat(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) aARElmnt.getElementsByTagName("integrationModified").item(0)).getChildNodes();
        aAR.setIntegrationModified(Float.parseFloat(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) aARElmnt.getElementsByTagName("softwareUnderstanding").item(0)).getChildNodes();
        aAR.setSoftwareUnderstanding(Float.parseFloat(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) aARElmnt.getElementsByTagName("assementAssimilation").item(0)).getChildNodes();
        aAR.setAssessmentAndAssimilation(Float.parseFloat(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) aARElmnt.getElementsByTagName("unfamiliarity").item(0)).getChildNodes();
        aAR.setUnfamiliarityWithSoftware(Float.parseFloat(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) aARElmnt.getElementsByTagName("automaticallyTranslated").item(0)).getChildNodes();
        aAR.setAutomaticallyTranslated(Float.parseFloat(elmnts.item(0).getNodeValue()));

        elmnts = ((Element) aARElmnt.getElementsByTagName("autoTranslationProductivity").item(0)).getChildNodes();
        aAR.setAutomaticTranslationProductivity(Float.parseFloat(elmnts.item(0).getNodeValue()));

        COINCOMOAdaptationAndReuseManager.updateAdaptationAndReuse(aAR, false);
    }

    //TODO (Larry) Should probably refactor validateCalibrationXML(FIle) and validateXML(File) functions into one
    public static boolean validateCalibrationXML(File file) {
        final String programDir = System.getProperty("user.dir");
        final String schemaPath = programDir + File.separator + DEFAULT_CALIBRATION_XML_SCHEMA;

        try {
            // Prepare the XML document.
            Document doc = parseXML(file);

            if (doc != null) {
                // Check the XML document to verify it is indeed COINCOMO file.
                Element rootElement = doc.getDocumentElement();
                if (rootElement.getNodeName().equals("COINCOMOCalibration")) {
                    /* 
                     * Check the root element to see if there is a version attribute
                     * to indicate new XML format and proceed with new parsing functions.
                     */
                    if (rootElement.hasAttribute("version")) {
                        // "version" attribute exists, thus 2.0 or newer XML format.
                        String rootVersion = rootElement.getAttribute("version");

                        // For now there is only one new XML format with version="2.0" tag.
                        if (rootVersion.equals(DEFAULT_CALIBRATION_XML_VERSION)) {
                            try {
                                File schemaFile = new File(schemaPath);
                                Source xmlFile = new StreamSource(file);

                                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                                Schema schema = schemaFactory.newSchema(schemaFile);

                                Validator xmlValidator = schema.newValidator();

                                // Validate XML file against XML schema.
                                xmlValidator.validate(xmlFile);

                                log(Level.INFO,
                                        "COINCOMO Calibration XML file <" + file.getCanonicalPath() + "> is validated against <" + DEFAULT_CALIBRATION_XML_SCHEMA + ">.");

                                // No exception is thrown, thus XML file is valid.
                                return true;
                            } catch (SAXException e) {
                                log(Level.WARNING,
                                        "COINCOMO Calibration XML file <" + file.getCanonicalPath() + "> is not valid against <" + DEFAULT_CALIBRATION_XML_SCHEMA + ">." + "\r\n"
                                        + "\r\n"
                                        + e.getLocalizedMessage());
                            }
                        } else {
                            log(Level.WARNING,
                                    "COINCOMO Calibration XML file <" + file.getCanonicalPath() + "> version is not supported.");
                        }
                    } else {
                        // "version" attribute doesn't exist, thus legacy XML format, and assume valid.
                        log(Level.WARNING,
                                "COINCOMO Calibration XML file <" + file.getCanonicalPath() + "> version is not supported.");
                    }
                }
            }
        } catch (IOException e) {
            log(Level.SEVERE,
                    "validateCalibrationXML().IOException: " + e.getLocalizedMessage());
        }

        return false;
    }

    //TODO (Larry) 99% of the codes are exact copy of exportXML(COINCOMOSystem, File) function. Should be refactored.
    public static void exportCalibrationXML(COINCOMOLocalCalibration localCalibration, File file) throws IOException {
        // Write out the new XML format
        String newLine = "\r\n";
        String systemTabs = "\t";
        String subSystemTabs = "\t\t";
        String componentTabs = "\t\t\t";
        String subComponentTabs = "\t\t\t\t";
        String adaptationTabs = "\t\t\t\t\t";

        DecimalFormat format1Decimal = new DecimalFormat("0.0");
        DecimalFormat format2Decimals = new DecimalFormat("0.00");

        BufferedWriter out = new BufferedWriter(new FileWriter(file));

        // Generate a list of systems and projects(components) to be exported to file
        ArrayList<COINCOMOSystem> systems = localCalibration.getListOfSystemUnits();

        // COINCOMO level
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + newLine);
        out.write(newLine);
        out.write("<COINCOMOCalibration version=\"2.0\"" + newLine);
        out.write("          xmlns=\"http://csse.usc.edu\"" + newLine);
        out.write("          xmlns:nsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + newLine);
        out.write("          nsi:schemaLocation=\"http://csse.usc.edu COINCOMOCalibration2.0.xsd\">" + newLine);

        // Calibration Mode
        out.write(systemTabs + "<CalibrationMode>" + COINCOMOLocalCalibration.getCalibrationMode().toString() + "</CalibrationMode>" + newLine);

        Iterator iter = systems.iterator();
        while (iter.hasNext()) {
            final COINCOMOSystem system = (COINCOMOSystem) iter.next();

            // System level
            out.write(systemTabs + "<System>" + newLine);
            out.write(systemTabs + "\t" + "<ID>" + system.getUnitID() + "</ID>" + newLine);
            out.write(systemTabs + "\t" + "<DatabaseID>" + system.getDatabaseID() + "</DatabaseID>" + newLine);
            out.write(systemTabs + "\t" + "<_Name>" + system.getName() + "</_Name>" + newLine);
            out.write(systemTabs + "\t" + "<SLOC>" + GlobalMethods.FormatLongWithComma(system.getSLOC()) + "</SLOC>" + newLine);
            out.write(systemTabs + "\t" + "<Cost>" + format2Decimals.format(GlobalMethods.roundOff(system.getCost(), 2)) + "</Cost>" + newLine);
            out.write(systemTabs + "\t" + "<Staff>" + format1Decimal.format(GlobalMethods.roundOff(system.getStaff(), 1)) + "</Staff>" + newLine);
            out.write(systemTabs + "\t" + "<Effort>" + format2Decimals.format(GlobalMethods.roundOff(system.getEffort(), 2)) + "</Effort>" + newLine);
            out.write(systemTabs + "\t" + "<Schedule>" + format2Decimals.format(GlobalMethods.roundOff(system.getSchedule(), 2)) + "</Schedule>" + newLine);

            // <SubSystem> sections
            ArrayList<COINCOMOUnit> orderedSubSystemsVector = system.getListOfSubUnits();
            for (int i = 0; i < orderedSubSystemsVector.size(); i++) {
                // Sub System level
                final COINCOMOSubSystem subSystem = (COINCOMOSubSystem) orderedSubSystemsVector.get(i);

                out.write(subSystemTabs + "<SubSystem>" + newLine);

                out.write(subSystemTabs + "\t" + "<ID>" + subSystem.getUnitID() + "</ID>" + newLine);
                out.write(subSystemTabs + "\t" + "<DatabaseID>" + subSystem.getDatabaseID() + "</DatabaseID>" + newLine);
                out.write(subSystemTabs + "\t" + "<_Name>" + subSystem.getName() + "</_Name>" + newLine);
                out.write(subSystemTabs + "\t" + "<SLOC>" + GlobalMethods.FormatLongWithComma(subSystem.getSLOC()) + "</SLOC>" + newLine);
                out.write(subSystemTabs + "\t" + "<Cost>" + format2Decimals.format(GlobalMethods.roundOff(subSystem.getCost(), 2)) + "</Cost>" + newLine);
                out.write(subSystemTabs + "\t" + "<Staff>" + format1Decimal.format(GlobalMethods.roundOff(subSystem.getStaff(), 1)) + "</Staff>" + newLine);
                out.write(subSystemTabs + "\t" + "<Effort>" + format2Decimals.format(GlobalMethods.roundOff(subSystem.getEffort(), 2)) + "</Effort>" + newLine);
                out.write(subSystemTabs + "\t" + "<Schedule>" + format2Decimals.format(GlobalMethods.roundOff(subSystem.getSchedule(), 2)) + "</Schedule>" + newLine);
                out.write(subSystemTabs + "\t" + "<_ZoomLevel>" + subSystem.getZoomLevel() + "</_ZoomLevel>" + newLine);

                // <Component> sections
                ArrayList<COINCOMOUnit> orderedComponentsVector = subSystem.getListOfSubUnits();
                for (int j = 0; j < orderedComponentsVector.size(); j++) {
                    // Component level
                    final COINCOMOComponent component = (COINCOMOComponent) orderedComponentsVector.get(j);

                    out.write(componentTabs + "<Component>" + newLine);

                    out.write(componentTabs + "\t" + "<ID>" + component.getUnitID() + "</ID>" + newLine);
                    out.write(componentTabs + "\t" + "<DatabaseID>" + component.getDatabaseID() + "</DatabaseID>" + newLine);
                    out.write(componentTabs + "\t" + "<_Name>" + component.getName() + "</_Name>" + newLine);
                    out.write(componentTabs + "\t" + "<SLOC>" + GlobalMethods.FormatLongWithComma(component.getSLOC()) + "</SLOC>" + newLine);
                    out.write(componentTabs + "\t" + "<Cost>" + format2Decimals.format(GlobalMethods.roundOff(component.getCost(), 2)) + "</Cost>" + newLine);
                    out.write(componentTabs + "\t" + "<Staff>" + format1Decimal.format(GlobalMethods.roundOff(component.getStaff(), 1)) + "</Staff>" + newLine);
                    out.write(componentTabs + "\t" + "<Effort>" + format2Decimals.format(GlobalMethods.roundOff(component.getEffort(), 2)) + "</Effort>" + newLine);
                    out.write(componentTabs + "\t" + "<Schedule>" + format2Decimals.format(GlobalMethods.roundOff(component.getSchedule(), 2)) + "</Schedule>" + newLine);
                    out.write(componentTabs + "\t" + "<Productivity>" + format2Decimals.format(GlobalMethods.roundOff(COINCOMOComponentManager.calculateProductivity(component), 2)) + "</Productivity>" + newLine);
                    out.write(componentTabs + "\t" + "<InstructionCost>" + format2Decimals.format(GlobalMethods.roundOff(COINCOMOComponentManager.calculateInstructionCost(component), 2)) + "</InstructionCost>" + newLine);
                    out.write(componentTabs + "\t" + "<Risk>" + "0" + "</Risk>" + newLine);

                    // Component level, Parameters (i.e. Local Calibration)
                    COINCOMOComponentParameters parameters = component.getParameters();

                    if (parameters == null) {
                        log(Level.SEVERE, "COINCOMOComponent " + component.getName() + " has no associated COINCOMOComponentParameters object!");
                        parameters = new COINCOMOComponentParameters(component);
                    }
                    final double[][] eafWeights = parameters.getEAFWeights();
                    final double[][] sfWeights = parameters.getSFWeights();
                    final int[][] fpWeights = parameters.getFPWeights();

                    out.write(componentTabs + "\t" + "<ParametersSettings>" + newLine);

                    // Effort Adjustment Factors section
                    out.write(componentTabs + "\t\t" + "<EffortAdjustmentFactors>" + newLine);

                    for (int k = 0; k < COINCOMOConstants.EAFS.length; k++) {
                        out.write(componentTabs + "\t\t\t" + "<_" + COINCOMOConstants.EAFS[k] + ">" + newLine);
                        for (int l = 0; l < COINCOMOConstants.Ratings.length; l++) {
                            out.write(componentTabs + "\t\t\t\t" + "<_" + COINCOMOConstants.Ratings[l] + ">" + eafWeights[k][l] + "</_" + COINCOMOConstants.Ratings[l] + ">" + newLine);
                        }
                        out.write(componentTabs + "\t\t\t" + "</_" + COINCOMOConstants.EAFS[k] + ">" + newLine);
                    }

                    out.write(componentTabs + "\t\t" + "</EffortAdjustmentFactors>" + newLine);

                    // Scale Factors section
                    out.write(componentTabs + "\t\t" + "<ScaleFactors>" + newLine);

                    for (int k = 0; k < COINCOMOConstants.SFS.length; k++) {
                        out.write(componentTabs + "\t\t\t" + "<_" + COINCOMOConstants.SFS[k] + ">" + newLine);
                        for (int l = 0; l < COINCOMOConstants.Ratings.length; l++) {
                            out.write(componentTabs + "\t\t\t\t" + "<_" + COINCOMOConstants.Ratings[l] + ">" + sfWeights[k][l] + "</_" + COINCOMOConstants.Ratings[l] + ">" + newLine);
                        }
                        out.write(componentTabs + "\t\t\t" + "</_" + COINCOMOConstants.SFS[k] + ">" + newLine);
                    }

                    out.write(componentTabs + "\t\t" + "</ScaleFactors>" + newLine);

                    // Equation Editors section
                    out.write(componentTabs + "\t\t" + "<EquationEditor>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<_EffortEstimationParameterA>" + parameters.getA() + "</_EffortEstimationParameterA>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<_ExponentParameterB>" + parameters.getB() + "</_ExponentParameterB>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<_ScheduleEstimationParameterC>" + parameters.getC() + "</_ScheduleEstimationParameterC>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<_ScheduleEstimationParameterD>" + parameters.getD() + "</_ScheduleEstimationParameterD>" + newLine);
                    out.write(componentTabs + "\t\t" + "</EquationEditor>" + newLine);

                    // Function Points section
                    out.write(componentTabs + "\t\t" + "<FunctionPoints>" + newLine);

                    for (int k = 0; k < COINCOMOConstants.FPS2.length; k++) {
                        out.write(componentTabs + "\t\t\t" + "<_" + COINCOMOConstants.FPS2[k] + ">" + newLine);
                        for (int l = 0; l < COINCOMOConstants.FTS.length - 1; l++) {
                            out.write(componentTabs + "\t\t\t\t" + "<_" + COINCOMOConstants.FTS[l] + ">" + fpWeights[k][l] + "</_" + COINCOMOConstants.FTS[l] + ">" + newLine);
                        }
                        out.write(componentTabs + "\t\t\t" + "</_" + COINCOMOConstants.FPS2[k] + ">" + newLine);
                    }

                    out.write(componentTabs + "\t\t" + "</FunctionPoints>" + newLine);

                    // Person Month section
                    out.write(componentTabs + "\t\t" + "<PersonMonth>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<_HoursPerPM>" + parameters.getWorkHours() + "</_HoursPerPM>" + newLine);
                    out.write(componentTabs + "\t\t" + "</PersonMonth>" + newLine);

                    out.write(componentTabs + "\t" + "</ParametersSettings>" + newLine);

                    // Scale Factors Settings section
                    out.write(componentTabs + "\t" + "<ScaleFactor>" + component.getSF() + "</ScaleFactor>" + newLine);

                    final Rating sfRatings[] = component.getSFRatings();
                    final Increment sfIncrements[] = component.getSFIncrements();

                    out.write(componentTabs + "\t" + "<ScaleFactorsSettings>" + newLine);
                    for (int k = 0; k < COINCOMOConstants.SFS.length; k++) {
                        out.write(componentTabs + "\t\t" + "<_" + COINCOMOConstants.SFS[k] + ">" + newLine);
                        out.write(componentTabs + "\t\t\t" + "<_Rating>" + sfRatings[k].toString() + "</_Rating>" + newLine);
                        out.write(componentTabs + "\t\t\t" + "<_Increment>" + sfIncrements[k].toString() + "</_Increment>" + newLine);
                        out.write(componentTabs + "\t\t" + "</_" + COINCOMOConstants.SFS[k] + ">" + newLine);
                    }
                    out.write(componentTabs + "\t" + "</ScaleFactorsSettings>" + newLine);

                    // Schedule Settings section
                    out.write(componentTabs + "\t" + "<ScheduleFactor>" + component.getSCED() + "</ScheduleFactor>" + newLine);
                    out.write(componentTabs + "\t" + "<SchedulePercentFactor>" + component.getSCEDPercent() + "</SchedulePercentFactor>" + newLine);

                    out.write(componentTabs + "\t" + "<ScheduleSettings>" + newLine);
                    out.write(componentTabs + "\t\t" + "<_SCED>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<_Rating>" + component.getSCEDRating().toString() + "</_Rating>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<_Increment>" + component.getSCEDIncrement().toString() + "</_Increment>" + newLine);
                    out.write(componentTabs + "\t\t" + "</_SCED>" + newLine);
                    out.write(componentTabs + "\t" + "</ScheduleSettings>" + newLine);

                    // COPSEMO section
                    out.write(componentTabs + "\t" + "<COPSEMO>" + newLine);
                    out.write(componentTabs + "\t\t" + "<_Inception>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<_EffortPercentage>" + component.getInceptionEffortPercentage() + "</_EffortPercentage>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<_SchedulePercentage>" + component.getInceptionSchedulePercentage() + "</_SchedulePercentage>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<Effort>" + component.getInceptionEffort() + "</Effort>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<Month>" + component.getInceptionMonth() + "</Month>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<Personnel>" + component.getInceptionPersonnel() + "</Personnel>" + newLine);
                    out.write(componentTabs + "\t\t" + "</_Inception>" + newLine);
                    out.write(componentTabs + "\t\t" + "<_Elaboration>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<_EffortPercentage>" + component.getElaborationEffortPercentage() + "</_EffortPercentage>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<_SchedulePercentage>" + component.getElaborationSchedulePercentage() + "</_SchedulePercentage>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<Effort>" + component.getElaborationEffort() + "</Effort>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<Month>" + component.getElaborationMonth() + "</Month>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<Personnel>" + component.getElaborationPersonnel() + "</Personnel>" + newLine);
                    out.write(componentTabs + "\t\t" + "</_Elaboration>" + newLine);
                    out.write(componentTabs + "\t\t" + "<_Construction>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<_EffortPercentage>" + component.getConstructionEffortPercentage() + "</_EffortPercentage>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<_SchedulePercentage>" + component.getConstructionSchedulePercentage() + "</_SchedulePercentage>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<Effort>" + component.getConstructionEffort() + "</Effort>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<Month>" + component.getConstructionMonth() + "</Month>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<Personnel>" + component.getConstructionPersonnel() + "</Personnel>" + newLine);
                    out.write(componentTabs + "\t\t" + "</_Construction>" + newLine);
                    out.write(componentTabs + "\t\t" + "<_Transition>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<_EffortPercentage>" + component.getTransitionEffortPercentage() + "</_EffortPercentage>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<_SchedulePercentage>" + component.getTransitionSchedulePercentage() + "</_SchedulePercentage>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<Effort>" + component.getTransitionEffort() + "</Effort>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<Month>" + component.getTransitionMonth() + "</Month>" + newLine);
                    out.write(componentTabs + "\t\t\t" + "<Personnel>" + component.getTransitionPersonnel() + "</Personnel>" + newLine);
                    out.write(componentTabs + "\t\t" + "</_Transition>" + newLine);
                    out.write(componentTabs + "\t" + "</COPSEMO>" + newLine);

                    out.write(componentTabs + "\t" + "<_MultiBuildShift>" + component.getMultiBuildShift() + "</_MultiBuildShift>" + newLine);

                    // <SubComponent> sections
                    ArrayList<COINCOMOUnit> orderedSubComponentsVector = component.getListOfSubUnits();
                    for (int k = 0; k < orderedSubComponentsVector.size(); k++) {
                        // Sub Component level
                        final COINCOMOSubComponent subComponent = (COINCOMOSubComponent) orderedSubComponentsVector.get(k);

                        out.write(subComponentTabs + "<SubComponent>" + newLine);

                        out.write(subComponentTabs + "\t" + "<ID>" + subComponent.getUnitID() + "</ID>" + newLine);
                        out.write(subComponentTabs + "\t" + "<DatabaseID>" + subComponent.getDatabaseID() + "</DatabaseID>" + newLine);
                        out.write(subComponentTabs + "\t" + "<_Name>" + subComponent.getName() + "</_Name>" + newLine);
                        out.write(subComponentTabs + "\t" + "<SLOC>" + GlobalMethods.FormatLongWithComma(subComponent.getSLOC()) + "</SLOC>" + newLine);
                        out.write(subComponentTabs + "\t" + "<Cost>" + subComponent.getCost() + "</Cost>" + newLine);
                        out.write(subComponentTabs + "\t" + "<Staff>" + subComponent.getStaff() + "</Staff>" + newLine);
                        out.write(subComponentTabs + "\t" + "<Effort>" + subComponent.getEffort() + "</Effort>" + newLine);
                        out.write(subComponentTabs + "\t" + "<Schedule>" + subComponent.getSchedule() + "</Schedule>" + newLine);
                        out.write(subComponentTabs + "\t" + "<Productivity>" + subComponent.getProductivity() + "</Productivity>" + newLine);
                        out.write(subComponentTabs + "\t" + "<InstructionCost>" + subComponent.getInstructionCost() + "</InstructionCost>" + newLine);
                        out.write(subComponentTabs + "\t" + "<Risk>" + subComponent.getRisk() + "</Risk>" + newLine);
                        out.write(subComponentTabs + "\t" + "<NominalEffort>" + subComponent.getNominalEffort() + "</NominalEffort>" + newLine);
                        out.write(subComponentTabs + "\t" + "<EstimatedEffort>" + subComponent.getEstimatedEffort() + "</EstimatedEffort>" + newLine);
                        out.write(subComponentTabs + "\t" + "<SLOCWithoutREVL>" + GlobalMethods.FormatLongWithComma(subComponent.getSumOfSLOCs()) + "</SLOCWithoutREVL>" + newLine);
                        out.write(subComponentTabs + "\t" + "<_LaborRate>" + subComponent.getLaborRate() + "</_LaborRate>" + newLine);
                        out.write(subComponentTabs + "\t" + "<_Language>" + subComponent.getLanguage() + "</_Language>" + newLine);

                        // EAF Settings section
                        out.write(subComponentTabs + "\t" + "<EAF>" + subComponent.getEAF() + "</EAF>" + newLine);

                        out.write(subComponentTabs + "\t" + "<EAFSettings>" + newLine);
                        final Rating[] eafRatings = subComponent.getEAFRatings();
                        final Increment[] eafIncrements = subComponent.getEAFIncrements();
                        for (int l = 0; l < COINCOMOConstants.EAFS.length - 1; l++) {
                            out.write(subComponentTabs + "\t\t" + "<_" + COINCOMOConstants.EAFS[l] + ">" + newLine);
                            out.write(subComponentTabs + "\t\t\t" + "<_Rating>" + eafRatings[l].toString() + "</_Rating>" + newLine);
                            out.write(subComponentTabs + "\t\t\t" + "<_Increment>" + eafIncrements[l].toString() + "</_Increment>" + newLine);
                            out.write(subComponentTabs + "\t\t" + "</_" + COINCOMOConstants.EAFS[l] + ">" + newLine);
                        }
                        out.write(subComponentTabs + "\t" + "</EAFSettings>" + newLine);

                        out.write(subComponentTabs + "\t" + "<_Breakage>" + subComponent.getREVL() + "</_Breakage>" + newLine);

                        // New SLOC section
                        out.write(subComponentTabs + "\t" + "<New>" + newLine);
                        out.write(subComponentTabs + "\t\t" + "<_NewSLOC>" + GlobalMethods.FormatLongWithComma(subComponent.getNewSLOC()) + "</_NewSLOC>" + newLine);
                        out.write(subComponentTabs + "\t" + "</New>" + newLine);

                        // Function Points section
                        out.write(subComponentTabs + "\t" + "<FunctionPoints>" + newLine);

                        out.write(subComponentTabs + "\t\t" + "<_Multiplier>" + subComponent.getMultiplier() + "</_Multiplier>" + newLine);
                        out.write(subComponentTabs + "\t\t" + "<_RatioType>" + subComponent.getRatioType().toString() + "</_RatioType>" + newLine);
                        out.write(subComponentTabs + "\t\t" + "<_CalculationMethod>" + subComponent.getCalculationMethod().toString() + "</_CalculationMethod>" + newLine);

                        out.write(subComponentTabs + "\t\t" + "<FunctionTypes>" + newLine);
                        // Internal Logical Files (Files) part
                        out.write(subComponentTabs + "\t\t\t" + "<_InternalLogicalFiles>" + newLine);
                        final int files[] = subComponent.getInternalLogicalFiles();
                        for (int l = 0; l < COINCOMOConstants.FTS.length - 1; l++) {
                            out.write(subComponentTabs + "\t\t\t\t" + "<_" + COINCOMOConstants.FTS[l] + ">" + files[l] + "</_" + COINCOMOConstants.FTS[l] + ">" + newLine);
                        }
                        out.write(subComponentTabs + "\t\t\t\t" + "<" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + files[COINCOMOConstants.FTS.length - 1] + "</" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + newLine);
                        out.write(subComponentTabs + "\t\t\t" + "</_InternalLogicalFiles>" + newLine);
                        // External Interface Files (Interfaces) part
                        out.write(subComponentTabs + "\t\t\t" + "<_ExternalInterfaceFiles>" + newLine);
                        final int interfaces[] = subComponent.getExternalInterfaceFiles();
                        for (int l = 0; l < COINCOMOConstants.FTS.length - 1; l++) {
                            out.write(subComponentTabs + "\t\t\t\t" + "<_" + COINCOMOConstants.FTS[l] + ">" + interfaces[l] + "</_" + COINCOMOConstants.FTS[l] + ">" + newLine);
                        }
                        out.write(subComponentTabs + "\t\t\t\t" + "<" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + interfaces[COINCOMOConstants.FTS.length - 1] + "</" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + newLine);
                        out.write(subComponentTabs + "\t\t\t" + "</_ExternalInterfaceFiles>" + newLine);
                        // External Inputs (Inputs) part
                        out.write(subComponentTabs + "\t\t\t" + "<_ExternalInputs>" + newLine);
                        final int inputs[] = subComponent.getExternalInputs();
                        for (int l = 0; l < COINCOMOConstants.FTS.length - 1; l++) {
                            out.write(subComponentTabs + "\t\t\t\t" + "<_" + COINCOMOConstants.FTS[l] + ">" + inputs[l] + "</_" + COINCOMOConstants.FTS[l] + ">" + newLine);
                        }
                        out.write(subComponentTabs + "\t\t\t\t" + "<" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + inputs[COINCOMOConstants.FTS.length - 1] + "</" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + newLine);
                        out.write(subComponentTabs + "\t\t\t" + "</_ExternalInputs>" + newLine);
                        // External Outputs (Outputs) part
                        out.write(subComponentTabs + "\t\t\t" + "<_ExternalOutputs>" + newLine);
                        final int outputs[] = subComponent.getExternalOutputs();
                        for (int l = 0; l < COINCOMOConstants.FTS.length - 1; l++) {
                            out.write(subComponentTabs + "\t\t\t\t" + "<_" + COINCOMOConstants.FTS[l] + ">" + outputs[l] + "</_" + COINCOMOConstants.FTS[l] + ">" + newLine);
                        }
                        out.write(subComponentTabs + "\t\t\t\t" + "<" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + outputs[COINCOMOConstants.FTS.length - 1] + "</" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + newLine);
                        out.write(subComponentTabs + "\t\t\t" + "</_ExternalOutputs>" + newLine);
                        // External Inquiries (Inquiries) part
                        out.write(subComponentTabs + "\t\t\t" + "<_ExternalInquiries>" + newLine);
                        final int inquiries[] = subComponent.getExternalInquiries();
                        for (int l = 0; l < COINCOMOConstants.FTS.length - 1; l++) {
                            out.write(subComponentTabs + "\t\t\t\t" + "<_" + COINCOMOConstants.FTS[l] + ">" + inquiries[l] + "</_" + COINCOMOConstants.FTS[l] + ">" + newLine);
                        }
                        out.write(subComponentTabs + "\t\t\t\t" + "<" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + inquiries[COINCOMOConstants.FTS.length - 1] + "</" + COINCOMOConstants.FTS[COINCOMOConstants.FTS.length - 1] + ">" + newLine);
                        out.write(subComponentTabs + "\t\t\t" + "</_ExternalInquiries>" + newLine);
                        out.write(subComponentTabs + "\t\t" + "</FunctionTypes>" + newLine);

                        out.write(subComponentTabs + "\t\t" + "<TotalUnadjustedFunctionPoints>" + subComponent.getTotalUnadjustedFunctionPoints() + "</TotalUnadjustedFunctionPoints>" + newLine);
                        out.write(subComponentTabs + "\t\t" + "<EquivalentSLOC>" + GlobalMethods.FormatLongWithComma(subComponent.getEquivalentSLOC()) + "</EquivalentSLOC>" + newLine);

                        out.write(subComponentTabs + "\t" + "</FunctionPoints>" + newLine);

                        // <AdaptationAndReuse> sections
                        ArrayList<COINCOMOUnit> orderedAdaptationAndReusesVector = subComponent.getListOfSubUnits();
                        for (int l = 0; l < orderedAdaptationAndReusesVector.size(); l++) {
                            // A&R level
                            final COINCOMOAdaptationAndReuse aAR = (COINCOMOAdaptationAndReuse) orderedAdaptationAndReusesVector.get(l);

                            out.write(adaptationTabs + "<AdaptationAndReuse>" + newLine);

                            out.write(adaptationTabs + "\t" + "<ID>" + aAR.getUnitID() + "</ID>" + newLine);
                            out.write(adaptationTabs + "\t" + "<DatabaseID>" + aAR.getDatabaseID() + "</DatabaseID>" + newLine);
                            out.write(adaptationTabs + "\t" + "<_Name>" + aAR.getName() + "</_Name>" + newLine);
                            out.write(adaptationTabs + "\t" + "<_AdaptedSLOC>" + GlobalMethods.FormatLongWithComma(aAR.getAdaptedSLOC()) + "</_AdaptedSLOC>" + newLine);
                            out.write(adaptationTabs + "\t" + "<_DesignModified>" + aAR.getDesignModified() + "</_DesignModified>" + newLine);
                            out.write(adaptationTabs + "\t" + "<_CodeModified>" + aAR.getCodeModified() + "</_CodeModified>" + newLine);
                            out.write(adaptationTabs + "\t" + "<_IntegrationModified>" + aAR.getIntegrationModified() + "</_IntegrationModified>" + newLine);
                            out.write(adaptationTabs + "\t" + "<_SoftwareUnderstanding>" + aAR.getSoftwareUnderstanding() + "</_SoftwareUnderstanding>" + newLine);
                            out.write(adaptationTabs + "\t" + "<_AssessmentAndAssimilation>" + aAR.getAssessmentAndAssimilation() + "</_AssessmentAndAssimilation>" + newLine);
                            out.write(adaptationTabs + "\t" + "<_UnfamiliarityWithSoftware>" + aAR.getUnfamiliarityWithSoftware() + "</_UnfamiliarityWithSoftware>" + newLine);
                            out.write(adaptationTabs + "\t" + "<_AutomaticallyTranslated>" + aAR.getAutomaticallyTranslated() + "</_AutomaticallyTranslated>" + newLine);
                            out.write(adaptationTabs + "\t" + "<_AutomaticTranslationProductivity>" + aAR.getAutomaticTranslationProductivity() + "</_AutomaticTranslationProductivity>" + newLine);
                            out.write(adaptationTabs + "\t" + "<AdaptationAdjustmentFactor>" + aAR.getAdaptationAdjustmentFactor() + "</AdaptationAdjustmentFactor>" + newLine);
                            out.write(adaptationTabs + "\t" + "<EquivalentSLOC>" + GlobalMethods.FormatLongWithComma(aAR.getEquivalentSLOC()) + "</EquivalentSLOC>" + newLine);

                            // End of A&R level
                            out.write(adaptationTabs + "</AdaptationAndReuse>" + newLine);
                        }

                        // End of Sub Component level
                        out.write(subComponentTabs + "</SubComponent>" + newLine);
                    }

                    // Local Calibration Specific parts
                    COINCOMOLocalCalibrationProject project = localCalibration.getProject(component);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    if (project != null) {
                        out.write(componentTabs + "\t" + "<Visible>" + true + "</Visible>" + newLine);
                        out.write(componentTabs + "\t" + "<_Selected>" + project.isSelected() + "</_Selected>" + newLine);
                        out.write(componentTabs + "\t" + "<EffectiveEAF>" + project.getEAF() + "</EffectiveEAF>" + newLine);
                        out.write(componentTabs + "\t" + "<Date>" + dateFormat.format(project.getDate()) + "</Date>" + newLine);
                        out.write(componentTabs + "\t" + "<_ActualEffort>" + project.getEffort() + "</_ActualEffort>" + newLine);
                        out.write(componentTabs + "\t" + "<_ActualSchedule>" + project.getSchedule() + "</_ActualSchedule>" + newLine);
                    } else {
                        out.write(componentTabs + "\t" + "<Visible>" + false + "</Visible>" + newLine);
                        out.write(componentTabs + "\t" + "<_Selected>" + false + "</_Selected>" + newLine);
                        out.write(componentTabs + "\t" + "<EffectiveEAF>" + "0.0" + "</EffectiveEAF>" + newLine);
                        out.write(componentTabs + "\t" + "<Date>" + dateFormat.format(new Date()) + "</Date>" + newLine);
                        out.write(componentTabs + "\t" + "<_ActualEffort>" + "0.0" + "</_ActualEffort>" + newLine);
                        out.write(componentTabs + "\t" + "<_ActualSchedule>" + "0.0" + "</_ActualSchedule>" + newLine);
                    }

                    // End of Component level
                    out.write(componentTabs + "</Component>" + newLine);
                }

                // End of Sub System level
                out.write(subSystemTabs + "</SubSystem>" + newLine);
            }

            // End of System level
            out.write(systemTabs + "</System>" + newLine);
        }

        // End of COINCOMO level
        out.write("</COINCOMOCalibration>");

        // Flush out the buffer
        out.flush();

        // Close the file
        out.close();
    }

    public static COINCOMOLocalCalibration importCalibrationXML(File file) {
        COINCOMOLocalCalibration localCalibration = new COINCOMOLocalCalibration();

        // Prepare the XML document.
        Document doc = parseXML(file);

        if (doc != null) {
            // Retrieve the document element, i.e., <COINCOMO>.
            Element rootElement = doc.getDocumentElement();

            // Check the root element to see if there is a version attribute
            // to indicate new XML format and proceed with new parsing functions.
            if (rootElement.hasAttribute("version")) {
                String rootVersion = rootElement.getAttribute("version");

                //TODO (Larry) Need to be able to import multiple systems!
                // Start parsing the XML file
                boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
                COINCOMO.setIgnoreDatabaseMode(true);
                
                NodeList systemNodes = rootElement.getElementsByTagName("System");
                for (int i = 0; i < systemNodes.getLength(); i++) {
                    Element systemElement = (Element) rootElement.getElementsByTagName("System").item(i);
                // Check against the database
                /*
                if (COINCOMOSystemManager.hasSystemName(systemElement.getElementsByTagName("_Name").item(0).getFirstChild().getNodeValue())) {
                    return null;
                }
                */
                // Create the local calibration
                    COINCOMOSystem system = null;
                    system = COINCOMOSystemManager.insertSystem();
                /*
                if (OperationMode.DESKTOP == COINCOMO.getOperationMode()) {
                    // If the system is belonging to database record, we want to keep the system ID intact while in desktop mode
                    system.setDatabaseID(Long.parseLong(systemElement.getElementsByTagName("DatabaseID").item(0).getFirstChild().getNodeValue()));
                }
                */
                // Populate the system
                    populateCalibrationSystem(systemElement, system, rootVersion.toString(), localCalibration);
                }

                COINCOMO.setIgnoreDatabaseMode(ignoreDatabaseMode);
            } else {
                return null;
            }
        }

        return localCalibration;
    }

    private static void populateCalibrationSystem(Element sysElmnt, COINCOMOSystem sys, String version, COINCOMOLocalCalibration localCalibration) {

        // Check to see if the XML format is 2.0 or not.
        if (version.equals("2.0")) {
            //Populate <Name> node
            sys.setName(sysElmnt.getElementsByTagName("_Name").item(0).getFirstChild().getNodeValue());
            //sys.setDatabaseID(Long.parseLong(sysElmnt.getElementsByTagName("DatabaseID").item(0).getFirstChild().getNodeValue()));

            //Populate <SubSystem> nodes
            NodeList subsystems = sysElmnt.getElementsByTagName("SubSystem");

            for (int i = 0; i < subsystems.getLength(); i++) {
                Element subsystemElement = (Element) subsystems.item(i);
                //Create the subsystem
                COINCOMOSubSystem subsys = COINCOMOSubSystemManager.insertSubSystem(sys);
                //Populate the subsystem
                populateCalibrationSubSystem(subsystemElement, subsys, version, localCalibration);
            }

            // Reflect the changes to underlying database.
            COINCOMOSystemManager.updateSystem(sys);

            return;
        }
    }

    private static void populateCalibrationSubSystem(Element subsysElmnt, COINCOMOSubSystem subsys, String version, COINCOMOLocalCalibration localCalibration) {

        // Check to see if the XML format is 2.0 or not.
        if (version.equals("2.0")) {
            //Populate <Name> node
            subsys.setName(subsysElmnt.getElementsByTagName("_Name").item(0).getFirstChild().getNodeValue());
            //subsys.setDatabaseID(Long.parseLong(subsysElmnt.getElementsByTagName("DatabaseID").item(0).getFirstChild().getNodeValue()));

            //Populate <ZoomLevel> node
            subsys.setZoomLevel(Integer.parseInt(subsysElmnt.getElementsByTagName("_ZoomLevel").item(0).getFirstChild().getNodeValue()));

            //Populate <Component> nodes
            NodeList components = subsysElmnt.getElementsByTagName("Component");

            for (int i = 0; i < components.getLength(); i++) {
                Element componentElement = (Element) components.item(i);
                //Create the component
                COINCOMOComponent component = COINCOMOComponentManager.insertComponent(subsys);
                //Populate the component
                populateCalibrationComponent(componentElement, component, version, localCalibration);
            }

            // Reflect the changes to underlying database.
            COINCOMOSubSystemManager.updateSubSystem(subsys, false);

            return;
        }
    }

    private static void populateCalibrationComponent(Element compElmnt, COINCOMOComponent comp, String version, COINCOMOLocalCalibration localCalibration) {

        // Check to see if the XML format is 2.0 or not.
        if (version.equals("2.0")) {
            //Populate <Name> node
            comp.setName(compElmnt.getElementsByTagName("_Name").item(0).getFirstChild().getNodeValue());
            //comp.setDatabaseID(Long.parseLong(compElmnt.getElementsByTagName("DatabaseID").item(0).getFirstChild().getNodeValue()));

            //Populate the Local Calibration parameters <EffortAdjustmentFactors>, <ScaleFactors>, <EquationEditors>, <FunctionPoints>, <PersonMonth>
            COINCOMOComponentParameters parameters = comp.getParameters();

            //Populate <EffortAdjustmentFactors> section
            Element EAFs = (Element) compElmnt.getElementsByTagName("EffortAdjustmentFactors").item(0);
            double[][] eafWeights = new double[COINCOMOConstants.EAFS.length][COINCOMOConstants.Ratings.length];

            for (int i = 0; i < COINCOMOConstants.EAFS.length; i++) {
                Element eaf = (Element) EAFs.getElementsByTagName("_" + COINCOMOConstants.EAFS[i]).item(0);

                for (int j = 0; j < COINCOMOConstants.Ratings.length; j++) {
                    eafWeights[i][j] = Double.parseDouble(eaf.getElementsByTagName("_" + COINCOMOConstants.Ratings[j]).item(0).getFirstChild().getNodeValue());
                }
            }
            parameters.setEAFWeights(eafWeights);

            //Populate <ScaleFactors> section
            Element SFs = (Element) compElmnt.getElementsByTagName("ScaleFactors").item(0);
            double[][] sfWeights = new double[COINCOMOConstants.SFS.length][COINCOMOConstants.Ratings.length];

            for (int i = 0; i < COINCOMOConstants.SFS.length; i++) {
                Element sf = (Element) SFs.getElementsByTagName("_" + COINCOMOConstants.SFS[i]).item(0);

                for (int j = 0; j < COINCOMOConstants.Ratings.length; j++) {
                    sfWeights[i][j] = Double.parseDouble(sf.getElementsByTagName("_" + COINCOMOConstants.Ratings[j]).item(0).getFirstChild().getNodeValue());
                }
            }
            parameters.setSFWeights(sfWeights);

            //Populate <EquationEditors> section
            Element EEs = (Element) compElmnt.getElementsByTagName("EquationEditor").item(0);

            parameters.setA(Double.parseDouble(EEs.getElementsByTagName("_EffortEstimationParameterA").item(0).getFirstChild().getNodeValue()));
            parameters.setB(Double.parseDouble(EEs.getElementsByTagName("_ExponentParameterB").item(0).getFirstChild().getNodeValue()));
            parameters.setC(Double.parseDouble(EEs.getElementsByTagName("_ScheduleEstimationParameterC").item(0).getFirstChild().getNodeValue()));
            parameters.setD(Double.parseDouble(EEs.getElementsByTagName("_ScheduleEstimationParameterD").item(0).getFirstChild().getNodeValue()));

            //Populate <FunctionPoints> section
            Element FPs = (Element) compElmnt.getElementsByTagName("FunctionPoints").item(0);
            int[][] fpWeights = new int[COINCOMOConstants.FPS.length][COINCOMOConstants.FTS.length - 1];

            for (int i = 0; i < COINCOMOConstants.FPS2.length; i++) {
                Element sf = (Element) FPs.getElementsByTagName("_" + COINCOMOConstants.FPS2[i]).item(0);

                for (int j = 0; j < COINCOMOConstants.FTS.length - 1; j++) {
                    fpWeights[i][j] = Integer.parseInt(sf.getElementsByTagName("_" + COINCOMOConstants.FTS[j]).item(0).getFirstChild().getNodeValue());
                }
            }
            parameters.setFPWeights(fpWeights);

            //Populate <PersonMonth> section
            Element PM = (Element) compElmnt.getElementsByTagName("PersonMonth").item(0);

            parameters.setWorkHours(Double.parseDouble(PM.getElementsByTagName("_HoursPerPM").item(0).getFirstChild().getNodeValue()));

            //Populate <ScaleFactorsSettings> section
            Element SFSs = (Element) compElmnt.getElementsByTagName("ScaleFactorsSettings").item(0);

            Rating sfRatings[] = new Rating[COINCOMOConstants.SFS.length];
            Increment sfIncrements[] = new Increment[COINCOMOConstants.SFS.length];

            for (int i = 0; i < COINCOMOConstants.SFS.length; i++) {
                Element sf = (Element) SFSs.getElementsByTagName("_" + COINCOMOConstants.SFS[i]).item(0);

                sfRatings[i] = Rating.valueOf(sf.getElementsByTagName("_Rating").item(0).getFirstChild().getNodeValue());
                sfIncrements[i] = Increment.getValueOf(sf.getElementsByTagName("_Increment").item(0).getFirstChild().getNodeValue());
            }

            comp.setSFRatings(sfRatings);
            comp.setSFIncrements(sfIncrements);

            //Populate <ScheduleSettings> section
            Element SSs = (Element) compElmnt.getElementsByTagName("ScheduleSettings").item(0);
            Element s = (Element) SSs.getElementsByTagName("_SCED").item(0);

            comp.setSCEDRating(Rating.valueOf(s.getElementsByTagName("_Rating").item(0).getFirstChild().getNodeValue()));
            comp.setSCEDIncrement(Increment.getValueOf(s.getElementsByTagName("_Increment").item(0).getFirstChild().getNodeValue()));

            //Populate <COPSEMO> nodes
            Element COPSEMO = (Element) compElmnt.getElementsByTagName("COPSEMO").item(0);

            for (int i = 0; i < COINCOMOConstants.COPSEMOS.length; i++) {
                Element stage = (Element) COPSEMO.getElementsByTagName("_" + COINCOMOConstants.COPSEMOS[i]).item(0);

                if (stage.getNodeName().equals("_Inception")) {
                    comp.setInceptionEffortPercentage(Double.parseDouble(stage.getElementsByTagName("_EffortPercentage").item(0).getFirstChild().getNodeValue()));
                    comp.setInceptionSchedulePercentage(Double.parseDouble(stage.getElementsByTagName("_SchedulePercentage").item(0).getFirstChild().getNodeValue()));
                } else if (stage.getNodeName().equals("_Elaboration")) {
                    comp.setElaborationEffortPercentage(Double.parseDouble(stage.getElementsByTagName("_EffortPercentage").item(0).getFirstChild().getNodeValue()));
                    comp.setElaborationSchedulePercentage(Double.parseDouble(stage.getElementsByTagName("_SchedulePercentage").item(0).getFirstChild().getNodeValue()));
                } else if (stage.getNodeName().equals("_Construction")) {
                    comp.setConstructionEffortPercentage(Double.parseDouble(stage.getElementsByTagName("_EffortPercentage").item(0).getFirstChild().getNodeValue()));
                    comp.setConstructionSchedulePercentage(Double.parseDouble(stage.getElementsByTagName("_SchedulePercentage").item(0).getFirstChild().getNodeValue()));
                } else if (stage.getNodeName().equals("_Transition")) {
                    comp.setTransitionEffortPercentage(Double.parseDouble(stage.getElementsByTagName("_EffortPercentage").item(0).getFirstChild().getNodeValue()));
                    comp.setTransitionSchedulePercentage(Double.parseDouble(stage.getElementsByTagName("_SchedulePercentage").item(0).getFirstChild().getNodeValue()));
                } else {
                    System.out.println("COINCOMOSystemManager.populateComponent() i in COINCOMOConstants.COPSEMOS[] out of bound!");
                }
            }

            //Populate <MultiBuildShift> node
            comp.setMultiBuildShift(Integer.parseInt(compElmnt.getElementsByTagName("_MultiBuildShift").item(0).getFirstChild().getNodeValue()));

            //Populate <SubComponent> nodes
            NodeList subComponents = compElmnt.getElementsByTagName("SubComponent");

            for (int i = 0; i < subComponents.getLength(); i++) {
                Element subComponentElement = (Element) subComponents.item(i);
                //Create the component
                COINCOMOSubComponent subComponent = COINCOMOSubComponentManager.insertSubComponent(comp);
                //Populate the component
                populateSubComponent(subComponentElement, subComponent, version);
            }
            
            // Reflect the changes to underlying database.
            COINCOMOComponentManager.updateComponent(comp, false);

            //Populate Local Calibration related nodes
            double effectiveEAF = Double.parseDouble(compElmnt.getElementsByTagName("EffectiveEAF").item(0).getFirstChild().getNodeValue());
            boolean visible = Boolean.parseBoolean(compElmnt.getElementsByTagName("Visible").item(0).getFirstChild().getNodeValue());
            boolean selected = Boolean.parseBoolean(compElmnt.getElementsByTagName("_Selected").item(0).getFirstChild().getNodeValue());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date;
            try {
                date = dateFormat.parse(compElmnt.getElementsByTagName("Date").item(0).getFirstChild().getNodeValue());
            } catch (ParseException ex) {
                log(Level.SEVERE, ex.getLocalizedMessage());
                date = new Date();
            }
            double actualEffort = Double.parseDouble(compElmnt.getElementsByTagName("_ActualEffort").item(0).getFirstChild().getNodeValue());
            double actualSchedule = Double.parseDouble(compElmnt.getElementsByTagName("_ActualSchedule").item(0).getFirstChild().getNodeValue());

            if (visible) {
                COINCOMOLocalCalibrationProject project = new COINCOMOLocalCalibrationProject(comp);
                if (effectiveEAF != project.getEAF()) {
                    log(Level.WARNING, "The stored effective EAF is not the same as recalculated effective EAF for component '" + comp.getName() + "'.");
                }
                project.setSelected(selected);
                project.setDate(date);
                project.setEffort(actualEffort);
                project.setSchedule(actualSchedule);
                
                localCalibration.addSubUnit(project);
            }

            return;
        }
    }

    private static void log(Level level, String message) {
        Logger.getLogger(COINCOMOXML.class.getName()).log(level, message);
    }
}
