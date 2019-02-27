import java.io.File;
import java.util.ArrayList;

/**
 * Created by furqan on 27/02/2019.
 */
public class Model {

    private ArrayList<Record> records;

    public Model(File impressions, File clicks, File server){
        try {
            records = Parser.readFiles(impressions,clicks,server);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error reading input files");
        }
    }

    //Cost per click
    public double getClickCost(){
        long numOfRecords = records.stream()
                .filter(e->e.hasServerLog())
                .count();

        double totalCost = records.parallelStream()
                .filter(e->e.hasServerLog())
                .mapToDouble(Record::getClickCost)
                .sum();

        return (totalCost/numOfRecords);
    }
}
