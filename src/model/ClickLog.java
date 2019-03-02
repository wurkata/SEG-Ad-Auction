package model;

import java.util.Date;

/**
 * Created by furqan on 26/02/2019.
 */
public class ClickLog {
    private String subjectID;
    private Date clickDate;
    private double clickCost;

    public ClickLog( Date clickDate, String subjectID, double clickCost){
        this.subjectID=subjectID;
        this.clickDate=clickDate;
        this.clickCost=clickCost;
    }

    public String getSubjectID(){
        return subjectID;
    }

    public Date getClickDate() {
        return clickDate;
    }

    public double getClickCost() {
        return clickCost;
    }
}
