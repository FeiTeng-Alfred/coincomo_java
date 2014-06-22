/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package database;

import core.COINCOMOComponent;
import core.COINCOMOConstants;
import core.COINCOMOConstants.Increment;
import core.COINCOMOConstants.OperationMode;
import core.COINCOMOConstants.Rating;
import core.COINCOMOSubSystem;
import core.COINCOMOSystem;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.COINCOMO;

/**
 *
 * @author Raed Shomali
 */
public class COINCOMOSubSystemManager extends COINCOMOManager {

    public static COINCOMOSubSystem insertSubSystem(COINCOMOSystem system) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;
        COINCOMOSubSystem subSystem = null;
        StringBuilder defaultName = new StringBuilder(COINCOMOSubSystem.DEFAULT_NAME);

        // If exists
        if (system != null) {
            defaultName.insert(defaultName.length()-1, system.getNextAutoID());

            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Insert only when a connection is available
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Insert_SubSystem(?, ?);";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setString(1, defaultName.toString());
                        preparedStatement.setLong(2, system.getDatabaseID());

                        // Insert
                        ResultSet rs = preparedStatement.executeQuery();

                        if (rs.next()) {
                            long subSystemID = rs.getLong(1);

                            if (subSystemID < 0) {
                                log(Level.SEVERE, "SQL command\'" + sql + "\' failed.");
                                isSuccessful = false;
                            } else {
                                subSystem = new COINCOMOSubSystem();
                                subSystem.setName(defaultName.toString());
                                subSystem.setDatabaseID(subSystemID);
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
                subSystem = new COINCOMOSubSystem();
                subSystem.setName(defaultName.toString());
            }

            // If the sub-system unit is properly created, then add it to the system unit.
            if (subSystem != null) {
                system.addSubUnit(subSystem);
                system.calculateNextAutoID(COINCOMOSubSystem.DEFAULT_NAME, subSystem.getName());
             }
        }

        return subSystem;
    }

    public static boolean updateSubSystem(COINCOMOSubSystem subSystem, boolean recursive) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = false;

        // If exists
        if (subSystem != null) {
            subSystem.setSLOC(subSystem.getSLOC());
            subSystem.setCost(subSystem.getCost());
            subSystem.setStaff(subSystem.getStaff());
            subSystem.setEffort(subSystem.getEffort());
            subSystem.setSchedule(subSystem.getSchedule());

            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Update only when a connection is available
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Update_SubSystem(?, ?, ?, ?, ?, ?, ?, ?, ?);";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);

                        // Replace "?" With Respective Values ..
                        int index = 0;
                        preparedStatement.setLong(++index, subSystem.getDatabaseID());
                        preparedStatement.setString(++index, subSystem.getName());
                        preparedStatement.setLong(++index, subSystem.getParent().getDatabaseID());
                        preparedStatement.setLong(++index, subSystem.getSLOC());
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(subSystem.getCost()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(subSystem.getStaff()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(subSystem.getEffort()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(subSystem.getSchedule()));
                        preparedStatement.setInt(++index, subSystem.getZoomLevel());
                        // Sanity check against parameter numbers
                        if (index != 9) {
                            log(Level.WARNING, "Wrong number of parameters are set for sql \'" + preparedStatement.toString() + "\'.");
                        }

                        // Update
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

            if (isSuccessful && recursive) {
                COINCOMOSystemManager.updateSystem((COINCOMOSystem) subSystem.getParent());
            }
        }

        return isSuccessful;
    }

    public static boolean deleteSubSystem(COINCOMOSubSystem subSystem) {
        // If exists
        if (subSystem != null) {
            ArrayList<COINCOMOSubSystem> subSystems = new ArrayList<COINCOMOSubSystem>();
            subSystems.add(subSystem);

            return deleteSubSystems(subSystems);
        } else {
            return false;
        }
    }

    public static boolean deleteSubSystems(ArrayList<COINCOMOSubSystem> subSystems) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;

        // If exists
        if (subSystems != null && !subSystems.isEmpty()) {
            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Delete_SubSystem(?);";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);

                        for (int i = 0; i < subSystems.size(); i++) {
                            COINCOMOSubSystem subSystem = subSystems.get(i);
                            preparedStatement.setLong(1, subSystem.getDatabaseID());

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

            if (isSuccessful) {
                for (int i = 0; i < subSystems.size(); i++) {
                    COINCOMOSystem system = (COINCOMOSystem) subSystems.get(i).getParent();
                    system.removeSubUnit(subSystems.get(i));
                }
            }
        }

        return isSuccessful;
    }

    public static boolean loadSubSystem(COINCOMOSubSystem subSystem, DBConnection connection) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean isSuccessful = false;

        if (operationMode == OperationMode.DATABASE) {
            // Check if a Connection is available ..
            if (connection != null) {
                try {
                    String sql = "SELECT * FROM Get_AllComponents(?);";
                    int nextID = 0;

                    // Efficient Way of Updating
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);

                    // Replace "?" With Respective Values ..
                    preparedStatement.setLong(1, subSystem.getDatabaseID());

                    ResultSet rs = preparedStatement.executeQuery();

                    // Check if a Row is returned ..
                    while (rs != null && rs.next()) {
                        int index = 0;

                        long componentID = rs.getLong(++index);
                        String name = rs.getString(++index);
                        long subSystemID = rs.getLong(++index);
                        long sloc = rs.getLong(++index);
                        double cost = rs.getDouble(++index);
                        double staff = rs.getDouble(++index);
                        double effort = rs.getDouble(++index);
                        double schedule = rs.getDouble(++index);
                        double sf = rs.getDouble(++index);
                        double sced = rs.getDouble(++index);
                        double scedPercent = rs.getDouble(++index);
                        int multiBuildShift = rs.getInt(++index);
                        int revision = rs.getInt(++index);

                        Rating scedRating = Rating.valueOf(rs.getString(++index));
                        Increment scedIncrement = Increment.getValueOf(rs.getString(++index));
                        Rating sfRatings[] = new Rating[COINCOMOConstants.SFS.length];
                        Increment sfIncrements[] = new Increment[COINCOMOConstants.SFS.length];
                        for (int i = 0; i < COINCOMOConstants.SFS.length; i++) {
                            sfRatings[i] = Rating.valueOf(rs.getString(++index));
                            sfIncrements[i] = Increment.getValueOf(rs.getString(++index));
                        }

                        // Create a component
                        COINCOMOComponent component = new COINCOMOComponent();

                        // Set parameters
                        component.setDatabaseID(componentID);
                        component.setName(name);
                        component.setSLOC(sloc);
                        component.setCost(cost);
                        component.setStaff(staff);
                        component.setEffort(effort);
                        component.setSchedule(schedule);
                        component.setSF(sf);
                        component.setSCED(sced);
                        component.setSCEDPercent(scedPercent);
                        component.setMultiBuildShift(multiBuildShift);
                        component.setRevision(revision);

                        component.setSCEDRating(scedRating);
                        component.setSCEDIncrement(scedIncrement);
                        component.setSFRatings(sfRatings);
                        component.setSFIncrements(sfIncrements);

                        // Load COPSEMO for components using the same connection for efficiency
                        COINCOMOComponentManager.loadCOPSEMO(component, connection);

                        // Load parameters for components using the same connection for efficiency
                        COINCOMOComponentManager.loadParameters(component, connection);

                        // Load components using the same connection for efficiency
                        COINCOMOComponentManager.loadComponent(component, connection);

                        component.clearDirty();

                        subSystem.addSubUnit(component);
                        subSystem.calculateNextAutoID(COINCOMOComponent.DEFAULT_NAME, component.getName());
                    }

                    // Release From Memory
                    preparedStatement.close();
                } catch (SQLException e) {
                    // Print Any Problems ..
                    log(Level.SEVERE, e.getLocalizedMessage());
                    //e.printStackTrace();
                }
            }

            isSuccessful = true;
        }

        return isSuccessful;
    }

    public static boolean updateSubSystemName(COINCOMOSubSystem subSystem) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;

        // If exists
        if (subSystem != null) {
            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Delete only when a connection is available
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Update_SubSystemName(?, ?);";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);

                        preparedStatement.setLong(1, subSystem.getDatabaseID());
                        preparedStatement.setString(2, subSystem.getName());

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

        // Determine the next auto-generated name will be
        if (isSuccessful) {
            COINCOMOSystem system = (COINCOMOSystem) subSystem.getParent();
            system.calculateNextAutoID(COINCOMOSubSystem.DEFAULT_NAME, subSystem.getName());
        }

        return isSuccessful;
    }

    private static void log(Level level, String message) {
        Logger.getLogger(COINCOMOSubSystemManager.class.getName()).log(level, message);
    }
}
