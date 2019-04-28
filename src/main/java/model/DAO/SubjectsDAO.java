package model.DAO;

import javafx.concurrent.Task;
import model.Subject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SubjectsDAO extends Task<Void> implements DAO {
    private List<Subject> subjects;

    public SubjectsDAO(List<Subject> subjects) {
        this.subjects = subjects;
    }

    @Override
    protected Void call() throws Exception {
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

        for (Subject u : subjects) {
            gender = u.getGender();
            age = u.getAge();
            income = u.getIncome();

            stmt.setString(1, u.getId());
            stmt.setString(2, gender);
            stmt.setString(3, age);
            stmt.setString(4, income);
            stmt.setString(5, gender);
            stmt.setString(6, age);
            stmt.setString(7, income);

            stmt.addBatch();

            i++;

            if(i % BATCH_SIZE == 0) {
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

        return null;
    }
}
