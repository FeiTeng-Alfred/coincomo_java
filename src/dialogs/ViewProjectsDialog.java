/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package dialogs;

import core.COINCOMOSystem;
import database.COINCOMOSystemManager;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import main.COINCOMO;
import main.GlobalMethods;
import main.Icons;
import main.MenuItemMethods;
import panels.HierarchyPanel;
import panels.OverviewsAndGraphsPanel;

/**
 *
 * @author Raed Shomali
 */
public class ViewProjectsDialog extends JDialog implements ActionListener {

    COINCOMO coincomo = null;
    Container c = getContentPane();
    JButton loadButton = new JButton("Load");
    JButton deleteButton = new JButton("Delete");
    JButton closeButton = new JButton("Close");
    DefaultListModel listModel = new DefaultListModel();
    JList availableSystemsList = new JList(listModel);
    JScrollPane listScroller = new JScrollPane(availableSystemsList);
    public static ArrayList<COINCOMOSystem> allSystems = null;

    /**
     *
     * @param parentFrame is the original frame that generated this Dialog
     */
    public ViewProjectsDialog(JFrame parentFrame) {
        super(parentFrame);

        coincomo = (COINCOMO) parentFrame;
        this.setTitle("View Projects...");
        this.setModal(true);

        // Buttons
        loadButton.addActionListener(this);
        deleteButton.addActionListener(this);
        closeButton.addActionListener(this);

        loadButton.setFocusable(false);
        deleteButton.setFocusable(false);
        closeButton.setFocusable(false);

        loadButton.setIcon(Icons.LOAD_PROJECT_ICON);
        deleteButton.setIcon(Icons.DELETE_PROJECT_ICON);
        closeButton.setIcon(Icons.CLOSE_ICON);

        // List
        TitledBorder availableSystemsTitleBorder = BorderFactory.createTitledBorder("Available Systems");
        availableSystemsTitleBorder.setTitleColor(Color.DARK_GRAY);
        availableSystemsTitleBorder.setTitlePosition(TitledBorder.BELOW_TOP);
        availableSystemsTitleBorder.setTitleJustification(TitledBorder.CENTER);
        listScroller.setBorder(availableSystemsTitleBorder);

        // GUI
        c.setLayout(null);

        c.add(listScroller);

        c.add(loadButton);
        c.add(deleteButton);
        c.add(closeButton);

        listScroller.setBounds(20, 20, 290, 220);

        loadButton.setBounds(20, 260, 90, 25);
        deleteButton.setBounds(120, 260, 90, 25);
        closeButton.setBounds(220, 260, 90, 25);

        GlobalMethods.updateStatusBar("Loading Available Systems ...", Color.BLACK, coincomo);

        loadButton.setEnabled(false);
        deleteButton.setEnabled(false);
        closeButton.setEnabled(false);

        // Update in a GUI Thread
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    // Rest a Bit ... So the GUI would Render
                    Thread.sleep(100);

                    allSystems = COINCOMOSystemManager.getAllSystems();

                    for (int i = 0; i < allSystems.size(); i++) {
                        COINCOMOSystem tempSystem = allSystems.get(i);

                        // Add to the list
                        listModel.addElement(tempSystem.getName());
                    }

                    loadButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    closeButton.setEnabled(true);

                    GlobalMethods.updateStatusBar("Available Systems have been loaded.", Color.BLACK, coincomo);
                } catch (InterruptedException ex) {
                }
            }
        });

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(this.getOwner().getX() + 100, this.getOwner().getY() + 100);
        this.setResizable(false);
        this.setSize(335, 330);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        final HierarchyPanel hierarchyPanel = coincomo.getHierachyPanel();
        final OverviewsAndGraphsPanel overviewsAndGraphsPanel = coincomo.getOverviewsAndGraphcsPanel();
        
        if (e.getSource() == loadButton) {
            // Is Nothing Selected ?
            if (availableSystemsList.isSelectionEmpty()) {
                GlobalMethods.updateStatusBar("No System was selected for loading.", Color.RED, coincomo);
            } else {
                if (availableSystemsList.getSelectedIndices().length > 1) {
                    GlobalMethods.updateStatusBar("One system at most can be selected for loading.", Color.RED, coincomo);
                } else {
                    loadButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                    closeButton.setEnabled(false);

                    GlobalMethods.updateStatusBar("Loading ...", Color.BLACK);

                    // Get a Reference of this Current Instance
                    final JDialog currentDialog = this;

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            // Get Selected System
                            COINCOMOSystem system = allSystems.get(availableSystemsList.getSelectedIndex());

                            // Load System From Database ...
                            COINCOMOSystemManager.loadSystem(system);

                            // Determine if the project is already opened in another COINCOMO window
                            COINCOMO someCOINCOMO = MenuItemMethods.hasActiveProject(system.getDatabaseID());
                            if (someCOINCOMO != null) {
                                someCOINCOMO.getFocus();

                                GlobalMethods.updateStatusBar("Project has been loaded.", coincomo);
                            } else {
                                // Determine if there is already an opened system
                                if (coincomo.getCurrentSystem() != null) {
                                    someCOINCOMO = new COINCOMO();
                                    someCOINCOMO.setCurrentSystem(system);
                                    someCOINCOMO.updateTitle();

                                    someCOINCOMO.getFocus();
                                    
                                    GlobalMethods.updateStatusBar("Project has been loaded.", coincomo);
                                } else {
                                    // Configure the menu bar
                                    coincomo.setCurrentSystem(system);
                                    coincomo.updateTitle();

                                    loadButton.setEnabled(true);
                                    deleteButton.setEnabled(true);
                                    closeButton.setEnabled(true);

                                    GlobalMethods.updateStatusBar("Project has been loaded.", coincomo);
                                }
                            }

                            currentDialog.dispose();
                        }
                    });
                }
            }
        } else if (e.getSource() == deleteButton) {
            // Is Nothing Selected ?
            if (availableSystemsList.isSelectionEmpty()) {
                GlobalMethods.updateStatusBar("No System was selected for deletion.", Color.RED);
            } else {
                int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to continue with the Deletion?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                // If User didnt Select YES ...
                if (result != JOptionPane.YES_OPTION) {
                    // Abort Deletion ...
                    return;
                }

                loadButton.setEnabled(false);
                deleteButton.setEnabled(false);
                closeButton.setEnabled(false);

                GlobalMethods.updateStatusBar("Deleting ...", Color.BLACK);

                // Create a Thread
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int selectedSystems[] = availableSystemsList.getSelectedIndices();

                        ArrayList<COINCOMOSystem> systemsToBeDeleted = new ArrayList<COINCOMOSystem>();

                        for (int i = 0; i < selectedSystems.length; i++) {
                            final COINCOMOSystem system = (COINCOMOSystem) allSystems.get(selectedSystems[i]);

                            // Remove From GUI
                            listModel.remove(selectedSystems[i] - i);

                            systemsToBeDeleted.add(system);

                            // Close the system in any COINCOMO window if necessary
                            coincomo.closeProject(system);
                        }

                        // Remove From Database
                        COINCOMOSystemManager.deleteSystems(systemsToBeDeleted);

                        // Refresh Available Systems List ...
                        for (int i = 0; i < systemsToBeDeleted.size(); i++) {
                            allSystems.remove(systemsToBeDeleted.get(i));
                        }

                        loadButton.setEnabled(true);
                        deleteButton.setEnabled(true);
                        closeButton.setEnabled(true);

                        GlobalMethods.updateStatusBar("Project(s) have been deleted.", Color.BLACK, coincomo);
                    }
                });
            }
        } else {
            // Exit ...
            GlobalMethods.updateStatusBar("", Color.BLACK, coincomo);
            this.dispose();
        }
    }
}
