/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package database;

import core.COINCOMOComponent;
import core.COINCOMOComponentParameters;
import core.COINCOMOConstants;

/**
 *
 * @author Hyunjoon Jo
 */
public class COINCOMOManager {

    //TODO (Larry) Need to get rid of all the legacy codes after verification and validation of the new calculation functions
//    public static double get_Cost(COINCOMOUnit unit) {
//        double cost = 0;
//
//        if (unit instanceof COINCOMOSubComponent) {
//            COINCOMOSubComponent sc = (COINCOMOSubComponent) unit;
//            return (COINCOMOSubComponentManager.calculateEffort((sc)) * sc.getLaborRate());
//        } else {
//            Iterator<COINCOMOUnit> it = unit.getListOfSubUnits().iterator();
//            while (it.hasNext()) {
//                COINCOMOUnit subunit = (COINCOMOUnit) it.next();
//                cost += get_Cost(subunit);
//            }
//        }
//        return cost;
//    }
//
//    public static double get_Effort(COINCOMOUnit unit) {
//        double effort = 0;
//
//        if (unit instanceof COINCOMOComponent) {
//            COINCOMOComponent sc = (COINCOMOComponent) unit;
//            return (COINCOMOComponentManager.calculateEffort(sc));
//        } else {
//            Iterator<COINCOMOUnit> it = unit.getListOfSubUnits().iterator();
//            while (it.hasNext()) {
//                COINCOMOUnit subunit = (COINCOMOUnit) it.next();
//                effort += get_Effort(subunit);
//            }
//        }
//        return effort;
//    }
//
//    public static double get_Schedule(COINCOMOUnit unit) {
//        double schedule = 0;
//
//        if (unit instanceof COINCOMOComponent) {
//            COINCOMOComponent sc = (COINCOMOComponent) unit;
//            return (COINCOMOComponentManager.calculateEffort(sc));
//        } else {
//            Iterator<COINCOMOUnit> it = unit.getListOfSubUnits().iterator();
//            while (it.hasNext()) {
//                COINCOMOUnit subunit = (COINCOMOUnit) it.next();
//                schedule += get_Schedule(subunit);
//            }
//        }
//        return schedule;
//    }
//
//    public static float get_SystemOverview_Staff(COINCOMOUnit unit) {
//        float sum = 0;
//        if (unit instanceof COINCOMOSubComponent) {
//            COINCOMOSubComponent sc = (COINCOMOSubComponent) unit;
//            sum += COINCOMOSubComponentManager.get_Staff(sc);
//            return sum;
//        } else {
//            Iterator iter = unit.getListOfSubUnits().iterator();
//
//            while (iter.hasNext()) {
//                COINCOMOUnit sc = (COINCOMOUnit) iter.next();
//                sum += get_SystemOverview_Staff(sc);
//            }
//        }
//
//        return sum;
//    }
//
//    public static long get_Staff(COINCOMOUnit unit) {
//        long staff = 0;
//
//        if (unit instanceof COINCOMOSubComponent) {
//            COINCOMOSubComponent subComponent = (COINCOMOSubComponent) unit;
//            COINCOMOComponent component = (COINCOMOComponent) unit.getParent();
//            COINCOMOLocalCalibration local = component.getLocalCalibration();
//            
//            double[] SCEDph = {0.75, 0.85, 1.00, 1.30, 1.60, 0.00};
//            double compExpansion = 0.00;
//            int scedBase = component.getSCEDRating().ordinal();
//            //int scedBase = component.getScedBase();
//            switch (scedBase) {
//                case 1:
//                    compExpansion = SCEDph[0];
//                    break;
//                case 2:
//                    compExpansion = SCEDph[1];
//                    break;
//                case 3:
//                    compExpansion = SCEDph[2];
//                    break;
//                case 4:
//                    compExpansion = SCEDph[3];
//                    break;
//                case 5:
//                    compExpansion = SCEDph[4];
//                    break;
//                default:
//                    compExpansion = SCEDph[5];
//                    break;
//            }
//
//            double estEffort = COINCOMOSubComponentManager.calculateEffort(subComponent);
//            double exponent1 = (0.01 * COINCOMOComponentManager.calculateSF(component));
//            double exponent2 = (local.getD() + 0.2 * exponent1);
//            double EM1to16 = COINCOMOSubComponentManager.calculateEAFWithoutSCED(subComponent);
//            double PM_ns = (COINCOMOSubComponentManager.calculatePMNominal(subComponent) * EM1to16);
//            double PM_ns_exp = Math.pow(PM_ns, exponent2);
//            double TDEVns = (local.getC() * PM_ns_exp);
//            double TDEV = (TDEVns * compExpansion);
//            staff += estEffort / TDEV;
//        }
//
//        Iterator<COINCOMOUnit> it = unit.getListOfSubUnits().iterator();
//        while (it.hasNext()) {
//            COINCOMOUnit subunit = (COINCOMOUnit) it.next();
//            staff += get_Staff(subunit);
//        }
//        return staff;
//    }
//
//    public static long get_Final_SLOC(COINCOMOUnit unit) {
//        float sum = 0;
//        float newSLOC = 0;
//        float equivalentSLOC = 0;
//        float adaptiveSLOC = 0;
//
//        if (unit instanceof COINCOMOSubComponent) {
//            COINCOMOSubComponent sc = (COINCOMOSubComponent) unit;
//            //TO DO:Changed by Abhishek
//            //float revl = 1 + sc.getBreakage() / 100;
//            //newSLOC = (float) sc.getNewSLOC();
//            //equivalentSLOC = (float) COINCOMOSubComponentManager.get_Equivalent_SLOC(sc);
//            //adaptiveSLOC = COINCOMOSubComponentManager.get_Adapted_SLOC(sc);
//            //TO DO:Changed by Abhishek
//            //sum += revl * (newSLOC + equivalentSLOC) + adaptiveSLOC;
//            sum += COINCOMOSubComponentManager.calculateSumOfSLOCs(sc);
//        } else {
//            Iterator<COINCOMOUnit> it = unit.getListOfSubUnits().iterator();
//            while (it.hasNext()) {
//                COINCOMOUnit subunit = (COINCOMOUnit) it.next();
//                sum += get_Final_SLOC(subunit);
//            }
//        }
//        return (long) sum;
//    }
//
//    public static long get_Final_SLOC_With_REVL(COINCOMOUnit unit) {
//        long sum = 0;
//        long newSLOC = 0;
//        long equivalentSLOC = 0;
//        long adaptedSLOC = 0;
//
//        if (unit instanceof COINCOMOSubComponent) {
//            COINCOMOSubComponent sc = (COINCOMOSubComponent) unit;
//            //double revl = 1 + sc.getREVL() / 100;
//            //newSLOC = sc.getNewSLOC();
//            //equivalentSLOC = COINCOMOSubComponentManager.get_Equivalent_SLOC(sc);
//            //adaptedSLOC = COINCOMOSubComponentManager.get_Adapted_SLOC(sc);
//            sum = COINCOMOSubComponentManager.calculateSLOC(sc);
//        } else {
//            Iterator<COINCOMOUnit> it = unit.getListOfSubUnits().iterator();
//            while (it.hasNext()) {
//                COINCOMOUnit subunit = (COINCOMOUnit) it.next();
//                sum += get_Final_SLOC_With_REVL(subunit);
//            }
//        }
//        return sum;
//    }

    //TODO (Larry) Need to come back and deal with Calibrat menu and the associated functions
    public static void get_Local_Calibration(COINCOMOComponent component) {
        COINCOMOComponentParameters local = component.getParameters();
        local.setA(get_CalibrationA(component));
        local.setC(get_CalibrationC(component));
        /*
        if (local.getCalibrationMode() == LocalCalibrationMode.COEFFICIENTS_AND_EXPONENTS) {
            local.setB(get_CalibrationB(component));
            local.setD(get_CalibrationD(component));
        }
        */
    }

    public static double get_CalibrationA(COINCOMOComponent component) {
        /* 
         * DUE TO MOVING Parameters from System level to Component level, this function no longer works.
        double squaredEffort = 0.0;
        double meanSquaredEffort = 0.0;
        double estimatedEffort = 0.0;
        double oldA = system.getLocalCalibration().getA();
        int number = 0;


        // TO DO - how to navigate secifically the current component when having multiple of everything?
        // Get the estimated effort 
        Iterator<COINCOMOUnit> iterSubSys = system.getListOfSubUnits().iterator();
        while (iterSubSys.hasNext()) {
            COINCOMOSubSystem subSys = (COINCOMOSubSystem) iterSubSys.next();

            Iterator<COINCOMOUnit> iterComp = subSys.getListOfSubUnits().iterator();
            while (iterComp.hasNext()) {
                COINCOMOComponent component = (COINCOMOComponent) iterComp.next();

                estimatedEffort = COINCOMOComponentManager.get_Total_Effort(component);
            }
        }

        // Calculate mean square of effort 
        Iterator<COINCOMOUnit> iterPro = system.getLocalCalibration().getListOfSubUnits().iterator();
        while (iterPro.hasNext()) {
            COINCOMOLocalCalibrationProject project = (COINCOMOLocalCalibrationProject) iterPro.next();
            if (project.isSelected()) {
                squaredEffort = project.getEffort() / estimatedEffort * oldA;
                meanSquaredEffort += Math.pow(squaredEffort, 2.0);
                number++;
            }
        }
        meanSquaredEffort = Math.pow((meanSquaredEffort / number), 0.5);

        return meanSquaredEffort;
        */
        return 0.0;
    }

    public static double get_CalibrationB(COINCOMOComponent component) {
        /* 
         * DUE TO MOVING Parameters from System level to Component level, this function no longer works.
        double newEffortln = 0.0;
        int counter = 0;
        double beta0 = system.getLocalCalibration().getA();

        Iterator<COINCOMOUnit> iterPro = system.getLocalCalibration().getListOfSubUnits().iterator();
        while (iterPro.hasNext()) {
            COINCOMOLocalCalibrationProject project = (COINCOMOLocalCalibrationProject) iterPro.next();
            if (project.isSelected()) {
                newEffortln += Math.log(project.getEffort());
                counter++;
            }
        }
        double size = (double) get_Final_SLOC(system) / 1000.0;      //totla SLOC 
        double scaleFactor = 0.0;
        double eafSCED = 1.0;

        Iterator<COINCOMOUnit> iterSub = system.getListOfSubUnits().iterator();
        while (iterSub.hasNext()) {
            COINCOMOSubSystem subSys = (COINCOMOSubSystem) iterSub.next();

            Iterator<COINCOMOUnit> iterComp = subSys.getListOfSubUnits().iterator();
            while (iterComp.hasNext()) {
                COINCOMOComponent component = (COINCOMOComponent) iterComp.next();
                scaleFactor = (double) COINCOMOComponentManager.get_Scale_Factor(component);

                Iterator<COINCOMOUnit> iterSubcomp = component.getListOfSubUnits().iterator();
                while (iterSubcomp.hasNext()) {
                    COINCOMOSubComponent subComponent = (COINCOMOSubComponent) iterSubcomp.next();
                    eafSCED *= (double) COINCOMOSubComponentManager.get_EAF_SCHED(subComponent);
                }
            }
        }

        double calB = (newEffortln / (double) counter) - 0.01 * Math.log(size) * scaleFactor;
        calB -= Math.log(eafSCED);
        calB -= beta0;
        calB /= Math.log(size);

        return calB;
        */
        return 0.0;
    }

    public static double get_CalibrationC(COINCOMOComponent component) {
        /* 
         * DUE TO MOVING Parameters from System level to Component level, this function no longer works.
        double squaredSched = 0.0;
        double meanSquaredSched = 0.0;
        double estimatedSched = 0.0;
        double SCED = 0.0;
        double SCEDph = 0.0;
        double Scalefactor = 0.0;
        double oldD = system.getLocalCalibration().getD();
        int number = 0;


        // TO DO - how to navigate secifically the current component when having multiple of everything?
        // Get the estimated schedule
        Iterator<COINCOMOUnit> iterSubSys = system.getListOfSubUnits().iterator();
        while (iterSubSys.hasNext()) {
            COINCOMOSubSystem subSys = (COINCOMOSubSystem) iterSubSys.next();

            Iterator<COINCOMOUnit> iterComp = subSys.getListOfSubUnits().iterator();
            while (iterComp.hasNext()) {
                COINCOMOComponent component = (COINCOMOComponent) iterComp.next();

                estimatedSched = COINCOMOComponentManager.get_Total_Schedule(component);
                SCED = COINCOMOComponentManager.get_Schedule(component);
                SCEDph = COINCOMOComponentManager.get_Schedule_Percentage(component);
                Scalefactor = COINCOMOComponentManager.get_Scale_Factor(component);
            }
        }

        // Calculate mean square of schedule
        double exponent = oldD + 0.2 * 0.01 * Scalefactor;
        Iterator<COINCOMOUnit> iterPro = system.getLocalCalibration().getListOfSubUnits().iterator();
        while (iterPro.hasNext()) {
            COINCOMOLocalCalibrationProject project = (COINCOMOLocalCalibrationProject) iterPro.next();
            if (project.isSelected()) {
                double newEffort = Math.pow((project.getEffort() / SCED), exponent);

                // TO DO - determine which schedule(old or new) to use
                squaredSched = estimatedSched / (newEffort * SCEDph); //old
                //squaredSched = project.getSchedule() / (newEffort * SCEDph); //new

                meanSquaredSched += Math.pow(squaredSched, 2.0);
                number++;
            }
        }
        meanSquaredSched = Math.pow((meanSquaredSched / number), 0.5);

        return meanSquaredSched;
        */
        return 0.0;
    }

    public static double get_CalibrationD(COINCOMOComponent component) {
        /* 
         * DUE TO MOVING Parameters from System level to Component level, this function no longer works.
        COINCOMOLocalCalibration local = system.getLocalCalibration();
        return local.getD();
        */
        return 0.0;
    }
    /* old version 
     public static long get_SLOC(COINCOMOSubComponent subComponent) {
     float REVL = subComponent.getBreakage();
     return (long) ((1 + REVL / 100) * (float) subComponent.getNewSLOC());
     }
     * 
     */

    /* old version 
     public static double get_CalibrationA(COINCOMOSystem system) {       
     double newEffortln = 0.0;
     int counter = 0;
     double beta0 = 0.0;
     Iterator<COINCOMOUnit> iterPro = system.getLocalCalibration().getListOfSubUnits().values().iterator();
     while (iterPro.hasNext()) {
     COINCOMOLocalCalibrationProject project = (COINCOMOLocalCalibrationProject) iterPro.next();
     if (project.isSelected()) {
     newEffortln += Math.log(project.getEffort());
     counter++;
     }                
     }
     double exponent = system.getLocalCalibration().getB();        //normally 0.91 
     double size = (double) get_Final_SLOC(system) / 1000.0;       //totla SLOC 
     double scaleFactor = 0.0;
     double eafSCED = 1.0;
        
     Iterator<COINCOMOUnit> iterSub = system.getListOfSubUnits().values().iterator();
     while (iterSub.hasNext()) {            
     COINCOMOSubSystem subSys = (COINCOMOSubSystem) iterSub.next();
            
     Iterator<COINCOMOUnit> iterComp = subSys.getListOfSubUnits().values().iterator();        
     while (iterComp.hasNext()) {            
     COINCOMOComponent component = (COINCOMOComponent) iterComp.next();
     scaleFactor = (double) COINCOMOComponentManager.get_Scale_Factor(component);

     Iterator<COINCOMOUnit> iterSubcomp = component.getListOfSubUnits().values().iterator();
     while (iterSubcomp.hasNext()) {            
     COINCOMOSubComponent subComponent = (COINCOMOSubComponent) iterSubcomp.next();
     eafSCED *= (double) COINCOMOSubComponentManager.get_EAF_SCHED(subComponent);
     }
     }           
     }
	  
     beta0 = (newEffortln / (double) counter) - exponent * Math.log(size);
     beta0 -= 0.01 * Math.log(size) * scaleFactor;
     beta0 -= Math.log(eafSCED);
	  
     double calA = Math.pow(Math.E, beta0);		//final result
     return calA;
     }
     
     public static double get_CalibrationC(COINCOMOSystem system) {       
     double newEffort = 0.0;
     double newSchedule = 0.0;
     int counter = 0;
     Iterator<COINCOMOUnit> iterPro = system.getLocalCalibration().getListOfSubUnits().values().iterator();
     while (iterPro.hasNext()) {
     COINCOMOLocalCalibrationProject project = (COINCOMOLocalCalibrationProject) iterPro.next();
     if (project.isSelected()) {
     newEffort += project.getEffort();
     newSchedule += project.getSchedule();
     counter++;
     }
     }
     double scaleFactor = 0.0;
     double schedule = 0.0;
     double SCEDph = 0.0;
        
     Iterator<COINCOMOUnit> iterSub = system.getListOfSubUnits().values().iterator();
     while (iterSub.hasNext()) {            
     COINCOMOSubSystem subSys = (COINCOMOSubSystem) iterSub.next();
            
     Iterator<COINCOMOUnit> iterComp = subSys.getListOfSubUnits().values().iterator();        
     while (iterComp.hasNext()) {            
     COINCOMOComponent component = (COINCOMOComponent) iterComp.next();
     scaleFactor = (double) COINCOMOComponentManager.get_Scale_Factor(component);
     schedule = (double) COINCOMOComponentManager.get_Schedule(component);
     SCEDph = (double) COINCOMOComponentManager.get_Schedule_Percentage(component); 
     }           
     }
	  	  
     //double calD = get_CalibrationD(system);
     double calD = system.getLocalCalibration().getD();
     double effortExponent = Math.pow((newEffort / schedule), (double) (calD + 0.2 * 0.01 * scaleFactor));
     double calC = newSchedule / (effortExponent * SCEDph);
        
     return calC;
     }
     */
}
