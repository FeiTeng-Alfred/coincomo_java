/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package core;

import core.COINCOMOConstants.CalculationMethod;
import core.COINCOMOConstants.FP;
import core.COINCOMOConstants.Increment;
import core.COINCOMOConstants.Rating;
import core.COINCOMOConstants.RatioType;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Raed Shomali
 */
public class COINCOMOSubComponent extends COINCOMOUnit {

    public static final String DEFAULT_NAME = "(Sub Component)";

    private static final Rating RATING = Rating.NOM;
    private static final Increment INCREMENT = Increment.Percent0;
    private static final String LANGUAGE = "Non-specified";
    private static final RatioType RATIO_TYPE = RatioType.Jones;
    private static final CalculationMethod CALCULATION_METHOD = CalculationMethod.UsingTable;

    private double productivity = 0.0d;
    private double instructionCost = 0.0d;
    private double risk = 0.0d;
    private double nominalEffort = 0.0d;
    private double estimatedEffort = 0.0d;
    private double eaf = 1.0d;
    private long sumOfNewSlocFpSlocAarSlocs = 0;
    private double laborRate = 0.0d;
    private double revl = 0.0d;
    private String language;
    private Rating eafRatings[];
    private Increment eafIncrements[];
    private long newSLOC = 0;
    private int multiplier = 0;
    private RatioType ratioType;
    private CalculationMethod calculationMethod;
    private int internalLogicalFiles[] = {0, 0, 0, 0};      // Low , Average , High, SubTotal
    private int externalInterfaceFiles[] = {0, 0, 0, 0};    // Low , Average , High, SubTotal
    private int externalInputs[] = {0, 0, 0, 0};            // Low , Average , High, SubTotal
    private int externalOutputs[] = {0, 0, 0, 0};           // Low , Average , High, SubTotal
    private int externalInquiries[] = {0, 0, 0, 0};         // Low , Average , High, SubTotal
    private int totalUnadjustedFunctionPoints = 0;
    private long equivalentSLOC = 0;

    public COINCOMOSubComponent() {
        super();

        this.language = LANGUAGE;

        this.eafRatings = new Rating[COINCOMOConstants.EAFS.length - 1];
        this.eafIncrements = new Increment[COINCOMOConstants.EAFS.length - 1];

        for (int i = 0; i < COINCOMOConstants.EAFS.length - 1; i++) {
            this.eafRatings[i] = RATING;
            this.eafIncrements[i] = INCREMENT;
        }

        ratioType = RATIO_TYPE;
        calculationMethod = CALCULATION_METHOD;
    }

    @Override
    public long getSLOC() {
        return this.sloc;
    }

    @Override
    public double getCost() {
        return this.cost;
    }

    @Override
    public double getStaff() {
        return this.staff;
    }

    @Override
    public double getEffort() {
        return this.effort;
    }

    @Override
    public double getSchedule() {
        return this.schedule;
    }

    public double getProductivity() {
        return this.productivity;
    }

    public void setProductivity(double productivity) {
        if (this.productivity == productivity) {
            return;
        }
        this.productivity = productivity;
        this.setDirty();
    }

    public double getInstructionCost() {
        return this.instructionCost;
    }

    public void setInstructionCost(double instructionCost) {
        if (this.instructionCost == instructionCost) {
            return;
        }
        this.instructionCost = instructionCost;
        this.setDirty();
    }

    public double getRisk() {
        return this.risk;
    }

    public void setRisk(double risk) {
        if (this.risk == risk) {
            return;
        }
        this.risk = risk;
        this.setDirty();
    }

    public double getNominalEffort() {
        return this.nominalEffort;
    }

    public void setNominalEffort(double nominalEffort) {
        if (this.nominalEffort == nominalEffort) {
            return;
        }
        this.nominalEffort = nominalEffort;
        this.setDirty();
    }

    public double getEstimatedEffort() {
        return this.estimatedEffort;
    }

    public void setEstimatedEffort(double estimatedEffort) {
        if (this.estimatedEffort == estimatedEffort) {
            return;
        }
        this.estimatedEffort = estimatedEffort;
        this.setDirty();
    }

    public double getEAF() {
        return this.eaf;
    }

    public void setEAF(double eaf) {
        if (this.eaf == eaf) {
            return;
        }
        this.eaf = eaf;
        this.setDirty();
    }

    public long getSumOfSLOCs() {
        return this.sumOfNewSlocFpSlocAarSlocs;
    }

    public void setSumOfSLOCs(long sumOfNewSlocFpSlocAarSlocs) {
        if (this.sumOfNewSlocFpSlocAarSlocs == sumOfNewSlocFpSlocAarSlocs) {
            return;
        }
        this.sumOfNewSlocFpSlocAarSlocs = sumOfNewSlocFpSlocAarSlocs;
        this.setDirty();
    }

    public double getLaborRate() {
        return this.laborRate;
    }

    public void setLaborRate(double laborRate) {
        if (this.laborRate == laborRate) {
            return;
        }
        this.laborRate = laborRate;
        this.setDirty();
    }

    public double getREVL() {
        return this.revl;
    }

    public void setREVL(double revl) {
        if (this.revl == revl) {
            return;
        }
        this.revl = revl;
        this.setDirty();
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        if (this.language.equals(language)) {
            return;
        }
        this.language = language;
        this.setDirty();
    }

    public Rating[] getEAFRatings() {
        return this.eafRatings;
    }

    public void setEAFRatings(Rating[] eafRatings) {
        boolean isDirty = false;

        if (this.eafRatings.length != eafRatings.length) {
            log(Level.SEVERE, "setEAFRatings() has internal variable of length=" + this.eafRatings.length + " compared to parameter variable of length=" + eafRatings.length);
            return;
        } else {
            for (int i = 0; i < this.eafRatings.length; i++) {
                if (this.eafRatings[i] != eafRatings[i]) {
                    isDirty = true;
                    break;
                }
            }
        }

        if (!isDirty) {
            return;
        }
        System.arraycopy(eafRatings, 0, this.eafRatings, 0, this.eafRatings.length);
        this.setDirty();
    }

    public Increment[] getEAFIncrements() {
        return this.eafIncrements;
    }

    public void setEAFIncrements(Increment[] eafIncrements) {
        boolean isDirty = false;

        if (this.eafIncrements.length != eafIncrements.length) {
            log(Level.SEVERE, "setEAFIncrements() has internal variable of length=" + this.eafIncrements.length + " compared to parameter variable of length=" + eafIncrements.length);
            return;
        } else {
            for (int i = 0; i < this.eafIncrements.length; i++) {
                if (this.eafIncrements[i] != eafIncrements[i]) {
                    isDirty = true;
                    break;
                }
            }
        }

        if (!isDirty) {
            return;
        }
        System.arraycopy(eafIncrements, 0, this.eafIncrements, 0, this.eafIncrements.length);
        this.setDirty();
    }

    public long getNewSLOC() {
        return newSLOC;
    }

    public void setNewSLOC(long newSLOC) {
        if (this.newSLOC == newSLOC) {
            return;
        }
        this.newSLOC = newSLOC;
        this.setDirty();
    }

    public RatioType getRatioType() {
        return this.ratioType;
    }

    public void setRatioType(RatioType ratioType) {
        if (this.ratioType == ratioType) {
            return;
        }
        this.ratioType = ratioType;
        this.setDirty();
    }

    public CalculationMethod getCalculationMethod() {
        return this.calculationMethod;
    }

    public void setCalculationMethod(CalculationMethod calcMethod) {
        if (this.calculationMethod == calcMethod) {
            return;
        }
        this.calculationMethod = calcMethod;
        this.setDirty();
    }

    public int getMultiplier() {
        return this.multiplier;
    }

    public void setMultiplier(int multiplier) {
        if (this.multiplier == multiplier) {
            return;
        }
        this.multiplier = multiplier;
        this.setDirty();
    }

    public int[] getInternalLogicalFiles() {
        return this.internalLogicalFiles;
    }

    public void setInternalLogicalFiles(int[] internalLogicalFiles) {
        for (int i = 0; i < internalLogicalFiles.length; i++) {
            this.internalLogicalFiles[i] = internalLogicalFiles[i];
        }
        this.setDirty();
    }

    public int[] getExternalInterfaceFiles() {
        return externalInterfaceFiles;
    }

    public void setExternalInterfaceFiles(int[] externalInterfaceFiles) {
        for (int i = 0; i < externalInterfaceFiles.length; i++) {
            this.externalInterfaceFiles[i] = externalInterfaceFiles[i];
        }
        this.setDirty();
    }

    public int[] getExternalInputs() {
        return this.externalInputs;
    }

    public void setExternalInputs(int[] externalInputs) {
        for (int i = 0; i < externalInputs.length; i++) {
            this.externalInputs[i] = externalInputs[i];
        }
        this.setDirty();
    }

    public int[] getExternalOutputs() {
        return this.externalOutputs;
    }

    public void setExternalOutputs(int[] externalOutputs) {
        for (int i = 0; i < externalOutputs.length; i++) {
            this.externalOutputs[i] = externalOutputs[i];
        }
        this.setDirty();
    }

    public int[] getExternalInquiries() {
        return this.externalInquiries;
    }

    public void setExternalInquiries(int[] externalInquiries) {
        for (int i = 0; i < externalInquiries.length; i++) {
            this.externalInquiries[i] = externalInquiries[i];
        }
        this.setDirty();
    }

    public int[] getSubTotals() {
        int subTotals[] = new int[COINCOMOConstants.FPS.length];
        subTotals[FP.ILF.ordinal()] = this.internalLogicalFiles[this.internalLogicalFiles.length-1];
        subTotals[FP.EIF.ordinal()] = this.externalInterfaceFiles[this.externalInterfaceFiles.length-1];
        subTotals[FP.EI.ordinal()] = this.externalInputs[this.externalInputs.length-1];
        subTotals[FP.EO.ordinal()] = this.externalOutputs[this.externalOutputs.length-1];
        subTotals[FP.EQ.ordinal()] = this.externalInquiries[this.externalInquiries.length-1];
        return subTotals;
    }

    public void setSubTotals(int[] subTotals) {
        this.internalLogicalFiles[this.internalLogicalFiles.length-1] = subTotals[FP.ILF.ordinal()];
        this.externalInterfaceFiles[this.externalInterfaceFiles.length-1] = subTotals[FP.EIF.ordinal()];
        this.externalInputs[this.externalInputs.length-1] = subTotals[FP.EI.ordinal()];
        this.externalOutputs[this.externalOutputs.length-1] = subTotals[FP.EO.ordinal()];
        this.externalInquiries[this.externalInquiries.length-1] = subTotals[FP.EQ.ordinal()];
        this.setDirty();
    }

    public int getTotalUnadjustedFunctionPoints() {
        return this.totalUnadjustedFunctionPoints;
    }

    public void setTotalUnadjustedFunctionPoints(int totalUnadjustedFunctionPoints) {
        if (this.totalUnadjustedFunctionPoints == totalUnadjustedFunctionPoints) {
            return;
        }
        this.totalUnadjustedFunctionPoints = totalUnadjustedFunctionPoints;
        this.setDirty();
    }

    public long getEquivalentSLOC() {
        return this.equivalentSLOC;
    }

    public void setEquivalentSLOC(long equivalentSLOC) {
        if (this.equivalentSLOC == equivalentSLOC) {
            return;
        }
        this.equivalentSLOC = equivalentSLOC;
        this.setDirty();
    }

    @Override
    public void copyUnit(COINCOMOUnit unitToBeCopied, boolean addCopyTag, boolean recursive) {
        if (unitToBeCopied != null) {
            COINCOMOSubComponent subComponentToBeCopied = (COINCOMOSubComponent) unitToBeCopied;
            if (addCopyTag) {
                this.setName("Copy of " + subComponentToBeCopied.getName());
            } else {
                this.setName(subComponentToBeCopied.getName());
            }
            this.setSLOC(subComponentToBeCopied.getSLOC());
            this.setCost(subComponentToBeCopied.getCost());
            this.setStaff(subComponentToBeCopied.getStaff());
            this.setEffort(subComponentToBeCopied.getEffort());
            this.setSchedule(subComponentToBeCopied.getSchedule());

            this.setProductivity(subComponentToBeCopied.getProductivity());
            this.setInstructionCost(subComponentToBeCopied.getInstructionCost());
            this.setRisk(subComponentToBeCopied.getRisk());
            this.setNominalEffort(subComponentToBeCopied.getNominalEffort());
            this.setEstimatedEffort(subComponentToBeCopied.getEstimatedEffort());
            this.setEAF(subComponentToBeCopied.getEAF());
            this.setSumOfSLOCs(subComponentToBeCopied.getSumOfSLOCs());
            this.setLaborRate(subComponentToBeCopied.getLaborRate());
            this.setREVL(subComponentToBeCopied.getREVL());
            this.setLanguage(subComponentToBeCopied.getLanguage());
            this.setEAFRatings(subComponentToBeCopied.getEAFRatings());
            this.setEAFIncrements(subComponentToBeCopied.getEAFIncrements());
            this.setNewSLOC(subComponentToBeCopied.getNewSLOC());
            this.setMultiplier(subComponentToBeCopied.getMultiplier());
            this.setRatioType(subComponentToBeCopied.getRatioType());
            this.setCalculationMethod(subComponentToBeCopied.getCalculationMethod());
            this.setInternalLogicalFiles(subComponentToBeCopied.getInternalLogicalFiles());
            this.setExternalInterfaceFiles(subComponentToBeCopied.getExternalInterfaceFiles());
            this.setExternalInputs(subComponentToBeCopied.getExternalInputs());
            this.setExternalOutputs(subComponentToBeCopied.getExternalOutputs());
            this.setExternalInquiries(subComponentToBeCopied.getExternalInquiries());
            this.setTotalUnadjustedFunctionPoints(subComponentToBeCopied.getTotalUnadjustedFunctionPoints());
            this.setEquivalentSLOC(subComponentToBeCopied.getEquivalentSLOC());

            if (recursive) {
                Iterator iter = unitToBeCopied.getListOfSubUnits().iterator();

                while (iter.hasNext()) {
                    COINCOMOAdaptationAndReuse adaptationToBeCopied = (COINCOMOAdaptationAndReuse) iter.next();
                    COINCOMOAdaptationAndReuse adaptation = new COINCOMOAdaptationAndReuse();
                    adaptation.copyUnit(adaptationToBeCopied, addCopyTag, recursive);
                    this.addSubUnit(adaptation);
                }
            }
        }
    }

    private static void log(Level level, String message) {
        Logger.getLogger(COINCOMOSubComponent.class.getName()).log(level, message);
    }
}
