/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package panels;

import core.COINCOMOComponent;
import java.awt.BorderLayout;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import main.GlobalMethods;

/**
 *
 * @author Raed Shomali
 */
public class COPSEMOPanel extends JPanel {

    private JSplitPane splitPane = new JSplitPane();
    private COPSEMOGraphPanel graphPanel = new COPSEMOGraphPanel();
    private COPSEMOParametersPanel parameters;
    protected static DecimalFormat format2Decimals = new DecimalFormat("0.00");
    private COINCOMOComponent component;

    public COPSEMOPanel() {
        parameters = new COPSEMOParametersPanel(this);

        // Split Pane
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

        splitPane.setTopComponent(new JScrollPane(parameters));
        splitPane.setBottomComponent(new JScrollPane(graphPanel));

        splitPane.setDividerLocation(180);

        // Refreshes Automatically As You Move the Divider
        splitPane.setContinuousLayout(true);

        // GUI
        this.setLayout(new BorderLayout());
        this.add(splitPane);

    }

    /**
     *
     * @param component used to update the COPSEMO Graph
     */
    public void updateCOPSEMO(COINCOMOComponent component) {
        this.component = component;

        graphPanel.setCOINCOMOComponent(component);

        parameters.setInceptionEffortPercentage(component.getInceptionEffortPercentage());
        parameters.setInceptionSchedulePercentage(component.getInceptionSchedulePercentage());
        parameters.setElaborationEffortPercentage(component.getElaborationEffortPercentage());
        parameters.setElaborationSchedulePercentage(component.getElaborationSchedulePercentage());
        parameters.setConstructionEffortPercentage(component.getConstructionEffortPercentage());
        parameters.setConstructionSchedulePercentage(component.getConstructionSchedulePercentage());
        parameters.setTransitionEffortPercentage(component.getTransitionEffortPercentage());
        parameters.setTransitionSchedulePercentage(component.getTransitionSchedulePercentage());

        parameters.setInceptionPM(format2Decimals.format(GlobalMethods.roundOff(component.getInceptionEffort(), 2)));
        parameters.setElaborationPM(format2Decimals.format(GlobalMethods.roundOff(component.getElaborationEffort(), 2)));
        parameters.setConstructionPM(format2Decimals.format(GlobalMethods.roundOff(component.getConstructionEffort(), 2)));
        parameters.setTransitionPM(format2Decimals.format(GlobalMethods.roundOff(component.getTransitionEffort(), 2)));
        parameters.setECTotalPM(format2Decimals.format(GlobalMethods.roundOff(component.getTotalEffortEC(), 2)));
        parameters.setTotalPM(format2Decimals.format(GlobalMethods.roundOff(component.getTotalEffort(), 2)));

        parameters.setInceptionM(format2Decimals.format(GlobalMethods.roundOff(component.getInceptionMonth(), 2)));
        parameters.setElaborationM(format2Decimals.format(GlobalMethods.roundOff(component.getElaborationMonth(), 2)));
        parameters.setConstructionM(format2Decimals.format(GlobalMethods.roundOff(component.getConstructionMonth(), 2)));
        parameters.setTransitionM(format2Decimals.format(GlobalMethods.roundOff(component.getTransitionMonth(), 2)));
        parameters.setECTotalM(format2Decimals.format(GlobalMethods.roundOff(component.getTotalMonthEC(), 2)));
        parameters.setTotalM(format2Decimals.format(GlobalMethods.roundOff(component.getTotalMonth(), 2)));

        parameters.setInceptionP(format2Decimals.format(GlobalMethods.roundOff(component.getInceptionPersonnel(), 2)));
        parameters.setElaborationP(format2Decimals.format(GlobalMethods.roundOff(component.getElaborationPersonnel(), 2)));
        parameters.setConstructionP(format2Decimals.format(GlobalMethods.roundOff(component.getConstructionPersonnel(), 2)));
        parameters.setTransitionP(format2Decimals.format(GlobalMethods.roundOff(component.getTransitionPersonnel(), 2)));
        parameters.setECTotalP(format2Decimals.format(GlobalMethods.roundOff(component.getTotalPersonnelEC(), 2))); 
        parameters.setTotalP(format2Decimals.format(GlobalMethods.roundOff(component.getTotalPersonnel(), 2))); 

        parameters.setECTotalEffort(format2Decimals.format(GlobalMethods.roundOff(component.getTotalEffortPercentageEC(), 2)));
        parameters.setECTotalSchedule(format2Decimals.format(GlobalMethods.roundOff(component.getTotalSchedulePercentageEC(), 2)));
        parameters.setTotalEffort(format2Decimals.format(GlobalMethods.roundOff(component.getTotalEffortPercentage(), 2)));
        parameters.setTotalSchedule(format2Decimals.format(GlobalMethods.roundOff(component.getTotalSchedulePercentage(), 2)));

        graphPanel.repaint();
    }

    public void updateCOPSEMO() {
        this.updateCOPSEMO(this.component);
    }

    public COPSEMOGraphPanel getCOPSEMOGraphPanel() {
        return graphPanel;
    }

    public COINCOMOComponent getComponent() {
        return this.component;
    }
}
