/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package extensions;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Raed Shomali
 */
public class COINCOMOComboBoxCellEditor extends JLabel implements ListCellRenderer {

    JSeparator separator;

    public COINCOMOComboBoxCellEditor() {
        setOpaque(true);
        setBorder(new EmptyBorder(1, 1, 1, 1));
        separator = new JSeparator(JSeparator.HORIZONTAL);
    }

    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        String str = (value == null) ? "" : value.toString();
        if ("SEPARATOR".equals(str)) {
            return separator;
        }
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setFont(list.getFont());
        setText(str);
        return this;
    }
}