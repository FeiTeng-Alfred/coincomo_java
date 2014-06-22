/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package database;

import core.COINCOMOAdaptationAndReuse;
import core.COINCOMOConstants.OperationMode;
import core.COINCOMOSubComponent;
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
 * @author Larry Chen
 */
public class COINCOMOAdaptationAndReuseManager extends COINCOMOManager {

    public static double calculateAdaptiveAdjustmentFactor(COINCOMOAdaptationAndReuse adaptation) {
        final double DM = adaptation.getDesignModified();
        final double CM = adaptation.getCodeModified();
        final double IM = adaptation.getIntegrationModified();
        final double AAF = 0.4d * DM + 0.3d * CM + 0.3d * IM;

        return AAF;
    }

    public static long calculateEquivalentSLOC(COINCOMOAdaptationAndReuse adaptation) {
        final double initialSLOC = (double) adaptation.getAdaptedSLOC();
        final double SU = adaptation.getSoftwareUnderstanding();
        final double AA = adaptation.getAssessmentAndAssimilation();
        final double UNFM = adaptation.getUnfamiliarityWithSoftware();
        final double AT = adaptation.getAutomaticallyTranslated();
        final double AAF = calculateAdaptiveAdjustmentFactor(adaptation);

        double AAM = 0.0d;
        long adaptedSLOC = 0;

        if (AAF <= 50.0d) {
            AAM = (AA + AAF * (1.0d + 0.02d * SU * UNFM)) / 100.0d;
        } else {
            AAM = (AA + AAF + (SU * UNFM)) / 100.0d;
        }

        adaptedSLOC = Math.round(initialSLOC * AAM * (1.0d - AT / 100.0d));

        return adaptedSLOC;
    }

    public static COINCOMOAdaptationAndReuse insertAdaptationAndReuse(COINCOMOSubComponent subComponent) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;
        COINCOMOAdaptationAndReuse adaptation = null;
        StringBuilder defaultName = new StringBuilder(COINCOMOAdaptationAndReuse.DEFAULT_NAME);

        // If exists
        if (subComponent != null) {
            defaultName.insert(defaultName.length()-1, subComponent.getNextAutoID());

            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Insert only when a connection is available
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Insert_AdaptationAndReuse(?, ?);";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setString(1, defaultName.toString());
                        preparedStatement.setLong(2, subComponent.getDatabaseID());

                        // Insert
                        ResultSet rs = preparedStatement.executeQuery();

                        if (rs.next()) {
                            long adaptationAndReuseID = rs.getLong(1);

                            if (adaptationAndReuseID < 0) {
                                log(Level.SEVERE, "SQL command\'" + sql + "\' failed.");
                                isSuccessful = false;
                            } else {
                                adaptation = new COINCOMOAdaptationAndReuse();
                                adaptation.setName(defaultName.toString());
                                adaptation.setDatabaseID(adaptationAndReuseID);
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
                adaptation = new COINCOMOAdaptationAndReuse();
                adaptation.setName(defaultName.toString());
            }

            // If the adaptation-and-reuse unit is properly created, then add it to the sub-component unit.
            if (adaptation != null) {
                subComponent.addSubUnit(adaptation);
                subComponent.calculateNextAutoID(COINCOMOAdaptationAndReuse.DEFAULT_NAME, adaptation.getName());
            }
        }

        return adaptation;
    }

    public static boolean updateAdaptationAndReuse(COINCOMOAdaptationAndReuse adaptation, boolean recursive) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;

        // If exists
        if (adaptation != null) {
            adaptation.setAdaptationAdjustmentFactor(calculateAdaptiveAdjustmentFactor(adaptation));
            adaptation.setEquivalentSLOC(calculateEquivalentSLOC(adaptation));

            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Update only when a connection is available
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Update_AdaptationAndReuse(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);

                        // Replace "?" with respective values
                        int index = 0;
                        preparedStatement.setLong(++index, adaptation.getDatabaseID());
                        preparedStatement.setString(++index, adaptation.getName());
                        preparedStatement.setLong(++index, adaptation.getParent().getDatabaseID());
                        preparedStatement.setLong(++index, adaptation.getAdaptedSLOC());
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(adaptation.getDesignModified()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(adaptation.getCodeModified()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(adaptation.getIntegrationModified()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(adaptation.getSoftwareUnderstanding()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(adaptation.getAssessmentAndAssimilation()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(adaptation.getUnfamiliarityWithSoftware()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(adaptation.getAutomaticallyTranslated()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(adaptation.getAutomaticTranslationProductivity()));
                        preparedStatement.setBigDecimal(++index, BigDecimal.valueOf(adaptation.getAdaptationAdjustmentFactor()));
                        preparedStatement.setLong(++index, adaptation.getEquivalentSLOC());
                        // Sanity check against parameter numbers
                        if (index != 14) {
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
                COINCOMOSubComponentManager.updateSubComponent((COINCOMOSubComponent) adaptation.getParent(), recursive);
            }
        }

        return isSuccessful;
    }

    public static boolean deleteAdaptationAndReuse(COINCOMOAdaptationAndReuse adaptation) {
        // If exists
        if (adaptation != null) {
            ArrayList<COINCOMOAdaptationAndReuse> adaptations = new ArrayList<COINCOMOAdaptationAndReuse>();
            adaptations.add(adaptation);

            return deleteAdaptationAndReuses(adaptations);
        } else {
            return false;
        }
    }

    public static boolean deleteAdaptationAndReuses(ArrayList<COINCOMOAdaptationAndReuse> adaptations) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;

        // If exists
        if (adaptations != null && !adaptations.isEmpty()) {
            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Delete only when a connection is available
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Delete_AdaptationAndReuse(?);";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);

                        for (int i = 0; i < adaptations.size(); i++) {
                            COINCOMOAdaptationAndReuse adaptation = adaptations.get(i);
                            preparedStatement.setLong(1, adaptation.getDatabaseID());

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
                for (int i = 0; i < adaptations.size(); i++) {
                    COINCOMOSubComponent subComponent = (COINCOMOSubComponent) adaptations.get(i).getParent();
                    subComponent.removeSubUnit(adaptations.get(i));
                }
            }
        }

        return isSuccessful;
    }

    public static boolean updateAdaptationAndReuseName(COINCOMOAdaptationAndReuse adaptation) {
        OperationMode operationMode = COINCOMO.getOperationMode();
        boolean ignoreDatabaseMode = COINCOMO.getIgnoreDatabaseMode();
        boolean isSuccessful = true;

        // If exists
        if (adaptation != null) {
            if (operationMode == OperationMode.DATABASE && !ignoreDatabaseMode) {
                DBConnection connection = COINCOMODatabaseManager.getConnection();

                // Delete only when a connection is available
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM Update_AdaptationAndReuseName(?, ?);";

                        // Efficient & safer way through prepared statement
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);

                        preparedStatement.setLong(1, adaptation.getDatabaseID());
                        preparedStatement.setString(2, adaptation.getName());

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
            COINCOMOSubComponent subComponent = (COINCOMOSubComponent) adaptation.getParent();
            subComponent.calculateNextAutoID(COINCOMOAdaptationAndReuse.DEFAULT_NAME, adaptation.getName());
        }

        return isSuccessful;
    }

    private static void log(Level level, String message) {
        Logger.getLogger(COINCOMOAdaptationAndReuseManager.class.getName()).log(level, message);
    }
}
