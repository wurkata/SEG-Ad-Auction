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

    @Override
    public void initialize(URL url, ResourceBundle resources) {

        double classIncrement = 2.5;
        ArrayList<Double> clickCosts = new ArrayList<Double>();

        int classesNumber = (int) ((((Collections.max(clickCosts)) / classIncrement) + (classIncrement - (classIncrement/5))) / classIncrement * classIncrement);
        ArrayList<Integer> clickCostFrequency = new ArrayList<Integer>(Collections.nCopies(classesNumber, 0));

        Model model = new Model();
        List<Pair<Date, Double>> clickCostPairs = new ArrayList<Pair<Date, Double>>();
        clickCostPairs = model.getClickCostPair();
        for ( Pair<Date, Double> pair : clickCostPairs ) {
            clickCosts.add(pair.getValue());

        }


        for ( Double clickCost : clickCosts ) {
            int classNumber = (int) Math.ceil(clickCost / classIncrement);
            clickCostFrequency.set(classNumber-1, clickCostFrequency.get(classesNumber-1)+1);

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
                histogramSet.getData().add(new XYChart.Data(classIncrement*i + " - " + classIncrement*2*i, clickCostFrequencyDensity.get(i)));

            }
        }

        ClickCostHistogram.getData().addAll(histogramSet);

    }

}
