/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package dialogs;

import core.COINCOMOComponent;
import core.COINCOMOConstants;
import core.COINCOMOConstants.Increment;
import core.COINCOMOConstants.Rating;
import database.COINCOMOComponentManager;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import main.COINCOMO;
import main.GlobalMethods;
import main.Icons;
import panels.ComponentOverviewPanel;

/**
 *
 * @author Raed Shomali
 */
public class ComponentScaleFactorsDialog extends JDialog implements ActionListener {

    private JPopupMenu ratingPopup = new JPopupMenu();
    private JPopupMenu incrementPopup = new JPopupMenu();
    private JMenuItem vloMenuItem = new JMenuItem("VLO");
    private JMenuItem loMenuItem = new JMenuItem("LO");
    private JMenuItem nomMenuItem = new JMenuItem("NOM");
    private JMenuItem hiMenuItem = new JMenuItem("HI");
    private JMenuItem vhiMenuItem = new JMenuItem("VHI");
    private JMenuItem xhiMenuItem = new JMenuItem("XHI");
    private JMenuItem zeroMenutItem = new JMenuItem("0%");
    private JMenuItem twentyFiveMenutItem = new JMenuItem("25%");
    private JMenuItem fiftyMenutItem = new JMenuItem("50%");
    private JMenuItem seventyFiveMenutItem = new JMenuItem("75%");
    private JButton ratingButtons[] = {
        new JButton("NOM"), new JButton("NOM"), new JButton("NOM"), new JButton("NOM"), new JButton("NOM")
    };
    private JButton incrementButtons[] = {
        new JButton("0%"), new JButton("0%"), new JButton("0%"), new JButton("0%"), new JButton("0%")
    };
    private JButton applyButton = new JButton("Apply");
    private JButton resetButton = new JButton("Reset");
    private JButton closeButton = new JButton("Close");
    // The Button From Which the Popup Generated ...
    private JButton ownerButton = null;
    private ComponentOverviewPanel componentOverviewPanel = null;
    private COINCOMOComponent component = null;
    DecimalFormat format2Decimals = new DecimalFormat("0.00");

    public ComponentScaleFactorsDialog(COINCOMO owner, ComponentOverviewPanel cOPanel) {
        super(owner);

        this.setModalityType(ModalityType.APPLICATION_MODAL);

        this.componentOverviewPanel = cOPanel;
        this.component = componentOverviewPanel.getCOINCOMOComponent();

        this.setTitle("Scale Factors - " + component.getName());

        GlobalMethods.updateStatusBar("Done.", owner);

        // Popup
        ratingPopup.add(vloMenuItem);
        ratingPopup.add(loMenuItem);
        ratingPopup.add(nomMenuItem);
        ratingPopup.add(hiMenuItem);
        ratingPopup.add(vhiMenuItem);
        ratingPopup.add(xhiMenuItem);

        incrementPopup.add(zeroMenutItem);
        incrementPopup.add(twentyFiveMenutItem);
        incrementPopup.add(fiftyMenutItem);
        incrementPopup.add(seventyFiveMenutItem);

        // Menu Items
        vloMenuItem.addActionListener(this);
        loMenuItem.addActionListener(this);
        nomMenuItem.addActionListener(this);
        hiMenuItem.addActionListener(this);
        vhiMenuItem.addActionListener(this);
        xhiMenuItem.addActionListener(this);

        zeroMenutItem.addActionListener(this);
        twentyFiveMenutItem.addActionListener(this);
        fiftyMenutItem.addActionListener(this);
        seventyFiveMenutItem.addActionListener(this);

        // Buttons
        applyButton.addActionListener(this);
        resetButton.addActionListener(this);
        closeButton.addActionListener(this);

        applyButton.setFocusable(false);
        resetButton.setFocusable(false);
        closeButton.setFocusable(false);

        applyButton.setIcon(Icons.SAVE_ICON);
        resetButton.setIcon(Icons.RESET_ICON);
        closeButton.setIcon(Icons.CLOSE_ICON);

        // GUI
        this.setLayout(null);

        JLabel ratingLabel = new JLabel("Rating");
        JLabel incrementLabel = new JLabel("Increment");

        ratingLabel.setFont(new Font("arial", 1, 11));
        incrementLabel.setFont(new Font("arial", 1, 11));

        ratingLabel.setHorizontalAlignment(JLabel.CENTER);
        incrementLabel.setHorizontalAlignment(JLabel.CENTER);

        this.add(ratingLabel);
        this.add(incrementLabel);

        ratingLabel.setBounds(200, 20, 65, 25);
        incrementLabel.setBounds(275, 20, 65, 25);

        JLabel label1 = new JLabel("Precedentedness (PREC)");
        JLabel label2 = new JLabel("Development Flexibility (FLEX)");
        JLabel label3 = new JLabel("Risk Resolution (RESL)");
        JLabel label4 = new JLabel("Team Cohesion (TEAM)");
        JLabel label5 = new JLabel("Process Maturity (PMAT)");

        label1.setBounds(10, 50, 200, 25);
        label2.setBounds(10, 85, 200, 25);
        label3.setBounds(10, 120, 200, 25);
        label4.setBounds(10, 155, 200, 25);
        label5.setBounds(10, 190, 200, 25);

        label1.setFont(new Font("arial", 1, 11));
        label2.setFont(new Font("arial", 1, 11));
        label3.setFont(new Font("arial", 1, 11));
        label4.setFont(new Font("arial", 1, 11));
        label5.setFont(new Font("arial", 1, 11));

        this.add(label1);
        this.add(label2);
        this.add(label3);
        this.add(label4);
        this.add(label5);

        for (int i = 0; i < COINCOMOConstants.SFS.length; i++) {
            JButton tempRatingButton = ratingButtons[i];
            JButton tempIncrementButton = incrementButtons[i];

            tempRatingButton.setName(COINCOMOConstants.SFS[i]);
            tempIncrementButton.setName("");

            tempRatingButton.setText(component.getSFRatings()[i].toString());
            tempIncrementButton.setText(component.getSFIncrements()[i].toString());

            tempRatingButton.setFont(new Font("courier", 1, 14));
            tempIncrementButton.setFont(new Font("courier", 1, 14));

            tempRatingButton.setFocusable(false);
            tempIncrementButton.setFocusable(false);

            tempRatingButton.addActionListener(this);
            tempIncrementButton.addActionListener(this);

            this.add(tempRatingButton);
            this.add(tempIncrementButton);

            tempRatingButton.setBounds(200, 50 + 35 * i, 65, 25);
            tempIncrementButton.setBounds(275, 50 + 35 * i, 65, 25);
        }

        // Panel
        JPanel southPanel = new JPanel();
        southPanel.add(applyButton);
        southPanel.add(resetButton);
        southPanel.add(closeButton);

        this.add(southPanel);

        southPanel.setBounds(0, 240, 360, 40);

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(this.getOwner().getX() + 100, this.getOwner().getY() + 100);
        this.setResizable(false);
        this.setSize(360, 320);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == applyButton) {
            applyButton.setEnabled(false);
            resetButton.setEnabled(false);
            closeButton.setEnabled(false);

            GlobalMethods.updateStatusBar("Saving ...", (COINCOMO) this.getParent());

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Rating sfRatings[] = new Rating[COINCOMOConstants.SFS.length];
                    Increment sfIncrements[] = new Increment[COINCOMOConstants.SFS.length];

                    for (int i = 0; i < COINCOMOConstants.SFS.length; i++) {
                        sfRatings[i] = Rating.valueOf(ratingButtons[i].getText());
                        sfIncrements[i] = Increment.getValueOf(incrementButtons[i].getText());
                    }

                    component.setSFRatings(sfRatings);
                    component.setSFIncrements(sfIncrements);

                    // Update database
                    COINCOMOComponentManager.updateComponent(component, true);

                    // Update button
                    componentOverviewPanel.getScaleFactorsButton().setText("Scale Factors " + format2Decimals.format(component.getSF()));

                    // Update summary report
                    componentOverviewPanel.updateClefTable();
                    componentOverviewPanel.updateEstimationTextPane(true);

                    GlobalMethods.updateStatusBar("Scale Factors Saved.", (COINCOMO) ComponentScaleFactorsDialog.this.getParent());

                    // Back To Default
                    applyButton.setEnabled(true);
                    resetButton.setEnabled(true);
                    closeButton.setEnabled(true);
                }
            });
        } else if (e.getSource() == resetButton) {
            for (int i = 0; i < 5; i++) {
                JButton tempRatingButton = ratingButtons[i];
                JButton tempIncrementButton = incrementButtons[i];

                tempRatingButton.setText("NOM");
                tempIncrementButton.setText("0%");
            }
        } else if (e.getSource() == closeButton) {
            // Free Resources ... Close Window
            this.dispose();
        } else if (e.getSource() == vloMenuItem || e.getSource() == loMenuItem || e.getSource() == nomMenuItem
                || e.getSource() == hiMenuItem || e.getSource() == vhiMenuItem || e.getSource() == xhiMenuItem) {
            if (ownerButton.getName().equals("PREC")) {
                if (e.getSource() == xhiMenuItem) {
                    incrementButtons[0].setText("0%");
                }
            } else if (ownerButton.getName().equals("FLEX")) {
                if (e.getSource() == xhiMenuItem) {
                    incrementButtons[1].setText("0%");
                }
            } else if (ownerButton.getName().equals("RESL")) {
                if (e.getSource() == xhiMenuItem) {
                    incrementButtons[2].setText("0%");
                }
            } else if (ownerButton.getName().equals("TEAM")) {
                if (e.getSource() == xhiMenuItem) {
                    incrementButtons[3].setText("0%");
                }
            } else if (ownerButton.getName().equals("PMAT")) {
                if (e.getSource() == xhiMenuItem) {
                    incrementButtons[4].setText("0%");
                }
            }

            ownerButton.setText(((JMenuItem) e.getSource()).getText());
        } else if (e.getSource() == zeroMenutItem || e.getSource() == twentyFiveMenutItem
                || e.getSource() == fiftyMenutItem || e.getSource() == seventyFiveMenutItem) {
            ownerButton.setText(((JMenuItem) e.getSource()).getText());
        } else {
            ownerButton = ((JButton) e.getSource());

            // Default
            vloMenuItem.setEnabled(true);
            loMenuItem.setEnabled(true);
            nomMenuItem.setEnabled(true);
            hiMenuItem.setEnabled(true);
            vhiMenuItem.setEnabled(true);
            xhiMenuItem.setEnabled(true);

            if (ownerButton.getName().equals("PREC")) {
            } else if (ownerButton.getName().equals("FLEX")) {
            } else if (ownerButton.getName().equals("RESL")) {
            } else if (ownerButton.getName().equals("TEAM")) {
            } else if (ownerButton.getName().equals("PMAT")) {
            }

            if (ownerButton.getText().endsWith("%")) {
                incrementPopup.show((Component) e.getSource(), ((JButton) e.getSource()).getWidth() / 2, ((JButton) e.getSource()).getHeight() / 2);
            } else {
                ratingPopup.show((Component) e.getSource(), ((JButton) e.getSource()).getWidth() / 2, ((JButton) e.getSource()).getHeight() / 2);
            }
        }
    }
}
