package common.Filters;

import model.ClickLog;
import model.ImpressionLog;
import model.ServerLog;
import model.User;

import java.util.List;

/**
 * Created by furqan on 12/03/2019.
 */
public class IncomeFilter extends AudienceFilter {

    private String income="";
    public IncomeFilter(String income, List<User> users){
        super(users);
        this.income=income;
    }


    @Override
    public boolean filter(ImpressionLog i) {
        // return users.get(i.getSubjectID()).getIncome().equals(income);
        return true;
    }

    @Override
    public boolean filter(ClickLog c) {
        // return subjects.get(c.getSubjectID()).getIncome().equals(income);
        return true;
    }

    @Override
    public boolean filter(ServerLog s) {
        // return subjects.get(s.getSubjectID()).getIncome().equals(income);
        return true;
    }
}
