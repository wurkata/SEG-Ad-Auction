package common.Filters;

import model.ClickLog;
import model.ImpressionLog;
import model.ServerLog;

import java.util.Date;

/**
 * Created by furqan on 12/03/2019.
 */
public class DateFilter extends Filter {
    //start and end ranges are inclusive
    private Date startRange;
    private Date endRange;

    public DateFilter(Date startRange, Date endRange){
        this.startRange=startRange;
        this.endRange=endRange;
    }

    @Override
    public boolean filter(ImpressionLog i) {
        return withinRange(i.getImpressionDate());

    }

    @Override
    public boolean filter(ClickLog c) {
        return withinRange(c.getClickDate());
    }

    @Override
    public boolean filter(ServerLog s) {
        return withinRange(s.getEntryDate());
    }


    private boolean withinRange(Date d){
        if(startRange!=null){
            int i = d.compareTo(startRange);
            if(i<0){
                return false;
            }
        }

        if(endRange !=null){
            int i = d.compareTo(endRange);
            if(i>0){
                return false;
            }
        }
        return true;
    }
}
