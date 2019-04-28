package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import common.FileType;
import common.Observer;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Campaign;
import model.Model;
import model.Parser;
import model.RawDataHolder;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable, Observer {
    @FXML
    private JFXButton createCampaignBtn;

    @FXML
    private JFXTextField campaignTitle;

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

    @FXML
    private JFXButton drawCmapaigns;

    @FXML
    private VBox verticalBox;

    private Model model = new Model();
    private boolean impressionLogLoaded = false;
    private boolean clickLogLoaded = false;
    private boolean serverLogLoaded = false;
    private boolean hasName = false;

    private Parser parserService;
    private File inputFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        model = new Model();
//        model.addObserver(this);
        RawDataHolder dataHolder = new RawDataHolder();
        dataHolder.addObserver(this);

        parserService = new Parser(dataHolder);

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
//                            model,
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

        campaignTitle.setOnKeyTyped(e -> {
                update(campaignTitle.getText());
        });

        createCampaignBtn.setOnMouseReleased(e-> {
            Campaign newCampaign = new Campaign(campaignTitle.getText(), new RawDataHolder());
            model.addCampaign(newCampaign);
            AnchorPane newCampaignPane = new AnchorPane();
            TitledPane pane = new TitledPane("",newCampaignPane);
            Label nameLabel = new Label(campaignTitle.getText());
            nameLabel.setPrefSize(300, 55);
            nameLabel.setStyle("-fx-font-size:20px;");
            pane.setGraphic(nameLabel);
            verticalBox.getChildren().add(pane);
            pane.setPadding(new Insets(0,0,0,0));
            HBox hBox = new HBox();
            JFXButton remove = new JFXButton("Remove");
            JFXButton export = new JFXButton("Export");
            remove.setStyle("-fx-background-color: #ff6961");
            export.setStyle("-fx-background-color: #77dd77");
            remove.setRipplerFill(javafx.scene.paint.Paint.valueOf("#ff6900"));
            export.setRipplerFill(javafx.scene.paint.Paint.valueOf("#77dd00"));
            remove.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
            export.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
            remove.setPrefSize(200, 60);
            export.setPrefSize(200, 60);
            remove.setPadding(new Insets(20,20,20,20));
            export.setPadding(new Insets(20,20,20,20));
            hBox.getChildren().addAll(remove, export);
            remove.setOnMouseReleased(f-> {
               verticalBox.getChildren().remove(pane);
               model.getCampaigns().remove(newCampaign);
            });
            export.setDisable(true);
            newCampaignPane.getChildren().add(hBox);
        });
    }

    private File importFile(FileType fileType) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import " + fileType + "...");
        return fc.showOpenDialog(importImpressionLog.getScene().getWindow());
    }

    @FXML
    private void createCampaign(Event event) throws Exception {

        model.addObserver(this);

        for (Campaign campaign: model.getCampaigns()
             ) {
            RawDataHolder rdh = campaign.getRdh();
            model.getRawDataHolders().add(rdh);
        }
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

        if(arg instanceof String){
            if (campaignTitle.getText().trim().isEmpty()) {
                hasName = false;
            }
            else {
                hasName = true;
            }
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
        return impressionLogLoaded && clickLogLoaded && serverLogLoaded && hasName;
    }
}
