/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package main;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author Raed Shomali
 */
public class DialogGridCreation {

    public static void createTextFieldGrid(Container container, String[] driverNames, String[] ratings) {
        container.setLayout(new GridBagLayout());

        //Column Header
        for (int j = 0; j < ratings.length; j++) {
            JLabel ratingLabel = new JLabel(ratings[j]);
            ratingLabel.setFont(new Font("arial", 1, 11));
            ratingLabel.setHorizontalAlignment(SwingConstants.CENTER);

            container.add(ratingLabel, new GridBagConstraints(j + 1, 0, 1, 1, 100.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        }

        //Rows
        for (int i = 0; i < driverNames.length; i++) {
            JLabel driverLabel = new JLabel(driverNames[i]);
            driverLabel.setFont(new Font("arial", 1, 11));

            container.add(driverLabel, new GridBagConstraints(0, i + 1, 1, 1, 100.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

            for (int j = 0; j < ratings.length; j++) {
                JTextField field = new JTextField();
                field.setHorizontalAlignment(JTextField.CENTER);
                field.setFont(new Font("courier", 0, 12));
                field.setPreferredSize(new Dimension(0, field.getPreferredSize().height));

                container.add(field, new GridBagConstraints(j + 1, i + 1, 1, 1, 100.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 35, 0));
            }
        }

        container.add(new JPanel(), new GridBagConstraints(0, driverNames.length + 1, ratings.length, 1, 100.0, 100.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 35, 0));
    }

    //Overload createTextFieldGrid function
    public static void createTextFieldGrid(Container container, String[] driverNames, String[] ratings, JTextField[][] field) {
        container.setLayout(new GridBagLayout());

        //Column Header
        for (int j = 0; j < ratings.length; j++) {
            JLabel ratingLabel = new JLabel(ratings[j]);
            ratingLabel.setFont(new Font("arial", 1, 11));
            ratingLabel.setHorizontalAlignment(SwingConstants.CENTER);

            container.add(ratingLabel, new GridBagConstraints(j + 1, 0, 1, 1, 100.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        }

        //Rows
        for (int i = 0; i < driverNames.length; i++) {
            JLabel driverLabel = new JLabel(driverNames[i]);
            driverLabel.setFont(new Font("arial", 1, 11));

            container.add(driverLabel, new GridBagConstraints(0, i + 1, 1, 1, 100.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

            for (int j = 0; j < ratings.length; j++) {
                field[i][j].setHorizontalAlignment(JTextField.CENTER);
                field[i][j].setPreferredSize(new Dimension(0, field[i][j].getPreferredSize().height));
                field[i][j].setFont(new Font("courier", 0, 12));

                container.add(field[i][j], new GridBagConstraints(j + 1, i + 1, 1, 1, 100.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 35, 0));
            }

            // Custom hack to disable fields that shouldn't be edited.
            // EAF cost drivers
            if (driverNames[i].equals("RELY")) {
                field[i][ratings.length-1].setEnabled(false);
                field[i][ratings.length-1].setEditable(false);
            } else if (driverNames[i].equals("DATA")) {
                field[i][0].setEnabled(false);
                field[i][0].setEditable(false);
                field[i][ratings.length-1].setEnabled(false);
                field[i][ratings.length-1].setEditable(false);
            } else if (driverNames[i].equals("DOCU")) {
                field[i][ratings.length-1].setEnabled(false);
                field[i][ratings.length-1].setEditable(false);
            } else if (driverNames[i].equals("CPLX")) {
            } else if (driverNames[i].equals("RUSE")) {
                field[i][0].setEnabled(false);
                field[i][0].setEditable(false);
            } else if (driverNames[i].equals("TIME")) {
                field[i][0].setEnabled(false);
                field[i][0].setEditable(false);
                field[i][1].setEnabled(false);
                field[i][1].setEditable(false);
            } else if (driverNames[i].equals("STOR")) {
                field[i][0].setEnabled(false);
                field[i][0].setEditable(false);
                field[i][1].setEnabled(false);
                field[i][1].setEditable(false);
            } else if (driverNames[i].equals("PVOL")) {
                field[i][0].setEnabled(false);
                field[i][0].setEditable(false);
                field[i][ratings.length-1].setEnabled(false);
                field[i][ratings.length-1].setEditable(false);
            } else if (driverNames[i].equals("ACAP")) {
                field[i][ratings.length-1].setEnabled(false);
                field[i][ratings.length-1].setEditable(false);
            } else if (driverNames[i].equals("APEX")) {
                field[i][ratings.length-1].setEnabled(false);
                field[i][ratings.length-1].setEditable(false);
            } else if (driverNames[i].equals("PCAP")) {
                field[i][ratings.length-1].setEnabled(false);
                field[i][ratings.length-1].setEditable(false);
            } else if (driverNames[i].equals("PLEX")) {
                field[i][ratings.length-1].setEnabled(false);
                field[i][ratings.length-1].setEditable(false);
            } else if (driverNames[i].equals("LTEX")) {
                field[i][ratings.length-1].setEnabled(false);
                field[i][ratings.length-1].setEditable(false);
            } else if (driverNames[i].equals("PCON")) {
                field[i][ratings.length-1].setEnabled(false);
                field[i][ratings.length-1].setEditable(false);
            } else if (driverNames[i].equals("TOOL")) {
                field[i][ratings.length-1].setEnabled(false);
                field[i][ratings.length-1].setEditable(false);
            } else if (driverNames[i].equals("SITE")) {
            } else if (driverNames[i].equals("USR1")) {
            } else if (driverNames[i].equals("USR2")) {
            } else if (driverNames[i].equals("SCED")) {
                field[i][ratings.length-1].setEnabled(false);
                field[i][ratings.length-1].setEditable(false);
            }
            // SF cost drivers
            if (driverNames[i].equals("PREC")) {
            } else if (driverNames[i].equals("FLEX")) {
            } else if (driverNames[i].equals("RESL")) {
            } else if (driverNames[i].equals("TEAM")) {
            } else if (driverNames[i].equals("PMAT")) {
            }
        }

        container.add(new JPanel(), new GridBagConstraints(0, driverNames.length + 1, ratings.length, 1, 100.0, 100.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 35, 0));
    }
}