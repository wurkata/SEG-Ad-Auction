package controller;

import common.Metric;
import javafx.application.Platform;
import javafx.concurrent.Task;
import model.Model;

import java.util.Observable;

public class GraphController extends Observable implements Runnable {
    private Model model;
    private FXController controller;

    public GraphController(FXController controller, Model model) {
        this.controller = controller;
        this.model = model;
    }

    @Override
    public void run() {
        controller.campaignChart.setVisible(false);
        controller.chartProgress.setVisible(true);
        plotChart();
    }

    private void plotChart() {
        if (controller.noImpressionsBtn.isSelected()) {
            model.setChartData(Metric.NUM_OF_IMPRESSIONS);
        }
        if (controller.noClicksBtn.isSelected()) {
            model.setChartData(Metric.NUM_OF_CLICKS);
        }
        if (controller.noUniqueClicksBtn.isSelected()) {
            model.setChartData(Metric.NUM_OF_UNIQUE_CLICKS);
        }
        if (controller.noConversionsBtn.isSelected()) {
            model.setChartData(Metric.NUM_OF_CONVERSIONS);
        }
        if (controller.noBouncesBtn.isSelected()) {
            model.setChartData(Metric.NUM_OF_BOUNCES);
        }
        if (controller.totalCostBtn.isSelected()) {
            model.setChartData(Metric.TOTAL_COST);
        }
        if (controller.bounceRateBtn.isSelected()) {
            model.setChartData(Metric.BOUNCE_RATE);
        }
        if (controller.CTRBtn.isSelected()) {
            model.setChartData(Metric.CTR);
        }
        if (controller.CPCBtn.isSelected()) {
            model.setChartData(Metric.CPC);
        }
        if (controller.CPMBtn.isSelected()) {
            model.setChartData(Metric.CPM);
        }
        if (controller.CPABtn.isSelected()) {
            model.setChartData(Metric.CPA);
        }

        Platform.runLater(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                controller.campaignChart.getData().clear();
                controller.campaignChart.getData().add(model.chartData.getChartData());
                controller.campaignChart.setVisible(true);
                controller.chartProgress.setVisible(false);
                return null;
            }
        });
        notifyObservers("chart");
    }

    @Override
    public void notifyObservers() {
        setChanged();
        super.notifyObservers();
    }

    @Override
    public void notifyObservers(Object arg) {
        setChanged();
        super.notifyObservers(arg);
    }
}
