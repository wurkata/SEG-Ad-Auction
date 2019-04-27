package controller;

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.Series;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by furqan on 24/04/2019.
 */
public class PercentileRenderer extends XYLineAndShapeRenderer {

    private double[] lowerDubBounds;
    private long[] lowerLongBounds;
    private boolean doub;

    //percentile = 90 means top 10th percentile will be highlighted
    public PercentileRenderer(TimeSeriesCollection d, int percentile, boolean doub) {
        this.doub = doub;
        List<TimeSeries> list = d.getSeries();
        Random rng = new Random();
        if (doub) {
            lowerDubBounds = new double[list.size()];
            int i = 0;
            for (TimeSeries s : list) {
//                this.setSeriesPaint(i, new Color(rng.nextInt(255), rng.nextInt(255), rng.nextInt(255)));

                List<TimeSeriesDataItem> items = s.getItems();
                ArrayList<Double> sortedValues = new ArrayList<>();
                items.stream()
                        .mapToDouble(e -> (double) e.getValue())
                        .forEach(e -> sortedValues.add(e));

                sortedValues.sort(Double::compareTo);
                Double k = sortedValues.size() * (double) percentile / 100.0;
                int index;
                if (k - k.intValue() != 0) {
                    index = (int) Math.ceil(k);
                    lowerDubBounds[i] = sortedValues.get(index);
                } else {
                    index = k.intValue();
                    lowerDubBounds[i] = (sortedValues.get(index) + sortedValues.get(index + 1)) / 2;
                }
                i++;
            }
        } else {
            lowerLongBounds = new long[list.size()];
            int i = 0;
            for (TimeSeries s : list) {
//                this.setSeriesPaint(i, new Color(rng.nextInt(255), rng.nextInt(255), rng.nextInt(255)));
                List<TimeSeriesDataItem> items = s.getItems();
                ArrayList<Long> sortedValues = new ArrayList<>();
                items.stream()
                        .mapToLong(e -> (long) e.getValue())
                        .forEach(e -> sortedValues.add(e));

                sortedValues.sort(Long::compareTo);
                Double k = sortedValues.size() * (double) percentile / 100.0;
                int index;
                if (k - k.intValue() != 0) {
                    index = (int) Math.ceil(k);
                    lowerLongBounds[i] = sortedValues.get(index);
                } else {
                    index = k.intValue();
                    lowerLongBounds[i] = (sortedValues.get(index) + sortedValues.get(index + 1)) / 2;
                }
                i++;
            }

        }
    }

    @Override
    public Paint getItemPaint(int row, int column){
            if(doub){
                double value = (double) this.getPlot().getDataset().getY(row, column);

                if(value<lowerDubBounds[row]){
                    return getSeriesPaint(row);
                }else{
                    Color c = (Color) getSeriesPaint(row);
                    return new Color(255-c.getRed(), 255-c.getGreen(), 255-c.getBlue());

                }
            }else{
                long value = (long) this.getPlot().getDataset().getY(row, column);

                if(value<lowerLongBounds[row]){
                    return getSeriesPaint(row);
                }else{
                    Color c = (Color) getSeriesPaint(row);
                    return new Color(255-c.getRed(), 255-c.getGreen(), 255-c.getBlue());
//                    return Color.blue;
                }
            }


    }
}
