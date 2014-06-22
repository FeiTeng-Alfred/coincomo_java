/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package database;

import core.COINCOMOComponent;
import core.COINCOMOComponentParameters;
import core.COINCOMOConstants.LocalCalibrationMode;
import core.COINCOMOLocalCalibration;
import core.COINCOMOLocalCalibrationProject;
import core.COINCOMOUnit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import main.COINCOMOXML;

/**
 *
 * @author Larry
 */
public class COINCOMOLocalCalibrationManager {

    public static boolean saveLocalCalibrationAsXML(COINCOMOLocalCalibration localCalibration, File file) {
        try {
            COINCOMOXML.exportCalibrationXML(localCalibration, file);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "File couldn't be saved to the selected location. " + ex.getMessage(), "SAVE ERROR", 0);
            log(Level.SEVERE, ex.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public static void calculateCoefficientsOnly(COINCOMOLocalCalibration localCalibration) {
        LocalCalibrationMode localCalibrationMode = COINCOMOLocalCalibration.getCalibrationMode();
        double effortCoefficient = 0.0d;
        double scheduleCoefficient = 0.0d;

        if (localCalibration != null) {
            int selectedCount = 0;
            ArrayList<COINCOMOUnit> projects = localCalibration.getListOfSubUnits();
            double effortSumX = 0.0d;
            double effortSumY = 0.0d;
            double effortSumZ = 0.0d;
            double scheduleSumX = 0.0d;
            double scheduleSumY = 0.0d;
            double scheduleSumZ = 0.0d;

            Iterator iter = projects.iterator();
            while (iter.hasNext()) {
                final COINCOMOLocalCalibrationProject project = (COINCOMOLocalCalibrationProject) iter.next();
                if (project.isSelected()) {
                    COINCOMOComponent component = project.getComponent();
                    COINCOMOComponentParameters parameters = component.getParameters();
                    double hoursPerPM = parameters.getWorkHours();
                    double effort = project.getEffort();
                    double normalizedEffort = effort * (hoursPerPM / COINCOMOComponentParameters.WORK_HOURS);
                    //System.out.println("hoursPerPM: " + effort + ", normalizedEffort: " + normalizedEffort);
                    double scedPercent = COINCOMOComponentManager.calculateSCEDPercent(component);
                    double schedule = project.getSchedule();
                    double normalizedSchedule = schedule / (scedPercent / 100.0d);
                    //System.out.println("scedPercent: " + scedPercent + ", normalizedSchedule: " + normalizedSchedule);

                    double EAF = project.getEAF();
                    double SF = component.getSF();
                    double B = parameters.getB();
                    double D = parameters.getD();
                    double KSLOC = component.getSLOC() / 1000.0d;
                    double PMns = COINCOMOComponentManager.calculatePMNS(component);

                    double effortX = Math.log10(normalizedEffort);
                    double effortY = Math.log10(EAF);
                    double effortZ = (B + 0.01d * SF) * Math.log10(KSLOC);
                    double scheduleX = Math.log10(normalizedSchedule);
                    double scheduleY = Math.log10(scedPercent / 100.0d);
                    double scheduleZ = (D + 0.002d * SF) * Math.log10(PMns);

                    effortSumX += effortX;
                    effortSumY += effortY;
                    effortSumZ += effortZ;
                    scheduleSumX += scheduleX;
                    scheduleSumY += scheduleY;
                    scheduleSumZ += scheduleZ;

                    selectedCount++;
                }
            }

            effortCoefficient = Math.pow(10.0d, ((effortSumX - effortSumY - effortSumZ) / (double) selectedCount));
            scheduleCoefficient = Math.pow(10.0d, ((scheduleSumX - scheduleSumY - scheduleSumZ) / (double) selectedCount));

            System.out.println(selectedCount + " project(s) selected.");
            System.out.println("Effort sum of X: " + effortSumX);
            System.out.println("Effort sum of Y: " + effortSumY);
            System.out.println("Effort sum of Z: " + effortSumZ);
            System.out.println("Calibrated effort coefficient = " + effortCoefficient);
            System.out.println("Schedule sum of X: " + scheduleSumX);
            System.out.println("Schedule sum of Y: " + scheduleSumY);
            System.out.println("Schedule sum of Z: " + scheduleSumZ);
            System.out.println("Calibrated schedule coefficient = " + scheduleCoefficient);

            localCalibration.setEffortCoefficient(effortCoefficient);
            localCalibration.setEffortExponent(0.0d);
            localCalibration.setScheduleCoefficient(scheduleCoefficient);
            localCalibration.setScheduleExponent(0.0d);
        }
    }

    public static void calculateCoefficientsAndExponents(COINCOMOLocalCalibration localCalibration) {
        LocalCalibrationMode localCalibrationMode = COINCOMOLocalCalibration.getCalibrationMode();
        double effortCoefficient = 0.0d;
        double effortExponent = 0.0d;
        double scheduleCoefficient = 0.0d;
        double scheduleExponent = 0.0d;

        if (localCalibration != null) {
            int selectedCount = 0;
            ArrayList<COINCOMOUnit> projects = localCalibration.getListOfSubUnits();
            double effortSumA1 = 0.0d;
            double effortSumA2 = 0.0d;
            double effortSumD0 = 0.0d;
            double effortSumD1 = 0.0d;
            double scheduleSumA1 = 0.0d;
            double scheduleSumA2 = 0.0d;
            double scheduleSumD0 = 0.0d;
            double scheduleSumD1 = 0.0d;

            Iterator iter = projects.iterator();
            while (iter.hasNext()) {
                final COINCOMOLocalCalibrationProject project = (COINCOMOLocalCalibrationProject) iter.next();
                if (project.isSelected()) {
                    COINCOMOComponent component = project.getComponent();
                    COINCOMOComponentParameters parameters = component.getParameters();
                    double hoursPerPM = parameters.getWorkHours();
                    double effort = project.getEffort();
                    double normalizedEffort = effort * (hoursPerPM / COINCOMOComponentParameters.WORK_HOURS);
                    //System.out.println("hoursPerPM: " + effort + ", normalizedEffort: " + normalizedEffort);
                    double scedPercent = COINCOMOComponentManager.calculateSCEDPercent(component);
                    double schedule = project.getSchedule();
                    double normalizedSchedule = schedule / (scedPercent / 100.0d);
                    //System.out.println("scedPercent: " + scedPercent + ", normalizedSchedule: " + normalizedSchedule);

                    double EAF = project.getEAF();
                    double SF = component.getSF();
                    double KSLOC = component.getSLOC() / 1000.0d;
                    double PMns = COINCOMOComponentManager.calculatePMNS(component);

                    double effortA1 = Math.log10(KSLOC);
                    double effortA2 = effortA1 * effortA1;
                    double effortD0 = Math.log10(normalizedEffort / EAF) - (0.01d * SF) * Math.log10(KSLOC);
                    double effortD1 = effortD0 * Math.log10(KSLOC);
                    double scheduleA1 = Math.log10(PMns);
                    double scheduleA2 = scheduleA1 * scheduleA1;
                    double scheduleD0 = Math.log10(normalizedSchedule / (scedPercent / 100.0d)) - (0.002d * SF) * Math.log10(PMns);
                    double scheduleD1 = scheduleD0 * Math.log10(PMns);

                    effortSumA1 += effortA1;
                    effortSumA2 += effortA2;
                    effortSumD0 += effortD0;
                    effortSumD1 += effortD1;
                    scheduleSumA1 += scheduleA1;
                    scheduleSumA2 += scheduleA2;
                    scheduleSumD0 += scheduleD0;
                    scheduleSumD1 += scheduleD1;

                    selectedCount++;
                }
            }

            effortCoefficient = Math.pow(10.0d, ((effortSumA2 * effortSumD0 - effortSumA1 * effortSumD1) / ((double) selectedCount * effortSumA2 - effortSumA1 * effortSumA1)));
            effortExponent = ((double) selectedCount * effortSumD1 - effortSumA1 * effortSumD0) / ((double) selectedCount * effortSumA2 - effortSumA1 * effortSumA1);
            scheduleCoefficient = Math.pow(10.0d, ((scheduleSumA2 * scheduleSumD0 - scheduleSumA1 * scheduleSumD1) / ((double) selectedCount * scheduleSumA2 - scheduleSumA1 * scheduleSumA1)));
            scheduleExponent = ((double) selectedCount * scheduleSumD1 - scheduleSumA1 * scheduleSumD0) / ((double) selectedCount * scheduleSumA2 - scheduleSumA1 * scheduleSumA1);

            System.out.println(selectedCount + " project(s) selected.");
            System.out.println("Effort sum of A1: " + effortSumA1);
            System.out.println("Effort sum of A2: " + effortSumA2);
            System.out.println("Effort sum of D0: " + effortSumD0);
            System.out.println("Effort sum of D1: " + effortSumD1);
            System.out.println("Schedule sum of A1: " + scheduleSumA1);
            System.out.println("Schedule sum of A2: " + scheduleSumA2);
            System.out.println("Schedule sum of D0: " + scheduleSumD0);
            System.out.println("Schedule sum of D1: " + scheduleSumD1);

            System.out.println("Calibrated effort coefficient = " + effortCoefficient);
            System.out.println("Calibrated effort exponent = " + effortExponent);
            System.out.println("Calibrated schedule coefficient = " + scheduleCoefficient);
            System.out.println("Calibrated schedule exponent = " + scheduleExponent);

            localCalibration.setEffortCoefficient(effortCoefficient);
            localCalibration.setEffortExponent(effortExponent);
            localCalibration.setScheduleCoefficient(scheduleCoefficient);
            localCalibration.setScheduleExponent(scheduleExponent);
        }
    }

    private static void log(Level level, String message) {
        Logger.getLogger(COINCOMOLocalCalibrationManager.class.getName()).log(level, message);
    }
}
