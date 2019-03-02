package controller;

import common.FileType;
import model.*;

import java.io.File;
import java.util.List;

public class AuctionController {
    private Model auctionModel;

    public AuctionController() {
        auctionModel = new Model();
        auctionModel.connectToDatabase();

        File clickLogFile = new File("input/click_log.csv");
        auctionModel.loadFile(clickLogFile, FileType.CLICK_LOG);

        auctionModel.uploadData(FileType.CLICK_LOG);
    }

    public static void main(String[] args) {
        // Load Model
        // Load UI
        // Manage Interaction
        new AuctionController();

        /*
        try {
            File clickLogFile = new File("input/click_log.csv");
            Parser parser = new Parser();
            List<ClickLog> clickLogList = parser.readClickLog(clickLogFile);

            for (ClickLog cl : clickLogList) {
                System.out.println(cl.getSubjectID());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}