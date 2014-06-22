/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogs;

import core.COINCOMOConstants;
import database.COINCOMODatabaseManager;
import database.DBConnection;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import main.COINCOMO;
import main.GlobalMethods;
import main.Icons;

/**
 *
 * @author Zihao Zhu
 */
public class SignupDialog extends JDialog implements ActionListener {
    
    private int Dialogsign = 0;
    Container c = getContentPane();
    COINCOMO coincomo = null;
    JLabel titleLegendLabel = new JLabel("");
    JLabel usernameLabel = new JLabel("User ID:");
    JLabel passwordLabel = new JLabel("Password:");
    JLabel RepasswordLabel = new JLabel("Re-Password:");
    JButton SignupButton = new JButton("Log in");
    JButton closeButton = new JButton("Close");

    JTextField userloginidTextField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JPasswordField RepasswordField = new JPasswordField();
    

    /**
     *
     * @param coincomo is the original frame that generated this Dialog
     */
    public SignupDialog(COINCOMO coincomo,int sign) {   //sign : 1 for signup, 2 for login
        super(coincomo);
        Dialogsign=sign;
        GlobalMethods.updateStatusBar("Connecting ...", coincomo);

        this.setTitle("Connect...");
        this.setModal(true);

        this.coincomo = coincomo;

        // Labels
        usernameLabel.setFont(new Font("arial", 1, 11));
        passwordLabel.setFont(new Font("arial", 1, 11));
        RepasswordLabel.setFont(new Font("arial", 1, 11));

        // Texts Fields
        userloginidTextField.setFont(new Font("courier", 0, 12));
        passwordField.setFont(new Font("courier", 0, 12));
        RepasswordField.setFont(new Font("courier", 0, 12));

        // Buttons
        SignupButton.addActionListener(this);
        closeButton.addActionListener(this);

        SignupButton.setIcon(Icons.CONNECT_ICON);
        closeButton.setIcon(Icons.CLOSE_ICON);

        SignupButton.setFocusable(false);
        closeButton.setFocusable(false);


        TitledBorder legendTitleBorder=null;
        // Legend
        if(sign==1)
        {
            legendTitleBorder = BorderFactory.createTitledBorder("Signup Parameters");
            SignupButton.setText("Sign Up");
        }
        if(sign==2)
            legendTitleBorder = BorderFactory.createTitledBorder("Login Parameters");
        legendTitleBorder.setTitleColor(Color.BLUE);
        titleLegendLabel.setBorder(legendTitleBorder);

        // GUI
        c.setLayout(null);

        c.add(titleLegendLabel);

        c.add(usernameLabel);
        c.add(passwordLabel);
        c.add(RepasswordLabel);

        c.add(userloginidTextField);
        c.add(passwordField);
        c.add(RepasswordField);

        c.add(SignupButton);
        c.add(closeButton);

        titleLegendLabel.setBounds(20, 10, 260, 120);

        if(sign==1)
        {
            usernameLabel.setBounds(30, 30, 80, 22);
            passwordLabel.setBounds(30, 60, 80, 22);
            RepasswordLabel.setBounds(30, 90, 80, 22);
            userloginidTextField.setBounds(120, 30, 150, 22);
            passwordField.setBounds(120, 60, 150, 22);
            RepasswordField.setBounds(120, 90, 150, 22);
        }
        if(sign==2)
        {
            usernameLabel.setBounds(30, 40, 80, 22);
            passwordLabel.setBounds(30, 80, 80, 22);
            
            userloginidTextField.setBounds(120, 40, 150, 22);
            passwordField.setBounds(120, 80, 150, 22);
        }
        SignupButton.setBounds(30, 140, 110, 25);
        closeButton.setBounds(160, 140, 110, 25);

        // Set the connect button as the default button
        getRootPane().setDefaultButton(SignupButton);

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(this.getOwner().getX() + 100, this.getOwner().getY() + 100);
        this.setResizable(false);

        // Set the focus to the password field
        this.pack();
        boolean willFocus = userloginidTextField.requestFocusInWindow();
        this.setSize(305, 220);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
         boolean isSuccessful = false;
        if (e.getSource() == SignupButton) {
            // Validations ...
            if (userloginidTextField.getText().trim().isEmpty()) {
                GlobalMethods.updateStatusBar("User Login ID Can NOT be Empty Or Only Space Character !", Color.RED, coincomo);
                boolean willFocus = userloginidTextField.requestFocusInWindow();
                return;
            } else if (String.valueOf(passwordField.getPassword()).trim().isEmpty()) {
                GlobalMethods.updateStatusBar("Password Can NOT Be Empty Or Only Space Character !", Color.RED, coincomo);
                boolean willFocus = passwordField.requestFocusInWindow();
                return;
            } else if ((String.valueOf(RepasswordField.getPassword()).trim().isEmpty())&&Dialogsign==1) {
                GlobalMethods.updateStatusBar("Please Reinput Password!", Color.RED, coincomo);
                boolean willFocus = RepasswordField.requestFocusInWindow();
                return;
            }
            if(String.valueOf(passwordField.getPassword()).trim().length()<6)
            {
                GlobalMethods.updateStatusBar("The Password Must Be At Least 6 Character ! Please Try Again !", Color.RED, coincomo);
                boolean willFocus = passwordField.requestFocusInWindow();
                return;
            }
            if((!String.valueOf(passwordField.getPassword()).trim().equals(String.valueOf(RepasswordField.getPassword()).trim()))&&Dialogsign==1)
            {
                GlobalMethods.updateStatusBar("Reinput password doesn't match ! Please Try Again !", Color.RED, coincomo);
                boolean willFocus = RepasswordField.requestFocusInWindow();
                return;
            }
        
            String userloginid = userloginidTextField.getText();
            String password = String.valueOf(passwordField.getPassword());
            DBConnection connection = COINCOMODatabaseManager.getConnection();
            // Get only when a connection is available
            if (connection != null) {
                if(Dialogsign==1)
                {
                    try {
                        String sql = "SELECT * FROM Get_AllUserInfo();";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);

                        // Get
                        ResultSet rs = preparedStatement.executeQuery();

                        while (rs != null && rs.next()) {
                            String temp_userloginid = rs.getString(1);
                            if(userloginid.equals(temp_userloginid))
                            {
                                GlobalMethods.updateStatusBar("User ID Has Already Been Used !", Color.RED, coincomo);
                                return;
                            }                    
                        }
                        // Free from memory
                        preparedStatement.close();

                        sql = "SELECT * FROM Insert_User(?,?);";

                        // Efficient & safer way through prepared statement
                        preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setString(1, userloginid);
                        preparedStatement.setString(2, password);
                        // Insert
                        rs = preparedStatement.executeQuery();

                        if (rs.next()) {
                            COINCOMODatabaseManager.UserId = rs.getLong(1);
                            GlobalMethods.updateStatusBar("Congratulations! User Account Has Been Created Successfully!", Color.RED, coincomo); 
                            isSuccessful=true;
                            COINCOMO.setLogedin();
                        }
                        // Free from memory
                        preparedStatement.close();

                        // If any of the sql statement failed to execute, rollback the entire operation.
                        if (!isSuccessful) {
                            connection.rollback();
                        }
                    } catch (SQLException se) {
                        // Print any problem
                        //COINCOMODatabaseManager.disconnect(connection);  
                        //e.printStackTrace();
                    }
                }
            }
            if(Dialogsign == 2 )
            {
                try {
                    String sql = "SELECT * FROM Match_User(?,?);";

                    // Efficient & safer way through prepared statement
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, userloginid);
                    preparedStatement.setString(2, password);
                    // Insert
                    ResultSet rs = preparedStatement.executeQuery();

                    if (rs.next()) {
                        COINCOMODatabaseManager.UserId = rs.getLong(1);
                        if(COINCOMODatabaseManager.UserId!=-1)
                        {   
                            GlobalMethods.updateStatusBar("Loged In Successfully!", Color.BLACK, coincomo); 
                            isSuccessful=true;
                            COINCOMO.setLogedin();
                        }
                        else
                        {
                            GlobalMethods.updateStatusBar("Not Record Found, Please Sign Up!", Color.RED, coincomo); 
                            isSuccessful=true;
                        }
                    }
                    // Free from memory
                    preparedStatement.close();

                    // If any of the sql statement failed to execute, rollback the entire operation.
                    if (!isSuccessful) {
                        connection.rollback();
                    }
                } catch (SQLException se) {
                    // Print any problem
                    //COINCOMODatabaseManager.disconnect(connection);  
                    //e.printStackTrace();
                }       
            }
            COINCOMODatabaseManager.disconnect(connection);     
            this.dispose();
        }
        else
            this.dispose();
    }
}