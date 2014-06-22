/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package extensions;

import core.COINCOMOComponent;
import core.COINCOMOSubSystem;
import core.COINCOMOSystem;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import main.Icons;

/**
 *
 * @author Raed Shomali
 */
public class COINCOMOTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        // Convert To Our Class Defined COINCOMO Node
        COINCOMOTreeNode node = (COINCOMOTreeNode) value;

        if (node.getCOINCOMOUnit() instanceof COINCOMOSystem) // Is System Node ?
        {
            setIcon(Icons.SYSTEM_ICON);
        } else if (node.getCOINCOMOUnit() instanceof COINCOMOSubSystem) // Is Subsystem Node ?
        {
            setIcon(Icons.SUBSYSTEM_ICON);
        } else if (node.getCOINCOMOUnit() instanceof COINCOMOComponent) // Is Component ?
        {
            setIcon(Icons.COMPONENT_ICON);
        }

        return this;
    }
}