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
    private static ArrayList<ImpressionLog> impressionLog = new ArrayList<>();
    private static ArrayList<ClickLog> clickLog = new ArrayList<>();
    private static ArrayList<ServerLog> serverLog = new ArrayList<>();

    private Parser(){}

//    public static ArrayList<ImpressionLog> readFiles(File impressions, File clicks, File server) throws Exception{
//        ArrayList<ImpressionLog> records = new ArrayList<>();
//
//        readImpressionLog(impressions);
//        readClickLog(clicks);
//        readServerLog(server);
//        Set<Long> ids = impressionLog.keySet();
//
//        if(!ids.containsAll(clickLog.keySet())){
//            throw new Exception("Mismatch between IDs in click log and impression log");
//        }else{
//            if(!serverLog.keySet().equals(clickLog.keySet())){
//                throw new Exception("Server log contains IDs not found in click log");
//            }
//        }
//
//        for(Long l:ids){
//
//            String[] iLog = impressionLog.get(l).split(",");
//
//
//            ImpressionLog record = new ImpressionLog(parseDate(iLog[0]),l,iLog[1],iLog[2],iLog[3],iLog[4],Double.parseDouble(iLog[5]));
//
//            if(clickLog.containsKey(l)){
//                String[] cLog = clickLog.get(l).split(",");
//                ClickLog clickLog = new ClickLog(parseDate(cLog[0]),Double.parseDouble(cLog[1]));
//                record.setClickLog(clickLog);
//
//                String[] sLog = serverLog.get(l).split(",");
//                ServerLog serverLog;
//                if(sLog[1].equals("n/a")) {
//                    serverLog = new ServerLog(parseDate(sLog[0]),Integer.parseInt(sLog[2]), parseBool(sLog[3]));
//                }else{
//                    serverLog = new ServerLog(parseDate(sLog[0]),parseDate(sLog[1]),Integer.parseInt(sLog[2]), parseBool(sLog[3]));
//                }
//                record.setServerLog(serverLog);
//            }
//
//            records.add(record);
//        }
//        return records;
//    }


    public static ArrayList<ClickLog> readClickLog(File file)throws Exception{
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            Stream<String> lines = reader.lines();
            lines.skip(1)
                    .map(e->e.split(","))
                    .forEach(s->clickLog.add(new ClickLog(Long.parseLong(s[1]),parseDate(s[0]),Double.parseDouble(s[2]))));
            return clickLog;
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("Exception thrown trying to read click log");
        }
    }

    public static ArrayList<ImpressionLog> readImpressionLog(File file)throws Exception{
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            reader.lines()
                    .skip(1)
                    .map(e->e.split(","))
                    .forEach(s->impressionLog.add(new ImpressionLog(parseDate(s[0]), Long.parseLong(s[1]), s[2],s[3],s[4],s[5],Double.parseDouble(s[6]))));
            return impressionLog;
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("Exception thrown trying to read impression log");
        }
    }

    public static ArrayList<ServerLog> readServerLog(File file)throws Exception{
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            Stream<String> lines = reader.lines();
            lines.skip(1)
                    .map(e->e.split(","))
                    .forEach(s->{
                        if(s[2].equals("n/a")) {
                            serverLog.add(new ServerLog(Long.parseLong(s[1]),parseDate(s[0]),Integer.parseInt(s[3]), parseBool(s[4])));
                        }else{
                            serverLog.add(new ServerLog(Long.parseLong(s[1]),parseDate(s[0]),parseDate(s[2]),Integer.parseInt(s[3]), parseBool(s[4])));
                        }
                    });
            return serverLog;
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("Exception thrown trying to read server log");
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
