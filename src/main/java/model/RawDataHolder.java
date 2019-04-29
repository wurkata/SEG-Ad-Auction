package model;

import common.FileType;
import common.Observable;
import common.Observer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by furqan on 24/04/2019.
 */
public class RawDataHolder implements Observable {
    private List<ImpressionLog> impressionLog;
    private List<ClickLog> clickLog;
    private List<ServerLog> serverLog;
    private Map<String, Subject> subjects;

    public RawDataHolder(){
    }

    public List<ImpressionLog> getImpressionLog() {
        return impressionLog;
    }


    public List<ClickLog> getClickLog() {
        return clickLog;
    }


    public List<ServerLog> getServerLog() {
        return serverLog;
    }

    public Map<String, Subject> getSubjects() {
        return subjects;
    }

    public void setImpressionLog(List<ImpressionLog> impressionLog) {
        this.impressionLog = impressionLog;
        notifyObservers(FileType.IMPRESSION_LOG);
    }

    public void setClickLog(List<ClickLog> clickLog) {
        this.clickLog = clickLog;
        notifyObservers(FileType.CLICK_LOG);
    }

    public void setServerLog(List<ServerLog> serverLog) {
        this.serverLog = serverLog;
        notifyObservers(FileType.SERVER_LOG);
    }

    public void setSubjects(Map<String, Subject> subjects) {
        this.subjects = subjects;
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
