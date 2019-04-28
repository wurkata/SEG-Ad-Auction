package controller;

import com.jfoenix.controls.*;
import common.Granularity;
import common.Metric;
import common.Observer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;

import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Model;
import org.jfree.chart.ChartPanel;

import org.jfree.chart.fx.ChartViewer;


import javax.swing.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import java.net.URL;
import java.util.ResourceBundle;

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

    /* Histogram control */
    @FXML
    private JFXButton clickCostHistogram;
    @FXML
    JFXToggleButton toggleThemeMode;
    @FXML
    private JFXButton printBtn;

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


    public Model model;

    private GraphController graphControllerService;

    private String selectedFilter = "";

    private ObservableList<String> filters;

    private String theme_light;
    private String theme_dark;

    CampaignController(Model model) {
        this.model = model;

        graphControllerService = new GraphController(this, model);
        graphControllerService.addObserver(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        theme_light = getClass().getResource("/css/campaign_scene.css").toExternalForm();
        theme_dark = getClass().getResource("/css/campaign_scene-dark.css").toExternalForm();

        chartProgress.toFront();
        chartProgress.setVisible(false);

        new Thread(model).start();

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

        chartProgress.progressProperty().unbind();

//        setChartGranularitySliderLabels();

        customBRBtn.setDisable(false);
//        chartGranularitySlider.setDisable(false);
        appliedFiltersList.setDisable(false);
        addFilter.setDisable(false);


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

        BRPagesVisited.valueProperty().addListener(e -> model.setBouncePageReq(BRPagesVisited.getValue()));
        BRTimeSpentH.valueProperty().addListener(e -> Platform.runLater(new BounceTimeChange()));
        BRTimeSpentM.valueProperty().addListener(e -> Platform.runLater(new BounceTimeChange()));
        BRTimeSpentS.valueProperty().addListener(e -> Platform.runLater(new BounceTimeChange()));

        customBRBtn.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                customBRGrid.getChildren().forEach(e -> e.setDisable(false));
            } else {
                customBRGrid.getChildren().forEach(e -> {
                    if (!(e instanceof JFXCheckBox)) e.setDisable(true);
                });

                BRPagesVisited.getValueFactory().setValue(0);
                BRTimeSpentH.getValueFactory().setValue(0);
                BRTimeSpentM.getValueFactory().setValue(0);
                BRTimeSpentS.getValueFactory().setValue(0);

                Platform.runLater(new BounceTimeReset());
            }
        });

        filters = appliedFiltersList.getItems();
        addFilter.setOnMouseClicked(e -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/filter.fxml"));
                fxmlLoader.setController(new FilterController(model, this));

                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setTitle("Add Filter");
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
            model.removeFilter(filterID);
            selectedFilter = "";
            appliedFiltersList.getSelectionModel().clearSelection();
            filters.removeIf(e -> Integer.parseInt(e.split(":")[0]) == filterID);
            removeFilter.setDisable(true);
        });


        clickCostHistogram.setOnMouseClicked(event -> {
            FXMLLoader fxmlLoader = new FXMLLoader();
//            fxmlLoader.getClassLoader().getResource("/fxml/histogram.fxml");
            fxmlLoader.setLocation(getClass().getResource("/fxml/histogram.fxml"));
            fxmlLoader.setController(new HistogramController(model));
//            Parent root = null;
            try {
//                root = (Parent) fxmlLoader.load();
                Stage histogram = new Stage();
                histogram.setTitle("Click Cost Histogram");
                histogram.setScene(new Scene(fxmlLoader.load()));
                histogram.show();
            } catch (Exception e) {
                e.printStackTrace();
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
    }

    private void initGranularityGrid() {
        hourBtn.setDisable(false);
        dayBtn.setDisable(false);
        monthBtn.setDisable(false);
        yearBtn.setDisable(false);
        todBtn.setDisable(false);
        dowBtn.setDisable(false);

        hourBtn.setOnMouseClicked(e -> model.setGranularity(Granularity.HOUR));
        dayBtn.setOnMouseClicked(e -> model.setGranularity(Granularity.DAY));
        monthBtn.setOnMouseClicked(e -> model.setGranularity(Granularity.MONTH));
        yearBtn.setOnMouseClicked(e -> model.setGranularity(Granularity.YEAR));
        todBtn.setOnMouseClicked(e -> model.setGranularity(Granularity.ToD));
        dowBtn.setOnMouseClicked(e -> model.setGranularity(Granularity.DoW));

    }

    void addFilter(String filter) {
        filters.add(filter);
    }

    int getUsableID() {
        int[] used = filters.stream().mapToInt(e -> Integer.parseInt(e.split(":")[0])).toArray();
        int i = 1;
        do {
            boolean found = true;
            for (int j : used) {
                if (i == j) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return i;
            } else {
                i++;
            }
        } while (true);
    }

    private void initBindings() {
        noImpressions.textProperty().bind(model.metrics.MetricsProperty(Metric.NUM_OF_IMPRESSIONS));
        noClicks.textProperty().bind(model.metrics.MetricsProperty(Metric.NUM_OF_CLICKS));
        noUniqueClicks.textProperty().bind(model.metrics.MetricsProperty(Metric.NUM_OF_UNIQUE_CLICKS));
        noConversions.textProperty().bind(model.metrics.MetricsProperty(Metric.NUM_OF_CONVERSIONS));
        noBounces.textProperty().bind(model.metrics.MetricsProperty(Metric.NUM_OF_BOUNCES));
        bounceRate.textProperty().bind(model.metrics.MetricsProperty(Metric.BOUNCE_RATE));
        totalCost.textProperty().bind(model.metrics.MetricsProperty(Metric.TOTAL_COST));
        CTR.textProperty().bind(model.metrics.MetricsProperty(Metric.CTR));
        CPC.textProperty().bind(model.metrics.MetricsProperty(Metric.CPC));
        CPM.textProperty().bind(model.metrics.MetricsProperty(Metric.CPM));
        CPA.textProperty().bind(model.metrics.MetricsProperty(Metric.CPA));

    }

//    private void setChartGranularitySliderLabels() {
//        chartGranularitySlider.setLabelFormatter(new StringConverter<Double>() {
//            @Override
//            public String toString(Double n) {
//                switch (n.intValue()) {
//                    case 1:
//                        return "Hour";
//                    case 2:
//                        return "Day";
//                    case 3:
//                        return "Month";
//                    case 4:
//                        return "Year";
//                    case 5:
//                        return "Time";
//                    case 6:
//                        return "Day of Week";
//                    default:
//                        return "";
//                }
//            }
//
//            @Override
//            public Double fromString(String s) {
//                switch (s) {
//                    case "Hour":
//                        return 1d;
//                    case "Day":
//                        return 2d;
//                    case "Month":
//                        return 3d;
//                    case "Year":
//                        return 4d;
//                    case "Time":
//                        return 5d;
//                    case "Day of Week":
//                        return 6d;
//                    default:
//                        return 0d;
//                }
//            }
//        });
//
//        chartGranularitySlider.setOnMouseReleased(e -> {
//            switch ((int) chartGranularitySlider.getValue()) {
//                case 1:
//                    model.setGranularity(Granularity.HOUR);
//                    break;
//                case 2:
//                    model.setGranularity(Granularity.DAY);
//                    break;
//                case 3:
//                    model.setGranularity(Granularity.MONTH);
//                    break;
//                case 4:
//                    model.setGranularity(Granularity.YEAR);
//                    break;
//                case 5:
//                    model.setGranularity(Granularity.ToD);
//                    break;
//                case 6:
//                    model.setGranularity(Granularity.DoW);
//                    break;
//            }
//
//            if (chartToggleGroup.getSelectedToggle() != null) {
//                chartProgress.progressProperty().bind(graphControllerService.progressProperty());
//                graphControllerService.restart();
//            }
//        });
//    }

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
    public void update(Object arg) {
        switch (arg.toString()) {
            case "files":
                metricsGrid.getChildren().forEach(e -> {
                    if (e instanceof RadioButton) {
                        e.setDisable(false);
                    }
                });

                initTimeSpinners();
                initBindings();
                break;
            case "metrics":
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
    }

    class BounceTimeReset extends Task<Void> {
        @Override
        protected Void call() {
            model.resetBounceFilters();
            return null;
        }
    }

    class BounceTimeChange extends Task<Void> {
        @Override
        protected Void call() {
            model.setBounceTime((BRTimeSpentS.getValueFactory().getValue() * 1000) + (BRTimeSpentM.getValueFactory().getValue() * 60 * 1000) + (BRTimeSpentH.getValueFactory().getValue() * 60 * 60 * 1000));
            return null;
        }
    }
}
