package model.DAO;

public interface DAO {
    int BATCH_SIZE = 1000;
    // Enter host ip
    String DB_URL = "jdbc:mysql://host.ip:3306/seg?" +
            "sendStringParametersAsUnicode=false&" +
            "useServerPrepStmts=false" +
            "&rewriteBatchedStatements=true";
    String DRIVER = "com.mysql.cj.jdbc.Driver";
    String USER = "remote-root";
    String PASS = "soton-seg";
}
