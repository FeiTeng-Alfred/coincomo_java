/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package panels;

import core.COINCOMOComponent;
import core.COINCOMOConstants;
import core.COINCOMOSubSystem;
import core.COINCOMOSystem;
import core.COINCOMOUnit;
import database.COINCOMOComponentManager;
import database.COINCOMOSubSystemManager;
import database.COINCOMOSystemManager;
import extensions.COINCOMOTreeNode;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import main.COINCOMO;
import main.GlobalMethods;
import main.Icons;

/**
 *
 * @author Raed Shomali
 */
public class HierarchyPanel extends JPanel implements ActionListener, TreeSelectionListener, MouseListener {

    private COINCOMO coincomo = null;
    // Popup Menu
    private JPopupMenu popup = new JPopupMenu();
    // Popup Items
    private JMenuItem addUnitMenuItem = new JMenuItem("Add");
    private JMenuItem deleteUnitMenuItem = new JMenuItem("Delete");
    private JMenuItem cutUnitMenuItem = new JMenuItem("Cut");
    private JMenuItem copyUnitMenuItem = new JMenuItem("Copy");
    private JMenuItem pasteUnitMenuItem = new JMenuItem("Paste");
    private JMenuItem renameUnitMenuItem = new JMenuItem("Rename");
    // Tool Bar
    private JToolBar toolBar = new JToolBar();
    // Tool Bar Items
    private JButton addSubSystemButton = new JButton();
    private JButton deleteSubSystemButton = new JButton();
    private JButton addComponentButton = new JButton();
    private JButton deleteComponentButton = new JButton();
    private JButton cutComponentButton = new JButton();
    private JButton copyComponentButton = new JButton();
    private JButton pasteComponentButton = new JButton();
    // Tree
    // Define a new Node Tree, give it a name and a type
    private COINCOMOTreeNode root = null;
    private JTree tree;
    private JScrollPane treeScroller;
    // Keep Track of selected Node
    private COINCOMOTreeNode selectedNode = null;
    // Hold temporary new name
    private String tempName = null;

    public HierarchyPanel(COINCOMO c) {
        this.coincomo = c;
        tree = new JTree(root);
        treeScroller = new JScrollPane(tree);

        // Popup Menu
        addUnitMenuItem.addActionListener(this);
        deleteUnitMenuItem.addActionListener(this);
        renameUnitMenuItem.addActionListener(this);

        renameUnitMenuItem.setIcon(Icons.RENAME_ICON);

        // Toolbar Buttons
        addSubSystemButton.setIcon(Icons.ADD_SUBSYSTEM_ICON);
        deleteSubSystemButton.setIcon(Icons.DELETE_SUBSYSTEM_ICON);
        addComponentButton.setIcon(Icons.ADD_COMPONENT_ICON);
        deleteComponentButton.setIcon(Icons.DELETE_COMPONENT_ICON);

        addSubSystemButton.setEnabled(false);
        deleteSubSystemButton.setEnabled(false);
        addComponentButton.setEnabled(false);
        deleteComponentButton.setEnabled(false);

        addSubSystemButton.addActionListener(this);
        deleteSubSystemButton.addActionListener(this);
        addComponentButton.addActionListener(this);
        deleteComponentButton.addActionListener(this);

        addSubSystemButton.setBorderPainted(false);
        deleteSubSystemButton.setBorderPainted(false);
        addComponentButton.setBorderPainted(false);
        deleteComponentButton.setBorderPainted(false);

        addSubSystemButton.setFocusPainted(false);
        deleteSubSystemButton.setFocusPainted(false);
        addComponentButton.setFocusPainted(false);
        deleteComponentButton.setFocusPainted(false);

        addSubSystemButton.setBackground(Color.decode("#FFFFFF"));
        deleteSubSystemButton.setBackground(Color.decode("#FFFFFF"));
        addComponentButton.setBackground(Color.decode("#FFFFFF"));
        deleteComponentButton.setBackground(Color.decode("#FFFFFF"));

        toolBar.setBackground(Color.decode("#FFFFFF"));
        toolBar.setBorder(BorderFactory.createEtchedBorder());

        toolBar.add(addSubSystemButton);
        toolBar.add(deleteSubSystemButton);
        toolBar.add(addComponentButton);
        toolBar.add(deleteComponentButton);

        // ---------------------------------------------------------------
        // Tree

        tree.addMouseListener(this);
        tree.addTreeSelectionListener(this);
        tree.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.WHITE));
        tree.setCellRenderer(new extensions.COINCOMOTreeCellRenderer());

        this.setLayout(new BorderLayout());
        this.add(toolBar, BorderLayout.NORTH);
        this.add(treeScroller);
    }

    public void clearHierarchyTree() {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        
        this.root = null;
        this.selectedNode = null;
        model.setRoot(null);
        
        addSubSystemButton.setEnabled(false);
        addComponentButton.setEnabled(false);
        deleteSubSystemButton.setEnabled(false);
        deleteComponentButton.setEnabled(false);
    }

    public void makeHierarchyTree(COINCOMOSystem system) {
        DefaultTreeModel model = (DefaultTreeModel) this.tree.getModel();
        
        if (system == null) {
            if (getCOINCOMOTreeRoot() != null) {
                System.out.println("Why are you making an hierarchy tree when new system is null with existing system loaded?!");
                
                getJTree().setSelectionPath(new TreePath(getCOINCOMOTreeRoot().getPath()));
                setCOINCOMOTreeRoot(null);
                model.setRoot(null);
            }
        } else {
            this.root = new COINCOMOTreeNode(system);
            model.setRoot(this.root);
            this.tree.setSelectionPath(new TreePath(this.root.getPath()));
            createTree(this.root, system);
        }
    }

    public void actionPerformed(ActionEvent e) {
        // If nothing is selected ...
        if (selectedNode == null) {
            // Exit
            return;
        }

        // If Popup's Items Are Selected
        if (e.getSource() == addUnitMenuItem) {
            // If System is Selected ..
            /*if ( selectedNode.getCOINCOMOUnit() instanceof COINCOMOSystem )
             {
             addUnit() ;
             }
             else // Sub System is Selected ...*/
            addSelectedNodeSubUnit();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    renameUnitMenuItem.doClick();
                }
            });
        } else if (e.getSource() == deleteUnitMenuItem) {
            // If Subsystem is Selected
            /*if ( selectedNode.getCOINCOMOUnit() instanceof COINCOMOSubSystem )
             {
             deleteSelectedNode() ;
             }
             else    // If Component Is Selected*/
            deleteSelectedNode();
        } else if (e.getSource() == renameUnitMenuItem) {
            String result;
            if (tempName == null) {
                result = JOptionPane.showInputDialog(coincomo, "Please enter the new name:", selectedNode.getCOINCOMOUnit().getName());
            } else {
                result = JOptionPane.showInputDialog(coincomo, "Please enter the new name:", tempName);
            }

            //check if the name is null
            if (result == null) {
                tempName = null;
                return;
            } else if (selectedNode.getCOINCOMOUnit().getName().equals(result)) {
                tempName = null;
                return;
            }

            coincomo.getCurrentSystem().setDirty();

            //name shouldn't contain <, >, / or \
           /* if(result.contains("<") || result.contains(">") || result.contains("/") || result.contains("\\") || result.contains("&") ){
             JOptionPane.showMessageDialog(coincomo, "Name shouldn't have <, >, /, \\ and &", "SPECIAL CHARACTERS IN NAME ERROR", 0);
             result = null;
             actionPerformed(e);
             }*/
            if (result.contains("<") || result.contains(">") || result.contains("/") || result.contains("\\") || result.contains("&")
                    || result.contains("\n") || result.contains("\r") || result.contains("\t") || result.contains("\0") || result.contains("\f")
                    || result.contains("`") || result.contains("?") || result.contains("*") || result.contains("|") || result.contains("\"")
                    || result.contains(":")) {
                JOptionPane.showMessageDialog(this, "Name shouldn't have <, >, /, \\, &, /, newline, carriage return, tab, null, '\\f', `, ?, *, |, \" and :", "SPECIAL CHARACTERS IN NAME ERROR", 0);
                tempName = result.toString();
                result = null;
                actionPerformed(e);
                return;
            }
            if(result.length() > COINCOMOConstants.NAME_LENGTH) {
                 JOptionPane.showMessageDialog(this, "Name should no more than " + COINCOMOConstants.NAME_LENGTH + " charactors", "EXCEED NAME SIZE BOUNDARY ERROR", 0);
                tempName = result.toString();
                result = null;
                actionPerformed(e);
                return;
            }

            //check if the name is duplicated
            if (!(selectedNode.getCOINCOMOUnit() instanceof COINCOMOSystem)) { //duplicated name will not occur in system level(only one system)
                ArrayList<COINCOMOUnit> list = selectedNode.getCOINCOMOUnit().getParent().getListOfSubUnits();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) != selectedNode.getCOINCOMOUnit() && list.get(i).getName().equals(result)) {
                        JOptionPane.showMessageDialog(this, "Duplicated name in the same level", "DUPLICATED NAME ERROR", 0);
                        tempName = result.toString();
                        result = null;
                        actionPerformed(e);
                        return;
                    }
                }
            } else {
                /*
                 * (Larry) Please note that with the current way of handling renaming in this class, there is really no way to spawn a thread only
                 * to make sure the system name is not already in the database, unlike other levels where the consistency check can be done with
                 * already loaded project in whole. Sad to say, this is not the best way and will have latency.
                 */
                if (COINCOMOSystemManager.hasSystemName(result)) {
                    JOptionPane.showMessageDialog(this, "Duplicated system name in the same level", "DUPLICATED SYSTEM NAME ERROR", 0);
                    tempName = result.toString();
                    result = null;
                    actionPerformed(e);
                    return;
                }
            }

            // If Not Empty
            if (result != null && !result.trim().equals("")) {
                // Update Name of the COINCOMO Unit Inside the Tree Node
                selectedNode.getCOINCOMOUnit().setName(result);
                tempName = null;

                // Update Name of the Tree Node on the Tree
                selectedNode.setUserObject(result);
                ((DefaultTreeModel) tree.getModel()).nodeChanged(selectedNode);

                // Refresh
                this.tree.repaint();

                // Create a Thread
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (selectedNode.getCOINCOMOUnit() instanceof COINCOMOSystem) {
                            COINCOMOSystemManager.updateSystemName((COINCOMOSystem) selectedNode.getCOINCOMOUnit());
                            coincomo.updateTitle();
                        } else if (selectedNode.getCOINCOMOUnit() instanceof COINCOMOSubSystem) {
                            COINCOMOSubSystemManager.updateSubSystemName((COINCOMOSubSystem) selectedNode.getCOINCOMOUnit());
                        } else {
                            COINCOMOComponentManager.updateComponentName((COINCOMOComponent) selectedNode.getCOINCOMOUnit());
                        }

                        GlobalMethods.updateStatusBar( "New name has been saved.", Color.BLACK, coincomo);
                    }
                });
            } else {
                JOptionPane.showMessageDialog(coincomo, "Enter a valid string", "EMPTY STRING ERROR", 0);
                tempName = "";
                result = null;
                actionPerformed(e);
            }
            // Update Overview Tab
            //coincomo.getOverviewsAndGraphsPanel().updateOverviewTabWith( selectedNode.getCOINCOMOUnit() );
            coincomo.refresh();
            coincomo.setCurrentUnit(selectedNode.getCOINCOMOUnit());
        } // Tool Buttons Are Selected ...
        else if (e.getSource() == addSubSystemButton) {
            addSelectedNodeSubUnit();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    renameUnitMenuItem.doClick();
                }
            });
        } else if (e.getSource() == deleteSubSystemButton) {
            deleteSelectedNode();
        } else if (e.getSource() == addComponentButton) {
            addSelectedNodeSubUnit();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    renameUnitMenuItem.doClick();
                }
            });
        } else // Delete Component
        {
            deleteSelectedNode();
        }
    }

    public JTree getJTree() {
        return tree;
    }

    public COINCOMOTreeNode getCOINCOMOTreeRoot() {
        return this.root;
    }

    public void setCOINCOMOTreeRoot(COINCOMOTreeNode root) {
        this.root = root;
    }

    public void valueChanged(TreeSelectionEvent e) {
        selectedNode = (COINCOMOTreeNode) (e.getPath().getLastPathComponent());

        // Set Everything to Disabled
        addSubSystemButton.setEnabled(false);
        deleteSubSystemButton.setEnabled(false);
        addComponentButton.setEnabled(false);
        deleteComponentButton.setEnabled(false);

        // Clear Popup Menu
        popup.removeAll();

        if (coincomo.getCurrentSystem() == null) {
            selectedNode = null;
            return;
        } else if (selectedNode.getCOINCOMOUnit() instanceof COINCOMOSystem) {
            addSubSystemButton.setEnabled(true);

            // Update Popup Menu Items Accordingly ..
            addUnitMenuItem.setText("Add Sub System");
            addUnitMenuItem.setIcon(Icons.ADD_SUBSYSTEM_ICON);

            popup.add(addUnitMenuItem);
            popup.addSeparator();
            popup.add(renameUnitMenuItem);
        } else if (selectedNode.getCOINCOMOUnit() instanceof COINCOMOSubSystem) {
            addComponentButton.setEnabled(true);

            deleteSubSystemButton.setEnabled(true);

            // Update Popup Menu Items Accordingly ..            
            addUnitMenuItem.setText("Add Component");
            addUnitMenuItem.setIcon(Icons.ADD_COMPONENT_ICON);

            deleteUnitMenuItem.setText("Delete Sub System");

            deleteUnitMenuItem.setIcon(Icons.DELETE_SUBSYSTEM_ICON);

            popup.add(addUnitMenuItem);
            popup.add(deleteUnitMenuItem);
            popup.addSeparator();
            popup.add(renameUnitMenuItem);
        } else if (selectedNode.getCOINCOMOUnit() instanceof COINCOMOComponent) {
            deleteComponentButton.setEnabled(true);

            // Update Popup Menu Items Accordingly ..
            deleteUnitMenuItem.setText("Delete Component");

            deleteUnitMenuItem.setIcon(Icons.DELETE_COMPONENT_ICON);

            popup.add(deleteUnitMenuItem);
            popup.addSeparator();
            popup.add(renameUnitMenuItem);
        }

        // Update the Overview Tab
        if (selectedNode.getCOINCOMOUnit() != null) {
            coincomo.setCurrentUnit(selectedNode.getCOINCOMOUnit());
        }
    }

    public void mouseClicked(MouseEvent e) {
        TreePath treePath = tree.getPathForLocation(e.getX(), e.getY());

        if (treePath != null) {
            // Highlight the Selected Node through its Tree Path ...
            tree.setSelectionPath(treePath);

            // Is Right Click Clicked ? And On a Tree Node ?
            if (e.getButton() == 3) {
                // Show the Popup Menu
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    /**
     *
     * @param unitType is used to determine the type of the unit being added
     */
    public COINCOMOUnit addSelectedNodeSubUnit() {
        coincomo.getCurrentSystem().setDirty();
        COINCOMOUnit unitType = null;
        if (selectedNode.getCOINCOMOUnit() instanceof COINCOMOSystem) {
            unitType = COINCOMOSubSystemManager.insertSubSystem((COINCOMOSystem) selectedNode.getCOINCOMOUnit());
        } else if (selectedNode.getCOINCOMOUnit() instanceof COINCOMOSubSystem) {
            unitType = COINCOMOComponentManager.insertComponent((COINCOMOSubSystem) selectedNode.getCOINCOMOUnit());
        }

        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();

        // Define a new Node Tree, give it a name and a type
        COINCOMOTreeNode subUnitNode = new COINCOMOTreeNode(unitType);

        // Insert in Tree
        treeModel.insertNodeInto(subUnitNode, selectedNode, selectedNode.getChildCount());

        // Show Newly Added Node, and Select It
        tree.scrollPathToVisible(new TreePath(subUnitNode.getPath()));
        tree.setSelectionPath(new TreePath(subUnitNode.getPath()));

        // Status Has To be Updated After Selection Happens ...
        // Since "selectedNode" is the node that is 'currently' highlighted
        GlobalMethods.updateStatusBar("One " + (unitType instanceof COINCOMOSubSystem ? "Subsystem" : "Component") + " has been added.", coincomo);
        return unitType;
    }

    public void deleteSelectedNode() {
        this.getCOINCOMOTreeRoot().getCOINCOMOUnit().setDirty();
        final COINCOMOUnit unitType = selectedNode.getCOINCOMOUnit();
        final DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();

        // Get Parent of Node to Be Deleted
        final COINCOMOTreeNode parentNode = (COINCOMOTreeNode) selectedNode.getParent();
        // Remove Internally
        if (parentNode != null) {
            //parentNode.getCOINCOMOUnit().getListOfSubUnits().remove(unitType);
        }

        // Thread Variables ...
        final long copyOfID = unitType.getUnitID();
        final boolean isSubSystem = (unitType instanceof COINCOMOSubSystem);
        final String unitName = unitType.getName();
        final COINCOMOUnit unitParent = unitType.getParent();

        // Create a Thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                boolean isSuccessful = false;
                // Remove From Database
                if (isSubSystem) {
                    isSuccessful = COINCOMOSubSystemManager.deleteSubSystem((COINCOMOSubSystem) unitType);
                } else {
                    isSuccessful = COINCOMOComponentManager.deleteComponent((COINCOMOComponent) unitType);
                }

                if (!isSuccessful) {
                    //Something is preventing the database to complete deleting the unit, stop.
                    return;
                }

                // Update database
                if (isSubSystem) {
                    COINCOMOSystemManager.updateSystem((COINCOMOSystem) unitParent);
                } else {
                    COINCOMOSubSystemManager.updateSubSystem((COINCOMOSubSystem) unitParent, true);
                }

                GlobalMethods.updateStatusBar((isSubSystem ? "Subsystem " : "Component ") + unitName + " has been deleted.", coincomo);
                
                if (parentNode != null) {
                    // Remove node
                    treeModel.removeNodeFromParent(selectedNode);

                    // Select Parent Node
                    tree.setSelectionPath(new TreePath(parentNode.getPath()));
                }
            }
        });
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void createTree(COINCOMOTreeNode parentNode, COINCOMOUnit parentUnit) {
        // Get Tree Model For Addition of Nodes ...
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();

        // Get Ordered Vector ..
        ArrayList<COINCOMOUnit> orderedVector = parentUnit.getListOfSubUnits();
        // Loop though Sub Units ....
        for (int i = 0; i < orderedVector.size(); i++) {
            // Define a new Node Tree, give it a name and a type
            COINCOMOTreeNode subUnitNode = new COINCOMOTreeNode(orderedVector.get(i));

            // Insert in Tree
            treeModel.insertNodeInto(subUnitNode, parentNode, parentNode.getChildCount());

            // Expand ...
            tree.scrollPathToVisible(new TreePath(subUnitNode.getPath()));

            // As Long as its not a Component ... Do a Recursive Call ...
            if (!(subUnitNode.getCOINCOMOUnit() instanceof COINCOMOComponent)) {
                createTree(subUnitNode, subUnitNode.getCOINCOMOUnit());
            }
        }
    }

    public void renameNewProject() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                renameUnitMenuItem.doClick();
            }
        });
    }
}
