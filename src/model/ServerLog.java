package model;

import java.util.Date;

/**
 * Created by furqan on 26/02/2019.
 */
public class ServerLog {
    private long subjectID;
    private Date entryDate;
    private Date exitDate;
    private int pagesViewed;
    private boolean conversion;

    public ServerLog(Date entryDate, long subjectID, int pagesViewed, boolean conversion){
        this.subjectID=subjectID;
        this.entryDate=entryDate;
        this.pagesViewed=pagesViewed;
        this.conversion=conversion;
    }

    public ServerLog(Date entryDate, long subjectID, Date exitDate, int pagesViewed, boolean conversion){
        this.subjectID=subjectID;
        this.entryDate=entryDate;
        this.exitDate=exitDate;
        this.pagesViewed=pagesViewed;
        this.conversion=conversion;
    }

    public long getSubjectID() {
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


    public boolean getConversion() {
        return conversion;
    }

}
