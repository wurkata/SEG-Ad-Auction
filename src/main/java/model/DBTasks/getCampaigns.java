package model.DBTasks;

import javafx.concurrent.Task;
import model.DAO.DBPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class getCampaigns extends Task<List<String>> {

    Connection con;

    @Override
    protected List<String> call() throws Exception {
        con = DBPool.getConnection();
        Statement stmt = con.createStatement();

        ResultSet resultSet;
        List<String> res = new ArrayList<>();

        resultSet = stmt.executeQuery("SELECT title FROM campaigns");

        while (resultSet.next()) {
            res.add(resultSet.getString("title"));
        }

        return res;
    }
}
