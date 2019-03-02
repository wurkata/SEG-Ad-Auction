package model;

import common.FileType;

import javax.swing.plaf.nimbus.State;
import java.io.File;
import java.security.spec.ECField;
import java.sql.Statement;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by furqan on 27/02/2019.
 */
public class Model {
    private List<ImpressionLog> impressionLog;
    private List<ClickLog> clickLog;
    private List<ServerLog> serverLog;
    private boolean impressionCost = true;

    private Connection con;

    public Model() {
    }

    public Model(File impressionLog, File clickLog, File serverLog) {
        loadFile(impressionLog, FileType.IMPRESSION_LOG);
        loadFile(clickLog, FileType.CLICK_LOG);
        loadFile(serverLog, FileType.SERVER_LOG);
    }

    public boolean loadFile(File inputFile, FileType fileType) {
        try {
            switch (fileType) {
                case IMPRESSION_LOG:
                    this.impressionLog = Parser.readImpressionLog(inputFile);
                    return true;
                case CLICK_LOG:
                    this.clickLog = Parser.readClickLog(inputFile);
                    return true;
                case SERVER_LOG:
                    this.serverLog = Parser.readServerLog(inputFile);
                    return true;
                default:
                    System.out.println("Wrong file type!");
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void connectToDatabase() {
        try {
            this.con = getConnector();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnector() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            Connection con = DriverManager.getConnection("jdbc:mysql://35.246.109.185:3306/ad_auction", "root", "seg-ad-auction");
            return con;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void uploadData(FileType type) {
        try {
            Connection uploadCon = getConnector();
            Statement stmt = uploadCon.createStatement();
            String query;

            switch (type) {
                case IMPRESSION_LOG:
                    for (ImpressionLog il : this.impressionLog) {
                        query = "INSERT INTO impression_log (Date, UserID, Gender, AgeID, Income, Context, ImpressionCost) VALUES(\"" +
                                il.getImpressionDate() + "\", \"" +
                                il.getSubjectID() + "\",\"" +
                                il.getGender() + "\"," +
                                il.getAgeRange() + ",\"" +
                                il.getIncome() + "\",\"" +
                                il.getContext() + "\"," +
                                il.getImpressionCost() + ");";

                        stmt.executeUpdate(query);
                    }
                    break;
                case CLICK_LOG:
                    for (ClickLog cl : this.clickLog) {
                        query = "INSERT INTO click_log (Date, UserID, ClickCost) VALUES(\"" +
                                cl.getClickDate() + "\", \"" +
                                cl.getSubjectID() + "\"," +
                                cl.getClickCost() + ");";

                        stmt.executeUpdate(query);
                    }
                    break;
                case SERVER_LOG:
                    for (ServerLog sl : this.serverLog) {
                        query = "INSERT INTO server_log (EntryDate, UserID, ExitDate, NoPagesViewed, isConversion) VALUES(\"" +
                                sl.getEntryDate() + "\", \"" +
                                sl.getSubjectID() + "\",\"" +
                                sl.getExitDate() + "\"," +
                                sl.getPagesViewed() + "," +
                                sl.getConversion() + ");";

                        stmt.executeUpdate(query);
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //True sets class to calculate costs based om impression costs, whereas false uses click costs
    public void setCostMode(boolean impressionCostMode){
        this.impressionCost=impressionCostMode;
    }

    //calculates cost based on either impression cost or click cost depending on value of impressionCost field
    private double calculateCost(){
        if(impressionCost) {
            return impressionLog.parallelStream()
                    .mapToDouble(ImpressionLog::getImpressionCost)
                    .sum();
        }else{
            return clickLog.parallelStream()
                    .mapToDouble(ClickLog::getClickCost)
                    .sum();
        }
    }

    //Cost per click
    public double getClickCost(){
        long numOfClicks = clickLog.size();

        return (calculateCost()/numOfClicks);
    }

    //Cost per 1000 impressionLog
    public double getCPM() {
        long numOfRecords = impressionLog.size();

        return (calculateCost()/numOfRecords)*1000.0;
    }

    //Click through rate
    public double getCTR() {
        long numOfImpressions = impressionLog.size();

        long numOfClicks = clickLog.size();

        return ((double) numOfClicks / numOfImpressions);
    }

    //Cost per acquisition
    public double getCPA(){
        long numOfAcquisitions = serverLog.stream()
                .filter(s -> s.getConversion() > 0)
                .count();

        return (calculateCost()/numOfAcquisitions);
    }

    //Number of impressions
    public long getNumOfImpressions(){
        return impressionLog.size();
    }

    //Number of clickLog
    public double getNumOfClicks() {

        return clickLog.size();
    }

    //Number of unique clickLog
    public double getNumOfUniqueClicks() {

        return clickLog.stream()
                .map(ClickLog::getSubjectID)
                .distinct()
                .count();
    }

}
