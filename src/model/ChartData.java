package model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class ChartData {

    private ObjectProperty<ObservableList<XYChart.Series<String, Number>>> chartData = new SimpleObjectProperty<>();

    public ObjectProperty chartDataProperty() {
        return chartData;
    }

    public void setChartData(ObservableList<XYChart.Series<String, Number>> chartData) {
        this.chartData.set(chartData);
    }
}
