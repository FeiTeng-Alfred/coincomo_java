/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package dialogs;

import core.COINCOMOAdaptationAndReuse;
import database.COINCOMOAdaptationAndReuseManager;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import main.GlobalMethods;
import main.Icons;

/**
 *
 * @author Abhishek
 */
public class SubComponentAdaptationReuseDialog extends JDialog implements ActionListener {

    // Labels and Text Fields
    private JTextField adaptedSLOCTextField = new JTextField();
    private JLabel adaptedSLOCLabel = new JLabel("Adapted (Initial) SLOC:");
    private JLabel DMLabel = new JLabel("% Design Modified (DM):");
    private JLabel CMLabel = new JLabel("% Code Modified (CM):");
    private JLabel IMLabel = new JLabel("% Integration Modified (IM):");
    private JLabel SULabel = new JLabel("Software Understanding:");
    private JLabel AALabel = new JLabel("Assessment & Assimilation:");
    private JLabel UNFMLabel = new JLabel("Unfamiliarity With Software:");
    private JLabel ATLabel = new JLabel("Components Automatically Translated:");
    private JLabel ATPRODLabel = new JLabel("Automatic Translation Productivity Level:");
    private JLabel AAFLabel = new JLabel("Computed Adaptation Adjustment Factor:");
    private JLabel equivalentSLOCLabel = new JLabel("Equivalent SLOC:");
    private JLabel DMLabel2 = new JLabel("%");
    private JLabel CMLabel2 = new JLabel("%");
    private JLabel IMLabel2 = new JLabel("%");
    private JLabel SULabel2 = new JLabel("(0 - 50%) SU");
    private JLabel AALabel2 = new JLabel("(0 - 8%) AA");
    private JLabel UNFMLabel2 = new JLabel("(0 - 1) UNFM");
    private JLabel ATLabel2 = new JLabel("%");
    private JLabel ATPRODLabel2 = new JLabel("");
    private JLabel AAFValueLabel = new JLabel("?");
    private JLabel EquivalentSLOCValueLabel = new JLabel("?");
    private JTextField adaptationTextField[] = new JTextField[8];
    // Buttons
    private JButton applyButton = new JButton("Apply");
    private JButton resetButton = new JButton("Reset");
    private JButton closeButton = new JButton("Close");
    private COINCOMOAdaptationAndReuse adaptation = null;
    // Reference to SubComponentSizeDialog
    private SubComponentSizeDialog owner = null;

    public SubComponentAdaptationReuseDialog(SubComponentSizeDialog owner, COINCOMOAdaptationAndReuse adaptation) {
        super(owner);

        this.setModal(true);

        this.setTitle("Adaptation and Reuse - " + adaptation.getName());

        this.adaptation = adaptation;

        this.owner = owner;

        // Buttons
        applyButton.addActionListener(this);
        resetButton.addActionListener(this);
        closeButton.addActionListener(this);

        applyButton.setFocusable(true);
        resetButton.setFocusable(true);
        closeButton.setFocusable(true);

        applyButton.setIcon(Icons.SAVE_ICON);
        resetButton.setIcon(Icons.RESET_ICON);
        closeButton.setIcon(Icons.CLOSE_ICON);

        //Adaption and Reuse Panel
        JPanel adaptationPanel = new JPanel(null);
        adaptationPanel.add(adaptedSLOCLabel);
        adaptationPanel.add(DMLabel);
        adaptationPanel.add(CMLabel);
        adaptationPanel.add(IMLabel);
        adaptationPanel.add(SULabel);
        adaptationPanel.add(AALabel);
        adaptationPanel.add(UNFMLabel);
        adaptationPanel.add(ATLabel);
        adaptationPanel.add(ATPRODLabel);
        adaptationPanel.add(AAFLabel);
        adaptationPanel.add(equivalentSLOCLabel);

        adaptationPanel.add(DMLabel2);
        adaptationPanel.add(CMLabel2);
        adaptationPanel.add(IMLabel2);
        adaptationPanel.add(SULabel2);
        adaptationPanel.add(AALabel2);
        adaptationPanel.add(UNFMLabel2);
        adaptationPanel.add(ATLabel2);
        adaptationPanel.add(ATPRODLabel2);
        adaptationPanel.add(AAFValueLabel);

        adaptationPanel.add(EquivalentSLOCValueLabel);
        adaptationPanel.add(adaptedSLOCTextField);

        adaptedSLOCLabel.setFont(new Font("arial", 1, 11));
        DMLabel.setFont(new Font("arial", 1, 11));
        CMLabel.setFont(new Font("arial", 1, 11));
        IMLabel.setFont(new Font("arial", 1, 11));
        SULabel.setFont(new Font("arial", 1, 11));
        AALabel.setFont(new Font("arial", 1, 11));
        UNFMLabel.setFont(new Font("arial", 1, 11));
        ATLabel.setFont(new Font("arial", 1, 11));
        ATPRODLabel.setFont(new Font("arial", 1, 11));
        AAFLabel.setFont(new Font("arial", 1, 11));
        equivalentSLOCLabel.setFont(new Font("arial", 1, 11));
        DMLabel2.setFont(new Font("arial", 1, 11));
        CMLabel2.setFont(new Font("arial", 1, 11));
        IMLabel2.setFont(new Font("arial", 1, 11));
        SULabel2.setFont(new Font("arial", 1, 11));
        AALabel2.setFont(new Font("arial", 1, 11));
        UNFMLabel2.setFont(new Font("arial", 1, 11));
        ATLabel2.setFont(new Font("arial", 1, 11));
        ATPRODLabel2.setFont(new Font("arial", 1, 11));
        AAFValueLabel.setFont(new Font("arial", 1, 11));
        EquivalentSLOCValueLabel.setFont(new Font("arial", 1, 11));

        adaptedSLOCLabel.setBounds(40, 20, 300, 20);
        DMLabel.setBounds(40, 40, 300, 20);
        CMLabel.setBounds(40, 60, 300, 20);
        IMLabel.setBounds(40, 80, 300, 20);
        SULabel.setBounds(40, 100, 300, 20);
        AALabel.setBounds(40, 120, 300, 20);
        UNFMLabel.setBounds(40, 140, 300, 20);
        ATLabel.setBounds(40, 160, 300, 20);
        ATPRODLabel.setBounds(40, 180, 300, 20);
        AAFLabel.setBounds(40, 210, 300, 20);
        equivalentSLOCLabel.setBounds(40, 230, 300, 20);
        DMLabel2.setBounds(340, 40, 50, 20);
        CMLabel2.setBounds(340, 60, 50, 20);
        IMLabel2.setBounds(340, 80, 50, 20);
        SULabel2.setBounds(340, 100, 150, 20);
        AALabel2.setBounds(340, 120, 150, 20);
        UNFMLabel2.setBounds(340, 140, 150, 20);
        ATLabel2.setBounds(340, 160, 50, 20);
        ATPRODLabel2.setBounds(340, 180, 50, 20);
        AAFValueLabel.setBounds(285, 210, 50, 20);
        EquivalentSLOCValueLabel.setBounds(285, 230, 50, 20);

        adaptedSLOCTextField.setBounds(280, 20, 50, 20);

        for (int i = 0; i < adaptationTextField.length; i++) {
            adaptationTextField[i] = new JTextField();

            adaptationPanel.add(adaptationTextField[i]);

            adaptationTextField[i].setBounds(280, 40 + 20 * i, 50, 20);
        }

        //   JPanel southPanel = new JPanel();
        applyButton.setBounds(70, 270, 100, 30);
        resetButton.setBounds(175, 270, 100, 30);
        closeButton.setBounds(280, 270, 100, 30);
        adaptationPanel.add(applyButton);
        adaptationPanel.add(resetButton);
        adaptationPanel.add(closeButton);

        //this.setLayout(new BorderLayout());
        this.add(adaptationPanel);

        adaptationTextField[0].addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        adaptationTextField[0].selectAll();
                    }
                });
            }
        });

        adaptationTextField[1].addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        adaptationTextField[1].selectAll();
                    }
                });
            }
        });

        adaptationTextField[2].addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        adaptationTextField[2].selectAll();
                    }
                });
            }
        });

        adaptationTextField[3].addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        adaptationTextField[3].selectAll();
                    }
                });
            }
        });

        adaptationTextField[4].addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        adaptationTextField[4].selectAll();
                    }
                });
            }
        });

        adaptationTextField[5].addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        adaptationTextField[5].selectAll();
                    }
                });
            }
        });

        adaptationTextField[6].addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        adaptationTextField[6].selectAll();
                    }
                });
            }
        });

        adaptationTextField[7].addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        adaptationTextField[7].selectAll();
                    }
                });
            }
        });

        // Loading ...
        loadParameters();

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(this.getOwner().getX() + 100, this.getOwner().getY() + 100);
        this.setResizable(false);
        this.setSize(450, 350);
        this.setVisible(true);
    }

    public void loadParameters() {
        applyButton.setEnabled(false);
        resetButton.setEnabled(false);
        closeButton.setEnabled(false);

        adaptedSLOCTextField.setText("" + GlobalMethods.FormatLongWithComma(adaptation.getAdaptedSLOC()));

        adaptationTextField[0].setText(adaptation.getDesignModified() + "");
        adaptationTextField[1].setText(adaptation.getCodeModified() + "");
        adaptationTextField[2].setText(adaptation.getIntegrationModified() + "");
        adaptationTextField[3].setText(adaptation.getSoftwareUnderstanding() + "");
        adaptationTextField[4].setText(adaptation.getAssessmentAndAssimilation() + "");
        adaptationTextField[5].setText(adaptation.getUnfamiliarityWithSoftware() + "");
        adaptationTextField[6].setText(adaptation.getAutomaticallyTranslated() + "");
        adaptationTextField[7].setText(adaptation.getAutomaticTranslationProductivity() + "");

        AAFValueLabel.setText("" + adaptation.getAdaptationAdjustmentFactor());
        EquivalentSLOCValueLabel.setText("" + GlobalMethods.FormatLongWithComma(adaptation.getEquivalentSLOC()));

        // Set Back To Default ...
        applyButton.setEnabled(true);
        resetButton.setEnabled(true);
        closeButton.setEnabled(true);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == applyButton) {
            // Validations

            if (!GlobalMethods.isNonNegativeLong(adaptedSLOCTextField.getText())) {
                //GlobalMethods.updateStatusBar("Initial SLOC Must be a Non Negative Integer Value.", Color.RED, (COINCOMO)this.getParent());
                JOptionPane.showMessageDialog(this.getParent(), "Initial SLOC Must be a Non Negative Integer Value.", "VALUE ERROR", 0);
                return;
            }

            // Adaptation and Reuse
            if (!GlobalMethods.isNonNegativeFloat(adaptationTextField[0].getText()) || Double.parseDouble(adaptationTextField[0].getText()) < 0 || Double.parseDouble(adaptationTextField[0].getText()) > 100) {
                //GlobalMethods.updateStatusBar("Design Modified Must be a value between 0 and 100", Color.RED, (COINCOMO)this.getParent());
                JOptionPane.showMessageDialog(this.getParent(), "Design Modified Must be a value between 0 and 100", "VALUE ERROR", 0);
                return;
            }

            if (!GlobalMethods.isNonNegativeFloat(adaptationTextField[1].getText()) || Double.parseDouble(adaptationTextField[1].getText()) < 0 || Double.parseDouble(adaptationTextField[1].getText()) > 100) {
                //GlobalMethods.updateStatusBar("Code Modified Must be a value between 0 and 100", Color.RED, (COINCOMO)this.getParent());
                JOptionPane.showMessageDialog(this.getParent(), "Code Modified Must be a value between 0 and 100", "VALUE ERROR", 0);
                return;
            }

            if (!GlobalMethods.isNonNegativeFloat(adaptationTextField[2].getText()) || Double.parseDouble(adaptationTextField[2].getText()) < 0 || Double.parseDouble(adaptationTextField[2].getText()) > 100) {
                //GlobalMethods.updateStatusBar("Integration Modified Must be a value between 0 and 100", Color.RED, (COINCOMO)this.getParent());
                JOptionPane.showMessageDialog(this.getParent(), "Integration Modified Must be a value between 0 and 100", "VALUE ERROR", 0);
                return;
            }

            if (!GlobalMethods.isNonNegativeFloat(adaptationTextField[3].getText()) || Double.parseDouble(adaptationTextField[3].getText()) < 0 || Double.parseDouble(adaptationTextField[3].getText()) > 50) {
                //GlobalMethods.updateStatusBar("Software Understanding Must be a value between 0 and 50", Color.RED, (COINCOMO)this.getParent());
                JOptionPane.showMessageDialog(this.getParent(), "Software Understanding Must be a value between 0 and 50", "VALUE ERROR", 0);
                return;
            }

            if (!GlobalMethods.isNonNegativeFloat(adaptationTextField[4].getText()) || Double.parseDouble(adaptationTextField[4].getText()) < 0 || Double.parseDouble(adaptationTextField[4].getText()) > 8) {
                //GlobalMethods.updateStatusBar("Assement & Assimilation Must be a value between 0 and 8", Color.RED, (COINCOMO)this.getParent());
                JOptionPane.showMessageDialog(this.getParent(), "Assement & Assimilation Must be a value between 0 and 8", "VALUE ERROR", 0);
                return;
            }

            if (!GlobalMethods.isNonNegativeFloat(adaptationTextField[5].getText()) || (Float.parseFloat(adaptationTextField[5].getText()) < 0 || Double.parseDouble(adaptationTextField[5].getText()) > 1)) {
                //GlobalMethods.updateStatusBar("Unfamiliarity Must be a value between 0 and 1", Color.RED, (COINCOMO)this.getParent());
                JOptionPane.showMessageDialog(this.getParent(), "Unfamiliarity Must be a value between 0 and 1", "VALUE ERROR", 0);
                return;
            }

            if (!GlobalMethods.isNonNegativeFloat(adaptationTextField[6].getText()) || Double.parseDouble(adaptationTextField[6].getText()) < 0 || Double.parseDouble(adaptationTextField[6].getText()) > 100) {
                //GlobalMethods.updateStatusBar("Automaticall Translated Components Must be a value between 0 and 100", Color.RED, (COINCOMO)this.getParent());
                JOptionPane.showMessageDialog(this.getParent(), "Automatically Translated Components Must be a value between 0 and 100", "VALUE ERROR", 0);
                return;
            }


            applyButton.setEnabled(false);
            resetButton.setEnabled(false);
            closeButton.setEnabled(false);

            //    GlobalMethods.updateStatusBar("Saving ...", (COINCOMO)this.getParent().getParent());
            //    componentOverviewPanel.getEstimationTextPane().setText("Loading ...");

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {

                    // Adaptation & Reuse
                    adaptation.setAdaptedSLOC(GlobalMethods.ParseLongWithComma(adaptedSLOCTextField.getText()));
                    adaptation.setDesignModified(Double.parseDouble(adaptationTextField[0].getText()));
                    adaptation.setCodeModified(Double.parseDouble(adaptationTextField[1].getText()));
                    adaptation.setIntegrationModified(Double.parseDouble(adaptationTextField[2].getText()));
                    adaptation.setSoftwareUnderstanding(Double.parseDouble(adaptationTextField[3].getText()));
                    adaptation.setAssessmentAndAssimilation(Double.parseDouble(adaptationTextField[4].getText()));
                    adaptation.setUnfamiliarityWithSoftware(Double.parseDouble(adaptationTextField[5].getText()));
                    adaptation.setAutomaticallyTranslated(Double.parseDouble(adaptationTextField[6].getText()));
                    adaptation.setAutomaticTranslationProductivity(Double.parseDouble(adaptationTextField[7].getText()));

                    // Calculate & Update values 
                    COINCOMOAdaptationAndReuseManager.updateAdaptationAndReuse(adaptation, true);

                    // Reflect the updated values to UI
                    adaptedSLOCTextField.setText("" + GlobalMethods.FormatLongWithComma(adaptation.getAdaptedSLOC()));
                    AAFValueLabel.setText("" + adaptation.getAdaptationAdjustmentFactor());
                    EquivalentSLOCValueLabel.setText("" + GlobalMethods.FormatLongWithComma(adaptation.getEquivalentSLOC()));

                    // Set Back To Default ...
                    applyButton.setEnabled(true);
                    resetButton.setEnabled(true);
                    closeButton.setEnabled(true);

                    //GlobalMethods.updateStatusBar("Adaptation and Reuse Saved.");

                    // Auto-apply Subcomponent
                    owner.Apply();
                }
            });
        } else if (e.getSource() == resetButton) {
            // Ask the user to make sure they want to reset the values in the A&R entry dialog before proceeding to reset.
            final int resetAnswer = JOptionPane.showConfirmDialog(this.getParent(), "All inputs will be reset to their default values, are you sure?", "RESET CONFIRMATION", JOptionPane.YES_NO_OPTION);
            if (resetAnswer == JOptionPane.YES_OPTION) {
                adaptedSLOCTextField.setText("0");
                adaptationTextField[0].setText("" + COINCOMOAdaptationAndReuse.DESIGN_MODIFIED);
                adaptationTextField[1].setText("" + COINCOMOAdaptationAndReuse.CODE_MODIFIED);
                adaptationTextField[2].setText("" + COINCOMOAdaptationAndReuse.INTEGRATION_MODIFIED);
                adaptationTextField[3].setText("" + COINCOMOAdaptationAndReuse.SOFTWARE_UNDERSTANDING);
                adaptationTextField[4].setText("" + COINCOMOAdaptationAndReuse.ASSESSMENT_AND_ASSIMILATION);
                adaptationTextField[5].setText("" + COINCOMOAdaptationAndReuse.UNFAMILIARITY_WITH_SOFTWARE);
                adaptationTextField[6].setText("" + COINCOMOAdaptationAndReuse.AUTOMATICALLY_TRANSLATED);
                adaptationTextField[7].setText("" + COINCOMOAdaptationAndReuse.AUTOMAITC_TRANSLATION_PRODUCTIVITY);
                AAFValueLabel.setText("0.0");
                EquivalentSLOCValueLabel.setText("0");
            }
        } else {
            // Free Resources ... Close Window
            this.dispose();
        }
    }
    
    private static void log(Level level, String message) {
        Logger.getLogger(SubComponentAdaptationReuseDialog.class.getName()).log(level, message);
    }
}
