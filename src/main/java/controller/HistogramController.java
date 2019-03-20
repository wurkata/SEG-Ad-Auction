package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;
import model.Model;

import java.net.URL;
import java.util.*;

public class HistogramController implements Initializable {

    @FXML
    private BarChart<?, ?> ClickCostHistogram;

    @FXML
    private CategoryAxis x;

    @FXML
    private NumberAxis y;

    private Model model;

    private List<Double> clickCosts;
    private Double classIncrement;
    private int classesNumber;

    HistogramController(Model model) {
        this.model = model;

        this.clickCosts = new ArrayList<Double>();
        clickCosts = model.getIndivClickCost();
        this.classIncrement = 2.5;
        this.classesNumber = (int) Math.ceil((Collections.max(clickCosts))/classIncrement);


    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {

        ArrayList<Integer> clickCostFrequency = new ArrayList<Integer>(Collections.nCopies(classesNumber+1, 0));

        for ( Double clickCost : model.getIndivClickCost()) {
            int classNumber = (int) Math.ceil(clickCost / classIncrement);
            clickCostFrequency.set(classNumber, clickCostFrequency.get(classNumber) + 1);
        }

        ArrayList<Double> clickCostFrequencyDensity = new ArrayList<Double>();
        for ( int freq : clickCostFrequency ) {
            clickCostFrequencyDensity.add(freq / classIncrement);

        }

        XYChart.Series histogramSet = new XYChart.Series();

        for (int i = 0; i < clickCostFrequencyDensity.size(); i++) {
            if (i == 0 )
                histogramSet.getData().add(new XYChart.Data("0 - " + classIncrement, clickCostFrequencyDensity.get(0)));

            else {
                histogramSet.getData().add(new XYChart.Data(classIncrement*i + " - " + classIncrement*(i+1), clickCostFrequencyDensity.get(i)));
            }
        }

        ClickCostHistogram.getData().addAll(histogramSet);

    }

}
