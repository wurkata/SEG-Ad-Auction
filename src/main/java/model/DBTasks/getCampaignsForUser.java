package model.DBTasks;

import controller.AccountController;
import javafx.concurrent.Task;
import model.Campaign;
import model.DAO.DBPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class getCampaignsForUser extends Task<List<Campaign>> {

    Connection con;
    private long userId;

    public getCampaignsForUser(long userId) {
        this.userId = userId;
    }

    @Override
    protected List<Campaign> call() throws Exception {
        if(AccountController.online) {
            con = DBPool.getConnection();
            String query = "SELECT * FROM campaigns WHERE user_id=?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setLong(1, userId);

            ResultSet resultSet = stmt.executeQuery();
            List<Campaign> res = new ArrayList<>();

            while (resultSet.next()) {
                res.add(new Campaign(resultSet.getLong(1), resultSet.getString("title"), false));
            }

            return res;
        }else return new ArrayList<Campaign>();
    }
}
