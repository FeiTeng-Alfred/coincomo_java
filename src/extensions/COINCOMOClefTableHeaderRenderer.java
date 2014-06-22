/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package extensions;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Raed Shomali
 */
public class COINCOMOClefTableHeaderRenderer extends JLabel implements TableCellRenderer {

    private Font tableHeaderFont;
    private FontMetrics tableHeaderFontMetrics;
    private int tableHeaderFontHeight;
    private JCheckBox checkBox = new JCheckBox();

    public COINCOMOClefTableHeaderRenderer(double height) {
        this.setBackground(Color.DARK_GRAY);
        this.setForeground(Color.WHITE);

        this.setOpaque(true);

        this.setHorizontalTextPosition(SwingConstants.CENTER);
        this.setVerticalTextPosition(SwingConstants.BOTTOM);
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setVerticalAlignment(SwingConstants.BOTTOM);

        this.setBorder(BorderFactory.createEtchedBorder());

        tableHeaderFont = getFont();
        tableHeaderFont = tableHeaderFont.deriveFont(Font.BOLD);
        tableHeaderFont = tableHeaderFont.deriveFont(11f);

        this.setFont(tableHeaderFont);

        tableHeaderFontMetrics = getFontMetrics(tableHeaderFont);
        tableHeaderFontHeight = tableHeaderFontMetrics.getHeight();
        this.setPreferredSize(new Dimension(0, (int) (tableHeaderFontHeight * height)));//change the height of the header
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

      
            String v = (String) value;
            this.setText(v);

  
        return this;
    }
}