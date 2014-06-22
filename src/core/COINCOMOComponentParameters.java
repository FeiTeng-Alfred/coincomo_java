/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package core;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Justin
 */
public class COINCOMOComponentParameters extends COINCOMOUnit {

    public static final double EAF_WEIGHTS[][] = {
        {0.82, 0.92, 1.00, 1.10, 1.26, 0.00},
        {0.00, 0.90, 1.00, 1.14, 1.28, 0.00},
        {0.81, 0.91, 1.00, 1.11, 1.23, 0.00},
        {0.73, 0.87, 1.00, 1.17, 1.34, 1.74},
        {0.00, 0.95, 1.00, 1.07, 1.15, 1.24},
        {0.00, 0.00, 1.00, 1.11, 1.29, 1.63},
        {0.00, 0.00, 1.00, 1.05, 1.17, 1.46},
        {0.00, 0.87, 1.00, 1.15, 1.30, 0.00},
        {1.42, 1.19, 1.00, 0.85, 0.71, 0.00},
        {1.22, 1.10, 1.00, 0.88, 0.81, 0.00},
        {1.34, 1.15, 1.00, 0.88, 0.76, 0.00},
        {1.19, 1.09, 1.00, 0.91, 0.85, 0.00},
        {1.20, 1.09, 1.00, 0.91, 0.84, 0.00},
        {1.29, 1.12, 1.00, 0.90, 0.81, 0.00},
        {1.17, 1.09, 1.00, 0.90, 0.78, 0.00},
        {1.22, 1.09, 1.00, 0.93, 0.86, 0.80},
        {1.00, 1.00, 1.00, 1.00, 1.00, 1.00},
        {1.00, 1.00, 1.00, 1.00, 1.00, 1.00},
        {1.43, 1.14, 1.00, 1.00, 1.00, 0.00}
    };
    public static final double SF_WEIGHTS[][] = {
        {6.20, 4.96, 3.72, 2.48, 1.24, 0.00},
        {5.07, 4.05, 3.04, 2.03, 1.01, 0.00},
        {7.07, 5.65, 4.24, 2.83, 1.41, 0.00},
        {5.48, 4.38, 3.29, 2.19, 1.10, 0.00},
        {7.80, 6.24, 4.68, 3.12, 1.56, 0.00}
    };
    public static final int FP_WEIGHTS[][] = {
        {7, 10, 15},
        {5, 7, 10},
        {3, 4, 6},
        {4, 5, 7},
        {3, 4, 6}
    };
    public static final double A = 2.94d;
    public static final double B = 0.91d;
    public static final double C = 3.67d;
    public static final double D = 0.28d;
    public static final double WORK_HOURS = 152.0d;

    //Effort Adjustment Factors
    private double eafWeights[][] = new double[COINCOMOConstants.EAFS.length][COINCOMOConstants.Ratings.length];
    //Scale Factors
    private double sfWeights[][] = new double[COINCOMOConstants.SFS.length][COINCOMOConstants.Ratings.length];
    //Function Points
    private int fpWeights[][] = new int[COINCOMOConstants.FPS.length][COINCOMOConstants.FTS.length-1];
    //Equation Editors
    private double a = A;
    private double b = B;
    private double c = C;
    private double d = D;
    //Person Month
    private double workHours = WORK_HOURS;
    //Revision
    private int revision = 1;

    private COINCOMOComponent component = null;

    public COINCOMOComponentParameters(COINCOMOComponent component) {
        this.component = component;

        for (int i = 0; i < COINCOMOConstants.EAFS.length; i++) {
            System.arraycopy(EAF_WEIGHTS[i], 0, eafWeights[i], 0, eafWeights[i].length);
        }
        for (int i = 0; i < COINCOMOConstants.SFS.length; i++) {
            System.arraycopy(SF_WEIGHTS[i], 0, sfWeights[i], 0, sfWeights[i].length);
        }
        for (int i = 0; i < COINCOMOConstants.FPS.length; i++) {
            System.arraycopy(FP_WEIGHTS[i], 0, fpWeights[i], 0, fpWeights[i].length);
        }
    }

    @Override
    public COINCOMOUnit getParent() {
        return (COINCOMOUnit) this.component;
    }

    public double[][] getEAFWeights() {
        return this.eafWeights;
    }

    public void setEAFWeights(double[][] eafWeights) {
        boolean isDirty = false;

        if (this.eafWeights.length != eafWeights.length) {
            log(Level.SEVERE, "setEAFRatings() has internal variable of length=" + this.eafWeights.length + " compared to parameter variable of length=" + eafWeights.length);
            return;
        } else {
            for (int i = 0; i < COINCOMOConstants.EAFS.length; i++) {
                for (int j = 0; j < COINCOMOConstants.Ratings.length; j++) {
                    if (this.eafWeights[i][j] != eafWeights[i][j]) {
                        isDirty = true;
                        break;
                    }
                }
                if (isDirty) {
                    break;
                }
            }
        }

        if (!isDirty) {
            return;
        }
        for (int i = 0; i < COINCOMOConstants.EAFS.length; i++) {
            System.arraycopy(eafWeights[i], 0, this.eafWeights[i], 0, this.eafWeights[i].length);
        }
        this.setDirty();
    }

    public double[][] getSFWeights() {
        return this.sfWeights;
    }

    public void setSFWeights(double[][] sfWeights) {
        boolean isDirty = false;

        if (this.sfWeights.length != sfWeights.length) {
            log(Level.SEVERE, "setSFWeights() has internal variable of length=" + this.sfWeights.length + " compared to parameter variable of length=" + sfWeights.length);
            return;
        } else {
            for (int i = 0; i < COINCOMOConstants.SFS.length; i++) {
                for (int j = 0; j < COINCOMOConstants.Ratings.length; j++) {
                    if (this.sfWeights[i][j] != sfWeights[i][j]) {
                        isDirty = true;
                        break;
                    }
                }
                if (isDirty) {
                    break;
                }
            }
        }

        if (!isDirty) {
            return;
        }
        for (int i = 0; i < COINCOMOConstants.SFS.length; i++) {
            System.arraycopy(sfWeights[i], 0, this.sfWeights[i], 0, this.sfWeights[i].length);
        }
        this.setDirty();
    }

    public int[][] getFPWeights() {
        return this.fpWeights;
    }

    public void setFPWeights(int[][] fpWeights) {
        boolean isDirty = false;

        if (this.fpWeights.length != fpWeights.length) {
            log(Level.SEVERE, "setFPWeights() has internal variable of length=" + this.fpWeights.length + " compared to parameter variable of length=" + fpWeights.length);
            return;
        } else {
            for (int i = 0; i < COINCOMOConstants.FPS.length; i++) {
                for (int j = 0; j < COINCOMOConstants.FTS.length-1; j++) {
                    if (this.fpWeights[i][j] != fpWeights[i][j]) {
                        isDirty = true;
                        break;
                    }
                }
                if (isDirty) {
                    break;
                }
            }
        }

        if (!isDirty) {
            return;
        }
        for (int i = 0; i < COINCOMOConstants.FPS.length; i++) {
            System.arraycopy(fpWeights[i], 0, this.fpWeights[i], 0, this.fpWeights[i].length);
        }
        this.setDirty();
    }

    //TODO (Larry) Can we remove the lagecy functions for COINCOMOComponentParameters?
    /* START OF LEGACY FUNCTIONS */
    public void setProductValue(int row, int column, double value) {
        if (eafWeights[row][column] == value) {
            return;
        }
        eafWeights[row][column] = value;
        this.setDirty();
    }

    public double getProductValue(int row, int column) {
        return eafWeights[row][column];
    }

    public void setPlatformValue(int row, int column, double value) {
        if (eafWeights[row+5][column] == value) {
            return;
        }
        eafWeights[row+5][column] = value;
        this.setDirty();
    }

    public double getPlatformValue(int row, int column) {
        return eafWeights[row+5][column];
    }

    public void setPersonnelValue(int row, int column, double value) {
        if (eafWeights[row+8][column] == value) {
            return;
        }
        eafWeights[row+8][column] = value;
        setDirty();
    }

    public double getPersonnelValue(int row, int column) {
        return eafWeights[row+8][column];
    }

    public void setProjectValue(int row, int column, double value) {
        // Index hacking to deal with SCED moving to the end of EAF list
        int k = row;
        if (row == 2) {
            k = 4;
        }

        if (eafWeights[k+14][column] == value) {
            return;
        }
        eafWeights[k+14][column] = value;
        setDirty();
    }

    public double getProjectValue(int row, int column) {
        // Index hacking to deal with SCED moving to the end of EAF list
        int k = row;
        if (row == 2) {
            k = 4;
        }

        return eafWeights[k+14][column];
    }

    public void setUserDefinedValue(int row, int column, double value) {
        if (eafWeights[row+16][column] == value) {
            return;
        }
        eafWeights[row+16][column] = value;
        setDirty();
    }

    public double getUserDefinedValue(int row, int column) {
        return eafWeights[row+16][column];
    }

    public void setScheduleValue(int row, int column, double value) {
        if (eafWeights[row+18][column] == value) {
            return;
        }
        eafWeights[row+18][column] = value;
        setDirty();
    }

    public double getScheduleValue(int row, int column) {
        return eafWeights[row+18][column];
    }

    public void setScaleFactorsValue(int row, int column, double value) {
        if (sfWeights[row][column] == value) {
            return;
        }
        sfWeights[row][column] = value;
        setDirty();
    }

    public double getScaleFactorsValue(int row, int column) {
        return sfWeights[row][column];
    }

    public void setFunctionPointsValue(int row, int column, int value) {
        if (fpWeights[row][column] == value) {
            return;
        }
        fpWeights[row][column] = value;
        setDirty();
    }

    public int getFunctionPointsValue(int row, int column) {
        return fpWeights[row][column];
    }
    /* END OF LEGACY FUNCTIONS */

    public double getA() {
        return this.a;
    }

    public void setA(double a) {
        if (this.a == a) {
            return;
        }
        this.a = a;
        this.setDirty();
    }

    public double getB() {
        return this.b;
    }

    public void setB(double b) {
        if (this.b == b) {
            return;
        }
        this.b = b;
        this.setDirty();
    }

    public double getC() {
        return this.c;
    }

    public void setC(double c) {
        if (this.c == c) {
            return;
        }
        this.c = c;
        this.setDirty();
    }

    public double getD() {
        return this.d;
    }

    public void setD(double d) {
        if (this.d == d) {
            return;
        }
        this.d = d;
        this.setDirty();
    }

    public double getWorkHours() {
        return this.workHours;
    }

    public void setWorkHours(double workHours) {
        if (this.workHours == workHours) {
            return;
        }
        this.workHours = workHours;
        this.setDirty();
    }

    public int getRevision() {
        return this.revision;
    }

    public void setRevision(int revision) {
        if (this.revision == revision) {
            return;
        }
        this.revision = revision;
        this.setDirty();
    }

    /* Functionality to provide backward compatibility with older XML format with parameters at system level by copying the parameters for each component */
    public void copyValues(COINCOMOComponentParameters parameters) {
        final double eafWeights[][] = parameters.getEAFWeights();
        final double sfWeights[][] = parameters.getSFWeights();
        final int fpWeights[][] = parameters.getFPWeights();

        for (int i = 0; i < COINCOMOConstants.EAFS.length; i++) {
            System.arraycopy(eafWeights[i], 0, this.eafWeights[i], 0, this.eafWeights[i].length);
        }
        for (int i = 0; i < COINCOMOConstants.SFS.length; i++) {
            System.arraycopy(sfWeights[i], 0, this.sfWeights[i], 0, this.sfWeights[i].length);
        }
        for (int i = 0; i < COINCOMOConstants.FPS.length; i++) {
            System.arraycopy(fpWeights[i], 0, this.fpWeights[i], 0, this.fpWeights[i].length);
        }
        this.a = parameters.getA();
        this.b = parameters.getB();
        this.c = parameters.getC();
        this.d = parameters.getD();
        this.workHours = parameters.getWorkHours();
        this.revision = parameters.getRevision();
    }

    @Override
    public void copyUnit(COINCOMOUnit unitToBeCopied, boolean addCopyTag, boolean recursive) {
        if (unitToBeCopied != null) {
            COINCOMOComponentParameters parametersToBeCopied = (COINCOMOComponentParameters) unitToBeCopied;
            if (addCopyTag) {
                this.setName("Copy of " + parametersToBeCopied.getName());
            } else {
                this.setName(parametersToBeCopied.getName());
            }
            this.setSLOC(parametersToBeCopied.getSLOC());
            this.setCost(parametersToBeCopied.getCost());
            this.setStaff(parametersToBeCopied.getStaff());
            this.setEffort(parametersToBeCopied.getEffort());
            this.setSchedule(parametersToBeCopied.getSchedule());

            this.setEAFWeights(parametersToBeCopied.getEAFWeights());
            this.setSFWeights(parametersToBeCopied.getSFWeights());
            this.setFPWeights(parametersToBeCopied.getFPWeights());
            this.setA(parametersToBeCopied.getA());
            this.setB(parametersToBeCopied.getB());
            this.setC(parametersToBeCopied.getC());
            this.setD(parametersToBeCopied.getD());
            this.setWorkHours(parametersToBeCopied.getWorkHours());
            this.setRevision(parametersToBeCopied.getRevision());
        }
    }

    private static void log(Level level, String message) {
        Logger.getLogger(COINCOMOComponentParameters.class.getName()).log(level, message);
    }
}