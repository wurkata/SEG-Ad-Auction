package controller;

import common.Granularity;
import common.Metric;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import model.Model;

import java.io.File;
import java.net.URL;
import java.util.*;

public class FXController implements Initializable {
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
    private Slider chartGranularitySlider;

    @FXML
    private LineChart<String, Number> campaignChart;

    @FXML
    private ToggleGroup chartToggleGroup;
    @FXML
    private RadioButton noImpressionsBtn;
    @FXML
    private RadioButton noClicksBtn;
    @FXML
    private RadioButton noUniqueClicksBtn;
    @FXML
    private RadioButton noConversionsBtn;
    @FXML
    private RadioButton noBouncesBtn;
    @FXML
    private RadioButton bounceRateBtn;
    @FXML
    private RadioButton totalCostBtn;
    @FXML
    private RadioButton CTRBtn;
    @FXML
    private RadioButton CPCBtn;
    @FXML
    private RadioButton CPMBtn;
    @FXML
    private RadioButton CPABtn;

    private Model auctionModel;

    public FXController() {
        this.auctionModel = new Model(
                new File("input/impression_log.csv"),
                new File("input/click_log.csv"),
                new File("input/server_log.csv")
        );
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(auctionModel);

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

        campaignChart.dataProperty().bind(auctionModel.chartData.chartDataProperty());

        chartToggleGroup.selectedToggleProperty().addListener(e -> {
            Task<Void> plotChart = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // campaignChart.getData().clear();

                    XYChart.Series<String, Number> campaignSeries = new XYChart.Series<>();
                    if (noImpressionsBtn.isSelected()) {
                        auctionModel.setChartData(Metric.NUM_OF_IMPRESSIONS);
                        return null;
                        // campaignSeries.setName("Number of Impressions");
                        // auctionModel.getNumOfImpressionsPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                    }
                    if (noClicksBtn.isSelected()) {
                        campaignSeries.setName("Number of Clicks");
                        auctionModel.getNumOfClicksPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                    }
                    if (noUniqueClicksBtn.isSelected()) {
                        campaignSeries.setName("Number of Unique Clicks");
                        auctionModel.getNumOfUniqueClicksPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                    }
                    if (noConversionsBtn.isSelected()) {
                        campaignSeries.setName("Number of Conversions");
                        auctionModel.getNumOfConversionsPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                    }
                    if (noBouncesBtn.isSelected()) {
                        campaignSeries.setName("Number of Bounces");
                        auctionModel.getNumOfBouncesPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                    }
                    if (totalCostBtn.isSelected()) {
                        campaignSeries.setName("Total Cost");
                        auctionModel.getTotalCostPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                    }
                    if (bounceRateBtn.isSelected()) {
                        campaignSeries.setName("Bounce Rate");
                        auctionModel.getBounceRatePair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                    }
                    if (CTRBtn.isSelected()) {
                        campaignSeries.setName("Click-through-rate (CTR)");
                        auctionModel.getCTRPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                    }
                    if (CPCBtn.isSelected()) {
                        campaignSeries.setName("Cost-per-click (CPC)");
                        auctionModel.getClickCostPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                    }
                    if (CPMBtn.isSelected()) {
                        campaignSeries.setName("Cost-per-thousand Impressions (CPM)");
                        auctionModel.getCPMPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                    }
                    if (CPABtn.isSelected()) {
                        campaignSeries.setName("Cost-per-acquisition (CPA)");
                        auctionModel.getCPAPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                    }
                    return null;
                }
            };

            Platform.runLater(plotChart);
        });
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
        });
    }

    public void chartToggleGroupAction(ActionEvent event) {
        System.out.println("Toggled: " + chartToggleGroup.getSelectedToggle().getUserData().toString());
    }
}
