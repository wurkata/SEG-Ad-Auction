package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import common.FileType;
import common.Observer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TitledPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.DBTasks.getCampaignsForUser;
import model.Model;
import model.Parser;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class DashboardController implements Initializable, Observer {

    @FXML
    @SuppressWarnings("unused")
    private Label feedbackMsg;

    @FXML
    @SuppressWarnings("unused")
    private JFXTextField campaignTitle;

    @FXML
    @SuppressWarnings("unused")
    private JFXButton createCampaignBtn;

    @FXML
    @SuppressWarnings("unused")
    private JFXButton importImpressionLog;

    @FXML
    @SuppressWarnings("unused")
    private JFXButton importClickLog;

    @FXML
    @SuppressWarnings("unused")
    private JFXButton importServerLog;

    @FXML
    @SuppressWarnings("unused")
    private JFXButton addTestCampaign;

    @FXML
    @SuppressWarnings("unused")
    private TitledPane newCampaignPane;

    @FXML
    @SuppressWarnings("unused")
    private ProgressIndicator impProgress;

    @FXML
    @SuppressWarnings("unused")
    private ProgressIndicator clickProgress;

    @FXML
    @SuppressWarnings("unused")
    private ProgressIndicator servProgress;

    @FXML
    @SuppressWarnings("unused")
    private ListView<String> campaignsList;

    @FXML
    @SuppressWarnings("unused")
    private JFXButton loadCampaignBtn;

    private Model model;
    private boolean impressionLogLoaded = false;
    private boolean clickLogLoaded = false;
    private boolean serverLogLoaded = false;

    private Set<String> campaignsSet;

    private File inputFile;

    DashboardController(Model model) {
        this.model = model;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        campaignsSet = new HashSet<>();
        model.addObserver(this);

        getCampaigns();

        loadCampaignBtn.setDisable(true);

        feedbackMsg.textProperty().setValue("");

        importImpressionLog.setDisable(true);
        importClickLog.setDisable(true);
        importServerLog.setDisable(true);

        createCampaignBtn.setOnMouseReleased(this::createCampaign);

        campaignTitle.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue.length() > 2) {
                if (!campaignsSet.contains(newValue)) {
                    feedbackMsg.textProperty().setValue("");
                    importImpressionLog.setDisable(false);
                    importClickLog.setDisable(false);
                    importServerLog.setDisable(false);
                    createCampaignBtn.setDisable(false);
                } else {
                    feedbackMsg.textProperty().setValue("Campaign with such name already exists.");
                    importImpressionLog.setDisable(true);
                    importClickLog.setDisable(true);
                    importServerLog.setDisable(true);
                    createCampaignBtn.setDisable(true);
                }
            } else {
                feedbackMsg.textProperty().setValue("Campaign title should contain at least 3 characters.");
                importImpressionLog.setDisable(true);
                importClickLog.setDisable(true);
                importServerLog.setDisable(true);
            }
        }));

        importImpressionLog.setOnMouseReleased(e -> {
            Parser parser = new Parser(model);
            inputFile = importFile(FileType.IMPRESSION_LOG);
            if (inputFile != null) {
                parser.setFile(inputFile, FileType.IMPRESSION_LOG);
                impProgress.setVisible(true);
                importImpressionLog.setVisible(false);
                new Thread(parser).start();
            }
        });

        importClickLog.setOnMouseReleased(e -> {
            Parser parser = new Parser(model);
            inputFile = importFile(FileType.CLICK_LOG);
            if (inputFile != null) {
                parser.setFile(inputFile, FileType.CLICK_LOG);
                clickProgress.setVisible(true);
                importClickLog.setVisible(false);
                new Thread(parser).start();
            }
        });

        importServerLog.setOnMouseReleased(e -> {
            Parser parser = new Parser(model);
            inputFile = importFile(FileType.SERVER_LOG);
            if (inputFile != null) {
                parser.setFile(inputFile, FileType.SERVER_LOG);
                servProgress.setVisible(true);
                importServerLog.setVisible(false);
                new Thread(parser).start();
            }
        });

        addTestCampaign.setOnMouseReleased(e -> {
            Parser parser = new Parser(
                    model,
                    new File("input/impression_log_small.csv"),
                    new File("input/click_log_small.csv"),
                    new File("input/server_log_small.csv"
                    ));
            parser.setOnSucceeded(a -> {
                if (parser.getValue()) {
                    createCampaign(a);
                }
            });
        });

        newCampaignPane.setOnMouseReleased(e -> {
            if (newCampaignPane.isExpanded())
                campaignsList.translateYProperty().setValue(230);
            else
                campaignsList.translateYProperty().setValue(15);
        });

        campaignsList.getSelectionModel().selectedItemProperty().addListener(e -> {
            loadCampaignBtn.setDisable(false);

            
        });
    }

    private File importFile(FileType fileType) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import " + fileType + "...");
        return fc.showOpenDialog(importImpressionLog.getScene().getWindow());
    }

    @FXML
    private void createCampaign(Event event) {
        try {
            model.setCampaignTitle(campaignTitle.textProperty().getValue());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (arg instanceof Exception) {
//            Alert err = new Alert(Alert.AlertType.ERROR);
//            err.setTitle("File Import Error");
//            err.setHeaderText(null);
//            err.setContentText(((Exception) arg).getMessage());
//            err.showAndWait();
            String m = ((Exception) arg).getMessage();
            JOptionPane.showMessageDialog(new JFXPanel(), m, "File Import Error", JOptionPane.ERROR_MESSAGE);
            if (m.contains("impression")) {
                importImpressionLog.setVisible(true);
                impProgress.setVisible(false);
                importImpressionLog.setText("Import");
                importImpressionLog.setStyle("-fx-background-color: #5ffab4");
                impressionLogLoaded = false;
            } else if (m.contains("click")) {
                importClickLog.setVisible(true);
                clickProgress.setVisible(false);
                importClickLog.setText("Import");
                importClickLog.setStyle("-fx-background-color: #5ffab4");
                clickLogLoaded = false;
            } else if (m.contains("server")) {
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

    private void getCampaigns() {
        getCampaignsForUser getCampaignsTask = new getCampaignsForUser(model.getUser());
        getCampaignsTask.setOnSucceeded(e -> {
            campaignsSet = getCampaignsTask.getValue();
            ObservableList<String> campaigns = FXCollections.observableArrayList(campaignsSet);

            campaignsList.setItems(campaigns);
        });

        new Thread(getCampaignsTask).start();
    }
}
