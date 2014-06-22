/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package database;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class DBConnection implements Connection {

    private DBConnectionPool pool;
    private Connection connection;
    private boolean isInUse;
    private long timestamp;

    public DBConnection(Connection connection, DBConnectionPool pool) {
        //throw new UnsupportedOperationException("Change for desktop app");
        this.connection = connection;
        this.pool = pool;
        this.isInUse = false;
        this.timestamp = 0;
    }

    public synchronized boolean lease() {
        //throw new UnsupportedOperationException("Change for desktop app");
        if (isInUse) {
            return false;
        } else {
            isInUse = true;

            timestamp = System.currentTimeMillis();

            return true;
        }
    }

    public boolean validate() {
        //throw new UnsupportedOperationException("Change for desktop app");
        try {
            connection.getMetaData();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean inUse() {
        //throw new UnsupportedOperationException("Change for desktop app");
        return isInUse;
    }

    public long getLastUse() {
        //throw new UnsupportedOperationException("Change for desktop app");
        return timestamp;
    }

    public void close() throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        pool.returnConnection(this);
    }

    protected void expireLease() {
        //throw new UnsupportedOperationException("Change for desktop app");
        isInUse = false;
    }

    protected Connection getConnection() {
        //throw new UnsupportedOperationException("Change for desktop app");
        return connection;
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        return connection.prepareStatement(sql);
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        return connection.prepareCall(sql);
    }

    public Statement createStatement() throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        return connection.createStatement();
    }

    public String nativeSQL(String sql) throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        return connection.nativeSQL(sql);
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        connection.setAutoCommit(autoCommit);
    }

    public boolean getAutoCommit() throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        return connection.getAutoCommit();
    }

    public void commit() throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        connection.commit();
    }

    public void rollback() throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        connection.rollback();
    }

    public boolean isClosed() throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        return connection.isClosed();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        return connection.getMetaData();
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        connection.setReadOnly(readOnly);
    }

    public boolean isReadOnly() throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        return connection.isReadOnly();
    }

    public void setCatalog(String catalog) throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        connection.setCatalog(catalog);
    }

    public String getCatalog() throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        return connection.getCatalog();
    }

    public void setTransactionIsolation(int level) throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        connection.setTransactionIsolation(level);
    }

    public int getTransactionIsolation() throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        return connection.getTransactionIsolation();
    }

    public SQLWarning getWarnings() throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        return connection.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        //throw new UnsupportedOperationException("Change for desktop app");
        connection.clearWarnings();
    }

    public void closeActual() throws SQLException {
        connection.close();
    }
    /*
     * Unimplemented Methods ...
     */
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setHoldability(int holdability) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getHoldability() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Savepoint setSavepoint() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Clob createClob() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Blob createBlob() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public NClob createNClob() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SQLXML createSQLXML() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isValid(int timeout) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getClientInfo(String name) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Properties getClientInfo() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setSchema(String schema) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getSchema() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void abort(Executor executor) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getNetworkTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}