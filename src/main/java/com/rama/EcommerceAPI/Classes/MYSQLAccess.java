package com.rama.EcommerceAPI.Classes;


import com.rama.EcommerceAPI.Configuration.PropertiesFile;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MYSQLAccess {
    private static Boolean initialized = false;
    public static DataSource dataSourcePool = getDataSource();
    static {
        initialize();
    }

    public MYSQLAccess() {
        if (!initialized) {
            initialize();
        }
    }

    public MYSQLAccess(Boolean forceUpdate) {
        if (forceUpdate) {
            initialize();
        }
    }

    public static DataSource getDataSource() {

        try {
            String user = PropertiesFile.getMYSQL_DB_USERNAME();
            String password = PropertiesFile.getMYSQL_DB_PASSWORD();
            String host = PropertiesFile.getMYSQL_DB_HOST();
            String port = PropertiesFile.getMYSQL_DB_PORT();
            String schema = PropertiesFile.getMYSQL_DB_NAME();
            String url = "jdbc:mysql://" + host + ":" + port + "/" + schema + "?tcpKeepAlive=true&autoReconnect=true&useSSL=false&connectionAttributes=program_name:retaildsc";

            if (dataSourcePool == null) {
                HikariConfig config = new HikariConfig();
                config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                config.setJdbcUrl(url);
                config.setUsername(user);
                config.setPassword(password);

                config.setMinimumIdle(5);
                config.setMaximumPoolSize(100);
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

                dataSourcePool = new HikariDataSource(config);
            }
            return dataSourcePool;
        } catch (Exception ex) {
            return null;
        }
    }

    private static void initialize() {
        try {
            initialized = true;
        } catch (Exception ex) {
        }

    }

    public static Long insertQuery(String query, PreparedStatement ps) throws Exception {
        try {
            int affectedRows = ps.executeUpdate();
            Long LastId = null;

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                LastId = generatedKeys.getLong(1);
            }

            return LastId;
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean updateQuery(String query, PreparedStatement ps) throws SQLException, Exception {
        boolean resultQuery;
        try {
            ps.executeUpdate();
            resultQuery = true;
        } catch (SQLException ex) {
            resultQuery = false;
        }
        return resultQuery;
    }

    public static ResultSet selectQuery(String query, PreparedStatement ps) throws SQLException, Exception {
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
        } catch (SQLException ex) {
        }
        return rs;
    }
}