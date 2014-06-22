/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class DBConnectionDriver implements Driver {

    public static final String URL_PREFIX = "jdbc:jdc:";
    private static final int MAJOR_VERSION = 1;
    private static final int MINOR_VERSION = 0;
    private DBConnectionPool pool;

    public DBConnectionDriver(String driver, String url,
            String user, String password)
            throws ClassNotFoundException,
            InstantiationException, IllegalAccessException,
            SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        DriverManager.registerDriver(this);
        Class.forName(driver).newInstance();
        pool = new DBConnectionPool(url, user, password);
    }

    public Connection connect(String url, Properties props) throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        if (!url.startsWith(URL_PREFIX)) {
            return null;
        }

        return pool.getConnection();
    }
    
    public void disconnectAll() {
        pool.closeConnections();
    }

    public boolean acceptsURL(String url) {
        //throw new UnsupportedOperationException("Change for desktop app");
        return url.startsWith(URL_PREFIX);
    }

    public int getMajorVersion() {
        //throw new UnsupportedOperationException("Change for desktop app");
        return MAJOR_VERSION;
    }

    public int getMinorVersion() {
        //throw new UnsupportedOperationException("Change for desktop app");
        return MINOR_VERSION;
    }

    public DriverPropertyInfo[] getPropertyInfo(String str, Properties props) {
        //throw new UnsupportedOperationException("Change for desktop app");
        return new DriverPropertyInfo[0];
    }

    public boolean jdbcCompliant() {
        //throw new UnsupportedOperationException("Change for desktop app");
        return false;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}