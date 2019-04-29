package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Model;

import java.io.IOException;

public class GlobalController {

    private Model model;

    void goTo(String sceneName, Stage stage, GlobalController controller) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/" + sceneName + ".fxml"));

        if (controller != null) fxmlLoader.setController(controller);

        Scene scene = new Scene(fxmlLoader.load());

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
