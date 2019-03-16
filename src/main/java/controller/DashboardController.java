package controller;

import com.jfoenix.controls.JFXButton;
import common.FileType;
import common.Observer;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private final ExecutorService exec = Executors.newFixedThreadPool(5, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createCampaignBtn.setDisable(false);
        model = new Model();
        model.addObserver(this);

        importImpressionLog.setOnAction(e -> Platform.runLater(new ImportImpressionLog()));
        importClickLog.setOnAction(e -> Platform.runLater(new ImportClickLog()));
        importServerLog.setOnAction(e -> Platform.runLater(new ImportServerLog()));

        addTestCampaign.setOnAction(e -> {
            exec.execute(() -> {
                Parser p = new Parser(model, new File("input/impression_log.csv"), FileType.IMPRESSION_LOG);
                p.addObserver(this);
                Platform.runLater(p);
            });
            exec.execute(() -> {
                Parser p = new Parser(model, new File("input/click_log.csv"), FileType.CLICK_LOG);
                p.addObserver(this);
                Platform.runLater(p);
            });
            exec.execute(() -> {
                Parser p = new Parser(model, new File("input/server_log.csv"), FileType.SERVER_LOG);
                p.addObserver(this);
                Platform.runLater(p);
            });
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

    @FXML
    private void createCampaign(ActionEvent event) throws Exception {
        CampaignController controller = new CampaignController(model);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/campaignsScene.fxml"));
        loader.setController(controller);
        Parent root = loader.load();

        Scene campaignsScene = new Scene(root);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(campaignsScene);
        window.show();
    }

    class ImportImpressionLog extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            FileChooser fc = new FileChooser();
            fc.setTitle("Import Impression Log...");
            File importFile = fc.showOpenDialog(importImpressionLog.getScene().getWindow());
            if (importFile != null) exec.submit(new Parser(model, importFile, FileType.IMPRESSION_LOG));
            return null;
        }
    }

    class ImportClickLog extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            FileChooser fc = new FileChooser();
            fc.setTitle("Import Click Log...");
            File importFile = fc.showOpenDialog(importClickLog.getScene().getWindow());
            if (importFile != null) exec.submit(new Parser(model, importFile, FileType.CLICK_LOG));
            return null;
        }
    }

    class ImportServerLog extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            FileChooser fc = new FileChooser();
            fc.setTitle("Import Server Log...");
            File importFile = fc.showOpenDialog(importServerLog.getScene().getWindow());
            if (importFile != null) exec.submit(new Parser(model, importFile, FileType.SERVER_LOG));
            return null;
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void update(Object arg) {
        if (arg == FileType.IMPRESSION_LOG) {
            importImpressionLog.setText("Change file...");
            importImpressionLog.setStyle("-fx-background-color: #c5ff8c");
            impressionLogLoaded = true;
        }
        if (arg == FileType.CLICK_LOG) {
            importClickLog.setText("Change file...");
            importClickLog.setStyle("-fx-background-color: #c5ff8c");
            clickLogLoaded = true;
        }
        if (arg == FileType.SERVER_LOG) {
            importServerLog.setText("Change file...");
            importServerLog.setStyle("-fx-background-color: #c5ff8c");
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
