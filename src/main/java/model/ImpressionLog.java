package model;


import java.util.Date;

/**
 * Created by furqan on 26/02/2019.
 */
public class ImpressionLog {
    private String subjectID;
    private Date impressionDate;
    private String context;
    private double impressionCost;

    public ImpressionLog(Date impressionDate, String subjectID,  String context, double impressionCost){
        this.impressionDate=impressionDate;
        this.subjectID=subjectID;
        this.context=context;
        this.impressionCost=impressionCost;
    }

    private ImpressionLog(double impressionCost){
        this.impressionCost=impressionCost;
    }

    public String getSubjectID(){
        return subjectID;
    }

    public Date getImpressionDate() {
        return impressionDate;
    }

    public double getImpressionCost() {
        return impressionCost;
    }

    public String getContext() {
        return context;
    }

    public static ImpressionLog addImpressionLogs(ImpressionLog i1, ImpressionLog i2){
        return new ImpressionLog(i1.getImpressionCost()+i2.getImpressionCost());
    }
}
