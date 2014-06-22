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
public class ScaleFactorsDialog extends JDialog implements ActionListener {

    /*private double[][] scaleFactorValue = new double[][]{{6.20, 4.96, 3.72, 2.48, 1.24, 0.00},
     {5.07, 4.05, 3.04, 2.03, 1.01, 0.00},
     {7.07, 5.65, 4.24, 2.83, 1.41, 0.00},
     {5.48, 4.38, 3.29, 2.19, 1.10, 0.00},
     {7.80, 6.24, 4.68, 3.12, 1.56, 0.00}
     };*/
    private JPanel scaleFactorPanel = new JPanel();
    private String[] driverNames = new String[]{
        "PREC", "FLEX", "RESL", "TEAM", "PMAT"
    };
    private String[] ratings = new String[]{
        "VLO", "LO", "NOM", "HI", "VHI", "XHI"
    };
    private JTextField[][] scaleFactorFields = new JTextField[driverNames.length][ratings.length];
    private JButton applyButton = new JButton("Apply");
    private JButton resetButton = new JButton("Reset");
    private JButton closeButton = new JButton("Close");
    private COINCOMO coincomo;
    private COINCOMOComponent component;
    private COINCOMOComponentParameters parameters;

    public ScaleFactorsDialog(COINCOMO frame) {
        super(frame);

        this.coincomo = frame;
        this.component = (COINCOMOComponent) this.coincomo.getCurrentUnit();
        this.parameters = this.component.getParameters();

        this.setModal(true);

        this.setTitle("Scale Factors - " + this.component.getName());

        GlobalMethods.updateStatusBar("Done.", frame);

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

        //COINCOMOLocalCalibration localCal =
        //        ((COINCOMOSystem) frame.getHierarchyPanel().getCOINCOMOTreeRoot().getCOINCOMOUnit()).getLocalCalibration();
        for (int i = 0; i < driverNames.length; i++) {
            for (int j = 0; j < ratings.length; j++) {
                scaleFactorFields[ i][ j] = new JTextField();
                JTextFieldLimit fieldLimit = new JTextFieldLimit();
                scaleFactorFields[i][j].setDocument(fieldLimit);
                scaleFactorFields[i][j].setText(((COINCOMOComponent) coincomo.getCurrentUnit()).getParameters().getScaleFactorsValue(i, j) + "");
                final int x = i, y = j;
                scaleFactorFields[i][j].addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusGained(java.awt.event.FocusEvent evt) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                scaleFactorFields[x][y].selectAll();
                            }
                        });
                    }
                });
            }
        }

        // GUI
        DialogGridCreation.createTextFieldGrid(scaleFactorPanel, driverNames, ratings, scaleFactorFields);

        // Panel
        JPanel southPanel = new JPanel();
        southPanel.add(applyButton);
        southPanel.add(resetButton);
        southPanel.add(closeButton);

        this.setLayout(new BorderLayout());
        this.add(scaleFactorPanel);
        this.add(southPanel, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(this.getOwner().getX() + 100, this.getOwner().getY() + 100);
        this.setResizable(false);
        this.setSize(400, 270);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == applyButton) {
            double scaleFactor = Double.NaN;

            try {
                outerLoop:
                for (int i = 0; i < driverNames.length; i++) {
                    for (int j = 0; j < ratings.length; j++) {
                        scaleFactor = Double.parseDouble(this.scaleFactorFields[ i][ j].getText());
                        if (scaleFactor < 0.0 || scaleFactor > 9.99) {
                            scaleFactor = Double.NaN;
                            JOptionPane.showMessageDialog(null, "Enter a value between 0 and 9.99",
                                    "warning", JOptionPane.WARNING_MESSAGE);
                            break outerLoop;
                        }
                    }
                }
                if (Double.compare(scaleFactor, Double.NaN) != 0) {
                    for (int i = 0; i < driverNames.length; i++) {
                        for (int j = 0; j < ratings.length; j++) {
                            parameters.setScaleFactorsValue(i, j, Double.parseDouble(this.scaleFactorFields[ i][ j].getText()));
                        }
                    }
                }

                COINCOMOComponentManager.updateComponent(component, true);
                
                coincomo.refresh();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "Enter a numeric value",
                        "warning", JOptionPane.WARNING_MESSAGE);
            }

        } else if (e.getSource() == resetButton) {
            COINCOMOComponentParameters localCal = ((COINCOMOComponent) coincomo.getCurrentUnit()).getParameters();
            for (int i = 0; i < driverNames.length; i++) {
                for (int j = 0; j < ratings.length; j++) {
                    localCal.setScaleFactorsValue(i, j, COINCOMOComponentParameters.SF_WEIGHTS[i][j]);
                    scaleFactorFields[ i][ j].setText(COINCOMOComponentParameters.SF_WEIGHTS[i][j] + "");
                }
            }
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