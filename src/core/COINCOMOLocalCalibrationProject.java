/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package core;

import java.util.Date;
import java.util.Iterator;

/**
 *
 * @author Abhishek
 */
public class COINCOMOLocalCalibrationProject extends COINCOMOUnit {

    private COINCOMOComponent component = null;
    private Date date;
    //these are the values of all the rows inserted in the table
    private double effort = 0;
    private double schedule = 0;
    private boolean selected = false;
    private double eaf = 0.0d;

    public COINCOMOLocalCalibrationProject(COINCOMOComponent component) {
        this.component = component;

        /*
         * Calculating the effective EAF for a component based on the sub-components and their respective EAFs.
         * Sub-component EAF values are the ones with SCED.
         */
        double eaf = 0.0d;
        double eafSum = 0.0d;
        Iterator iter = component.getListOfSubUnits().iterator();
        while (iter.hasNext()) {
            COINCOMOSubComponent subComponent = (COINCOMOSubComponent) iter.next();
            eafSum += subComponent.getEAF() * (double) subComponent.getSLOC();
        }
        eaf = eafSum / (double) component.getSLOC();

        if (Double.isInfinite(eaf) || Double.isNaN(eaf)) {
            this.eaf = 0.0d;
        } else {
            this.eaf = eaf;
        }
    }

    public COINCOMOComponent getComponent() {
        return this.component;
    }

    public void setDate() {
        Date date = new Date();
        this.date = date;
        this.setDirty();
    }

    public void setDate(Date date) {
        if (this.date == date) {
            return;
        }
        this.date = date;
        this.setDirty();
        
    }
    public Date getDate() {
        return date;
    }

    public void setEffort(double effort) {
        if (this.effort == effort) {
            return;
        }
        this.effort = effort;
        this.setDirty();
    }

    public double getEffort() {
        return this.effort;
    }

    public double getSchedule() {
        return this.schedule;
    }

    public void setSchedule(double schedule) {
        if (this.schedule == schedule) {
            return;
        }
        this.schedule = schedule;
        this.setDirty();
    }

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

    public double getEAF() {
        return this.eaf;
    }

    public COINCOMOSystem getParentSystem() {
        return (COINCOMOSystem) this.component.getParent().getParent();
    }
    @Override
    public String getName() {
        return this.component.getName();
    }

    @Override
    public void copyUnit(COINCOMOUnit unitToBeCopied, boolean addCopyTag, boolean recursive) {
        /* TODO (Larry) What is COINCOMOLocalCalibrationProject is supposed to be? */
    }
}
