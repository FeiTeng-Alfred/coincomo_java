/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package panels;

import core.COINCOMOComponent;
import core.COINCOMOSubSystem;
import core.COINCOMOSystem;
import core.COINCOMOUnit;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import main.COINCOMO;
import main.GlobalMethods;

/**
 *
 * @author Raed Shomali
 */
public class OverviewsAndGraphsPanel extends JPanel {

    private JTabbedPane tabs = new JTabbedPane();
    private COPSEMOPanel copsemoPanel = new COPSEMOPanel();
    private COINCOMO coincomo;
    private JPanel overviewPanel = null;

    public OverviewsAndGraphsPanel(COINCOMO coincomo) {
        this.coincomo = coincomo;
        tabs.setFocusable(false);

        overviewPanel = new JPanel();
        tabs.addTab("Overview", overviewPanel);
        tabs.addTab("COPSEMO", new JPanel());
        //tabs.addTab( "Extension 1" , new JPanel() );
        //tabs.addTab( "Extension 2" , new JPanel() );

        tabs.setEnabledAt(1, false);

        this.setLayout(new BorderLayout());
        this.add(tabs);
    }

    public JPanel getOverviewPanel() {
        return overviewPanel;
    }

    public COPSEMOPanel getCOPSEMOPanel() {
        return this.copsemoPanel;
    }

    public void clearOverviewTab() {
        tabs.setSelectedIndex(0);
        tabs.setTitleAt(0, "Overview");
        overviewPanel = new JPanel();
        tabs.setComponentAt(0, overviewPanel);
    }

    /**
     *
     * @param selectedUnit is used to update the Tab with respective unit
     * content
     */
    public void updateOverviewTabWith(COINCOMOUnit selectedUnit) {
        tabs.setEnabledAt(1, false);
        tabs.setComponentAt(1, new JPanel());

        // Determine Which Panel To Use
        if (selectedUnit instanceof COINCOMOSystem) {
            overviewPanel = new SystemOverviewPanel((COINCOMOSystem) selectedUnit);
            tabs.setComponentAt(0, overviewPanel);
        } else if (selectedUnit instanceof COINCOMOSubSystem) {
            overviewPanel = new SubSystemOverviewPanel((COINCOMOSubSystem) selectedUnit);
            tabs.setComponentAt(0, overviewPanel);
        } else // COINCOMO Component
        {
            overviewPanel = new ComponentOverviewPanel(coincomo, this, (COINCOMOComponent) selectedUnit);
            tabs.setComponentAt(0, overviewPanel);

            tabs.setEnabledAt(1, true);
            tabs.setComponentAt(1, copsemoPanel);

            // Show the Loading Text ...
            copsemoPanel.getCOPSEMOGraphPanel().setIsLoading(true);
            copsemoPanel.getCOPSEMOGraphPanel().repaint();
            GlobalMethods.updateStatusBar("Loading COPSEMO ...", this.coincomo);

            this.copsemoPanel.updateCOPSEMO((COINCOMOComponent) selectedUnit);

            // Show the Graph ..
            copsemoPanel.getCOPSEMOGraphPanel().setIsLoading(false);
            copsemoPanel.getCOPSEMOGraphPanel().repaint();
            GlobalMethods.updateStatusBar("COPSEMO Loaded.", this.coincomo);
        }

        // Change Overview's Tab's Title
        tabs.setTitleAt(0, selectedUnit.getName() + "'s Overview");

        // If COPSEMO Is Disabled ...
        if (!tabs.isEnabledAt(1)) {
            // Show the Overview Panel
            tabs.setSelectedIndex(0);
        }
    }
}
