/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package core;

import core.COINCOMOConstants.LocalCalibrationMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.COINCOMO;

/**
 *
 * @author Larry Chen
 */
public class COINCOMOLocalCalibration extends COINCOMOUnit {

    private static LocalCalibrationMode calibrationMode = LocalCalibrationMode.COEFFICIENTS_ONLY;
    private COINCOMO coincomo = null;
    private ArrayList<COINCOMOSystem> systems;
    private ArrayList<COINCOMOComponent> components;
    private HashMap<COINCOMOComponent, COINCOMOLocalCalibrationProject> projects;
    private double effortCoefficient = 0.0d;
    private double effortExponent = 0.0d;
    private double scheduleCoefficient = 0.0d;
    private double scheduleExponent = 0.0d;

    public COINCOMOLocalCalibration(COINCOMO coincomo) {
        this.coincomo = coincomo;
        this.systems = new ArrayList<COINCOMOSystem>();
        this.components = new ArrayList<COINCOMOComponent>();
        this.projects = new HashMap<COINCOMOComponent, COINCOMOLocalCalibrationProject>();
    }

    public COINCOMOLocalCalibration() {
        this(null);
    }

    public static LocalCalibrationMode getCalibrationMode() {
        return COINCOMOLocalCalibration.calibrationMode;
    }

    public static void setCalibrationType(LocalCalibrationMode calibrationMode) {
        COINCOMOLocalCalibration.calibrationMode = calibrationMode;
    }

    public double getEffortCoefficient() {
        return this.effortCoefficient;
    }

    public double getEffortExponent() {
        return this.effortExponent;
    }

    public double getScheduleCoefficient() {
        return this.scheduleCoefficient;
    }

    public double getScheduleExponent() {
        return this.scheduleExponent;
    }

    public void setEffortCoefficient(double effortCoefficient) {
        if (this.effortCoefficient == effortCoefficient) {
            return;
        }
        this.effortCoefficient = effortCoefficient;
        this.setDirty();
    }

    public void setEffortExponent(double effortExponent) {
        if (this.effortExponent == effortExponent) {
            return;
        }
        this.effortExponent = effortExponent;
        this.setDirty();
    }

    public void setScheduleCoefficient(double scheduleCoefficient) {
        if (this.scheduleCoefficient == scheduleCoefficient) {
            return;
        }
        this.scheduleCoefficient = scheduleCoefficient;
        this.setDirty();
    }

    public void setScheduleExponent(double scheduleExponent) {
        if (this.scheduleExponent == scheduleExponent) {
            return;
        }
        this.scheduleExponent = scheduleExponent;
        this.setDirty();
    }

    private static void log(Level level, String message) {
        Logger.getLogger(COINCOMOLocalCalibration.class.getName()).log(level, message);
    }

    //TODO (Larry) What happens if the same COINCOMO project ia added again after deletion? The function will probably fail.
    @Override
    public void addSubUnit(COINCOMOUnit unit) {
        super.addSubUnit(unit);

        COINCOMOLocalCalibrationProject project = (COINCOMOLocalCalibrationProject) unit;
        COINCOMOComponent component = project.getComponent();
        COINCOMOSystem system = project.getParentSystem();

        if (!components.contains(component)) {
            components.add(component);
            projects.put(component, project);
            if (!systems.contains(system)) {
                systems.add(system);
            }
        }
    }

    @Override
    public void removeSubUnit(COINCOMOUnit unit) {
        super.removeSubUnit(unit);

        COINCOMOLocalCalibrationProject project = (COINCOMOLocalCalibrationProject) unit;
        COINCOMOComponent component = project.getComponent();
        COINCOMOSystem system = project.getParentSystem();

        project.setSelected(false);
        if (components.contains(component)) {
            components.remove(component);
            projects.remove(component);
            boolean hasOtherProjects = false;

            Iterator iter = components.iterator();
            while (iter.hasNext()) {
                COINCOMOComponent otherComponent = (COINCOMOComponent) iter.next();
                COINCOMOSystem otherSystem = (COINCOMOSystem) otherComponent.getParent().getParent();
                if (system == otherSystem) {
                    hasOtherProjects = true;
                    break;
                }
            }

            if (!hasOtherProjects) {
                systems.remove(system);
            }
        }
    }

    public ArrayList<COINCOMOSystem> getListOfSystemUnits() {
        return this.systems;
    }

    public COINCOMOLocalCalibrationProject getProject(COINCOMOComponent component) {
        if (projects.containsKey(component)) {
            return projects.get(component);
        } else {
            return null;
        }
    }

    @Override
    public void copyUnit(COINCOMOUnit unitToBeCopied, boolean addCopyTag, boolean recursive) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}