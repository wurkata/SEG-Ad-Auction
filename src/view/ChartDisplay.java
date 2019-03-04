package view;

import javafx.util.Pair;
import model.Model;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChartDisplay {
    private JFreeChart chart;
    private XYSeries series;

    public ChartDisplay() {
        Model m = new Model(new File("files/impression_log.csv"), new File("files/click_log.csv"), new File("files/server_log.csv"));
        TimeSeries totalCost = new TimeSeries("TotalCost");
        m.getTotalCostPair().stream().forEach(e->totalCost.addOrUpdate(new Hour(e.getKey()), e.getValue()));

        TimeSeries bounceRate = new TimeSeries("BounceRate");
        m.getBounceRatePair().stream().forEach(e->bounceRate.addOrUpdate(new Hour(e.getKey()), e.getValue()));

        TimeSeries clickCost = new TimeSeries("ClickCost");
        m.getClickCostPair().stream().forEach(e->clickCost.addOrUpdate(new Hour(e.getKey()), e.getValue()));

        TimeSeries ctr = new TimeSeries("CTR");
        m.getCTRPair().stream().forEach(e->ctr.addOrUpdate(new Hour(e.getKey()), e.getValue()));

        TimeSeries bp = new TimeSeries("NumOfBounces");
        m.getNumOfBouncesPair().stream().forEach(e->bp.addOrUpdate(new Hour(e.getKey()), e.getValue()));

        TimeSeries cpm = new TimeSeries("CPM");
        m.getCPMPair().stream().forEach(e->cpm.addOrUpdate(new Hour(e.getKey()), e.getValue()));

        TimeSeries cpa = new TimeSeries("CPA");
        m.getCPAPair().stream().forEach(e->cpa.addOrUpdate(new Hour(e.getKey()), e.getValue()));

        TimeSeries numOfClicks = new TimeSeries("NumOfClicks");
        m.getNumOfClicksPair().stream().forEach(e->numOfClicks.addOrUpdate(new Hour(e.getKey()), e.getValue()));

        TimeSeries conversion = new TimeSeries("NumOfConversions");
        m.getNumOfConversionsPair().stream().forEach(e->conversion.addOrUpdate(new Hour(e.getKey()), e.getValue()));

        TimeSeries imp = new TimeSeries("NumOfImpressions");
        m.getNumOfImpressionsPair().stream().forEach(e->imp.addOrUpdate(new Hour(e.getKey()), e.getValue()));

        TimeSeries uclick = new TimeSeries("NumOfUniqueClicks");
        m.getNumOfUniqueClicksPair().stream().forEach(e->uclick.addOrUpdate(new Hour(e.getKey()), e.getValue()));

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(totalCost);
        dataset.addSeries(bounceRate);
        dataset.addSeries(clickCost);
        dataset.addSeries(ctr);
        dataset.addSeries(bp);
        dataset.addSeries(cpm);
        dataset.addSeries(cpa);
        dataset.addSeries(numOfClicks);
        dataset.addSeries(conversion);
        //dataset.addSeries(imp);
        dataset.addSeries(uclick);

        DateAxis x = new DateAxis("Date");
        x.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd HHHH"));
        XYPlot plot = new XYPlot(dataset, x, new NumberAxis("Value"),new XYLineAndShapeRenderer());
        plot.getRenderer().setSeriesStroke(0,new BasicStroke(4.0f));
        chart = new JFreeChart("Test", plot);
    }

    public JFreeChart getChart() {
        return chart;
    }

    private XYSeries getSeries(String title) {
        XYSeries xySeries = new XYSeries(title);
        xySeries.add(0.5, 2.3);
        xySeries.add(1, 3);
        xySeries.add(2, 4);
        return xySeries;
    }
}
