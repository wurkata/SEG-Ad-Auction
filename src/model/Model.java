package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by furqan on 27/02/2019.
 */
public class Model {
    private List<ImpressionLog> impressions;
    private List<ClickLog> clicks;
    private List<ServerLog> server;
    private boolean impressionCost = true;

    public Model() {
    }

    public Model(File impressions, File clicks, File server){
        try {
            this.impressions=Parser.readImpressionLog(impressions);
            this.clicks=Parser.readClickLog(clicks);
            this.server=Parser.readServerLog(server);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error reading input files");
        }
    }

    public void loadFile(File inputFile) {

    }

    //True sets class to calculate costs based om impression costs, whereas false uses click costs
    public void setCostMode(boolean impressionCostMode){
        this.impressionCost=impressionCostMode;
    }

    //calculates cost based on either impression cost or click cost depending on value of impressionCost field
    private double calculateCost(){
        if(impressionCost) {
            return impressions.parallelStream()
                    .mapToDouble(ImpressionLog::getImpressionCost)
                    .sum();
        }else{
            return clicks.parallelStream()
                    .mapToDouble(ClickLog::getClickCost)
                    .sum();
        }
    }

    //Cost per click
    public double getClickCost(){
        long numOfClicks = clicks.size();

        return (calculateCost()/numOfClicks);
    }

    //Cost per 1000 impressions
    public double getCPM(){
        long numOfRecords = impressions.size();

        return (calculateCost()/numOfRecords)*1000.0;
    }

    //Click through rate
    public double getCTR(){
        long numOfImpressions = impressions.size();

        long numOfClicks = clicks.size();

        return ((double) numOfClicks/numOfImpressions);
    }

    //Cost per acquisition
    public double getCPA(){
        long numOfAcquisitions = server.stream()
                .filter(ServerLog::getConversion)
                .count();

        return (calculateCost()/numOfAcquisitions);
    }

    //Number of impressions
    public long getNumOfImpressions(){
        return impressions.size();
    }

    //Number of clicks
    public double getNumOfClicks(){

        return clicks.size();
    }

    //Number of unique clicks
    public double getNumOfUniqueClicks(){

        return clicks.stream()
                .mapToLong(ClickLog::getSubjectID)
                .distinct()
                .count();
    }

}
