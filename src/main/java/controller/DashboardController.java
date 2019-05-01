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
import model.DAO.SubjectsDAO;
import model.DBTasks.DB;
import model.DBTasks.Insert;
import model.DBTasks.getCampaignsForUser;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static model.DAO.DAO.BATCH_SIZE;

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
    private Label uploadProgressMsg;

    @FXML
    private ProgressIndicator uploadProgress;


    private List<Model> models = new ArrayList<Model>();
    private List<Model> modelsToLoad = new ArrayList<>();
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

    private User user;

    public DashboardController() {
    }

    public DashboardController(User user) {
        this.user = user;
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

        if (user != null) getCampaigns();

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

        campaignsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadCampaignBtn.setDisable(false);
                uploadBtn.setDisable((user == null || !newValue.isNew));
                deleteCampaignBtn.setDisable(false);

                if(!newValue.isNew) {
                    uploadProgressMsg.setText("Selected campaign is already uploaded in the database.");
                } else {
                    uploadProgressMsg.setText("");
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
                uploadProgressMsg.translateYProperty().setValue(230);
                deleteCampaignBtn.translateYProperty().setValue(230);
            } else {
                campaignsList.translateYProperty().setValue(15);
                loadCampaignBtn.translateYProperty().setValue(0);
                uploadProgress.translateYProperty().setValue(0);
                uploadBtn.translateYProperty().setValue(0);
                uploadProgressMsg.translateYProperty().setValue(0);
                deleteCampaignBtn.translateYProperty().setValue(0);
            }
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
            Campaign c = campaignsList.getSelectionModel().getSelectedItems().get(0);

            String query = "INSERT INTO campaigns (title, user_id) VALUES (" +
                    "'" + c.getTitle() + "'," +
                    "(SELECT id FROM users WHERE username='" + user.getName() + "'))";
            Insert insertTask = new Insert(query);

            new Thread(insertTask).start();

            SubjectsDAO subjectsDAO = new SubjectsDAO(c.getModel().getSubjects());
            UploadData uploadImp = new UploadData(c, FileType.IMPRESSION_LOG);
            UploadData uploadCli = new UploadData(c, FileType.CLICK_LOG);
            UploadData uploadSer = new UploadData(c, FileType.SERVER_LOG);

            subjectsDAO.setOnSucceeded(a -> {
                uploadProgressMsg.setText("Uploading IMPRESSION LOG...");
                uploadProgress.progressProperty().bind(uploadImp.progressProperty());
                new Thread(uploadImp).start();
            });
            uploadImp.setOnSucceeded(a -> {
                uploadProgressMsg.setText("Uploading CLICK LOG...");
                uploadProgress.progressProperty().bind(uploadCli.progressProperty());
                new Thread(uploadCli).start();
            });
            uploadCli.setOnSucceeded(a -> {
                uploadProgressMsg.setText("Uploading SERVER LOG...");
                uploadProgress.progressProperty().bind(uploadSer.progressProperty());
                new Thread(uploadSer).start();
            });
            uploadSer.setOnSucceeded(a -> {
                uploadProgressMsg.setText("Data was successfully uploaded.");
            });

            uploadProgressMsg.setText("Uploading Users Data...");
            uploadProgress.progressProperty().bind(subjectsDAO.progressProperty());
            new Thread(subjectsDAO).start();
        });

        createCampaignBtn.setOnMouseReleased(e -> {
            boolean canAdd = true;
            if (campaignTitle.getText().length() < 3) feedbackMsg.setText("Please, enter a campaign title.");
            else if (!isUniqueCampaignTitle(campaignTitle.getText()))
                feedbackMsg.setText("Campaign with such name already exists.");
            else if (!importImpressionLog.getText().equals("Change file..."))
                feedbackMsg.setText("Please, upload Impression Log data.");
            else if (!importClickLog.getText().equals("Change file..."))
                feedbackMsg.setText("Please, upload Click Log data.");
            else if (!importServerLog.getText().equals("Change file..."))
                feedbackMsg.setText("Please, upload Server Log data.");
            else {
                Model m = new Model(campaignTitle.getText(), dataHolder);
                models.add(m);

                Campaign c = new Campaign(0, campaignTitle.textProperty().getValue(), true);
                c.setModel(m);
                c.setRdh(dataHolder);
                campaignsList.getItems().add(c);
            }
        });

        deleteCampaignBtn.setOnMouseReleased(e -> {
            ObservableList<Campaign> selectedItems = campaignsList.getSelectionModel().getSelectedItems();
            selectedItems.forEach(c -> models.remove(c.getModel()));
            campaignsList.getItems().removeAll(selectedItems);

            if (user != null) {
                DeleteCampaign deleteCampaignTask = new DeleteCampaign(selectedItems);
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
        return campaignsList.getItems().stream().noneMatch(c -> c.getTitle().equals(title));
    }

    public List<Model> getSelectedCampaigns() {
        List<Model> modelsToAdd = new ArrayList<Model>();
        ObservableList<Campaign> selectedItems = campaignsList.getSelectionModel().getSelectedItems();

        for (Campaign c : selectedItems) {
            modelsToAdd.addAll(models.stream().filter(m -> m.getName().equals(c.getTitle())).collect(Collectors.toList()));
        }

        return modelsToAdd;
    }

    private class UploadData extends Task<List<Object>> {

        private FileType type;
        private Campaign c;
        private RawDataHolder dataHolder;
        private Connection con;
        private Object entry;

        public UploadData(Campaign c, FileType type) {
            this.c = c;
            this.type = type;
            dataHolder = c.getRdh();
        }

        @Override
        protected List<Object> call() {
            if(AccountController.online) {
                try {
                    con = DBPool.getConnection();
                    PreparedStatement pstmt;

                    int i;

                    switch (type) {
                        case IMPRESSION_LOG:
                            pstmt = con.prepareStatement("INSERT INTO impression_log (campaign_id, subject_id, date, context, cost) " +
                                    "VALUES (" +
                                    "(SELECT id FROM campaigns WHERE title=? LIMIT 1), " +
                                    "(SELECT id FROM subjects WHERE subject_id=? LIMIT 1), " +
                                    "?, ?, ?" +
                                    ")"
                            );

                            i = 0;

                            List<ImpressionLog> impressionLog = dataHolder.getImpressionLog();
                            for (ImpressionLog log : impressionLog) {
                                pstmt.setString(1, c.getTitle());
                                pstmt.setString(2, log.getSubjectID());
                                pstmt.setString(3, log.getImpressionDate().toString());
                                pstmt.setString(4, log.getContext());
                                pstmt.setDouble(5, log.getImpressionCost());

                                pstmt.addBatch();

                                i++;

                                if (i % BATCH_SIZE == 0) {
                                    pstmt.executeBatch();
                                    con.commit();
                                }
                            }
                            pstmt.executeBatch();
                            DBPool.closeConnection(con);
                            break;
                        case CLICK_LOG:
                            pstmt = con.prepareStatement("INSERT INTO click_log(campaign_id, subject_id, date, click_cost) " +
                                    "VALUES (" +
                                    "(SELECT id FROM campaigns WHERE title=? LIMIT 1), " +
                                    "(SELECT id FROM subjects WHERE subject_id=? LIMIT 1), " +
                                    "?, ?" +
                                    ")"
                            );

                            i = 0;

                            List<ClickLog> clickLog = dataHolder.getClickLog();
                            for (ClickLog log : clickLog) {
                                pstmt.setString(1, c.getTitle());
                                pstmt.setString(2, log.getSubjectID());
                                pstmt.setString(3, log.getClickDate().toString());
                                pstmt.setDouble(4, log.getClickCost());

                                pstmt.addBatch();

                                i++;

                                if (i % BATCH_SIZE == 0) {
                                    pstmt.executeBatch();
                                    con.commit();
                                }
                            }
                            pstmt.executeBatch();
                            con.commit();
                            break;
                        case SERVER_LOG:
                            pstmt = con.prepareStatement("INSERT INTO server_log (campaign_id, subject_id, entry_date, exit_date, pages_viewed, conversion) " +
                                    "VALUES (" +
                                    "(SELECT id FROM campaigns WHERE title=? LIMIT 1), " +
                                    "(SELECT id FROM subjects WHERE subject_id=? LIMIT 1), " +
                                    "?, ?, ?, ?" +
                                    ")"
                            );

                            i = 0;

                            List<ServerLog> serverLog= dataHolder.getServerLog();
                            for (ServerLog log : serverLog) {
                                pstmt.setString(1, c.getTitle());
                                pstmt.setString(2, log.getSubjectID());
                                pstmt.setString(3, (entry = log.getEntryDate()) == null ? "null" : entry.toString());
                                pstmt.setString(4, (entry = log.getExitDate()) == null ? "null" : entry.toString());
                                pstmt.setInt(5, (int) (entry = log.getPagesViewed()) < 0 ? -1 : (int) entry);
                                pstmt.setBoolean(6, log.getConversion() > 0);

                                pstmt.addBatch();

                                i++;

                                if (i % BATCH_SIZE == 0) {
                                    pstmt.executeBatch();
                                    con.commit();
                                }
                            }
                            pstmt.executeBatch();
                            con.commit();
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void failed() {
            super.failed();
            uploadProgressMsg.setText("Uploading data failed...");
            uploadProgress.setVisible(false);
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            uploadProgress.setVisible(false);
        }
    }
}
