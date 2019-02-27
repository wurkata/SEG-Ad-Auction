import java.util.Date;

/**
 * Created by furqan on 26/02/2019.
 */
public class ClickLog {
    private Date clickDate;
    private double clickCost;

    public ClickLog(Date clickDate, double clickCost){
        this.clickDate=clickDate;
        this.clickCost=clickCost;
    }

    public Date getClickDate() {
        return clickDate;
    }

    public double getClickCost() {
        return clickCost;
    }
}
