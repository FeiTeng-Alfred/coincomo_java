/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package extensions;

import core.COINCOMOUnit;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Raed Shomali
 */
public class COINCOMOTreeNode extends DefaultMutableTreeNode {

    COINCOMOUnit COINCOMOUnit = null;

    /**
     * @param unit determines the type of the unit associated with the node
     */
    public COINCOMOTreeNode(COINCOMOUnit unit) {
        super(unit.getName());

        this.COINCOMOUnit = unit;
    }

    /**
     *
     * @return the unit that is associated with the tree node
     */
    public COINCOMOUnit getCOINCOMOUnit() {
        return COINCOMOUnit;
    }

    /**
     *
     * @param COINCOMOUnit is the unit associated with the tree node
     */
    public void setCOINCOMOUnit(COINCOMOUnit COINCOMOUnit) {
        this.COINCOMOUnit = COINCOMOUnit;
    }
}
