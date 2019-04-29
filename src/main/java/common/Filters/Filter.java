package common.Filters;

import model.ClickLog;
import model.ImpressionLog;
import model.ServerLog;

/**
 * Created by furqan on 12/03/2019.
 */
public abstract class Filter {
    private String filterName = "";

    public String getFilterName(){
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public abstract boolean filter(ImpressionLog i);

    public abstract boolean filter(ClickLog c);

    public abstract boolean filter(ServerLog s);

}
