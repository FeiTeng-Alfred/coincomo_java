/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package dialogs;

import core.COINCOMOConstants.DatabaseType;
import database.COINCOMODatabaseManager;
import database.DBConnection;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import main.COINCOMO;
import main.GlobalMethods;
import main.Icons;

/**
 *
 * @author guolin
 */
public class ConnectDialog extends JDialog implements ActionListener, ItemListener {

    Container c = getContentPane();
    COINCOMO coincomo = null;
    JLabel titleLegendLabel = new JLabel("");
    JLabel nameLabel = new JLabel("Name:");
    JLabel ipAddressLabel = new JLabel("IP Address:");
    JLabel usernameLabel = new JLabel("Userame:");
    JLabel passwordLabel = new JLabel("Password:");
    JButton connectButton = new JButton("Connect");
    JButton closeButton = new JButton("Close");
    JTextField nameTextField = new JTextField();
    JTextField ipAddressTextField = new JTextField();
    JTextField portTextField = new JTextField();
    JTextField usernameTextField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JRadioButton postgresRadio = new JRadioButton("Postgres", true);
    JRadioButton mySQLRadio = new JRadioButton("MySQL");
    JRadioButton defaultPortRadio = new JRadioButton("Default Port", true);
    JRadioButton portRadio = new JRadioButton("Port");

    /**
     *
     * @param coincomo is the original frame that generated this Dialog
     */
    public ConnectDialog(COINCOMO coincomo) {
        super(coincomo);

        GlobalMethods.updateStatusBar("Connecting ...", coincomo);

        this.setTitle("Connect...");
        this.setModal(true);

        this.coincomo = coincomo;

        // Labels
        nameLabel.setFont(new Font("arial", 1, 11));
        ipAddressLabel.setFont(new Font("arial", 1, 11));
        usernameLabel.setFont(new Font("arial", 1, 11));
        passwordLabel.setFont(new Font("arial", 1, 11));

        // Defaults ...
        nameTextField.setText(COINCOMODatabaseManager.DB_NAME);
        ipAddressTextField.setText(COINCOMODatabaseManager.HOST);
        portTextField.setText(COINCOMODatabaseManager.PORT);
        usernameTextField.setText(COINCOMODatabaseManager.USERNAME);
        passwordField.setText(COINCOMODatabaseManager.PASSWORD);

        // Texts Fields
        portTextField.setEnabled(false);

        nameTextField.setFont(new Font("courier", 0, 12));
        ipAddressTextField.setFont(new Font("courier", 0, 12));
        portTextField.setFont(new Font("courier", 0, 12));
        usernameTextField.setFont(new Font("courier", 0, 12));
        passwordField.setFont(new Font("courier", 0, 12));

        // Buttons
        connectButton.addActionListener(this);
        closeButton.addActionListener(this);

        connectButton.setIcon(Icons.CONNECT_ICON);
        closeButton.setIcon(Icons.CLOSE_ICON);

        connectButton.setFocusable(false);
        closeButton.setFocusable(false);

        // Radio Buttons & Groups
        postgresRadio.addItemListener(this);
        mySQLRadio.addItemListener(this);
        defaultPortRadio.addItemListener(this);
        portRadio.addItemListener(this);

        ButtonGroup databaseButtonGroup = new ButtonGroup();
        databaseButtonGroup.add(postgresRadio);
        databaseButtonGroup.add(mySQLRadio);
        postgresRadio.setEnabled(true);
        mySQLRadio.setEnabled(false);

        ButtonGroup portButtonGroup = new ButtonGroup();
        portButtonGroup.add(defaultPortRadio);
        portButtonGroup.add(portRadio);

        // Legend
        TitledBorder legendTitleBorder = BorderFactory.createTitledBorder("Site Parameters");
        legendTitleBorder.setTitleColor(Color.BLUE);
        titleLegendLabel.setBorder(legendTitleBorder);

        // GUI
        c.setLayout(null);

        c.add(titleLegendLabel);

        c.add(nameLabel);
        c.add(ipAddressLabel);
        c.add(usernameLabel);
        c.add(passwordLabel);

        c.add(nameTextField);
        c.add(ipAddressTextField);
        c.add(portTextField);
        c.add(usernameTextField);
        c.add(passwordField);

        c.add(postgresRadio);
        c.add(mySQLRadio);
        c.add(defaultPortRadio);
        c.add(portRadio);

        c.add(connectButton);
        c.add(closeButton);

        titleLegendLabel.setBounds(20, 10, 260, 290);

        nameLabel.setBounds(40, 35, 100, 22);
        nameTextField.setBounds(40, 55, 220, 22);
        ipAddressLabel.setBounds(40, 85, 100, 22);
        ipAddressTextField.setBounds(40, 105, 220, 22);

        postgresRadio.setBounds(40, 135, 100, 22);
        mySQLRadio.setBounds(140, 135, 100, 22);
        defaultPortRadio.setBounds(40, 165, 100, 22);
        portRadio.setBounds(40, 190, 50, 22);
        portTextField.setBounds(110, 190, 60, 22);

        usernameLabel.setBounds(40, 230, 70, 22);
        passwordLabel.setBounds(40, 260, 70, 22);
        usernameTextField.setBounds(110, 230, 150, 22);
        passwordField.setBounds(110, 260, 150, 22);

        connectButton.setBounds(30, 310, 110, 25);
        closeButton.setBounds(160, 310, 110, 25);

        // Set the connect button as the default button
        getRootPane().setDefaultButton(connectButton);

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(this.getOwner().getX() + 100, this.getOwner().getY() + 100);
        this.setResizable(false);

        // Set the focus to the password field
        this.pack();
        boolean willFocus = passwordField.requestFocusInWindow();
        this.setSize(305, 380);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == connectButton) {
            // Validations ...
            if (nameTextField.getText().isEmpty()) {
                GlobalMethods.updateStatusBar("Database Name Can NOT be Empty !", Color.RED, coincomo);

                return;
            } else if (ipAddressTextField.getText().isEmpty()) {
                GlobalMethods.updateStatusBar("IP Address Can NOT Be Empty !", Color.RED, coincomo);

                return;
            } else if (portTextField.getText().isEmpty() && portRadio.isSelected()) {
                GlobalMethods.updateStatusBar("Port Can NOT Be Empty !", Color.RED, coincomo);

                return;
            }

            // Disable the Buttons (Give Loading Effect)
            connectButton.setEnabled(false);
            closeButton.setEnabled(false);

            final JDialog currentDialog = this;

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    COINCOMODatabaseManager.DB_NAME = nameTextField.getText();
                    COINCOMODatabaseManager.DB_DRIVER = postgresRadio.isSelected() ? COINCOMODatabaseManager.POSTGRES_DRIVER : COINCOMODatabaseManager.MYSQL_DRIVER;
                    COINCOMODatabaseManager.DB_TYPE = postgresRadio.isSelected() ? COINCOMODatabaseManager.POSTGRES_TYPE : COINCOMODatabaseManager.MYSQL_TYPE;
                    COINCOMODatabaseManager.HOST = ipAddressTextField.getText();
                    COINCOMODatabaseManager.PORT = postgresRadio.isSelected() ? COINCOMODatabaseManager.POSTGRES_PORT : COINCOMODatabaseManager.MYSQL_PORT;
                    COINCOMODatabaseManager.PORT = defaultPortRadio.isSelected() ? COINCOMODatabaseManager.PORT : portTextField.getText();
                    COINCOMODatabaseManager.USERNAME = usernameTextField.getText();
                    COINCOMODatabaseManager.PASSWORD = String.valueOf(passwordField.getPassword());

                    // Is registering successful ?
                    try {

                        COINCOMODatabaseManager.registerDriver();
                        // Can we create a connection ?
                        DBConnection connection = COINCOMODatabaseManager.getConnection();
                        if (connection != null) {
                            COINCOMO.setConnected();
                            if (postgresRadio.isSelected()) {
                                COINCOMO.setDatabaseType(DatabaseType.PostgreSQL);
                            } else if (mySQLRadio.isSelected()) {
                                COINCOMO.setDatabaseType(DatabaseType.MySQL);
                            }
                            coincomo.resetMenuBar();

                            GlobalMethods.updateStatusBar("Connected to Database.", coincomo);

                            COINCOMODatabaseManager.disconnect(connection);

                            // Close Window
                            currentDialog.dispose();
                        }
                    } catch (Exception e) {
                        GlobalMethods.updateStatusBar("Could Not Register Database Driver.", Color.RED, coincomo);
                    }

                    // Set Back to Default
                    connectButton.setEnabled(true);
                    closeButton.setEnabled(true);
                }
            });
        } else {
            // Exit ...
            if (COINCOMODatabaseManager.getConnection() == null) {
                GlobalMethods.updateStatusBar("Not connected to database.", coincomo);
            }
            this.dispose();
        }
    }

    public void itemStateChanged(ItemEvent e) {
        // Only Check Items When Selected ...
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (e.getSource() == postgresRadio) {
                if (defaultPortRadio.isSelected()) {
                    portTextField.setText(COINCOMODatabaseManager.POSTGRES_PORT);
                }
            } else if (e.getSource() == mySQLRadio) {
                if (defaultPortRadio.isSelected()) {
                    portTextField.setText(COINCOMODatabaseManager.MYSQL_PORT);
                }
            } else if (e.getSource() == defaultPortRadio) {
                // Set to Default
                portTextField.setEnabled(false);
                if (postgresRadio.isSelected()) {
                    portTextField.setText(COINCOMODatabaseManager.POSTGRES_PORT);
                } else if (mySQLRadio.isSelected()) {
                    portTextField.setText(COINCOMODatabaseManager.MYSQL_PORT);
                }
            } else if (e.getSource() == portRadio) {
                // Set to Enabled when Custom Port Radio Is selected
                portTextField.setEnabled(true);
            }
        }
    }
}
