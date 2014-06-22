/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package database;

import java.awt.Color;
import java.sql.DriverManager;
import java.sql.SQLException;
import main.GlobalMethods;

/**
 *
 * @author Raed Shomali
 */
public class COINCOMODatabaseManager {

    public static String HOST = "localhost";
    public static String PORT = "5432";
    public static String DB_NAME = "COINCOMO";
    public static String DB_DRIVER = "org.postgresql.Driver";
    public static String DB_TYPE = "postgresql";
    public static String USERNAME = "postgres";
    public static String PASSWORD = "123";
    public static long UserId = -1;
    private static String DB_URL = null;
    private static DBConnectionDriver dbConnectionDriver = null;
    // Default Database Constants
    public static final String POSTGRES_TYPE = "postgresql";
    public static final String POSTGRES_DRIVER = "org.postgresql.Driver";
    public static final String POSTGRES_PORT = "5432";
    public static final String MYSQL_TYPE = "mysql";
    public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    public static final String MYSQL_PORT = "3306";

    /**
     * Register the database connection driver Throw appropriate exceptions
     *
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws SQLException
     */
    public static void registerDriver() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        DB_URL = "jdbc:" + DB_TYPE + "://" + HOST + ":" + PORT + "/" + DB_NAME;
        dbConnectionDriver = new DBConnectionDriver(DB_DRIVER, DB_URL, USERNAME, PASSWORD);
    }

    /**
     *
     * @return a DB Connection
     */
    public static DBConnection getConnection() {
        try {
            // Connection Object
            DBConnection connection = (DBConnection) DriverManager.getConnection("jdbc:jdc:jdcpool");

            // For Better Performance (Batch Commiting)
            connection.setAutoCommit(false);

            return connection;
        } catch (Exception e) {
            // Sorry, We were unable to Connect to the Database.
            GlobalMethods.updateStatusBar("Unable to connect to Database.", Color.RED);

            return null;
        }
    }

    /**
     *
     * @param connection to be disconnected
     */
    public static void disconnect(DBConnection connection) {
        //throw new UnsupportedOperationException("Change for desktop app");
        // Make Sure Passed Connection Exists.
        if (connection != null) {
            try {
                // Save
                connection.commit();

                // Free Resources
                connection.close();
            } catch (Exception e) {
                GlobalMethods.updateStatusBar("Could Not Disconnect Database: " + e.getMessage(), Color.RED);
            }
        }
    }
    
    public static void disconnectAll() {
        if (dbConnectionDriver != null) {
            dbConnectionDriver.disconnectAll();
        }
    }

    public static void deregisterDriver() {
        if (dbConnectionDriver != null) {
            try {
                DriverManager.deregisterDriver(dbConnectionDriver);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            dbConnectionDriver = null;
        }
    }
}