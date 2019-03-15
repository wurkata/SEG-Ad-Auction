package view;


import common.Metric;
import controller.AuctionController;
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
import java.util.Observable;

public class ChartDisplay extends Observable implements Runnable {
    private AuctionController controller;

    private Metric chartMetric;
    private JFreeChart chart;

    public ChartDisplay(AuctionController controller) {
        this.controller = controller;
    }

    public void getChart(Metric metric) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        String title = "";
        String val = "";
        switch (metric) {
            case CPA:
                TimeSeries cpa = new TimeSeries("CPA");
                controller.getCPAPair().forEach(e -> cpa.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Cost per Acquisition";
                val = "Cost /pence";
                dataset.addSeries(cpa);
                break;
            case CPC:
                TimeSeries clickCost = new TimeSeries("CPC");
                controller.getClickCostPair().forEach(e -> clickCost.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Cost per Click";
                val = "Cost /pence";
                dataset.addSeries(clickCost);
                break;
            case CPM:
                TimeSeries cpm = new TimeSeries("CPM");
                controller.getCPMPair().forEach(e -> cpm.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Cost per 1000 Impressions";
                val = "Cost /pence";
                dataset.addSeries(cpm);
                break;
            case CTR:
                TimeSeries ctr = new TimeSeries("CTR");
                controller.getCTRPair().forEach(e -> ctr.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Click-Through Rate";
                val = "Click-Through Rate";
                dataset.addSeries(ctr);
                break;
            case TOTAL_COST:
                TimeSeries totalCost = new TimeSeries("Total Cost");
                controller.getTotalCostPair().forEach(e -> totalCost.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Total Cost";
                val = "Cost /pence";
                dataset.addSeries(totalCost);
                break;
            case BOUNCE_RATE:
                TimeSeries bounceRate = new TimeSeries("Bounce Rate");
                controller.getBounceRatePair().forEach(e -> bounceRate.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Bounce Rate";
                val = "Bounce Rate";
                dataset.addSeries(bounceRate);
                break;
            case NUM_OF_CLICKS:
                TimeSeries numOfClicks = new TimeSeries("Number Of Clicks");
                controller.getNumOfClicksPair().forEach(e -> numOfClicks.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Number of Clicks";
                val = "Number of Clicks";
                dataset.addSeries(numOfClicks);
                break;
            case NUM_OF_BOUNCES:
                TimeSeries bp = new TimeSeries("Number Of Bounces");
                controller.getNumOfBouncesPair().forEach(e -> bp.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Number of Bounces";
                val = "Bounces";
                dataset.addSeries(bp);
                break;
            case NUM_OF_CONVERSIONS:
                TimeSeries conversion = new TimeSeries("Number Of Conversions");
                controller.getNumOfConversionsPair().forEach(e -> conversion.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Number of Conversions";
                val = "Conversions";
                dataset.addSeries(conversion);
                break;
            case NUM_OF_IMPRESSIONS:
                TimeSeries imp = new TimeSeries("Number Of Impressions");
                controller.getNumOfImpressionsPair().forEach(e -> imp.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                title = "Number of Impressions";
                val = "Impressions";
                dataset.addSeries(imp);
                break;
            case NUM_OF_UNIQUE_CLICKS:
                TimeSeries uclick = new TimeSeries("Number Of Unique Clicks");
                controller.getNumOfUniqueClicksPair().forEach(e -> uclick.addOrUpdate(new Hour(e.getKey()), e.getValue()));
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

        notifyObservers("chart");
    }

    @Override
    public void run() {
        getChart(chartMetric);
    }
}
