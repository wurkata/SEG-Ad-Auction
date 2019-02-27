import java.io.File;
import java.util.ArrayList;

/**
 * Created by furqan on 27/02/2019.
 */
public class Model {

    private ArrayList<ImpressionLog> impressions;
    private ArrayList<ClickLog> clicks;
    private ArrayList<ServerLog> server;

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

    //Cost per click
    //assuming cost is sum of impression costs (need to confirm as this does not take into account click cost)
    public double getClickCost(){
        long numOfClicks = clicks.size();

        double totalCost = impressions.parallelStream()
                .mapToDouble(e->e.getImpressionCost())
                .sum();

        return (totalCost/numOfClicks);
    }

    //Cost per 1000 impressions
    public double getCPM(){
        long numOfRecords = impressions.size();

        double totalCost = impressions.parallelStream()
                .mapToDouble(ImpressionLog::getImpressionCost)
                .sum();

        return (totalCost/numOfRecords)*1000.0;
    }

    //Click through rate
    public double getCTR(){
        long numOfImpressions = impressions.size();

        long numOfClicks = clicks.size();

        return ((double) numOfClicks/numOfImpressions);
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
                .mapToLong(e->e.getSubjectID())
                .distinct()
                .count();
    }

}
