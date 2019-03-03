package controller;

import common.FileType;
import model.*;

import java.io.File;

public class AuctionController {
    private Model auctionModel;

    public AuctionController() {
        auctionModel = new Model();
        auctionModel.connectToDatabase();
        loadFile("input/click_log.csv", FileType.CLICK_LOG);
        setCampaignTitle("Tester 1");
        uploadData(FileType.CLICK_LOG);
    }

    public static void main(String[] args) {
        // Load Model
        // Load UI
        // Manage Interaction
        new AuctionController();
    }

    public void loadFile(String filename, FileType fileType) {
        File inputFile = new File(filename);
        auctionModel.loadFile(inputFile, fileType);
    }

    public void uploadData(FileType fileType) {
        auctionModel.uploadData(fileType);
    }

    public void setCampaignTitle(String title) {
        auctionModel.setCampaignTitle(title);
    }
}