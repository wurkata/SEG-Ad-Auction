import java.util.Date;

/**
 * Created by furqan on 26/02/2019.
 */
public class ServerLog {
    private Date entryDate;
    private Date exitDate;
    private int pagesViewed;
    private boolean conversion;

    public ServerLog(Date entryDate, int pagesViewed, boolean conversion){
        this.entryDate=entryDate;
        this.pagesViewed=pagesViewed;
        this.conversion=conversion;
    }

    public ServerLog(Date entryDate, Date exitDate, int pagesViewed, boolean conversion){
        this.entryDate=entryDate;
        this.exitDate=exitDate;
        this.pagesViewed=pagesViewed;
        this.conversion=conversion;
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
