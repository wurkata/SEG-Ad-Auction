package model.DBTasks;

import javafx.concurrent.Task;
import model.DAO.DBPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;

public class Insert extends Task<Boolean> {
    private String ACTION;
    private Connection con;

    private Map<String, String> params;

    @Override
    protected Boolean call() throws Exception {
        if(ACTION.equals("ACCOUNT")) {
            con = DBPool.getConnection();

            try {
                Statement stmt = con.createStatement();
                String fields = getFields();
                String values = getValues();
                String query = "INSERT INTO clients (" + fields + ") VALUES (" + values + ");";

                stmt.executeUpdate(query);

                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    protected void failed() {
        super.failed();
        try {
            DBPool.closeConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public void setACTION(String ACTION) {
        this.ACTION = ACTION;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    private String getFields() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> itr = params.keySet().iterator();

        while(itr.hasNext()) {
            stringBuilder.append(itr.next());

            if(itr.hasNext()) stringBuilder.append(",");
        }

        return stringBuilder.toString();
    }

    private String getValues() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> itr = params.keySet().iterator();

        while(itr.hasNext()) {
            stringBuilder.append("'" + params.get(itr.next()) + "'");

            if(itr.hasNext()) stringBuilder.append(",");
        }

        return stringBuilder.toString();
    }
}
