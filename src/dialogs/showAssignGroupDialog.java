/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogs;

import core.COINCOMOSystem;
import database.COINCOMODatabaseManager;
import database.COINCOMOSystemManager;
import database.DBConnection;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import main.COINCOMO;
import main.GlobalMethods;
import main.Icons;
import main.MenuItemMethods;
import panels.HierarchyPanel;
import panels.OverviewsAndGraphsPanel;

/**
 *
 * @author Zihao Zhu
 */
public class showAssignGroupDialog extends JDialog implements ActionListener {

    COINCOMO coincomo = null;
    Container c = getContentPane();
    JButton okButton = new JButton("OK");
    JButton closeButton = new JButton("Close");
    JButton assignButton = new JButton(">");
    JButton revokeButton = new JButton("<");
    
    DefaultListModel<String> listModel_Available = new DefaultListModel<String>();
    JList<String> availableUserList = new JList<String>(listModel_Available);
    JScrollPane listScroller_Available = new JScrollPane(availableUserList);
    public static ArrayList<String> availableUsers_changed = new ArrayList<String>();
    public static ArrayList<String> availableUsers = new ArrayList<String>();
    
    DefaultListModel<String> listModel_Assigned = new DefaultListModel<String>();
    JList<String> assignedUserList = new JList<String>(listModel_Assigned);
    JScrollPane listScroller_Assigned = new JScrollPane(assignedUserList);
    public static ArrayList<String> assignedUsers_changed = new ArrayList<String>();
    public static ArrayList<String> assignedUsers = new ArrayList<String>();
    

    /**
     *
     * @param parentFrame is the original frame that generated this Dialog
     */
    public showAssignGroupDialog(JFrame parentFrame) {
        super(parentFrame);

        coincomo = (COINCOMO) parentFrame;
        this.setTitle("Assign Group");
        this.setModal(true);

        // Buttons
        okButton.addActionListener(this);
        closeButton.addActionListener(this);
        assignButton.addActionListener(this);
        revokeButton.addActionListener(this);
        
        okButton.setFocusable(false);
        closeButton.setFocusable(false);
        
        assignButton.setFocusable(false);
        revokeButton.setFocusable(false);

        okButton.setIcon(Icons.EXIT_ICON);
        closeButton.setIcon(Icons.CLOSE_ICON);

        // List
        TitledBorder availableUsersTitleBorder = BorderFactory.createTitledBorder("Available Users");
        availableUsersTitleBorder.setTitleColor(Color.DARK_GRAY);
        availableUsersTitleBorder.setTitlePosition(TitledBorder.BELOW_TOP);
        availableUsersTitleBorder.setTitleJustification(TitledBorder.CENTER);
        listScroller_Available.setBorder(availableUsersTitleBorder);
        
        TitledBorder assignedUsersTitleBorder = BorderFactory.createTitledBorder("Assigned Users");
        assignedUsersTitleBorder.setTitleColor(Color.DARK_GRAY);
        assignedUsersTitleBorder.setTitlePosition(TitledBorder.BELOW_TOP);
        assignedUsersTitleBorder.setTitleJustification(TitledBorder.CENTER);
        listScroller_Assigned.setBorder(assignedUsersTitleBorder);

        // GUI
        c.setLayout(null);
        c.add(listScroller_Available);
        c.add(listScroller_Assigned);
        c.add(okButton);
        c.add(closeButton);
        c.add(assignButton);
        c.add(revokeButton);

        listScroller_Available.setBounds(20, 20, 180, 220);
        listScroller_Assigned.setBounds(265, 20, 180, 220);

        okButton.setBounds(60, 260, 90, 25);
        closeButton.setBounds(315, 260, 90, 25);
        
        assignButton.setBounds(210, 95, 45, 25);
        revokeButton.setBounds(210, 140, 45, 25);

        GlobalMethods.updateStatusBar("Loading All Users ...", Color.BLACK, coincomo);

        okButton.setEnabled(false);
        closeButton.setEnabled(false);
        
        assignButton.setEnabled(false);
        revokeButton.setEnabled(false);

        // Update in a GUI Thread
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    // Rest a Bit ... So the GUI would Render
                    Thread.sleep(100);
                    availableUsers_changed.clear();
                    assignedUsers_changed.clear();
                    availableUsers.clear();
                    assignedUsers.clear();
                    listModel_Assigned.clear();
                    listModel_Available.clear();
                    
                    DBConnection connection = COINCOMODatabaseManager.getConnection();

                    // Get only when a connection is available
                    if (connection != null) {
                        try {
                            String sql = "SELECT * FROM Get_AllAvailableUserInfo(?,?);";

                            // Efficient & safer way through prepared statement
                            PreparedStatement preparedStatement = connection.prepareStatement(sql);
                            preparedStatement.setLong(1, COINCOMODatabaseManager.UserId);
                            preparedStatement.setLong(2, coincomo.getCurrentSystem().getDatabaseID());
                            // Get
                            ResultSet rs = preparedStatement.executeQuery();

                            while (rs != null && rs.next()) {
                                String temp_userloginid = rs.getString(1);
                                listModel_Available.addElement(temp_userloginid);
                                availableUsers.add(temp_userloginid);
                            }
                            // Free from memory
                            preparedStatement.close();
                    
                        } catch (SQLException e) {
                            // Print any problem
                            //e.printStackTrace();
                        }
                    }
                    COINCOMODatabaseManager.disconnect(connection);
                    
                    GlobalMethods.updateStatusBar("Available Users have been loaded.", Color.BLACK, coincomo);
                
                    connection = COINCOMODatabaseManager.getConnection();

                    // Get only when a connection is available
                    if (connection != null) {
                        try {
                            String sql = "SELECT * FROM Get_AssignedUserInfo(?,?);";

                            // Efficient & safer way through prepared statement
                            PreparedStatement preparedStatement = connection.prepareStatement(sql);
                            preparedStatement.setLong(1, COINCOMODatabaseManager.UserId);
                            preparedStatement.setLong(2, coincomo.getCurrentSystem().getDatabaseID());
                            // Get
                            ResultSet rs = preparedStatement.executeQuery();
                            while (rs != null && rs.next()) {
                                String temp_userloginid = rs.getString(1);
                                listModel_Assigned.addElement(temp_userloginid);
                                assignedUsers.add(temp_userloginid);
                            }
                            // Free from memory
                            preparedStatement.close();
                    
                        } catch (SQLException e) {
                            // Print any problem
                            //e.printStackTrace();
                        }
                    }
                    COINCOMODatabaseManager.disconnect(connection);

                    GlobalMethods.updateStatusBar("Available Users have been loaded.", Color.BLACK, coincomo);
                    
                }catch (InterruptedException ex1) {
                } 
                
            }
        });
        
        okButton.setEnabled(true);
        closeButton.setEnabled(true);
        assignButton.setEnabled(true);
        revokeButton.setEnabled(true);
        
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(this.getOwner().getX() + 100, this.getOwner().getY() + 100);
        this.setResizable(false);
        this.setSize(470, 330);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == assignButton) {
            if (availableUserList.isSelectionEmpty()) {
                GlobalMethods.updateStatusBar("No User(s) Was(Were) Selected For Assiging To Your Group.", Color.RED, coincomo);
            } else {
                int selected[] = availableUserList.getSelectedIndices();
  	        for (int i=selected.length-1; i >=0; i--) {
                   String element = (String)availableUserList.getModel().getElementAt(selected[i]);
                   listModel_Available.removeElement(element);
                   listModel_Assigned.addElement(element);
                   if(availableUsers_changed.contains(element))
                        availableUsers_changed.remove(element);
                   assignedUsers_changed.add(element);
                }
            }   
        }else if (e.getSource() == revokeButton) {
            if (assignedUserList.isSelectionEmpty()) {
                GlobalMethods.updateStatusBar("No User(s) Was(Were) Selected For Revoking From Your Group.", Color.RED, coincomo);
            } else {
                int selected[] = assignedUserList.getSelectedIndices();
  	        for (int i=selected.length-1; i >=0; i--) {
                   String element = (String)assignedUserList.getModel().getElementAt(selected[i]);
                   listModel_Assigned.removeElement(element);
                   listModel_Available.addElement(element);
                   if(assignedUsers_changed.contains(element))
                        assignedUsers_changed.remove(element);
                   availableUsers_changed.add(element);
                }
            }   
        }else if (e.getSource() == okButton) {
            if (!availableUsers_changed.isEmpty()) 
            {
                for(int i =0;i<availableUsers.size();i++)
                {
                    String element = availableUsers.get(i);
                    if(availableUsers_changed.contains(element))
                        availableUsers_changed.remove(element);
                }
                //call function 
                if(!availableUsers_changed.isEmpty())
                {
                     DBConnection connection = COINCOMODatabaseManager.getConnection();
                     if (connection != null) {
                         for(int i = 0 ; i<availableUsers_changed.size();i++)
                         {
                             try {
                                 String sql = "SELECT * FROM update_group(?,?,?,?);";

                                 // Efficient & safer way through prepared statement
                                 PreparedStatement preparedStatement = connection.prepareStatement(sql);
                                 preparedStatement.setInt(1, 0);
                                 preparedStatement.setLong(2, COINCOMODatabaseManager.UserId);
                                 preparedStatement.setLong(3, coincomo.getCurrentSystem().getDatabaseID());
                                 preparedStatement.setString(4, availableUsers_changed.get(i));
                                 // Get
                                 ResultSet rs = preparedStatement.executeQuery();
                                 if (rs.next()) {
                                 }
                                 // Free from memory
                                 preparedStatement.close();

                             } catch (SQLException ex) {
                                 // Print any problem
                                 //e.printStackTrace();
                             }
                         }
                     }
                      COINCOMODatabaseManager.disconnect(connection);

                     GlobalMethods.updateStatusBar("Group Members Assigned Successfully !.", Color.BLACK, coincomo);
                }  
            }
            if (!assignedUsers_changed.isEmpty())
            {
               for(int i =0;i<assignedUsers.size();i++)
               {
                   String element = assignedUsers.get(i);
                   if(assignedUsers_changed.contains(element))
                       assignedUsers_changed.remove(element);
               }
               //call function
               if(!assignedUsers_changed.isEmpty())
                {
                     DBConnection connection = COINCOMODatabaseManager.getConnection();
                     if (connection != null) {
                         for(int i = 0 ; i<assignedUsers_changed.size();i++)
                         {
                             try {
                                 String sql = "SELECT * FROM update_group(?,?,?,?);";

                                 // Efficient & safer way through prepared statement
                                 PreparedStatement preparedStatement = connection.prepareStatement(sql);
                                 preparedStatement.setInt(1, 1);
                                 preparedStatement.setLong(2, COINCOMODatabaseManager.UserId);
                                 preparedStatement.setLong(3, coincomo.getCurrentSystem().getDatabaseID());
                                 String tmp = assignedUsers_changed.get(i);
                                 preparedStatement.setString(4,tmp );
                                 // Get
                                 ResultSet rs = preparedStatement.executeQuery();
                                 if (rs.next()) {
                                 }
                                 // Free from memory
                                 preparedStatement.close();

                             } catch (SQLException ex) {
                                 // Print any problem
                                 //e.printStackTrace();
                             }
                         }
                     }
                      COINCOMODatabaseManager.disconnect(connection);

                     
                }
            }
            // Exit ...
            GlobalMethods.updateStatusBar("Group Members Assigned Successfully !.", Color.BLACK, coincomo);
            this.dispose();
            
        }else {
            // Exit ...
                GlobalMethods.updateStatusBar("", Color.BLACK, coincomo);
                this.dispose();
        }
    }
    
}
