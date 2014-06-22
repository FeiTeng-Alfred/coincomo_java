/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package panels;

import database.COINCOMOComponentManager;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Raed Shomali
 */
public class COPSEMOParametersPanel extends JPanel implements ChangeListener {

    private UpdateCOPSEMOGraph update = null;
    private COPSEMOPanel copsemoPanel = null;
    // Spinners Models (Value , Min , Max , Step)
    // Changed the step for spinner from 1 to 0.5
    private SpinnerModel spinnerModel1 = new SpinnerNumberModel(6.0, 2.0, 15.0, 0.5);
    private SpinnerModel spinnerModel2 = new SpinnerNumberModel(24.0, 20.0, 28.0, 0.5);
    private SpinnerModel spinnerModel3 = new SpinnerNumberModel(76.0, 72.0, 80.0, 0.5);
    private SpinnerModel spinnerModel4 = new SpinnerNumberModel(12.0, 0.0, 20.0, 0.5);
    private SpinnerModel spinnerModel5 = new SpinnerNumberModel(12.5, 2.0, 30.0, 0.5);
    private SpinnerModel spinnerModel6 = new SpinnerNumberModel(37.5, 33.0, 42.0, 0.5);
    private SpinnerModel spinnerModel7 = new SpinnerNumberModel(62.5, 58.0, 67.0, 0.5);
    private SpinnerModel spinnerModel8 = new SpinnerNumberModel(12.5, 0.0, 20.0, 0.5);
    // Spinners
    private JSpinner inceptionEffortSpinner = new JSpinner(spinnerModel1);
    private JSpinner elaborationEffortSpinner = new JSpinner(spinnerModel2);
    private JSpinner constructionEffortSpinner = new JSpinner(spinnerModel3);
    private JSpinner transitionEffortSpinner = new JSpinner(spinnerModel4);
    private JSpinner inceptionScheduleSpinner = new JSpinner(spinnerModel5);
    private JSpinner elaborationScheduleSpinner = new JSpinner(spinnerModel6);
    private JSpinner constructionScheduleSpinner = new JSpinner(spinnerModel7);
    private JSpinner transitionScheduleSpinner = new JSpinner(spinnerModel8);
    // Labels
    private JLabel inceptionLabel = new JLabel("Inception");
    private JLabel elaborationLabel = new JLabel("Elaboration");
    private JLabel constructionLabel = new JLabel("Construction");
    private JLabel transitionLabel = new JLabel("Transition");
    private JLabel totalECLabel = new JLabel("Total E&C");
    private JLabel totalLabel = new JLabel("Total");
    private JLabel ECTotalEffort = new JLabel("");
    private JLabel totalEffort = new JLabel("");
    private JLabel ECTotalSchedule = new JLabel("");
    private JLabel totalSchedule = new JLabel("");
    private JLabel inceptionPMLabel = new JLabel("19");
    private JLabel elaborationPMLabel = new JLabel("20");
    private JLabel constructionPMLabel = new JLabel("21");
    private JLabel transitionPMLabel = new JLabel("22");
    private JLabel ECTotalPMLabel = new JLabel("23");
    private JLabel totalPMLabel = new JLabel("24");
    private JLabel inceptionMLabel = new JLabel("25");
    private JLabel elaborationScheduleLabel = new JLabel("26");
    private JLabel constructionMLabel = new JLabel("27");
    private JLabel transitionMLabel = new JLabel("28");
    private JLabel ECTotalMLabel = new JLabel("29");
    private JLabel totalScheduleMLabel = new JLabel("30");
    private JLabel inceptionPLabel = new JLabel("31");
    private JLabel elaborationPLabel = new JLabel("32");
    private JLabel constructionPLabel = new JLabel("33");
    private JLabel transitionPLabel = new JLabel("34");
    private JLabel ECTotalPLabel = new JLabel("35");
    private JLabel totalPLabel = new JLabel("36");
    private JLabel effortLabel = new JLabel("Effort %");
    private JLabel scheduleLabel = new JLabel("Schedule %");
    private JLabel PMLabel = new JLabel("PM");
    private JLabel MLabel = new JLabel("M");
    private JLabel PLabel = new JLabel("P = PM / M");

//    private JLabel warningLabel = new JLabel("*COPSEMO values are calculated with the assumption that Person Month is set to 152.0.");

    /**
     * Constructor
     */
    public COPSEMOParametersPanel(COPSEMOPanel cPanel) {
        this.copsemoPanel = cPanel;

        // Spinners
        inceptionEffortSpinner.setEditor(new JSpinner.NumberEditor(inceptionEffortSpinner, "#0.0"));
        inceptionEffortSpinner.addChangeListener(this);
        transitionEffortSpinner.setEditor(new JSpinner.NumberEditor(transitionEffortSpinner, "#0.0"));
        transitionEffortSpinner.addChangeListener(this);
        inceptionScheduleSpinner.setEditor(new JSpinner.NumberEditor(inceptionScheduleSpinner, "#0.0"));
        inceptionScheduleSpinner.addChangeListener(this);
        transitionScheduleSpinner.setEditor(new JSpinner.NumberEditor(transitionScheduleSpinner, "#0.0"));
        transitionScheduleSpinner.addChangeListener(this);
        elaborationEffortSpinner.setEditor(new JSpinner.NumberEditor(elaborationEffortSpinner, "#0.0"));
        elaborationEffortSpinner.addChangeListener(this);
        constructionEffortSpinner.setEditor(new JSpinner.NumberEditor(constructionEffortSpinner, "#0.0"));
        constructionEffortSpinner.addChangeListener(this);
        elaborationScheduleSpinner.setEditor(new JSpinner.NumberEditor(elaborationScheduleSpinner, "#0.0"));
        elaborationScheduleSpinner.addChangeListener(this);
        constructionScheduleSpinner.setEditor(new JSpinner.NumberEditor(constructionScheduleSpinner, "#0.0"));
        constructionScheduleSpinner.addChangeListener(this);

        // Labels
        inceptionPMLabel.setHorizontalAlignment(JLabel.CENTER);
        elaborationPMLabel.setHorizontalAlignment(JLabel.CENTER);
        constructionPMLabel.setHorizontalAlignment(JLabel.CENTER);
        transitionPMLabel.setHorizontalAlignment(JLabel.CENTER);
        ECTotalPMLabel.setHorizontalAlignment(JLabel.CENTER);
        totalPMLabel.setHorizontalAlignment(JLabel.CENTER);
        inceptionMLabel.setHorizontalAlignment(JLabel.CENTER);
        elaborationScheduleLabel.setHorizontalAlignment(JLabel.CENTER);
        constructionMLabel.setHorizontalAlignment(JLabel.CENTER);
        transitionMLabel.setHorizontalAlignment(JLabel.CENTER);
        ECTotalMLabel.setHorizontalAlignment(JLabel.CENTER);
        totalScheduleMLabel.setHorizontalAlignment(JLabel.CENTER);
        inceptionPLabel.setHorizontalAlignment(JLabel.CENTER);
        elaborationPLabel.setHorizontalAlignment(JLabel.CENTER);
        constructionPLabel.setHorizontalAlignment(JLabel.CENTER);
        transitionPLabel.setHorizontalAlignment(JLabel.CENTER);
        ECTotalPLabel.setHorizontalAlignment(JLabel.CENTER);
        totalPLabel.setHorizontalAlignment(JLabel.CENTER);

        ECTotalEffort.setHorizontalAlignment(JLabel.CENTER);
        ECTotalSchedule.setHorizontalAlignment(JLabel.CENTER);
        totalEffort.setHorizontalAlignment(JLabel.CENTER);
        totalSchedule.setHorizontalAlignment(JLabel.CENTER);

        PMLabel.setHorizontalAlignment(JLabel.CENTER);
        MLabel.setHorizontalAlignment(JLabel.CENTER);
        PLabel.setHorizontalAlignment(JLabel.CENTER);

//        warningLabel.setHorizontalAlignment(JLabel.LEFT);

        // GUI
        this.setLayout(null);

        this.add(inceptionEffortSpinner);
        this.add(elaborationEffortSpinner);
        this.add(constructionEffortSpinner);
        this.add(transitionEffortSpinner);
        this.add(inceptionScheduleSpinner);
        this.add(elaborationScheduleSpinner);
        this.add(constructionScheduleSpinner);
        this.add(transitionScheduleSpinner);

        this.add(inceptionLabel);
        this.add(elaborationLabel);
        this.add(constructionLabel);
        this.add(transitionLabel);
        this.add(totalECLabel);
        this.add(totalLabel);

        this.add(ECTotalEffort);
        this.add(totalEffort);

        this.add(ECTotalSchedule);
        this.add(totalSchedule);
        this.add(inceptionPMLabel);
        this.add(elaborationPMLabel);
        this.add(constructionPMLabel);
        this.add(transitionPMLabel);
        this.add(ECTotalPMLabel);
        this.add(totalPMLabel);
        this.add(inceptionMLabel);
        this.add(elaborationScheduleLabel);
        this.add(constructionMLabel);
        this.add(transitionMLabel);
        this.add(ECTotalMLabel);
        this.add(totalScheduleMLabel);
        this.add(inceptionPLabel);
        this.add(elaborationPLabel);
        this.add(constructionPLabel);
        this.add(transitionPLabel);
        this.add(ECTotalPLabel);
        this.add(totalPLabel);

        this.add(effortLabel);
        this.add(scheduleLabel);

        this.add(PMLabel);
        this.add(MLabel);
        this.add(PLabel);

//        this.add(warningLabel);

        inceptionLabel.setBounds(100, 20, 90, 22);
        elaborationLabel.setBounds(190, 20, 90, 22);
        constructionLabel.setBounds(280, 20, 90, 22);
        transitionLabel.setBounds(370, 20, 90, 22);
        totalECLabel.setBounds(460, 20, 90, 22);
        totalLabel.setBounds(550, 20, 90, 22);

        inceptionEffortSpinner.setBounds(100, 45, 75, 22);
        elaborationEffortSpinner.setBounds(190, 45, 75, 22);
        constructionEffortSpinner.setBounds(280, 45, 75, 22);
        transitionEffortSpinner.setBounds(370, 45, 75, 22);
        ECTotalEffort.setBounds(460, 45, 60, 22);
        totalEffort.setBounds(540, 45, 50, 22);

        inceptionScheduleSpinner.setBounds(100, 70, 75, 22);
        elaborationScheduleSpinner.setBounds(190, 70, 75, 22);
        constructionScheduleSpinner.setBounds(280, 70, 75, 22);
        transitionScheduleSpinner.setBounds(370, 70, 75, 22);
        ECTotalSchedule.setBounds(460, 70, 60, 22);
        totalSchedule.setBounds(540, 70, 50, 22);

        inceptionPMLabel.setBounds(100, 95, 70, 22);
        elaborationPMLabel.setBounds(190, 95, 70, 22);
        constructionPMLabel.setBounds(280, 95, 70, 22);
        transitionPMLabel.setBounds(370, 95, 70, 22);
        ECTotalPMLabel.setBounds(460, 95, 60, 22);
        totalPMLabel.setBounds(540, 95, 50, 22);

        inceptionMLabel.setBounds(100, 120, 70, 22);
        elaborationScheduleLabel.setBounds(190, 120, 70, 22);
        constructionMLabel.setBounds(280, 120, 70, 22);
        transitionMLabel.setBounds(370, 120, 70, 22);
        ECTotalMLabel.setBounds(460, 120, 60, 22);
        totalScheduleMLabel.setBounds(540, 120, 50, 22);

        inceptionPLabel.setBounds(100, 145, 70, 22);
        elaborationPLabel.setBounds(190, 145, 70, 22);
        constructionPLabel.setBounds(280, 145, 70, 22);
        transitionPLabel.setBounds(370, 145, 70, 22);
        ECTotalPLabel.setBounds(460, 145, 60, 22);
        totalPLabel.setBounds(540, 145, 50, 22);

        effortLabel.setBounds(10, 45, 90, 22);
        scheduleLabel.setBounds(10, 70, 90, 22);
        PMLabel.setBounds(10, 95, 50, 22);
        MLabel.setBounds(10, 120, 50, 22);
        PLabel.setBounds(10, 145, 58, 22);

        ECTotalPLabel.setVisible(false);
        totalPLabel.setVisible(false);

//        warningLabel.setForeground(Color.red);
//        warningLabel.setBounds(10, 0, 600, 20);
    }

    public void stateChanged(ChangeEvent e) {
        // Update Other Spinners As Current Spinner Being Updated ..

        if (e.getSource() == elaborationEffortSpinner) {
            constructionEffortSpinner.setValue(100.0 - Double.parseDouble(elaborationEffortSpinner.getValue() + ""));

            this.copsemoPanel.getComponent().setElaborationEffortPercentage(Double.parseDouble(elaborationEffortSpinner.getValue() + ""));
            this.copsemoPanel.getComponent().setConstructionEffortPercentage(100.0 - Double.parseDouble(elaborationEffortSpinner.getValue() + ""));
        } else if (e.getSource() == constructionEffortSpinner) {
            elaborationEffortSpinner.setValue(100.0 - Double.parseDouble(constructionEffortSpinner.getValue() + ""));

            this.copsemoPanel.getComponent().setElaborationEffortPercentage(Double.parseDouble(elaborationEffortSpinner.getValue() + ""));
            this.copsemoPanel.getComponent().setConstructionEffortPercentage(100.0 - Double.parseDouble(elaborationEffortSpinner.getValue() + ""));
        } else if (e.getSource() == elaborationScheduleSpinner) {
            constructionScheduleSpinner.setValue(100.0 - Double.parseDouble(elaborationScheduleSpinner.getValue() + ""));

            this.copsemoPanel.getComponent().setElaborationSchedulePercentage(Double.parseDouble(elaborationScheduleSpinner.getValue() + ""));
            this.copsemoPanel.getComponent().setConstructionSchedulePercentage(100.0 - Double.parseDouble(elaborationScheduleSpinner.getValue() + ""));
        } else if (e.getSource() == constructionScheduleSpinner) {
            elaborationScheduleSpinner.setValue(100.0 - Double.parseDouble(constructionScheduleSpinner.getValue() + ""));

            this.copsemoPanel.getComponent().setElaborationSchedulePercentage(Double.parseDouble(elaborationScheduleSpinner.getValue() + ""));
            this.copsemoPanel.getComponent().setConstructionSchedulePercentage(100.0 - Double.parseDouble(elaborationScheduleSpinner.getValue() + ""));
        }

        this.copsemoPanel.getComponent().setInceptionEffortPercentage(Double.parseDouble(inceptionEffortSpinner.getValue() + ""));
        this.copsemoPanel.getComponent().setInceptionSchedulePercentage(Double.parseDouble(inceptionScheduleSpinner.getValue() + ""));
        this.copsemoPanel.getComponent().setTransitionEffortPercentage(Double.parseDouble(transitionEffortSpinner.getValue() + ""));
        this.copsemoPanel.getComponent().setTransitionSchedulePercentage(Double.parseDouble(transitionScheduleSpinner.getValue() + ""));

        // Alreadt Exists ?
        if (update != null) {
            // Kill It
            update.interrupt();
        }

        // Create a New Thread
        update = new UpdateCOPSEMOGraph(this.copsemoPanel);
        update.start();
    }

    /*static*/ class UpdateCOPSEMOGraph extends Thread {

        private COPSEMOPanel copsemoPanel = null;

        public UpdateCOPSEMOGraph(COPSEMOPanel cPanel) {
            this.copsemoPanel = cPanel;
            // Lowest Priority ..
            // So the User Can Play With the System..
            this.setPriority(1);
        }

        @Override
        public void run() {
            // Show the Loading Text ...
            this.copsemoPanel.getCOPSEMOGraphPanel().setIsLoading(true);
            this.copsemoPanel.getCOPSEMOGraphPanel().repaint();
            //GlobalMethods.updateStatusBar( "Loading COPSEMO ...", copsemoPanel );

            // Update the Graph's Parameters ..
            COINCOMOComponentManager.updateComponentCOPSEMO(this.copsemoPanel.getComponent());
            copsemoPanel.updateCOPSEMO(this.copsemoPanel.getComponent());

            // Show the Graph ..
            this.copsemoPanel.getCOPSEMOGraphPanel().setIsLoading(false);
            this.copsemoPanel.getCOPSEMOGraphPanel().repaint();
            //GlobalMethods.updateStatusBar( "COPSEMO Loaded." );
        }
    }

    /**
     *
     * @param value sets the Inception's Effort's Spinner
     */
    public void setInceptionEffortPercentage(double value) {
        inceptionEffortSpinner.setValue(value);
    }

    /**
     *
     * @param value sets the Inception's Schedule's Spinner
     */
    public void setInceptionSchedulePercentage(double value) {
        inceptionScheduleSpinner.setValue(value);
    }

    /**
     *
     * @param value sets the Elaboration's Effort's Spinner
     */
    public void setElaborationEffortPercentage(double value) {
        elaborationEffortSpinner.setValue(value);
    }

    /**
     *
     * @param value sets the Elaboration's Schedule's Spinner
     */
    public void setElaborationSchedulePercentage(double value) {
        elaborationScheduleSpinner.setValue(value);
    }

    /**
     *
     * @param value sets the Construction's Effort's Spinner
     */
    public void setConstructionEffortPercentage(double value) {
        constructionEffortSpinner.setValue(value);
    }

    /**
     *
     * @param value sets the Construction's Schedule's Spinner
     */
    public void setConstructionSchedulePercentage(double value) {
        constructionScheduleSpinner.setValue(value);
    }

    /**
     *
     * @param value sets the Transition's Effort's Spinner
     */
    public void setTransitionEffortPercentage(double value) {
        transitionEffortSpinner.setValue(value);
    }

    /**
     *
     * @param value sets the Transition's Schedule's Spinner
     */
    public void setTransitionSchedulePercentage(double value) {
        transitionScheduleSpinner.setValue(value);
    }

    /**
     *
     * @param value sets the Inception's Effort's Label
     */
    public void setInceptionPM(String value) {
        inceptionPMLabel.setText(value);
    }

    /**
     *
     * @param value sets the Elaboration's Effort Label
     */
    public void setElaborationPM(String value) {
        elaborationPMLabel.setText(value);
    }

    /**
     *
     * @param value sets the Construction's Effort Label
     */
    public void setConstructionPM(String value) {
        constructionPMLabel.setText(value);
    }

    /**
     *
     * @param value sets the Transition's Effort Label
     */
    public void setTransitionPM(String value) {
        transitionPMLabel.setText(value);
    }

    /**
     *
     * @param value sets the EC Total's Effort Label
     */
    public void setECTotalPM(String value) {
        ECTotalPMLabel.setText(value);
    }

    /**
     *
     * @param value sets the Total Effort Label
     */
    public void setTotalPM(String value) {
        totalPMLabel.setText(value);
    }

    /**
     *
     * @param value sets the Inception's Schedule Label
     */
    public void setInceptionM(String value) {
        inceptionMLabel.setText(value);
    }

    /**
     *
     * @param value sets the Elaboration's Schedule's Label
     */
    public void setElaborationM(String value) {
        elaborationScheduleLabel.setText(value);
    }

    /**
     *
     * @param value sets the Construction's Schedule Label
     */
    public void setConstructionM(String value) {
        constructionMLabel.setText(value);
    }

    /**
     *
     * @param value sets the Transition's Schedule Label
     */
    public void setTransitionM(String value) {
        transitionMLabel.setText(value);
    }

    /**
     *
     * @param value sets EC Total's Schedule's Label
     */
    public void setECTotalM(String value) {
        ECTotalMLabel.setText(value);
    }

    /**
     *
     * @param value sets the Total Schedule Label
     */
    public void setTotalM(String value) {
        totalScheduleMLabel.setText(value);
    }

    /**
     *
     * @param value sets Inception Personnel Label
     */
    public void setInceptionP(String value) {
        inceptionPLabel.setText(value);
    }

    /**
     *
     * @param value sets elaboration Personnel Label
     */
    public void setElaborationP(String value) {
        elaborationPLabel.setText(value);
    }

    /**
     *
     * @param value sets Construction Personnel Label
     */
    public void setConstructionP(String value) {
        constructionPLabel.setText(value);
    }

    /**
     *
     * @param value sets Transition Personnel Label
     */
    public void setTransitionP(String value) {
        transitionPLabel.setText(value);
    }

    /**
     *
     * @param value sets EC Total Personnel Label
     */
    public void setECTotalP(String value) {
        ECTotalPLabel.setText(value);
    }

    /**
     *
     * @param value sets Total Personnel Label
     */
    public void setTotalP(String value) {
        totalPLabel.setText(value);
    }

    /**
     *
     * @param value sets EC Total Effort Label
     */
    public void setECTotalEffort(String value) {
        ECTotalEffort.setText(value);
    }

    /**
     *
     * @param value sets EC Total Schedule Label
     */
    public void setECTotalSchedule(String value) {
        ECTotalSchedule.setText(value);
    }

    /**
     *
     * @param value sets Total Effort Label
     */
    public void setTotalEffort(String value) {
        totalEffort.setText(value);
    }

    /**
     *
     * @param value sets Total Schedule Label
     */
    public void setTotalSchedule(String value) {
        totalSchedule.setText(value);
    }
}
