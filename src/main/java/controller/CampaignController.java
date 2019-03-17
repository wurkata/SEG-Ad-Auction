package controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXSlider;
import common.Granularity;
import common.Metric;
import common.Observer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import model.Model;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class CampaignController implements Initializable, Observer {
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

    public Model model;

    private GraphController graphControllerService;

    CampaignController(Model model) {
        this.model = model;

        graphControllerService = new GraphController(this, model);
        graphControllerService.addObserver(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chartProgress.setVisible(false);
        chartProgress.toFront();

        model.restart();

        chartProgress.progressProperty().unbind();

        setChartGranularitySliderLabels();


        chartToggleGroup.selectedToggleProperty().addListener(e -> {
                    customBRBtn.setDisable(false);
                    chartGranularitySlider.setDisable(false);

                    campaignChartViewer.setVisible(false);
                    chartProgress.setVisible(true);


                    chartProgress.progressProperty().bind(graphControllerService.progressProperty());
                    graphControllerService.restart();
                }
        );

        BRPagesVisited.valueProperty().addListener(e -> model.setBouncePageReq(BRPagesVisited.getValue()));
        BRTimeSpentH.valueProperty().addListener( e -> Platform.runLater(new BounceTimeChange()));
        BRTimeSpentM.valueProperty().addListener( e -> Platform.runLater(new BounceTimeChange()));
        BRTimeSpentS.valueProperty().addListener( e -> Platform.runLater(new BounceTimeChange()));

        customBRBtn.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
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
            }
        });
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
                    model.setGranularity(Granularity.HOUR);
                    break;
                case 2:
                    model.setGranularity(Granularity.DAY);
                    break;
                case 3:
                    model.setGranularity(Granularity.MONTH);
                    break;
                case 4:
                    model.setGranularity(Granularity.YEAR);
                    break;
            }

            if (chartToggleGroup.getSelectedToggle() != null) {
                chartProgress.progressProperty().bind(graphControllerService.progressProperty());
                graphControllerService.restart();
            }
        });
    }

    private void initTimeSpinners() {
        BRPagesVisited.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000));
        BRTimeSpentH.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 24));
        BRTimeSpentM.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59));
        BRTimeSpentS.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59));
    }

    @Override
    public void update() {

    }

    @Override
    public void update(Object arg) {
        if (arg instanceof TimeSeries) {
            JFreeChart chart = campaignChartViewer.getChart();
            XYPlot plot = chart.getXYPlot();
            plot.getRenderer().setSeriesStroke(0, new BasicStroke(4.0f));
            plot.setDataset(new TimeSeriesCollection((TimeSeries) arg));
            plot.getDomainAxis().setAutoRange(true);
        } else {
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
    }

    class BounceTimeReset extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            model.resetBounceFilters();
            return null;
        }
    }

    class BounceTimeChange extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            model.setBounceTime((Integer) BRTimeSpentS.getValueFactory().getValue() * 1000 + (Integer) BRTimeSpentM.getValueFactory().getValue() * 60 * 1000 + (Integer) BRTimeSpentH.getValueFactory().getValue() * 60 * 60 * 1000);
            return null;
        }
    }
}
