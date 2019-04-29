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
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.data.RangeType;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.List;

public class GraphController extends Service<JFreeChart> implements Observable {
    private List<Model> models;
    private CampaignController controller;

    private Metric metric;

    GraphController(CampaignController controller, List<Model> models) {
        this.controller = controller;
        this.models = models;
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

    private JFreeChart getChartFor(String title, String xLabel, String yLabel, TimeSeriesCollection dataset, boolean doub) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title,
                xLabel,
                yLabel,
                dataset,
                true,
                true,
                false
        );
        XYLineAndShapeRenderer renderer = new PercentileRenderer(dataset, 90, doub);

        chart.getXYPlot().setRenderer(renderer);
        DateAxis dateAxis = (DateAxis)chart.getXYPlot().getDomainAxis();

        NumberAxis numAxis = (NumberAxis) chart.getXYPlot().getRangeAxis();

        numAxis.setRangeType(RangeType.POSITIVE);
        numAxis.setAutoRangeIncludesZero(true);
        numAxis.configure();

        for (Model model: models) {


        switch(model.getGranularity()) {
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

    //Updated for multiple
    private JFreeChart setChart() {
            setMetric();
            if(metric != null) {
                controller.campaignChartViewer.setVisible(false);
                controller.chartProgress.setVisible(true);

            }
            TimeSeriesCollection dataset = new TimeSeriesCollection();
            String title = "";
            String val = "";
            boolean doub = false;

            switch (metric) {

                case CPA:
                    title = "Cost per Acquisition";
                    val = "Cost /pence";
                    doub = true;
                    for (Model model : models) {

                        TimeSeries cpa = new TimeSeries(model.getTitle());
                        model.getCPAPair().forEach(e -> {
                            if (!e.getValue().isInfinite()) {
                                cpa.addOrUpdate(new Hour(e.getKey()), e.getValue());
                            }
                        });

                        dataset.addSeries(cpa);
                    }
                    break;

                case CPC:
                    title = "Cost per Click";
                    val = "Cost /pence";
                    doub = true;
                    for (Model model : models) {

                        TimeSeries clickCost = new TimeSeries(model.getTitle());
                        model.getClickCostPair().forEach(e -> {
                            if (!e.getValue().isInfinite()) {
                                clickCost.addOrUpdate(new Hour(e.getKey()), e.getValue());
                            }
                        });

                        dataset.addSeries(clickCost);
                    }
                    break;

                case CPM:
                    title = "Cost per 1000 Impressions";
                    val = "Cost /pence";
                    doub = true;
                    for (Model model : models) {

                        TimeSeries cpm = new TimeSeries(model.getTitle());
                        model.getCPMPair().forEach(e -> {
                            if (!e.getValue().isInfinite()) {
                                cpm.addOrUpdate(new Hour(e.getKey()), e.getValue());
                            }
                        });
                        dataset.addSeries(cpm);
                    }
                    break;

                case CTR:
                    title = "Click-Through Rate";
                    val = "Click-Through Rate";
                    doub = true;
                    for (Model model : models) {

                        TimeSeries ctr = new TimeSeries(model.getTitle());
                        model.getCTRPair().forEach(e -> {
                            if (!e.getValue().isInfinite()) {
                                ctr.addOrUpdate(new Hour(e.getKey()), e.getValue());
                            }
                        });
                        dataset.addSeries(ctr);
                    }
                    break;

                case TOTAL_COST:
                    title = "Total Cost";
                    val = "Cost /pence";
                    doub = true;
                    for (Model model : models) {

                        TimeSeries totalCost = new TimeSeries(model.getTitle());
                        model.getTotalCostPair().forEach(e -> totalCost.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                        dataset.addSeries(totalCost);
                    }
                    break;

                case BOUNCE_RATE:
                    title = "Bounce Rate";
                    val = "Bounce Rate";
                    doub = true;
                    for (Model model : models) {

                        TimeSeries bounceRate = new TimeSeries(model.getTitle());
                        model.getBounceRatePair().forEach(e -> {
                            if (!e.getValue().isInfinite()) {
                                bounceRate.addOrUpdate(new Hour(e.getKey()), e.getValue());
                            }
                        });

                        dataset.addSeries(bounceRate);
                    }
                    break;

                case NUM_OF_CLICKS:
                    title = "Number of Clicks";
                    val = "Number of Clicks";
                    for (Model model: models) {

                        TimeSeries numOfClicks = new TimeSeries(model.getTitle());
                        model.getNumOfClicksPair().forEach(e -> numOfClicks.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                        dataset.addSeries(numOfClicks);
                    }
                    break;

                case NUM_OF_BOUNCES:
                    title = "Number of Bounces";
                    val = "Bounces";
                    for (Model model: models) {

                        TimeSeries bp = new TimeSeries(model.getTitle());
                        model.getNumOfBouncesPair().forEach(e -> bp.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                        dataset.addSeries(bp);
                    }
                    break;

                case NUM_OF_CONVERSIONS:
                    title = "Number of Conversions";
                    val = "Conversions";
                    for (Model model: models) {

                        TimeSeries conversion = new TimeSeries(model.getTitle());
                        model.getNumOfConversionsPair().forEach(e -> conversion.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                        dataset.addSeries(conversion);
                    }
                    break;

                case NUM_OF_IMPRESSIONS:
                    title = "Number of Impressions";
                    val = "Impressions";
                    for (Model model: models) {

                        TimeSeries imp = new TimeSeries(model.getTitle());
                        model.getNumOfImpressionsPair().forEach(e -> imp.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                        dataset.addSeries(imp);
                    }
                    break;

                case NUM_OF_UNIQUE_CLICKS:
                    title = "Number of Unique Clicks";
                    val = "Unique Clicks";
                    for (Model model: models) {

                        TimeSeries uclick = new TimeSeries(model.getTitle());
                        model.getNumOfUniqueClicksPair().forEach(e -> uclick.addOrUpdate(new Hour(e.getKey()), e.getValue()));
                        dataset.addSeries(uclick);
                    }
                    break;
            }

        return getChartFor(title, "Date/Time", val, dataset, doub);
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
