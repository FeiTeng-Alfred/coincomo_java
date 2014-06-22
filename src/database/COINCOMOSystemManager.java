/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package database;

import core.COINCOMOConstants.OperationMode;
import core.COINCOMOSubSystem;
import core.COINCOMOSystem;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import main.COINCOMO;
import main.COINCOMOXML;

/**
 *
 * @author Raed Shomali
 */
public class COINCOMOSystemManager extends COINCOMOManager {

    public static boolean saveSystemAsXML(COINCOMOSystem system, File file) {
        try {
            COINCOMOXML.exportXML(system, file);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "File couldn't be saved to the selected location. " + ex.getMessage(), "SAVE ERROR", 0);
            log(Level.SEVERE, ex.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public static boolean exportSystemAsXML(COINCOMOSystem system, File file) {
        try {
            COINCOMOXML.exportXML(system, file);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "File couldn't be exported to the selected location. " + ex.getMessage(), "EXPORT ERROR", 0);
            log(Level.SEVERE, ex.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public static COINCOMOSystem synchronizeSystemWithXML(long databaseID) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean isSuccessful = true;
        COINCOMOSystem system = null;

        if (operationMode == OperationMode.DATABASE) {
            DBConnection connection = COINCOMODatabaseManager.getConnection();

            // Check only when a connection is available
            if (connection != null) {
                try {
                    String sql = "SELECT * FROM Get_System(?);";

                    // Efficient & safer way through prepared statement
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setLong(1, databaseID);

                    // Retrieve the system
                    ResultSet rs = preparedStatement.executeQuery();

                    if (rs != null && rs.next()) {
                        long systemID = rs.getLong(1);

                        if (systemID < 0) {
                            log(Level.SEVERE, "SQL command\'" + sql + "\' failed.");
                            isSuccessful = false;
                        }
                        else {
                            system = new COINCOMOSystem();
                            system.setDatabaseID(systemID);
                        }
                    }

                    // Free from memory
                    preparedStatement.close();

                    // If the system exists in the database, clear all the records associated with it.
                    if (system != null) {
                        sql = "SELECT * FROM Clear_AllSubSystems(?);";

                        preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setLong(1, system.getDatabaseID());

                        // Clear
                        rs = preparedStatement.executeQuery();

                        if (rs != null && rs.next()) {
                            isSuccessful = isSuccessful && rs.getBoolean(1);
                        }

                        // Free from memory
                        preparedStatement.close();
                    }

                    // If any of the sql statement failed to execute, rollback the entire operation.
                    if (!isSuccessful) {
                        connection.rollback();
                    }
                } catch (SQLException e) {
                    // Print any problem
                    log(Level.SEVERE, e.getLocalizedMessage());
                    //e.printStackTrace();
                }
            }

            COINCOMODatabaseManager.disconnect(connection);
        }

        return system;
    }

    public static ArrayList<COINCOMOSystem> getAllSystems() {
        DBConnection connection = COINCOMODatabaseManager.getConnection();

        ArrayList<COINCOMOSystem> systems = new ArrayList<COINCOMOSystem>();

        // Get only when a connection is available
        if (connection != null) {
            try {
                String sql = "SELECT * FROM Get_AllSystems(?);";

                // Efficient & safer way through prepared statement
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setLong(1, COINCOMODatabaseManager.UserId);
                // Get
                ResultSet rs = preparedStatement.executeQuery();

                while (rs != null && rs.next()) {
                    int index = 0;
                    long systemID = rs.getLong(++index);
                    String name = rs.getString(++index);
                    long sloc = rs.getLong(++index);
                    double cost = rs.getDouble(++index);
                    double staff = rs.getDouble(++index);
                    double effort = rs.getDouble(++index);
                    double schedule = rs.getDouble(++index);

                    COINCOMOSystem system = new COINCOMOSystem();

                    system.setDatabaseID(systemID);
                    system.setName(name);
                    system.setSLOC(sloc);
                    system.setCost(cost);
                    system.setStaff(staff);
                    system.setEffort(effort);
                    system.setSchedule(schedule);

                    system.clearDirty();

                    systems.add(system);
                }

                // Free from memory
                preparedStatement.close();
            } catch (SQLException e) {
                // Print any problem
                log(Level.SEVERE, e.getLocalizedMessage());
                //e.printStackTrace();
            }
        }

        COINCOMODatabaseManager.disconnect(connection);

        return systems;
    }

    public static boolean loadSystem(COINCOMOSystem system) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean isSuccessful = false;

        if (operationMode == OperationMode.DATABASE) {
            DBConnection connection = COINCOMODatabaseManager.getConnection();

            // Check if a Connection is available ..
            if (connection != null) {
                try {
                    String sql = "SELECT * FROM Get_AllSubSystems(?);";
                    int nextID = 0;

                    // Efficient Query Statement
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);

                    // Replace the "?" With the ID Value
                    preparedStatement.setLong(1, system.getDatabaseID());

                    ResultSet rs = preparedStatement.executeQuery();

                    // Check if a Row is returned ..
                    while (rs != null && rs.next()) {
                        int index = 0;
                        long subSystemID = rs.getLong(++index);
                        String name = rs.getString(++index);
                        long systemID = rs.getLong(++index);
                        long sloc = rs.getLong(++index);
                        double cost = rs.getDouble(++index);
                        double staff = rs.getDouble(++index);
                        double effort = rs.getDouble(++index);
                        double schedule = rs.getDouble(++index);
                        int zoomLevel = rs.getInt(++index);

                        // Create a sub system
                        COINCOMOSubSystem subSystem = new COINCOMOSubSystem();

                        // Set parameters
                        subSystem.setDatabaseID(subSystemID);
                        subSystem.setName(name);
                        subSystem.setSLOC(sloc);
                        subSystem.setCost(cost);
                        subSystem.setStaff(staff);
                        subSystem.setEffort(effort);
                        subSystem.setSchedule(schedule);
                        subSystem.setZoomLevel(zoomLevel);

                        // Load sub systems using the same connection for efficiency
                        COINCOMOSubSystemManager.loadSubSystem(subSystem, connection);

                        subSystem.clearDirty();

                        system.addSubUnit(subSystem);
                        system.calculateNextAutoID(COINCOMOSubSystem.DEFAULT_NAME, subSystem.getName());
                    }

                    // Release From Memory
                    preparedStatement.close();
                } catch (SQLException e) {
                    // Print Any Problems ..
                    log(Level.SEVERE, e.getLocalizedMessage());
                    //e.printStackTrace();
                }
            }

            COINCOMODatabaseManager.disconnect(connection);

            system.clearDirty();

            isSuccessful = true;
        }

        return isSuccessful;
    }

    public static COINCOMOSystem insertSystem() {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;
        COINCOMOSystem system = null;
        StringBuilder defaultName = new StringBuilder(COINCOMOSystem.DEFAULT_NAME);

        if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
            DBConnection connection = COINCOMODatabaseManager.getConnection();

            // Insert only when a connection is available
            if (connection != null) {
                try {
                    String sql = "SELECT * FROM Get_DefaultSystemName();";

                    // Efficient & safer way through prepared statement
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);

                    // Get the latest auto-generated default system name
                    ResultSet rs = preparedStatement.executeQuery();

                    if (rs.next()) {
                        String name = rs.getString(1);
                        int id = 1;

                        if (COINCOMOSystem.DEFAULT_NAME.equals(name)) {
                            // Set the auto-generated default system name to '(System1)'
                            defaultName.insert(defaultName.length()-1, id);
                        } else {
                            // Try to retrieve the ID from the auto-generated default system name in the format '(System\d)'
                            String subName = name.substring(7, name.length()-1);

                            // Increment the ID to next number, or if the ID is not an actual integer, reset to 1
                            try {
                                id = Integer.parseInt(subName);
                                id++;
                            } catch (NumberFormatException e) {
                                id = 1;
                            }

                            defaultName.insert(defaultName.length()-1, id);
                        }
                    }

                    // Free from memory
                    preparedStatement.close();

                    sql = "SELECT * FROM Insert_System(?,?);";

                    // Efficient & safer way through prepared statement
                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, defaultName.toString());
                    preparedStatement.setLong(2, COINCOMODatabaseManager.UserId);
                    // Insert
                    rs = preparedStatement.executeQuery();

                    if (rs.next()) {
                        long systemID = rs.getLong(1);

                        if (systemID < 0) {
                            log(Level.SEVERE, "SQL command\'" + sql + "\' failed.");
                            isSuccessful = false;
                        }
                        else {
                            system = new COINCOMOSystem();
                            system.setName(defaultName.toString());
                            system.setDatabaseID(systemID);
                        }
                    }

                    // Free from memory
                    preparedStatement.close();

                    // If any of the sql statement failed to execute, rollback the entire operation.
                    if (!isSuccessful) {
                        connection.rollback();
                    }
                } catch (SQLException e) {
                    // Print any problem
                    log(Level.SEVERE, e.getLocalizedMessage());
                    //e.printStackTrace();
                }
            }

            COINCOMODatabaseManager.disconnect(connection);
        } else {
            system = new COINCOMOSystem();
            system.setName(COINCOMOSystem.DEFAULT_NAME);
        }

        return system;
    }

    public static boolean updateSystem(COINCOMOSystem system) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;

        // If exists
        if (system != null) {
            system.setSLOC(system.getSLOC());
            system.setCost(system.getCost());
            system.setStaff(system.getStaff());
            system.setEffort(system.getEffort());
            system.setSchedule(system.getSchedule());

            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Update only when a connection is available
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Update_System(?, ?, ?, ?, ?, ?, ?);";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);

                        // Replace "?" with respective values
                        int index = 0;
                        preparedStatement.setLong(++index, system.getDatabaseID());
                        preparedStatement.setString(++index, system.getName());
                        preparedStatement.setLong(++index, system.getSLOC());
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(system.getCost()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(system.getStaff()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(system.getEffort()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(system.getSchedule()));
                        // Sanity check against parameter numbers
                        if (index != 7) {
                            log(Level.WARNING, "Wrong number of parameters are set for sql \'" + preparedStatement.toString() + "\'.");
                        }

                        // Update
                        ResultSet rs = preparedStatement.executeQuery();
                        if (rs.next()) {
                            isSuccessful = rs.getBoolean(1);
                        }

                        // Update
                        preparedStatement.close();

                        // If any of the sql statement failed to execute, rollback the entire operation.
                        if (!isSuccessful) {
                            connection.rollback();
                        }
                    } catch (SQLException e) {
                        // Print any problem
                        log(Level.SEVERE, e.getLocalizedMessage());
                        //e.printStackTrace();
                    }
                }

                COINCOMODatabaseManager.disconnect(connection);
            }
        }

        return isSuccessful;
    }

    public static boolean deleteSystem(COINCOMOSystem system) {
        // If exists
        if (system != null) {
            ArrayList<COINCOMOSystem> systems = new ArrayList<COINCOMOSystem>();
            systems.add(system);

            return deleteSystems(systems);
        } else {
            return false;
        }
    }

    public static boolean deleteSystems(ArrayList<COINCOMOSystem> systems) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;

        // If exists
        if (systems != null && !systems.isEmpty()) {
            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Delete only when a connection is available
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Delete_System(?);";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);

                        for (int i = 0; i < systems.size(); i++) {
                            COINCOMOSystem system = systems.get(i);
                            preparedStatement.setLong(1, system.getDatabaseID());

                            // Delete
                            ResultSet rs = preparedStatement.executeQuery();
                            if (rs.next()) {
                                isSuccessful = isSuccessful & rs.getBoolean(1);
                            }
                        }

                        // Free from memory
                        preparedStatement.close();

                        // If any of the sql statement failed to execute, rollback the entire operation.
                        if (!isSuccessful) {
                            connection.rollback();
                        }
                    } catch (SQLException e) {
                        // Print any problem
                        log(Level.SEVERE, e.getLocalizedMessage());
                        //e.printStackTrace();
                    }
                }

                COINCOMODatabaseManager.disconnect(connection);
            }
        }

        return isSuccessful;
    }

    public static boolean updateSystemName(COINCOMOSystem system) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;

        // If exists
        if (system != null) {
            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Delete only when a connection is available
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Update_SystemName(?, ?);";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);

                        preparedStatement.setLong(1, system.getDatabaseID());
                        preparedStatement.setString(2, system.getName());

                        // Delete
                        ResultSet rs = preparedStatement.executeQuery();
                        if (rs.next()) {
                            isSuccessful = rs.getBoolean(1);
                        }

                        // Free from memory
                        preparedStatement.close();

                        // If any of the sql statement failed to execute, rollback the entire operation.
                        if (!isSuccessful) {
                            connection.rollback();
                        }
                    } catch (SQLException e) {
                        // Print any problem
                        log(Level.SEVERE, e.getLocalizedMessage());
                        //e.printStackTrace();
                    }
                }

                COINCOMODatabaseManager.disconnect(connection);
            }
        }

        return isSuccessful;
    }

    public static boolean hasSystemName(String systemName) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean isTrue = false;

        // If exists
        if (systemName != null) {
            if (operationMode == OperationMode.DATABASE) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Delete only when a connection is available
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Has_SystemName(?);";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);

                        preparedStatement.setString(1, systemName);

                        // Delete
                        ResultSet rs = preparedStatement.executeQuery();
                        if (rs.next()) {
                            isTrue = rs.getBoolean(1);
                        }

                        // Free from memory
                        preparedStatement.close();
                    } catch (SQLException e) {
                        // Print any problem
                        log(Level.SEVERE, e.getLocalizedMessage());
                        //e.printStackTrace();
                    }
                }

                COINCOMODatabaseManager.disconnect(connection);
            }
        }

        return isTrue;
    }

    private static void log(Level level, String message) {
        Logger.getLogger(COINCOMOSystemManager.class.getName()).log(level, message);
    }
}
