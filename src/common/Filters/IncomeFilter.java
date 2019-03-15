package common.Filters;

import model.ClickLog;
import model.ImpressionLog;
import model.ServerLog;
import model.SubjectLog;

import java.util.HashMap;

/**
 * Created by furqan on 12/03/2019.
 */
public class IncomeFilter extends AudienceFilter {

    private String income="";
    public IncomeFilter(String income, HashMap<String, SubjectLog> subjects){
        super(subjects);
        this.income=income;
    }


    @Override
    public boolean filter(ImpressionLog i) {
        return subjects.get(i.getSubjectID()).getIncome().equals(income);
    }

    @Override
    public boolean filter(ClickLog c) {
        return subjects.get(c.getSubjectID()).getIncome().equals(income);
    }

    @Override
    public boolean filter(ServerLog s) {
        return subjects.get(s.getSubjectID()).getIncome().equals(income);
    }
}
