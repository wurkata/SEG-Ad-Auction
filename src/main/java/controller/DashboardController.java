package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import common.FileType;
import common.Observer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.*;
import model.DAO.DBPool;
import model.DBTasks.DB;
import model.DBTasks.getCampaignsForUser;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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

    @FXML
    private JFXButton uploadBtn;

    @FXML
    private ProgressIndicator uploadProgress;


    private List<Model> models = new ArrayList<Model>();
    private List<Model> modelsToLoad = new ArrayList<Model>();
    private JFXButton drawCampaigns;

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
        uploadProgress.setVisible(false);
        // importImpressionLog.setDisable(true);
        // importClickLog.setDisable(true);
        // importServerLog.setDisable(true);
        uploadBtn.setDisable(true);

        loadCampaignBtn.setDisable(true);
        deleteCampaignBtn.setDisable(true);


        campaignsSet = new ArrayList<>();
        for (Model model : models) {
            model.addObserver(this);
        }

        RawDataHolder dataHolder = new RawDataHolder();
        dataHolder.addObserver(this);

        if (isOnline) getCampaigns();

        campaignsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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

        feedbackMsg.textProperty().setValue("");

        campaignTitle.textProperty().addListener(((observable, oldValue, newValue) -> {
            update(campaignTitle.getText());

            if (newValue.length() > 2) {
                if (campaignsList.getItems().stream().noneMatch(c -> c.getTitle().equals(newValue))) {
                    feedbackMsg.textProperty().setValue("");
                    importImpressionLog.setDisable(false);
                    importClickLog.setDisable(false);
                    importServerLog.setDisable(false);
                } else {
                    feedbackMsg.textProperty().setValue("Campaign with such name already exists.");
                }
            } else {
                feedbackMsg.textProperty().setValue("Campaign title should contain at least 3 characters.");
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

        newCampaignPane.setOnMouseReleased(e -> {
            if (newCampaignPane.isExpanded()) {
                campaignsList.translateYProperty().setValue(230);
                loadCampaignBtn.translateYProperty().setValue(230);
                uploadProgress.translateYProperty().setValue(230);
                uploadBtn.translateYProperty().setValue(230);
                deleteCampaignBtn.translateYProperty().setValue(230);
            } else {
                campaignsList.translateYProperty().setValue(15);
                loadCampaignBtn.translateYProperty().setValue(0);
                uploadProgress.translateYProperty().setValue(0);
                uploadBtn.translateYProperty().setValue(0);
                deleteCampaignBtn.translateYProperty().setValue(0);
            }
        });

        campaignsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        campaignsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            loadCampaignBtn.setDisable(false);
        });

        loadCampaignBtn.setOnMouseReleased(e -> {
            modelsToLoad.addAll(getSelectedCampaigns());

            if (modelsToLoad != null) {
                try {
                    goTo("campaign_scene", (Stage) loadCampaignBtn.getScene().getWindow(), new CampaignController(modelsToLoad));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            if (AccountController.online) {
                List<String> queryList = new ArrayList<>();
                String q;
                ObservableList<Campaign> selectedItems = campaignsList.getSelectionModel().getSelectedItems();

                for (Campaign c : selectedItems) {
                    q = "SELECT * FROM campaigns, impression_log i, server_log s, click_log c, subjects sb WHERE i.campaign_id='" + c.getId() +
                            "' AND s.campaign_id=i.campaign_id AND c.campaign_id = s.campaign_id AND campaigns.id=i.campaign_id AND sb.id=impression_log.subject_id";
                    queryList.add(q);
                }

                DB dbTask = new DB(queryList);

                dbTask.setOnSucceeded(a -> {
                    RawDataHolder rawDataHolder;

                    List<ResultSet> resultList = dbTask.getValue();
                    List<ImpressionLog> impressionLog = new ArrayList<>();

                    try {
                        String title = "Null";
                        for (ResultSet resultSet : resultList) {
                            rawDataHolder = new RawDataHolder();
                            while (resultSet.next()) {
                                title = resultSet.getString("campaigns.title");
                                impressionLog.add(new ImpressionLog(
                                        new SimpleDateFormat("EEEEE MMMMM dd HH:mm:ss z yyyy").parse(resultSet.getString("i.date")),
                                        resultSet.getString("i.subject_id"),
                                        resultSet.getString("i.context"),
                                        resultSet.getDouble("i.cost")
                                ));
                            }

                            rawDataHolder.setImpressionLog(impressionLog);
                            rawDataHolder.setClickLog(new ArrayList<>());
                            rawDataHolder.setServerLog(new ArrayList<>());
                            models.add(new Model(title, rawDataHolder));
                        }

                        goTo("campaign_scene", (Stage) loadCampaignBtn.getScene().getWindow(), new CampaignController(modelsToLoad));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                System.out.println("Starting query");

                new Thread(dbTask).start();
            } else {
                try {
                    goTo("campaign_scene", (Stage) loadCampaignBtn.getScene().getWindow(), new CampaignController(modelsToLoad));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        uploadBtn.setOnMouseReleased(e -> {
            uploadProgress.setVisible(true);
            Model m = campaignsList.getSelectionModel().getSelectedItems().get(0).getModel();
            m.setCampaignTitle(m.getName());
            m.uploadData();
        });

        createCampaignBtn.setOnMouseReleased(e -> {
            boolean canAdd = true;
            if (campaignTitle.getText().length() < 3) feedbackMsg.setText("Please, enter a campaign title.");
            else if (!isUniqueCampaignTitle(campaignTitle.getText())) feedbackMsg.setText("Campaign with such name already exists.");
            else if(!impProgress.isVisible()) feedbackMsg.setText("Please, upload Impression Log data.");
            else if(!servProgress.isVisible()) feedbackMsg.setText("Please, upload Server Log data.");
            else if(!clickProgress.isVisible()) feedbackMsg.setText("Please, upload Click Log data.");
            else {
                models.add(new Model(campaignTitle.getText(), dataHolder));
                campaignsList.getItems().add(new Campaign(0, campaignTitle.textProperty().getValue()));
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

            CampaignController controller = new CampaignController(modelsToLoad);
            goTo("campaign_scene", (Stage) createCampaignBtn.getScene().getWindow(), controller);

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

        if (arg instanceof String) {
            if (campaignTitle.getText().trim().isEmpty()) {
                hasName = false;
            } else if (isUniqueCampaignTitle(campaignTitle.getText())) {
                hasName = true;
            } else {
                hasName = false;
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
        return impressionLogLoaded && clickLogLoaded && serverLogLoaded && isUniqueCampaignTitle(campaignTitle.getText());
    }

    private void getCampaigns() {
        getCampaignsForUser getCampaignsTask = new getCampaignsForUser(AccountController.user.getId());
        getCampaignsTask.setOnSucceeded(e -> {
            campaignsSet = getCampaignsTask.getValue();

            ObservableList<Campaign> observableList = FXCollections.observableList(campaignsSet);

            campaignsList.setItems(observableList);
        });

        new Thread(getCampaignsTask).start();
    }

    public List<Model> getModels() {
        return models;
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }


    class DeleteCampaign extends Task<Boolean> {

        private Connection con;
        private ObservableList<Campaign> campaigns;

        public DeleteCampaign(ObservableList<Campaign> campaigns) {
            this.campaigns = campaigns;
        }

        @Override
        protected Boolean call() throws Exception {
            con = DBPool.getConnection();
            String query = "DELETE FROM campaigns WHERE title=?";

            PreparedStatement stmt = con.prepareStatement(query);

            for (Campaign c : campaigns) {
                stmt.setString(1, c.getTitle());
                stmt.addBatch();
            }

            stmt.executeBatch();
            DBPool.closeConnection(con);

            return null;
        }
    }

    private boolean isUniqueCampaignTitle(String title) {
        for (Model model : models) {
            if (model.getName().equals(title)) {
                return false;
            }
        }
        return true;
    }

    public List<Model> getSelectedCampaigns() {
        List<Model> modelsToAdd = new ArrayList<Model>();
        ObservableList<Campaign> selectedItems = campaignsList.getSelectionModel().getSelectedItems();

        for (Campaign c : selectedItems) {
            modelsToAdd.addAll(models.stream().filter(m -> m.getName().equals(c.getTitle())).collect(Collectors.toList()));
        }

        return modelsToAdd;
    }
}
