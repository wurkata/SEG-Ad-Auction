package model;

import common.FileType;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.util.Pair;

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
public class Parser extends Task<Void> {

    private File inputFile;
    private FileType fileType;
    private Model model;

    public Parser(Model model, File inputFile, FileType fileType) {
        this.model = model;
        this.inputFile = inputFile;
        this.fileType = fileType;
    }

    public ArrayList<ClickLog> readClickLog(File file) throws Exception {
        ArrayList<ClickLog> clickLog = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            Stream<String> lines = reader.lines();
            lines.skip(1)
                    .map(e -> e.split(","))
                    .forEach(s -> clickLog.add(new ClickLog(parseDate(s[0]), s[1], Double.parseDouble(s[2]))));

            model.setClickLog(clickLog);

            return clickLog;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error reading click log: \nPlease check the file is in the correct format and try again.");
        }
    }

    public Pair<ArrayList<ImpressionLog>, HashMap<String, SubjectLog>> readImpressionLog(File file) throws Exception {
        ArrayList<ImpressionLog> impressionLog = new ArrayList<>();
        HashMap<String, SubjectLog> subjects = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            reader.lines()
                    .skip(1)
                    .map(e -> e.split(","))
                    .forEach(s -> {
                        impressionLog.add(new ImpressionLog(parseDate(s[0]), s[1], s[5], Double.parseDouble(s[6])));
                        subjects.put(s[1], new SubjectLog(s[2], parseAge(s[3]), s[4]));
                    });

            model.setImpressionLog(impressionLog);

            return new Pair<>(impressionLog, subjects);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error reading impression log: \nPlease check the file is in the correct format and try again.");
        }
    }

    public ArrayList<ServerLog> readServerLog(File file) throws Exception {
        ArrayList<ServerLog> serverLog = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            Stream<String> lines = reader.lines();
            lines.skip(1)
                    .map(e -> e.split(","))
                    .forEach(s -> {
                        if (s[2].equals("n/a")) {
                            serverLog.add(new ServerLog(parseDate(s[0]), s[1], Integer.parseInt(s[3]), parseBool(s[4])));
                        } else {
                            serverLog.add(new ServerLog(parseDate(s[0]), s[1], parseDate(s[2]), Integer.parseInt(s[3]), parseBool(s[4])));
                        }
                    });

            model.setServerLog(serverLog);

            return serverLog;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error reading server log: \nPlease check the file is in the correct format and try again.");
        }

    }

    private static Date parseDate(String d) {
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

    private static int parseBool(String s) {
        return s.equals("Yes") ? 1 : 0;
    }

    public static int parseAge(String age) {
        switch (age) {
            case "<25":
                return 1;
            case "25-34":
                return 2;
            case "35-44":
                return 3;
            case "45-54":
                return 4;
            case ">54":
                return 5;
            default:
                return 0;
        }
    }
    public static String parseAge(int age) {
        switch (age) {
            case 1:
                return "<25";
            case 2:
                return "25-34";
            case 3:
                return "35-44";
            case 4:
                return "45-54";
            case 5:
                return ">54";
            default:
                return "<25";
        }
    }

    @Override
    protected Void call() throws Exception {
        Platform.runLater(() -> {
            try {
                if (fileType == FileType.IMPRESSION_LOG) readImpressionLog(inputFile);
                if (fileType == FileType.CLICK_LOG) readClickLog(inputFile);
                if (fileType == FileType.SERVER_LOG) readServerLog(inputFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return null;
    }
}