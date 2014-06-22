/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package dialogs;

import core.COINCOMOSubComponent;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import main.GlobalMethods;
import main.Icons;

/**
 *
 * @author Larry Chen
 */
public class SubComponentMultiplierDialog extends JDialog implements ActionListener {

    private JLabel languagesLabel = new JLabel("Languages:");
    private JLabel multiplierDefaultLabel = new JLabel("Default multiplier:");
    private JComboBox languagesComboBox = new JComboBox();
    private JTextField multiplierTextField = new JTextField();
    private JTextField multiplierDefaultTextField = new JTextField();
    private JButton changeButton = new JButton("Change");
    private JButton resetButton = new JButton("Reset");
    private JButton closeButton = new JButton("Close");
    // Reference to SubComponentSizeDialog
    private SubComponentSizeDialog owner;
    private COINCOMOSubComponent subComponent;
    private String currentLanguage = null;
    private String currentMultiplier = null;
    private String defaultMultiplier = null;
    // Dialog size
    private final int dialogWidth = 400;
    private final int dialogHeight = 200;
    private final Dimension dialogDimension = new Dimension(dialogWidth, dialogHeight);

    public SubComponentMultiplierDialog(SubComponentSizeDialog owner, COINCOMOSubComponent subComponent) {
        super(owner);

        this.setModal(true);

        this.setTitle("Change Multiplier - " + subComponent.getName());

        this.owner = owner;
        this.subComponent = subComponent;

        this.currentLanguage = owner.getCurrentFPLanaguage();
        this.currentMultiplier = owner.getCurrentFPMultiplier();
        this.defaultMultiplier = owner.getDefaultLanguageMultiplier(this.currentLanguage);

        languagesLabel.setFont(new Font("arial", 1, 11));
        multiplierDefaultLabel.setFont(new Font("arial", 1, 11));

        changeButton.addActionListener(this);
        resetButton.addActionListener(this);
        closeButton.addActionListener(this);

        changeButton.setIcon(Icons.SAVE_ICON);
        resetButton.setIcon(Icons.RESET_ICON);
        closeButton.setIcon(Icons.CLOSE_ICON);

        JPanel multiplierPanel = new JPanel(null);
        JPanel buttonsPanel = new JPanel();

        multiplierPanel.setSize(new Dimension(400, 150));
        multiplierPanel.setPreferredSize(new Dimension(400, 150));
        multiplierPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        multiplierPanel.add(languagesLabel);
        multiplierPanel.add(languagesComboBox);
        multiplierPanel.add(multiplierTextField);
        multiplierPanel.add(multiplierDefaultLabel);
        multiplierPanel.add(multiplierDefaultTextField);

        languagesLabel.setBounds(20, 20, 95, 20);
        languagesComboBox.setBounds(90, 20, 170, 20);
        languagesComboBox.setEditable(true);
        languagesComboBox.setEnabled(false);
        multiplierTextField.setBounds(270, 20, 50, 20);

        multiplierDefaultLabel.setBounds(90, 50, 170, 20);
        multiplierDefaultLabel.setHorizontalAlignment(JLabel.TRAILING);
        multiplierDefaultTextField.setBounds(270, 50, 50, 20);
        multiplierDefaultTextField.setEditable(false);

        languagesComboBox.addItem(this.currentLanguage);
        multiplierTextField.setText(this.currentMultiplier);
        multiplierDefaultTextField.setText(this.defaultMultiplier);

        buttonsPanel.add(changeButton);
        buttonsPanel.add(resetButton);
        buttonsPanel.add(closeButton);

        this.setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        this.add(multiplierPanel);
        this.add(buttonsPanel);

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(this.getOwner().getX() + 50, this.getOwner().getY() + 250);
        this.setResizable(false);
        this.setSize(dialogDimension);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String multiplierString = multiplierTextField.getText();

        if (e.getSource() == changeButton) {
            if (!GlobalMethods.isNonNegativeLong(multiplierString)) {
                JOptionPane.showMessageDialog(this.getParent(), "Multiplier must be a positve integer.", "FUNCTION POINTS MULTIPLIER ERROR", 0);
                multiplierTextField.selectAll();
                return;
            }

            currentMultiplier = multiplierString;
            owner.setCurrentFPMultiplier(currentMultiplier);
        } else if (e.getSource() == resetButton) {
            multiplierTextField.setText(defaultMultiplier);
        } else {
            this.dispose();
        }
    }
}
