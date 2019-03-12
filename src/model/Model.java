package model;

import common.FileType;
import common.Granularity;
import javafx.util.Pair;

import java.io.File;
import java.sql.Statement;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;

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
    private int bouncePages = 0;
    private Connection con;
    private long bounceTime=-1;

    private Granularity granularity = Granularity.DAY;

    private ArrayList<FilterDate> dates = new ArrayList<>();


    public Model(File impressionLog, File clickLog, File serverLog) throws Exception{
        loadFile(impressionLog, FileType.IMPRESSION_LOG);
        loadFile(clickLog, FileType.CLICK_LOG);
        loadFile(serverLog, FileType.SERVER_LOG);
        getDates();
    }

    private boolean loadFile(File inputFile, FileType fileType) throws Exception{
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

//----------------------------------------------------------------------------------------------------------------------

    //calculates cost based on either impression cost or click cost depending on value of impressionCost field
    public double getTotalCost() {
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

    private double getTotalCost(FilterDate d){
        switch (granularity){
            case HOUR:
                if(impressionCost){
                    return impressionLog.stream()
                            .filter(e->e.getImpressionDate().getHours() == d.hours && e.getImpressionDate().getDate() == d.day && e.getImpressionDate().getMonth() == d.month && e.getImpressionDate().getYear() == d.year)
                            .mapToDouble(ImpressionLog::getImpressionCost)
                            .sum();
                }else{
                    return clickLog.stream()
                            .filter(e->e.getClickDate().getHours() == d.hours && e.getClickDate().getDate() == d.day && e.getClickDate().getMonth() == d.month && e.getClickDate().getYear() == d.year)
                            .mapToDouble(ClickLog::getClickCost)
                            .sum();
                }
            case DAY:
                if(impressionCost){
                    return impressionLog.stream()
                            .filter(e-> e.getImpressionDate().getDate() == d.day && e.getImpressionDate().getMonth() == d.month && e.getImpressionDate().getYear() == d.year)
                            .mapToDouble(ImpressionLog::getImpressionCost)
                            .sum();
                }else{
                    return clickLog.stream()
                            .filter(e-> e.getClickDate().getDate() == d.day && e.getClickDate().getMonth() == d.month && e.getClickDate().getYear() == d.year)
                            .mapToDouble(ClickLog::getClickCost)
                            .sum();
                }
            case MONTH:
                if(impressionCost){
                    return impressionLog.stream()
                            .filter(e-> e.getImpressionDate().getMonth() == d.month && e.getImpressionDate().getYear() == d.year)
                            .mapToDouble(ImpressionLog::getImpressionCost)
                            .sum();
                }else{
                    return clickLog.stream()
                            .filter(e-> e.getClickDate().getMonth() == d.month && e.getClickDate().getYear() == d.year)
                            .mapToDouble(ClickLog::getClickCost)
                            .sum();
                }
            case YEAR:
                if(impressionCost){
                    return impressionLog.stream()
                            .filter(e-> e.getImpressionDate().getYear() == d.year)
                            .mapToDouble(ImpressionLog::getImpressionCost)
                            .sum();
                }else{
                    return clickLog.stream()
                            .filter(e-> e.getClickDate().getYear() == d.year)
                            .mapToDouble(ClickLog::getClickCost)
                            .sum();
                }
        }
        return 0;
    }

    public List<Pair<Date, Double>> getTotalCostPair(){
        ArrayList<Pair<Date,Double>> list = new ArrayList<>();
        switch(granularity){
            case HOUR:
                if (impressionCost) {
                    for(FilterDate d:dates){
                        if(list.stream().map(Pair::getKey).noneMatch(e-> e.getHours()==d.hours && e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                            list.add( new Pair<Date, Double>(new Date(d.year, d.month, d.day, d.hours, 0),
                                    impressionLog.stream()
                                            .filter(e-> e.getImpressionDate().getHours() == d.hours && e.getImpressionDate().getDate() == d.day && e.getImpressionDate().getMonth() == d.month && e.getImpressionDate().getYear() == d.year)
                                            .mapToDouble(ImpressionLog::getImpressionCost)
                                            .sum()));

                    }
                } else {
                    for(FilterDate d:dates){
                        if(list.stream().map(Pair::getKey).noneMatch(e-> e.getHours()==d.hours && e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                            list.add( new Pair<Date, Double>(new Date(d.year, d.month, d.day, d.hours, 0),
                                    clickLog.stream()
                                            .filter(e-> e.getClickDate().getHours() == d.hours && e.getClickDate().getDate() == d.day && e.getClickDate().getMonth() == d.month && e.getClickDate().getYear() == d.year)
                                            .mapToDouble(ClickLog::getClickCost)
                                            .sum()));

                    }
                }

                break;
            case DAY:
                if (impressionCost) {
                    for(FilterDate d:dates){
                        if(list.stream().map(Pair::getKey).noneMatch(e-> e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                            list.add( new Pair<Date, Double>(new Date(d.year, d.month, d.day),
                                    impressionLog.stream()
                                            .filter(e-> e.getImpressionDate().getDate() == d.day && e.getImpressionDate().getMonth() == d.month && e.getImpressionDate().getYear() == d.year)
                                            .mapToDouble(ImpressionLog::getImpressionCost)
                                            .sum()));

                    }
                } else {
                    for(FilterDate d:dates){
                        if(list.stream().map(Pair::getKey).noneMatch(e-> e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                            list.add( new Pair<Date, Double>(new Date(d.year, d.month, d.day),
                                    clickLog.stream()
                                            .filter(e-> e.getClickDate().getDate() == d.day && e.getClickDate().getMonth() == d.month && e.getClickDate().getYear() == d.year)
                                            .mapToDouble(ClickLog::getClickCost)
                                            .sum()));

                    }
                }
                break;
            case MONTH:
                if (impressionCost) {
                    for(FilterDate d:dates){
                        if(list.stream().map(Pair::getKey).noneMatch(e-> e.getMonth() == d.month && e.getYear() == d.year))
                            list.add( new Pair<Date, Double>(new Date(d.year, d.month+1, -1),
                                    impressionLog.stream()
                                            .filter(e-> e.getImpressionDate().getMonth() == d.month && e.getImpressionDate().getYear() == d.year)
                                            .mapToDouble(ImpressionLog::getImpressionCost)
                                            .sum()));

                    }
                } else {
                    for(FilterDate d:dates){
                        if(list.stream().map(Pair::getKey).noneMatch(e-> e.getMonth() == d.month && e.getYear() == d.year))
                            list.add( new Pair<Date, Double>(new Date(d.year, d.month+1,-1),
                                    clickLog.stream()
                                            .filter(e-> e.getClickDate().getMonth() == d.month && e.getClickDate().getYear() == d.year)
                                            .mapToDouble(ClickLog::getClickCost)
                                            .sum()));

                    }
                }
                break;
            case YEAR:
                if (impressionCost) {
                    for(FilterDate d:dates){
                        if(list.stream().map(Pair::getKey).noneMatch(e-> e.getYear() == d.year))
                            list.add( new Pair<Date, Double>(new Date(d.year, 11, 31),
                                    impressionLog.stream()
                                            .filter(e-> e.getImpressionDate().getYear() == d.year)
                                            .mapToDouble(ImpressionLog::getImpressionCost)
                                            .sum()));

                    }
                } else {
                    for(FilterDate d:dates){
                        if(list.stream().map(Pair::getKey).noneMatch(e-> e.getYear() == d.year))
                            list.add( new Pair<Date, Double>(new Date(d.year, 11,31),
                                    clickLog.stream()
                                            .filter(e-> e.getClickDate().getYear() == d.year)
                                            .mapToDouble(ClickLog::getClickCost)
                                            .sum()));

                    }
                }
                break;
        }
        return list;
    }

    //Cost per click
    public double getClickCost() {
        long numOfClicks = clickLog.size();

        return (getTotalCost() / numOfClicks);
    }

    public List<Pair<Date, Double>> getClickCostPair(){
        ArrayList<Pair<Date,Double>> list = new ArrayList<>();
        switch(granularity){
            case HOUR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getHours()==d.hours && e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Double>(new Date(d.year, d.month, d.day, d.hours, 0),
                                getTotalCost(d) /
                                        clickLog.stream()
                        .filter(e->e.getClickDate().getHours() == d.hours && e.getClickDate().getDate() == d.day && e.getClickDate().getMonth() == d.month && e.getClickDate().getYear() == d.year)
                        .count()));
                }
                break;
            case DAY:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Double>(new Date(d.year, d.month, d.day),
                                getTotalCost(d) /
                                        clickLog.stream()
                                                .filter(e->e.getClickDate().getDate() == d.day && e.getClickDate().getMonth() == d.month && e.getClickDate().getYear() == d.year)
                                                .count()));
                }
                break;
            case MONTH:
                for(FilterDate d:dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> e.getMonth() == d.month && e.getYear() == d.year))
                        list.add(new Pair<Date, Double>(new Date(d.year, d.month + 1, -1),
                                getTotalCost(d) /
                                        clickLog.stream()
                                                .filter(e->e.getClickDate().getMonth() == d.month && e.getClickDate().getYear() == d.year)
                                                .count()));
                }
                break;
            case YEAR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getYear() == d.year))
                        list.add( new Pair<Date, Double>(new Date(d.year, 11, 31),
                                getTotalCost(d) /
                                        clickLog.stream()
                                                .filter(e-> e.getClickDate().getYear() == d.year)
                                                .count()));
                }
                break;
        }
        return list;
    }

    //Cost per 1000 impressionLog
    public double getCPM() {
        long numOfRecords = impressionLog.size();

        return (getTotalCost() / numOfRecords) * 1000.0;
    }

    public List<Pair<Date, Double>> getCPMPair(){
        ArrayList<Pair<Date,Double>> list = new ArrayList<>();
        switch(granularity){
            case HOUR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getHours()==d.hours && e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Double>(new Date(d.year, d.month, d.day, d.hours, 0),
                                getTotalCost(d) /
                                        impressionLog.stream()
                                                .filter(e->e.getImpressionDate().getHours() == d.hours && e.getImpressionDate().getDate() == d.day && e.getImpressionDate().getMonth() == d.month && e.getImpressionDate().getYear() == d.year)
                                                .count()*1000.0));
                }
                break;
            case DAY:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Double>(new Date(d.year, d.month, d.day),
                                getTotalCost(d) /
                                        impressionLog.stream()
                                                .filter(e-> e.getImpressionDate().getDate() == d.day && e.getImpressionDate().getMonth() == d.month && e.getImpressionDate().getYear() == d.year)
                                                .count()*1000.0));
                }
                break;
            case MONTH:
                for(FilterDate d:dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> e.getMonth() == d.month && e.getYear() == d.year))
                        list.add(new Pair<Date, Double>(new Date(d.year, d.month + 1, -1),
                                getTotalCost(d) /
                                        impressionLog.stream()
                                                .filter(e->e.getImpressionDate().getMonth() == d.month && e.getImpressionDate().getYear() == d.year)
                                                .count()*1000.0));
                }
                break;
            case YEAR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getYear() == d.year))
                        list.add( new Pair<Date, Double>(new Date(d.year, 11, 31),
                                getTotalCost(d) /
                                        impressionLog.stream()
                                                .filter(e-> e.getImpressionDate().getYear() == d.year)
                                                .count()*1000.0));
                }
                break;
        }
        return list;
    }

    //Click through rate
    public double getCTR() {
        long numOfImpressions = impressionLog.size();

        long numOfClicks = clickLog.size();

        return ((double) numOfClicks / numOfImpressions);
    }

    public List<Pair<Date, Double>> getCTRPair(){
        ArrayList<Pair<Date,Double>> list = new ArrayList<>();
        switch(granularity){
            case HOUR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getHours()==d.hours && e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Double>(new Date(d.year, d.month, d.day, d.hours, 0),
                                (double) clickLog.stream()
                                        .filter(e->e.getClickDate().getHours() == d.hours && e.getClickDate().getDate() == d.day && e.getClickDate().getMonth() == d.month && e.getClickDate().getYear() == d.year)
                                        .count()
                                /
                                        impressionLog.stream()
                                                .filter(e->e.getImpressionDate().getHours() == d.hours && e.getImpressionDate().getDate() == d.day && e.getImpressionDate().getMonth() == d.month && e.getImpressionDate().getYear() == d.year)
                                                .count()));
                }
                break;
            case DAY:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Double>(new Date(d.year, d.month, d.day),
                                (double) clickLog.stream()
                                        .filter(e->e.getClickDate().getDate() == d.day && e.getClickDate().getMonth() == d.month && e.getClickDate().getYear() == d.year)
                                        .count()
                                        /
                                        impressionLog.stream()
                                                .filter(e->e.getImpressionDate().getDate() == d.day && e.getImpressionDate().getMonth() == d.month && e.getImpressionDate().getYear() == d.year)
                                                .count()));
                }
                break;
            case MONTH:
                for(FilterDate d:dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> e.getMonth() == d.month && e.getYear() == d.year))
                        list.add(new Pair<Date, Double>(new Date(d.year, d.month + 1, -1),
                                (double) clickLog.stream()
                                        .filter(e-> e.getClickDate().getMonth() == d.month && e.getClickDate().getYear() == d.year)
                                        .count()
                                        /
                                        impressionLog.stream()
                                                .filter(e->e.getImpressionDate().getMonth() == d.month && e.getImpressionDate().getYear() == d.year)
                                                .count()));
                }
                break;
            case YEAR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getYear() == d.year))
                        list.add( new Pair<Date, Double>(new Date(d.year, 11, 31),
                                (double) clickLog.stream()
                                        .filter(e-> e.getClickDate().getYear() == d.year)
                                        .count()
                                        /
                                        impressionLog.stream()
                                                .filter(e-> e.getImpressionDate().getYear() == d.year)
                                                .count()));
                }
                break;
        }
        return list;
    }

    //Cost per acquisition
    public double getCPA() {
        long numOfAcquisitions = serverLog.stream()
                .filter(s -> s.getConversion() ==1)
                .count();

        return (getTotalCost() / numOfAcquisitions);
    }

    public List<Pair<Date, Double>> getCPAPair(){
        ArrayList<Pair<Date,Double>> list = new ArrayList<>();
        switch(granularity){
            case HOUR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getHours()==d.hours && e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Double>(new Date(d.year, d.month, d.day, d.hours, 0),
                                getTotalCost(d) /
                                        serverLog.stream()
                                                .filter(e->e.getEntryDate().getHours() == d.hours && e.getEntryDate().getDate() == d.day && e.getEntryDate().getMonth() == d.month && e.getEntryDate().getYear() == d.year)
                                                .filter(s->s.getConversion()==1)
                                                .count()));
                }
                break;
            case DAY:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Double>(new Date(d.year, d.month, d.day),
                                getTotalCost(d) /
                                        serverLog.stream()
                                                .filter(e->e.getEntryDate().getDate() == d.day && e.getEntryDate().getMonth() == d.month && e.getEntryDate().getYear() == d.year)
                                                .filter(s->s.getConversion()==1)
                                                .count()));
                }
                break;
            case MONTH:
                for(FilterDate d:dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> e.getMonth() == d.month && e.getYear() == d.year))
                        list.add(new Pair<Date, Double>(new Date(d.year, d.month + 1, -1),
                                getTotalCost(d) /
                                        serverLog.stream()
                                                .filter(e->e.getEntryDate().getMonth() == d.month && e.getEntryDate().getYear() == d.year)
                                                .filter(s->s.getConversion()==1)
                                                .count()));
                }
                break;
            case YEAR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getYear() == d.year))
                        list.add( new Pair<Date, Double>(new Date(d.year, 11, 31),
                                getTotalCost(d) /
                                        serverLog.stream()
                                                .filter(e->e.getEntryDate().getYear() == d.year)
                                                .filter(s->s.getConversion()==1)
                                                .count()));
                }
                break;
        }
        return list;
    }

    //Number of impressions
    public long getNumOfImpressions() {
        return impressionLog.size();
    }

    public List<Pair<Date, Long>> getNumOfImpressionsPair(){
        ArrayList<Pair<Date,Long>> list = new ArrayList<>();
        switch(granularity){
            case HOUR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getHours()==d.hours && e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                        impressionLog.stream()
                                                .filter(e->e.getImpressionDate().getHours() == d.hours && e.getImpressionDate().getDate() == d.day && e.getImpressionDate().getMonth() == d.month && e.getImpressionDate().getYear() == d.year)
                                                .count()));
                }
                break;
            case DAY:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                impressionLog.stream()
                                        .filter(e->e.getImpressionDate().getDate() == d.day && e.getImpressionDate().getMonth() == d.month && e.getImpressionDate().getYear() == d.year)
                                        .count()));
                }
                break;
            case MONTH:
                for(FilterDate d:dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                impressionLog.stream()
                                        .filter(e->e.getImpressionDate().getMonth() == d.month && e.getImpressionDate().getYear() == d.year)
                                        .count()));
                }
                break;
            case YEAR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                impressionLog.stream()
                                        .filter(e->e.getImpressionDate().getYear() == d.year)
                                        .count()));
                }
                break;
        }
        return list;
    }

    //Number of clickLog
    public long getNumOfClicks() {

        return clickLog.size();
    }

    public List<Pair<Date, Long>> getNumOfClicksPair(){
        ArrayList<Pair<Date,Long>> list = new ArrayList<>();
        switch(granularity){
            case HOUR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getHours()==d.hours && e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                clickLog.stream()
                                        .filter(e->e.getClickDate().getHours() == d.hours && e.getClickDate().getDate() == d.day && e.getClickDate().getMonth() == d.month && e.getClickDate().getYear() == d.year)
                                        .count()));
                }
                break;
            case DAY:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                clickLog.stream()
                                        .filter(e->e.getClickDate().getDate() == d.day && e.getClickDate().getMonth() == d.month && e.getClickDate().getYear() == d.year)
                                        .count()));
                }
                break;
            case MONTH:
                for(FilterDate d:dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                clickLog.stream()
                                        .filter(e-> e.getClickDate().getMonth() == d.month && e.getClickDate().getYear() == d.year)
                                        .count()));
                }
                break;
            case YEAR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                clickLog.stream()
                                        .filter(e-> e.getClickDate().getYear() == d.year)
                                        .count()));
                }
                break;
        }
        return list;
    }


    //Number of unique clickLog
    public long getNumOfUniqueClicks() {

        return clickLog.stream()
                .map(ClickLog::getSubjectID)
                .distinct()
                .count();
    }

    public List<Pair<Date, Long>> getNumOfUniqueClicksPair(){
        ArrayList<Pair<Date,Long>> list = new ArrayList<>();
        switch(granularity){
            case HOUR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getHours()==d.hours && e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                clickLog.stream()
                                        .filter(e->e.getClickDate().getHours() == d.hours && e.getClickDate().getDate() == d.day && e.getClickDate().getMonth() == d.month && e.getClickDate().getYear() == d.year)
                                        .map(ClickLog::getSubjectID)
                                        .distinct()
                                        .count()));
                }
                break;
            case DAY:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                clickLog.stream()
                                        .filter(e->e.getClickDate().getDate() == d.day && e.getClickDate().getMonth() == d.month && e.getClickDate().getYear() == d.year)
                                        .map(ClickLog::getSubjectID)
                                        .distinct()
                                        .count()));
                }
                break;
            case MONTH:
                for(FilterDate d:dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                clickLog.stream()
                                        .filter(e-> e.getClickDate().getMonth() == d.month && e.getClickDate().getYear() == d.year)
                                        .map(ClickLog::getSubjectID)
                                        .distinct()
                                        .count()));
                }
                break;
            case YEAR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                clickLog.stream()
                                        .filter(e-> e.getClickDate().getYear() == d.year)
                                        .map(ClickLog::getSubjectID)
                                        .distinct()
                                        .count()));
                }
                break;
        }
        return list;
    }

    private long getNumOfBounces(FilterDate d){
        switch(granularity) {
            case HOUR:
                if (bounceTime <= 0 && bouncePages<=0) {
                    return serverLog.stream()
                            .filter(e -> e.getEntryDate().getHours() == d.hours && e.getEntryDate().getDate() == d.day && e.getEntryDate().getMonth() == d.month && e.getEntryDate().getYear() == d.year)
                            .filter(e->e.getConversion()==0)
                            .count();
                } else {
                    return serverLog.stream()
                            .filter(e -> e.getEntryDate().getHours() == d.hours && e.getEntryDate().getDate() == d.day && e.getEntryDate().getMonth() == d.month && e.getEntryDate().getYear() == d.year)
                            .filter(e -> (e.getExitDate() != null && bounceTime < (e.getExitDate().getTime() - e.getEntryDate().getTime())) || e.getPagesViewed()<bouncePages)
                            .count();
                }
            case DAY:
                if (bounceTime <= 0 && bouncePages<=0) {
                    return serverLog.stream()
                            .filter(e -> e.getEntryDate().getDate() == d.day && e.getEntryDate().getMonth() == d.month && e.getEntryDate().getYear() == d.year)
                            .filter(e->e.getConversion()==0)
                            .count();
                } else {
                    return serverLog.stream()
                            .filter(e -> e.getEntryDate().getDate() == d.day && e.getEntryDate().getMonth() == d.month && e.getEntryDate().getYear() == d.year)
                            .filter(e -> (e.getExitDate() != null && bounceTime < (e.getExitDate().getTime() - e.getEntryDate().getTime())) || e.getPagesViewed()<bouncePages)
                            .count();
                }
            case MONTH:
                if (bounceTime <= 0 && bouncePages<=0) {
                    return serverLog.stream()
                            .filter(e -> e.getEntryDate().getMonth() == d.month && e.getEntryDate().getYear() == d.year)
                            .filter(e->e.getConversion()==0)
                            .count();
                } else {
                    return serverLog.stream()
                            .filter(e -> e.getEntryDate().getMonth() == d.month && e.getEntryDate().getYear() == d.year)
                            .filter(e -> (e.getExitDate() != null && bounceTime < (e.getExitDate().getTime() - e.getEntryDate().getTime())) || e.getPagesViewed()<bouncePages)
                            .count();
                }
            case YEAR:
                if (bounceTime <= 0 && bouncePages<=0) {
                    return serverLog.stream()
                            .filter(e -> e.getEntryDate().getYear() == d.year)
                            .filter(e->e.getConversion()==0)
                            .count();
                } else {
                    return serverLog.stream()
                            .filter(e -> e.getEntryDate().getYear() == d.year)
                            .filter(e -> (e.getExitDate() != null && bounceTime < (e.getExitDate().getTime() - e.getEntryDate().getTime())) || e.getPagesViewed()<bouncePages)
                            .count();
                }
        }
        return 0;
    }

    // Get number of bounces
    public long getNumOfBounces() {
        // if no bounce time is use conversion
        if (bounceTime <= 0 && bouncePages<=0) {
            return serverLog.size() - getNumOfConversions();
        } else {
            return serverLog.stream()
                    .filter(entry ->
                            (entry.getExitDate() != null &&
                                    bounceTime < (entry.getExitDate().getTime() - entry.getEntryDate().getTime()))
                            || entry.getPagesViewed()<bouncePages
                    )
                    .count();
        }
    }

    public List<Pair<Date, Long>> getNumOfBouncesPair(){
        ArrayList<Pair<Date,Long>> list = new ArrayList<>();
        switch(granularity){
            case HOUR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getHours()==d.hours && e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                getNumOfBounces(d)));
                }
                break;
            case DAY:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                getNumOfBounces(d)));
                }
                break;
            case MONTH:
                for(FilterDate d:dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                getNumOfBounces(d)));
                }
                break;
            case YEAR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                getNumOfBounces(d)));
                }
                break;
        }
        return list;
    }

    // Get bounce rate
    public double getBounceRate() {
        double bounceNum = (double) getNumOfBounces();
        return bounceNum / serverLog.size();
    }

    public List<Pair<Date, Double>> getBounceRatePair(){
        ArrayList<Pair<Date,Double>> list = new ArrayList<>();
        switch(granularity){
            case HOUR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getHours()==d.hours && e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Double>(new Date(d.year, d.month, d.day, d.hours, 0),
                                (double)getNumOfBounces(d)
                                        /
                                        serverLog.stream()
                                                .filter(e->e.getEntryDate().getHours() == d.hours && e.getEntryDate().getDate() == d.day && e.getEntryDate().getMonth() == d.month && e.getEntryDate().getYear() == d.year)
                                                .count()
                        ));
                }
                break;
            case DAY:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Double>(new Date(d.year, d.month, d.day, d.hours, 0),
                                (double)getNumOfBounces(d)
                                        /
                                        serverLog.stream()
                                                .filter(e->e.getEntryDate().getDate() == d.day && e.getEntryDate().getMonth() == d.month && e.getEntryDate().getYear() == d.year)
                                                .count()
                        ));
                }
                break;
            case MONTH:
                for(FilterDate d:dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Double>(new Date(d.year, d.month, d.day, d.hours, 0),
                                (double)getNumOfBounces(d)
                                        /
                                        serverLog.stream()
                                                .filter(e->e.getEntryDate().getMonth() == d.month && e.getEntryDate().getYear() == d.year)
                                                .count()
                        ));
                }
                break;
            case YEAR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getYear() == d.year))
                        list.add( new Pair<Date, Double>(new Date(d.year, d.month, d.day, d.hours, 0),
                                (double)getNumOfBounces(d)
                                        /
                                        serverLog.stream()
                                                .filter(e->e.getEntryDate().getYear() == d.year)
                                                .count()
                        ));
                }
                break;
        }
        return list;
    }



    // Get number of conversions
    public long getNumOfConversions() {
        return serverLog.stream()
                .filter(e->e.getConversion()==1)
                .count();
    }

    public List<Pair<Date, Long>> getNumOfConversionsPair(){
        ArrayList<Pair<Date,Long>> list = new ArrayList<>();
        switch(granularity){
            case HOUR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getHours()==d.hours && e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                serverLog.stream()
                                        .filter(e->e.getEntryDate().getHours() == d.hours && e.getEntryDate().getDate() == d.day && e.getEntryDate().getMonth() == d.month && e.getEntryDate().getYear() == d.year)
                                        .filter(e->e.getConversion()==1)
                                        .count()));
                }
                break;
            case DAY:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getDate() == d.day && e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                serverLog.stream()
                                        .filter(e-> e.getEntryDate().getDate() == d.day && e.getEntryDate().getMonth() == d.month && e.getEntryDate().getYear() == d.year)
                                        .filter(e->e.getConversion()==1)
                                        .count()));
                }
                break;
            case MONTH:
                for(FilterDate d:dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> e.getMonth() == d.month && e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                serverLog.stream()
                                        .filter(e->e.getEntryDate().getMonth() == d.month && e.getEntryDate().getYear() == d.year)
                                        .filter(e->e.getConversion()==1)
                                        .count()));
                }
                break;
            case YEAR:
                for(FilterDate d:dates){
                    if(list.stream().map(Pair::getKey).noneMatch(e-> e.getYear() == d.year))
                        list.add( new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                serverLog.stream()
                                        .filter(e->e.getEntryDate().getYear() == d.year)
                                        .filter(e->e.getConversion()==1)
                                        .count()));
                }
                break;
        }
        return list;
    }

    //sets granularity for output data.
    //0=hour, 1=day, 2=month, 3=year
    public void setGranularity(Granularity g){
        this.granularity=g;
    }

    // Sets Bounce Time
    public void setBounceTime(long ms) {
        this.bounceTime = ms;
    }
    //Sets minimum number of pages for visit to not be a bounce
    public void setBouncePageReq(int pages){
        this.bouncePages = pages;
    }

    //Collects dates for granularising
    private void getDates(){
        impressionLog.stream()
                .map(ImpressionLog::getImpressionDate)
                .distinct()
                .forEach(e->{
                    FilterDate d = new FilterDate(e.getHours(), e.getDate(), e.getMonth(), e.getYear());
                    if(!dates.contains(d)){
                        dates.add(d);
                    }
                });

        clickLog.stream()
                .map(ClickLog::getClickDate)
                .distinct()
                .forEach(e->{
                    FilterDate d = new FilterDate(e.getHours(), e.getDate(), e.getMonth(), e.getYear());
                    if(!dates.contains(d)){
                        dates.add(d);
                    }
                });

        serverLog.stream()
                .map(ServerLog::getEntryDate)
                .distinct()
                .forEach(e->{
                    FilterDate d = new FilterDate(e.getHours(), e.getDate(), e.getMonth(), e.getYear());
                    if(!dates.contains(d)){
                        dates.add(d);
                    }
                });

    }


    private static class FilterDate{
        public int hours,day,month,year;
        public FilterDate(int hours, int day, int month, int year){
            this.hours=hours;
            this.day=day;
            this.month=month;
            this.year=year;
        }

        @Override
        public boolean equals(Object o){
            if(o instanceof FilterDate) {
                FilterDate d = (FilterDate) o;
                return this.hours == d.hours && this.day == d.day && this.month == d.month && this.year == d.year;
            }else {
                return false;
            }
        }
    }
}
