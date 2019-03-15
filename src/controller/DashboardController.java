package controller;

import com.jfoenix.controls.JFXButton;
import common.FileType;
import common.Observer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Model;
import model.Parser;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class DashboardController implements Initializable, Observer {
    @FXML
    private JFXButton createCampaignBtn;

    @FXML
    private JFXButton importImpressionLog;
    @FXML
    private JFXButton importClickLog;
    @FXML
    private JFXButton importServerLog;

    private Model model;
    private boolean impressionLogLoaded = false;
    private boolean clickLogLoaded = false;
    private boolean serverLogLoaded = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = new Model();
        model.addObserver(this);
    }

    @FXML
    private void createCampaign(ActionEvent event) throws Exception {
        FXController controller = new FXController(model);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/campaignsScene.fxml"));
        loader.setController(controller);
        Parent root = loader.load();

        Scene campaignsScene = new Scene(root);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(campaignsScene);
        window.show();
    }

    @FXML
    private void importFile (ActionEvent event) throws Exception {
        String importPath = Paths.get("./input").toAbsolutePath().normalize().toString();
        File importFile;

        FileChooser fc = new FileChooser();
        Parser p;

        String btnId = ((JFXButton) event.getSource()).getId();
        if (btnId.contains("Impression")) {
            fc.setTitle("Import Impression Log...");
            importFile = fc.showOpenDialog(((Node)event.getSource()).getScene().getWindow());
            Platform.runLater(new Parser(model, importFile, FileType.IMPRESSION_LOG));
        }
        if (btnId.contains("Click")) {
            fc.setTitle("Import Click Log...");
            importFile = fc.showOpenDialog(((Node)event.getSource()).getScene().getWindow());
            Platform.runLater(new Parser(model, importFile, FileType.CLICK_LOG));
        }
        if (btnId.contains("Server")) {
            fc.setTitle("Import Server Log...");
            importFile = fc.showOpenDialog(((Node)event.getSource()).getScene().getWindow());
            Platform.runLater(new Parser(model, importFile, FileType.SERVER_LOG));
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void update(Object arg) {
        if (arg == FileType.IMPRESSION_LOG) {
            importImpressionLog.setText("Change file...");
            importImpressionLog.setStyle("-fx-background-color: green");
            impressionLogLoaded = true;
        }
        if (arg == FileType.CLICK_LOG) {
            importClickLog.setText("Change file...");
            importClickLog.setStyle("-fx-background-color: green");
            clickLogLoaded = true;
        }
        if (arg == FileType.SERVER_LOG) {
            importServerLog.setText("Change file...");
            importServerLog.setStyle("-fx-background-color: green");
            serverLogLoaded = true;
        }

        if (canAddCampaign()) {
            createCampaignBtn.setDisable(false);
        }
    }

    private boolean canAddCampaign() {
        return impressionLogLoaded && clickLogLoaded && serverLogLoaded;
    }
}
