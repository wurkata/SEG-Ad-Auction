package model.DBTasks;

import javafx.concurrent.Task;
import model.DAO.DBPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CheckAccount extends Task<Boolean> {

    private String user;
    private String pwd;
    private Connection con;

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String pwd) {
        this.pwd = pwd;
    }

    @Override
    protected Boolean call() throws Exception {
        System.out.println("Authenticating user " + user);
        con = DBPool.getConnection();

        try {
            Statement stmt = con.createStatement();
            String query;

            if (pwd != null) {
                query = "SELECT * FROM clients WHERE username='" + user + "' AND password='" + pwd + "';";
                pwd = null;
            } else {
                query = "SELECT * FROM clients WHERE username='" + user + "';";
            }

            ResultSet result = stmt.executeQuery(query);

            if(result.next()) {
                System.out.println("Successful authentication.");
                DBPool.closeConnection(con);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Unsuccessful authentication.");
        DBPool.closeConnection(con);
        return false;
    }
}

