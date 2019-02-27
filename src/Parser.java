import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by furqan on 26/02/2019.
 */
public class Parser {
    private static HashMap<Long, String> impressionLog = new HashMap<>();
    private static HashMap<Long, String> clickLog = new HashMap<>();
    private static HashMap<Long, String> serverLog = new HashMap<>();

    private Parser(){}

    public static ArrayList<Record> readFiles(File impressions, File clicks, File server) throws Exception{
        ArrayList<Record> records = new ArrayList<>();

        readImpressionLog(impressions);
        readClickLog(clicks);
        readServerLog(server);
        Set<Long> ids = impressionLog.keySet();

        if(!ids.containsAll(clickLog.keySet())){
            throw new Exception("Mismatch between IDs in click log and impression log");
        }else{
            if(!serverLog.keySet().equals(clickLog.keySet())){
                throw new Exception("Server log contains IDs not found in click log");
            }
        }

        for(Long l:ids){

            String[] iLog = impressionLog.get(l).split(",");


            Record record = new Record(parseDate(iLog[0]),l,iLog[1],iLog[2],iLog[3],iLog[4],Double.parseDouble(iLog[5]));

            if(clickLog.containsKey(l)){
                String[] cLog = clickLog.get(l).split(",");
                ClickLog clickLog = new ClickLog(parseDate(cLog[0]),Double.parseDouble(cLog[1]));
                record.setClickLog(clickLog);

                String[] sLog = serverLog.get(l).split(",");
                ServerLog serverLog;
                if(sLog[1].equals("n/a")) {
                    serverLog = new ServerLog(parseDate(sLog[0]),Integer.parseInt(sLog[2]), parseBool(sLog[3]));
                }else{
                    serverLog = new ServerLog(parseDate(sLog[0]),parseDate(sLog[1]),Integer.parseInt(sLog[2]), parseBool(sLog[3]));
                }
                record.setServerLog(serverLog);
            }

            records.add(record);
        }
        return records;
    }


    private static void readClickLog(File file){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            Stream<String> lines = reader.lines();
            lines.skip(1)
                .map(e->e.split(","))
                    .forEach(s->clickLog.put(Long.parseLong(s[1]), (s[0]+","+s[2])));


        }catch(Exception e){
            e.printStackTrace();
            System.err.println("Exception thrown trying to read click log");
        }
    }

    private static void readImpressionLog(File file){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            Stream<String> lines = reader.lines();
            lines.skip(1)
                    .map(e->e.split(","))
                    .forEach(s->impressionLog.put(Long.parseLong(s[1]), (s[0]+","+s[2]+","+s[3]+","+s[4]+","+s[5])+","+s[6]));


        }catch(Exception e){
            e.printStackTrace();
            System.err.println("Exception thrown trying to read impression log");
        }
    }

    private static void readServerLog(File file){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            Stream<String> lines = reader.lines();
            lines.skip(1)
                    .map(e->e.split(","))
                    .forEach(s->serverLog.put(Long.parseLong(s[1]), (s[0]+","+s[2]+","+s[3]+","+s[4])));


        }catch(Exception e){
            e.printStackTrace();
            System.err.println("Exception thrown trying to read server log");
        }
    }

    private static Date parseDate(String d){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
            System.err.println("Error parsing date");
        }
        return parsedDate;
    }

    private static boolean parseBool(String s){
        if(s.equals("Yes")){
            return true;
        }else{
            return false;
        }
    }
}
