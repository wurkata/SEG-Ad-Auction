package model.DBTasks;

import javafx.concurrent.Task;
import model.DAO.DBPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class getCampaignsForUser extends Task<List<String>> {

    Connection con;
    private String user;

    public getCampaignsForUser(String user) {
        this.user = user;
    }

    @Override
    protected List<String> call() throws Exception {
        con = DBPool.getConnection();
        Statement stmt = con.createStatement();

        ResultSet resultSet;
        List<String> res = new ArrayList<>();

        resultSet = stmt.executeQuery("SELECT title FROM " +
                "(campaign_users INNER JOIN campaigns ON campaigns.id=campaign_id) " +
                "WHERE user_id=(SELECT id FROM users WHERE username='" + user + "' LIMIT 1))");

        while (resultSet.next()) {
            res.add(resultSet.getString("title"));
        }

        return res;
    }
}
