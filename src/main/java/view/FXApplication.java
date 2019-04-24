package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.DAO.DBPool;

import java.beans.PropertyVetoException;

public class FXApplication extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Ad Auction");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);

        try {
            DBPool.createDataSource();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

        primaryStage.show();
    }
}
