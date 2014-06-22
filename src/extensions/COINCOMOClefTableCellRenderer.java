/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package extensions;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Raed Shomali
 */
public class COINCOMOClefTableCellRenderer extends JLabel implements TableCellRenderer {

    private int startColorColumn;
    private int stopColorColumn;

    public COINCOMOClefTableCellRenderer(int changeColorColumn) {
        this(0, changeColorColumn);
    }

    public COINCOMOClefTableCellRenderer(int startColorColumn, int stopColorColumn) {
        // to Apply Font and Color ...
        setOpaque(true);

        setFont(new Font("courier", 0, 12));

        this.startColorColumn = startColorColumn;
        this.stopColorColumn = stopColorColumn;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int colIndex) {
        if (colIndex >= this.startColorColumn && colIndex <= this.stopColorColumn)//colIndex == 1 || colIndex == 2 || colIndex == 3 || colIndex == 4 )
        {
            setBackground(new Color(255, 255, 0, 130));
        } else {
            setBackground(Color.decode("#DDDDDD"));
        }

        setHorizontalAlignment(SwingConstants.CENTER);

        // Validations ...
        if (value == null) {
            setText("");
        } else {
            setText(value.toString());
        }

        return this;
    }
}