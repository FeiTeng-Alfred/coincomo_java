/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package dialogs;

import core.COINCOMOComponent;
import core.COINCOMOComponentParameters;
import database.COINCOMOComponentManager;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import main.COINCOMO;
import main.DialogGridCreation;
import main.GlobalMethods;
import main.Icons;

/**
 *
 * @author Raed Shomali
 */
public class EAFDialog extends JDialog implements ActionListener {

    private JTabbedPane tabs = new JTabbedPane();
    private JPanel productPanel = new JPanel();
    private JPanel platformPanel = new JPanel();
    private JPanel personnelPanel = new JPanel();
    private JPanel projectPanel = new JPanel();
    private JPanel userPanel = new JPanel();
    private JPanel schedulePanel = new JPanel();
    private String[] productNames = new String[]{
        "RELY", "DATA", "DOCU", "CPLX", "RUSE"
    };
    private String[] platformNames = new String[]{
        "TIME", "STOR", "PVOL"
    };
    private String[] personnelNames = new String[]{
        "ACAP", "APEX", "PCAP", "PLEX", "LTEX", "PCON"
    };
    private String[] projectNames = new String[]{
        "TOOL", "SITE", "SCED"
    };
    private String[] userNames = new String[]{
        "USR1", "USR2"
    };
    private String[] scheduleNames = new String[]{
        "SCED"
    };
    private String[] ratings = new String[]{
        "VLO", "LO", "NOM", "HI", "VHI", "XHI"
    };
    private JTextField[][] productFields = new JTextField[productNames.length][ratings.length];
    private JTextField[][] platformFields = new JTextField[platformNames.length][ratings.length];
    private JTextField[][] personnelFields = new JTextField[personnelNames.length][ratings.length];
    private JTextField[][] projectFields = new JTextField[projectNames.length][ratings.length];
    private JTextField[][] userFields = new JTextField[userNames.length][ratings.length];
//    private JTextField[][] scheduleFields = new JTextField[scheduleNames.length][ratings.length];
    private JButton applyButton = new JButton("Apply");
    private JButton resetButton = new JButton("Reset");
    private JButton closeButton = new JButton("Close");
    private COINCOMO coincomo = null;
    private COINCOMOComponent component = null;
    private COINCOMOComponentParameters parameters = null;

    public EAFDialog(COINCOMO coincomo) {
        super(coincomo);

        this.coincomo = coincomo;
        this.component = (COINCOMOComponent) coincomo.getCurrentUnit();
        this.parameters = this.component.getParameters();
        
        this.setModal(true);

        this.setTitle("Effort Adjustment Factors - " + this.component.getName());
        GlobalMethods.updateStatusBar("Done.", coincomo);

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

        // Text Fields
        for (int i = 0; i < productNames.length; i++) {
            for (int j = 0; j < ratings.length; j++) {
                productFields[ i][ j] = new JTextField();
                JTextFieldLimit fieldLimit = new JTextFieldLimit();
                productFields[i][j].setDocument(fieldLimit);
                productFields[i][j].setText(parameters.getProductValue(i, j) + "");
                final int x = i, y = j;
                productFields[i][j].addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusGained(java.awt.event.FocusEvent evt) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                productFields[x][y].selectAll();
                            }
                        });
                    }
                });
            }
        }

        for (int i = 0; i < platformNames.length; i++) {
            for (int j = 0; j < ratings.length; j++) {
                platformFields[ i][ j] = new JTextField();
                JTextFieldLimit fieldLimit = new JTextFieldLimit();
                platformFields[i][j].setDocument(fieldLimit);
                platformFields[i][j].setText(parameters.getPlatformValue(i, j) + "");
                final int x = i, y = j;
                platformFields[i][j].addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusGained(java.awt.event.FocusEvent evt) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                platformFields[x][y].selectAll();
                            }
                        });
                    }
                });
            }
        }

        for (int i = 0; i < personnelNames.length; i++) {
            for (int j = 0; j < ratings.length; j++) {
                personnelFields[ i][ j] = new JTextField();
                JTextFieldLimit fieldLimit = new JTextFieldLimit();
                personnelFields[i][j].setDocument(fieldLimit);
                personnelFields[i][j].setText(parameters.getPersonnelValue(i, j) + "");
                final int x = i, y = j;
                personnelFields[i][j].addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusGained(java.awt.event.FocusEvent evt) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                personnelFields[x][y].selectAll();
                            }
                        });
                    }
                });
            }
        }

        for (int i = 0; i < projectNames.length; i++) {
            for (int j = 0; j < ratings.length; j++) {
                projectFields[ i][ j] = new JTextField();
                JTextFieldLimit fieldLimit = new JTextFieldLimit();
                projectFields[i][j].setDocument(fieldLimit);
                projectFields[i][j].setText(parameters.getProjectValue(i, j) + "");
                final int x = i, y = j;
                projectFields[i][j].addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusGained(java.awt.event.FocusEvent evt) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                projectFields[x][y].selectAll();
                            }
                        });
                    }
                });
            }
        }

        for (int i = 0; i < userNames.length; i++) {
            for (int j = 0; j < ratings.length; j++) {
                userFields[ i][ j] = new JTextField();
                JTextFieldLimit fieldLimit = new JTextFieldLimit();
                userFields[i][j].setDocument(fieldLimit);
                userFields[i][j].setText(parameters.getUserDefinedValue(i, j) + "");
                final int x = i, y = j;
                userFields[i][j].addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusGained(java.awt.event.FocusEvent evt) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                userFields[x][y].selectAll();
                            }
                        });
                    }
                });
            }
        }

//        for (int i = 0; i < scheduleNames.length; i++) {
//            for (int j = 0; j < ratings.length; j++) {
//                scheduleFields[ i][ j] = new JTextField();
//                JTextFieldLimit fieldLimit = new JTextFieldLimit();
//                scheduleFields[i][j].setDocument(fieldLimit);
//                scheduleFields[i][j].setText(localCalibration.getScheduleValue(i, j) + "");
//                final int x = i, y = j;
//                scheduleFields[i][j].addFocusListener(new java.awt.event.FocusAdapter() {
//                    public void focusGained(java.awt.event.FocusEvent evt) {
//                        SwingUtilities.invokeLater(new Runnable() {
//                            @Override
//                            public void run() {
//                                scheduleFields[x][y].selectAll();
//                            }
//                        });
//                    }
//                });
//            }
//        }

        // Tabs
        addTab("Product", productPanel, productNames, ratings, productFields);
        addTab("Platform", platformPanel, platformNames, ratings, platformFields);
        addTab("Personnel", personnelPanel, personnelNames, ratings, personnelFields);
        addTab("Project", projectPanel, projectNames, ratings, projectFields);
        addTab("User Defined", userPanel, userNames, ratings, userFields);
//        addTab("Schedule", schedulePanel, scheduleNames, ratings, scheduleFields);

        // Panel
        JPanel southPanel = new JPanel();
        southPanel.add(applyButton);
        southPanel.add(resetButton);
        southPanel.add(closeButton);

        // GUI
        this.setLayout(new BorderLayout());
        this.add(tabs);
        this.add(southPanel, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(this.getOwner().getX() + 100, this.getOwner().getY() + 100);
        this.setResizable(false);
        this.setSize(480, 330);
        this.setVisible(true);

    }

    private void addTab(String tabName, JPanel container, String[] driverNames, String[] ratings, JTextField[][] field) {
        tabs.addTab(tabName, container);
        DialogGridCreation.createTextFieldGrid(container, driverNames, ratings, field);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == applyButton) {
            double product = Double.NaN;
            double platform = Double.NaN;
            double personnel = Double.NaN;
            double project = Double.NaN;
            double userDefined = Double.NaN;
//            double schedule = Double.NaN;

            try {
                outerLoop1:
                for (int i = 0; i < productFields.length; i++) {
                    for (int j = 0; j < productFields[i].length; j++) {
                        product = Double.parseDouble(productFields[i][j].getText());
                        if (product < 0.0 || product > 9.99) {
                            product = Double.NaN;
                            JOptionPane.showMessageDialog(null, "Enter a value between 0 and 9.99",
                                    "warning", JOptionPane.WARNING_MESSAGE);
                            break outerLoop1;
                        }
                    }
                }
                if (Double.compare(product, Double.NaN) != 0) {
                    for (int i = 0; i < productFields.length; i++) {
                        for (int j = 0; j < productFields[i].length; j++) {
                            parameters.setProductValue(i, j, Double.parseDouble(productFields[i][j].getText()));
                        }
                    }
                }

                outerLoop2:
                for (int i = 0; i < platformFields.length; i++) {
                    for (int j = 0; j < platformFields[i].length; j++) {
                        platform = Double.parseDouble(platformFields[i][j].getText());
                        if (platform < 0.0 || platform > 9.99) {
                            platform = Double.NaN;
                            JOptionPane.showMessageDialog(null, "Enter a value between 0 and 9.99",
                                    "warning", JOptionPane.WARNING_MESSAGE);
                            break outerLoop2;
                        }
                    }
                }
                if (Double.compare(platform, Double.NaN) != 0) {
                    for (int i = 0; i < platformFields.length; i++) {
                        for (int j = 0; j < platformFields[i].length; j++) {
                            parameters.setPlatformValue(i, j, Double.parseDouble(platformFields[i][j].getText()));
                        }
                    }
                }

                outerLoop3:
                for (int i = 0; i < personnelFields.length; i++) {
                    for (int j = 0; j < personnelFields[i].length; j++) {
                        personnel = Double.parseDouble(personnelFields[i][j].getText());
                        if (personnel < 0.0 || personnel > 9.99) {
                            personnel = Double.NaN;
                            JOptionPane.showMessageDialog(null, "Enter a value between 0 and 9.99",
                                    "warning", JOptionPane.WARNING_MESSAGE);
                            break outerLoop3;
                        }
                    }
                }
                if (Double.compare(personnel, Double.NaN) != 0) {
                    for (int i = 0; i < personnelFields.length; i++) {
                        for (int j = 0; j < personnelFields[i].length; j++) {
                            parameters.setPersonnelValue(i, j, Double.parseDouble(personnelFields[i][j].getText()));
                        }
                    }
                }

                outerLoop4:
                for (int i = 0; i < projectFields.length; i++) {
                    for (int j = 0; j < projectFields[i].length; j++) {
                        project = Double.parseDouble(projectFields[i][j].getText());
                        if (project < 0.0 || project > 9.99) {
                            project = Double.NaN;
                            JOptionPane.showMessageDialog(null, "Enter a value between 0 and 9.99",
                                    "warning", JOptionPane.WARNING_MESSAGE);
                            break outerLoop4;
                        }
                    }
                }
                if (Double.compare(project, Double.NaN) != 0) {
                    for (int i = 0; i < projectFields.length; i++) {
                        for (int j = 0; j < projectFields[i].length; j++) {
                            parameters.setProjectValue(i, j, Double.parseDouble(projectFields[i][j].getText()));
                        }
                    }
                }

                outerLoop5:
                for (int i = 0; i < userFields.length; i++) {
                    for (int j = 0; j < userFields[i].length; j++) {
                        userDefined = Double.parseDouble(userFields[i][j].getText());
                        if (userDefined < 0.0 || userDefined > 9.99) {
                            userDefined = Double.NaN;
                            JOptionPane.showMessageDialog(null, "Enter a value between 0 and 9.99",
                                    "warning", JOptionPane.WARNING_MESSAGE);
                            break outerLoop5;
                        }
                    }
                }
                if (Double.compare(userDefined, Double.NaN) != 0) {
                    for (int i = 0; i < userFields.length; i++) {
                        for (int j = 0; j < userFields[i].length; j++) {
                            parameters.setUserDefinedValue(i, j, Double.parseDouble(userFields[i][j].getText()));
                        }
                    }
                }

//                outerLoop6:
//                for (int i = 0; i < scheduleFields.length; i++) {
//                    for (int j = 0; j < scheduleFields[i].length; j++) {
//                        schedule = Double.parseDouble(scheduleFields[i][j].getText());
//                        if (schedule < 0.0 || schedule > 9.99) {
//                            schedule = Double.NaN;
//                            JOptionPane.showMessageDialog(null, "Enter a value between 0 and 9.99",
//                                    "warning", JOptionPane.WARNING_MESSAGE);
//                            break outerLoop6;
//                        }
//                    }
//                }
//                if (Double.compare(schedule, Double.NaN) != 0) {
//                    for (int i = 0; i < scheduleFields.length; i++) {
//                        for (int j = 0; j < scheduleFields[i].length; j++) {
//                            localCalibration.setScheduleValue(i, j, Double.parseDouble(scheduleFields[i][j].getText()));
//                        }
//                    }
//                }

                //Update database
                COINCOMOComponentManager.updateComponent(component, true);

                coincomo.refresh();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "Enter a numeric value",
                        "warning", JOptionPane.WARNING_MESSAGE);
            }
        } else if (e.getSource() == resetButton) {
            // Text Fields
            final double[][] eafWeights = COINCOMOComponentParameters.EAF_WEIGHTS;
            for (int i = 0; i < productNames.length; i++) {
                for (int j = 0; j < ratings.length; j++) {
                    productFields[ i][ j].setText("" + eafWeights[i][j]);
                }
            }

            for (int i = 0; i < platformNames.length; i++) {
                for (int j = 0; j < ratings.length; j++) {
                    platformFields[ i][ j].setText("" + eafWeights[i+5][j]);
                }
            }

            for (int i = 0; i < personnelNames.length; i++) {
                for (int j = 0; j < ratings.length; j++) {
                    personnelFields[ i][ j].setText("" + eafWeights[i+8][j]);
                }
            }

            for (int i = 0; i < projectNames.length; i++) {
                for (int j = 0; j < ratings.length; j++) {
                    // Index hacking to deal with SCED moving to the end of EAF list
                    int k = i;
                    if (i == 2) {
                        k = 4;
                    }
                    projectFields[ i][ j].setText("" + eafWeights[k+14][j]);
                }
            }

            for (int i = 0; i < userNames.length; i++) {
                for (int j = 0; j < ratings.length; j++) {
                    userFields[ i][ j].setText("" + eafWeights[i+16][j]);
                }
            }

//            for (int i = 0; i < scheduleNames.length; i++) {
//                for (int j = 0; j < ratings.length; j++) {
//                    scheduleFields[ i][ j].setText("" + eafWeights[i+18][j]);
//                }
//            }
        } else {
            // Free Resources ... Close Window
            this.dispose();
        }
    }

    /* Source: http://www.rgagnon.com/javadetails/java-0198.html */
    class JTextFieldLimit extends PlainDocument {

        private static final int LIMIT = 4;

        JTextFieldLimit() {
            super();
        }

        @Override
        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
            if (str == null) {
                return;
            }

            if ((getLength() + str.length()) <= LIMIT) {
                super.insertString(offset, str, attr);
            }
        }
    }
}
