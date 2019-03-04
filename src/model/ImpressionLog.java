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
}
