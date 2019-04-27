package model.DBTasks;

import javafx.concurrent.Task;
import model.DAO.DBPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class getCampaignsForUser extends Task<Set<String>> {

    Connection con;
    private String user;

    public getCampaignsForUser(String user) {
        this.user = user;
    }

    @Override
    protected Set<String> call() throws Exception {
        con = DBPool.getConnection();
        Statement stmt = con.createStatement();

        ResultSet resultSet;
        Set<String> res = new HashSet<>();

        resultSet = stmt.executeQuery("SELECT title FROM " +
                "(campaign_users INNER JOIN campaigns ON campaigns.id=campaign_id) " +
                "WHERE user_id=(SELECT id FROM users WHERE username='" + user + "' LIMIT 1)");

        while (resultSet.next()) {
            res.add(resultSet.getString("title"));
        }

        return res;
    }
}
