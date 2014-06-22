/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package database;

import core.COINCOMOAdaptationAndReuse;
import core.COINCOMOComponent;
import core.COINCOMOComponentParameters;
import core.COINCOMOConstants;
import core.COINCOMOConstants.CalculationMethod;
import core.COINCOMOConstants.EAF;
import core.COINCOMOConstants.RISK;
import core.COINCOMOConstants.FP;
import core.COINCOMOConstants.Increment;
import core.COINCOMOConstants.OperationMode;
import core.COINCOMOConstants.Rating;
import core.COINCOMOConstants.SF;
import core.COINCOMOConstants.Scenario;
import core.COINCOMOSubComponent;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.COINCOMO;

/**
 *
 * @author Raed Shomali
 */
public class COINCOMOSubComponentManager extends COINCOMOManager {

    public static double calculateEAFWithoutSCED(COINCOMOSubComponent subComponent) {
        final COINCOMOComponent component = (COINCOMOComponent) subComponent.getParent();
        final COINCOMOComponentParameters parameters = component.getParameters();
        final double[][] eafWeights = parameters.getEAFWeights();
        final Rating[] eafRatings = subComponent.getEAFRatings();
        final Increment[] eafIncrements = subComponent.getEAFIncrements();

        double result = 1.0d;


        for (int i = 0; i < COINCOMOConstants.EAFS.length - 1; i++) {
            double difference = 0.0d;
            double fraction = 0.0d;
            double rating = 0.0d;

            /*
             * From USC COCOMO II User's manual
             * Final rating of an EAF cost driver = (next cost driver rating - current cost driver rating) * current inter cost driver / 100
             * 
             * To calculate the EAF value from the EAF cost drivers and EAF cost driver weights,
             * one will multiply all the final ratings of the EAF cost drivers.
             * 
             * To calculate the final rating for each EAF cost driver, we break down the calculation
             * into difference, fraction, and rating (as final rating).
             * 
             * difference = EAF cost driver weight @ next rating - EAF cost driver weight @ current rating.
             * For example, if the EAF cost driver is RELY, and current rating is NOM,
             * then difference = EAF cost driver weight for RELY @ HI - EAF cost driver weight for RELY @ NOM
             * 
             * fraction = difference * EAF cost driver @ current increment (0%, 25%, 50%, and 75%)
             * For example, if the EAF cost driver is RELY, and current increment is 25%,
             * then fraction = EAF cost driver difference for RELY * 0.25
             * 
             * rating = EAF cost driver weight @ current rating + difference * fraction.
             * For example, if the EAF cost driver is RELY, and current rating is NOM,
             * then rating = EAF cost driver weight for RELY @ NOM + difference * fraction.
             * 
             * ****IMPORTANT POINTS****
             * Note that EAF cost driver weight @ next rating is nothing if current rating is 'XHI',
             * then the final rating is just EAF cost driver weight @ current rating.
             * 
             * In addition, for the EAF cost driver weights @ invalid ratings,
             * then the final rating should be 0 for debugging purpose,
             * since the program should not allow the users to select such invalid ratings for the EAF cost drivers.
             * 
             * For example, EAF cost driver for RELY @ XHI is invalid (RELY only goes from VLO to VHI),
             * thus if somehow the program accepts input to set rating cost driver for RELY @ XHI,
             * then the EAF value should come out to be 0,
             * and the programmer can go and fix the issue.
             * 
             * To avoid calculating the wrong difference & fraction values, a condition is added to see if the
             * cost driver for the next rating is 0 or not, if it is, then we simply set the difference and fraction
             * to 0.
             * ************************
             */
            if (eafRatings[i] == Rating.XHI) {
                difference = 0.0d;
                fraction = 0.0d;
            } else {
                if (eafWeights[i][eafRatings[i].ordinal() + 1] == 0.0d) {
                    difference = 0.0d;
                    fraction = 0.0d;
                } else {
                    difference = eafWeights[i][eafRatings[i].ordinal() + 1] - eafWeights[i][eafRatings[i].ordinal()];
                    fraction = eafIncrements[i].getDoubleValue();
                }
            }

            rating = eafWeights[i][eafRatings[i].ordinal()] + difference * fraction;
            result *= rating;
        }

        return result;
    }

    public static double calculateEAFWithSCED(COINCOMOSubComponent subComponent) {
        final COINCOMOComponent component = (COINCOMOComponent) subComponent.getParent();
        final double eaf = calculateEAFWithoutSCED(subComponent);
        final double sced = component.getSCED();
        final double eafWithSCED = eaf * sced;

        return eafWithSCED;
    }

    public static int[] calculateSubTotals(COINCOMOSubComponent subComponent) {
        final COINCOMOComponent component = (COINCOMOComponent) subComponent.getParent();
        final COINCOMOComponentParameters parameters = component.getParameters();
        final int ILFs[] = subComponent.getInternalLogicalFiles();
        final int EIFs[] = subComponent.getExternalInterfaceFiles();
        final int EIs[] = subComponent.getExternalInputs();
        final int EOs[] = subComponent.getExternalOutputs();
        final int EQs[] = subComponent.getExternalInquiries();
        final int fpWeights[][] = parameters.getFPWeights();

        int subTotals[] = new int[5];
        subTotals[FP.ILF.ordinal()] = ILFs[0] * fpWeights[FP.ILF.ordinal()][0] + ILFs[1] * fpWeights[FP.ILF.ordinal()][1] + ILFs[2] * fpWeights[FP.ILF.ordinal()][2];
        subTotals[FP.EIF.ordinal()] = EIFs[0] * fpWeights[FP.EIF.ordinal()][0] + EIFs[1] * fpWeights[FP.EIF.ordinal()][1] + EIFs[2] * fpWeights[FP.EIF.ordinal()][2];
        subTotals[FP.EI.ordinal()] = EIs[0] * fpWeights[FP.EI.ordinal()][0] + EIs[1] * fpWeights[FP.EI.ordinal()][1] + EIs[2] * fpWeights[FP.EI.ordinal()][2];
        subTotals[FP.EO.ordinal()] = EOs[0] * fpWeights[FP.EO.ordinal()][0] + EOs[1] * fpWeights[FP.EO.ordinal()][1] + EOs[2] * fpWeights[FP.EO.ordinal()][2];
        subTotals[FP.EQ.ordinal()] = EQs[0] * fpWeights[FP.EQ.ordinal()][0] + EQs[1] * fpWeights[FP.EQ.ordinal()][1] + EQs[2] * fpWeights[FP.EQ.ordinal()][2];

        return subTotals;
    }

    public static int calculateTotalUnadjustedFunctionPoints(COINCOMOSubComponent subComponent) {
        final int subTotals[] = subComponent.getSubTotals();

        int totalUnadjustedFunctionPoints = 0;

        for (int i = 0; i < subTotals.length; i++) {
            totalUnadjustedFunctionPoints += subTotals[i];
        }

        return totalUnadjustedFunctionPoints;
    }

    public static long calculateEquivalentSLOC(COINCOMOSubComponent subComponent) {
        final long multiplier = (long) subComponent.getMultiplier();
        final long totalUnadjustedFunctionPoints = (long) subComponent.getTotalUnadjustedFunctionPoints();
        final long equivalentSLOC = multiplier * totalUnadjustedFunctionPoints;

        return equivalentSLOC;
    }

    public static long calculateSumOfSLOCs(COINCOMOSubComponent subComponent) {
        final long newSLOC = subComponent.getNewSLOC();
        final long fpSLOC = subComponent.getEquivalentSLOC();

        long aarSLOC = 0;
        long sumOfSLOCs = 0;

        Iterator iter = subComponent.getListOfSubUnits().iterator();
        while (iter.hasNext()) {
            COINCOMOAdaptationAndReuse tempAdaptation = (COINCOMOAdaptationAndReuse) iter.next();
            aarSLOC += tempAdaptation.getEquivalentSLOC();
        }

        sumOfSLOCs = newSLOC + fpSLOC + aarSLOC;

        return sumOfSLOCs;
    }

    public static long calculateSLOC(COINCOMOSubComponent subComponent) {
        final double sumOfSLOCs = (double) calculateSumOfSLOCs(subComponent);
        final double revl = subComponent.getREVL();
        final double sloc = (1.0d + revl / 100.0d) * sumOfSLOCs;

        return Math.round(sloc);
    }

    public static double calculatePMBase(COINCOMOSubComponent subComponent) {
        /*
         * PM(base) for each sub-component is calculated as:
         * 
         * PM(base) = sub-component % of PM(base) of component
         * 
         * PM(base) of component = A * Size ^ E, where Size is the total sloc with REVL on the component level.
         */
        final long slocSubComponent = subComponent.getSLOC();
        final COINCOMOComponent component = (COINCOMOComponent) subComponent.getParent();
        final COINCOMOComponentParameters parameters = component.getParameters();
        final double parameterA = parameters.getA();
        final double exponentE = COINCOMOComponentManager.calculateExponentE(component);
        final double hoursPerPM = parameters.getWorkHours();
        final double hoursPerPMRatio = COINCOMOComponentParameters.WORK_HOURS / hoursPerPM;

        double pmBaseComponent = 0.0d;
        double pmBaseSubComponent = 0.0d;
        long slocComponent = 0;

        Iterator iter = component.getListOfSubUnits().iterator();
        while (iter.hasNext()) {
            final COINCOMOSubComponent tempSubComponent = (COINCOMOSubComponent) iter.next();
            slocComponent += tempSubComponent.getSLOC();
        }

        double kSlocComponent = ((double) slocComponent) / 1000.0d;
        double kSlocSubComponent = ((double) slocSubComponent) / 1000.0d;
        pmBaseComponent = parameterA * Math.pow(kSlocComponent, exponentE) * hoursPerPMRatio;
        pmBaseSubComponent = pmBaseComponent * (kSlocSubComponent / kSlocComponent);

        if (Double.isNaN(pmBaseSubComponent) || Double.isInfinite(pmBaseSubComponent)) {
            return 0.0d;
        } else {
            return pmBaseSubComponent;
        }
    }

    public static double calculatePMAuto(COINCOMOSubComponent subComponent) {
        /*
         * PM(auto) for each adaptation is calculated as:
         *
         * PM(auto) = Adapted SLOC (a.k.a Initial SLOC) x ( AT / 100 ) / ATPROD
         * 
         * PM(auto) for a sub-component is the sum of all the PM(auto)
         */
        double pmAuto = 0.0d;

        Iterator iter = subComponent.getListOfSubUnits().iterator();
        while (iter.hasNext()) {
            final COINCOMOAdaptationAndReuse tempAdaptation = (COINCOMOAdaptationAndReuse) iter.next();
            final double adaptedSLOC = tempAdaptation.getAdaptedSLOC();
            final double AT = tempAdaptation.getAutomaticallyTranslated();
            final double ATPROD = tempAdaptation.getAutomaticTranslationProductivity();

            if (ATPROD != 0.0d && !Double.isNaN(ATPROD)) {
                pmAuto += (adaptedSLOC * AT / 100.0d) / ATPROD;
            }
        }

        if (Double.isNaN(pmAuto) || Double.isInfinite(pmAuto)) {
            return 0.0d;
        } else {
            return pmAuto;
        }
    }

    public static double calculatePMNominal(COINCOMOSubComponent subComponent) {
        /*
         * PM(nominal) for each sub-component is calculated as:
         * 
         * PM(nominal) = PM(base) of sub-component + PM(auto)
         */
        final double pmBase = calculatePMBase(subComponent);
        final double pmAuto = calculatePMAuto(subComponent);
        double pmNominal = pmBase + pmAuto;

        if (Double.isNaN(pmNominal) || Double.isInfinite(pmNominal)) {
            return 0.0d;
        } else {
            return pmNominal;
        }
    }

    public static double calculatePMEstimated(COINCOMOSubComponent subComponent) {
        /*
         * PM(estimated) for each sub-component is calculated as:
         * 
         * PM(estimated) = PM(base) of sub-component * EAF + PM(auto)
         */
        final double pmBase = calculatePMBase(subComponent);
        final double pmAuto = calculatePMAuto(subComponent);
        final double eafWithSCED = calculateEAFWithSCED(subComponent);
        final double pmEstimated = pmBase * eafWithSCED + pmAuto;

        if (Double.isNaN(pmEstimated) || Double.isInfinite(pmEstimated)) {
            return 0.0d;
        } else {
            return pmEstimated;
        }
    }

    public static double calculateTDEVEstimated(COINCOMOSubComponent subComponent) {
        final long sloc = subComponent.getSLOC();
        final double eaf = calculateEAFWithoutSCED(subComponent);
        final COINCOMOComponent component = (COINCOMOComponent) subComponent.getParent();
        final COINCOMOComponentParameters parameters = component.getParameters();
        final double parameterA = parameters.getA();
        final double parameterC = parameters.getC();
        final double exponentE = COINCOMOComponentManager.calculateExponentE(component);
        final double exponentF = COINCOMOComponentManager.calculateExponentF(component);
        final double scedPercent = COINCOMOComponentManager.calculateSCEDPercent(component);
        final double hoursPerPM = parameters.getWorkHours();
        final double hoursPerPMRatio = COINCOMOComponentParameters.WORK_HOURS / hoursPerPM;
        long slocComponent = 0;

        Iterator iter = component.getListOfSubUnits().iterator();
        while (iter.hasNext()) {
            final COINCOMOSubComponent tempSubComponent = (COINCOMOSubComponent) iter.next();

            slocComponent += tempSubComponent.getSLOC();
        }

        final double kSlocComponent = ((double) slocComponent) / 1000.0d;
        final double kSlocSubComponent = ((double) sloc) / 1000.0d;
        double pmBaseComponent = 0.0d;
        double pmBaseSubComponent = 0.0d;
        pmBaseComponent = parameterA * Math.pow(kSlocComponent, exponentE) * hoursPerPMRatio;
        pmBaseSubComponent = pmBaseComponent * (kSlocSubComponent / kSlocComponent);

        double pmNSComponent = 0.0d;
        double pmNSSubComponent = pmBaseSubComponent * eaf * hoursPerPMRatio;
        double tdevComponent = 0.0d;
        double tdevSubComponent = 0.0d;

        iter = component.getListOfSubUnits().iterator();
        while (iter.hasNext()) {
            final COINCOMOSubComponent tempSubComponent = (COINCOMOSubComponent) iter.next();
            final long tempSlocSubComponent = tempSubComponent.getSLOC();
            final double tempKSlocSubComponent = ((double) tempSlocSubComponent) / 1000.0d;
            final double tempEAFSubComponent = calculateEAFWithoutSCED(tempSubComponent);
            final double tempPMBaseSubComponent = pmBaseComponent * (tempKSlocSubComponent / kSlocComponent);

            pmNSComponent += tempPMBaseSubComponent * tempEAFSubComponent;
        }

        double tdevBaseComponent = 0.0d;
        double tdevBaseSubComponent = 0.0d;
        tdevComponent = parameterC * Math.pow(pmNSComponent, exponentF) * (scedPercent / 100.0d);
        tdevSubComponent = tdevComponent * (pmNSSubComponent / pmNSComponent);

        if (Double.isNaN(tdevSubComponent) || Double.isInfinite(tdevSubComponent)) {
            return 0.0d;
        } else {
            return tdevSubComponent;
        }
    }

    public static double calculateProductivity(COINCOMOSubComponent subComponent) {
        final double sloc = (double) subComponent.getSLOC();
        final double pmEstimated = calculatePMEstimated(subComponent);
        final double productivity = sloc / pmEstimated;

        if (Double.isNaN(productivity) || Double.isInfinite(productivity)) {
            return 0.0d;
        } else {
            return productivity;
        }
    }

    public static double calculateCost(COINCOMOSubComponent subComponent) {
        final double pmEstimated = calculatePMEstimated(subComponent);
        final double laborRate = subComponent.getLaborRate();
        final double cost = pmEstimated * laborRate;

        if (Double.isNaN(cost) || Double.isInfinite(cost)) {
            return 0.0d;
        } else {
            return cost;
        }
    }

    public static double calculateInstructionCost(COINCOMOSubComponent subComponent) {
        final double cost = calculateCost(subComponent);
        final double sloc = (double) subComponent.getSLOC();
        final double instructionCost = cost / sloc;

        if (Double.isNaN(instructionCost) || Double.isInfinite(instructionCost)) {
            return 0.0d;
        } else {
            return instructionCost;
        }
    }

    public static double calculateStaff(COINCOMOSubComponent subComponent) {
        final COINCOMOComponent component = (COINCOMOComponent) subComponent.getParent();
        final double componentSchedule = COINCOMOComponentManager.calculateSchedule(component, Scenario.MostLikely);
        final double pmEstimated = calculatePMEstimated(subComponent);
        final double staff = pmEstimated / componentSchedule;

        if (Double.isNaN(staff) || Double.isInfinite(staff)) {
            return 0.0d;
        } else {
            return staff;
        }
    }

    public static double calculateEffort(COINCOMOSubComponent subComponent) {
        return calculatePMEstimated(subComponent);
    }

    public static double calculateSchedule(COINCOMOSubComponent subComponent) {
        return calculateTDEVEstimated(subComponent);
    }

    //Changed by Roopa Dharap----------------------
    //------------------ risk module changes
    public static double calculateRisk(COINCOMOSubComponent subComponent, RISK risktype) {
        //   NumberFormat nf = NumberFormat.getInstance();
        //   nf.setMaximumFractionDigits(2);

        double riskReturn = 0.0d;
        double totalRisk = 0.0d;
        double normalizedTotalRisk = 0.0d;
        double schedule = 0.0d;
        double product = 0.0d;
        double personnel = 0.0d;
        double platform = 0.0d;
        double process = 0.0d;
        double reuse = 0.0d;

        if (risktype == RISK.SCHEDELE) {


            double scheduleCoefficient = 100.0d / 45.8d;
            double sceduleValue = 0.0d;

            schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.RELY, subComponent);
            schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TIME, subComponent);
            schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PVOL, subComponent);
            schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TOOL, subComponent);
            schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.ACAP, subComponent);
            schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.APEX, subComponent);
            schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PCAP, subComponent);
            schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PLEX, subComponent);
            schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.LTEX, subComponent);
            schedule += calculateEffortMultiplierProductEAFSF(EAF.SCED, SF.PMAT, subComponent);

            riskReturn = schedule * scheduleCoefficient;
            sceduleValue = schedule * scheduleCoefficient;
        }
        if (risktype == RISK.PRODUCT) {
            // Product

            double productCoefficient = 100.0d / 82.5d;
            double productValue = 0.0d;
            product += calculateEffortMultiplierProductEAFEAF(EAF.RELY, EAF.ACAP, subComponent);
            product += calculateEffortMultiplierProductEAFEAF(EAF.RELY, EAF.PCAP, subComponent);
            product += calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.ACAP, subComponent);
            product += calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.PCAP, subComponent);
            product += calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.TOOL, subComponent);
            product += calculateEffortMultiplierProductEAFSF(EAF.RELY, SF.PMAT, subComponent);
            product += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.CPLX, subComponent);
            product += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.RELY, subComponent);
            product += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TIME, subComponent);
            product += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.APEX, subComponent);
            product += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.LTEX, subComponent);


            productValue = product * productCoefficient;
            riskReturn = product * productCoefficient;
        }
        // Personnel
        if (risktype == RISK.PERSONNEL) {

            double personnelValue = 0.0d;
            double personnelCoefficient = 100.0d / 138.7d;
            personnel += calculateEffortMultiplierProductSFEAF(SF.PMAT, EAF.ACAP, subComponent);
            personnel += calculateEffortMultiplierProductEAFEAF(EAF.STOR, EAF.ACAP, subComponent);
            personnel += calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.ACAP, subComponent);
            personnel += calculateEffortMultiplierProductEAFEAF(EAF.TOOL, EAF.ACAP, subComponent);
            personnel += calculateEffortMultiplierProductEAFEAF(EAF.TOOL, EAF.PCAP, subComponent);
            personnel += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.APEX, subComponent);
            personnel += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.LTEX, subComponent);
            personnel += calculateEffortMultiplierProductSFEAF(SF.PMAT, EAF.PCAP, subComponent);
            personnel += (calculateEffortMultiplierProductEAFEAF(EAF.STOR, EAF.PCAP, subComponent));
            personnel += calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.PCAP, subComponent);
            personnel += calculateEffortMultiplierProductEAFEAF(EAF.LTEX, EAF.PCAP, subComponent);
            personnel += calculateEffortMultiplierProductEAFEAF(EAF.PVOL, EAF.LTEX, subComponent);
            personnel += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.ACAP, subComponent);
            personnel += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.APEX, subComponent);
            personnel += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PCAP, subComponent);
            personnel += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PLEX, subComponent);
            personnel += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.LTEX, subComponent);
            personnel += calculateEffortMultiplierProductEAFEAF(EAF.RELY, EAF.ACAP, subComponent);
            personnel += calculateEffortMultiplierProductEAFEAF(EAF.RELY, EAF.PCAP, subComponent);
            personnel += calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.ACAP, subComponent);
            personnel += calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.PCAP, subComponent);
            personnel += calculateEffortMultiplierProductSFEAF(SF.TEAM, EAF.APEX, subComponent);

            personnelValue = personnel * personnelCoefficient;
            riskReturn = personnel * personnelCoefficient;
        }
        // Process
        if (risktype == RISK.PROCESS) {

            double processValue = 0.0d;
            double processCofficient = 100.0d / 44.1d;
            process += calculateEffortMultiplierProductEAFSF(EAF.TOOL, SF.PMAT, subComponent);
            process += calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.TOOL, subComponent);
            process += calculateEffortMultiplierProductEAFSF(EAF.TOOL, SF.PMAT, subComponent);
            process += calculateEffortMultiplierProductSFEAF(SF.TEAM, EAF.APEX, subComponent);
            process += calculateEffortMultiplierProductSFEAF(SF.TEAM, EAF.SCED, subComponent);
            process += calculateEffortMultiplierProductSFEAF(SF.TEAM, EAF.SITE, subComponent);
            process += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TOOL, subComponent);
            process += calculateEffortMultiplierProductEAFSF(EAF.SCED, SF.PMAT, subComponent);
            process += calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.TOOL, subComponent);
            process += calculateEffortMultiplierProductSFEAF(SF.PMAT, EAF.ACAP, subComponent);
            process += calculateEffortMultiplierProductEAFEAF(EAF.TOOL, EAF.ACAP, subComponent);
            process += calculateEffortMultiplierProductEAFEAF(EAF.TOOL, EAF.PCAP, subComponent);
            process += calculateEffortMultiplierProductSFEAF(SF.PMAT, EAF.PCAP, subComponent);

            processValue = process * processCofficient;
            riskReturn = process * processCofficient;
        }
        // Platform
        if (risktype == RISK.PLATFORM) {

            double platformValue = 0.0d;
            double platformCoefficient = 100.0d / 46.5d;
            platform += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TIME, subComponent);
            platform += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PVOL, subComponent);
            platform += calculateEffortMultiplierProductEAFEAF(EAF.STOR, EAF.ACAP, subComponent);
            platform += calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.ACAP, subComponent);
            platform += calculateEffortMultiplierProductEAFEAF(EAF.STOR, EAF.PCAP, subComponent);
            platform += calculateEffortMultiplierProductEAFEAF(EAF.PVOL, EAF.PLEX, subComponent);
            platform += calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.TOOL, subComponent);

            platformValue = platform * platformCoefficient;
            riskReturn = platform * platformCoefficient;
        } // Reuse
        else if (risktype == RISK.REUSE) {

            double reuseValue = 0.0d;
            double reuseCoefficient = 100.0d / 100.0d;
            reuse += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.APEX, subComponent);
            reuse += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.LTEX, subComponent);

            reuseValue = reuse * reuseCoefficient;
            riskReturn = reuse * reuseCoefficient;
        } 
        else if (risktype == RISK.TOTAL) {

            double riskCoefficient = 100.0d / 373.0d;
            totalRisk = schedule + product + personnel + process + platform + reuse;
            //totalRisk = sceduleValue + productValue + personnelValue + processValue + platformValue + reuseValue;
            normalizedTotalRisk = totalRisk * riskCoefficient;
            riskReturn = totalRisk * riskCoefficient;
        }

        return riskReturn;
    }

    public static double calculateRiskTotal(COINCOMOSubComponent subComponent) {
        double riskReturn = 0.0d;
        
        double normalizedTotalRisk = 0.0d;
        double schedule = 0.0d;
        double product = 0.0d;
        double personnel = 0.0d;
        double platform = 0.0d;
        double process = 0.0d;
        double reuse = 0.0d;
        double totalRisk = 0.0d;
        schedule=calculateRisk(subComponent, RISK.SCHEDELE);
        product=calculateRisk(subComponent, RISK.PRODUCT);
        personnel=calculateRisk(subComponent, RISK.PERSONNEL);
        process=calculateRisk(subComponent, RISK.PROCESS);
        platform=calculateRisk(subComponent, RISK.PLATFORM);
        reuse=calculateRisk(subComponent, RISK.REUSE);
        double riskCoefficient = 100.0d / 373.0d;
            totalRisk = schedule + product + personnel + process + platform + reuse;
            //totalRisk = sceduleValue + productValue + personnelValue + processValue + platformValue + reuseValue;
            normalizedTotalRisk = totalRisk * riskCoefficient;
            riskReturn = totalRisk * riskCoefficient;
        
        return riskReturn;

    }
    private static HashMap<String, byte[][]> riskLevelMaps = new HashMap<String, byte[][]>(40, 0.9f);

    private static double calculateEffortMultiplierProductEAFEAF(EAF eaf1, EAF eaf2, COINCOMOSubComponent subComponent) {
        double riskLevelMultiplier = 1.0d;
        double eaf1Multiplier = 1.0d;
        Rating eaf1Rating = Rating.NOM;
        double eaf2Multiplier = 1.0d;
        Rating eaf2Rating = Rating.NOM;
        double effortMultiplierProduct = 1.0d;

        final COINCOMOComponent component = (COINCOMOComponent) subComponent.getParent();
        final COINCOMOComponentParameters parameters = component.getParameters();

        double[][] eafWeights = parameters.getEAFWeights();
        Rating[] eafRatings = subComponent.getEAFRatings();
        Rating scedRating = component.getSCEDRating();

        double[][] sfWeights = parameters.getSFWeights();
        Rating[] sfRatings = component.getSFRatings();

        if (eaf1 == EAF.SCED) {
            eaf1Multiplier = calculateSCEDMultiplier(subComponent);
            eaf1Rating = scedRating;
        } else {
            //Code changed by Roopa Dharap -------------------------
            //changed per cocomo expert code - error implemented deliberately
            if (eaf2 == EAF.SCED && eaf1 == EAF.CPLX) {
                eaf1Multiplier = calculateEAFMultiplier(EAF.RELY, subComponent);
            } else if (eaf2 == EAF.SCED && eaf1 == EAF.ACAP) {
                eaf1Multiplier = calculateEAFMultiplier(EAF.PVOL, subComponent);
            } else {
                eaf1Multiplier = calculateEAFMultiplier(eaf1, subComponent);
            }
            //----------------------------------------------------
            eaf1Rating = eafRatings[eaf1.ordinal()];
        }

        if (eaf2 == EAF.SCED) {
            eaf2Multiplier = calculateSCEDMultiplier(subComponent);
            eaf2Rating = scedRating;
        } else {
            //Code changed by Roopa Dharap -------------------------
            //changed per cocomo expert code - error implemented deliberately
            if (eaf1 == EAF.SCED && eaf2 == EAF.CPLX) {
                eaf2Multiplier = calculateEAFMultiplier(EAF.RELY, subComponent);
            } else if (eaf1 == EAF.SCED && eaf2 == EAF.ACAP) {
                eaf2Multiplier = calculateEAFMultiplier(EAF.PVOL, subComponent);
            } else {
                eaf2Multiplier = calculateEAFMultiplier(eaf2, subComponent);
            }
            //------------------------------------
            eaf2Rating = eafRatings[eaf2.ordinal()];
        }



        riskLevelMultiplier = calculateRiskLevelMultiplierEAFEAF(eaf1, eaf1Rating, eaf2, eaf2Rating);

        effortMultiplierProduct = riskLevelMultiplier * eaf1Multiplier * eaf2Multiplier;

        return effortMultiplierProduct;
    }

    private static double calculateEffortMultiplierProductEAFSF(EAF eaf, SF sf, COINCOMOSubComponent subComponent) {
        double riskLevelMultiplier = 1.0d;
        double eafMultiplier = 1.0d;
        Rating eafRating = Rating.NOM;
        double sfMultiplier = 1.0d;
        Rating sfRating = Rating.NOM;
        double effortMultiplierProduct = 1.0d;

        final COINCOMOComponent component = (COINCOMOComponent) subComponent.getParent();
        final COINCOMOComponentParameters parameters = component.getParameters();

        double[][] eafWeights = parameters.getEAFWeights();
        Rating[] eafRatings = subComponent.getEAFRatings();
        Rating scedRating = component.getSCEDRating();

        double[][] sfWeights = parameters.getSFWeights();
        Rating[] sfRatings = component.getSFRatings();

        if (eaf == EAF.SCED) {
            eafMultiplier = calculateSCEDMultiplier(subComponent);
            eafRating = scedRating;
        } else {
            eafMultiplier = calculateEAFMultiplier(eaf, subComponent);
            eafRating = eafRatings[eaf.ordinal()];
        }

        sfMultiplier = calculateSFMultiplier(sf, subComponent);
        sfRating = sfRatings[sf.ordinal()];

        riskLevelMultiplier = calculateRiskLevelMultiplierEAFSF(eaf, eafRating, sf, sfRating);

        effortMultiplierProduct = riskLevelMultiplier * eafMultiplier * sfMultiplier;

        return effortMultiplierProduct;
    }

    private static double calculateEffortMultiplierProductSFEAF(SF sf, EAF eaf, COINCOMOSubComponent subComponent) {
        double riskLevelMultiplier = 1.0d;
        double sfMultiplier = 1.0d;
        Rating sfRating = Rating.NOM;
        double eafMultiplier = 1.0d;
        Rating eafRating = Rating.NOM;

        double effortMultiplierProduct = 1.0d;

        initializeRiskLevelMaps();

        final COINCOMOComponent component = (COINCOMOComponent) subComponent.getParent();
        final COINCOMOComponentParameters parameters = component.getParameters();

        double[][] eafWeights = parameters.getEAFWeights();
        Rating[] eafRatings = subComponent.getEAFRatings();
        Rating scedRating = component.getSCEDRating();

        double[][] sfWeights = parameters.getSFWeights();
        Rating[] sfRatings = component.getSFRatings();

        sfMultiplier = calculateSFMultiplier(sf, subComponent);
        sfRating = sfRatings[sf.ordinal()];

        if (eaf == EAF.SCED) {
            eafMultiplier = calculateSCEDMultiplier(subComponent);
            eafRating = scedRating;
        } else {
            eafMultiplier = calculateEAFMultiplier(eaf, subComponent);
            eafRating = eafRatings[eaf.ordinal()];
        }

        riskLevelMultiplier = calculateRiskLevelMultiplierSFEAF(sf, sfRating, eaf, eafRating);

        effortMultiplierProduct = riskLevelMultiplier * sfMultiplier * eafMultiplier;

        return effortMultiplierProduct;
    }

    private static double calculateRiskLevelMultiplierEAFEAF(EAF eaf1, Rating eafRating1, EAF eaf2, Rating eafRating2) {
        if (riskLevelMaps.containsKey(eaf1.toString() + eaf2.toString())) {
            byte[][] riskLevelMap = riskLevelMaps.get(eaf1.toString() + eaf2.toString());

            return riskLevelMap[eafRating1.ordinal()][eafRating2.ordinal()];
        } else {
            return 0.0d;
        }
    }

    private static double calculateRiskLevelMultiplierEAFSF(EAF eaf, Rating eafRating, SF sf, Rating sfRating) {
        if (riskLevelMaps.containsKey(eaf.toString() + sf.toString())) {
            byte[][] riskLevelMap = riskLevelMaps.get(eaf.toString() + sf.toString());

            return riskLevelMap[eafRating.ordinal()][sfRating.ordinal()];
        } else {
            return 0.0d;
        }
    }

    private static double calculateRiskLevelMultiplierSFEAF(SF sf, Rating sfRating, EAF eaf, Rating eafRating) {
        if (riskLevelMaps.containsKey(sf.toString() + eaf.toString())) {
            byte[][] riskLevelMap = riskLevelMaps.get(sf.toString() + eaf.toString());

            return riskLevelMap[sfRating.ordinal()][eafRating.ordinal()];
        } else {
            return 0.0d;
        }
    }

    //private 
    public static double calculateSCEDMultiplier(COINCOMOSubComponent subComponent) {
        final COINCOMOComponent component = (COINCOMOComponent) subComponent.getParent();
        final COINCOMOComponentParameters parameters = component.getParameters();
        double[][] eafWeights = parameters.getEAFWeights();
        return eafWeights[EAF.SCED.ordinal()][component.getSCEDRating().ordinal()];
    }

    public static double calculateSFMultiplier(SF sf, COINCOMOSubComponent subComponent) {
        //private static double calculateSFMultiplier(SF sf,COINCOMOSubComponent subComponent) {
        final COINCOMOComponent component = (COINCOMOComponent) subComponent.getParent();
        final COINCOMOComponentParameters parameters = component.getParameters();
        double[][] sfWeights = parameters.getSFWeights();
        Rating[] sfRatings = component.getSFRatings();
        /*
         if (subComponent.getSLOC() == 0) {
         return 0.0d;
         } else {
         return Math.pow(subComponent.getSLOC(), (0.01d * sfWeights[sf.ordinal()][sfRatings[sf.ordinal()].ordinal()]))
         / Math.pow(subComponent.getSLOC(), (0.01d * 3.0d));
         }
         */
        switch (sfRatings[sf.ordinal()]) {
            case VLO:
                return Math.pow(subComponent.getSLOC() / 1000.0d, 0.02d);
            case LO:
                return Math.pow(subComponent.getSLOC() / 1000.0d, 0.01d);
            default:
                //Code changed by Roopa Dharap -------------------------
                //If SF is PMAT, it should return 1
                if (sf == SF.PMAT) {
                    return 1.0d;
                } else {
                    return 0.0d;
                }
            //----------------------------------------------
        }
    }

    //private static double calculateEAFMultiplier(EAF eaf,COINCOMOSubComponent subComponent) {
    public static double calculateEAFMultiplier(EAF eaf, COINCOMOSubComponent subComponent) {
        final COINCOMOComponent component = (COINCOMOComponent) subComponent.getParent();
        final COINCOMOComponentParameters parameters = component.getParameters();

        double[][] eafWeights = parameters.getEAFWeights();
        Rating[] eafRatings = subComponent.getEAFRatings();
        return eafWeights[eaf.ordinal()][eafRatings[eaf.ordinal()].ordinal()];
    }

    /**
     *
     */
    private static void initializeRiskLevelMaps() {
        byte[][] riskLevelMap = null;

        // SCED vs RELY map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.HI.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VHI.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VHI.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.RELY.toString(), riskLevelMap);

        // SCED vs CPLX map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.HI.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VHI.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.XHI.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VHI.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.XHI.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.XHI.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.CPLX.toString(), riskLevelMap);

        // SCED vs TIME map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.HI.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VHI.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.XHI.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VHI.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.XHI.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.XHI.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.TIME.toString(), riskLevelMap);

        // SCED vs PVOL map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.HI.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VHI.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VHI.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.PVOL.toString(), riskLevelMap);

        // SCED vs TOOL map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.TOOL.toString(), riskLevelMap);

        // SCED vs PLEX map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.PLEX.toString(), riskLevelMap);

        // SCED vs PCAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.PCAP.toString(), riskLevelMap);

        // SCED vs APEX map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.APEX.toString(), riskLevelMap);

        // SCED vs ACAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.ACAP.toString(), riskLevelMap);

        // SCED vs LTEX map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.LTEX.toString(), riskLevelMap);

        // SCED vs PMAT map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.SF.PMAT.toString(), riskLevelMap);

        //==========

        // RELY vs ACAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.RELY.toString() + COINCOMOConstants.EAF.ACAP.toString(), riskLevelMap);

        // RELY vs PCAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.RELY.toString() + COINCOMOConstants.EAF.PCAP.toString(), riskLevelMap);

        // CPLX vs ACAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.CPLX.toString() + COINCOMOConstants.EAF.ACAP.toString(), riskLevelMap);

        // CPLX vs PCAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.CPLX.toString() + COINCOMOConstants.EAF.PCAP.toString(), riskLevelMap);

        // CPLX vs TOOL map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.CPLX.toString() + COINCOMOConstants.EAF.TOOL.toString(), riskLevelMap);

        // RELY vs PMAT map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.RELY.toString() + COINCOMOConstants.SF.PMAT.toString(), riskLevelMap);

        // PMAT vs ACAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.SF.PMAT.toString() + COINCOMOConstants.EAF.ACAP.toString(), riskLevelMap);

        // STOR vs ACAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.STOR.toString() + COINCOMOConstants.EAF.ACAP.toString(), riskLevelMap);

        // TIME vs ACAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.TIME.toString() + COINCOMOConstants.EAF.ACAP.toString(), riskLevelMap);

        //Code changed by Roopa Dharap -------------------------
        //Mapping for TOOL_ACAP and TOOL_PCAP were wrong. Changed now.

        // TOOL vs ACAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.TOOL.toString() + COINCOMOConstants.EAF.ACAP.toString(), riskLevelMap);

        // TOOL vs PCAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.STOR.toString() + COINCOMOConstants.EAF.PCAP.toString(), riskLevelMap);

        //==========

        // RUSE vs APEX map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.RUSE.toString() + COINCOMOConstants.EAF.APEX.toString(), riskLevelMap);

        // RUSE vs LTEX map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.RUSE.toString() + COINCOMOConstants.EAF.LTEX.toString(), riskLevelMap);

        // PMAT vs PCAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.SF.PMAT.toString() + COINCOMOConstants.EAF.PCAP.toString(), riskLevelMap);

        // STOR vs PCAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.STOR.toString() + COINCOMOConstants.EAF.PCAP.toString(), riskLevelMap);

        // TIME vs PCAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.TIME.toString() + COINCOMOConstants.EAF.PCAP.toString(), riskLevelMap);

        // LTEX vs PCAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.LTEX.toString() + COINCOMOConstants.EAF.PCAP.toString(), riskLevelMap);

        // PVOL vs PLEX map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.PVOL.toString() + COINCOMOConstants.EAF.PLEX.toString(), riskLevelMap);

        // TOOL vs PMAT map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.TOOL.toString() + COINCOMOConstants.SF.PMAT.toString(), riskLevelMap);

        // TIME vs TOOL map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.TIME.toString() + COINCOMOConstants.EAF.TOOL.toString(), riskLevelMap);

        // TEAM vs APEX map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.SF.TEAM.toString() + COINCOMOConstants.EAF.APEX.toString(), riskLevelMap);

        // TEAM vs SCED map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.SF.TEAM.toString() + COINCOMOConstants.EAF.SCED.toString(), riskLevelMap);

        // TEAM vs SITE map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.SF.TEAM.toString() + COINCOMOConstants.EAF.SITE.toString(), riskLevelMap);
    }

    //------------------end risk module
//End Change-----------------------------------
    public static COINCOMOSubComponent insertSubComponent(COINCOMOComponent component) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;
        COINCOMOSubComponent subComponent = null;
        StringBuilder defaultName = new StringBuilder(COINCOMOSubComponent.DEFAULT_NAME);

        // If exists
        if (component != null) {
            defaultName.insert(defaultName.length() - 1, component.getNextAutoID());

            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Insert only when a connection is available
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Insert_SubComponent(?, ?);";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setString(1, defaultName.toString());
                        preparedStatement.setLong(2, component.getDatabaseID());

                        // Insert
                        ResultSet rs = preparedStatement.executeQuery();

                        if (rs.next()) {
                            long subComponentID = rs.getLong(1);

                            if (subComponentID < 0) {
                                log(Level.SEVERE, "SQL command\'" + sql + "\' failed.");
                                isSuccessful = false;
                            } else {
                                subComponent = new COINCOMOSubComponent();
                                subComponent.setName(defaultName.toString());
                                subComponent.setDatabaseID(subComponentID);
                            }
                        }

                        // Free from memory
                        preparedStatement.close();

                        // If any of the sql statement failed to execute, rollback the entire operation.
                        if (!isSuccessful) {
                            connection.rollback();
                        }
                    } catch (SQLException e) {
                        // Print any problem
                        log(Level.SEVERE, e.getLocalizedMessage());
                        //e.printStackTrace();
                    }
                }

                COINCOMODatabaseManager.disconnect(connection);
            } else {
                subComponent = new COINCOMOSubComponent();
                subComponent.setName(defaultName.toString());
            }

            // If the sub-component unit is properly created, then add it to the component unit.
            if (subComponent != null) {
                component.addSubUnit(subComponent);
                component.calculateNextAutoID(COINCOMOSubComponent.DEFAULT_NAME, subComponent.getName());
            }
        }

        return subComponent;
    }

    public static boolean updateSubComponent(COINCOMOSubComponent subComponent, boolean recursive) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;

        // If exists
        if (subComponent != null) {
            subComponent.setEAF(calculateEAFWithSCED(subComponent));
            subComponent.setSubTotals(calculateSubTotals(subComponent));
            if (subComponent.getCalculationMethod() == CalculationMethod.UsingTable) {
                subComponent.setTotalUnadjustedFunctionPoints(calculateTotalUnadjustedFunctionPoints(subComponent));
            }
            subComponent.setEquivalentSLOC(calculateEquivalentSLOC(subComponent));
            subComponent.setSumOfSLOCs(calculateSumOfSLOCs(subComponent));
            subComponent.setSLOC(calculateSLOC(subComponent));
            subComponent.setNominalEffort(calculatePMNominal(subComponent));
            subComponent.setEstimatedEffort(calculatePMEstimated(subComponent));
            subComponent.setProductivity(calculateProductivity(subComponent));
            subComponent.setCost(calculateCost(subComponent));
            subComponent.setInstructionCost(calculateInstructionCost(subComponent));
            subComponent.setStaff(calculateStaff(subComponent));

            //Changed by Roopa Dharap----------------------
            //subComponent.setRisk(0.0d);

            //subComponent.setRisk(calculateRisk(subComponent, RISK.TOTAL));//rebecca
            subComponent.setRisk(calculateRiskTotal(subComponent));
            //End Change-----------------------------------

            subComponent.setEffort(calculateEffort(subComponent));
            subComponent.setSchedule(calculateSchedule(subComponent));

            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Update only when a connection is available
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Update_SubComponent("
                                // 18 parameters for sub-component
                                + "  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
                                // 36 parameters for sub-component EAFs (2 parameters for each EAF rating/increment)
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                //  1 parameter for sub-component new sloc tab
                                + ", ?"
                                // 25 parameters for sub-component function points tab
                                + ", ?, ?::ratio_type_enum, ?::calculation_method_enum, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
                                + ");";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);

                        // Replace "?" with respective values
                        int index = 0;
                        preparedStatement.setLong(++index, subComponent.getDatabaseID());
                        preparedStatement.setString(++index, subComponent.getName());
                        preparedStatement.setLong(++index, subComponent.getParent().getDatabaseID());
                        preparedStatement.setLong(++index, subComponent.getSLOC());
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(subComponent.getCost()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(subComponent.getStaff()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(subComponent.getEffort()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(subComponent.getSchedule()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(subComponent.getProductivity()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(subComponent.getInstructionCost()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(subComponent.getRisk())); //Changed by Roopa Dharap
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(subComponent.getNominalEffort()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(subComponent.getEstimatedEffort()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(subComponent.getEAF()));
                        preparedStatement.setLong(++index, subComponent.getSumOfSLOCs());
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(subComponent.getLaborRate()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(subComponent.getREVL()));
                        preparedStatement.setString(++index, subComponent.getLanguage());

                        Rating ratings[] = subComponent.getEAFRatings();
                        Increment increments[] = subComponent.getEAFIncrements();
                        for (int i = 0; i < COINCOMOConstants.EAFS.length - 1; i++) {
                            preparedStatement.setString(++index, ratings[i].toString());
                            preparedStatement.setString(++index, increments[i].toString());
                        }

                        preparedStatement.setLong(++index, subComponent.getNewSLOC());

                        preparedStatement.setInt(++index, subComponent.getMultiplier());
                        preparedStatement.setString(++index, subComponent.getRatioType().toString());
                        preparedStatement.setString(++index, subComponent.getCalculationMethod().toString());
                        preparedStatement.setInt(++index, subComponent.getInternalLogicalFiles()[0]);
                        preparedStatement.setInt(++index, subComponent.getInternalLogicalFiles()[1]);
                        preparedStatement.setInt(++index, subComponent.getInternalLogicalFiles()[2]);
                        preparedStatement.setInt(++index, subComponent.getInternalLogicalFiles()[3]);
                        preparedStatement.setInt(++index, subComponent.getExternalInterfaceFiles()[0]);
                        preparedStatement.setInt(++index, subComponent.getExternalInterfaceFiles()[1]);
                        preparedStatement.setInt(++index, subComponent.getExternalInterfaceFiles()[2]);
                        preparedStatement.setInt(++index, subComponent.getExternalInterfaceFiles()[3]);
                        preparedStatement.setInt(++index, subComponent.getExternalInputs()[0]);
                        preparedStatement.setInt(++index, subComponent.getExternalInputs()[1]);
                        preparedStatement.setInt(++index, subComponent.getExternalInputs()[2]);
                        preparedStatement.setInt(++index, subComponent.getExternalInputs()[3]);
                        preparedStatement.setInt(++index, subComponent.getExternalOutputs()[0]);
                        preparedStatement.setInt(++index, subComponent.getExternalOutputs()[1]);
                        preparedStatement.setInt(++index, subComponent.getExternalOutputs()[2]);
                        preparedStatement.setInt(++index, subComponent.getExternalOutputs()[3]);
                        preparedStatement.setInt(++index, subComponent.getExternalInquiries()[0]);
                        preparedStatement.setInt(++index, subComponent.getExternalInquiries()[1]);
                        preparedStatement.setInt(++index, subComponent.getExternalInquiries()[2]);
                        preparedStatement.setInt(++index, subComponent.getExternalInquiries()[3]);
                        preparedStatement.setInt(++index, subComponent.getTotalUnadjustedFunctionPoints());
                        preparedStatement.setLong(++index, subComponent.getEquivalentSLOC());
                        // Sanity check against parameter numbers
                        if (index != 80) {
                            log(Level.WARNING, "Wrong number of parameters are set for sql \'" + preparedStatement.toString() + "\'.");
                        }

                        // Update
                        ResultSet rs = preparedStatement.executeQuery();
                        if (rs.next()) {
                            isSuccessful = rs.getBoolean(1);
                        }

                        // Free from memory
                        preparedStatement.close();

                        // If any of the sql statement failed to execute, rollback the entire operation.
                        if (!isSuccessful) {
                            connection.rollback();
                        }
                    } catch (SQLException e) {
                        // Print any problem
                        log(Level.SEVERE, e.getLocalizedMessage());
                        //e.printStackTrace();
                    }
                }

                COINCOMODatabaseManager.disconnect(connection);
            }

            if (isSuccessful && recursive) {
                COINCOMOComponentManager.updateComponent((COINCOMOComponent) subComponent.getParent(), recursive);
            }
        }

        return isSuccessful;
    }

    public static boolean deleteSubComponent(COINCOMOSubComponent subComponent) {
        // If exists
        if (subComponent != null) {
            ArrayList<COINCOMOSubComponent> subComponents = new ArrayList<COINCOMOSubComponent>();
            subComponents.add(subComponent);

            return deleteSubComponents(subComponents);
        } else {
            return false;
        }
    }

    public static boolean deleteSubComponents(ArrayList<COINCOMOSubComponent> SubComponents) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;

        // If exists
        if (SubComponents != null && !SubComponents.isEmpty()) {
            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Delete only when a connection is available
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Delete_SubComponent(?);";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);

                        for (int i = 0; i < SubComponents.size(); i++) {
                            COINCOMOSubComponent subComponent = SubComponents.get(i);
                            preparedStatement.setLong(1, subComponent.getDatabaseID());

                            // Delete
                            ResultSet rs = preparedStatement.executeQuery();
                            if (rs.next()) {
                                isSuccessful = isSuccessful & rs.getBoolean(1);
                            }
                        }

                        // Free from memory
                        preparedStatement.close();

                        // If any of the sql statement failed to execute, rollback the entire operation.
                        if (!isSuccessful) {
                            connection.rollback();
                        }
                    } catch (SQLException e) {
                        // Print any problem
                        log(Level.SEVERE, e.getLocalizedMessage());
                        //e.printStackTrace();
                    }
                }

                COINCOMODatabaseManager.disconnect(connection);
            }

            if (isSuccessful) {
                for (int i = 0; i < SubComponents.size(); i++) {
                    COINCOMOComponent component = (COINCOMOComponent) SubComponents.get(i).getParent();
                    component.removeSubUnit(SubComponents.get(i));
                }
            }
        }

        return isSuccessful;
    }

    public static boolean loadSubComponent(COINCOMOSubComponent subComponent, DBConnection connection) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean isSuccessful = false;

        if (operationMode == OperationMode.DATABASE) {
            // Check if a Connection is available ..
            if (connection != null) {
                try {
                    String sql = "SELECT * FROM Get_AllAdaptationAndReuses(?);";
                    int nextID = 0;

                    // Efficient Query Statement
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);

                    // Replace "?" With Respective Values ..
                    preparedStatement.setLong(1, subComponent.getDatabaseID());

                    ResultSet rs = preparedStatement.executeQuery();

                    // Loop through ..
                    while (rs != null && rs.next()) {
                        int index = 0;

                        long adaptationID = rs.getLong(++index);
                        String name = rs.getString(++index);
                        long subComponentID = rs.getLong(++index);
                        long adaptedSLOC = rs.getLong(++index);
                        double DM = rs.getDouble(++index);
                        double CM = rs.getDouble(++index);
                        double IM = rs.getDouble(++index);
                        double SU = rs.getDouble(++index);
                        double AA = rs.getDouble(++index);
                        double UNFM = rs.getDouble(++index);
                        double AT = rs.getDouble(++index);
                        double ATPROD = rs.getDouble(++index);
                        double AAF = rs.getDouble(++index);
                        long equivalentSLOC = rs.getLong(++index);

                        // Create a sub component
                        COINCOMOAdaptationAndReuse adaptation = new COINCOMOAdaptationAndReuse();

                        // Set Parameters
                        adaptation.setDatabaseID(adaptationID);
                        adaptation.setName(name);
                        adaptation.setAdaptedSLOC(adaptedSLOC);
                        adaptation.setDesignModified(DM);
                        adaptation.setCodeModified(CM);
                        adaptation.setIntegrationModified(IM);
                        adaptation.setAssessmentAndAssimilation(AA);
                        adaptation.setSoftwareUnderstanding(SU);
                        adaptation.setUnfamiliarityWithSoftware(UNFM);
                        adaptation.setAutomaticallyTranslated(AT);
                        adaptation.setAutomaticTranslationProductivity(ATPROD);
                        adaptation.setAdaptationAdjustmentFactor(AAF);
                        adaptation.setEquivalentSLOC(equivalentSLOC);

                        adaptation.clearDirty();

                        subComponent.addSubUnit(adaptation);
                        subComponent.calculateNextAutoID(COINCOMOAdaptationAndReuse.DEFAULT_NAME, adaptation.getName());
                    }

                    // Release From Memory
                    preparedStatement.close();
                } catch (SQLException e) {
                    // Print Any Problems ..
                    log(Level.SEVERE, e.getLocalizedMessage());
                    //e.printStackTrace();
                }
            }

            isSuccessful = true;
        }

        return isSuccessful;
    }

    public static boolean updateSubComponentName(COINCOMOSubComponent subComponent) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;

        // If exists
        if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
            DBConnection connection = COINCOMODatabaseManager.getConnection();

            // Delete only when a connection is available
            if (connection != null) {
                try {
                    String sql = "SELECT * FROM Update_SubComponentName(?, ?);";

                    // Efficient & safer way through prepared statement
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);

                    preparedStatement.setLong(1, subComponent.getDatabaseID());
                    preparedStatement.setString(2, subComponent.getName());

                    // Delete
                    ResultSet rs = preparedStatement.executeQuery();
                    if (rs.next()) {
                        isSuccessful = rs.getBoolean(1);
                    }

                    // Free from memory
                    preparedStatement.close();

                    // If any of the sql statement failed to execute, rollback the entire operation.
                    if (!isSuccessful) {
                        connection.rollback();
                    }
                } catch (SQLException e) {
                    // Print any problem
                    log(Level.SEVERE, e.getLocalizedMessage());
                    //e.printStackTrace();
                }
            }

            COINCOMODatabaseManager.disconnect(connection);
        }

        // Determine the next auto-generated name will be
        if (isSuccessful) {
            COINCOMOComponent component = (COINCOMOComponent) subComponent.getParent();
            component.calculateNextAutoID(COINCOMOSubComponent.DEFAULT_NAME, subComponent.getName());
        }

        return isSuccessful;
    }

    public static COINCOMOSubComponent copySubComponent(COINCOMOSubComponent subComponentToBeCopied, COINCOMOComponent componentToBeCopiedTo) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        COINCOMOSubComponent subComponent = null;

        // If exists
        if (subComponentToBeCopied != null && componentToBeCopiedTo != null) {
            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Insert only when a connection is available
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Copy_SubComponent(?, ?);";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setLong(1, subComponentToBeCopied.getDatabaseID());
                        preparedStatement.setLong(2, componentToBeCopiedTo.getDatabaseID());

                        // Copy
                        System.out.println(preparedStatement.toString());
                        ResultSet rs = preparedStatement.executeQuery();

                        if (rs.next()) {
                            long subComponentID = rs.getLong(1);

                            if (subComponentID < 0) {
                                log(Level.SEVERE, "SQL command\'" + sql + "\' failed.");
                            } else {
                                subComponent = new COINCOMOSubComponent();
                                subComponent.setDatabaseID(subComponentID);
                            }
                        }

                        preparedStatement.close();
                    } catch (SQLException e) {
                        // Print any problem
                        log(Level.SEVERE, e.getLocalizedMessage());
                        //e.printStackTrace();
                    }
                }

                COINCOMODatabaseManager.disconnect(connection);
            } else {
                subComponent = new COINCOMOSubComponent();
            }

            // If the sub-component unit is properly copied, then add it to the component unit.
            if (subComponent != null) {
                componentToBeCopiedTo.addSubUnit(subComponent);

                if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                    subComponent.copyUnit(subComponentToBeCopied, false);

                    DBConnection connection = COINCOMODatabaseManager.getConnection();
                    if (connection != null) {
                        loadSubComponent(subComponent, connection);
                    } else {
                        log(Level.SEVERE, "Unable to get a DBConnection for loadSubComponent() in copySubComponent().");
                    }
                    COINCOMODatabaseManager.disconnect(connection);
                } else {
                    subComponent.copyUnit(subComponentToBeCopied, true);
                }
            }
        }

        return subComponent;
    }

    private static void log(Level level, String message) {
        Logger.getLogger(COINCOMOSubComponentManager.class.getName()).log(level, message);
    }
}