package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import common.FileType;
import common.Observer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.DAO.DBPool;
import model.DBTasks.getCampaignsForUser;
import model.ImpressionLog;
import model.Campaign;
import model.Model;
import model.Parser;
import model.RawDataHolder;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("unused")
public class DashboardController extends GlobalController implements Initializable, Observer {

    @FXML
    private Label feedbackMsg;

    @FXML
    private JFXTextField campaignTitle;

    @FXML
    private JFXButton createCampaignBtn;

    @FXML
    private JFXButton deleteCampaignBtn;

    @FXML
    private JFXButton importImpressionLog;

    @FXML
    private JFXButton importClickLog;

    @FXML
    private JFXButton importServerLog;

    @FXML
    private JFXButton addTestCampaign;

    @FXML
    private TitledPane newCampaignPane;

    @FXML
    private ProgressIndicator impProgress;

    @FXML
    private ProgressIndicator clickProgress;

    @FXML
    private ProgressIndicator servProgress;

    @FXML
    private ListView<Campaign> campaignsList;

    @FXML
    private JFXButton loadCampaignBtn;


    private List<Model> models = new ArrayList<Model>();

    private JFXButton drawCmapaigns;

    @FXML
    private Accordion accordion;

    //    private Model model;
    private boolean impressionLogLoaded = false;
    private boolean clickLogLoaded = false;
    private boolean serverLogLoaded = false;
    private boolean hasName = false;

    private Parser parserService;
    private List<Campaign> campaignsSet;

    private File inputFile;

    private boolean isOnline;

    public DashboardController() {

    }

    public DashboardController(Boolean isOnline) {
        this.isOnline = isOnline;
    }

//    public DashboardController(Model model) {
//        this.model = model;
//    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        campaignsSet = new ArrayList<>();
        for (Model model : models) {
            model.addObserver(this);
        }

        RawDataHolder dataHolder = new RawDataHolder();
        dataHolder.addObserver(this);

        if (isOnline) getCampaigns();

        loadCampaignBtn.setDisable(true);

        campaignsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        feedbackMsg.textProperty().setValue("");

        importImpressionLog.setDisable(true);
        importClickLog.setDisable(true);
        importServerLog.setDisable(true);

        createCampaignBtn.setOnMouseReleased(this::createCampaign);
        createCampaignBtn.setOnMouseReleased(e -> {
            ObservableList<Campaign> selectedItems = campaignsList.getSelectionModel().getSelectedItems();
            models.add(new Model(campaignTitle.getText(), dataHolder));
            campaignsList.getItems().add(new Campaign((long) campaignsList.getItems().size() - 1,
                    campaignTitle.textProperty().getValue()));
        });

        campaignTitle.textProperty().addListener(((observable, oldValue, newValue) -> {
            update(campaignTitle.getText());

            if (newValue.length() > 2) {
                if (campaignsList.getItems().stream().noneMatch(c -> c.getTitle().equals(newValue))) {
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
//        model = new Model();
//        model.addObserver(this);

        parserService = new Parser(dataHolder);

        importImpressionLog.setOnMouseReleased(e -> {
            Parser parser = new Parser(dataHolder);
            inputFile = importFile(FileType.IMPRESSION_LOG);
            if (inputFile != null) {
                parser.setFile(inputFile, FileType.IMPRESSION_LOG);
                impProgress.setVisible(true);
                importImpressionLog.setVisible(false);
                new Thread(parser).start();
            }
        });

        importClickLog.setOnMouseReleased(e -> {
            Parser parser = new Parser(dataHolder);
            inputFile = importFile(FileType.CLICK_LOG);
            if (inputFile != null) {
                parser.setFile(inputFile, FileType.CLICK_LOG);
                clickProgress.setVisible(true);
                importClickLog.setVisible(false);
                new Thread(parser).start();
            }
        });

        importServerLog.setOnMouseReleased(e -> {
            Parser parser = new Parser(dataHolder);
            inputFile = importFile(FileType.SERVER_LOG);
            if (inputFile != null) {
                parser.setFile(inputFile, FileType.SERVER_LOG);
                servProgress.setVisible(true);
                importServerLog.setVisible(false);
                new Thread(parser).start();
            }
        });

//        addTestCampaign.setOnMouseReleased(e -> {
//            Parser parser = new Parser(
//                    new File("input/impression_log_small.csv"),
//                    new File("input/click_log_small.csv"),
//                    new File("input/server_log_small.csv"
//                    ));
//            parser.setOnSucceeded(a -> {
//                if (parser.getValue()) {
//                    createCampaign(a);
//                }
//            });
//        });

        newCampaignPane.setOnMouseReleased(e -> {
            if (newCampaignPane.isExpanded())
                campaignsList.translateYProperty().setValue(230);
            else
                campaignsList.translateYProperty().setValue(15);
        });

        campaignsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        campaignsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            loadCampaignBtn.setDisable(false);
        });

        loadCampaignBtn.setOnMouseReleased(e -> {
            if (AccountController.online) {
                LoadCampaign loadCampaignTask = new LoadCampaign();
                loadCampaignTask.setOnSucceeded(a -> {
                    try {
                        goTo("campaign_scene", (Stage) loadCampaignBtn.getScene().getWindow(), new CampaignController(models));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                });

                new Thread(loadCampaignTask).start();

            } else {
                try {
                    goTo("campaign_scene", (Stage) loadCampaignBtn.getScene().getWindow(), new CampaignController(models));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
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
            Model model = new Model();
            models.add(model);
            model.setUser(AccountController.user);
            model.setCampaignTitle(campaignTitle.textProperty().getValue());
            if (AccountController.online) model.uploadData();

            model.addObserver(this);
            RawDataHolder rdh = parserService.getRawDataHolder();
            Campaign campaign = new Campaign(campaignTitle.getText(), rdh, model);
            model.setRawDataHolder(rdh);

            CampaignController controller = new CampaignController(models);
            goTo("campaign_scene", (Stage) createCampaignBtn.getScene().getWindow(), controller);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @FXML
//    private void createCampaign2(Event event) {
//        try {
//            model.setCampaignTitle(campaignTitle.textProperty().getValue());
//            model.uploadData();
//
//            model.addObserver(this);
//            RawDataHolder rdh = parserService.getRawDataHolder();
//            Campaign campaign = new Campaign(campaignTitle.getText(), rdh, model);
//            model.setRawDataHolder(rdh);
//
//            CampaignController controller = new CampaignController(models);
//
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/campaign_scene.fxml"));
//            loader.setController(controller);
//            Parent root = loader.load();
//
//            Scene campaignsScene = new Scene(root);
//            campaignsScene.getStylesheets().add(getClass().getResource("/css/campaign_scene.css").toExternalForm());
//
//            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
//
//            window.setScene(campaignsScene);
//            window.setResizable(false);
//            window.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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

        if (arg instanceof String) {
            if (campaignTitle.getText().trim().isEmpty()) {
                hasName = false;
            } else {
                hasName = true;
            }
        }

        if (arg instanceof Exception) {
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
        return impressionLogLoaded && clickLogLoaded && serverLogLoaded && hasName;
    }

    private void getCampaigns() {
        getCampaignsForUser getCampaignsTask = new getCampaignsForUser(AccountController.user.getId());
        getCampaignsTask.setOnSucceeded(e -> {
            campaignsSet = getCampaignsTask.getValue();

            ObservableList<Campaign> observableList = FXCollections.observableList(campaignsSet);

            campaignsList.setItems(observableList);
            campaignsList.setCellFactory(param -> new ListCell<Campaign>() {
                @Override
                protected void updateItem(Campaign item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null || item.getTitle() == null) {
                        setText(null);
                    } else {
                        setText(item.getTitle());
                    }
                }
            });
        });

        new Thread(getCampaignsTask).start();
    }

    class LoadCampaign extends Task<Boolean> {
        Connection con;

        @Override
        protected Boolean call() throws Exception {
            con = DBPool.getConnection();

            try {
                ResultSet resultSet;
                String query = "SELECT * FROM impression_log WHERE campaign_id=?";
                PreparedStatement stmt = con.prepareStatement(query);
                ObservableList<Campaign> selectedItems = campaignsList.getSelectionModel().getSelectedItems();

                for (Campaign c : selectedItems) {
                    stmt.setLong(1, c.getId());
                    resultSet = stmt.executeQuery();

                    List<ImpressionLog> impressionLog = new ArrayList<>();

                    while (resultSet.next()) {
                        impressionLog.add(new ImpressionLog(
                                new SimpleDateFormat("EEEEE MMMMM dd HH:mm:ss z yyyy").parse(resultSet.getString("date")),
                                resultSet.getString("subject_id"),
                                resultSet.getString("context"),
                                resultSet.getDouble("cost")
                        ));
                    }
                }

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }

    class DeleteCampaign extends Task<Boolean> {

        private Connection con;
        private String campaign;

        public DeleteCampaign(String campaign) {
            this.campaign = campaign;
        }

        @Override
        protected Boolean call() throws Exception {
            campaignsList.getItems().remove(campaign);

            con = DBPool.getConnection();

            try {
                String query = "DELETE * FROM campaigns WHERE title=? AND user_id=?";
                PreparedStatement stmt = con.prepareStatement(query);

                stmt.setString(1, campaign);
                stmt.setLong(2, AccountController.user.getId());


                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public List<Model> getModels() {
        return models;
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }
}
