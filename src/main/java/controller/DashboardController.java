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
import java.util.ResourceBundle;

public class DashboardController implements Initializable, Observer {
    @FXML
    private JFXButton createCampaignBtn;

    @FXML
    private JFXButton importImpressionLog;
    @FXML
    private JFXButton importClickLog;
    @FXML
    private JFXButton importServerLog;

    @FXML
    private JFXButton addTestCampaign;

    private Model model;
    private boolean impressionLogLoaded = false;
    private boolean clickLogLoaded = false;
    private boolean serverLogLoaded = false;

    private Parser parserService;
    private File inputFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = new Model();
        model.addObserver(this);

        parserService = new Parser(model);

        importImpressionLog.setOnAction(e -> {
            inputFile = importFile(FileType.IMPRESSION_LOG);
            if (inputFile != null) {
                parserService.setFile(importFile(FileType.IMPRESSION_LOG), FileType.IMPRESSION_LOG);
                parserService.restart();
            }
        });
        importClickLog.setOnAction(e -> {
            inputFile = importFile(FileType.CLICK_LOG);
            if (inputFile != null) {
                parserService.setFile(importFile(FileType.CLICK_LOG), FileType.CLICK_LOG);
                parserService.restart();
            }
        });
        importServerLog.setOnAction(e -> {
            inputFile = importFile(FileType.SERVER_LOG);
            if (inputFile != null) {
                parserService.setFile(importFile(FileType.SERVER_LOG), FileType.SERVER_LOG);
                parserService.restart();
            }
        });

        addTestCampaign.setOnAction(e -> {
            Platform.runLater(() ->
                    parserService = new Parser(
                            model,
                            new File("input/impression_log.csv"),
                            new File("input/click_log.csv"),
                            new File("input/server_log.csv"
                            ))
            );
            // Platform.runLater(new Parser(model, new File("input/impression_log.csv"), FileType.IMPRESSION_LOG));
            // Platform.runLater(new Parser(model, new File("input/click_log.csv"), FileType.CLICK_LOG));
            // Platform.runLater(new Parser(model, new File("input/server_log.csv"), FileType.SERVER_LOG));

            try {
                createCampaign(e);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private File importFile(FileType fileType) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import " + fileType + "...");
        return fc.showOpenDialog(importImpressionLog.getScene().getWindow());
    }

    @FXML
    private void createCampaign(ActionEvent event) throws Exception {
        CampaignController controller = new CampaignController(model);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/campaign_scene.fxml"));
        loader.setController(controller);
        Parent root = loader.load();

        Scene campaignsScene = new Scene(root);
        campaignsScene.getStylesheets().add(getClass().getResource("/css/campaign_scene.css").toExternalForm());

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(campaignsScene);
        window.show();
    }

    @Override
    public void update() {

    }

    @Override
    public void update(Object arg) {
        if (arg == FileType.IMPRESSION_LOG) {
            Platform.runLater(() -> {
                importImpressionLog.setText("Change file...");
                importImpressionLog.setStyle("-fx-background-color: #c5ff8c");
                impressionLogLoaded = true;
            });
        }
        if (arg == FileType.CLICK_LOG) {
            Platform.runLater(() -> {
                importClickLog.setText("Change file...");
                importClickLog.setStyle("-fx-background-color: #c5ff8c");
                clickLogLoaded = true;
            });
        }
        if (arg == FileType.SERVER_LOG) {
            Platform.runLater(() -> {
                importServerLog.setText("Change file...");
                importServerLog.setStyle("-fx-background-color: #c5ff8c");
                serverLogLoaded = true;
            });
        }

        if (canAddCampaign()) {
            createCampaignBtn.setDisable(false);
        }
    }

    private boolean canAddCampaign() {
        return impressionLogLoaded && clickLogLoaded && serverLogLoaded;
    }
}
