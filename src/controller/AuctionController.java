package controller;

import model.*;

import java.io.File;
import java.util.List;

public class AuctionController {
    private Model auctionModel;

    public AuctionController() {
        auctionModel = new Model();
    }

    public AuctionController(File... inputFiles) {
        auctionModel = new Model();

        for(int i = 0; i < 3; i++) {
            auctionModel.loadFile(inputFiles[i]);
        }
    }

    public static void main(String[] args) {
        // Load Model
        // Load UI
        // Manage Interaction

        try {
            File clickLogFile = new File("input/click_log.csv");
            Parser parser = new Parser();
            List<ClickLog> clickLogList = parser.readClickLog(clickLogFile);

            for (ClickLog cl : clickLogList) {
                System.out.println(cl.getSubjectID());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}