package model.DBTasks;

import javafx.concurrent.Task;
import model.DAO.DBPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class getCampaignsForClient extends Task<List<String>> {

    Connection con;
    private String client;

    public getCampaignsForClient(String client) {
        this.client= client;
    }

    @Override
    protected List<String> call() throws Exception {
        con = DBPool.getConnection();
        Statement stmt = con.createStatement();

        ResultSet resultSet;
        List<String> res = new ArrayList<>();

        resultSet = stmt.executeQuery("SELECT title FROM campaigns WHERE client_id=" +
                "(SELECT id FROM clients WHERE username='" + client + "' LIMIT 1)");

        while (resultSet.next()) {
            res.add(resultSet.getString("title"));
        }

        return res;
    }
}
