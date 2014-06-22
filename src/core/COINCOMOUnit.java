/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Raed Shomali
 */
public abstract class COINCOMOUnit implements Comparable<COINCOMOUnit> {

    private String name = "";
    private int unitID = 0;
    private long databaseID = 0;
    protected long sloc = 0;
    protected double cost = 0.0;
    protected double staff = 0.0;
    protected double effort = 0.0;
    protected double schedule = 0.0;
    private boolean dirty = false;
    private COINCOMOUnit parent = null;
    private HashMap<Integer, COINCOMOUnit> listOfSubUnits = new HashMap<Integer, COINCOMOUnit>();
    private int counter = 0;
    private int nextAutoID = 1;

    /**
     *
     * @return the name of the unit as type String.
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @param name sets the name of the unit.
     */
    public void setName(String name) {
        if (name.equals(this.name)) {
            return;
        }

        this.name = name;
        this.setDirty();
    }

    /**
     *
     * @return the ID of the unit as type long.
     */
    public int getUnitID() {
        return this.unitID;
    }

    /**
     *
     * @return the database ID of the unit as type long.
     */
    public long getDatabaseID() {
        return this.databaseID;
    }

    /**
     *
     * @param name sets the name of the unit.
     */
    public void setDatabaseID(long databaseID) {
        this.databaseID = databaseID;
        this.setDirty();
    }

    /**
     * @return the dirty flag of the unit as type boolean.
     */
    public boolean isDirty() {
        return this.dirty;
    }

    /**
     * Set the unit as dirty if variable values are changed.
     */
    public void setDirty() {
        if (this.parent != null) {
            this.parent.setDirty();
        }
        this.dirty = true;
    }

    /**
     * Clear the dirty flag of the unit.
     */
    public void clearDirty() {
        this.dirty = false;
    }

    /**
     *
     * @return the object of the root unit as type COINCOMOUnit.
     */
    public COINCOMOUnit getRoot() {
        if (this.parent != null) {
            return this.parent.getRoot();
        } else {
            return this;
        }
    }

    /**
     *
     * @return the object of the parent unit as type COINCOMOUnit.
     */
    public COINCOMOUnit getParent() {
        return this.parent;
    }

    /**
     *
     * @return the sorted sub units by unit ID as type ArrayList<COINCOMOUnit>.
     */
    public ArrayList<COINCOMOUnit> getListOfSubUnits() {
        ArrayList<COINCOMOUnit> list = new ArrayList<COINCOMOUnit>(this.listOfSubUnits.values());
        Collections.sort(list);

        return list;
    }

    /**
     * Add a sub unit to the list of sub units for the unit.
     */
    public void addSubUnit(COINCOMOUnit unit) {
        if (unit.getParent() == this) {
            return;
        }

        unit.parent = this;
        unit.unitID = this.getNextID();
        this.listOfSubUnits.put(unit.getUnitID(), unit);
        this.setDirty();
    }

    /**
     * Remove a sub unit from the list of sub units for the unit.
     */
    public void removeSubUnit(COINCOMOUnit unit) {
        if (unit.getParent() != this) {
            return;
        }

        unit.parent = null;
        this.listOfSubUnits.remove(unit.getUnitID());
        this.setDirty();
    }

    /**
     * @return the incremented internal counter for the unit.
     */
    protected int getNextID() {
        return counter++;
    }

    public void setNextID(int counter) {
        if (this.counter >= counter) {
            return;
        }
        this.counter = counter;
    }

    /**
     * @return the total SLOC for the unit.
     */
    public long getSLOC() {
        if (this.isDirty()) {
            long sloc = 0;

            Iterator<COINCOMOUnit> it = this.getListOfSubUnits().iterator();

            while (it.hasNext()) {
                sloc += it.next().getSLOC();
            }

            this.sloc = sloc;
        }

        return this.sloc;
    }

    public void setSLOC(long sloc) {
        if (this.sloc == sloc) {
            return;
        }
        this.sloc = sloc;
        this.setDirty();
    }

    /**
     * @return the total cost for the unit.
     */
    public double getCost() {
        if (this.isDirty()) {
            double cost = 0.0;

            Iterator<COINCOMOUnit> it = this.getListOfSubUnits().iterator();

            while (it.hasNext()) {
                cost += it.next().getCost();
            }

            this.cost = cost;
        }

        return this.cost;
    }

    public void setCost(double cost) {
        if (this.cost == cost) {
            return;
        }
        this.cost = cost;
        this.setDirty();
    }

    /**
     * @return the total staff for the unit.
     */
    public double getStaff() {
        if (this.isDirty()) {
            double staff = 0.0;

            Iterator<COINCOMOUnit> it = this.getListOfSubUnits().iterator();

            while (it.hasNext()) {
                staff += it.next().getStaff();
            }

            this.staff = staff;
        }

        return this.staff;
    }

    public void setStaff(double staff) {
        if (this.staff == staff) {
            return;
        }
        this.staff = staff;
        this.setDirty();
    }

    /**
     * @return the total effort for the unit.
     */
    public double getEffort() {
        if (this.isDirty()) {
            double effort = 0.0;

            Iterator<COINCOMOUnit> it = this.getListOfSubUnits().iterator();

            while (it.hasNext()) {
                effort += it.next().getEffort();
            }

            this.effort = effort;
        }

        return this.effort;
    }

    public void setEffort(double effort) {
        if (this.effort == effort) {
            return;
        }
        this.effort = effort;
        this.setDirty();
    }

    /**
     * @return the total schedule for the unit.
     */
    public double getSchedule() {
        if (this.isDirty()) {
            double schedule = 0.0;

            Iterator<COINCOMOUnit> it = this.getListOfSubUnits().iterator();

            while (it.hasNext()) {
                schedule += it.next().getSchedule();
            }

            this.schedule = schedule;
        }

        return this.schedule;
    }

    public void setSchedule(double schedule) {
        if (this.schedule == schedule) {
            return;
        }
        this.schedule = schedule;
        this.setDirty();
    }

    public int getNextAutoID() {
        return this.nextAutoID;
    }

    public void calculateNextAutoID(String defaultName, String name) {
        //System.out.println("defaultName: " + defaultName);
        //System.out.println("name: " + name);
        String defaultNamePattern = "^\\" + defaultName.substring(0, defaultName.length()-1) + "\\d+?\\" + defaultName.substring(defaultName.length()-1) + "$";
        Pattern pattern = Pattern.compile(defaultNamePattern);
        Matcher matcher = pattern.matcher(name);

        if (matcher.matches()) {
            String subName = name.substring(defaultName.length()-1, name.length()-1);
            int autoID = this.nextAutoID;

            //System.out.println("nextAutoID: " + nextAutoID);
            try {
                autoID = Integer.parseInt(subName);
            //System.out.println("autoID: " + autoID);
                if (autoID < this.nextAutoID) {
                } else {
                    autoID++;
                    this.nextAutoID = autoID;
                }
            } catch (NumberFormatException e) {
            }
        }
    }

    public void copyUnit(COINCOMOUnit unitToBeCopied, boolean recursive) {
        copyUnit(unitToBeCopied, true, recursive);
    }

    abstract public void copyUnit(COINCOMOUnit unitToBeCopied, boolean addCopyTag, boolean recursive);

    public int compareTo(COINCOMOUnit o) {
        if (this.unitID < o.unitID) {
            return -1;
        } else if (this.unitID == o.unitID) {
            return 0;
        } else {
            return 1;
        }
    }

    private static void log(Level level, String message) {
        Logger.getLogger(COINCOMOUnit.class.getName()).log(level, message);
    }
}
