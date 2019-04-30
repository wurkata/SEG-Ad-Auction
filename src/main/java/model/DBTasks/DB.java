package model.DBTasks;

import javafx.concurrent.Task;
import model.DAO.DBPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DB extends Task<List<ResultSet>> {
    private Connection con;
    private List<String> queryList;

    public DB(String query) {
        queryList = new ArrayList<>();
        queryList.add(query);
    }

    public DB(List<String> queryList) {
        this.queryList = queryList;
    }

    @Override
    protected List<ResultSet> call() throws Exception {
        con = DBPool.getConnection();
        List<ResultSet> resultSets = new ArrayList<>();
        Statement stmt;

        for(String q : queryList) {
            stmt = con.createStatement();
            stmt.executeQuery(q);
        }

        return resultSets;
    }
}
