package model;

import java.util.HashMap;
import java.util.List;

/**
 * Created by furqan on 24/04/2019.
 */
public class RawDataHolder {
    private List<ImpressionLog> impressionLog;
    private List<ClickLog> clickLog;
    private List<ServerLog> serverLog;
    private HashMap<String, SubjectLog> subjects;

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

    public HashMap<String, SubjectLog> getSubjects() {
        return subjects;
    }

    public void setImpressionLog(List<ImpressionLog> impressionLog) {
        this.impressionLog = impressionLog;
    }

    public void setClickLog(List<ClickLog> clickLog) {
        this.clickLog = clickLog;
    }

    public void setServerLog(List<ServerLog> serverLog) {
        this.serverLog = serverLog;
    }

    public void setSubjects(HashMap<String, SubjectLog> subjects) {
        this.subjects = subjects;
    }
}
