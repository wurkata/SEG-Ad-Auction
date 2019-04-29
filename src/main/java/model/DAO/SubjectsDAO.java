package model.DAO;

import controller.AccountController;
import javafx.concurrent.Task;
import model.Subject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SubjectsDAO extends Task<Void> implements DAO {
    private Map<String, Subject> subjects;

    public SubjectsDAO(Map<String, Subject> subjects) {
        this.subjects = subjects;
    }

    @Override
    protected Void call() throws Exception {
        if(AccountController.online) {
            Connection con = DBPool.getConnection();

            PreparedStatement stmt = con.prepareStatement("INSERT INTO subjects (subject_id, gender, age, income) " +
                    "VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                    "gender = COALESCE(?, gender), " +
                    "age = COALESCE(?, age), " +
                    "income = COALESCE(?, income)");

            int i = 0;

            String gender;
            String age;
            String income;

            Iterator<String> itr = subjects.keySet().iterator();
            String key;
            Subject s;

            while (itr.hasNext()) {
                key = itr.next();
                s = subjects.get(key);

                gender = s.getGender();
                age = s.getAge();
                income = s.getIncome();

                stmt.setString(1, key);
                stmt.setString(2, gender);
                stmt.setString(3, age);
                stmt.setString(4, income);
                stmt.setString(5, gender);
                stmt.setString(6, age);
                stmt.setString(7, income);

                stmt.addBatch();

                i++;

                if (i % BATCH_SIZE == 0) {
                    try {
                        stmt.executeBatch();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    con.commit();
                }
            }

            stmt.executeBatch();
            DBPool.closeConnection(con);
        }
        return null;
    }
}
