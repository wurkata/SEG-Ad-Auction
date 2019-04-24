package model.DBTasks;

import javafx.concurrent.Task;
import model.DAO.DBPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CheckTitle extends Task<Boolean> {

    private String title;
    private Connection con;

    public CheckTitle(String title) {
        this.title = title;
    }

    @Override
    protected Boolean call() throws Exception {
        con = DBPool.getConnection();

        try {
            Statement stmt = con.createStatement();
            String query = "SELECT id FROM campaigns WHERE title='" + title + "';";

            ResultSet result = stmt.executeQuery(query);

            if(!result.next()) {
                DBPool.closeConnection(con);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        DBPool.closeConnection(con);
        return false;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
    }

    @Override
    protected void failed() {
        super.failed();
    }
}

