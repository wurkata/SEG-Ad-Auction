package model.DAO;

public interface DAO {
    int BATCH_SIZE = 1000;
    String DB_URL = "jdbc:mysql://95.111.125.115:3306/seg?" +
            "sendStringParametersAsUnicode=false&" +
            "useServerPrepStmts=false" +
            "&rewriteBatchedStatements=true";
    String DRIVER = "com.mysql.cj.jdbc.Driver";
    String USER = "remote-root";
    String PASS = "758291Qq32@";
}
