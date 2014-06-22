/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package core;

/**
 *
 * @author Justin
 */
public class COINCOMOAdaptationAndReuse extends COINCOMOUnit {

    public static final String DEFAULT_NAME = "(Reused Module)";

    public static final double DESIGN_MODIFIED = 0.0d;
    public static final double CODE_MODIFIED = 0.0d;
    public static final double INTEGRATION_MODIFIED = 0.0d;
    public static final double SOFTWARE_UNDERSTANDING = 30.0d;
    public static final double ASSESSMENT_AND_ASSIMILATION = 4.0d;
    public static final double UNFAMILIARITY_WITH_SOFTWARE = 0.4d;
    public static final double AUTOMATICALLY_TRANSLATED = 0.0d;
    public static final double AUTOMAITC_TRANSLATION_PRODUCTIVITY = 2400.0d;
    
    private long adaptedSLOC = 0;
    private double designModified = DESIGN_MODIFIED;
    private double codeModified = CODE_MODIFIED;
    private double integrationModified = INTEGRATION_MODIFIED;
    private double softwareUnderstanding = SOFTWARE_UNDERSTANDING;
    private double assessmentAndAssimilation = ASSESSMENT_AND_ASSIMILATION;
    private double unfamiliarityWithSoftware = UNFAMILIARITY_WITH_SOFTWARE;
    private double automaticallyTranslated = AUTOMATICALLY_TRANSLATED;
    private double automaticTranslationProductivity = AUTOMAITC_TRANSLATION_PRODUCTIVITY;
    private double adaptationAdjustmentFactor = 0.0d;
    private long equivalentSLOC = 0;

    @Override
    public long getSLOC() {
        return 0;
    }

    @Override
    public double getCost() {
        return 0.0d;
    }

    @Override
    public double getStaff() {
        return 0.0d;
    }

    @Override
    public double getEffort() {
        return 0.0d;
    }

    @Override
    public double getSchedule() {
        return 0.0d;
    }

    public long getAdaptedSLOC() {
        return this.adaptedSLOC;
    }

    public void setAdaptedSLOC(long adaptedSLOC) {
        if (this.adaptedSLOC == adaptedSLOC) {
            return;
        }
        this.adaptedSLOC = adaptedSLOC;
        this.setDirty();
    }

    public double getDesignModified() {
        return designModified;
    }

    public void setDesignModified(double designModified) {
        if (this.designModified == designModified) {
            return;
        }
        this.designModified = designModified;
        this.setDirty();
    }

    public double getCodeModified() {
        return this.codeModified;
    }

    public void setCodeModified(double codeModified) {
        if (this.codeModified == codeModified) {
            return;
        }
        this.codeModified = codeModified;
        this.setDirty();
    }

    public double getIntegrationModified() {
        return this.integrationModified;
    }

    public void setIntegrationModified(double integrationModified) {
        if (this.integrationModified == integrationModified) {
            return;
        }
        this.integrationModified = integrationModified;
        this.setDirty();
    }

    public double getSoftwareUnderstanding() {
        return this.softwareUnderstanding;
    }

    public void setSoftwareUnderstanding(double softwareUnderstanding) {
        if (this.softwareUnderstanding == softwareUnderstanding) {
            return;
        }
        this.softwareUnderstanding = softwareUnderstanding;
        this.setDirty();
    }

    public double getAssessmentAndAssimilation() {
        return this.assessmentAndAssimilation;
    }

    public void setAssessmentAndAssimilation(double assessmentAndAssimilation) {
        if (this.assessmentAndAssimilation == assessmentAndAssimilation) {
            return;
        }
        this.assessmentAndAssimilation = assessmentAndAssimilation;
        this.setDirty();
    }

    public double getUnfamiliarityWithSoftware() {
        return this.unfamiliarityWithSoftware;
    }

    public void setUnfamiliarityWithSoftware(double unfamiliarityWithSoftware) {
        if (this.unfamiliarityWithSoftware == unfamiliarityWithSoftware) {
            return;
        }
        this.unfamiliarityWithSoftware = unfamiliarityWithSoftware;
        this.setDirty();
    }

    public double getAutomaticallyTranslated() {
        return this.automaticallyTranslated;
    }

    public void setAutomaticallyTranslated(double automaticallyTranslated) {
        if (this.automaticallyTranslated == automaticallyTranslated) {
            return;
        }
        this.automaticallyTranslated = automaticallyTranslated;
        this.setDirty();
    }

    public double getAutomaticTranslationProductivity() {
        return this.automaticTranslationProductivity;
    }

    public void setAutomaticTranslationProductivity(double automaticTranslationProductivity) {
        if (this.automaticTranslationProductivity == automaticTranslationProductivity) {
            return;
        }
        this.automaticTranslationProductivity = automaticTranslationProductivity;
        this.setDirty();
    }

    public double getAdaptationAdjustmentFactor() {
        return this.adaptationAdjustmentFactor;
    }

    public void setAdaptationAdjustmentFactor(double adaptationAdjustmentFactor) {
        if (this.adaptationAdjustmentFactor == adaptationAdjustmentFactor) {
            return;
        }
        this.adaptationAdjustmentFactor = adaptationAdjustmentFactor;
        this.setDirty();
    }

    public long getEquivalentSLOC() {
        return equivalentSLOC;
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
            COINCOMOAdaptationAndReuse adaptationToBeCopied = (COINCOMOAdaptationAndReuse) unitToBeCopied;
            if (addCopyTag) {
                this.setName("Copy of " + adaptationToBeCopied.getName());
            } else {
                this.setName(adaptationToBeCopied.getName());
            }
            this.setSLOC(adaptationToBeCopied.getSLOC());
            this.setCost(adaptationToBeCopied.getCost());
            this.setStaff(adaptationToBeCopied.getStaff());
            this.setEffort(adaptationToBeCopied.getEffort());
            this.setSchedule(adaptationToBeCopied.getSchedule());

            this.setAdaptedSLOC(adaptationToBeCopied.getAdaptedSLOC());
            this.setDesignModified(adaptationToBeCopied.getDesignModified());
            this.setCodeModified(adaptationToBeCopied.getCodeModified());
            this.setIntegrationModified(adaptationToBeCopied.getIntegrationModified());
            this.setSoftwareUnderstanding(adaptationToBeCopied.getSoftwareUnderstanding());
            this.setAssessmentAndAssimilation(adaptationToBeCopied.getAssessmentAndAssimilation());
            this.setUnfamiliarityWithSoftware(adaptationToBeCopied.getUnfamiliarityWithSoftware());
            this.setAutomaticallyTranslated(adaptationToBeCopied.getAutomaticallyTranslated());
            this.setAutomaticTranslationProductivity(adaptationToBeCopied.getAutomaticTranslationProductivity());
            this.setAdaptationAdjustmentFactor(adaptationToBeCopied.getAdaptationAdjustmentFactor());
            this.setEquivalentSLOC(adaptationToBeCopied.getEquivalentSLOC());
        }
    }
}
