package model.DBTasks;

import javafx.concurrent.Task;
import model.DAO.DBPool;
import model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CheckAccount extends Task<User> {

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
    protected User call() throws Exception {
        System.out.println("Authenticating user " + user);
        con = DBPool.getConnection();

        try {
            Statement stmt = con.createStatement();
            String query;

            if (pwd != null) {
                query = "SELECT * FROM users WHERE username='" + user + "' AND pwd='" + pwd + "';";
                pwd = null;
            } else {
                query = "SELECT * FROM users WHERE username='" + user + "';";
            }

            ResultSet result = stmt.executeQuery(query);

            if(result.next()) {
                System.out.println("Successful authentication.");
                return new User(result.getLong(1), result.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Unsuccessful authentication.");
        DBPool.closeConnection(con);
        return null;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        try {
            DBPool.closeConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

