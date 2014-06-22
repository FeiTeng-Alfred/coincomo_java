/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package panels;

import core.COINCOMOComponent;
import core.COINCOMOSubComponent;
import core.COINCOMOSubSystem;
import core.COINCOMOSystem;
import core.COINCOMOUnit;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import main.GlobalMethods;

/**
 *
 * @author Raed Shomali
 */
public class SystemOverviewPanel extends JPanel implements HyperlinkListener {

    private JTextPane summaryReportTextPane = new JTextPane();
    private JScrollPane summaryScroller = new JScrollPane(summaryReportTextPane);
    private COINCOMOSystem currentSystem = null;
    private COINCOMOSubSystem currentSubSystem = null;
    private COINCOMOComponent currentComponent = null;
    private DecimalFormat format1Decimal = new DecimalFormat("0.0");
    private DecimalFormat format2Decimals = new DecimalFormat("0.00");
    private DecimalFormat format2DecimalWithComma = new DecimalFormat("#,##0.00");

    public SystemOverviewPanel(COINCOMOUnit unit) {
        // Text Pane
        TitledBorder dictionaryTitleBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Summary Report");
        dictionaryTitleBorder.setTitleColor(Color.BLUE);
        dictionaryTitleBorder.setTitlePosition(TitledBorder.BELOW_TOP);
        dictionaryTitleBorder.setTitleJustification(TitledBorder.CENTER);
        summaryReportTextPane.setEditable(false);
        summaryScroller.setBorder(dictionaryTitleBorder);
        summaryReportTextPane.setContentType("text/html");
        summaryReportTextPane.setMargin(new Insets(20, 20, 20, 20));

        summaryReportTextPane.addHyperlinkListener(this);

        updateReport(unit);

        // GUI
        this.setLayout(new BorderLayout());

        this.add(summaryScroller);
        this.add(new JLabel("    "), BorderLayout.NORTH);
        this.add(new JLabel("    "), BorderLayout.SOUTH);
        this.add(new JLabel("    "), BorderLayout.EAST);
        this.add(new JLabel("    "), BorderLayout.WEST);
    }

    public void updateReport(COINCOMOUnit unit) {
        // For Efficient Appending ...
        StringBuilder output = new StringBuilder();

        if (unit instanceof COINCOMOSystem) {
            output.append("<h2> System " + unit.getName() + "\'s Overview: </h2>");

            this.currentSystem = (COINCOMOSystem) unit;
            this.currentSubSystem = null;
            this.currentComponent = null;
        } else if (unit instanceof COINCOMOSubSystem) {
            output.append("<h2> Sub System " + unit.getName() + "\'s Overview: </h2>");

            this.currentSubSystem = (COINCOMOSubSystem) unit;
            this.currentComponent = null;
        } else {
            output.append("<h2> Component " + unit.getName() + "\'s Overview: </h2>");

            this.currentComponent = (COINCOMOComponent) unit;
        }

        // To color each row differently
        String color = "";

        // If there are sub units ...
        if (!unit.getListOfSubUnits().isEmpty()) {
            color = "DDDDDD";

            output.append("<table border = '1' cellpadding = '1'  cellspacing = '1' width = '100%' align = 'center'>");
            output.append("<tr>");
            output.append("<th bgcolor = " + color + "> Name </th>");
            output.append("<th bgcolor = " + color + "> Size SLOC </th>");
            output.append("<th bgcolor = " + color + "> Cost </th>");
            output.append("<th bgcolor = " + color + "> Staff </th>");
            output.append("<th bgcolor = " + color + "> Effort </th>");
            output.append("<th bgcolor = " + color + "> Schedule </th>");
            output.append("</tr>");
        } else {
            output.append("There are currently no " + ((unit instanceof COINCOMOSystem) ? "Sub systems " : "Components ") + "in " + unit.getName() + "<br />");
        }

        // Get all subunits
        ArrayList<COINCOMOUnit> orderedVector = unit.getListOfSubUnits();

        // Used for determining an Odd Row from an Even one
        int rowIndex = 0;

        long totalSLOC = 0;
        double totalCost = 0;
        double totalStaff = 0;
        double totalEffort = 0;
        double totalSchedule = 0;

        // Go Through subunits
        for (int c = 0; c < orderedVector.size(); c++) {
            COINCOMOUnit tempUnit = (COINCOMOUnit) orderedVector.get(c);

            // Determine Row being even or odd
            if (rowIndex++ % 2 == 0) {
                color = "CCFFFF";
            } else {
                color = "white";
            }

            output.append("<tr align = 'center'>");

            // If a Sub Component ... No Drilling More Down ...
            if (tempUnit instanceof COINCOMOSubComponent) {
                output.append("<td bgcolor = " + color + ">" + tempUnit.getName() + "</td>");
            } else {
                output.append("<td bgcolor = " + color + ">" + "<a href = 'http://" + tempUnit.getUnitID() + "' >" + tempUnit.getName() + "</a>" + "</td>");
            }

            totalSLOC += tempUnit.getSLOC();
            totalCost += tempUnit.getCost();
            totalStaff += tempUnit.getStaff();
            totalEffort += tempUnit.getEffort();
            totalSchedule += tempUnit.getSchedule();

            output.append("<td bgcolor = " + color + ">" + GlobalMethods.FormatLongWithComma(tempUnit.getSLOC()) + "</td>");
            output.append("<td bgcolor = " + color + "> $" + format2DecimalWithComma.format(GlobalMethods.roundOff(tempUnit.getCost(), 2)) + "</td>");
            output.append("<td bgcolor = " + color + ">" + format1Decimal.format(GlobalMethods.roundOff(tempUnit.getStaff(), 1)) + "</td>");
            output.append("<td bgcolor = " + color + ">" + format2Decimals.format(GlobalMethods.roundOff(tempUnit.getEffort(), 2)) + "</td>");
            output.append("<td bgcolor = " + color + ">" + format2Decimals.format(GlobalMethods.roundOff(tempUnit.getSchedule(), 2)) + "</td>");
            output.append("</tr>");

            // No More Records ?
            if (c == orderedVector.size() - 1) {
                color = "CCFF66";

                output.append("<tr align = 'center'>");
                output.append("<th bgcolor = " + color + ">" + "Total" + "</th>");
                output.append("<th bgcolor = " + color + ">" + GlobalMethods.FormatLongWithComma(totalSLOC) + "</th>");
                output.append("<th bgcolor = " + color + "> $" + format2DecimalWithComma.format(GlobalMethods.roundOff(totalCost, 2)) + "</th>");
                if (!(tempUnit instanceof COINCOMOSubComponent)) {
                    output.append("<th bgcolor = DDDDDD>&nbsp;</th>");
                } else {
                    output.append("<th bgcolor = " + color + "> " + format1Decimal.format(GlobalMethods.roundOff(totalStaff, 1)) + "</th>");
                }
                output.append("<th bgcolor = " + color + ">" + format2Decimals.format(GlobalMethods.roundOff(totalEffort, 2)) + "</th>");
                if (!(tempUnit instanceof COINCOMOSubComponent)) {
                    output.append("<th bgcolor = DDDDDD>&nbsp;</th>");
                } else {
                    output.append("<th bgcolor = " + color + "> " + format2Decimals.format(GlobalMethods.roundOff(totalSchedule, 1)) + "</th>");
                }
                output.append("</tr>");
            }
        }

        output.append("</table>");

        // If Not the System Page ...
        if (!(unit instanceof COINCOMOSystem)) {
            // Print "Back" Link ...
            output.append("<br /> &nbsp; <a href = 'https://" + unit.getParent().getUnitID() + "' >" + "Go Back" + "</a>");
        }

        summaryReportTextPane.setText(output.toString());
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
        // User Clicked The Link
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            /*
             * To Differentiate between a back Button and a show details one 
             * I used 'https' for "Go Back" and 'http' for More Details
             */

            // If Back ??
            if (e.getURL().toString().startsWith("https://")) {
                // If On the Component Page ...
                if (this.currentComponent != null) {
                    // Go Back to Sub System Page ..
                    updateReport(currentSubSystem);
                } else {
                    // Go Back To System Page ..
                    updateReport(currentSystem);
                }
            } else {
                if (this.currentComponent != null) {
                    // Go To Sub Component Page ...
                    updateReport(currentComponent.getListOfSubUnits().get(Integer.parseInt(e.getURL().toString().replaceAll("http://", ""))));
                } else if (this.currentSubSystem != null) {
                    // Go To Component Page ...
                    updateReport(currentSubSystem.getListOfSubUnits().get(Integer.parseInt(e.getURL().toString().replaceAll("http://", ""))));
                } else {
                    // Go to Sub System Page
                    updateReport(currentSystem.getListOfSubUnits().get(Integer.parseInt(e.getURL().toString().replaceAll("http://", ""))));
                }
            }
        }
    }
}
