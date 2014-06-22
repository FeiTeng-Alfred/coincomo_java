/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package core;

import core.COINCOMOConstants.Increment;
import core.COINCOMOConstants.Rating;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Raed Shomali
 */
public class COINCOMOComponent extends COINCOMOUnit {

    public static final String DEFAULT_NAME = "(Component)";
    private static final Rating RATING = Rating.NOM;
    private static final Increment INCREMENT = Increment.Percent0;
    private static final double INCEPTION_EFFORT_PERCENTAGE = 6.0d;
    private static final double ELABORATION_EFFORT_PERCENTAGE = 24.0d;
    private static final double CONSTRUCTION_EFFORT_PERCENTAGE = 76.0d;
    private static final double TRANSITION_EFFORT_PERCENTAGE = 12.0d;
    private static final double INCEPTION_SCHEDULE_PERCENTAGE = 12.5d;
    private static final double ELABORATION_SCHEDULE_PERCENTAGE = 37.5d;
    private static final double CONSTRUCTION_SCHEDULE_PERCENTAGE = 62.5d;
    private static final double TRANSITION_SCHEDULE_PERCENTAGE = 12.5d;

    private COINCOMOComponentParameters parameters = null;
    private double sced = 1.0d;
    private double scedPercent = 1.0d;
    private double sf = 18.97d;
    private int multiBuildShift = 0;
    private int revision = 1;
    private Rating scedRating;
    private Increment scedIncrement;
    private Rating sfRatings[];
    private Increment sfIncrements[];
    private double inceptionEffortPercentage = INCEPTION_EFFORT_PERCENTAGE;
    private double elaborationEffortPercentage = ELABORATION_EFFORT_PERCENTAGE;
    private double constructionEffortPercentage = CONSTRUCTION_EFFORT_PERCENTAGE;
    private double transitionEffortPercentage = TRANSITION_EFFORT_PERCENTAGE;
    private double inceptionSchedulePercentage = INCEPTION_SCHEDULE_PERCENTAGE;
    private double elaborationSchedulePercentage = ELABORATION_SCHEDULE_PERCENTAGE;
    private double constructionSchedulePercentage = CONSTRUCTION_SCHEDULE_PERCENTAGE;
    private double transitionSchedulePercentage = TRANSITION_SCHEDULE_PERCENTAGE;
    private double inceptionEffort = 0.0d;
    private double inceptionMonth = 0.0d;
    private double inceptionPersonnel = 0.0d;
    private double elaborationEffort = 0.0d;
    private double elaborationMonth = 0.0d;
    private double elaborationPersonnel = 0.0d;
    private double constructionEffort = 0.0d;
    private double constructionMonth = 0.0d;
    private double constructionPersonnel = 0.0d;
    private double transitionEffort = 0.0d;
    private double transitionMonth = 0.0d;
    private double transitionPersonnel = 0.0d;
    
    private boolean selected = true;//Rebecca

    public COINCOMOComponent() {
        parameters = new COINCOMOComponentParameters(this);

        scedRating = RATING;
        scedIncrement = INCREMENT;

        sfRatings = new Rating[COINCOMOConstants.SFS.length];
        sfIncrements = new Increment[COINCOMOConstants.SFS.length];
        for (int i = 0; i < COINCOMOConstants.SFS.length; i++) {
            sfRatings[i] = RATING;
            sfIncrements[i] = INCREMENT;
        }
    }

    public COINCOMOComponentParameters getParameters() {
        return this.parameters;
    }

    public double getSCED() {
        return this.sced;
    }

    public void setSCED(double sced) {
        if (this.sced == sced) {
            return;
        }
        this.sced = sced;
        this.setDirty();
    }

    public double getSCEDPercent() {
        return this.scedPercent;
    }

    public void setSCEDPercent(double scedPercent) {
        if (this.scedPercent == scedPercent) {
            return;
        }
        this.scedPercent = scedPercent;
        this.setDirty();
    }

    public double getSF() {
        return this.sf;
    }

    public void setSF(double sf) {
        if (this.sf == sf) {
            return;
        }
        this.sf = sf;
        this.setDirty();
    }

    public int getMultiBuildShift() {
        return multiBuildShift;
    }

    public void setMultiBuildShift(int multiBuildShift) {
        if (this.multiBuildShift == multiBuildShift) {
            return;
        }
        this.multiBuildShift = multiBuildShift;
        this.setDirty();
    }

    public void addMultiBuildShift(int multiBuildShift) {
        if (multiBuildShift == 0) {
            return;
        }

        this.multiBuildShift += multiBuildShift;
        this.setDirty();

        if (this.multiBuildShift < 0) {
            this.multiBuildShift = 0;
        }
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

    public Rating getSCEDRating() {
        return this.scedRating;
    }

    public void setSCEDRating(Rating scedRating) {
        if (this.scedRating == scedRating) {
            return;
        }
        this.scedRating = scedRating;
        this.setDirty();
    }

    public Increment getSCEDIncrement() {
        return this.scedIncrement;
    }

    public void setSCEDIncrement(Increment scedIncrement) {
        if (this.scedIncrement == scedIncrement) {
            return;
        }
        this.scedIncrement = scedIncrement;
        this.setDirty();
    }

    public Rating[] getSFRatings() {
        return this.sfRatings;
    }

    public void setSFRatings(Rating[] sfRatings) {
        boolean isDirty = false;

        if (this.sfRatings.length != sfRatings.length) {
            log(Level.SEVERE, "setSFRatings() has internal variable of length=" + this.sfRatings.length + " compared to parameter variable of length=" + sfRatings.length);
            return;
        } else {
            for (int i = 0; i < this.sfRatings.length; i++) {
                if (this.sfRatings[i] != sfRatings[i]) {
                    isDirty = true;
                    break;
                }
            }
        }

        if (!isDirty) {
            return;
        }
        System.arraycopy(sfRatings, 0, this.sfRatings, 0, this.sfRatings.length);
        this.setDirty();
    }

    public Increment[] getSFIncrements() {
        return this.sfIncrements;
    }

    public void setSFIncrements(Increment[] sfIncrements) {
        boolean isDirty = false;

        if (this.sfIncrements.length != sfIncrements.length) {
            log(Level.SEVERE, "setSFIncrements() has internal variable of length=" + this.sfIncrements.length + " compared to parameter variable of length=" + sfIncrements.length);
            return;
        } else {
            for (int i = 0; i < this.sfIncrements.length; i++) {
                if (this.sfIncrements[i] != sfIncrements[i]) {
                    isDirty = true;
                    break;
                }
            }
        }

        if (!isDirty) {
            return;
        }
        System.arraycopy(sfIncrements, 0, this.sfIncrements, 0, this.sfIncrements.length);
        this.setDirty();
    }

    @Override
    public double getSchedule() {
        return this.schedule;
    }

    public double getInceptionEffortPercentage() {
        return this.inceptionEffortPercentage;
    }

    public void setInceptionEffortPercentage(double inceptionEffortPercentage) {
        if (this.inceptionEffortPercentage == inceptionEffortPercentage) {
            return;
        }
        this.inceptionEffortPercentage = inceptionEffortPercentage;
        this.setDirty();
    }

    public double getInceptionSchedulePercentage() {
        return this.inceptionSchedulePercentage;
    }

    public void setInceptionSchedulePercentage(double inceptionSchedulePercentage) {
        if (this.inceptionSchedulePercentage == inceptionSchedulePercentage) {
            return;
        }
        this.inceptionSchedulePercentage = inceptionSchedulePercentage;
        this.setDirty();
    }

    public double getElaborationEffortPercentage() {
        return this.elaborationEffortPercentage;
    }

    public void setElaborationEffortPercentage(double elaborationEffortPercentage) {
        if (this.elaborationEffortPercentage == elaborationEffortPercentage) {
            return;
        }
        this.elaborationEffortPercentage = elaborationEffortPercentage;
        this.setDirty();
    }

    public double getElaborationSchedulePercentage() {
        return this.elaborationSchedulePercentage;
    }

    public void setElaborationSchedulePercentage(double elaborationSchedulePercentage) {
        if (this.elaborationSchedulePercentage == elaborationSchedulePercentage) {
            return;
        }
        this.elaborationSchedulePercentage = elaborationSchedulePercentage;
        this.setDirty();
    }

    public double getConstructionEffortPercentage() {
        return this.constructionEffortPercentage;
    }

    public void setConstructionEffortPercentage(double constructionEffortPercentage) {
        if (this.constructionEffortPercentage == constructionEffortPercentage) {
            return;
        }
        this.constructionEffortPercentage = constructionEffortPercentage;
        this.setDirty();
    }

    public double getConstructionSchedulePercentage() {
        return this.constructionSchedulePercentage;
    }

    public void setConstructionSchedulePercentage(double constructionSchedulePercentage) {
        if (this.constructionSchedulePercentage == constructionSchedulePercentage) {
            return;
        }
        this.constructionSchedulePercentage = constructionSchedulePercentage;
        this.setDirty();
    }

    public double getTransitionEffortPercentage() {
        return this.transitionEffortPercentage;
    }

    public void setTransitionEffortPercentage(double transitionEffortPercentage) {
        if (this.transitionEffortPercentage == transitionEffortPercentage) {
            return;
        }
        this.transitionEffortPercentage = transitionEffortPercentage;
        this.setDirty();
    }

    public double getTransitionSchedulePercentage() {
        return this.transitionSchedulePercentage;
    }

    public void setTransitionSchedulePercentage(double transitionSchedulePercentage) {
        if (this.transitionSchedulePercentage == transitionSchedulePercentage) {
            return;
        }
        this.transitionSchedulePercentage = transitionSchedulePercentage;
        this.setDirty();
    }

    public double getInceptionEffort() {
        return this.inceptionEffort;
    }

    public void setInceptionEffort(double inceptionEffort) {
        if (this.inceptionEffort == inceptionEffort) {
            return;
        }
        this.inceptionEffort = inceptionEffort;
        this.setDirty();
    }

    public double getInceptionMonth() {
        return this.inceptionMonth;
    }

    public void setInceptionMonth(double inceptionMonth) {
        if (this.inceptionMonth == inceptionMonth) {
            return;
        }
        this.inceptionMonth = inceptionMonth;
        this.setDirty();
    }

    public double getInceptionPersonnel() {
        return this.inceptionPersonnel;
    }

    public void setInceptionPersonnel(double inceptionPersonnel) {
        if (this.inceptionPersonnel == inceptionPersonnel) {
            return;
        }
        this.inceptionPersonnel = inceptionPersonnel;
        this.setDirty();
    }

    public double getElaborationEffort() {
        return this.elaborationEffort;
    }

    public void setElaborationEffort(double elaborationEffort) {
        if (this.elaborationEffort == elaborationEffort) {
            return;
        }
        this.elaborationEffort = elaborationEffort;
        this.setDirty();
    }

    public double getElaborationMonth() {
        return this.elaborationMonth;
    }

    public void setElaborationMonth(double elaborationMonth) {
        if (this.elaborationMonth == elaborationMonth) {
            return;
        }
        this.elaborationMonth = elaborationMonth;
        this.setDirty();
    }

    public double getElaborationPersonnel() {
        return this.elaborationPersonnel;
    }

    public void setElaborationPersonnel(double elaborationPersonnel) {
        if (this.elaborationPersonnel == elaborationPersonnel) {
            return;
        }
        this.elaborationPersonnel = elaborationPersonnel;
        this.setDirty();
    }

    public double getConstructionEffort() {
        return this.constructionEffort;
    }

    public void setConstructionEffort(double constructionEffort) {
        if (this.constructionEffort == constructionEffort) {
            return;
        }
        this.constructionEffort = constructionEffort;
        this.setDirty();
    }

    public double getConstructionMonth() {
        return this.constructionMonth;
    }

    public void setConstructionMonth(double constructionMonth) {
        if (this.constructionMonth == constructionMonth) {
            return;
        }
        this.constructionMonth = constructionMonth;
        this.setDirty();
    }

    public double getConstructionPersonnel() {
        return this.constructionPersonnel;
    }

    public void setConstructionPersonnel(double constructionPersonnel) {
        if (this.constructionPersonnel == constructionPersonnel) {
            return;
        }
        this.constructionPersonnel = constructionPersonnel;
        this.setDirty();
    }

    public double getTransitionEffort() {
        return this.transitionEffort;
    }

    public void setTransitionEffort(double transitionEffort) {
        if (this.transitionEffort == transitionEffort) {
            return;
        }
        this.transitionEffort = transitionEffort;
        this.setDirty();
    }

    public double getTransitionMonth() {
        return this.transitionMonth;
    }

    public void setTransitionMonth(double transitionMonth) {
        if (this.transitionMonth == transitionMonth) {
            return;
        }
        this.transitionMonth = transitionMonth;
        this.setDirty();
    }

    public double getTransitionPersonnel() {
        return this.transitionPersonnel;
    }

    public void setTransitionPersonnel(double transitionPersonnel) {
        if (this.transitionPersonnel == transitionPersonnel) {
            return;
        }
        this.transitionPersonnel = transitionPersonnel;
        this.setDirty();
    }

    public double getTotalEffortPercentageEC() {
        return (this.elaborationEffortPercentage + this.constructionEffortPercentage);
    }

    public double getTotalEffortPercentage() {
        return (this.inceptionEffortPercentage + this.elaborationEffortPercentage + this.constructionEffortPercentage + this.transitionEffortPercentage);
    }

    public double getTotalSchedulePercentageEC() {
        return (this.elaborationSchedulePercentage + this.constructionSchedulePercentage);
    }

    public double getTotalSchedulePercentage() {
        return (this.inceptionSchedulePercentage + this.elaborationSchedulePercentage + this.constructionSchedulePercentage + this.transitionSchedulePercentage);
    }

    public double getTotalEffortEC() {
        return (this.elaborationEffort + this.constructionEffort);
    }

    public double getTotalEffort() {
        return (this.inceptionEffort + this.elaborationEffort + this.constructionEffort + this.transitionEffort);
    }

    public double getTotalMonthEC() {
        return (this.elaborationMonth + this.constructionMonth);
    }

    public double getTotalMonth() {
        return (this.inceptionMonth + this.elaborationMonth + this.constructionMonth + this.transitionMonth);
    }

    public double getTotalPersonnelEC() {
        return (this.elaborationEffort + this.constructionEffort) / (this.elaborationMonth + this.constructionMonth);
    }

    public double getTotalPersonnel() {
        return (this.inceptionEffort + this.elaborationEffort + this.constructionEffort + this.transitionEffort) / (this.inceptionMonth+ this.elaborationMonth + this.constructionMonth + this.transitionMonth);
    }
    
       //rebecca
    public void setSelected(boolean selected) {
        if (this.selected == selected) {
            return;
        }
        this.selected = selected;
        this.setDirty();
    }
     
    public boolean isSelected() {
        return this.selected;
    }
    //rebecca

    @Override
    public void copyUnit(COINCOMOUnit unitToBeCopied, boolean addCopyTag, boolean recursive) {
        if (unitToBeCopied != null) {
            COINCOMOComponent componentToBeCopied = (COINCOMOComponent) unitToBeCopied;
            if (addCopyTag) {
                this.setName("Copy of " + componentToBeCopied.getName());
            } else {
                this.setName(componentToBeCopied.getName());
            }
            this.setSLOC(componentToBeCopied.getSLOC());
            this.setCost(componentToBeCopied.getCost());
            this.setStaff(componentToBeCopied.getStaff());
            this.setEffort(componentToBeCopied.getEffort());
            this.setSchedule(componentToBeCopied.getSchedule());

            this.getParameters().copyUnit(componentToBeCopied.getParameters(), addCopyTag, false);
            this.setSCED(componentToBeCopied.getSCED());
            this.setSCEDPercent(componentToBeCopied.getSCEDPercent());
            this.setSF(componentToBeCopied.getSF());
            this.setMultiBuildShift(componentToBeCopied.getMultiBuildShift());
            this.setRevision(componentToBeCopied.getRevision());
            this.setSCEDRating(componentToBeCopied.getSCEDRating());
            this.setSCEDIncrement(componentToBeCopied.getSCEDIncrement());
            this.setSFRatings(componentToBeCopied.getSFRatings());
            this.setSFIncrements(componentToBeCopied.getSFIncrements());
            this.setInceptionEffortPercentage(componentToBeCopied.getInceptionEffortPercentage());
            this.setInceptionSchedulePercentage(componentToBeCopied.getInceptionSchedulePercentage());
            this.setInceptionEffort(componentToBeCopied.getInceptionEffort());
            this.setInceptionMonth(componentToBeCopied.getInceptionMonth());
            this.setInceptionPersonnel(componentToBeCopied.getInceptionPersonnel());
            this.setElaborationEffortPercentage(componentToBeCopied.getElaborationEffortPercentage());
            this.setElaborationSchedulePercentage(componentToBeCopied.getElaborationSchedulePercentage());
            this.setElaborationEffort(componentToBeCopied.getElaborationEffort());
            this.setElaborationMonth(componentToBeCopied.getElaborationMonth());
            this.setElaborationPersonnel(componentToBeCopied.getElaborationPersonnel());
            this.setConstructionEffortPercentage(componentToBeCopied.getConstructionEffortPercentage());
            this.setConstructionSchedulePercentage(componentToBeCopied.getConstructionSchedulePercentage());
            this.setConstructionEffort(componentToBeCopied.getConstructionEffort());
            this.setConstructionMonth(componentToBeCopied.getConstructionMonth());
            this.setConstructionPersonnel(componentToBeCopied.getConstructionPersonnel());
            this.setTransitionEffortPercentage(componentToBeCopied.getTransitionEffortPercentage());
            this.setTransitionSchedulePercentage(componentToBeCopied.getTransitionSchedulePercentage());
            this.setTransitionEffort(componentToBeCopied.getTransitionEffort());
            this.setTransitionMonth(componentToBeCopied.getTransitionMonth());
            this.setTransitionPersonnel(componentToBeCopied.getTransitionPersonnel());

            if (recursive) {
                Iterator iter = unitToBeCopied.getListOfSubUnits().iterator();

                while (iter.hasNext()) {
                    COINCOMOSubComponent subComponentToBeCopied = (COINCOMOSubComponent) iter.next();
                    COINCOMOSubComponent subComponent = new COINCOMOSubComponent();
                    subComponent.copyUnit(subComponentToBeCopied, addCopyTag, recursive);
                    this.addSubUnit(subComponent);
                }
            }
        }
    }

    private static void log(Level level, String message) {
        Logger.getLogger(COINCOMOComponent.class.getName()).log(level, message);
    }
}
