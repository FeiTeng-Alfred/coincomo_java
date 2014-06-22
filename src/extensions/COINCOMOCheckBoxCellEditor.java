/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package extensions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 *
 * @author Raed Shomali
 */
public class COINCOMOCheckBoxCellEditor extends DefaultCellEditor {
    // CheckBox In Cell

    JCheckBox checkbox = new JCheckBox();
    // Checkbox's Caption
    String caption = "";

    /**
     * Constructor
     */
    public COINCOMOCheckBoxCellEditor() {
        super(new JCheckBox());

        checkbox.setHorizontalAlignment(JLabel.CENTER);

        checkbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        checkbox.setSelected(value != null && value.equals("true") ? true : false);

        // Set the Check Box's Text
        caption = (value.equals("true") ? false : true) + "";

        return checkbox;
    }

    @Override
    public Object getCellEditorValue() {
        //the Checkbox text
        return caption;
    }

    @Override
    public boolean stopCellEditing() {
        return super.stopCellEditing();
    }

    @Override
    public void fireEditingStopped() {
        super.fireEditingStopped();
    }
}