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
    private Parser(){}


    public static ArrayList<ClickLog> readClickLog(File file)throws Exception{
        ArrayList<ClickLog> clickLog = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            Stream<String> lines = reader.lines();
            lines.skip(1)
                    .map(e->e.split(","))
                    .forEach(s->clickLog.add(new ClickLog(parseDate(s[0]), Long.parseLong(s[1]), Double.parseDouble(s[2]))));
            return clickLog;
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("Exception thrown trying to read click log");
        }
    }

    public static ArrayList<ImpressionLog> readImpressionLog(File file)throws Exception{
        ArrayList<ImpressionLog> impressionLog = new ArrayList<>();
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
        ArrayList<ServerLog> serverLog = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            Stream<String> lines = reader.lines();
            lines.skip(1)
                    .map(e->e.split(","))
                    .forEach(s->{
                        if(s[2].equals("n/a")) {
                            serverLog.add(new ServerLog(parseDate(s[0]), Long.parseLong(s[1]), Integer.parseInt(s[3]), parseBool(s[4])));
                        }else{
                            serverLog.add(new ServerLog(parseDate(s[0]), Long.parseLong(s[1]), parseDate(s[2]), Integer.parseInt(s[3]), parseBool(s[4])));
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
        return s.equals("Yes");
    }
}
