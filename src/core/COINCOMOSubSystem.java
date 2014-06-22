/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package core;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Raed Shomali
 */
public class COINCOMOSubSystem extends COINCOMOUnit {

    public static final String DEFAULT_NAME = "(Sub System)";

    private int zoomLevel = 100;

    public int getZoomLevel() {
        return this.zoomLevel;
    }

    public void setZoomLevel(int zoomLevel) /*throws Exception*/ {
        if (zoomLevel < 0 || zoomLevel > 100) {
            log(Level.SEVERE, this.getName() + ".setZoomLevel(" + zoomLevel + ") is illegal. Reset to 100.");
            //throw new Exception(this.getName() + ".setZoomLevel(" + zoomLevel + ") is illegal.");
            zoomLevel = 100;
        } else if (this.zoomLevel == zoomLevel) {
            return;
        }
        this.zoomLevel = zoomLevel;
        this.setDirty();
    }

    @Override
    public void copyUnit(COINCOMOUnit unitToBeCopied, boolean addCopyTag, boolean recursive) {
        if (unitToBeCopied != null) {
            COINCOMOSubSystem subSystemToBeCopied = (COINCOMOSubSystem) unitToBeCopied;
            if (addCopyTag) {
                this.setName("Copy of " + subSystemToBeCopied.getName());
            } else {
                this.setName(subSystemToBeCopied.getName());
            }
            this.setSLOC(subSystemToBeCopied.getSLOC());
            this.setCost(subSystemToBeCopied.getCost());
            this.setStaff(subSystemToBeCopied.getStaff());
            this.setEffort(subSystemToBeCopied.getEffort());
            this.setSchedule(subSystemToBeCopied.getSchedule());

            this.setZoomLevel(subSystemToBeCopied.getZoomLevel());

            if (recursive) {
                Iterator iter = unitToBeCopied.getListOfSubUnits().iterator();

                while (iter.hasNext()) {
                    COINCOMOComponent componentToBeCopied = (COINCOMOComponent) iter.next();
                    COINCOMOComponent component = new COINCOMOComponent();
                    component.copyUnit(componentToBeCopied, addCopyTag, recursive);
                    this.addSubUnit(component);
                }
            }
        }
    }

    private static void log(Level level, String message) {
        Logger.getLogger(COINCOMOSubSystem.class.getName()).log(level, message);
    }
}
