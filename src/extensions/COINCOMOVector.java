/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package extensions;

import java.util.Vector;

/**
 *
 * @param <String> determines this is a Vector of Strings
 * @author Raed Shomali
 */
public class COINCOMOVector<String> extends Vector<String> {
    // Used to be able to link it to a sub component

    int rowID = 0;

    /**
     *
     * @return a unique long value that represents that row ID in the table
     */
    public int getRowID() {
        return rowID;
    }

    /**
     *
     * @param rowID is used to set the Row ID's value
     */
    public void setRowID(int rowID) {
        this.rowID = rowID;
    }
}
