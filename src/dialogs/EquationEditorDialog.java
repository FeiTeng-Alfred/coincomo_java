/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package dialogs;

import core.COINCOMOComponent;
import core.COINCOMOComponentParameters;
import database.COINCOMOComponentManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import main.COINCOMO;
import main.GlobalMethods;
import main.Icons;

/**
 *
 * @author Raed Shomali
 */
public class EquationEditorDialog extends JDialog implements ActionListener {

    private JTextField aTextField = new JTextField();
    private JTextField bTextField = new JTextField();
    private JTextField cTextField = new JTextField();
    private JTextField dTextField = new JTextField();
    private JButton applyButton = new JButton("Apply");
    private JButton resetButton = new JButton("Reset");
    private JButton closeButton = new JButton("Close");
    private COINCOMO coincomo;
    private COINCOMOComponent component;
    private COINCOMOComponentParameters parameters;

    public EquationEditorDialog(COINCOMO coincomo) {
        super(coincomo);

        this.coincomo = coincomo;
        this.component = (COINCOMOComponent) this.coincomo.getCurrentUnit();
        this.parameters = this.component.getParameters();

        this.setModalityType(ModalityType.APPLICATION_MODAL);

        this.setTitle("Equation Editor - " + this.component.getName());

        // Update Status ...
        GlobalMethods.updateStatusBar("Done.", coincomo);

        JTextFieldLimit aTextFieldLimit = new JTextFieldLimit();
        JTextFieldLimit bTextFieldLimit = new JTextFieldLimit();
        JTextFieldLimit cTextFieldLimit = new JTextFieldLimit();
        JTextFieldLimit dTextFieldLimit = new JTextFieldLimit();

        aTextField.setDocument(aTextFieldLimit);
        bTextField.setDocument(bTextFieldLimit);
        cTextField.setDocument(cTextFieldLimit);
        dTextField.setDocument(dTextFieldLimit);

        aTextField.setText(this.parameters.getA() + "");
        bTextField.setText(this.parameters.getB() + "");
        cTextField.setText(this.parameters.getC() + "");
        dTextField.setText(this.parameters.getD() + "");

        aTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        aTextField.selectAll();
                    }
                });
            }
        });
        bTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        bTextField.selectAll();
                    }
                });
            }
        });
        cTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        cTextField.selectAll();
                    }
                });
            }
        });
        dTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        dTextField.selectAll();
                    }
                });
            }
        });

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

        // Labels
        JLabel effortEquationLabel = new JLabel("Effort Estimation Equation");
        JLabel scheduleEquationLabel = new JLabel("Schedule Estimation Equation");

        effortEquationLabel.setHorizontalAlignment(JLabel.CENTER);
        scheduleEquationLabel.setHorizontalAlignment(JLabel.CENTER);

        effortEquationLabel.setForeground(Color.BLUE);
        scheduleEquationLabel.setForeground(Color.BLUE);

        // Text Fields
        aTextField.setPreferredSize(new Dimension(40, 25));
        bTextField.setPreferredSize(new Dimension(40, 25));
        cTextField.setPreferredSize(new Dimension(40, 25));
        dTextField.setPreferredSize(new Dimension(40, 25));

        aTextField.setFont(new Font("courier", 0, 12));
        bTextField.setFont(new Font("courier", 0, 12));
        cTextField.setFont(new Font("courier", 0, 12));
        dTextField.setFont(new Font("courier", 0, 12));

        // GUI

        // set up the Effort Estimation Equation Panel
        JPanel effortEquationPanel = new JPanel();
        JPanel effortEquationPanel2 = new JPanel();

        JLabel effortPart1 = new JLabel("<html>PM = </html>");
        JLabel effortPart2 = new JLabel("<html> x Size<sup>E</sup> x &prod; (EM<sub>1</sub> , ... , EM<sub>17</sub>) + PM<sub>auto</sub></html>");
        effortEquationPanel.add(effortPart1);
        effortEquationPanel.add(aTextField);
        effortEquationPanel.add(effortPart2);

        JLabel effortPart3 = new JLabel("<html>A</html>");
        effortEquationPanel2.add(effortPart3);

        JPanel effortEquationPanel3 = new JPanel();
        JPanel effortEquationPanel4 = new JPanel();
        JPanel effortEquationPanel5 = new JPanel();

        JLabel effortPart4 = new JLabel("<html>E = </html>");
        JLabel effortPart5 = new JLabel("<html> + 0.01 x &sum; (SF<sub>1</sub> , ... , SF<sub>5</sub>)<sup>&nbsp;</sup></html>");
        effortEquationPanel3.add(effortPart4);
        effortEquationPanel3.add(bTextField);
        effortEquationPanel3.add(effortPart5);

        JLabel effortPart6 = new JLabel("<html>B</html>");
        effortEquationPanel4.add(effortPart6);

        JLabel effortPart7 = new JLabel("<html>PM<sub>auto</sub> = Adapted SLOC x (AT / 100) / ATPROD<sup>&nbsp;</sup></html>");
        effortEquationPanel5.add(effortPart7);

        // set up the Schedule Estimation Equation Panel
        JPanel scheduleEquationPanel = new JPanel();
        JPanel scheduleEquationPanel2 = new JPanel();
        JPanel scheduleEquationPanel3 = new JPanel();
        JPanel scheduleEquationPanel4 = new JPanel();

        JLabel schedulePart1 = new JLabel("<html>TDEV = [ </html>");
        JLabel schedulePart2 = new JLabel("<html> x (PM<sub>NS</sub> )<sup>F</sup> ] x SCED% / 100 </html>");
        scheduleEquationPanel.add(schedulePart1);
        scheduleEquationPanel.add(cTextField);
        scheduleEquationPanel.add(schedulePart2);

        JLabel schedulePart3 = new JLabel("<html>C</html>");
        scheduleEquationPanel2.add(schedulePart3);

        JLabel schedulePart4 = new JLabel("<html>F = </html>");
        JLabel schedulePart5 = new JLabel("<html> + 0.2 x [E - B]</html>");
        scheduleEquationPanel3.add(schedulePart4);
        scheduleEquationPanel3.add(dTextField);
        scheduleEquationPanel3.add(schedulePart5);

        JLabel schedulePart6 = new JLabel("<html>D</html>");
        scheduleEquationPanel4.add(schedulePart6);

        // South Panel
        JPanel southPanel = new JPanel();
        southPanel.add(applyButton);
        southPanel.add(resetButton);
        southPanel.add(closeButton);

        this.setLayout(null);
        this.add(effortEquationLabel);
        this.add(effortEquationPanel);
        this.add(effortEquationPanel2);
        this.add(effortEquationPanel3);
        this.add(effortEquationPanel4);
        this.add(effortEquationPanel5);
        this.add(scheduleEquationLabel);
        this.add(scheduleEquationPanel);
        this.add(scheduleEquationPanel2);
        this.add(scheduleEquationPanel3);
        this.add(scheduleEquationPanel4);
        this.add(southPanel);

        effortEquationLabel.setBounds(0, 5, 500, 25);
        effortEquationPanel.setBounds(0, 25, 500, 30);
        effortEquationPanel2.setBounds(150, 50, 20, 25);
        effortEquationPanel3.setBounds(0, 75, 500, 30);
        effortEquationPanel4.setBounds(175, 100, 20, 25);
        effortEquationPanel5.setBounds(0, 125, 500, 30);

        scheduleEquationLabel.setBounds(0, 170, 500, 25);
        scheduleEquationPanel.setBounds(0, 190, 500, 30);
        scheduleEquationPanel2.setBounds(190, 215, 20, 25);
        scheduleEquationPanel3.setBounds(0, 240, 500, 30);
        scheduleEquationPanel4.setBounds(210, 265, 20, 25);

        southPanel.setBounds(0, 310, 500, 50);

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(this.getOwner().getX() + 100, this.getOwner().getY() + 100);
        this.setResizable(false);
        this.setSize(500, 380);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == applyButton) {
            double aParameter = Double.NaN;
            double bParameter = Double.NaN;
            double cParameter = Double.NaN;
            double dParameter = Double.NaN;

            aTextField.setText(aTextField.getText().trim());
            bTextField.setText(bTextField.getText().trim());
            cTextField.setText(cTextField.getText().trim());
            dTextField.setText(dTextField.getText().trim());

            if (!GlobalMethods.isNonNegativeDouble(aTextField.getText())) {
                JOptionPane.showMessageDialog(this, "Please enter a numeric value.", "INVALID NUMBER ERROR", JOptionPane.WARNING_MESSAGE);
                aTextField.selectAll();
                aTextField.requestFocusInWindow();
                return;
            }
            if (!GlobalMethods.isNonNegativeDouble(bTextField.getText())) {
                JOptionPane.showMessageDialog(this, "Please enter a numeric value.", "INVALID NUMBER ERROR", JOptionPane.WARNING_MESSAGE);
                bTextField.selectAll();
                bTextField.requestFocusInWindow();
                return;
            }
            if (!GlobalMethods.isNonNegativeDouble(cTextField.getText())) {
                JOptionPane.showMessageDialog(this, "Please enter a numeric value.", "INVALID NUMBER ERROR", JOptionPane.WARNING_MESSAGE);
                cTextField.selectAll();
                cTextField.requestFocusInWindow();
                return;
            }
            if (!GlobalMethods.isNonNegativeDouble(dTextField.getText())) {
                JOptionPane.showMessageDialog(this, "Please enter a numeric value.", "INVALID NUMBER ERROR", JOptionPane.WARNING_MESSAGE);
                dTextField.selectAll();
                dTextField.requestFocusInWindow();
                return;
            }

            aParameter = Double.parseDouble(aTextField.getText());
            bParameter = Double.parseDouble(bTextField.getText());
            cParameter = Double.parseDouble(cTextField.getText());
            dParameter = Double.parseDouble(dTextField.getText());

            if (aParameter < 0.0d || aParameter > 10.0d) {
                JOptionPane.showMessageDialog(this, "Please enter a value between 0 and 10.", "INVALID NUMBER RANGE ERROR", JOptionPane.WARNING_MESSAGE);
                aTextField.selectAll();
                aTextField.requestFocusInWindow();
                return;
            }
            if (bParameter < 0.0d || bParameter > 10.0d) {
                JOptionPane.showMessageDialog(this, "Please enter a value between 0 and 10.", "INVALID NUMBER RANGE ERROR", JOptionPane.WARNING_MESSAGE);
                bTextField.selectAll();
                bTextField.requestFocusInWindow();
                return;
            }
            if (cParameter < 0.0d || cParameter > 10.0d) {
                JOptionPane.showMessageDialog(this, "Please enter a value between 0 and 10.", "INVALID NUMBER RANGE ERROR", JOptionPane.WARNING_MESSAGE);
                cTextField.selectAll();
                cTextField.requestFocusInWindow();
                return;
            }
            if (dParameter < 0.0d || dParameter > 10.0d) {
                JOptionPane.showMessageDialog(this, "Please enter a value between 0 and 10.", "INVALID NUMBER RANGE ERROR", JOptionPane.WARNING_MESSAGE);
                dTextField.selectAll();
                dTextField.requestFocusInWindow();
                return;
            }

            if ((Double.compare(aParameter, Double.NaN) != 0) && (Double.compare(bParameter, Double.NaN) != 0)
                    && (Double.compare(cParameter, Double.NaN) != 0) && (Double.compare(dParameter, Double.NaN) != 0)) {
                parameters.setA(Double.parseDouble(aTextField.getText()));
                parameters.setB(Double.parseDouble(bTextField.getText()));
                parameters.setC(Double.parseDouble(cTextField.getText()));
                parameters.setD(Double.parseDouble(dTextField.getText()));

                COINCOMOComponentManager.updateComponent(component, true);
                coincomo.refresh();
            }
        } else if (e.getSource() == resetButton) {
            parameters.setA(COINCOMOComponentParameters.A);
            parameters.setB(COINCOMOComponentParameters.B);
            parameters.setC(COINCOMOComponentParameters.C);
            parameters.setD(COINCOMOComponentParameters.D);
            aTextField.setText(COINCOMOComponentParameters.A + "");
            bTextField.setText(COINCOMOComponentParameters.B + "");
            cTextField.setText(COINCOMOComponentParameters.C + "");
            dTextField.setText(COINCOMOComponentParameters.D + "");
        } else {
            // Close Window ...
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

            int leftLength = LIMIT - getLength();
            if (leftLength > str.length()) {
                leftLength = str.length();
            }
            if (leftLength > 0) {
                super.insertString(offset, str.substring(0, leftLength), attr);
            }
        }
    }
}
