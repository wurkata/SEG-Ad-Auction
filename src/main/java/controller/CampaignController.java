package controller;

import com.jfoenix.controls.*;
import common.Filter;
import common.Filters.AgeFilter;
import common.Filters.AudienceFilter;
import common.Granularity;
import common.Metric;
import common.Observer;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;

import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Campaign;
import model.Model;
import model.User;
import org.jfree.chart.ChartPanel;

import org.jfree.chart.fx.ChartViewer;


import javax.swing.*;
import javax.xml.bind.annotation.XmlSchema;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import java.io.IOException;
import java.net.URL;
import java.util.*;

@SuppressWarnings("unused")
public class CampaignController extends GlobalController implements Initializable, Observer {
    @FXML
    private GridPane metricsGrid;

    @FXML
    private Label noImpressions;
    @FXML
    private Label noClicks;
    @FXML
    private Label noBounces;
    @FXML
    private Label noUniqueClicks;
    @FXML
    private Label noConversions;
    @FXML
    private Label totalCost;
    @FXML
    private Label CTR;
    @FXML
    private Label CPC;
    @FXML
    private Label CPM;
    @FXML
    private Label CPA;
    @FXML
    private Label bounceRate;

    @FXML
    JFXSlider zoomChartX;
    @FXML
    ProgressIndicator chartProgress;
    @FXML
    ChartViewer campaignChartViewer;

    @FXML
    private ToggleGroup chartToggleGroup;

    @FXML
    JFXRadioButton noImpressionsBtn;
    @FXML
    JFXRadioButton noClicksBtn;
    @FXML
    JFXRadioButton noUniqueClicksBtn;
    @FXML
    JFXRadioButton noConversionsBtn;
    @FXML
    JFXRadioButton noBouncesBtn;
    @FXML
    JFXRadioButton bounceRateBtn;
    @FXML
    JFXRadioButton totalCostBtn;
    @FXML
    JFXRadioButton CTRBtn;
    @FXML
    JFXRadioButton CPCBtn;
    @FXML
    JFXRadioButton CPMBtn;
    @FXML
    JFXRadioButton CPABtn;

    @FXML
    JFXButton removeCampaignBtn;

    /* Bounce Rate control */
    @FXML
    private GridPane customBRGrid;
    @FXML
    private JFXCheckBox customBRBtn;
    @FXML
    private Spinner<Integer> BRPagesVisited;
    @FXML
    private Spinner<Integer> BRTimeSpentH;
    @FXML
    private Spinner<Integer> BRTimeSpentM;
    @FXML
    private Spinner<Integer> BRTimeSpentS;

    @FXML
    private ListView<String> appliedFiltersList;
    @FXML
    private JFXButton addFilter;
    //    @FXML
//    private JFXButton filterInfo;
    @FXML
    private JFXButton removeFilter;

    /* Comparing Filtered Metrics */
    private HashMap<String, Integer> campaignCopies = new HashMap<>();

    @FXML
    private JFXButton duplicateCampaign;
    /* Histogram control */
    @FXML
    private JFXButton clickCostHistogram;
    @FXML
    JFXToggleButton toggleThemeMode;
    @FXML
    private JFXButton printBtn;

    @FXML
    private JFXButton backToDashboardBtn;

    @FXML
    private ToggleGroup granularityToggleGroup;

    @FXML
    private RadioButton hourBtn;

    @FXML
    private RadioButton dayBtn;

    @FXML
    private RadioButton monthBtn;

    @FXML
    private RadioButton yearBtn;

    @FXML
    private RadioButton todBtn;

    @FXML
    private RadioButton dowBtn;


    @FXML
    private JFXListView campaignsList;

    @FXML
    private JFXCheckBox highlightCheckBox;

    @FXML
    private Label highlightLabel;

    @FXML
    private Label highlightLabel2;

    @FXML
    private Spinner highlightSpinner;

    @FXML
    private Label selectedCampaignLabel;


    @FXML
    private JFXComboBox<String> costTypeCombo;

    public List<Model> models;

    private GraphController graphControllerService;

    private String selectedFilter = "";

    private ObservableList<String> filters;

    private String theme_light;
    private String theme_dark;

    private DashboardController dashboard;
    private User user;

    CampaignController(List<Model> models) {
        this.models = models;


        graphControllerService = new GraphController(this, models);
        graphControllerService.addObserver(this);
    }

    CampaignController(List<Model> models, User user) {
        this.user = user;
        this.models = models;


        graphControllerService = new GraphController(this, models);
        graphControllerService.addObserver(this);
    }


    private void initHighlighting(){
        highlightCheckBox.setOnMouseReleased(e->{
            if(highlightCheckBox.isSelected()){
                highlightLabel.setDisable(false);
                highlightSpinner.setDisable(false);
                highlightLabel2.setDisable(false);
//                highlightCombo.setDisable(false);
                graphControllerService.setHighlight(true);
                graphControllerService.restart();

            }else{
                highlightLabel.setDisable(true);
                highlightSpinner.setDisable(true);
                highlightLabel2.setDisable(true);
//                highlightCombo.setDisable(true);
                graphControllerService.setHighlight(false);
                graphControllerService.restart();
            }
        });

        highlightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 10));
        highlightSpinner.valueProperty().addListener(e-> {
            graphControllerService.setPercentileToHighlight((Integer) highlightSpinner.getValue());
            graphControllerService.restart();
        });


//        highlightCombo.getItems().addAll("Of Each Data Series", "Of All Data");

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        theme_light = getClass().getResource("/css/campaign_scene.css").toExternalForm();
        theme_dark = getClass().getResource("/css/campaign_scene-dark.css").toExternalForm();
        printBtn.setOnAction(a -> SwingUtilities.invokeLater(() -> {
                    PrinterJob job = PrinterJob.getPrinterJob();
                    PageFormat pf = job.defaultPage();
                    PageFormat pf2 = job.pageDialog(pf);
                    if (pf2 == pf)
                        return;
                    ChartPanel p = new ChartPanel(campaignChartViewer.getChart());
                    job.setPrintable(p, pf2);
                    if (!job.printDialog()) return;

                    try {
                        job.print();
                    } catch (PrinterException e) {
                        e.printStackTrace();
                    }
                }
        ));

        backToDashboardBtn.setOnMouseReleased(e-> {
            try {
                user.setModels(models);
                DashboardController dashBC = new DashboardController(user);
                List<Campaign> campaigns = new ArrayList<Campaign>();
                for (Model model : models) {
                    campaigns.add(new Campaign(model.getName(), model.getRawDataHolder(), model));
                }
                dashBC.getCampaignsList().getItems().addAll(campaigns);
                goTo("dashboard", (Stage) backToDashboardBtn.getScene().getWindow(),dashBC);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        removeCampaignBtn.setOnMouseReleased(e->{
            if(selectedModel==null){
                JOptionPane.showMessageDialog(new JFXPanel(), "Please select a campaign to remove.", "No Campaign Selected", JOptionPane.ERROR_MESSAGE);
            }else{
                if(models.size()<=1) {
                    JOptionPane.showMessageDialog(new JFXPanel(), "Cannot remove campaign when only one is loaded.\nUse the \"DashBoard\" button to go back to the dashboard to start again.", "Must Have At Least One Campaign Loaded", JOptionPane.ERROR_MESSAGE);
                }else{
                    campaignsList.getItems().remove(campaignsList.getSelectionModel().getSelectedItem());
                    models.remove(selectedModel);
                    campaignsList.getSelectionModel().select(0);
                    for(Model m:models){
                        if(m.getName().equals(campaignsList.getSelectionModel().getSelectedItem().toString())){
                            selectedModel=m;
                            selectedCampaignLabel.setText("Currently Selected Campaign: \n"+m.getName());
                            resetBounceButton(m);
                            break;
                        }

                    }
                    update("filter");
                }


            }
        });


        chartProgress.toFront();
        chartProgress.setVisible(false);
        chartProgress.progressProperty().unbind();
        for (Model model:models) {
            model.addObserver(this);
            new Thread(model).start();
        }
        updateList();


        customBRBtn.setDisable(false);
        appliedFiltersList.setDisable(false);
//        addFilter.setDisable(false);


        chartToggleGroup.selectedToggleProperty().addListener(e -> {
                    chartProgress.progressProperty().bind(graphControllerService.progressProperty());
                    graphControllerService.restart();
                }
        );

        granularityToggleGroup.selectedToggleProperty().addListener(e -> {
                    chartProgress.progressProperty().bind(graphControllerService.progressProperty());
                    graphControllerService.restart();
                }
        );

        BRPagesVisited.valueProperty().addListener(e -> getSelectedModel().setBouncePageReq(BRPagesVisited.getValue()));
        BRTimeSpentH.valueProperty().addListener(e -> Platform.runLater(new BounceTimeChange()));
        BRTimeSpentM.valueProperty().addListener(e -> Platform.runLater(new BounceTimeChange()));
        BRTimeSpentS.valueProperty().addListener(e -> Platform.runLater(new BounceTimeChange()));

        customBRBtn.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                customBRGrid.getChildren().forEach(e -> e.setDisable(false));
                getSelectedModel().setCustomBounce(true);
            } else {
                customBRGrid.getChildren().forEach(e -> {
                    if (!(e instanceof JFXCheckBox)) e.setDisable(true);
                });

                BRPagesVisited.getValueFactory().setValue(0);
                BRTimeSpentH.getValueFactory().setValue(0);
                BRTimeSpentM.getValueFactory().setValue(0);
                BRTimeSpentS.getValueFactory().setValue(0);
                getSelectedModel().setCustomBounce(false);
                Platform.runLater(new BounceTimeReset());
            }
        });

        filters = appliedFiltersList.getItems();
        addFilter.setOnMouseClicked(e -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/filter.fxml"));
                fxmlLoader.setController(new FilterController(getSelectedModel(), this));

                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setTitle("Add Filter To "+selectedModel.getName());
                stage.setScene(scene);
                stage.show();
            } catch (Exception er) {
                er.printStackTrace();
            }
        });

        appliedFiltersList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedFilter = newValue;
            removeFilter.setDisable(false);
        });

        removeFilter.setOnMouseClicked(event -> {
            int filterID = Integer.parseInt(selectedFilter.split(":")[0]);
            getSelectedModel().removeFilter(filterID);
            selectedFilter = "";
            appliedFiltersList.getSelectionModel().clearSelection();
            filters.removeIf(e -> Integer.parseInt(e.split(":")[0]) == filterID);
            removeFilter.setDisable(true);
        });

        duplicateCampaign.setOnMouseClicked(event -> {

            Model m = this.getSelectedModel();
            if(selectedModel==null){
                JOptionPane.showMessageDialog(new JFXPanel(), "Please select a campaign to duplicate.", "No Campaign Selected", JOptionPane.ERROR_MESSAGE);
            }else {
                if (campaignCopies.keySet().contains(m.getName())) {
                    campaignCopies.put(m.getName(), campaignCopies.get(m.getName()) + 1);

                } else {
                    campaignCopies.put(m.getName(), 1);

                }

                Model dupe = new Model("Copy # " + campaignCopies.get(m.getName()) + " of " + m.getName(), m.getRawDataHolder());
                this.models.add(dupe);
                dupe.addObserver(this);

                update("filter");
            }
        });

        clickCostHistogram.setOnMouseClicked(event -> {
            if(selectedModel==null){
                JOptionPane.showMessageDialog(new JFXPanel(), "Please select a campaign to generate a click-cost histogram for.", "No Campaign Selected", JOptionPane.ERROR_MESSAGE);
            }else {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/histogram.fxml"));
                fxmlLoader.setController(new HistogramController(getSelectedModel()));
                try {
                    Stage histogram = new Stage();
                    histogram.setTitle("Click Cost Histogram");
                    histogram.setScene(new Scene(fxmlLoader.load()));
                    histogram.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        toggleThemeMode.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            Scene scene = toggleThemeMode.getScene();
            if (newValue) {
                scene.getStylesheets().removeAll(scene.getStylesheets());
                scene.getStylesheets().add(theme_dark);
            } else {
                scene.getStylesheets().removeAll(scene.getStylesheets());
                scene.getStylesheets().add(theme_light);
            }

            graphControllerService.restart();
        }));

        initGranularityGrid();

        campaignsList.setOnMouseReleased(e->{
            String selected = campaignsList.getSelectionModel().getSelectedItem().toString();
            for(Model model:models){
                if(model.getName().equals(selected)){
                    updateMetrics(model);
                    updateFilterList(model);
                    this.selectedModel=model;
                    selectedCampaignLabel.setText("Currently Selected Campaign: \n"+model.getName());
                    if(addFilter.isDisabled()){
                        addFilter.setDisable(false);
                    }
                    if(costTypeCombo.isDisabled()){
                        costTypeCombo.setDisable(false);
                    }
                    if(clickCostHistogram.isDisabled()){
                        clickCostHistogram.setDisable(false);
                    }
                    if(duplicateCampaign.isDisabled()){
                        duplicateCampaign.setDisable(false);
                    }
                    if(removeCampaignBtn.isDisabled()){
                        removeCampaignBtn.setDisable(false);
                    }
                    resetBounceButton(model);
                }
            }
        });

        initHighlighting();
        initCostTypeCombo();
        update("files");
    }

    private void resetBounceButton(Model model){
        if(model.getCustomBounce()){
            customBRBtn.setSelected(true);
            BRPagesVisited.getValueFactory().setValue(model.getBouncePages());
            long millis = model.getBounceTime();
            BRTimeSpentH.getValueFactory().setValue(model.getBounceHours());
            BRTimeSpentM.getValueFactory().setValue(model.getBounceMinutes());
            BRTimeSpentS.getValueFactory().setValue(model.getBounceSeconds());
        }else{
            customBRBtn.setSelected(false);
        }

        if(model.getCostMode()){
            costTypeCombo.getSelectionModel().select(0);
        }else{
            costTypeCombo.getSelectionModel().select(1);
        }
    }

    public void updateList() {
        for (Model model: models) {
            if(!campaignsList.getItems().contains(model.getName())){
                campaignsList.getItems().add(model.getName());
            }

        }
    }

    private void initGranularityGrid() {
        hourBtn.setDisable(false);
        dayBtn.setDisable(false);
        monthBtn.setDisable(false);
        yearBtn.setDisable(false);
        todBtn.setDisable(false);
        dowBtn.setDisable(false);

        for (Model model: models) {

            hourBtn.setOnMouseClicked(e -> model.setGranularity(Granularity.HOUR));
            dayBtn.setOnMouseClicked(e -> model.setGranularity(Granularity.DAY));
            monthBtn.setOnMouseClicked(e -> model.setGranularity(Granularity.MONTH));
            yearBtn.setOnMouseClicked(e -> model.setGranularity(Granularity.YEAR));
            todBtn.setOnMouseClicked(e -> model.setGranularity(Granularity.ToD));
            dowBtn.setOnMouseClicked(e -> model.setGranularity(Granularity.DoW));
        }
    }

    int getUsableID(Model model) {
        return model.getUsableID();
    }



    private void updateMetrics(Model model){
        noImpressions.setText(Long.toString(model.getNumOfImpressions()));
        noClicks.setText(Long.toString(model.getNumOfClicks()));
        noUniqueClicks.setText(Long.toString(model.getNumOfUniqueClicks()));
        noConversions.setText(Long.toString(model.getNumOfConversions()));
        noBounces.setText(Long.toString(model.getNumOfBounces()));

        String doubleString;

        doubleString = Double.toString(model.getBounceRate());
        bounceRate.setText(doubleString.substring(0, doubleString.indexOf('.')+4));

        doubleString = Double.toString(model.getTotalCost());
        totalCost.setText(doubleString.substring(0, doubleString.indexOf('.')+4));

        doubleString = Double.toString(model.getCTR());
        CTR.setText(doubleString.substring(0, doubleString.indexOf('.')+4));

        doubleString = Double.toString(model.getClickCost());
        CPC.setText(doubleString.substring(0, doubleString.indexOf('.')+4));

        doubleString = Double.toString(model.getCPM());
        CPM.setText(doubleString.substring(0, doubleString.indexOf('.')+4));

        doubleString = Double.toString(model.getCPA());
        CPA.setText(doubleString.substring(0, doubleString.indexOf('.')+4));
    }

    private void updateFilterList(Model model){
        if(model!=null) {
            filters.clear();

            for (Integer i : model.getAgeFilters().keySet()) {
                filters.add(i + ": " + model.getAgeFilters().get(i).getFilterName());
            }
            for (Integer i : model.getContextFilters().keySet()) {
                filters.add(i + ": " + model.getContextFilters().get(i).getFilterName());
            }
            for (Integer i : model.getDateFilters().keySet()) {
                filters.add(i + ": " + model.getDateFilters().get(i).getFilterName());
            }
            for (Integer i : model.getGenderFilters().keySet()) {
                filters.add(i + ": " + model.getGenderFilters().get(i).getFilterName());
            }
            for (Integer i : model.getIncomeFilters().keySet()) {
                filters.add(i + ": " + model.getIncomeFilters().get(i).getFilterName());
            }
        }
    }

    private void initCostTypeCombo(){
        ObservableList<String> options = FXCollections.observableArrayList("Impression Cost", "Click Cost");
        costTypeCombo.setItems(options);
        costTypeCombo.getSelectionModel().select(0);
        costTypeCombo.getSelectionModel().selectedItemProperty().addListener(
                ((observable, oldValue, newValue) ->{
                    switch (newValue){
                        case "Impression Cost":
                            selectedModel.setCostMode(true);
                            break;
                        case "Click Cost":
                            selectedModel.setCostMode(false);
                            break;
                    }
                    graphControllerService.restart();
                })
        );
    }

    private void initTimeSpinners() {
        BRPagesVisited.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000));
        BRTimeSpentH.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
        BRTimeSpentM.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59));
        BRTimeSpentS.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59));
    }

    @Override
    public void update() {

    }

    @Override
    public synchronized void update(Object arg) {
        switch (arg.toString()) {
            case "files":
                metricsGrid.getChildren().forEach(e -> {
                    if (e instanceof RadioButton) {
                        e.setDisable(false);
                    }
                });

                initTimeSpinners();
//                initBindings();
                break;
            case "metrics":
                if (selectedModel!=null) updateMetrics(selectedModel);
                break;
            case "filter":
                if (campaignChartViewer.getChart() != null) {
                    chartProgress.progressProperty().bind(graphControllerService.progressProperty());
                    graphControllerService.restart();
                }
                break;
            default:
                break;
        }
        if(getSelectedModel()!=null) {
            updateFilterList(getSelectedModel());
            updateMetrics(getSelectedModel());
        }
        updateList();
    }

    class BounceTimeReset extends Task<Void> {
        Model model = getSelectedModel();
        protected Void call() {
            model.resetBounceFilters();
            return null;
        }
    }

    class BounceTimeChange extends Task<Void> {
        protected Void call() {
            if(getSelectedModel()!=null) {
                getSelectedModel().setBounceTime(BRTimeSpentS.getValueFactory().getValue(), BRTimeSpentM.getValueFactory().getValue(),  BRTimeSpentH.getValueFactory().getValue());
            }
            return null;
        }
    }
    private Model selectedModel;
    public Model getSelectedModel(){
        return selectedModel;
    }

    public DashboardController getDashboard() {
        return dashboard;
    }

    public void setDashboard(DashboardController dashboard) {
        this.dashboard = dashboard;
    }
}
