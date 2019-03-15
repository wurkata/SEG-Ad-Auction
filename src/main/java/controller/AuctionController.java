package controller;

import common.Granularity;
import javafx.application.Application;
import javafx.util.Pair;
import view.BaseFrame;
import common.FileType;
import model.*;
import view.FXApplication;

import javax.swing.*;
import java.io.File;
import java.util.*;

public class AuctionController extends Observable {
    private Model auctionModel;

    public AuctionController() {
        /*
        BaseFrame bs = new BaseFrame(this);
        new Thread(bs).start();
        */

        Application.launch(FXApplication.class);
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }

        new AuctionController();
    }

    /*
    public void setModel(File impLog, File clickLog, File serverLog) {
        auctionModel = new Model(this, impLog, clickLog, serverLog);
        new Thread(auctionModel).start();
    }
    */

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
        notifyObservers("metrics");
    }

    public void setBounceTime(long ms) {
        auctionModel.setBounceTime(ms);
        notifyObservers("metrics");
    }

    public void setBouncePageReq(int pages) {
        auctionModel.setBouncePageReq(pages);
        notifyObservers("metrics");
    }
}