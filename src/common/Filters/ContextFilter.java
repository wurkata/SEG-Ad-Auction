package common.Filters;

import model.ClickLog;
import model.ImpressionLog;
import model.ServerLog;

/**
 * Created by furqan on 12/03/2019.
 */
public class ContextFilter extends Filter {
    //need to ask supervisor about how to match impression log record with click and server records to filter them
    private String context="";

    public ContextFilter(String context){
        this.context=context;
    }


    @Override
    public boolean filter(ImpressionLog i) {
        return i.getContext().equals(context);
    }

    @Override
    public boolean filter(ClickLog c) {
        return true;
    }

    @Override
    public boolean filter(ServerLog s) {
        return true;
    }
}
