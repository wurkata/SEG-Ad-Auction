package view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import java.awt.*;

public class ChartDisplay {
    private JFreeChart chart;
    private XYSeries series;

    public ChartDisplay() {
        series = getSeries("Testing");
        XYSeries wtfSeries = new XYSeries("WTF");
        wtfSeries.add(0, 1);
        wtfSeries.add(2, 1.5);
        wtfSeries.add(3,4);
        wtfSeries.add(5,6);

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        dataset.addSeries(wtfSeries);

        chart = ChartFactory.createXYLineChart(
                "Test Chart",
                "X-axis",
                "y-axis",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        plot.getRenderer().setSeriesStroke(0,new BasicStroke(4.0f));
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
