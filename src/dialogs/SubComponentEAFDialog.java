/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package dialogs;

import core.COINCOMOConstants;
import core.COINCOMOConstants.Increment;
import core.COINCOMOConstants.Rating;
import core.COINCOMOSubComponent;
import database.COINCOMOSubComponentManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import main.COINCOMO;
import main.GlobalMethods;
import main.Icons;
import panels.ComponentOverviewPanel;

/**
 *
 * @author Raed Shomali
 */
public class SubComponentEAFDialog extends JDialog implements ActionListener {

    private JLabel[] productDriverLabels = new JLabel[]{
        new JLabel("RELY"), new JLabel("DATA"), new JLabel("DOCU"), new JLabel("CPLX"), new JLabel("RUSE")
    };
    private JLabel[] platformDriverLabels = new JLabel[]{
        new JLabel("TIME"), new JLabel("STOR"), new JLabel("PVOL")
    };
    private JLabel[] personnelDriverLabels = new JLabel[]{
        new JLabel("ACAP"), new JLabel("APEX"), new JLabel("PCAP"), new JLabel("PLEX"), new JLabel("LTEX"), new JLabel("PCON")
    };
    private JLabel[] projectDriverLabels = new JLabel[]{
        new JLabel("TOOL"), new JLabel("SITE")
    };
    private JLabel[] userDriverLabels = new JLabel[]{
        new JLabel("USR1"), new JLabel("USR2")
    };
    private JButton[] productRatingButtons = new JButton[]{
        new JButton("NOM"), new JButton("NOM"), new JButton("NOM"), new JButton("NOM"), new JButton("NOM")
    };
    private JButton[] platformRatingButtons = new JButton[]{
        new JButton("NOM"), new JButton("NOM"), new JButton("NOM")
    };
    private JButton[] personnelRatingButtons = new JButton[]{
        new JButton("NOM"), new JButton("NOM"), new JButton("NOM"), new JButton("NOM"), new JButton("NOM"), new JButton("NOM")
    };
    private JButton[] projectRatingButtons = new JButton[]{
        new JButton("NOM"), new JButton("NOM")
    };
    private JButton[] userRatingButtons = new JButton[]{
        new JButton("NOM"), new JButton("NOM")
    };
    private JButton[] productIncrementButtons = new JButton[]{
        new JButton("0%"), new JButton("0%"), new JButton("0%"), new JButton("0%"), new JButton("0%")
    };
    private JButton[] platformIncrementButtons = new JButton[]{
        new JButton("0%"), new JButton("0%"), new JButton("0%")
    };
    private JButton[] personnelIncrementButtons = new JButton[]{
        new JButton("0%"), new JButton("0%"), new JButton("0%"), new JButton("0%"), new JButton("0%"), new JButton("0%")
    };
    private JButton[] projectIncrementButtons = new JButton[]{
        new JButton("0%"), new JButton("0%")
    };
    private JButton[] userIncrementButtons = new JButton[]{
        new JButton("0%"), new JButton("0%")
    };
    private JLabel[] driverLabels = new JLabel[]{
        new JLabel("Product"), new JLabel("Platform"), new JLabel("Personnel"), new JLabel("Project"), new JLabel("User")
    };
    private JLabel[][] driverSetLabels = new JLabel[][]{
        productDriverLabels,
        platformDriverLabels,
        personnelDriverLabels,
        projectDriverLabels,
        userDriverLabels
    };
    private JButton[][] ratingSetButtons = new JButton[][]{
        productRatingButtons,
        platformRatingButtons,
        personnelRatingButtons,
        projectRatingButtons,
        userRatingButtons
    };
    private JButton[][] incrementSetButtons = new JButton[][]{
        productIncrementButtons,
        platformIncrementButtons,
        personnelIncrementButtons,
        projectIncrementButtons,
        userIncrementButtons
    };
    private JPopupMenu ratingPopup = new JPopupMenu();
    private JPopupMenu incrementPopup = new JPopupMenu();
    private JMenuItem vloMenuItem = new JMenuItem("VLO");
    private JMenuItem loMenuItem = new JMenuItem("LO");
    private JMenuItem nomMenuItem = new JMenuItem("NOM");
    private JMenuItem hiMenuItem = new JMenuItem("HI");
    private JMenuItem vhiMenuItem = new JMenuItem("VHI");
    private JMenuItem xhiMenuItem = new JMenuItem("XHI");
    private JMenuItem zeroMenuItem = new JMenuItem("0%");
    private JMenuItem twentyFiveMenuItem = new JMenuItem("25%");
    private JMenuItem fiftyMenuItem = new JMenuItem("50%");
    private JMenuItem seventyFiveMenuItem = new JMenuItem("75%");
    private JButton applyButton = new JButton("Apply");
    private JButton resetButton = new JButton("Reset");
    private JButton closeButton = new JButton("Close");
    // The Button From Which the Popup Generated ...
    private JButton ownerButton = null;
    private ComponentOverviewPanel componentOverviewPanel = null;
    private COINCOMOSubComponent subComponent = null;

    /**
     *
     * @param frame to be used to set the Owner of the Dialog
     * @param subComponent object to read from and save to
     * @param rowNumber to update the corresponding CLEF's SubComponent
     */
    public SubComponentEAFDialog(COINCOMO frame, ComponentOverviewPanel cOPanel, COINCOMOSubComponent subComponent /*, int rowNumber*/) {
        super(frame);

        this.setModalityType(ModalityType.APPLICATION_MODAL);

        //this.rowNumber = rowNumber ;
        this.componentOverviewPanel = cOPanel;
        this.subComponent = subComponent;

        this.setTitle("Effort Adjustment Factors - " + subComponent.getName());

        GlobalMethods.updateStatusBar("Done.", frame);

        // Popup
        ratingPopup.add(vloMenuItem);
        ratingPopup.add(loMenuItem);
        ratingPopup.add(nomMenuItem);
        ratingPopup.add(hiMenuItem);
        ratingPopup.add(vhiMenuItem);
        ratingPopup.add(xhiMenuItem);

        incrementPopup.add(zeroMenuItem);
        incrementPopup.add(twentyFiveMenuItem);
        incrementPopup.add(fiftyMenuItem);
        incrementPopup.add(seventyFiveMenuItem);

        // Menu Items
        vloMenuItem.addActionListener(this);
        loMenuItem.addActionListener(this);
        nomMenuItem.addActionListener(this);
        hiMenuItem.addActionListener(this);
        vhiMenuItem.addActionListener(this);
        xhiMenuItem.addActionListener(this);

        zeroMenuItem.addActionListener(this);
        twentyFiveMenuItem.addActionListener(this);
        fiftyMenuItem.addActionListener(this);
        seventyFiveMenuItem.addActionListener(this);

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

        this.setLayout(null);

        Rating eafRatings[] = subComponent.getEAFRatings();
        Increment eafIncrements[] = subComponent.getEAFIncrements();

        int counter = 0;

        // GUI
        for (int i = 0; i < driverSetLabels.length; i++) {
            // Legend
            TitledBorder legendTitleBorder = BorderFactory.createTitledBorder(driverLabels[i].getText());
            legendTitleBorder.setTitleColor(Color.BLUE);

            this.add(driverLabels[i]);

            driverLabels[i].setText("");
            driverLabels[i].setBorder(legendTitleBorder);
            driverLabels[i].setFont(new Font("courier", 1, 12));

            // Last and Before Last Sets
            if (i == driverSetLabels.length - 1) {
                driverLabels[i].setBounds(10 + 270, 10 + i * 100 - 100, 260, 100);
            } else if (i == driverSetLabels.length - 2) {
                driverLabels[i].setBounds(10, 10 + i * 100, 260, 100);
            } else {
                driverLabels[i].setBounds(10, 10 + i * 100, 530, 100);
            }

            JLabel label1 = new JLabel("Rating");
            JLabel label2 = new JLabel("% Incr");

            label1.setFont(new Font("arial", 1, 11));
            label2.setFont(new Font("arial", 1, 11));

            this.add(label1);
            this.add(label2);

            // Last Set ?
            if (i == driverSetLabels.length - 1) {
                label1.setBounds(320, 10 + i * 100 + 35 - 100, 65, 25);
                label2.setBounds(320, 10 + i * 100 + 65 - 100, 65, 25);
            } else {
                label1.setBounds(40, 10 + i * 100 + 35, 65, 25);
                label2.setBounds(40, 10 + i * 100 + 65, 65, 25);
            }

            for (int j = 0; j < driverSetLabels[i].length; j++) {
                this.add(driverSetLabels[i][j]);
                this.add(ratingSetButtons[i][j]);
                this.add(incrementSetButtons[i][j]);

                ratingSetButtons[i][j].setName(driverSetLabels[i][j].getText());
                incrementSetButtons[i][j].setName(driverSetLabels[i][j].getText());

                ratingSetButtons[i][j].setText(eafRatings[counter].toString());
                incrementSetButtons[i][j].setText(eafIncrements[counter++].toString());

                ratingSetButtons[i][j].setFont(new Font("courier", 1, 16));
                incrementSetButtons[i][j].setFont(new Font("courier", 1, 16));

                ratingSetButtons[i][j].setFocusable(false);
                incrementSetButtons[i][j].setFocusable(false);

                ratingSetButtons[i][j].addActionListener(this);
                incrementSetButtons[i][j].addActionListener(this);

                driverSetLabels[i][j].setHorizontalAlignment(JLabel.CENTER);
                driverSetLabels[i][j].setFont(new Font("arial", 1, 11));

                // Last Set...
                if (i == driverSetLabels.length - 1) {
                    driverSetLabels[i][j].setBounds(370 + j * 70, 10 + i * 100 + 10 - 100, 65, 25);
                    ratingSetButtons[i][j].setBounds(370 + j * 70, 10 + i * 100 + 35 - 100, 65, 25);
                    incrementSetButtons[i][j].setBounds(370 + j * 70, 10 + i * 100 + 65 - 100, 65, 25);
                } else {
                    driverSetLabels[i][j].setBounds(90 + j * 70, 10 + i * 100 + 10, 65, 25);
                    ratingSetButtons[i][j].setBounds(90 + j * 70, 10 + i * 100 + 35, 65, 25);
                    incrementSetButtons[i][j].setBounds(90 + j * 70, 10 + i * 100 + 65, 65, 25);
                }
            }
        }

        // Panel
        JPanel southPanel = new JPanel();
        southPanel.add(applyButton);
        southPanel.add(resetButton);
        southPanel.add(closeButton);

        this.add(southPanel);
        southPanel.setBounds(0, 420, 570, 40);

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(this.getOwner().getX() + 100, this.getOwner().getY() + 100);
        this.setResizable(false);
        this.setSize(570, 500);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == applyButton) {
            // Disable Them ...
            applyButton.setEnabled(false);
            resetButton.setEnabled(false);
            closeButton.setEnabled(false);

            GlobalMethods.updateStatusBar("Saving ...", ((COINCOMO) this.getParent()));

            componentOverviewPanel.getEstimationTextPane().setText("Loading ...");

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Rating eafRatings[] = new Rating[COINCOMOConstants.EAFS.length - 1];
                    Increment eafIncrements[] = new Increment[COINCOMOConstants.EAFS.length - 1];

                    int counter = 0;

                    for (int i = 0; i < ratingSetButtons.length; i++) {
                        for (int j = 0; j < ratingSetButtons[i].length; j++) {
                            eafRatings[counter] = Rating.valueOf(ratingSetButtons[i][j].getText());
                            eafIncrements[counter++] = Increment.getValueOf(incrementSetButtons[i][j].getText());
                        }
                    }

                    subComponent.setEAFRatings(eafRatings);
                    subComponent.setEAFIncrements(eafIncrements);

                    // Update database
                    COINCOMOSubComponentManager.updateSubComponent(subComponent, true);

                    // Update summary report
                    componentOverviewPanel.updateClefTable();
                    SubComponentEAFDialog.this.componentOverviewPanel.updateEstimationTextPane(false);

                    //GlobalMethods.updateStatusBar( "Saved." );

                    // Enable Them ...
                    applyButton.setEnabled(true);
                    resetButton.setEnabled(true);
                    closeButton.setEnabled(true);
                }
            });
        } else if (e.getSource() == resetButton) {
            for (int i = 0; i < ratingSetButtons.length; i++) {
                for (int j = 0; j < ratingSetButtons[i].length; j++) {
                    JButton button = ratingSetButtons[i][j];

                    button.setText("NOM");
                }
            }

            for (int i = 0; i < incrementSetButtons.length; i++) {
                for (int j = 0; j < incrementSetButtons[i].length; j++) {
                    JButton button = incrementSetButtons[i][j];

                    button.setText("0%");
                }
            }
        } else if (e.getSource() == closeButton) {
            // Free Resources ... Close Window
            this.dispose();
        } else if (e.getSource() == vloMenuItem || e.getSource() == loMenuItem || e.getSource() == nomMenuItem
                || e.getSource() == hiMenuItem || e.getSource() == vhiMenuItem || e.getSource() == xhiMenuItem) {
            if (ownerButton.getName().equals("RELY")) {
                if (e.getSource() == vhiMenuItem) {
                    incrementSetButtons[0][0].setText("0%");
                }
            } else if (ownerButton.getName().equals("DATA")) {
                if (e.getSource() == vhiMenuItem) {
                    incrementSetButtons[0][1].setText("0%");
                }
            } else if (ownerButton.getName().equals("DOCU")) {
                if (e.getSource() == vhiMenuItem) {
                    incrementSetButtons[0][2].setText("0%");
                }
            } else if (ownerButton.getName().equals("CPLX")) {
                if (e.getSource() == xhiMenuItem) {
                    incrementSetButtons[0][3].setText("0%");
                }
            } else if (ownerButton.getName().equals("RUSE")) {
                if (e.getSource() == xhiMenuItem) {
                    incrementSetButtons[0][4].setText("0%");
                }
            } else if (ownerButton.getName().equals("TIME")) {
                if (e.getSource() == xhiMenuItem) {
                    incrementSetButtons[1][0].setText("0%");
                }
            } else if (ownerButton.getName().equals("STOR")) {
                if (e.getSource() == xhiMenuItem) {
                    incrementSetButtons[1][1].setText("0%");
                }
            } else if (ownerButton.getName().equals("PVOL")) {
                if (e.getSource() == vhiMenuItem) {
                    incrementSetButtons[1][2].setText("0%");
                }
            } else if (ownerButton.getName().equals("ACAP")) {
                if (e.getSource() == vhiMenuItem) {
                    incrementSetButtons[2][0].setText("0%");
                }
            } else if (ownerButton.getName().equals("APEX")) {
                if (e.getSource() == vhiMenuItem) {
                    incrementSetButtons[2][1].setText("0%");
                }
            } else if (ownerButton.getName().equals("PCAP")) {
                if (e.getSource() == vhiMenuItem) {
                    incrementSetButtons[2][2].setText("0%");
                }
            } else if (ownerButton.getName().equals("PLEX")) {
                if (e.getSource() == vhiMenuItem) {
                    incrementSetButtons[2][3].setText("0%");
                }
            } else if (ownerButton.getName().equals("LTEX")) {
                if (e.getSource() == vhiMenuItem) {
                    incrementSetButtons[2][4].setText("0%");
                }
            } else if (ownerButton.getName().equals("PCON")) {
                if (e.getSource() == vhiMenuItem) {
                    incrementSetButtons[2][5].setText("0%");
                }
            } else if (ownerButton.getName().equals("TOOL")) {
                if (e.getSource() == vhiMenuItem) {
                    incrementSetButtons[3][0].setText("0%");
                }
            } else if (ownerButton.getName().equals("SITE")) {
                if (e.getSource() == xhiMenuItem) {
                    incrementSetButtons[3][1].setText("0%");
                }
            } else if (ownerButton.getName().equals("USR1")) {
                if (e.getSource() == xhiMenuItem) {
                    incrementSetButtons[4][0].setText("0%");
                }
            } else if (ownerButton.getName().equals("USR2")) {
                if (e.getSource() == xhiMenuItem) {
                    incrementSetButtons[4][1].setText("0%");
                }
            }

            ownerButton.setText(((JMenuItem) e.getSource()).getText());
        } else if (e.getSource() == zeroMenuItem || e.getSource() == twentyFiveMenuItem
                || e.getSource() == fiftyMenuItem || e.getSource() == seventyFiveMenuItem) {
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
            
            zeroMenuItem.setEnabled(true);
            twentyFiveMenuItem.setEnabled(true);
            fiftyMenuItem.setEnabled(true);
            seventyFiveMenuItem.setEnabled(true);

            if (ownerButton.getName().equals("RELY")) {
                xhiMenuItem.setEnabled(false);
                
                if (ratingSetButtons[0][0].getText().equals("VHI")) {
                    twentyFiveMenuItem.setEnabled(false);
                    fiftyMenuItem.setEnabled(false);
                    seventyFiveMenuItem.setEnabled(false);
                }
            } else if (ownerButton.getName().equals("DATA")) {
                vloMenuItem.setEnabled(false);
                xhiMenuItem.setEnabled(false);

                if (ratingSetButtons[0][1].getText().equals("VHI")) {
                    twentyFiveMenuItem.setEnabled(false);
                    fiftyMenuItem.setEnabled(false);
                    seventyFiveMenuItem.setEnabled(false);
                }
            } else if (ownerButton.getName().equals("DOCU")) {
                xhiMenuItem.setEnabled(false);

                if (ratingSetButtons[0][2].getText().equals("VHI")) {
                    twentyFiveMenuItem.setEnabled(false);
                    fiftyMenuItem.setEnabled(false);
                    seventyFiveMenuItem.setEnabled(false);
                }
            } else if (ownerButton.getName().equals("CPLX")) {
                if (ratingSetButtons[0][3].getText().equals("XHI")) {
                    twentyFiveMenuItem.setEnabled(false);
                    fiftyMenuItem.setEnabled(false);
                    seventyFiveMenuItem.setEnabled(false);
                }
            } else if (ownerButton.getName().equals("RUSE")) {
                vloMenuItem.setEnabled(false);

                if (ratingSetButtons[0][4].getText().equals("XHI")) {
                    twentyFiveMenuItem.setEnabled(false);
                    fiftyMenuItem.setEnabled(false);
                    seventyFiveMenuItem.setEnabled(false);
                }
            } else if (ownerButton.getName().equals("TIME")) {
                vloMenuItem.setEnabled(false);
                loMenuItem.setEnabled(false);

                if (ratingSetButtons[1][0].getText().equals("XHI")) {
                    twentyFiveMenuItem.setEnabled(false);
                    fiftyMenuItem.setEnabled(false);
                    seventyFiveMenuItem.setEnabled(false);
                }
            } else if (ownerButton.getName().equals("STOR")) {
                vloMenuItem.setEnabled(false);
                loMenuItem.setEnabled(false);

                if (ratingSetButtons[1][1].getText().equals("XHI")) {
                    twentyFiveMenuItem.setEnabled(false);
                    fiftyMenuItem.setEnabled(false);
                    seventyFiveMenuItem.setEnabled(false);
                }
            } else if (ownerButton.getName().equals("PVOL")) {
                vloMenuItem.setEnabled(false);
                xhiMenuItem.setEnabled(false);

                if (ratingSetButtons[1][2].getText().equals("VHI")) {
                    twentyFiveMenuItem.setEnabled(false);
                    fiftyMenuItem.setEnabled(false);
                    seventyFiveMenuItem.setEnabled(false);
                }
            } else if (ownerButton.getName().equals("ACAP")) {
                xhiMenuItem.setEnabled(false);

                if (ratingSetButtons[2][0].getText().equals("VHI")) {
                    twentyFiveMenuItem.setEnabled(false);
                    fiftyMenuItem.setEnabled(false);
                    seventyFiveMenuItem.setEnabled(false);
                }
            } else if (ownerButton.getName().equals("APEX")) {
                xhiMenuItem.setEnabled(false);

                if (ratingSetButtons[2][1].getText().equals("VHI")) {
                    twentyFiveMenuItem.setEnabled(false);
                    fiftyMenuItem.setEnabled(false);
                    seventyFiveMenuItem.setEnabled(false);
                }
            } else if (ownerButton.getName().equals("PCAP")) {
                xhiMenuItem.setEnabled(false);

                if (ratingSetButtons[2][2].getText().equals("VHI")) {
                    twentyFiveMenuItem.setEnabled(false);
                    fiftyMenuItem.setEnabled(false);
                    seventyFiveMenuItem.setEnabled(false);
                }
            } else if (ownerButton.getName().equals("PLEX")) {
                xhiMenuItem.setEnabled(false);

                if (ratingSetButtons[2][3].getText().equals("VHI")) {
                    twentyFiveMenuItem.setEnabled(false);
                    fiftyMenuItem.setEnabled(false);
                    seventyFiveMenuItem.setEnabled(false);
                }
            } else if (ownerButton.getName().equals("LTEX")) {
                xhiMenuItem.setEnabled(false);

                if (ratingSetButtons[2][4].getText().equals("VHI")) {
                    twentyFiveMenuItem.setEnabled(false);
                    fiftyMenuItem.setEnabled(false);
                    seventyFiveMenuItem.setEnabled(false);
                }
            } else if (ownerButton.getName().equals("PCON")) {
                xhiMenuItem.setEnabled(false);

                if (ratingSetButtons[2][5].getText().equals("VHI")) {
                    twentyFiveMenuItem.setEnabled(false);
                    fiftyMenuItem.setEnabled(false);
                    seventyFiveMenuItem.setEnabled(false);
                }
            } else if (ownerButton.getName().equals("TOOL")) {
                xhiMenuItem.setEnabled(false);

                if (ratingSetButtons[3][0].getText().equals("VHI")) {
                    twentyFiveMenuItem.setEnabled(false);
                    fiftyMenuItem.setEnabled(false);
                    seventyFiveMenuItem.setEnabled(false);
                }
            } else if (ownerButton.getName().equals("SITE")) {
                if (ratingSetButtons[3][1].getText().equals("XHI")) {
                    twentyFiveMenuItem.setEnabled(false);
                    fiftyMenuItem.setEnabled(false);
                    seventyFiveMenuItem.setEnabled(false);
                }
            } else if (ownerButton.getName().equals("USR1")) {
                if (ratingSetButtons[4][0].getText().equals("XHI")) {
                    twentyFiveMenuItem.setEnabled(false);
                    fiftyMenuItem.setEnabled(false);
                    seventyFiveMenuItem.setEnabled(false);
                }
            } else if (ownerButton.getName().equals("USR2")) {
                if (ratingSetButtons[4][1].getText().equals("XHI")) {
                    twentyFiveMenuItem.setEnabled(false);
                    fiftyMenuItem.setEnabled(false);
                    seventyFiveMenuItem.setEnabled(false);
                }
            }

            // Determine Which Popup to show ...
            if (ownerButton.getText().endsWith("%")) {
                incrementPopup.show((Component) e.getSource(), ((JButton) e.getSource()).getWidth() / 2, ((JButton) e.getSource()).getHeight() / 2);
            } else {
                ratingPopup.show((Component) e.getSource(), ((JButton) e.getSource()).getWidth() / 2, ((JButton) e.getSource()).getHeight() / 2);
            }
        }
    }
}
