package model;

import common.FileType;
import common.Filters.*;
import common.Granularity;
import common.Metric;
import common.Observable;
import common.Observer;
import controller.AccountController;
import controller.CampaignController;
import controller.FilterController;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;
import model.DAO.DBPool;
import model.DAO.SubjectsDAO;
import model.DBTasks.Insert;

import java.io.File;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Date;
import java.util.function.Function;

import static common.FileType.*;

/**
 * Created by furqan on 27/02/2019.
 */

public class Model extends Task<Void> implements Observable {

    private String name;

    private DecimalFormat df = new DecimalFormat("#.####");

    private Connection con;
    private int BATCH_SIZE = 1000;
    private User user;

    private CampaignController campaignController;

    private List<ImpressionLog> impressionLog = new ArrayList<>();
    private List<ClickLog> clickLog = new ArrayList<>();
    private List<ServerLog> serverLog = new ArrayList<>();

    private List<ImpressionLog> rawImpressionLog;
    private List<ClickLog> rawClickLog;
    private List<ServerLog> rawServerLog;
    private RawDataHolder rawDataHolder;


    public Metrics metrics;
    private ChartData chartData;

    private String campaignTitle;

    private Map<String, Subject> subjects = new HashMap<>();
    private boolean impressionCost = true;
    private int bouncePages = 0;
    private long bounceTime = -1;

    private static Granularity granularity = Granularity.DAY;
    private ArrayList<FilterDate> dates = new ArrayList<>();

    private HashMap<Integer, AgeFilter> ageFilters = new HashMap<>();
    private HashMap<Integer, ContextFilter> contextFilters = new HashMap<>();
    private HashMap<Integer, GenderFilter> genderFilters = new HashMap<>();
    private HashMap<Integer, IncomeFilter> incomeFilters = new HashMap<>();
    private HashMap<Integer, DateFilter> dateFilters = new HashMap<>();

    private FilterController filterController;

    public void setFilterController(FilterController filterController) {
        this.filterController = filterController;
    }

    public HashMap<Integer, AgeFilter> getAgeFilters() {
        return ageFilters;
    }

    public HashMap<Integer, ContextFilter> getContextFilters() {
        return contextFilters;
    }

    public HashMap<Integer, GenderFilter> getGenderFilters() {
        return genderFilters;
    }

    public HashMap<Integer, IncomeFilter> getIncomeFilters() {
        return incomeFilters;
    }

    public HashMap<Integer, DateFilter> getDateFilters() {
        return dateFilters;
    }

    public void setRawDataHolder(RawDataHolder rawDataHolder) {
        this.rawDataHolder = rawDataHolder;
        impressionLog.addAll(rawDataHolder.getImpressionLog());
        clickLog.addAll(rawDataHolder.getClickLog());
        serverLog.addAll(rawDataHolder.getServerLog());
        setMetrics();
        notifyObservers();
    }

    public Model() {
        metrics = new Metrics();
        chartData = new ChartData();
    }

    @Override
    protected Void call() {
        getDates();
        setMetrics();
        return null;
    }

    public Map<String, Subject> getSubjects() {
        return rawDataHolder.getSubjects();
    }

    @Override
    protected void succeeded() {
        notifyObservers("files");
    }


    public Model(File fileImpressionLog, File fileClickLog, File fileServerLog) {
        metrics = new Metrics();
        chartData = new ChartData();
    }

    public void setCampaignController(CampaignController campaignController) {
        this.campaignController = campaignController;
    }

    public Model(String name, RawDataHolder dataHolder){
        this.name = name;
        try {
            this.rawDataHolder = (RawDataHolder) dataHolder.clone();
            impressionLog.addAll(rawDataHolder.getImpressionLog());
            clickLog.addAll(rawDataHolder.getClickLog());
            serverLog.addAll(rawDataHolder.getServerLog());
            notifyObservers();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        getDates();
        metrics=new Metrics();
        chartData = new ChartData();
    }

    public String getName() {
        return name;
    }

    public RawDataHolder getRawDataHolder() {
        return rawDataHolder;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    Object entry;

    public void uploadData() {
        if(impressionLog != null) {
            UploadData uploadDataTask = new UploadData(IMPRESSION_LOG);
            new Thread(uploadDataTask).start();
        }
        if(clickLog != null) {
            UploadData uploadDataTask = new UploadData(SERVER_LOG);
            new Thread(uploadDataTask).start();
        }
        if(impressionLog != null) {
            UploadData uploadDataTask = new UploadData(CLICK_LOG);
            new Thread(uploadDataTask).start();
        }
    }

    public class UploadData extends Task<List<Object>> {

        private FileType type;

        public UploadData(FileType type) {
            this.type = type;
        }

        @Override
        protected List<Object> call() {
            if(AccountController.online) {
                try {
                    con = DBPool.getConnection();
                    PreparedStatement pstmt;

                    int i;

                    switch (type) {
                        case IMPRESSION_LOG:
                            pstmt = con.prepareStatement("INSERT INTO impression_log (campaign_id, subject_id, date, context, cost) " +
                                    "VALUES (" +
                                    "(SELECT id FROM campaigns WHERE title=? LIMIT 1), " +
                                    "(SELECT id FROM subjects WHERE subject_id=? LIMIT 1), " +
                                    "?, ?, ?" +
                                    ")"
                            );

                            i = 0;

                            for (ImpressionLog log : impressionLog) {
                                pstmt.setString(1, campaignTitle);
                                pstmt.setString(2, log.getSubjectID());
                                pstmt.setString(3, log.getImpressionDate().toString());
                                pstmt.setString(4, log.getContext());
                                pstmt.setDouble(5, log.getImpressionCost());

                                pstmt.addBatch();

                                i++;

                                if (i % BATCH_SIZE == 0) {
                                    pstmt.executeBatch();
                                    con.commit();
                                }
                            }
                            pstmt.executeBatch();
                            DBPool.closeConnection(con);
                            break;
                        case CLICK_LOG:
                            pstmt = con.prepareStatement("INSERT INTO click_log(campaign_id, subject_id, date, click_cost) " +
                                    "VALUES (" +
                                    "(SELECT id FROM campaigns WHERE title=? LIMIT 1), " +
                                    "(SELECT id FROM subjects WHERE subject_id=? LIMIT 1), " +
                                    "?, ?" +
                                    ")"
                            );

                            i = 0;

                            for (ClickLog log : clickLog) {
                                pstmt.setString(1, campaignTitle);
                                pstmt.setString(2, log.getSubjectID());
                                pstmt.setString(3, log.getClickDate().toString());
                                pstmt.setDouble(4, log.getClickCost());

                                pstmt.addBatch();

                                i++;

                                if (i % BATCH_SIZE == 0) {
                                    pstmt.executeBatch();
                                    con.commit();
                                }
                            }
                            pstmt.executeBatch();
                            con.commit();
                            break;
                        case SERVER_LOG:
                            pstmt = con.prepareStatement("INSERT INTO server_log (campaign_id, subject_id, entry_date, exit_date, pages_viewed, conversion) " +
                                    "VALUES (" +
                                    "(SELECT id FROM campaigns WHERE title=? LIMIT 1), " +
                                    "(SELECT id FROM subjects WHERE subject_id=? LIMIT 1), " +
                                    "?, ?, ?, ?" +
                                    ")"
                            );

                            i = 0;

                            for (ServerLog log : serverLog) {
                                pstmt.setString(1, campaignTitle);
                                pstmt.setString(2, log.getSubjectID());
                                pstmt.setString(3, (entry = log.getEntryDate()) == null ? "null" : entry.toString());
                                pstmt.setString(4, (entry = log.getExitDate()) == null ? "null" : entry.toString());
                                pstmt.setInt(5, (int) (entry = log.getPagesViewed()) < 0 ? -1 : (int) entry);
                                pstmt.setBoolean(6, log.getConversion() > 0);

                                pstmt.addBatch();

                                i++;

                                if (i % BATCH_SIZE == 0) {
                                    pstmt.executeBatch();
                                    con.commit();
                                }
                            }
                            pstmt.executeBatch();
                            con.commit();
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public void setCampaignTitle(String title) {
        this.campaignTitle = title;
        String query = "INSERT INTO campaigns (title, user_id) VALUES (" +
                "'" + campaignTitle + "'," +
                "(SELECT id FROM users WHERE username='"+ user +"'))";
        Insert insertTask = new Insert(query);

        new Thread(insertTask).start();
    }

    //True sets class to calculate costs based om impression costs, whereas false uses click costs
    public void setCostMode(boolean impressionCostMode) {
        this.impressionCost = impressionCostMode;
    }

//------------CALCULATING DATA------------------------------------------------------------------------------------------

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


    public List<Pair<Date, Double>> getTotalCostPair() {
        return getDoubleMetric(d -> {
                    if (impressionCost) {
                        return impressionLog.stream()
                                .filter(e -> filterDateEqualsDate(e.getImpressionDate(), d))
                                .mapToDouble(ImpressionLog::getImpressionCost)
                                .sum();

                    } else {
                        return clickLog.stream()
                                .filter(e -> filterDateEqualsDate(e.getClickDate(), d))
                                .mapToDouble(ClickLog::getClickCost)
                                .sum();

                    }
                }
        );
    }

    //Cost per click
    public double getClickCost() {
        long numOfClicks = clickLog.size();

        return (getTotalCost() / numOfClicks);
    }

    public List<Pair<Date, Double>> getClickCostPair() {
        return getDoubleMetric(d ->
                getTotalCost(d) /
                        clickLog.stream()
                                .filter(e -> filterDateEqualsDate(e.getClickDate(), d))
                                .count()
        );
    }

    //Individual Click Costs for use in the Histogram
    public List<Double> getIndivClickCost(){
        ArrayList<Double> costs = new ArrayList<>();
        clickLog.stream()
                .mapToDouble(ClickLog::getClickCost)
                .forEach(e->costs.add(e));
        return costs;
    }

    //Cost per 1000 impressionLog
    public double getCPM() {
        long numOfRecords = impressionLog.size();

        return (getTotalCost() / numOfRecords) * 1000.0;
    }

    public List<Pair<Date, Double>> getCPMPair() {
        return getDoubleMetric(d ->
                getTotalCost(d) /
                        impressionLog.stream()
                                .filter(e -> filterDateEqualsDate(e.getImpressionDate(), d))
                                .count() * 1000.0
                );
    }

    //Click through rate
    public double getCTR() {
        long numOfImpressions = impressionLog.size();

        long numOfClicks = clickLog.size();

        return ((double) numOfClicks / numOfImpressions);
    }

    public List<Pair<Date, Double>> getCTRPair() {
        return getDoubleMetric(d ->
                (double) clickLog.stream()
                        .filter(e -> filterDateEqualsDate(e.getClickDate(), d))
                        .count()
                        /
                        impressionLog.stream()
                                .filter(e -> filterDateEqualsDate(e.getImpressionDate(), d))
                                .count()
        );
    }

    //Cost per acquisition
    public double getCPA() {
        long numOfAcquisitions = serverLog.stream()
                .filter(s -> s.getConversion() == 1)
                .count();

        return (getTotalCost() / numOfAcquisitions);
    }

    public List<Pair<Date, Double>> getCPAPair() {
        return getDoubleMetric(d ->
                getTotalCost(d) /
                        serverLog.stream()
                                .filter(e -> filterDateEqualsDate(e.getEntryDate(), d))
                                .filter(s -> s.getConversion() == 1)
                                .count()
        );
    }

    //Number of impressions
    public long getNumOfImpressions() {
        return impressionLog.size();
    }

    public List<Pair<Date, Long>> getNumOfImpressionsPair() {
        return getLongMetric(d ->
                impressionLog.stream()
                        .filter(e -> filterDateEqualsDate(e.getImpressionDate(), d))
                        .count()
        );
    }

    //Number of clickLog
    public long getNumOfClicks() {

        return clickLog.size();
    }

    public List<Pair<Date, Long>> getNumOfClicksPair() {
        return getLongMetric(d ->
                clickLog.stream()
                        .filter(e -> filterDateEqualsDate(e.getClickDate(), d))
                        .count()
                );
    }


    //Number of unique clickLog
    public long getNumOfUniqueClicks() {

        return clickLog.stream()
                .map(ClickLog::getSubjectID)
                .distinct()
                .count();
    }

    public List<Pair<Date, Long>> getNumOfUniqueClicksPair() {
        return getLongMetric(d ->
                clickLog.stream()
                        .filter(e -> filterDateEqualsDate(e.getClickDate(), d))
                        .map(ClickLog::getSubjectID)
                        .distinct()
                        .count()
        );
    }


    // Get number of bounces
    public long getNumOfBounces() {
        // if no bounce time is use conversion
        if (bounceTime <= 0 && bouncePages <= 0) {
            return serverLog.size() - getNumOfConversions();
        } else {
            return serverLog.stream()
                    .filter(entry ->
                            entry.getTimeSpent() < bounceTime
                                    || entry.getPagesViewed() < bouncePages
                    )
                    .count();
        }
    }

    public List<Pair<Date, Long>> getNumOfBouncesPair() {
        return getLongMetric(this::getNumOfBounces);
    }

    // Get bounce rate
    public double getBounceRate() {
        double bounceNum = (double) getNumOfBounces();
        return bounceNum / serverLog.size();
    }

    public List<Pair<Date, Double>> getBounceRatePair() {
        return getDoubleMetric(d ->
            (double) getNumOfBounces(d)
                    /
                    serverLog.stream()
                            .filter(e -> filterDateEqualsDate(e.getEntryDate(), d))
                            .count()
                );
    }


    // Get number of conversions
    public long getNumOfConversions() {
        return serverLog.stream()
                .filter(e -> e.getConversion() == 1)
                .count();
    }

    public List<Pair<Date, Long>> getNumOfConversionsPair() {
        return getLongMetric( d ->
                        serverLog.stream()
                            .filter(e -> filterDateEqualsDate(e.getEntryDate(), d))
                            .filter(e -> e.getConversion() == 1)
                            .count()
                );
    }

    private double getTotalCost(FilterDate d) {
//        switch (granularity) {
//            case HOUR:
//                if (impressionCost) {
//                    return impressionLog.stream()
//                            .filter(e -> filterDateEqualsDate(e.getImpressionDate(), d))
//                            .mapToDouble(ImpressionLog::getImpressionCost)
//                            .sum();
//                } else {
//                    return clickLog.stream()
//                            .filter(e -> filterDateEqualsDate(e.getClickDate(), d))
//                            .mapToDouble(ClickLog::getClickCost)
//                            .sum();
//                }
//            case DAY:
//                if (impressionCost) {
//                    return impressionLog.stream()
//                            .filter(e -> filterDateEqualsDate(e.getImpressionDate(), d))
//                            .mapToDouble(ImpressionLog::getImpressionCost)
//                            .sum();
//                } else {
//                    return clickLog.stream()
//                            .filter(e -> filterDateEqualsDate(e.getClickDate(), d))
//                            .mapToDouble(ClickLog::getClickCost)
//                            .sum();
//                }
//            case MONTH:
//                if (impressionCost) {
//                    return impressionLog.stream()
//                            .filter(e -> filterDateEqualsDate(e.getImpressionDate(), d))
//                            .mapToDouble(ImpressionLog::getImpressionCost)
//                            .sum();
//                } else {
//                    return clickLog.stream()
//                            .filter(e -> filterDateEqualsDate(e.getClickDate(), d))
//                            .mapToDouble(ClickLog::getClickCost)
//                            .sum();
//                }
//            case YEAR:
//                if (impressionCost) {
//                    return impressionLog.stream()
//                            .filter(e -> filterDateEqualsDate(e.getImpressionDate(), d))
//                            .mapToDouble(ImpressionLog::getImpressionCost)
//                            .sum();
//                } else {
//                    return clickLog.stream()
//                            .filter(e -> filterDateEqualsDate(e.getClickDate(), d))
//                            .mapToDouble(ClickLog::getClickCost)
//                            .sum();
//                }
//            case ToD:
//                if (impressionCost) {
//                    return impressionLog.stream()
//                            .filter(e -> filterDateEqualsDate(e.getImpressionDate(), d))
//                            .mapToDouble(ImpressionLog::getImpressionCost)
//                            .sum();
//                } else {
//                    return clickLog.stream()
//                            .filter(e -> filterDateEqualsDate(e.getClickDate(), d))
//                            .mapToDouble(ClickLog::getClickCost)
//                            .sum();
//                }
//            case DoW:
                if (impressionCost) {
                    return impressionLog.stream()
                            .filter(e -> filterDateEqualsDate(e.getImpressionDate(), d))
                            .mapToDouble(ImpressionLog::getImpressionCost)
                            .sum();
                } else {
                    return clickLog.stream()
                            .filter(e -> filterDateEqualsDate(e.getClickDate(), d))
                            .mapToDouble(ClickLog::getClickCost)
                            .sum();
                }
//        }
//        return 0;
    }

    private long getNumOfBounces(FilterDate d) {
//        switch (granularity) {
//            case HOUR:
//                if (bounceTime <= 0 && bouncePages <= 0) {
//                    return serverLog.stream()
//                            .filter(e -> filterDateEqualsDate(e.getEntryDate(), d))
//                            .filter(e -> e.getConversion() == 0)
//                            .count();
//                } else {
//                    return serverLog.stream()
//                            .filter(e -> filterDateEqualsDate(e.getEntryDate(), d))
//                            .filter(e -> (e.getTimeSpent() < bounceTime || e.getPagesViewed() < bouncePages))
//                            .count();
//                }
//            case DAY:
//                if (bounceTime <= 0 && bouncePages <= 0) {
//                    return serverLog.stream()
//                            .filter(e -> filterDateEqualsDate(e.getEntryDate(), d))
//                            .filter(e -> e.getConversion() == 0)
//                            .count();
//                } else {
//                    return serverLog.stream()
//                            .filter(e -> filterDateEqualsDate(e.getEntryDate(), d))
//                            .filter(e -> (e.getTimeSpent() < bounceTime || e.getPagesViewed() < bouncePages))
//                            .count();
//                }
//            case MONTH:
//                if (bounceTime <= 0 && bouncePages <= 0) {
//                    return serverLog.stream()
//                            .filter(e -> filterDateEqualsDate(e.getEntryDate(), d))
//                            .filter(e -> e.getConversion() == 0)
//                            .count();
//                } else {
//                    return serverLog.stream()
//                            .filter(e -> filterDateEqualsDate(e.getEntryDate(), d))
//                            .filter(e -> (e.getTimeSpent() < bounceTime || e.getPagesViewed() < bouncePages))
//                            .count();
//                }
//            case YEAR:
                if (bounceTime <= 0 && bouncePages <= 0) {
                    return serverLog.stream()
                            .filter(e -> filterDateEqualsDate(e.getEntryDate(), d))
                            .filter(e -> e.getConversion() == 0)
                            .count();
                } else {
                    return serverLog.stream()
                            .filter(e -> filterDateEqualsDate(e.getEntryDate(), d))
                            .filter(e -> (e.getTimeSpent() < bounceTime || e.getPagesViewed() < bouncePages))
                            .count();
                }
//        }
//        return 0;
    }

    private ArrayList<Pair<Date, Double>> getDoubleMetric(Function<FilterDate, Double> f){
        ArrayList<Pair<Date, Double>> list = new ArrayList<>();
        switch (granularity) {
            case HOUR:
                for (FilterDate d : dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> filterDateEqualsDate(e, d))) {
                        list.add(new Pair<Date, Double>(new Date(d.year, d.month, d.day, d.hours, 0),
                                f.apply(d)));
                    }
                }
                break;
            case DAY:
                for (FilterDate d : dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> filterDateEqualsDate(e, d))) {
                        list.add(new Pair<Date, Double>(new Date(d.year, d.month, d.day),
                                f.apply(d)));
                    }
                }
                break;
            case MONTH:
                for (FilterDate d : dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> filterDateEqualsDate(e, d))) {
                        list.add(new Pair<Date, Double>(new Date(d.year, d.month, 1),
                                f.apply(d)));
                    }

                }
                break;
            case YEAR:
                for (FilterDate d : dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> filterDateEqualsDate(e, d))) {
                        list.add(new Pair<Date, Double>(new Date(d.year, 0, 1),
                                f.apply(d)));
                    }
                }
                break;
            case ToD:
                for (FilterDate d : dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> filterDateEqualsDate(e, d))) {
                        list.add(new Pair<Date, Double>(new Date(0,0,0,d.hours,0),
                                f.apply(d)));
                    }
                }
                break;
            case DoW:
                for (FilterDate d : dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> filterDateEqualsDate(e, d))) {
                        if(d.dow==0){
                            list.add(new Pair<Date, Double>(new Date(0,0,7),
                                    f.apply(d)));
                        }else {
                            list.add(new Pair<Date, Double>(new Date(0, 0, d.dow),
                                    f.apply(d)));
                        }
                    }
                }
                break;
        }
        return list;
    }

    private ArrayList<Pair<Date, Long>> getLongMetric(Function<FilterDate, Long> f){
        ArrayList<Pair<Date, Long>> list = new ArrayList<>();
        switch (granularity) {
            case HOUR:
                for (FilterDate d : dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> filterDateEqualsDate(e, d))) {
                        list.add(new Pair<Date, Long>(new Date(d.year, d.month, d.day, d.hours, 0),
                                f.apply(d)));
                    }
                }
                break;
            case DAY:
                for (FilterDate d : dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> filterDateEqualsDate(e, d))) {
                        list.add(new Pair<Date, Long>(new Date(d.year, d.month, d.day),
                                f.apply(d)));
                    }
                }
                break;
            case MONTH:
                for (FilterDate d : dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> filterDateEqualsDate(e, d))) {
                        list.add(new Pair<Date, Long>(new Date(d.year, d.month, 1),
                                f.apply(d)));
                    }

                }
                break;
            case YEAR:
                for (FilterDate d : dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> filterDateEqualsDate(e, d))) {
                        list.add(new Pair<Date, Long>(new Date(d.year, 0, 1),
                                f.apply(d)));
                    }
                }
                break;
            case ToD:
                for (FilterDate d : dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> filterDateEqualsDate(e, d))) {
                        list.add(new Pair<Date, Long>(new Date(0,0,1,d.hours,0),
                                f.apply(d)));
                    }
                }
                break;
            case DoW:
                for (FilterDate d : dates) {
                    if (list.stream().map(Pair::getKey).noneMatch(e -> filterDateEqualsDate(e, d))) {
                        if(d.dow==0){
                            list.add(new Pair<Date, Long>(new Date(0,0,7),
                                    f.apply(d)));
                        }else {
                            list.add(new Pair<Date, Long>(new Date(0, 0, d.dow),
                                    f.apply(d)));
                        }
                    }
                }
                break;
        }
        return list;
    }

    //---------------------------------------------FILTERING AND GRANULARITY--------------------------------------------

    //sets granularity for output data.
    //0=hour, 1=day, 2=month, 3=year
    public void setGranularity(Granularity g) {
        this.granularity = g;
        setMetrics();
    }

    public Granularity getGranularity(){
        return granularity;
    }

    public void resetBounceFilters() {
        this.bouncePages = 0;
        this.bounceTime = 0;
        setMetrics();
        notifyObservers("filter");
    }

    // Sets Bounce Time
    public void setBounceTime(long ms) {
        this.bounceTime = ms;
        setMetrics();
        notifyObservers("filter");
    }

    //Sets minimum number of pages for visit to not be a bounce
    public void setBouncePageReq(int pages) {
        this.bouncePages = pages;
        setMetrics();
        notifyObservers("filter");
    }

    private void filter(){
        filterData();
        setMetrics();
        notifyObservers("filter");
    }

    //add filter
    public void addFilter(AgeFilter f, int filterID) {
        ageFilters.put(filterID, f);
        filter();
    }
    public void addFilter(ContextFilter f, int filterID) {
        contextFilters.put(filterID, f);
        filter();
    }
    public void addFilter(DateFilter f, int filterID) {
        dateFilters.put(filterID, f);
        filter();
    }
    public void addFilter(GenderFilter f, int filterID) {
        genderFilters.put(filterID, f);
        filter();
    }
    public void addFilter(IncomeFilter f, int filterID) {
        incomeFilters.put(filterID, f);
        filter();
    }

    //remove filter
    public void removeFilter(int filterID) {
        if(ageFilters.containsKey(filterID)) {
            ageFilters.remove(filterID);
        }else if(contextFilters.containsKey(filterID)){
            contextFilters.remove(filterID);
        }else if(dateFilters.containsKey(filterID)){
            dateFilters.remove(filterID);
        }else if(genderFilters.containsKey(filterID)){
            genderFilters.remove(filterID);
        }else if(incomeFilters.containsKey(filterID)){
            incomeFilters.remove(filterID);
        }
        filter();
    }

    public int getUsableID(){
        int i=1;
        while(ageFilters.keySet().contains(i) || contextFilters.keySet().contains(i) || dateFilters.keySet().contains(i) || genderFilters.keySet().contains(i) || incomeFilters.keySet().contains(i)) {
            i++;
        }
        return i;
    }

    private void filterData() {
        impressionLog.clear();
        rawDataHolder.getImpressionLog().stream()
                .filter(il -> matchesFilter(il))
                .forEach(il -> impressionLog.add(il));

        clickLog.clear();
        rawDataHolder.getClickLog().stream()
                .filter(cl -> matchesFilter(cl))
                .forEach(cl -> clickLog.add(cl));

        serverLog.clear();
        rawDataHolder.getServerLog().stream()
                .filter(sl -> matchesFilter(sl))
                .forEach(sl -> serverLog.add(sl));

        notifyObservers(IMPRESSION_LOG);
        notifyObservers(CLICK_LOG);
        notifyObservers(SERVER_LOG);
    }

    private boolean matchesFilter(Object o){
        if(o instanceof ImpressionLog){
            return (ageFilters.size()>0?ageFilters.values().stream().map(f -> f.filter((ImpressionLog)o)).reduce(false, Boolean::logicalOr):true)
             &&
             (contextFilters.size()>0?contextFilters.values().stream().map(f -> f.filter((ImpressionLog)o)).reduce(false, Boolean::logicalOr):true)
             &&
             (dateFilters.size()>0?dateFilters.values().stream().map(f -> f.filter((ImpressionLog)o)).reduce(false, Boolean::logicalOr):true)
             &&
             (genderFilters.size()>0?genderFilters.values().stream().map(f -> f.filter((ImpressionLog)o)).reduce(false, Boolean::logicalOr):true)
             &&
             (incomeFilters.size()>0?incomeFilters.values().stream().map(f -> f.filter((ImpressionLog)o)).reduce(false, Boolean::logicalOr):true);
        }else if(o instanceof ClickLog){
            return (ageFilters.size()>0?ageFilters.values().stream().map(f -> f.filter((ClickLog)o)).reduce(false, Boolean::logicalOr):true)
                    &&
                    (contextFilters.size()>0?contextFilters.values().stream().map(f -> f.filter((ClickLog)o)).reduce(false, Boolean::logicalOr):true)
                    &&
                    (dateFilters.size()>0?dateFilters.values().stream().map(f -> f.filter((ClickLog)o)).reduce(false, Boolean::logicalOr):true)
                    &&
                    (genderFilters.size()>0?genderFilters.values().stream().map(f -> f.filter((ClickLog)o)).reduce(false, Boolean::logicalOr):true)
                    &&
                    (incomeFilters.size()>0?incomeFilters.values().stream().map(f -> f.filter((ClickLog)o)).reduce(false, Boolean::logicalOr):true);
        }else if(o instanceof ServerLog){
            return (ageFilters.size()>0?ageFilters.values().stream().map(f -> f.filter((ServerLog)o)).reduce(false, Boolean::logicalOr):true)
                    &&
                    (contextFilters.size()>0?contextFilters.values().stream().map(f -> f.filter((ServerLog)o)).reduce(false, Boolean::logicalOr):true)
                    &&
                    (dateFilters.size()>0?dateFilters.values().stream().map(f -> f.filter((ServerLog)o)).reduce(false, Boolean::logicalOr):true)
                    &&
                    (genderFilters.size()>0?genderFilters.values().stream().map(f -> f.filter((ServerLog)o)).reduce(false, Boolean::logicalOr):true)
                    &&
                    (incomeFilters.size()>0?incomeFilters.values().stream().map(f -> f.filter((ServerLog)o)).reduce(false, Boolean::logicalOr):true);
        }else{
            return false;
        }
    }

    //Collects dates for granularising
    private void getDates() {
        impressionLog.stream()
                .map(ImpressionLog::getImpressionDate)
                .distinct()
                .forEach(e -> {
                    FilterDate d = new FilterDate(e.getHours(), e.getDate(), e.getMonth(), e.getYear(), e.getDay());
                    if (!dates.contains(d)) {
                        dates.add(d);
                    }
                });

        clickLog.stream()
                .map(ClickLog::getClickDate)
                .distinct()
                .forEach(e -> {
                    FilterDate d = new FilterDate(e.getHours(), e.getDate(), e.getMonth(), e.getYear(), e.getDay());
                    if (!dates.contains(d)) {
                        dates.add(d);
                    }
                });

        serverLog.stream()
                .map(ServerLog::getEntryDate)
                .distinct()
                .forEach(e -> {
                    FilterDate d = new FilterDate(e.getHours(), e.getDate(), e.getMonth(), e.getYear(), e.getDay());
                    if (!dates.contains(d)) {
                        dates.add(d);
                    }
                });

    }

    private boolean filterDateEqualsDate(Date d, FilterDate fd){
        switch(granularity){
            case HOUR:
                return d.getHours() == fd.hours && d.getDate() == fd.day && d.getMonth() == fd.month && d.getYear() == fd.year;
            case DAY:
                return d.getDate() == fd.day && d.getMonth() == fd.month && d.getYear() == fd.year;
            case MONTH:
                return d.getMonth() == fd.month && d.getYear() == fd.year;
            case YEAR:
                return  d.getYear() == fd.year;
            case ToD:
                return d.getHours()==fd.hours;
            case DoW:
                return d.getDay() == fd.dow;
        }
        return false;
    }

    private static class FilterDate {
        public int hours, day, month, year, dow;

        public FilterDate(int hours, int day, int month, int year, int dow) {
            this.hours = hours;
            this.day = day;
            this.month = month;
            this.year = year;
            this.dow=dow;
//            switch(dow){
//                case 0:
//                    this.dow="Sun";
//                    break;
//                case 1:
//                    this.dow="Mon";
//                    break;
//                case 2:
//                    this.dow="Tue";
//                    break;
//                case 3:
//                    this.dow="Wed";
//                    break;
//                case 4:
//                    this.dow="Thu";
//                    break;
//                case 5:
//                    this.dow="Fri";
//                    break;
//                case 6:
//                    this.dow="Sat";
//            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof FilterDate) {
                FilterDate d = (FilterDate) o;
                return this.hours == d.hours && this.day == d.day && this.month == d.month && this.year == d.year && this.dow == d.dow;
            }else {
                return false;
            }
        }

    }

    //------------SERVICE TASKS-----------------------------------------------------------------------------------------

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(Object arg) {
        observers.forEach(o -> o.update(arg));
    }

    @Override
    public void notifyObservers() {
        observers.forEach(Observer::update);
    }


    private void setMetrics() {
        metrics.setNoImpressions(df.format(getNumOfImpressions()));
        metrics.setNoClicks(df.format(getNumOfClicks()));
        metrics.setNoUniqueClicks(df.format(getNumOfUniqueClicks()));
        metrics.setNoConversions(df.format(getNumOfConversions()));
        metrics.setNoBounces(df.format(getNumOfBounces()));
        metrics.setBounceRate(df.format(getBounceRate()));
        metrics.setTotalCost(df.format(getTotalCost()));
        metrics.setCTR(df.format(getCTR()));
        metrics.setCPC(df.format(getClickCost()));
        metrics.setCPM(df.format(getCPM()));
        metrics.setCPA(df.format(getCPA()));
    }

    public void setChartData(Metric metric) {
        XYChart.Series<String, Number> campaignSeries = new XYChart.Series<>();

        switch (metric) {
            case NUM_OF_IMPRESSIONS:
                getNumOfImpressionsPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                campaignSeries.setName("Number of Impressions");
                chartData.setChartData(FXCollections.observableArrayList(campaignSeries));
                break;
            case NUM_OF_CLICKS:
                getNumOfClicksPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                campaignSeries.setName("Number of Clicks");
                chartData.setChartData(FXCollections.observableArrayList(campaignSeries));
                break;
            case NUM_OF_UNIQUE_CLICKS:
                getNumOfUniqueClicksPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                campaignSeries.setName("Number of Unique Clicks");
                chartData.setChartData(FXCollections.observableArrayList(campaignSeries));
                break;
            case NUM_OF_CONVERSIONS:
                getNumOfConversionsPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                campaignSeries.setName("Number of Conversions");
                chartData.setChartData(FXCollections.observableArrayList(campaignSeries));
                break;
            case NUM_OF_BOUNCES:
                getNumOfBouncesPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                campaignSeries.setName("Number of Bounces");
                chartData.setChartData(FXCollections.observableArrayList(campaignSeries));
                break;
            case BOUNCE_RATE:
                getBounceRatePair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                campaignSeries.setName("Bounce Rate");
                chartData.setChartData(FXCollections.observableArrayList(campaignSeries));
                break;
            case TOTAL_COST:
                getTotalCostPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                campaignSeries.setName("Total Cost");
                chartData.setChartData(FXCollections.observableArrayList(campaignSeries));
                break;
            case CTR:
                getCTRPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                campaignSeries.setName("Click-through-rate");
                chartData.setChartData(FXCollections.observableArrayList(campaignSeries));
                break;
            case CPC:
                getClickCostPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                campaignSeries.setName("Cost-per-click");
                chartData.setChartData(FXCollections.observableArrayList(campaignSeries));
                break;
            case CPM:
                getCPMPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                campaignSeries.setName("Cost-per-thousand Impressions");
                chartData.setChartData(FXCollections.observableArrayList(campaignSeries));
                break;
            case CPA:
                getCPAPair().forEach(e -> campaignSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue())));
                campaignSeries.setName("Cost-per-acquisition");
                chartData.setChartData(FXCollections.observableArrayList(campaignSeries));
                break;
        }
    }
}
