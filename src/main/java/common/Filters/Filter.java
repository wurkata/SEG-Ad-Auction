package common.Filters;

import model.ClickLog;
import model.ImpressionLog;
import model.ServerLog;

/**
 * Created by furqan on 12/03/2019.
 */
public abstract class Filter {

    public abstract boolean filter(ImpressionLog i);

    public abstract boolean filter(ClickLog c);

    public abstract boolean filter(ServerLog s);

}
