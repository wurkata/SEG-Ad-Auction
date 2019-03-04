package model;

import common.FileType;
import common.Granularity;
import javafx.util.Pair;

import java.io.File;
import java.sql.Statement;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.stream.Stream;

/**
 * Created by furqan on 27/02/2019.
 */
public class Model {
    private List<ImpressionLog> impressionLog;
    private List<ClickLog> clickLog;
    private List<ServerLog> serverLog;
    private HashMap<String, SubjectLog> subjects;
    private boolean impressionCost = true;
    private String campaignTitle;

    private Connection con;
    private long bounceTime=-1;

    private Granularity granularity = Granularity.DAY;

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
                    Pair<ArrayList<ImpressionLog>, HashMap<String, SubjectLog>> p = Parser.readImpressionLog(inputFile);
                    impressionLog = p.getKey();
                    subjects = p.getValue();
                    //impressionLog.sort(Comparator.comparing(ImpressionLog::getImpressionDate));
                    return true;
                case CLICK_LOG:
                    this.clickLog = Parser.readClickLog(inputFile);
                    //clickLog.sort(Comparator.comparing(ClickLog::getClickDate));
                    return true;
                case SERVER_LOG:
                    this.serverLog = Parser.readServerLog(inputFile);
                    //serverLog.sort(Comparator.comparing(ServerLog::getEntryDate));
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

            // Insert table name
            query = "INSERT INTO campaigns (CampaignTitle) " +
                    "SELECT * FROM (SELECT \"" + this.campaignTitle + "\") AS tmp " +
                    "WHERE NOT EXISTS (SELECT CampaignTitle FROM campaigns WHERE CampaignTitle=\"" + this.campaignTitle + "\") LIMIT 1;";
            stmt.executeUpdate(query);

            switch (type) {
                case IMPRESSION_LOG:
                    for (ImpressionLog il : this.impressionLog) {
                        query = "INSERT INTO impression_log (CampaignID, Date, UserID, Gender, AgeID, Income, Context, ImpressionCost) VALUES(" +
                                "(SELECT ID FROM campaigns WHERE CampaignTitle=\"" + this.campaignTitle + "\"), \"" +
                                il.getImpressionDate() + "\", \"" +
                                il.getSubjectID() + "\",\"" +
                                subjects.get(il.getSubjectID()).getGender() + "\"," +
                                subjects.get(il.getSubjectID()).getAgeRange() + ",\"" +
                                subjects.get(il.getSubjectID()).getIncome() + "\",\"" +
                                il.getContext() + "\"," +
                                il.getImpressionCost() + ");";

                        stmt.executeUpdate(query);
                    }
                    break;
                case CLICK_LOG:
                    for (ClickLog cl : this.clickLog) {
                        query = "INSERT INTO click_log (CampaignID, Date, UserID, ClickCost) VALUES(" +
                                "(SELECT ID FROM campaigns WHERE CampaignTitle=\"" + this.campaignTitle + "\"), \"" +
                                cl.getClickDate() + "\", \"" +
                                cl.getSubjectID() + "\"," +
                                cl.getClickCost() + ");";

                        stmt.executeUpdate(query);
                    }
                    break;
                case SERVER_LOG:
                    for (ServerLog sl : this.serverLog) {
                        query = "INSERT INTO server_log (CampaignID, EntryDate, UserID, ExitDate, NoPagesViewed, isConversion) VALUES(" +
                                "(SELECT ID FROM campaigns WHERE CampaignTitle=\"" + this.campaignTitle + "\"), \"" +
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

    public void setCampaignTitle(String title) {
        this.campaignTitle = title;
    }

    //True sets class to calculate costs based om impression costs, whereas false uses click costs
    public void setCostMode(boolean impressionCostMode) {
        this.impressionCost = impressionCostMode;
    }

    //calculates cost based on either impression cost or click cost depending on value of impressionCost field
    private double calculateCost() {
        if (impressionCost) {
            return impressionLog.parallelStream()
                    .mapToDouble(ImpressionLog::getImpressionCost)
                    .sum();
        } else {
            return clickLog.parallelStream()
                    .mapToDouble(ClickLog::getClickCost)
                    .sum();
        }
    }

    //Cost per click
    public double getClickCost() {
        long numOfClicks = clickLog.size();

        return (calculateCost() / numOfClicks);
    }

    //Cost per 1000 impressionLog
    public double getCPM() {
        long numOfRecords = impressionLog.size();

        return (calculateCost() / numOfRecords) * 1000.0;
    }

    //Click through rate
    public double getCTR() {
        long numOfImpressions = impressionLog.size();

        long numOfClicks = clickLog.size();

        return ((double) numOfClicks / numOfImpressions);
    }

    //Cost per acquisition
    public double getCPA() {
        long numOfAcquisitions = serverLog.stream()
                .filter(s -> s.getConversion() > 0)
                .count();

        return (calculateCost() / numOfAcquisitions);
    }

    //Number of impressions
    public long getNumOfImpressions() {
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

    // Get number of bounces
    public long getNumOfBounces() {
        // if no bounce time is use conversion
        if (bounceTime < 0) {
            return serverLog.size() - getNumOfConversions();
        } else {
            return serverLog.stream()
                    .filter(entry ->
                                    entry.getExitDate() != null &&
                                    bounceTime < (entry.getExitDate().getTime() - entry.getExitDate().getTime())
                    )
                    .count();
        }
    }

    // Sets Bounce Time
    public void setBounceTime(long ms) {
        this.bounceTime = ms;
    }

    // Get bounce rate
    public double getBounceRate() {
        double bounceNum = (double) getNumOfBounces();
        return bounceNum / serverLog.size();
    }

    // Get number of conversions
    public long getNumOfConversions() {
        return serverLog.stream()
                .filter(e->e.getConversion()==1)
                .count();
    }

    //sets granularity for output data.
    //0=hour, 1=day, 2=month, 3=year
    public void setGranularity(Granularity g){
        this.granularity=g;
    }

//    public Stream<ImpressionLog> getImpressionLogs(){
//
//    }
//
//    public Stream<ClickLog> getClickLogs(){
//
//    }
//
//    public Stream<ServerLog> getServerLogs(){
//
//    }
}
