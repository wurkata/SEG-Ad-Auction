package model.DAO;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

public class DBPool implements DAO {
    private static final ComboPooledDataSource cpds = new ComboPooledDataSource();

    public static void createDataSource() throws PropertyVetoException {
        cpds.setDriverClass(DRIVER);
        cpds.setJdbcUrl(DB_URL);
        cpds.setUser(USER);
        cpds.setPassword(PASS);

        // c3p0 pool configuration settings
        // cpds.setAutoCommitOnClose(true);
        cpds.setMinPoolSize(5);
        cpds.setAcquireIncrement(5);
        cpds.setMaxPoolSize(20);
        cpds.setMaxStatements(20);
    }

    public static Connection getConnection() throws SQLException {
        Connection con = cpds.getConnection();
        con.setAutoCommit(false);

        return con;
    }

    public static void closeConnection(Connection con) throws SQLException {
        con.commit();

        System.out.println(cpds.getJdbcUrl());

        con.close();
    }
}
