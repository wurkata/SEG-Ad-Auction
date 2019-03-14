package model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class ChartData {

    private ObjectProperty<ObservableList<XYChart.Series<String, Number>>> chartData = new SimpleObjectProperty<>();
    private ObservableList<XYChart.Series<String, Number>> chartDataList = FXCollections.observableArrayList();

    public ObjectProperty chartDataProperty() {
        return chartData;
    }

    public XYChart.Series getChartData() {
        return chartDataList.get(0);
    }

    public void setChartData(ObservableList<XYChart.Series<String, Number>> chartData) {
        this.chartData.set(chartData);
        this.chartDataList = chartData;
    }
}
