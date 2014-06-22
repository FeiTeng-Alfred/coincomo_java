/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package database;

import core.COINCOMOComponent;
import core.COINCOMOComponentParameters;
import core.COINCOMOConstants;
import core.COINCOMOConstants.CalculationMethod;
import core.COINCOMOConstants.Increment;
import core.COINCOMOConstants.OperationMode;
import core.COINCOMOConstants.Rating;
import core.COINCOMOConstants.RatioType;
import core.COINCOMOConstants.Scenario;
import core.COINCOMOSubComponent;
import core.COINCOMOSubSystem;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.COINCOMO;

/**
 *
 * @author Raed Shomali
 */
public class COINCOMOComponentManager extends COINCOMOManager {

    private static final int scaleFactorRow = 5;
    private static final int scaleFactorColumn = 6;

    public static double calculateSCED(COINCOMOComponent component) {
        final COINCOMOComponentParameters parameters = component.getParameters();
        final double[][] eafWeights = parameters.getEAFWeights();
        final Rating scedRating = component.getSCEDRating();
        final Increment scedIncrement = component.getSCEDIncrement();

        double result = 1.0d;

        double difference = 0.0d;
        double fraction = 0.0d;
        double rating = 0.0d;

        /*
         * From USC COCOMO II User's manual
         * (Please refer to COINCOMOSubComponentManager class, calculateEAF() function for detailed explanation)
         */
        if (scedRating == Rating.XHI) {
            difference = 0.0d;
            fraction = 0.0d;
        } else {
            if (eafWeights[COINCOMOConstants.EAFS.length - 1][scedRating.ordinal() + 1] == 0.0d) {
                difference = 0.0d;
                fraction = 0.0d;
            } else {
                difference = eafWeights[COINCOMOConstants.EAFS.length - 1][scedRating.ordinal() + 1] - eafWeights[COINCOMOConstants.EAFS.length - 1][scedRating.ordinal()];
                fraction = scedIncrement.getDoubleValue();
            }
        }

        rating = eafWeights[COINCOMOConstants.EAFS.length - 1][scedRating.ordinal()] + difference * fraction;
        result *= rating;

        return result;
    }


    public static double calculateSCEDPercent(COINCOMOComponent component) {
        //The following weights are used to calculate SCED%
        final double[] SCED_PERCENTS = {0.75d, 0.85d, 1.00d, 1.30d, 1.60d, 0.00d};

        final Rating scedRating = component.getSCEDRating();
        final Increment scedIncrement = component.getSCEDIncrement();

        double result = 0.0d;
        double difference = 0.0d;
        double fraction = 0.0d;

        if (scedRating == Rating.XHI) {
            difference = 0.0d;
            fraction = 0.0d;
        } else {
            if (SCED_PERCENTS[scedRating.ordinal()+1] == 0.0d) {
                difference = 0.0d;
                fraction = 0.0d;
            } else {
                difference = SCED_PERCENTS[scedRating.ordinal()+1] - SCED_PERCENTS[scedRating.ordinal()];
                fraction = scedIncrement.getDoubleValue();
            }
        }
        result = SCED_PERCENTS[component.getSCEDRating().ordinal()] + difference * fraction;
        result *= 100.0d;

        return result;
    }

    public static double calculateSF(COINCOMOComponent component) {
        final COINCOMOComponentParameters parameters = component.getParameters();
        final double[][] sfWeights = parameters.getSFWeights();
        final Rating[] sfRatings = component.getSFRatings();
        final Increment[] sfIncrements = component.getSFIncrements();

        double result = 0.0d;

        for (int i = 0; i < COINCOMOConstants.SFS.length; i++) {
            double difference = 0.0d;
            double fraction = 0.0d;
            double rating = 0.0d;

            /*
             * From USC COCOMO II User's manual
             * (Please refer to COINCOMOSubComponentManager class, calculateEAF() function for detailed explanation)
             * HOWEVER, SF IS accumulative, INSTEAD OF multiplicative.
             */
            if (sfRatings[i] == Rating.XHI) {
                difference = 0.0d;
                fraction = 0.0d;
            } else {
                difference = sfWeights[i][sfRatings[i].ordinal() + 1] - sfWeights[i][sfRatings[i].ordinal()];
                fraction = sfIncrements[i].getDoubleValue();
            }

            rating = sfWeights[i][sfRatings[i].ordinal()] + difference * fraction;
            result += rating;
        }

        return result;
    }

    public static double calculateExponentE(COINCOMOComponent component) {
        final COINCOMOComponentParameters parameters = component.getParameters();
        final double parameterB = parameters.getB();
        final double sf = calculateSF(component);
        final double exponentE = parameterB + 0.01d * sf;

        return exponentE;
    }

    public static double calculateExponentF(COINCOMOComponent component) {
        final COINCOMOComponentParameters parameters = component.getParameters();
        final double exponentE = calculateExponentE(component);
        final double parameterB = parameters.getB();
        final double parameterD = parameters.getD();
        final double exponentF = parameterD + 0.2d * (exponentE - parameterB);

        return exponentF;
    }

    public static double calculateEffort(COINCOMOComponent component) {
        double effort = 0.0d;
        Iterator iter = component.getListOfSubUnits().iterator();

        while (iter.hasNext()) {
            final COINCOMOSubComponent subComponent = (COINCOMOSubComponent) iter.next();
            effort += subComponent.getEffort();
        }

        if (Double.isNaN(effort) || Double.isInfinite(effort)) {
            return 0.0d;
        } else {
            return effort;
        }
    }

    public static double calculateSchedule(COINCOMOComponent component, Scenario scenario) {
        final double[] SCENARIOS = {(1.0d/1.25d), 1.00d, (1.0d*1.25d)};

        final COINCOMOComponentParameters parameters = component.getParameters();
        final double parameterC = parameters.getC();
        final double exponentF = calculateExponentF(component);
        final double scedPercent = calculateSCEDPercent(component);

        double schedule = 0.0d;
        double pmAggregate = 0.0d;
        Iterator iter = component.getListOfSubUnits().iterator();

        while (iter.hasNext()) {
            final COINCOMOSubComponent subComponent = (COINCOMOSubComponent) iter.next();
            final double pmBase = COINCOMOSubComponentManager.calculatePMBase(subComponent);
            final double pmAuto = COINCOMOSubComponentManager.calculatePMAuto(subComponent);
            final double eaf = COINCOMOSubComponentManager.calculateEAFWithoutSCED(subComponent);

            pmAggregate += pmBase * eaf;
        }

        pmAggregate *= SCENARIOS[scenario.ordinal()];
        schedule = parameterC * Math.pow(pmAggregate, exponentF) * (scedPercent / 100.0d);

        return schedule;
    }

    public static double calculateProductivity(COINCOMOComponent component) {
        final double sloc = component.getSLOC();
        final double effort = calculateEffort(component);
        final double productivity = sloc / effort;

        if (Double.isNaN(productivity) || Double.isInfinite(productivity)) {
            return 0.0d;
        } else {
            return productivity;
        }
    }

    public static double calculateCost(COINCOMOComponent component) {
        double cost = 0.0d;
        Iterator iter = component.getListOfSubUnits().iterator();

        while (iter.hasNext()) {
            final COINCOMOSubComponent subComponent = (COINCOMOSubComponent) iter.next();
            cost += subComponent.getCost();
        }

        if (Double.isNaN(cost) || Double.isInfinite(cost)) {
            return 0.0d;
        } else {
            return cost;
        }
    }

    public static double calculateInstructionCost(COINCOMOComponent component) {
        final double cost = calculateCost(component);
        final double sloc = component.getSLOC();
        final double instructionCost = cost / sloc;

        if (Double.isNaN(instructionCost) || Double.isInfinite(instructionCost)) {
            return 0.0d;
        } else {
            return instructionCost;
        }
    }

    public static double calculateStaff(COINCOMOComponent component) {
        double staff = 0.0d;
        Iterator iter = component.getListOfSubUnits().iterator();

        while (iter.hasNext()) {
            final COINCOMOSubComponent subComponent = (COINCOMOSubComponent) iter.next();
            staff += subComponent.getStaff();
        }

        if (Double.isNaN(staff) || Double.isInfinite(staff)) {
            return 0.0d;
        } else {
            return staff;
        }
    }

    public static double calculateRisk(COINCOMOComponent component) {
        double risk = 0.0d;
        Iterator iter = component.getListOfSubUnits().iterator();

        while (iter.hasNext()) {
            final COINCOMOSubComponent subComponent = (COINCOMOSubComponent) iter.next();
            risk += subComponent.getRisk();
        }

        if (Double.isNaN(risk) || Double.isInfinite(risk)) {
            return 0.0d;
        } else {
            return risk; //Changed by Roopa Dharap        }
        }
    }

    public static long calculateSLOCWithoutREVL(COINCOMOComponent component) {
        long slocWithoutREVL = 0;
        Iterator iter = component.getListOfSubUnits().iterator();

        while (iter.hasNext()) {
            final COINCOMOSubComponent subComponent = (COINCOMOSubComponent) iter.next();
            slocWithoutREVL += subComponent.getSumOfSLOCs();
        }

        return slocWithoutREVL;
    }

    public static double calculateMof64(COINCOMOComponent component) {
        double result = 0.0d;

        final COINCOMOComponentParameters parameters = component.getParameters();
        final double parameterC = parameters.getC();
        final double exponentF = calculateExponentF(component);
        final double scedPercent = calculateSCEDPercent(component);

        result = parameterC * Math.pow(64, exponentF) * (scedPercent / 100.0d);

        if (Double.isNaN(result) || Double.isInfinite(result)) {
            return 0.0d;
        } else {
            return result;
        }
    }

    public static double calculatePMNS(COINCOMOComponent component) {
        final COINCOMOComponentParameters parameters = component.getParameters();
        final double kSlocComponent = ((double) component.getSLOC()) / 1000.0d;
        final double parameterA = parameters.getA();
        final double exponentE = calculateExponentE(component);
        final double hoursPerPM = parameters.getWorkHours();
        final double hoursPerPMRatio = COINCOMOComponentParameters.WORK_HOURS / hoursPerPM;
        double pmNSComponent = 0.0d;

        double pmBaseComponent = parameterA * Math.pow(kSlocComponent, exponentE) * hoursPerPMRatio;

        Iterator iter = component.getListOfSubUnits().iterator();
        while (iter.hasNext()) {
            final COINCOMOSubComponent tempSubComponent = (COINCOMOSubComponent) iter.next();
            final long tempSlocSubComponent = tempSubComponent.getSLOC();
            final double tempKSlocSubComponent = ((double) tempSlocSubComponent) / 1000.0d;
            final double tempEAFSubComponent = COINCOMOSubComponentManager.calculateEAFWithoutSCED(tempSubComponent);
            final double tempPMBaseSubComponent = pmBaseComponent * (tempKSlocSubComponent / kSlocComponent);

            pmNSComponent += tempPMBaseSubComponent * tempEAFSubComponent;
        }

        if (Double.isNaN(pmNSComponent) || Double.isInfinite(pmNSComponent)) {
            return 0.0d;
        } else {
            return pmNSComponent;
        }
    }

    public static double calculatePMBS(COINCOMOComponent component) {
        final COINCOMOComponentParameters parameters = component.getParameters();
        final double hoursPerPM = parameters.getWorkHours();
        /* currently COPSEMO does not work with Hours/PM ratio other than the default 152.0 Hours/PM? */
        final double hoursPerPMRatioReverse = hoursPerPM / COINCOMOComponentParameters.WORK_HOURS;
        //final double hoursPerPMRatioReverse = 1.0;
        final double pm = calculateEffort(component);
        double result = hoursPerPMRatioReverse * pm;

        if (Double.isNaN(result) || Double.isInfinite(result)) {
            return 0.0d;
        } else {
            return result;
        }
    }

    /*
     * mBS  = M_BS in COPSEMO.XLS
     * pm   = PM_C in COPSEMO.XLS (PM in UserManual.pdf, the estimated effort)
     */
    public static double calculateMBS(COINCOMOComponent component) {
        final COINCOMOComponentParameters parameters = component.getParameters();
        final double hoursPerPM = parameters.getWorkHours();
        /* currently COPSEMO does not work with Hours/PM ratio other than the default 152.0 Hours/PM? */
        final double hoursPerPMRatioReverse = hoursPerPM / COINCOMOComponentParameters.WORK_HOURS;
        //final double hoursPerPMRatioReverse = 1.0;
        final double parameterC = parameters.getC();
        final double exponentF = calculateExponentF(component);
        final double sced = calculateSCED(component);
        final double scedPercent = calculateSCEDPercent(component);
        final double pm = calculateEffort(component);
        final double mof64 = calculateMof64(component);
        double mBS = 0.0d;

        double pmNoSCED = pm / sced;

        if (pmNoSCED < 16.0d) {
            mBS = Math.sqrt(pmNoSCED);
        } else if (pmNoSCED >= 16.0d && pmNoSCED < 64.0d) {
            mBS = ((mof64 - 4.0d) / 48.0d * pmNoSCED) + (4.0d - (16.0d * (mof64 - 4.0d) / 48.0d));
        } else {
            mBS = parameterC * Math.pow(pmNoSCED, exponentF) * scedPercent / 100.0d;
        }

        mBS *= hoursPerPMRatioReverse;

//        System.out.println("pm: " + pm);
//        System.out.println("pmNoSCED: " + pmNoSCED);
//        System.out.println("pmNoSCED2: " + calculatePMNS(component));
//        System.out.println("mof64: " + mof64);
//        System.out.println("pmBS: " + (hoursPerPMRatioReverse * pm));
//        System.out.println("mBS: " + mBS);
//        System.out.println("pBS: " + (pmNoSCED / mBS));

        if (Double.isNaN(mBS) || Double.isInfinite(mBS)) {
            return 0.0d;
        } else {
            return mBS;
        }
    }

    public static double calculatePBS(COINCOMOComponent component) {
        final double pmBS = calculatePMBS(component);
        final double mBS = calculateMBS(component);
        double pBS = pmBS / mBS;

        if (Double.isNaN(pBS) || Double.isInfinite(pBS)) {
            return 0.0d;
        } else {
            return pBS;
        }
    }

//    public static double get_M_Total_EC(COINCOMOComponent component) {
//        return get_Elaboration_M(component) + get_Construction_M(component);
//    }
//
//    public static double get_PM_Total(COINCOMOComponent component) {
//        return COINCOMOComponentManager.get_Inception_PM(component)
//                + COINCOMOComponentManager.get_Elaboration_PM(component)
//                + COINCOMOComponentManager.get_Construction_PM(component)
//                + COINCOMOComponentManager.get_Transition_PM(component);
//    }
//
//    public static double get_PM_Total_EC(COINCOMOComponent component) {
//        return get_Elaboration_PM(component) + get_Construction_PM(component);
//    }
//
//    public static double get_Total_Prod(COINCOMOComponent component) {
//        long finalSLOC = get_Final_SLOC(component);
//        double totalEffort = get_Total_Effort(component);
//        if (totalEffort == 0.0 || totalEffort == Double.NaN) {
//            return 0.0;
//        } else {
//            return (double) finalSLOC / totalEffort;
//        }
//    }
//
//    public static double get_Total_Cost(COINCOMOComponent component) {
//        double sum = 0;
//        Iterator<COINCOMOUnit> iter = component.getListOfSubUnits().iterator();
//
//        while (iter.hasNext()) {
//            COINCOMOSubComponent unit = (COINCOMOSubComponent) iter.next();
//            sum += COINCOMOSubComponentManager.get_Cost(unit);
//        }
//        return sum;
//    }
//
//    public static double get_Total_Inst_Cost(COINCOMOComponent component) {
//        double totalCost = get_Cost(component);
//        float totalSLOC = 0;
//        Iterator iter = component.getListOfSubUnits().iterator();
//
//        while (iter.hasNext()) {
//            COINCOMOSubComponent unit = (COINCOMOSubComponent) iter.next();
//            totalSLOC += (float) COINCOMOSubComponentManager.get_Final_SLOC(unit);
//        }
//        if (totalSLOC == 0) {
//            return 0;
//        } else {
//            return totalCost / totalSLOC;
//        }
//    }
//
//    /*new version*/
//    public static double get_Total_Effort(COINCOMOComponent component) {
//        double sum = 0;
//        Iterator iter = component.getListOfSubUnits().iterator();
//
//        while (iter.hasNext()) {
//            COINCOMOSubComponent unit = (COINCOMOSubComponent) iter.next();
//            sum += COINCOMOSubComponentManager.get_Estimated_Effort(unit);
//        }
//        return sum;
//    }
//
//    /*new version for logic 
//     public static float get_Total_Effort( COINCOMOComponent component )
//     {   
//     double finalSLOC = (double)((COINCOMOSubComponent)component.getParent()).getFinalSLOC();
//     double scaleFactor = (double)component.getScaleFactor();
//     double exponentB = 0.91 + 0.01*scaleFactor;
//     double EM17 = (double)((COINCOMOSubComponent)component.getParent()).getEaf();        
//     double sizeToB = Math.pow(finalSLOC, exponentB);
//     double ASLOC = (double)((COINCOMOSubComponent)component.getParent()).getAdaptiveSLOC();
//     double AT = (double)((COINCOMOSubComponent)component.getParent()).getAutomaticallyTranslated();
//     double ATPROD = (double)((COINCOMOSubComponent)component.getParent()).getAutoTranslationProductivity();
//        
//     //calculate the PM 
//     double result = 0.0;
//     if(ATPROD == 0)
//     result = EM17 * 2.94 * sizeToB;
//     else 
//     result = EM17 * 2.94 * sizeToB + (ASLOC*AT/100)/ATPROD;
//        
//     return (float)result;        
//     } 
//     //*/
//    
//    public static float get_Scale_Factor(COINCOMOComponent component) {
//        //lookup table for scale factor
//        double[] PREC = new double[scaleFactorColumn];
//        double[] FLEX = new double[scaleFactorColumn];
//        double[] RESL = new double[scaleFactorColumn];
//        double[] TEAM = new double[scaleFactorColumn];
//        double[] PMAT = new double[scaleFactorColumn];
//        double[] INCR = {0.00, 0.25, 0.50, 0.75};
//
//        for (int j = 0; j < scaleFactorColumn; j++) {
//            PREC[j] = component.getLocalCalibration().getScaleFactorsValue(0, j);
//            FLEX[j] = component.getLocalCalibration().getScaleFactorsValue(1, j);
//            RESL[j] = component.getLocalCalibration().getScaleFactorsValue(2, j);
//            TEAM[j] = component.getLocalCalibration().getScaleFactorsValue(3, j);
//            PMAT[j] = component.getLocalCalibration().getScaleFactorsValue(4, j);
//        }
//
//        final Rating sfRatings[] = component.getSFRatings();
//        final Increment sfIncrements[] = component.getSFIncrements();
//        int[] scaleFactorBase = new int[sfRatings.length];
//        int[] scaleFactorIncr = new int[sfIncrements.length];
//        for (int i = 0; i < sfRatings.length; i++) {
//            scaleFactorBase[i] = sfRatings[i].ordinal() + 1;
//        }
//        for (int i = 0; i < sfIncrements.length; i++) {
//            scaleFactorIncr[i] = sfIncrements[i].ordinal();
//        }
//        //int[] scaleFactorBase = component.getScaleFactorsBase();
//        //int[] scaleFactorIncr = component.getScaleFactorsIncr();
//        double result = 0.0;
//
//        //cumulate the scale factor 
//        for (int i = 0; i < scaleFactorBase.length; i++) {
//            double increment = 0.0;
//            if (i == 0) {
//                double difference = 0.0;
//                switch (scaleFactorIncr[i]) {
//                    case 1:
//                        increment = INCR[1];
//                        break;
//                    case 2:
//                        increment = INCR[2];
//                        break;
//                    case 3:
//                        increment = INCR[3];
//                        break;
//                    default:
//                        increment = INCR[0];
//                        break;
//                }
//                switch (scaleFactorBase[i]) {
//                    case 1:
//                        result += PREC[0] + (increment * (PREC[1] - PREC[0]));
//                        break;
//                    case 2:
//                        result += PREC[1] + (increment * (PREC[2] - PREC[1]));
//                        break;
//                    case 3:
//                        result += PREC[2] + (increment * (PREC[3] - PREC[2]));
//                        break;
//                    case 4:
//                        result += PREC[3] + (increment * (PREC[4] - PREC[3]));
//                        break;
//                    case 5:
//                        result += PREC[4] + (increment * (PREC[5] - PREC[4]));
//                        break;
//                    default:
//                        result += PREC[5];
//                        break;
//                }
//            }
//            if (i == 1) {
//                switch (scaleFactorIncr[i]) {
//                    case 1:
//                        increment = INCR[1];
//                        break;
//                    case 2:
//                        increment = INCR[2];
//                        break;
//                    case 3:
//                        increment = INCR[3];
//                        break;
//                    default:
//                        increment = INCR[0];
//                        break;
//                }
//                switch (scaleFactorBase[i]) {
//                    case 1:
//                        result += FLEX[0] + (increment * (FLEX[1] - FLEX[0]));
//                        break;
//                    case 2:
//                        result += FLEX[1] + (increment * (FLEX[2] - FLEX[1]));
//                        break;
//                    case 3:
//                        result += FLEX[2] + (increment * (FLEX[3] - FLEX[2]));
//                        break;
//                    case 4:
//                        result += FLEX[3] + (increment * (FLEX[4] - FLEX[3]));
//                        break;
//                    case 5:
//                        result += FLEX[4] + (increment * (FLEX[5] - FLEX[4]));
//                        break;
//                    default:
//                        result += FLEX[5];
//                        break;
//                }
//            }
//            if (i == 2) {
//                switch (scaleFactorIncr[i]) {
//                    case 1:
//                        increment = INCR[1];
//                        break;
//                    case 2:
//                        increment = INCR[2];
//                        break;
//                    case 3:
//                        increment = INCR[3];
//                        break;
//                    default:
//                        increment = INCR[0];
//                        break;
//                }
//                switch (scaleFactorBase[i]) {
//                    case 1:
//                        result += RESL[0] + (increment * (RESL[1] - RESL[0]));
//                        break;
//                    case 2:
//                        result += RESL[1] + (increment * (RESL[2] - RESL[1]));
//                        break;
//                    case 3:
//                        result += RESL[2] + (increment * (RESL[3] - RESL[2]));
//                        break;
//                    case 4:
//                        result += RESL[3] + (increment * (RESL[4] - RESL[3]));
//                        break;
//                    case 5:
//                        result += RESL[4] + (increment * (RESL[5] - RESL[4]));
//                        break;
//                    default:
//                        result += RESL[5];
//                        break;
//                }
//            }
//            if (i == 3) {
//                switch (scaleFactorIncr[i]) {
//                    case 1:
//                        increment = INCR[1];
//                        break;
//                    case 2:
//                        increment = INCR[2];
//                        break;
//                    case 3:
//                        increment = INCR[3];
//                        break;
//                    default:
//                        increment = INCR[0];
//                        break;
//                }
//                switch (scaleFactorBase[i]) {
//                    case 1:
//                        result += TEAM[0] + (increment * (TEAM[1] - TEAM[0]));
//                        break;
//                    case 2:
//                        result += TEAM[1] + (increment * (TEAM[2] - TEAM[1]));
//                        break;
//                    case 3:
//                        result += TEAM[2] + (increment * (TEAM[3] - TEAM[2]));
//                        break;
//                    case 4:
//                        result += TEAM[3] + (increment * (TEAM[4] - TEAM[3]));
//                        break;
//                    case 5:
//                        result += TEAM[4] + (increment * (TEAM[5] - TEAM[4]));
//                        break;
//                    default:
//                        result += TEAM[5];
//                        break;
//                }
//            }
//            if (i == 4) {
//                switch (scaleFactorIncr[i]) {
//                    case 1:
//                        increment = INCR[1];
//                        break;
//                    case 2:
//                        increment = INCR[2];
//                        break;
//                    case 3:
//                        increment = INCR[3];
//                        break;
//                    default:
//                        increment = INCR[0];
//                        break;
//                }
//                switch (scaleFactorBase[i]) {
//                    case 1:
//                        result += PMAT[0] + (increment * (PMAT[1] - PMAT[0]));
//                        break;
//                    case 2:
//                        result += PMAT[1] + (increment * (PMAT[2] - PMAT[1]));
//                        break;
//                    case 3:
//                        result += PMAT[2] + (increment * (PMAT[3] - PMAT[2]));
//                        break;
//                    case 4:
//                        result += PMAT[3] + (increment * (PMAT[4] - PMAT[3]));
//                        break;
//                    case 5:
//                        result += PMAT[4] + (increment * (PMAT[5] - PMAT[4]));
//                        break;
//                    default:
//                        result += PMAT[5];
//                        break;
//                }
//            }
//        }
//        return (float) result;
//    }
//
//    public static double get_ExponentB(COINCOMOComponent component) {
//        COINCOMOLocalCalibration local = component.getLocalCalibration();
//        double exponentB = local.getB() + 0.01 * (double) get_Scale_Factor(component);
//        return exponentB;
//    }
//
//    public static double get_Mof64(COINCOMOComponent component) {
//        COINCOMOLocalCalibration local = component.getLocalCalibration();
//        double exponent_B = get_ExponentB(component);
//        double powerEquation = local.getD() + 0.2 * (exponent_B - 0.91);
//        double SCEDph = (double) get_Schedule_Percentage(component);
//        double schedule_PM_Effort64 = local.getC() * Math.pow(64, powerEquation) * SCEDph;
//        return schedule_PM_Effort64;
//    }
//
//    public static double get_MonthBaseSchedule(COINCOMOComponent component, double totEffort, double normEffort) {
//        COINCOMOLocalCalibration local = component.getLocalCalibration();
//
//        double monthBaseSchedule = 0.0;
//
//        if (totEffort < 16) {
//            monthBaseSchedule = Math.pow(normEffort, 0.5);
//        } else if ((16 <= totEffort) && (totEffort < 64)) {
//            double mOf64 = (double) get_Mof64(component);
//            monthBaseSchedule = (((mOf64 - 4) * normEffort) / 48) + 4 - ((16 * (mOf64 - 4)) / 48);
//        } else if (totEffort > 64) {
//            double powerEquation = local.getD() + 0.2 * (get_ExponentB(component) - 0.91);
//            monthBaseSchedule = local.getC() * Math.pow(totEffort, powerEquation) * get_Schedule_Percentage(component);
//        }
//
//        return monthBaseSchedule;
//    }
//
//    public static float get_Schedule(COINCOMOComponent component) {
//        //lookup table - SCED
//        double[] SCED = {1.43, 1.14, 1.00, 1.00, 1.00, 0.00};
//        double[] INCR = {0.00, 0.25, 0.50, 0.75};
//
//        int scedBase = component.getSCEDRating().ordinal() + 1;
//        int scedIncr = component.getSCEDIncrement().ordinal();
//        //int scedBase = component.getScedBase();
//        //int scedIncr = component.getScedIncr();
//        double result = 0.0;
//        double increment = 0.0;
//        double decrement = 0.0;
//        switch (scedIncr) {
//            case 1:
//                increment = INCR[1];
//                break;
//            case 2:
//                increment = INCR[2];
//                break;
//            case 3:
//                increment = INCR[3];
//                break;
//            default:
//                increment = INCR[0];
//                break;
//        }
//        switch (scedBase) {
//            case 1:
//                if (increment == INCR[1]) {
//                    decrement = 0.07;
//                } else if (increment == INCR[2]) {
//                    decrement = 0.15;
//                } else if (increment == INCR[3]) {
//                    decrement = 0.22;
//                }
//                //COINCOMOSubComponent.setRisk(3.1);
//                result = SCED[0] - decrement;
//                break;
//
//            case 2:
//                if (increment == INCR[1]) {
//                    decrement = 0.03;
//                } else if (increment == INCR[2]) {
//                    decrement = 0.07;
//                } else if (increment == INCR[3]) {
//                    decrement = 0.11;
//                }
//                result = SCED[1] - decrement;
//                break;
//
//            case 3:
//                result = SCED[2];
//                break;
//            case 4:
//                result = SCED[3];
//                break;
//            case 5:
//                result = SCED[4];
//                break;
//            default:
//                result = SCED[5];
//                break;
//        }
//        return (float) result;
//    }
//
//    public static float get_Schedule_Percentage(COINCOMOComponent component) {
//        double[] SCEDph = {0.75, 0.85, 1.00, 1.30, 1.60, 0.00};
//        double schedPercent = 0.0;
//        int scedBase = component.getSCEDRating().ordinal()+1;
//        //int scedBase = component.getScedBase();
//        switch (scedBase) {
//            case 1:
//                schedPercent = SCEDph[0];
//                break;
//            case 2:
//                schedPercent = SCEDph[1];
//                break;
//            case 3:
//                schedPercent = SCEDph[2];
//                break;
//            case 4:
//                schedPercent = SCEDph[3];
//                break;
//            case 5:
//                schedPercent = SCEDph[4];
//                break;
//            default:
//                schedPercent = SCEDph[5];
//                break;
//        }
//        return (float) schedPercent;
//    }
//
//    public static String get_All_Phase_Schedule(long componentID) {
//        throw new UnsupportedOperationException("Change for desktop app");
//        /*StringBuffer sql = new StringBuffer();
//
//         sql.append( "SELECT Get_COPSEMO_S_Phase(" );
//         sql.append( "" + componentID + ",1)," );
//         sql.append( "Get_COPSEMO_S_Phase(" );
//         sql.append( "" + componentID + ",2)," );
//         sql.append( "Get_COPSEMO_S_Phase(" );
//         sql.append( "" + componentID + ",3)," );
//         sql.append( "Get_COPSEMO_S_Phase(" );
//         sql.append( "" + componentID + ",4);" );
//
//         return sql.toString();*/
//    }
//
//    public static double get_Inception_PM(COINCOMOComponent component) {
//        double totalEffort = get_Total_Effort(component);
//        double inception = component.getInceptionEffortPercentage();
//
//        return (totalEffort * inception / 100d);
//    }
//
//    public static double get_Inception_M(COINCOMOComponent component) {
//        double totalEffort = (double) get_Total_Effort(component);
//        double pmNormal = totalEffort / (double) COINCOMOComponentManager.get_Schedule(component);
//        double monthBaseSchedule = get_MonthBaseSchedule(component, totalEffort, pmNormal);
//        double inceptionM = component.getInceptionSchedulePercentage();
//
//        return monthBaseSchedule * inceptionM / 100;
//    }
//
//    public static double get_Elaboration_PM(COINCOMOComponent component) {
//        double totalEffort = get_Total_Effort(component);
//        double elaboration = component.getElaborationEffortPercentage();
//
//        return (totalEffort * elaboration / 100d);
//    }
//
//    public static double get_Elaboration_M(COINCOMOComponent component) {
//        double totalEffort = (double) get_Total_Effort(component);
//        double pmNormal = totalEffort / (double) COINCOMOComponentManager.get_Schedule(component);
//        double monthBaseSchedule = get_MonthBaseSchedule(component, totalEffort, pmNormal);
//        double elaborationM = component.getElaborationSchedulePercentage();
//
//        return monthBaseSchedule * elaborationM / 100;
//    }
//
//    public static double get_Elaboration_P(COINCOMOComponent component) {
//        if (get_Elaboration_M(component) == 0) {
//            return 0;
//        }
//
//        return get_Elaboration_PM(component) / get_Elaboration_M(component);
//    }
//
//    public static double get_Construction_P(COINCOMOComponent component) {
//        if (get_Construction_M(component) == 0) {
//            return 0;
//        }
//        double retVal = get_Construction_PM(component) / get_Construction_M(component);
//        return retVal;
//    }
//
//    public static double get_P_Total_EC(COINCOMOComponent component) {
//        return get_Elaboration_P(component) + get_Construction_P(component);
//    }
//
//    public static double get_Construction_PM(COINCOMOComponent component) {
//        double totalEffort = get_Total_Effort(component);
//        double construction = component.getConstructionEffortPercentage();
//
//        return (totalEffort * construction / 100d);
//    }
//
//    public static double get_P_Total(COINCOMOComponent component) {
//        return get_Inception_P(component) + get_Elaboration_P(component) + get_Construction_P(component) + get_Transition_P(component);
//    }
//
//    public static double get_M_Total(COINCOMOComponent component) {
//        return get_Inception_M(component) + get_Elaboration_M(component) + get_Construction_M(component) + get_Transition_M(component);
//    }
//
//    public static double get_Transition_P(COINCOMOComponent component) {
//        if (get_Transition_M(component) == 0) {
//            return 0;
//        }
//        return get_Transition_PM(component) / get_Transition_M(component);
//    }
//
//    public static double get_Inception_P(COINCOMOComponent component) {
//        if (get_Inception_M(component) == 0) {
//            return 0;
//        }
//        return (get_Inception_PM(component) / get_Inception_M(component));
//    }
//
//    public static double get_Construction_M(COINCOMOComponent component) {
//        double totalEffort = get_Total_Effort(component);
//        double pmNormal = (totalEffort / COINCOMOComponentManager.get_Schedule(component));
//        double monthBaseSchedule = get_MonthBaseSchedule(component, totalEffort, pmNormal);
//        double constructionM = component.getConstructionSchedulePercentage();
//
//        return constructionM = (monthBaseSchedule * constructionM) / 100;
//    }
//
//    public static double get_Transition_PM(COINCOMOComponent component) {
//        double totalEffort = (double) get_Total_Effort(component);
//        double workHourScaleFactor;
//        double transition = component.getTransitionEffortPercentage();
//
//        return totalEffort * transition / 100;
//    }
//
//    public static double get_Transition_M(COINCOMOComponent component) {
//        double totalEffort = (double) get_Total_Effort(component);
//        double pmNormal = totalEffort / (double) COINCOMOComponentManager.get_Schedule(component);
//        double monthBaseSchedule = get_MonthBaseSchedule(component, totalEffort, pmNormal);
//        double transitionM = component.getTransitionSchedulePercentage();
//
//        return monthBaseSchedule * transitionM / 100;
//    }
//
//    public static float get_Total_Staff(COINCOMOComponent component) {
//        float sum = 0;
//        Iterator iter = component.getListOfSubUnits().iterator();
//
//        while (iter.hasNext()) {
//            COINCOMOSubComponent unit = (COINCOMOSubComponent) iter.next();
//            sum += COINCOMOSubComponentManager.get_Staff(unit);
//        }
//        return sum;
//    }
//
//    public static float get_Total_Schedule(COINCOMOComponent component) {
//        COINCOMOLocalCalibration local = component.getLocalCalibration();
//
//        // Total Schedule = TDEV 
//        // lookup table for SCED percentage for staff calculation 
//        double[] SCEDph = {0.75, 0.85, 1.00, 1.30, 1.60, 0.00};
//        double schedPercent = 0.0;
//
//        int scedBase = component.getSCEDRating().ordinal()+1;
//        //int scedBase = component.getScedBase();
//        switch (scedBase) {
//            case 1:
//                schedPercent = SCEDph[0];
//                break;
//            case 2:
//                schedPercent = SCEDph[1];
//                break;
//            case 3:
//                schedPercent = SCEDph[2];
//                break;
//            case 4:
//                schedPercent = SCEDph[3];
//                break;
//            case 5:
//                schedPercent = SCEDph[4];
//                break;
//            default:
//                schedPercent = SCEDph[5];
//                break;
//        }
//        double schedule = (double) COINCOMOComponentManager.get_Schedule(component);
//
//        // Calculate PM_aggregate 
//        Iterator iter = component.getListOfSubUnits().iterator();
//        double pmAggregate = 0.0;
//        while (iter.hasNext()) {
//            COINCOMOSubComponent subCom = (COINCOMOSubComponent) iter.next();
//            pmAggregate += (double) COINCOMOSubComponentManager.get_Estimated_Effort(subCom) / schedule;
//        }
//
//        // Calculate exponent
//        double scaleFactor = (double) COINCOMOComponentManager.get_Scale_Factor(component);
//        double exponentE = local.getD() + 0.2 * 0.01 * scaleFactor;
//        double TDEVns = local.getC() * Math.pow(pmAggregate, exponentE);
//        double TDEV = TDEVns * schedPercent;
//        return (float) TDEV;
//    }
//
//    public static float get_Optimistic_Total_Schedule(COINCOMOComponent component) {
//        COINCOMOLocalCalibration local = component.getLocalCalibration();
//
//        // Total Schedule = TDEV 
//        // lookup table for SCED percentage for staff calculation 
//        double[] SCEDph = {0.75, 0.85, 1.00, 1.30, 1.60, 0.00};
//        double schedPercent = 0.0;
//
//        int scedBase = component.getSCEDRating().ordinal()+1;
//        //int scedBase = component.getScedBase();
//        switch (scedBase) {
//            case 1:
//                schedPercent = SCEDph[0];
//                break;
//            case 2:
//                schedPercent = SCEDph[1];
//                break;
//            case 3:
//                schedPercent = SCEDph[2];
//                break;
//            case 4:
//                schedPercent = SCEDph[3];
//                break;
//            case 5:
//                schedPercent = SCEDph[4];
//                break;
//            default:
//                schedPercent = SCEDph[5];
//                break;
//        }
//        double schedule = (double) COINCOMOComponentManager.get_Schedule(component);
//
//        // Calculate PM_aggregate 
//        Iterator iter = component.getListOfSubUnits().iterator();
//        double pmAggregate = 0.0;
//        while (iter.hasNext()) {
//            COINCOMOSubComponent subCom = (COINCOMOSubComponent) iter.next();
//            pmAggregate += (double) (COINCOMOSubComponentManager.get_Estimated_Effort(subCom) / 1.25) / schedule;
//        }
//
//        // Calculate exponent
//        double scaleFactor = (double) COINCOMOComponentManager.get_Scale_Factor(component);
//        double exponentE = local.getD() + (0.2 * ((local.getB() + (0.01 * scaleFactor)) - local.getB()));
//        double TDEVns = local.getC() * Math.pow(pmAggregate, exponentE);
//        double TDEV = TDEVns * schedPercent;
//        return (float) TDEV;
//    }
//
//    public static float get_Pessimistic_Total_Schedule(COINCOMOComponent component) {
//        COINCOMOLocalCalibration local = component.getLocalCalibration();
//
//        // Total Schedule = TDEV 
//        // lookup table for SCED percentage for staff calculation 
//        double[] SCEDph = {0.75, 0.85, 1.00, 1.30, 1.60, 0.00};
//        double schedPercent = 0.0;
//
//        int scedBase = component.getSCEDRating().ordinal()+1;
//        //int scedBase = component.getScedBase();
//        switch (scedBase) {
//            case 1:
//                schedPercent = SCEDph[0];
//                break;
//            case 2:
//                schedPercent = SCEDph[1];
//                break;
//            case 3:
//                schedPercent = SCEDph[2];
//                break;
//            case 4:
//                schedPercent = SCEDph[3];
//                break;
//            case 5:
//                schedPercent = SCEDph[4];
//                break;
//            default:
//                schedPercent = SCEDph[5];
//                break;
//        }
//        double schedule = (double) COINCOMOComponentManager.get_Schedule(component);
//
//        // Calculate PM_aggregate 
//        Iterator iter = component.getListOfSubUnits().iterator();
//        double pmAggregate = 0.0;
//        while (iter.hasNext()) {
//            COINCOMOSubComponent subCom = (COINCOMOSubComponent) iter.next();
//            pmAggregate += (double) (COINCOMOSubComponentManager.get_Estimated_Effort(subCom) * 1.25) / schedule;
//        }
//
//        // Calculate exponent
//        double scaleFactor = (double) COINCOMOComponentManager.get_Scale_Factor(component);
//        double exponentE = local.getD() + (0.2 * ((local.getB() + (0.01 * scaleFactor)) - local.getB()));
//        double TDEVns = local.getC() * Math.pow(pmAggregate, exponentE);
//        double TDEV = TDEVns * schedPercent;
//        return (float) TDEV;
//    }

    public static COINCOMOComponent insertComponent(COINCOMOSubSystem subSystem) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;
        COINCOMOComponent component = null;
        StringBuilder defaultName = new StringBuilder(COINCOMOComponent.DEFAULT_NAME);

        // If exists
        if (subSystem != null) {
            defaultName.insert(defaultName.length()-1, subSystem.getNextAutoID());

            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Insert only when a connection is available
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Insert_Component(?, ?);";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setString(1, defaultName.toString());
                        preparedStatement.setLong(2, subSystem.getDatabaseID());

                        // Insert
                        ResultSet rs = preparedStatement.executeQuery();

                        if (rs.next()) {
                            long componentID = rs.getLong(1);

                            if (componentID < 0) {
                                log(Level.SEVERE, "SQL command\'" + sql + "\' failed.");
                                isSuccessful = false;
                            } else {
                                component = new COINCOMOComponent();
                                component.setName(defaultName.toString());
                                component.setDatabaseID(componentID);
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
                component = new COINCOMOComponent();
                component.setName(defaultName.toString());
            }

            // If the component unit is properly created, then add it to the sub-system unit.
            if (component != null) {
                subSystem.addSubUnit(component);
                subSystem.calculateNextAutoID(COINCOMOComponent.DEFAULT_NAME, component.getName());
            }
        }

        return component;
    }

    public static boolean updateComponent(COINCOMOComponent component, boolean recursive) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;

        // If exists
        if (component != null) {
            component.setSCED(calculateSCED(component));
            component.setSCEDPercent(calculateSCEDPercent(component));
            component.setSF(calculateSF(component));

            Iterator iter = component.getListOfSubUnits().iterator();
            while (iter.hasNext()) {
                final COINCOMOSubComponent subComponent = (COINCOMOSubComponent) iter.next();
                COINCOMOSubComponentManager.updateSubComponent(subComponent, false);
            }

            component.setSLOC(component.getSLOC());
            component.setCost(calculateCost(component));
            component.setStaff(calculateStaff(component));
            component.setEffort(calculateEffort(component));
            component.setSchedule(calculateSchedule(component, Scenario.MostLikely));

            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Update only when a connection is available
                if (connection != null) {
                    try {
                        /*
                         *** Component PART ***
                         */
                        String sql = "SELECT * from Update_Component("
                                // 13 parameters for component
                                + "  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
                                //  2 parameters for component EAF (2 parameters for each EAF rating/increment)
                                + ", ?::rating_enum, ?::increment_enum"
                                // 10 parameters for component SF (2 parameters for each SF rating/increment)
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ", ?::rating_enum, ?::increment_enum"
                                + ");";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);

                        // Replace "?" with respective values
                        int index = 0;
                        preparedStatement.setLong(++index, component.getDatabaseID());
                        preparedStatement.setString(++index, component.getName());
                        preparedStatement.setLong(++index, component.getParent().getDatabaseID());
                        preparedStatement.setLong(++index, component.getSLOC());
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getCost()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getStaff()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getEffort()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getSchedule()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getSF()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getSCED()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getSCEDPercent()));
                        preparedStatement.setInt(++index, component.getMultiBuildShift());
                        preparedStatement.setInt(++index, component.getRevision());
                        preparedStatement.setString(++index, component.getSCEDRating().toString());
                        preparedStatement.setString(++index, component.getSCEDIncrement().toString());
                        Rating[] sfRatings = component.getSFRatings();
                        Increment[] sfIncrements = component.getSFIncrements();
                        for (int i = 0; i < COINCOMOConstants.SFS.length; i++) {
                            preparedStatement.setString(++index, sfRatings[i].toString());
                            preparedStatement.setString(++index, sfIncrements[i].toString());
                        }
                        // Sanity check against parameter numbers
                        if (index != 25) {
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

                        /*
                         *** Parameters PART ***
                         */
                        sql = "SELECT * FROM Update_Component_Parameters("
                                //  1 parameter for component ID
                                + "  ?"
                                // 19 parameters for EAFs (each EAF is a composite value of weight vlo/lo/nom/hi/vhi/xhi)
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                //  5 parameters for SFs (each SF is a composite value of weight vlo/lo/nom/hi/vhi/xhi)
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                + ", ?::coincomo_cost_driver_weight_type"
                                // 15 parameters for FPs (3 parameters for each FP weight low/average/high)
                                + ", ?, ?, ?"
                                + ", ?, ?, ?"
                                + ", ?, ?, ?"
                                + ", ?, ?, ?"
                                + ", ?, ?, ?"
                                //  4 parameters for EQs
                                + ", ?, ?, ?, ?"
                                //  1 parameter for hours per PM
                                + ", ?"
                                //  1 parameter for revision number
                                + ", ?"
                                + ");";

                        preparedStatement = connection.prepareStatement(sql);

                        // Replace "?" with respective values
                        index = 0;
                        final COINCOMOComponentParameters parameters = component.getParameters();
                        final double[][] EAFWeights = parameters.getEAFWeights();
                        final double[][] SFWeights = parameters.getSFWeights();
                        final int[][] FPWeights = parameters.getFPWeights();
                        preparedStatement.setLong(++index, component.getDatabaseID());
                        for (int i = 0; i < COINCOMOConstants.EAFS.length; i++) {
                            String weight = "(";
                            weight += EAFWeights[i][0];
                            for (int j = 1; j < COINCOMOConstants.Ratings.length; j++) {
                                weight += ", " + EAFWeights[i][j];
                            }
                            weight += ")";
                            preparedStatement.setString(++index, weight);
                        }
                        for (int i = 0; i < COINCOMOConstants.SFS.length; i++) {
                            String weight = "(";
                            weight += SFWeights[i][0];
                            for (int j = 1; j < COINCOMOConstants.Ratings.length; j++) {
                                weight += ", " + SFWeights[i][j];
                            }
                            weight += ")";
                            preparedStatement.setString(++index, weight);
                        }
                        for (int i = 0; i < COINCOMOConstants.FPS.length; i++) {
                            for (int j = 0; j < COINCOMOConstants.FTS.length - 1; j++) {
                                preparedStatement.setInt(++index, FPWeights[i][j]);
                            }
                        }
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(parameters.getA()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(parameters.getB()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(parameters.getC()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(parameters.getD()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(parameters.getWorkHours()));
                        preparedStatement.setInt(++index, component.getRevision());
                        // Sanity check against parameter numbers
                        if (index != 46) {
                            log(Level.WARNING, "Wrong number of parameters are set for sql \'" + preparedStatement.toString() + "\'.");
                        }

                        // Update
                        rs = preparedStatement.executeQuery();

                        if (rs.next()) {
                            isSuccessful = isSuccessful & rs.getBoolean(1);
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
                updateComponentCOPSEMO(component);

                if (recursive) {
                    COINCOMOSubSystemManager.updateSubSystem((COINCOMOSubSystem) component.getParent(), recursive);
                }
            }
        }

        return isSuccessful;
    }

    public static boolean deleteComponent(COINCOMOComponent component) {
        // If exists
        if (component != null) {
            ArrayList<COINCOMOComponent> components = new ArrayList<COINCOMOComponent>();
            components.add(component);

            return deleteComponents(components);
        } else {
            return false;
        }
    }

    public static boolean deleteComponents(ArrayList<COINCOMOComponent> components) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;

        // If exists
        if (components != null && !components.isEmpty()) {
            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Delete only when a connection is available
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Delete_Component(?);";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);

                        for (int i = 0; i < components.size(); i++) {
                            COINCOMOComponent component = components.get(i);
                            preparedStatement.setLong(1, component.getDatabaseID());

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
                for (int i = 0; i < components.size(); i++) {
                    COINCOMOSubSystem subSystem = (COINCOMOSubSystem) components.get(i).getParent();
                    subSystem.removeSubUnit(components.get(i));
                }
            }
        }

        return isSuccessful;
    }

    public static boolean loadCOPSEMO(COINCOMOComponent component, DBConnection connection) {
        // Check if a Connection is available ..
        if (connection != null) {
            try {
                String sql = "SELECT * FROM Get_Component_COPSEMO(?);";

                // Efficient Query Statement
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                // Replace "?" With Respective Values ..
                preparedStatement.setLong(1, component.getDatabaseID());

                ResultSet rs = preparedStatement.executeQuery();

                // Loop through ..
                if (rs != null && rs.next()) {
                    int index = 0;

                    long componentID = rs.getLong(++index);
                    double inceptionEffortPercentage = rs.getDouble(++index);
                    double inceptionSchedulePercentage = rs.getDouble(++index);
                    double inceptionEffort = rs.getDouble(++index);
                    double inceptionMonth = rs.getDouble(++index);
                    double inceptionPersonnel = rs.getDouble(++index);
                    double elaborationEffortPercentage = rs.getDouble(++index);
                    double elaborationSchedulePercentage = rs.getDouble(++index);
                    double elaborationEffort = rs.getDouble(++index);
                    double elaborationMonth = rs.getDouble(++index);
                    double elaborationPersonnel = rs.getDouble(++index);
                    double constructionEffortPercentage = rs.getDouble(++index);
                    double constructionSchedulePercentage = rs.getDouble(++index);
                    double constructionEffort = rs.getDouble(++index);
                    double constructionMonth = rs.getDouble(++index);
                    double constructionPersonnel = rs.getDouble(++index);
                    double transitionEffortPercentage = rs.getDouble(++index);
                    double transitionSchedulePercentage = rs.getDouble(++index);
                    double transitionEffort = rs.getDouble(++index);
                    double transitionMonth = rs.getDouble(++index);
                    double transitionPersonnel = rs.getDouble(++index);
                    int revision = rs.getInt(++index);

                    component.setInceptionEffortPercentage(inceptionEffortPercentage);
                    component.setInceptionSchedulePercentage(inceptionSchedulePercentage);
                    component.setInceptionEffort(inceptionEffort);
                    component.setInceptionMonth(inceptionMonth);
                    component.setInceptionPersonnel(inceptionPersonnel);
                    component.setElaborationEffortPercentage(elaborationEffortPercentage);
                    component.setElaborationSchedulePercentage(elaborationSchedulePercentage);
                    component.setElaborationEffort(elaborationEffort);
                    component.setElaborationMonth(elaborationMonth);
                    component.setElaborationPersonnel(elaborationPersonnel);
                    component.setConstructionEffortPercentage(constructionEffortPercentage);
                    component.setConstructionSchedulePercentage(constructionSchedulePercentage);
                    component.setConstructionEffort(constructionEffort);
                    component.setConstructionMonth(constructionMonth);
                    component.setConstructionPersonnel(constructionPersonnel);
                    component.setTransitionEffortPercentage(transitionEffortPercentage);
                    component.setTransitionSchedulePercentage(transitionSchedulePercentage);
                    component.setTransitionEffort(transitionEffort);
                    component.setTransitionMonth(transitionMonth);
                    component.setTransitionPersonnel(transitionPersonnel);
                }

                // Release From Memory
                preparedStatement.close();
            } catch (SQLException e) {
                // Print any Problems ...
                e.printStackTrace();

                return false;
            }
        }

        return true;

    }

    public static boolean loadParameters(COINCOMOComponent component, DBConnection connection) {
        // Check if a Connection is available ..
        if (connection != null) {
            try {
                String sql = "SELECT * FROM Get_Component_Parameters(?);";

                // Efficient Query Statement
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                // Replace "?" With Respective Values ..
                preparedStatement.setLong(1, component.getDatabaseID());

                ResultSet rs = preparedStatement.executeQuery();

                // Loop through ..
                if (rs != null && rs.next()) {
                    int index = 0;

                    long componentID = rs.getLong(++index);
                    double[][] eafWeights = new double[COINCOMOConstants.EAFS.length][COINCOMOConstants.Ratings.length];
                    for (int i = 0; i < COINCOMOConstants.EAFS.length; i++) {
                        for (int j = 0; j < COINCOMOConstants.Ratings.length; j++) {
                            eafWeights[i][j] = rs.getDouble(++index);
                        }
                    }
                    double[][] sfWeights = new double[COINCOMOConstants.SFS.length][COINCOMOConstants.Ratings.length];
                    for (int i = 0; i < COINCOMOConstants.SFS.length; i++) {
                        for (int j = 0; j < COINCOMOConstants.Ratings.length; j++) {
                            sfWeights[i][j] = rs.getDouble(++index);
                        }
                    }
                    int[][] fpWeights = new int[COINCOMOConstants.FPS.length][COINCOMOConstants.FTS.length-1];
                    for (int i = 0; i < COINCOMOConstants.FPS.length; i++) {
                        for (int j = 0; j < COINCOMOConstants.FTS.length-1; j++) {
                            fpWeights[i][j] = rs.getInt(++index);
                        }
                    }
                    double a = rs.getDouble(++index);
                    double b = rs.getDouble(++index);
                    double c = rs.getDouble(++index);
                    double d = rs.getDouble(++index);
                    double workHours = rs.getDouble(++index);
                    int revision = rs.getInt(++index);

                    //Sanity check against the number of parameters read
                    if (index != 166) {
                        log(Level.WARNING, "Wrong number of parameters are set for sql \'" + preparedStatement.toString() + "\'.");
                    }

                    COINCOMOComponentParameters parameters = component.getParameters();
                    parameters.setEAFWeights(eafWeights);
                    parameters.setSFWeights(sfWeights);
                    parameters.setFPWeights(fpWeights);
                    parameters.setA(a);
                    parameters.setB(b);
                    parameters.setC(c);
                    parameters.setD(d);
                    parameters.setWorkHours(workHours);
                    parameters.setRevision(revision);

                    parameters.clearDirty();
                }

                // Release From Memory
                preparedStatement.close();
            } catch (SQLException e) {
                // Print any Problems ...
                e.printStackTrace();

                return false;
            }
        }

        return true;

    }

    public static boolean loadComponent(COINCOMOComponent component, DBConnection connection) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean isSuccessful = false;

        if (operationMode == OperationMode.DATABASE) {
            // Check if a Connection is available ..
            if (connection != null) {
                try {
                    String sql = "SELECT * FROM Get_AllSubComponents(?);";
                    int nextID = 0;

                    // Efficient Query Statement
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);

                    // Replace "?" With Respective Values ..
                    preparedStatement.setLong(1, component.getDatabaseID());

                    ResultSet rs = preparedStatement.executeQuery();

                    // Loop through ..
                    while (rs != null && rs.next()) {
                        int index = 0;

                        long subComponentID = rs.getLong(++index);
                        String name = rs.getString(++index);
                        long componentID = rs.getLong(++index);
                        long sloc = rs.getLong(++index);
                        double cost = rs.getDouble(++index);
                        double staff = rs.getDouble(++index);
                        double effort = rs.getDouble(++index);
                        double schedule = rs.getDouble(++index);
                        double productivity = rs.getDouble(++index);
                        double instructionCost = rs.getDouble(++index);
                        double risk = rs.getDouble(++index);
                        double nominalEffort = rs.getDouble(++index);
                        double estimatedEffort = rs.getDouble(++index);
                        double eaf = rs.getDouble(++index);
                        long sumfOfSLOCs = rs.getLong(++index);
                        double laborRate = rs.getDouble(++index);
                        double revl = rs.getDouble(++index);
                        String language = rs.getString(++index);

                        Rating eafRatings[] = new Rating[COINCOMOConstants.EAFS.length - 1];
                        Increment eafIncrements[] = new Increment[COINCOMOConstants.EAFS.length - 1];
                        for (int i = 0; i < COINCOMOConstants.EAFS.length - 1; i++) {
                            eafRatings[i] = Rating.valueOf(rs.getString(++index));
                            eafIncrements[i] = Increment.getValueOf(rs.getString(++index));
                        }

                        long newSLOC = rs.getLong(++index);

                        int multiplier = rs.getInt(++index);
                        RatioType ratioType = RatioType.valueOf(rs.getString(++index));
                        CalculationMethod calculationMethod = CalculationMethod.getValueOf(rs.getString(++index));
                        int[] internalLogicalFiles = new int[COINCOMOConstants.FTS.length];
                        int[] externalInterfaceFiles = new int[COINCOMOConstants.FTS.length];
                        int[] externalInputs = new int[COINCOMOConstants.FTS.length];
                        int[] externalOutputs = new int[COINCOMOConstants.FTS.length];
                        int[] externalInquiries = new int[COINCOMOConstants.FTS.length];
                        for (int i = 0; i < COINCOMOConstants.FTS.length; i++) {
                            internalLogicalFiles[i] = rs.getInt(++index);
                        }
                        for (int i = 0; i < COINCOMOConstants.FTS.length; i++) {
                            externalInterfaceFiles[i] = rs.getInt(++index);
                        }
                        for (int i = 0; i < COINCOMOConstants.FTS.length; i++) {
                            externalInputs[i] = rs.getInt(++index);
                        }
                        for (int i = 0; i < COINCOMOConstants.FTS.length; i++) {
                            externalOutputs[i] = rs.getInt(++index);
                        }
                        for (int i = 0; i < COINCOMOConstants.FTS.length; i++) {
                            externalInquiries[i] = rs.getInt(++index);
                        }
                        int totalUnadjustedFunctionPoints = rs.getInt(++index);
                        long equivalentSLOC = rs.getLong(++index);

                        // Create a sub component
                        COINCOMOSubComponent subComponent = new COINCOMOSubComponent();

                        // Set parameters
                        subComponent.setDatabaseID(subComponentID);
                        subComponent.setName(name);
                        subComponent.setSLOC(sloc);
                        subComponent.setCost(cost);
                        subComponent.setStaff(staff);
                        subComponent.setEffort(effort);
                        subComponent.setSchedule(schedule);
                        subComponent.setProductivity(productivity);
                        subComponent.setInstructionCost(instructionCost);
                        subComponent.setRisk(risk);//Changed by Roopa Dharap
                        subComponent.setNominalEffort(nominalEffort);
                        subComponent.setEstimatedEffort(estimatedEffort);
                        subComponent.setEAF(eaf);
                        subComponent.setSumOfSLOCs(sumfOfSLOCs);
                        subComponent.setLaborRate(laborRate);
                        subComponent.setREVL(revl);
                        subComponent.setLanguage(language);

                        subComponent.setEAFRatings(eafRatings);
                        subComponent.setEAFIncrements(eafIncrements);

                        subComponent.setNewSLOC(newSLOC);

                        subComponent.setMultiplier(multiplier);
                        subComponent.setRatioType(ratioType);
                        subComponent.setCalculationMethod(calculationMethod);
                        subComponent.setInternalLogicalFiles(internalLogicalFiles);
                        subComponent.setExternalInterfaceFiles(externalInterfaceFiles);
                        subComponent.setExternalInputs(externalInputs);
                        subComponent.setExternalOutputs(externalOutputs);
                        subComponent.setExternalInquiries(externalInquiries);
                        subComponent.setTotalUnadjustedFunctionPoints(totalUnadjustedFunctionPoints);
                        subComponent.setEquivalentSLOC(equivalentSLOC);

                        // Load sub components using the same connection for efficiency
                        COINCOMOSubComponentManager.loadSubComponent(subComponent, connection);

                        subComponent.clearDirty();

                        component.addSubUnit(subComponent);
                        component.calculateNextAutoID(COINCOMOSubComponent.DEFAULT_NAME, subComponent.getName());
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

    public static boolean updateComponentName(COINCOMOComponent component) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;

        // If exists
        if (component != null) {
            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Delete only when a connection is available
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Update_ComponentName(?, ?);";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);

                        preparedStatement.setLong(1, component.getDatabaseID());
                        preparedStatement.setString(2, component.getName());

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
        }

        // Determine the next auto-generated name will be
        if (isSuccessful) {
            COINCOMOSubSystem subSystem = (COINCOMOSubSystem) component.getParent();
            subSystem.calculateNextAutoID(COINCOMOComponent.DEFAULT_NAME, component.getName());
        }

        return isSuccessful;
    }

    public static boolean updateComponentCOPSEMO(COINCOMOComponent component) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;

        // If exists
        if (component != null) {
            final double pmBS = calculatePMBS(component);
            final double mBS = calculateMBS(component);

            // Inception
            final double inceptionEffortPercentage = component.getInceptionEffortPercentage();
            final double inceptionSchedulePercentage = component.getInceptionSchedulePercentage();
            component.setInceptionEffort(pmBS * inceptionEffortPercentage / 100.0d);
            component.setInceptionMonth(mBS * inceptionSchedulePercentage / 100.0d);
            double inceptionPersonnel = component.getInceptionEffort() / component.getInceptionMonth();
            if (Double.isNaN(inceptionPersonnel) || Double.isInfinite(inceptionPersonnel)) {
                inceptionPersonnel = 0.0d;
            }
            component.setInceptionPersonnel(inceptionPersonnel);
            // Elaboration
            final double elaborationEffortPercentage = component.getElaborationEffortPercentage();
            final double elaborationSchedulePercentage = component.getElaborationSchedulePercentage();
            component.setElaborationEffort(pmBS * elaborationEffortPercentage / 100.0d);
            component.setElaborationMonth(mBS * elaborationSchedulePercentage / 100.0d);
            double elaborationPersonnel = component.getElaborationEffort() / component.getElaborationMonth();
            if (Double.isNaN(elaborationPersonnel) || Double.isInfinite(elaborationPersonnel)) {
                elaborationPersonnel = 0.0d;
            }
            component.setElaborationPersonnel(elaborationPersonnel);
            // Construction
            final double constructionEffortPercentage = component.getConstructionEffortPercentage();
            final double constructionSchedulePercentage = component.getConstructionSchedulePercentage();
            component.setConstructionEffort(pmBS * constructionEffortPercentage / 100.0d);
            component.setConstructionMonth(mBS * constructionSchedulePercentage / 100.0d);
            double constructionPersonnel = component.getConstructionEffort() / component.getConstructionMonth();
            if (Double.isNaN(constructionPersonnel) || Double.isInfinite(constructionPersonnel)) {
                constructionPersonnel = 0.0d;
            }
            component.setConstructionPersonnel(constructionPersonnel);
            // Transition
            final double transitionEffortPercentage = component.getTransitionEffortPercentage();
            final double transitionSchedulePercentage = component.getTransitionSchedulePercentage();
            component.setTransitionEffort(pmBS * transitionEffortPercentage / 100.0d);
            component.setTransitionMonth(mBS * transitionSchedulePercentage / 100.0d);
            double transitionPersonnel = component.getTransitionEffort() / component.getTransitionMonth();
            if (Double.isNaN(transitionPersonnel) || Double.isInfinite(transitionPersonnel)) {
                transitionPersonnel = 0.0d;
            }
            component.setTransitionPersonnel(transitionPersonnel);

            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Delete only when a connection is available
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Update_Component_COPSEMO("
                                //  1 parameter for component ID
                                + "  ?"
                                // 20 parameters for COPSEMO (5 parameters per COPSEMO section)
                                + ", ?, ?, ?, ?, ?"
                                + ", ?, ?, ?, ?, ?"
                                + ", ?, ?, ?, ?, ?"
                                + ", ?, ?, ?, ?, ?"
                                //  1 parameter for revision number
                                + ", ?"
                                + ");";

                        PreparedStatement preparedStatement = connection.prepareStatement(sql);

                        // Replace "?" with respective values
                        int index = 0;
                        preparedStatement.setLong(++index, component.getDatabaseID());
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getInceptionEffortPercentage()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getInceptionSchedulePercentage()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getInceptionEffort()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getInceptionMonth()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getInceptionPersonnel()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getElaborationEffortPercentage()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getElaborationSchedulePercentage()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getElaborationEffort()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getElaborationMonth()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getElaborationPersonnel()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getConstructionEffortPercentage()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getConstructionSchedulePercentage()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getConstructionEffort()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getConstructionMonth()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getConstructionPersonnel()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getTransitionEffortPercentage()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getTransitionSchedulePercentage()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getTransitionEffort()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getTransitionMonth()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(component.getTransitionPersonnel()));
                        preparedStatement.setInt(++index, component.getRevision());
                        // Sanity check against parameter numbers
                        if (index != 22) {
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
        }

        return isSuccessful;
    }

    private static void log(Level level, String message) {
        Logger.getLogger(COINCOMOComponentManager.class.getName()).log(level, message);
    }
}
