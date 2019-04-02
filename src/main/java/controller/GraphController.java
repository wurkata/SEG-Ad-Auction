package controller;

import common.Metric;
import common.Observable;
import common.Observer;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Model;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.RangeType;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import java.text.SimpleDateFormat;

public class GraphController extends Service<JFreeChart> implements Observable {
    private Model model;
    private CampaignController controller;

    private Metric metric;

    GraphController(CampaignController controller, Model model) {
        this.controller = controller;
        this.model = model;
    }

    private void setMetric() {
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

    private JFreeChart getChartFor(String title, String xLabel, String yLabel, TimeSeriesCollection dataset) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title,
                xLabel,
                yLabel,
                dataset,
                true,
                true,
                false
        );

        DateAxis dateAxis = (DateAxis)chart.getXYPlot().getDomainAxis();

        NumberAxis numAxis = (NumberAxis) chart.getXYPlot().getRangeAxis();

        numAxis.setRangeType(RangeType.POSITIVE);
        numAxis.setAutoRangeIncludesZero(true);
        numAxis.configure();

        switch(model.getGranularity()){
            case HOUR:
                dateAxis.setDateFormatOverride(new SimpleDateFormat("HH:59 dd/MM/yyyy"));
                break;
            case DAY:
                dateAxis.setDateFormatOverride(new SimpleDateFormat("dd/MM/yyyy"));
                break;
            case MONTH:
                dateAxis.setDateFormatOverride(new SimpleDateFormat("MM/yyyy"));
                break;
            case YEAR:
                dateAxis.setDateFormatOverride(new SimpleDateFormat("yyyy"));
                break;
            case ToD:
                dateAxis.setDateFormatOverride(new SimpleDateFormat("HH:59"));
                break;
            case DoW:
                dateAxis.setDateFormatOverride(new SimpleDateFormat("EEEE"));
                break;
        }

        controller.campaignChartViewer.setVisible(true);
        controller.chartProgress.setVisible(false);

        if (controller.toggleThemeMode.isSelected()) {
            chart.setBackgroundPaint(new Color(47, 56, 80));
            chart.getTitle().setPaint(new Color(26, 172, 231));
        } else {
            chart.setBackgroundPaint(Color.white);
            chart.getTitle().setPaint(Color.BLACK);
        }

        return chart;
    }

    private JFreeChart setChart() {
        setMetric();
        if(metric != null) {
            controller.campaignChartViewer.setVisible(false);
            controller.chartProgress.setVisible(true);

        }
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        String title = "";
        String val = "";
        switch (metric) {
            case CPA:
                TimeSeries cpa = new TimeSeries("CPA");
                model.getCPAPair().forEach(e -> {
                    if (!e.getValue().isInfinite()) {
                        cpa.addOrUpdate(new Hour(e.getKey()), e.getValue());
                    }
                });
                title = "Cost per Acquisition";
                val = "Cost /pence";
                dataset.addSeries(cpa);
                break;
            case CPC:
                TimeSeries clickCost = new TimeSeries("CPC");
                model.getClickCostPair().forEach(e -> {
                    if (!e.getValue().isInfinite()) {
                        clickCost.addOrUpdate(new Hour(e.getKey()), e.getValue());
                    }
                });
                title = "Cost per Click";
                val = "Cost /pence";
                dataset.addSeries(clickCost);
                break;
            case CPM:
                TimeSeries cpm = new TimeSeries("CPM");
                model.getCPMPair().forEach(e -> {
                    if (!e.getValue().isInfinite()) {
                        cpm.addOrUpdate(new Hour(e.getKey()), e.getValue());
                    }
                });
                title = "Cost per 1000 Impressions";
                val = "Cost /pence";
                dataset.addSeries(cpm);
                break;
            case CTR:
                TimeSeries ctr = new TimeSeries("CTR");
                model.getCTRPair().forEach(e -> {
                    if (!e.getValue().isInfinite()) {
                        ctr.addOrUpdate(new Hour(e.getKey()), e.getValue());
                    }
                });
                title = "Click-Through Rate";
                val = "Click-Through Rate";
                dataset.addSeries(ctr);
                break;
            case TOTAL_COST:
                TimeSeries totalCost = new TimeSeries("Total Cost");
                model.getTotalCostPair().forEach(e -> totalCost.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Total Cost";
                val = "Cost /pence";
                dataset.addSeries(totalCost);
                break;
            case BOUNCE_RATE:
                TimeSeries bounceRate = new TimeSeries("Bounce Rate");
                model.getBounceRatePair().forEach(e -> {
                    if (!e.getValue().isInfinite()) {
                        bounceRate.addOrUpdate(new Hour(e.getKey()), e.getValue());
                    }
                });
                title = "Bounce Rate";
                val = "Bounce Rate";
                dataset.addSeries(bounceRate);
                break;
            case NUM_OF_CLICKS:
                TimeSeries numOfClicks = new TimeSeries("Number Of Clicks");
                model.getNumOfClicksPair().forEach(e -> numOfClicks.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Number of Clicks";
                val = "Number of Clicks";
                dataset.addSeries(numOfClicks);
                break;
            case NUM_OF_BOUNCES:
                TimeSeries bp = new TimeSeries("Number Of Bounces");
                model.getNumOfBouncesPair().forEach(e -> bp.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Number of Bounces";
                val = "Bounces";
                dataset.addSeries(bp);
                break;
            case NUM_OF_CONVERSIONS:
                TimeSeries conversion = new TimeSeries("Number Of Conversions");
                model.getNumOfConversionsPair().forEach(e -> conversion.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Number of Conversions";
                val = "Conversions";
                dataset.addSeries(conversion);
                break;
            case NUM_OF_IMPRESSIONS:
                TimeSeries imp = new TimeSeries("Number Of Impressions");
                model.getNumOfImpressionsPair().forEach(e -> imp.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Number of Impressions";
                val = "Impressions";
                dataset.addSeries(imp);
                break;
            case NUM_OF_UNIQUE_CLICKS:
                TimeSeries uclick = new TimeSeries("Number Of Unique Clicks");
                model.getNumOfUniqueClicksPair().forEach(e -> uclick.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Number of Unique Clicks";
                val = "Unique Clicks";
                dataset.addSeries(uclick);
                break;
        }

        return getChartFor(title, "Date/Time", val, dataset);
    }

    @Override
    protected Task<JFreeChart> createTask() {
        return new Task<JFreeChart>() {
            @Override
            protected JFreeChart call() {
                return setChart();
            }
        };
    }

    @Override
    protected void succeeded() {
        controller.campaignChartViewer.setChart(getValue());
        controller.chartProgress.progressProperty().unbind();
        XYPlot plot = controller.campaignChartViewer.getChart().getXYPlot();

        if (controller.toggleThemeMode.isSelected()) {
            plot.setBackgroundPaint(new Color(47, 56, 80));
            plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
            plot.getDomainAxis().setLabelPaint(new Color(26, 172, 231));
            plot.getDomainAxis().setTickLabelPaint(new Color(26, 172, 231));
            plot.getRangeAxis().setLabelPaint(new Color(26, 172, 231));
            plot.getRangeAxis().setTickLabelPaint(new Color(26, 172, 231));
        } else {
            plot.setBackgroundPaint(Color.WHITE);
            plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        }

        plot.getDomainAxis().setAutoRange(true);
        plot.getRangeAxis().setAutoRange(true);
        plot.getRangeAxis().setAutoRange(true);
        plot.getRenderer().setSeriesStroke(0, new BasicStroke(4.0f));
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
