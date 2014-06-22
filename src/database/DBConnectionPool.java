/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

class ConnectionReaper extends Thread {

    private DBConnectionPool pool;
    // 300 Seconds
    private final long delay = 300000;

    ConnectionReaper(DBConnectionPool pool) {
        //throw new UnsupportedOperationException("Change for desktop app");
        this.pool = pool;
    }

    @Override
    public void run() {
        //throw new UnsupportedOperationException("Change for desktop app");
        // Run Forever
        while (true) {
            try {
                // Rest
                sleep(delay);
            } catch (InterruptedException e) {
            }

            // To Resolve Deadlocks ...
            pool.reapConnections();
        }
    }
}

/**
 *
 * @author Raed Shomali
 */
public class DBConnectionPool {

    private ArrayList<DBConnection> connections;
    private String url, user, password;
    final private long timeout = 60000;
    private ConnectionReaper reaper;
    final private int poolSize = 10;

    /**
     *
     * @param url to set the Host
     * @param user to set the Database's Username
     * @param password to set the Database's Password
     */
    public DBConnectionPool(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;

        connections = new ArrayList<DBConnection>(poolSize);

        reaper = new ConnectionReaper(this);
        reaper.start();
    }

    /**
     * for reaping connections
     */
    public synchronized void reapConnections() {
        long stale = System.currentTimeMillis() - timeout;

        for (int i = 0; i < connections.size(); i++) {
            DBConnection connection = (DBConnection) connections.get(i);

            if ((connection.inUse()) && (stale > connection.getLastUse()) && (!connection.validate())) {
                removeConnection(connection);
            }
        }
    }

    /**
     * for closing connections
     */
    public synchronized void closeConnections() {
        for (int i = 0; i < connections.size(); i++) {
            DBConnection connection = (DBConnection) connections.get(i);

            removeConnection(connection);
        }
    }

    private synchronized void removeConnection(DBConnection connection) {
        try {
            connection.closeActual();
            connections.remove(connection);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * @return a Connection from the Pool
     * @throws java.sql.SQLException
     */
    public synchronized Connection getConnection() throws SQLException {
        DBConnection jdcConnection;

        for (int i = 0; i < connections.size(); i++) {
            jdcConnection = (DBConnection) connections.get(i);

            if (jdcConnection.lease()) {
                return jdcConnection;
            }
        }

        Connection connection = DriverManager.getConnection(url, user, password);

        jdcConnection = new DBConnection(connection, this);
        jdcConnection.lease();

        connections.add(jdcConnection);

        return jdcConnection;
    }

    /**
     *
     * @param connection to be expired
     */
    public synchronized void returnConnection(DBConnection connection) {
        connection.expireLease();
    }
}
