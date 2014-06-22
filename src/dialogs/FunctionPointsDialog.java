/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package dialogs;

import core.COINCOMOComponent;
import core.COINCOMOComponentParameters;
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
public class FunctionPointsDialog extends JDialog implements ActionListener {

    private JPanel functionPointPanel = new JPanel();
    private String[] driverNames = new String[]{
        "Internal Logical Files (Files)",
        "External Interface Files (Interfaces)",
        "External Inputs (Inputs)",
        "External Outputs (Outputs)",
        "External Inquiries (Inquiries)"
    };
    private String[] ratings = new String[]{
        "Low", "Average", "High"
    };
    private JTextField[][] functionPointFields = new JTextField[driverNames.length][ratings.length];
    private JButton applyButton = new JButton("Apply");
    private JButton resetButton = new JButton("Reset");
    private JButton closeButton = new JButton("Close");
    private COINCOMO coincomo;
    private COINCOMOComponent component = null;
    private COINCOMOComponentParameters parameters = null;

    public FunctionPointsDialog(COINCOMO frame) {
        super(frame);

        this.coincomo = frame;
        this.component = (COINCOMOComponent) this.coincomo.getCurrentUnit();
        this.parameters = this.component.getParameters();

        this.setModal(true);

        this.setTitle("Function Points - " + this.component.getName());

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

        for (int i = 0; i < driverNames.length; i++) {
            for (int j = 0; j < ratings.length; j++) {
                functionPointFields[ i][ j] = new JTextField();
                JTextFieldLimit fieldLimit = new JTextFieldLimit();
                functionPointFields[i][j].setDocument(fieldLimit);
                final int x = i, y = j;
                functionPointFields[ i][ j].addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusGained(java.awt.event.FocusEvent evt) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                functionPointFields[ x][ y].selectAll();
                            }
                        });
                    }
                });
            }
        }
        
        DialogGridCreation.createTextFieldGrid(functionPointPanel, driverNames, ratings, functionPointFields);

        // Panel
        JPanel southPanel = new JPanel();
        southPanel.add(applyButton);
        southPanel.add(resetButton);
        southPanel.add(closeButton);

        this.setLayout(new BorderLayout());
        this.add(functionPointPanel);
        this.add(southPanel, BorderLayout.SOUTH);

        this.setValues();

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(this.getOwner().getX() + 100, this.getOwner().getY() + 100);
        this.setResizable(false);
        this.setSize(400, 270);
        this.setVisible(true);
    }

    private void setValues() {
        /*COINCOMOLocalCalibration localCal = 
         ((COINCOMOSystem)this.coincomo.getHierarchyPanel().getCOINCOMOTreeRoot().getCOINCOMOUnit()).getLocalCalibration();*/
        for (int i = 0; i < driverNames.length; i++) {
            for (int j = 0; j < ratings.length; j++) {
                this.functionPointFields[i][j].setText(((COINCOMOComponent) coincomo.getCurrentUnit()).getParameters().getFunctionPointsValue(i, j) + "");
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == applyButton) {
            COINCOMOComponentParameters localCal = ((COINCOMOComponent) coincomo.getCurrentUnit()).getParameters();
            int functionPoint = 0;
            try {
                for (int i = 0; i < driverNames.length; i++) {
                    for (int j = 0; j < ratings.length; j++) {
                        functionPoint = Integer.parseInt(this.functionPointFields[i][j].getText());
                        if (functionPoint < 0 || functionPoint > 25) {
                            JOptionPane.showMessageDialog(null, "Please enter a value between 0 and 25 for <" + driverNames[i] + ">\'s <" + ratings[j] + "> column.",
                                    "warning", JOptionPane.WARNING_MESSAGE);
                            this.functionPointFields[i][j].requestFocusInWindow();
                            this.functionPointFields[i][j].selectAll();
                            return;
                        }
                    }
                }

                for (int i = 0; i < driverNames.length; i++) {
                    for (int j = 0; j < ratings.length; j++) {
                        localCal.setFunctionPointsValue(i, j, Integer.parseInt(this.functionPointFields[ i][ j].getText()));
                        coincomo.refresh();
                    }
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "Enter a numeric value",
                        "warning", JOptionPane.WARNING_MESSAGE);
            }
            //DialogGridCreation.createTextFieldGrid( functionPointPanel , driverNames , ratings , functionPointFields );
        } else if (e.getSource() == resetButton) {
            COINCOMOComponentParameters localCal = ((COINCOMOComponent) coincomo.getCurrentUnit()).getParameters();
            for (int i = 0; i < driverNames.length; i++) {
                for (int j = 0; j < ratings.length; j++) {
                    localCal.setFunctionPointsValue(i, j, COINCOMOComponentParameters.FP_WEIGHTS[i][j]);
                    functionPointFields[ i][ j].setText(COINCOMOComponentParameters.FP_WEIGHTS[i][j] + "");
                }
            }
        } else {
            // Free Resources ... Close Window
            this.dispose();
        }

    }

        /* Source: http://www.rgagnon.com/javadetails/java-0198.html */
    class JTextFieldLimit extends PlainDocument {

        private static final int LIMIT = 2;

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
