/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package dialogs;

import core.COINCOMOComponent;
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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import main.COINCOMO;
import main.GlobalMethods;
import main.Icons;
import panels.ComponentOverviewPanel;

/**
 *
 * @author Raed Shomali
 */
public class ComponentScheduleDriverDialog extends JDialog implements ActionListener {

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
    private JButton ratingButton = new JButton("NOM");
    private JButton incrementButton = new JButton("0%");
    private JButton applyButton = new JButton("Apply");
    private JButton resetButton = new JButton("Reset");
    private JButton closeButton = new JButton("Close");
    // The Button From Which the Popup Generated ...
    private JButton ownerButton = null;
    private ComponentOverviewPanel componentOverviewPanel = null;
    private COINCOMOComponent component = null;
    private JTextField scheduleText = new JTextField("1.00");
    private JTextField schedulePercentText = new JTextField("100");
    DecimalFormat format2Decimals = new DecimalFormat("0.00");

    public ComponentScheduleDriverDialog(COINCOMO owner, ComponentOverviewPanel coPanel) {
        super(owner);

        this.setModalityType(ModalityType.APPLICATION_MODAL);

        this.componentOverviewPanel = coPanel;
        this.component = this.componentOverviewPanel.getCOINCOMOComponent();

        this.setTitle("Schedule (SCED) - " + component.getName());

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

        scheduleText.addActionListener(this);
        schedulePercentText.addActionListener(this);

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

        JLabel labelSchedule = new JLabel("Schedule (SCED)");
        labelSchedule.setBounds(10, 50, 200, 25);
        labelSchedule.setFont(new Font("arial", 1, 11));
        
        JLabel labelSchedulePercent = new JLabel("Schedule% (SCED%)");
        labelSchedulePercent.setBounds(10, 80, 200, 25);
        labelSchedulePercent.setFont(new Font("arial", 1, 11));

        this.add(labelSchedule);
        this.add(labelSchedulePercent);

        scheduleText.setText(format2Decimals.format(component.getSCED()));
        scheduleText.setEditable(false);
        scheduleText.setBounds(150, 50, 40, 25);
        scheduleText.setFont(new Font("arial", 1, 11));
        scheduleText.setHorizontalAlignment(JTextField.CENTER);

        schedulePercentText.setText(format2Decimals.format(component.getSCEDPercent()));
        schedulePercentText.setEditable(false);
        schedulePercentText.setBounds(150, 80, 40, 25);
        schedulePercentText.setFont(new Font("arial", 1, 11));
        schedulePercentText.setHorizontalAlignment(JTextField.CENTER);

        this.add(scheduleText);
        this.add(schedulePercentText);

        ratingButton.setName("SCED");
        incrementButton.setName("");
        
        ratingButton.setText(component.getSCEDRating().toString());
        incrementButton.setText(component.getSCEDIncrement().toString());

        ratingButton.setFont(new Font("courier", 1, 14));
        incrementButton.setFont(new Font("courier", 1, 14));

        ratingButton.setFocusable(false);
        incrementButton.setFocusable(false);

        ratingButton.addActionListener(this);
        incrementButton.addActionListener(this);

        this.add(ratingButton);
        this.add(incrementButton);

        ratingButton.setBounds(200, 50, 65, 25);
        incrementButton.setBounds(275, 50, 65, 25);

        // Panel
        JPanel southPanel = new JPanel();
        southPanel.add(applyButton);
        southPanel.add(resetButton);
        southPanel.add(closeButton);

        this.add(southPanel);

        southPanel.setBounds(0, 120, 360, 40);

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(this.getOwner().getX() + 100, this.getOwner().getY() + 100);
        this.setResizable(false);
        this.setSize(360, 200);
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
                    component.setSCEDRating(Rating.valueOf(ratingButton.getText()));
                    component.setSCEDIncrement(Increment.getValueOf(incrementButton.getText()));

                    // Update database
                    COINCOMOComponentManager.updateComponent(component, true);

                    // Update summary report
                    componentOverviewPanel.updateClefTable();
                    componentOverviewPanel.updateEstimationTextPane(true);

                    GlobalMethods.updateStatusBar("Schedule Saved.", (COINCOMO) ComponentScheduleDriverDialog.this.getParent());

                    scheduleText.setText(format2Decimals.format(component.getSCED()));
                    schedulePercentText.setText(format2Decimals.format(component.getSCEDPercent()));

                    // Back To Default
                    applyButton.setEnabled(true);
                    resetButton.setEnabled(true);
                    closeButton.setEnabled(true);
                }
            });
        } else if (e.getSource() == resetButton) {
            ratingButton.setText("NOM");
            incrementButton.setText("0%");

            scheduleText.setText("1.00");
        } else if (e.getSource() == closeButton) {
            // Free Resources ... Close Window
            this.dispose();
        } else if (e.getSource() == vloMenuItem || e.getSource() == loMenuItem || e.getSource() == nomMenuItem
                || e.getSource() == hiMenuItem || e.getSource() == vhiMenuItem || e.getSource() == xhiMenuItem) {
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

            if (ownerButton.getName().equals("SCED")) {
                xhiMenuItem.setEnabled(false);
            }

            if (ownerButton.getText().endsWith("%")) {
                incrementPopup.show((Component) e.getSource(), ((JButton) e.getSource()).getWidth() / 2, ((JButton) e.getSource()).getHeight() / 2);
            } else {
                ratingPopup.show((Component) e.getSource(), ((JButton) e.getSource()).getWidth() / 2, ((JButton) e.getSource()).getHeight() / 2);
            }
        }
    }
}