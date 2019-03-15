package controller;

import common.Metric;
import common.Observable;
import common.Observer;
import model.Model;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import java.text.SimpleDateFormat;

public class GraphController implements Observable, Runnable {
    private Model model;
    private CampaignController controller;

    private Metric metric;
    private JFreeChart chart;

    public GraphController(CampaignController controller, Model model) {
        this.controller = controller;
        this.model = model;
    }

    public void setMetric() {
        if (controller.noImpressionsBtn.isSelected()) {
            this.metric = Metric.NUM_OF_IMPRESSIONS;
        }
        if (controller.noClicksBtn.isSelected()) {
            this.metric = Metric.NUM_OF_CLICKS;
        }
        if (controller.noUniqueClicksBtn.isSelected()) {
            this.metric = Metric.NUM_OF_UNIQUE_CLICKS;
        }
        if (controller.noConversionsBtn.isSelected()) {
            this.metric = Metric.NUM_OF_CONVERSIONS;
        }
        if (controller.noBouncesBtn.isSelected()) {
            this.metric = Metric.NUM_OF_BOUNCES;
        }
        if (controller.totalCostBtn.isSelected()) {
            this.metric = Metric.TOTAL_COST;
        }
        if (controller.bounceRateBtn.isSelected()) {
            this.metric = Metric.BOUNCE_RATE;
        }
        if (controller.CTRBtn.isSelected()) {
            this.metric = Metric.CTR;
        }
        if (controller.CPCBtn.isSelected()) {
            this.metric = Metric.CPC;
        }
        if (controller.CPMBtn.isSelected()) {
            this.metric = Metric.CPM;
        }
        if (controller.CPABtn.isSelected()) {
            this.metric = Metric.CPA;
        }
    }

    public JFreeChart getChart() {
        return this.chart;
    }

    @Override
    public void run() {
        // controller.campaignChartViewer.setVisible(false);
        // controller.chartProgress.setVisible(true);
        plotChart();
    }

    private void plotChart() {
        setMetric();
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        String title = "";
        String val = "";
        switch (metric) {
            case CPA:
                TimeSeries cpa = new TimeSeries("CPA");
                controller.model.getCPAPair().forEach(e -> cpa.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Cost per Acquisition";
                val = "Cost /pence";
                dataset.addSeries(cpa);
                break;
            case CPC:
                TimeSeries clickCost = new TimeSeries("CPC");
                controller.model.getClickCostPair().forEach(e -> clickCost.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Cost per Click";
                val = "Cost /pence";
                dataset.addSeries(clickCost);
                break;
            case CPM:
                TimeSeries cpm = new TimeSeries("CPM");
                controller.model.getCPMPair().forEach(e -> cpm.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Cost per 1000 Impressions";
                val = "Cost /pence";
                dataset.addSeries(cpm);
                break;
            case CTR:
                TimeSeries ctr = new TimeSeries("CTR");
                controller.model.getCTRPair().forEach(e -> ctr.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Click-Through Rate";
                val = "Click-Through Rate";
                dataset.addSeries(ctr);
                break;
            case TOTAL_COST:
                TimeSeries totalCost = new TimeSeries("Total Cost");
                controller.model.getTotalCostPair().forEach(e -> totalCost.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Total Cost";
                val = "Cost /pence";
                dataset.addSeries(totalCost);
                break;
            case BOUNCE_RATE:
                TimeSeries bounceRate = new TimeSeries("Bounce Rate");
                controller.model.getBounceRatePair().forEach(e -> {
                    bounceRate.addOrUpdate(new Hour(e.getKey()), e.getValue());
                    notifyObservers(bounceRate);
                    try {
                        Thread.sleep(500);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                title = "Bounce Rate";
                val = "Bounce Rate";
                dataset.addSeries(bounceRate);
                break;
            case NUM_OF_CLICKS:
                TimeSeries numOfClicks = new TimeSeries("Number Of Clicks");
                controller.model.getNumOfClicksPair().forEach(e -> numOfClicks.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Number of Clicks";
                val = "Number of Clicks";
                dataset.addSeries(numOfClicks);
                break;
            case NUM_OF_BOUNCES:
                TimeSeries bp = new TimeSeries("Number Of Bounces");
                controller.model.getNumOfBouncesPair().forEach(e -> bp.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Number of Bounces";
                val = "Bounces";
                dataset.addSeries(bp);
                break;
            case NUM_OF_CONVERSIONS:
                TimeSeries conversion = new TimeSeries("Number Of Conversions");
                controller.model.getNumOfConversionsPair().forEach(e -> conversion.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Number of Conversions";
                val = "Conversions";
                dataset.addSeries(conversion);
                break;
            case NUM_OF_IMPRESSIONS:
                TimeSeries imp = new TimeSeries("Number Of Impressions");
                controller.model.getNumOfImpressionsPair().forEach(e -> {
                    imp.addOrUpdate(new Hour(e.getKey()), e.getValue());
                    notifyObservers(imp);
                });
                title = "Number of Impressions";
                val = "Impressions";
                dataset.addSeries(imp);
                break;
            case NUM_OF_UNIQUE_CLICKS:
                TimeSeries uclick = new TimeSeries("Number Of Unique Clicks");
                controller.model.getNumOfUniqueClicksPair().forEach(e -> uclick.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Number of Unique Clicks";
                val = "Unique Clicks";
                dataset.addSeries(uclick);
                break;
        }

        DateAxis x = new DateAxis("Date (YYYY-MM-DD HHHH)");
        x.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd HHHH"));
        XYPlot plot = new XYPlot(dataset, x, new NumberAxis(val), new XYLineAndShapeRenderer());
        plot.getRenderer().setSeriesStroke(0, new BasicStroke(4.0f));
        this.chart = new JFreeChart(title, plot);

        /*
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
        */

        controller.campaignChartViewer.setVisible(true);
        controller.chartProgress.setVisible(false);
        notifyObservers("chart");
    }

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(Object arg) {
        observers.forEach(o -> o.update(arg));
    }

    @Override
    public void notifyObservers() {
        observers.forEach(Observer::update);
    }
}
