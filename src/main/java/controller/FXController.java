package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXSlider;
import common.Granularity;
import common.Metric;
import common.Observer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import model.Model;

import java.net.URL;
import java.util.ResourceBundle;

public class FXController implements Initializable, Observer {
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
    private JFXSlider chartGranularitySlider;

    @FXML
    JFXSlider zoomChartX;
    @FXML
    ProgressIndicator chartProgress;
    @FXML
    LineChart<String, Number> campaignChart;

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

    public Model auctionModel;

    private GraphController graphController;

    public FXController(Model auctionModel) {
        this.auctionModel = auctionModel;
        this.graphController = new GraphController(this, auctionModel);
        graphController.addObserver(this);
        /*
        this.auctionModel = new Model(
                new File("input/impression_log.csv"),
                new File("input/click_log.csv"),
                new File("input/server_log.csv")
        );

        this.graphController = new GraphController(this, auctionModel);
        graphController.addObserver(this);
        */
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chartProgress.setVisible(false);
        chartProgress.toFront();
        // chartProgress.progressProperty().bind(auctionModel.progressProperty());

        Platform.runLater(auctionModel);

        chartProgress.progressProperty().unbind();

        setChartGranularitySliderLabels();

        noImpressions.textProperty().bind(auctionModel.metrics.MetricsProperty(Metric.NUM_OF_IMPRESSIONS));
        noClicks.textProperty().bind(auctionModel.metrics.MetricsProperty(Metric.NUM_OF_CLICKS));
        noUniqueClicks.textProperty().bind(auctionModel.metrics.MetricsProperty(Metric.NUM_OF_UNIQUE_CLICKS));
        noConversions.textProperty().bind(auctionModel.metrics.MetricsProperty(Metric.NUM_OF_CONVERSIONS));
        noBounces.textProperty().bind(auctionModel.metrics.MetricsProperty(Metric.NUM_OF_BOUNCES));
        bounceRate.textProperty().bind(auctionModel.metrics.MetricsProperty(Metric.BOUNCE_RATE));
        totalCost.textProperty().bind(auctionModel.metrics.MetricsProperty(Metric.TOTAL_COST));
        CTR.textProperty().bind(auctionModel.metrics.MetricsProperty(Metric.CTR));
        CPC.textProperty().bind(auctionModel.metrics.MetricsProperty(Metric.CPC));
        CPM.textProperty().bind(auctionModel.metrics.MetricsProperty(Metric.CPM));
        CPA.textProperty().bind(auctionModel.metrics.MetricsProperty(Metric.CPA));

        campaignChart.setAnimated(false);

        chartToggleGroup.selectedToggleProperty().addListener(e -> {
                    PlotChartTask plotChartTask = new PlotChartTask();
                    chartProgress.progressProperty().bind(plotChartTask.progressProperty());
                    Platform.runLater(plotChartTask);
                }
        );
    }

    private void setChartGranularitySliderLabels() {
        chartGranularitySlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                switch (n.intValue()) {
                    case 1:
                        return "Hour";
                    case 2:
                        return "Day";
                    case 3:
                        return "Month";
                    default:
                        return "Year";
                }
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "Hour":
                        return 1d;
                    case "Day":
                        return 2d;
                    case "Month":
                        return 3d;
                    default:
                        return 4d;
                }
            }
        });

        chartGranularitySlider.setOnMouseReleased(e -> {
            switch ((int) chartGranularitySlider.getValue()) {
                case 1:
                    auctionModel.setGranularity(Granularity.HOUR);
                    break;
                case 2:
                    auctionModel.setGranularity(Granularity.DAY);
                    break;
                case 3:
                    auctionModel.setGranularity(Granularity.MONTH);
                    break;
                case 4:
                    auctionModel.setGranularity(Granularity.YEAR);
                    break;
            }

            if (chartToggleGroup.getSelectedToggle() != null) {
                PlotChartTask plotChartTask = new PlotChartTask();
                chartProgress.progressProperty().bind(plotChartTask.progressProperty());
                Platform.runLater(plotChartTask);
            }
        });
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
                break;
            case "metrics":
                break;
            case "chart":
                break;
            default:
                break;
        }
    }

    class PlotChartTask extends Task {
        @Override
        protected Void call() {
            new Thread(graphController).start();
            return null;
        }
    }
}
