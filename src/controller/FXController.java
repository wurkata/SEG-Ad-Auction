package controller;

import common.FileType;
import common.Granularity;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.util.Pair;
import javafx.util.StringConverter;
import model.Model;

import java.io.File;
import java.net.URL;
import java.util.*;

public class FXController implements Initializable, Observer {
    public Label noImpressions;
    public Label noClicks;
    public Label noBounces;
    public Label noUqClicks;
    public Label noConversions;
    public Label totalCost;
    public Label ctr;
    public Label cpc;
    public Label cti;
    public Label cpa;
    public Label bounceRate;
    public Slider chartGranularitySlider;

    private List<Label> metricsList;
    private Model auctionModel;

    public FXController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setChartGranularitySliderLabels();
        addMetricsToList();
    }

    private void setChartGranularitySliderLabels() {
        chartGranularitySlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                switch (n.intValue()) {
                    case 1:
                        return "Hour";
                    case 2:
                        return "Day";
                    case 3:
                        return "Month";
                    default:
                        return "Year";
                }
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "Hour":
                        return 1d;
                    case "Day":
                        return 2d;
                    case "Month":
                        return 3d;
                    default:
                        return 4d;
                }
            }
        });
    }

    private void setMetrics() {

    }

    public void setModel(File impLog, File clickLog, File serverLog) {
        auctionModel = new Model(this, impLog, clickLog, serverLog);
        auctionModel.addObserver(this);
        new Thread(auctionModel).start();
    }

    public void uploadData(FileType fileType) {
        auctionModel.uploadData(fileType);
    }

    public void setCampaignTitle(String title) {
        auctionModel.setCampaignTitle(title);
    }

    public void setCostMode(boolean impressionCostMode) {
        auctionModel.setCostMode(impressionCostMode);
    }

    public double getTotalCost() {
        return auctionModel.getTotalCost();
    }

    public List<Pair<Date, Double>> getTotalCostPair() {
        return auctionModel.getTotalCostPair();
    }

    public double getClickCost() {
        return auctionModel.getClickCost();
    }

    public List<Pair<Date, Double>> getClickCostPair() {
        return auctionModel.getClickCostPair();
    }

    public double getCPM() {
        return auctionModel.getCPM();
    }

    public List<Pair<Date, Double>> getCPMPair() {
        return auctionModel.getCPMPair();
    }

    public double getCTR() {
        return auctionModel.getCTR();
    }

    public List<Pair<Date, Double>> getCTRPair() {
        return auctionModel.getCTRPair();
    }

    public double getCPA() {
        return auctionModel.getCPA();
    }

    public List<Pair<Date, Double>> getCPAPair() {
        return auctionModel.getCPAPair();
    }

    public long getNumOfImpressions() {
        return auctionModel.getNumOfImpressions();
    }

    public List<Pair<Date, Long>> getNumOfImpressionsPair() {
        return auctionModel.getNumOfImpressionsPair();
    }

    public long getNumOfClicks() {
        return auctionModel.getNumOfClicks();
    }

    public List<Pair<Date, Long>> getNumOfClicksPair() {
        return auctionModel.getNumOfClicksPair();
    }

    public long getNumOfUniqueClicks() {
        return auctionModel.getNumOfUniqueClicks();
    }

    public List<Pair<Date, Long>> getNumOfUniqueClicksPair() {
        return auctionModel.getNumOfUniqueClicksPair();
    }

    public long getNumOfBounces() {
        return auctionModel.getNumOfBounces();
    }

    public List<Pair<Date, Long>> getNumOfBouncesPair() {
        return auctionModel.getNumOfBouncesPair();
    }

    public double getBounceRate() {
        return auctionModel.getBounceRate();
    }

    public List<Pair<Date, Double>> getBounceRatePair() {
        return auctionModel.getBounceRatePair();
    }

    public long getNumOfConversions() {
        return auctionModel.getNumOfConversions();
    }

    public List<Pair<Date, Long>> getNumOfConversionsPair() {
        return auctionModel.getNumOfConversionsPair();
    }

    public void setGranularity(Granularity g) {
        auctionModel.setGranularity(g);
    }

    public void setBounceTime(long ms) {
        auctionModel.setBounceTime(ms);
    }

    public void setBouncePageReq(int pages) {
        auctionModel.setBouncePageReq(pages);
    }

    private void addMetricsToList() {
        metricsList = new ArrayList<>();
        Collections.addAll(metricsList,
                noImpressions,
                noClicks,
                noBounces,
                noUqClicks,
                noConversions,
                totalCost,
                ctr,
                cpc,
                cti,
                cpa,
                bounceRate);
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
