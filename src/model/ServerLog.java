package model;

import java.util.Date;

/**
 * Created by furqan on 26/02/2019.
 */
public class ServerLog {
    private String subjectID;
    private Date entryDate;
    private Date exitDate;
    private int pagesViewed;
    private int conversion;

    public ServerLog(Date entryDate, String subjectID, int pagesViewed, int conversion){
        this.subjectID=subjectID;
        this.entryDate=entryDate;
        this.pagesViewed=pagesViewed;
        this.conversion=conversion;
    }

    public ServerLog(Date entryDate, String subjectID, Date exitDate, int pagesViewed, int conversion){
        this.subjectID=subjectID;
        this.entryDate=entryDate;
        this.exitDate=exitDate;
        this.pagesViewed=pagesViewed;
        this.conversion=conversion;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public Date getExitDate() {
        return exitDate;
    }


    public int getPagesViewed() {
        return pagesViewed;
    }

    public int getConversion() {
        return conversion;
    }

}
