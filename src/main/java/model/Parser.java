package model;

import common.FileType;
import common.Observable;
import common.Observer;
import javafx.concurrent.Task;
import model.DAO.UsersDAO;

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
public class Parser extends Task<Boolean> implements Observable {

    private File inputFile;
    private FileType fileType;
    private Model model;

    public Parser(Model model, File imp, File click, File serv) {
        this.model = model;
        try {
            readImpressionLog(imp);
            readClickLog(click);
            readServerLog(serv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Parser(Model model) {
        this.model = model;
    }

    public void setFile(File file, FileType fileType) {
        this.inputFile = file;
        this.fileType = fileType;
    }

    private void readClickLog(File file) throws Exception {
        ArrayList<ClickLog> clickLog = new ArrayList<>();
        List<User> users = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            Stream<String> lines = reader.lines();
            lines.skip(1)
                    .map(e -> e.split(","))
                    .forEach(s -> {
                        clickLog.add(new ClickLog(parseDate(s[0]), s[1], Double.parseDouble(s[2])));
                        users.add(new User(s[1], null, null, null));
                    });

            model.setClickLog(clickLog);
            model.setUsers(users);
            UsersDAO usersDAO = new UsersDAO(users);
            new Thread(usersDAO).start();
            // model.uploadData(FileType.CLICK_LOG);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error reading click log: \nPlease check the file is in the correct format and try again.");
        }
    }

    private void readImpressionLog(File file) throws Exception {
        ArrayList<ImpressionLog> impressionLog = new ArrayList<>();
        List<User> users = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            reader.lines()
                    .skip(1)
                    .map(e -> e.split(","))
                    .forEach(s -> {
                        impressionLog.add(new ImpressionLog(parseDate(s[0]), s[1], s[5], Double.parseDouble(s[6])));
                        users.add(new User(s[1], s[2], (s[3]), s[4]));
                    });

            model.setImpressionLog(impressionLog);
            model.setUsers(users);
            UsersDAO usersDAO = new UsersDAO(users);
            new Thread(usersDAO).start();
            // model.uploadData(FileType.IMPRESSION_LOG);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error reading impression log: \nPlease check the file is in the correct format and try again.");
        }
    }

    private void readServerLog(File file) throws Exception {
        ArrayList<ServerLog> serverLog = new ArrayList<>();
        List<User> users = new ArrayList<>();

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

                        users.add(new User(s[1], null, null, null));
                    });

            model.setServerLog(serverLog);
            model.setUsers(users);
            UsersDAO usersDAO = new UsersDAO(users);
            new Thread(usersDAO).start();
            model.uploadData(FileType.SERVER_LOG);
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

    /*
    private static int parseAge(String age) {
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
    */

    @Override
    protected Boolean call() throws Exception {
        if (fileType == FileType.IMPRESSION_LOG) readImpressionLog(inputFile);
        if (fileType == FileType.CLICK_LOG) readClickLog(inputFile);
        if (fileType == FileType.SERVER_LOG) readServerLog(inputFile);

        return true;
    }

    @Override
    protected void succeeded() {
        notifyObservers("FILES_LOADED");
    }

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
}
