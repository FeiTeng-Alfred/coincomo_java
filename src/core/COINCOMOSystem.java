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
public class COINCOMOSystem extends COINCOMOUnit {

    public static final String DEFAULT_NAME = "(System)";

    @Override
    public void copyUnit(COINCOMOUnit unitToBeCopied, boolean addCopyTag, boolean recursive) {
        if (unitToBeCopied != null) {
            COINCOMOSystem systemToBeCopied = (COINCOMOSystem) unitToBeCopied;
            if (addCopyTag) {
                this.setName("Copy of " + systemToBeCopied.getName());
            } else {
                this.setName(systemToBeCopied.getName());
            }
            this.setSLOC(systemToBeCopied.getSLOC());
            this.setCost(systemToBeCopied.getCost());
            this.setStaff(systemToBeCopied.getStaff());
            this.setEffort(systemToBeCopied.getEffort());
            this.setSchedule(systemToBeCopied.getSchedule());

            if (recursive) {
                Iterator iter = unitToBeCopied.getListOfSubUnits().iterator();

                while (iter.hasNext()) {
                    COINCOMOSubSystem subSystemToBeCopied = (COINCOMOSubSystem) iter.next();
                    COINCOMOUnit subSystem = new COINCOMOSubSystem();
                    subSystem.copyUnit(subSystemToBeCopied, addCopyTag, recursive);
                    this.addSubUnit(subSystem);
                }
            }
        }
    }

    private static void log(Level level, String message) {
        Logger.getLogger(COINCOMOSystem.class.getName()).log(level, message);
    }
}
