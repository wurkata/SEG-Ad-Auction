package controller;

import com.jfoenix.controls.JFXButton;
import common.FileType;
import common.Observer;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Model;
import model.Parser;
import model.RawDataHolder;

import javax.swing.*;
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

    @FXML
    private ProgressIndicator impProgress;

    @FXML
    private ProgressIndicator clickProgress;

    @FXML
    private ProgressIndicator servProgress;

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

//        parserService = new Parser(new RawDataHolder());

        importImpressionLog.setOnMouseReleased(e -> {
            inputFile = importFile(FileType.IMPRESSION_LOG);
            if (inputFile != null) {
                parserService.setFile(inputFile, FileType.IMPRESSION_LOG);
                impProgress.setVisible(true);
                importImpressionLog.setVisible(false);
                parserService.restart();
            }
        });

        importClickLog.setOnMouseReleased(e -> {
            inputFile = importFile(FileType.CLICK_LOG);
            if (inputFile != null) {
                parserService.setFile(inputFile, FileType.CLICK_LOG);
                clickProgress.setVisible(true);
                importClickLog.setVisible(false);
                parserService.restart();
            }
        });

        importServerLog.setOnMouseReleased(e -> {
            inputFile = importFile(FileType.SERVER_LOG);
            if (inputFile != null) {
                parserService.setFile(inputFile, FileType.SERVER_LOG);
                servProgress.setVisible(true);
                importServerLog.setVisible(false);
                parserService.restart();
            }
        });

        addTestCampaign.setOnMouseReleased(e -> {
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
    private void createCampaign(Event event) throws Exception {
        CampaignController controller = new CampaignController(model);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/campaign_scene.fxml"));
        loader.setController(controller);
        Parent root = loader.load();

        Scene campaignsScene = new Scene(root);
        campaignsScene.getStylesheets().add(getClass().getResource("/css/campaign_scene.css").toExternalForm());

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(campaignsScene);
        window.setResizable(false);
        window.show();
    }

    @Override
    public void update() {

    }

    @SuppressWarnings("Duplicates")
    @Override
    public void update(Object arg) {
        if (arg == FileType.IMPRESSION_LOG) {
            Platform.runLater(() -> {
                importImpressionLog.setVisible(true);
                impProgress.setVisible(false);
                importImpressionLog.setText("Change file...");
                importImpressionLog.setStyle("-fx-background-color: #c5ff8c");
                impressionLogLoaded = true;
            });
        }
        if (arg == FileType.CLICK_LOG) {
            Platform.runLater(() -> {
                importClickLog.setVisible(true);
                clickProgress.setVisible(false);
                importClickLog.setText("Change file...");
                importClickLog.setStyle("-fx-background-color: #c5ff8c");
                clickLogLoaded = true;
            });
        }
        if (arg == FileType.SERVER_LOG) {
            Platform.runLater(() -> {
                importServerLog.setVisible(true);
                servProgress.setVisible(false);
                importServerLog.setText("Change file...");
                importServerLog.setStyle("-fx-background-color: #c5ff8c");
                serverLogLoaded = true;
            });
        }
        if(arg instanceof Exception){
//            Alert err = new Alert(Alert.AlertType.ERROR);
//            err.setTitle("File Import Error");
//            err.setHeaderText(null);
//            err.setContentText(((Exception) arg).getMessage());
//            err.showAndWait();
            String m = ((Exception) arg).getMessage();
            JOptionPane.showMessageDialog(new JFXPanel(), m, "File Import Error", JOptionPane.ERROR_MESSAGE);
            if(m.contains("impression")){
                importImpressionLog.setVisible(true);
                impProgress.setVisible(false);
                importImpressionLog.setText("Import");
                importImpressionLog.setStyle("-fx-background-color: #5ffab4");
                impressionLogLoaded = false;
            }else if(m.contains("click")){
                importClickLog.setVisible(true);
                clickProgress.setVisible(false);
                importClickLog.setText("Import");
                importClickLog.setStyle("-fx-background-color: #5ffab4");
                clickLogLoaded = false;
            }else if(m.contains("server")){
                importServerLog.setVisible(true);
                servProgress.setVisible(false);
                importServerLog.setText("Import");
                importServerLog.setStyle("-fx-background-color: #5ffab4");
                serverLogLoaded = false;
            }
        }

        if (canAddCampaign()) {
            createCampaignBtn.setDisable(false);
        }
    }

    private boolean canAddCampaign() {
        return impressionLogLoaded && clickLogLoaded && serverLogLoaded;
    }
}
