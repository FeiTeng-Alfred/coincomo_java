/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package extensions;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Raed Shomali
 */
public class COINCOMOCheckBoxTableCellRenderer extends JCheckBox implements TableCellRenderer {

    /**
     * Constructor
     */
    public COINCOMOCheckBoxTableCellRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setSelected(value != null && value.equals("true") ? true : false);

        setFont(new Font("courier", 1, 12));

        return this;
    }
}