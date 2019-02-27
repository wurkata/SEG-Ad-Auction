import java.util.Date;

/**
 * Created by furqan on 26/02/2019.
 */
public class Record {
    private long subjectID;
    private String gender;
    private String ageRange;
    private String income;
    private String context;
    private Date impressionDate;
    private double impressionCost;
    private ClickLog clickLog;
    private ServerLog serverLog;


    public Record(Date impressionDate, long subjectID, String gender, String ageRange, String income, String context, double impressionCost){
        this.impressionDate=impressionDate;
        this.subjectID=subjectID;
        this.gender=gender;
        this.ageRange=ageRange;
        this.income=income;
        this.context=context;
        this.impressionCost=impressionCost;
    }

    public void setServerLog(ServerLog serverLog) {
        this.serverLog = serverLog;
    }

    public void setClickLog(ClickLog clickLog){
        this.clickLog=clickLog;
    }

    public long getSubjectID() {
        return subjectID;
    }

    public String getGender() {
        return gender;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public String getIncome() {
        return income;
    }

    public Date getImpressionDate() {
        return impressionDate;
    }

    public double getImpressionCost() {
        return impressionCost;
    }

    public Date getClickDate() {
        if(clickLog==null){
            return null;
        }else{
            return clickLog.getClickDate();
        }
    }

    public Double getClickCost() {
        if(clickLog==null){
            return null;
        }else{
            return clickLog.getClickCost();
        }
    }

    public Date getEntryDate(){
        if(serverLog==null){
            return null;
        }else{
            return serverLog.getEntryDate();
        }
    }

    public Date getExitDate(){
        if(serverLog==null){
            return null;
        }else{
            return serverLog.getExitDate();
        }
    }

    public Integer getPagesViewed(){
        if(serverLog==null){
            return null;
        }else{
            return serverLog.getPagesViewed();
        }
    }

    public Boolean getConversion(){
        if(serverLog==null){
            return null;
        }else{
            return serverLog.getConversion();
        }
    }

    public void printRecord() {
        if (serverLog == null) {
            System.out.println("ID:" + subjectID + "\tImpDate:" + impressionDate.toString() + "\tGender:" + gender + "\tAge:" + ageRange + "\tIncome:" + income + "\tContext:" + context + "\tImpCost:" + impressionCost + "\tClickLog:n/a\tServer:n/a");

        }else{
            System.out.println("ID:"+subjectID+"\tImpDate:"+impressionDate.toString()+"\tGender:"+gender+"\tAge:"+ageRange+"\tIncome:"+income+"\tContext:"+context+"\tImpCost:"+impressionCost+"\tClickDate:"+getClickDate().toString()+"\tClickCost:"+getClickCost()+"\tEntryDate:"+getEntryDate().toString()+"\tExitDate:"+getExitDate().toString()+"\tPagesViewed:"+getPagesViewed()+"\tConversion:"+getConversion());
        }
    }
}
