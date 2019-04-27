package common.Filters;

import model.ClickLog;
import model.ImpressionLog;
import model.ServerLog;
import model.Subject;

import java.util.List;

/**
 * Created by furqan on 12/03/2019.
 */
public class GenderFilter extends AudienceFilter{
    private String gender = "";

    public GenderFilter(String gender, List<Subject> subjects){
        super(subjects);
        this.gender=gender;
    }

    @Override
    public boolean filter(ImpressionLog i) {
        // return subjects.get(i.getSubjectID()).getGender().equals(gender);
        return true;
    }

    @Override
    public boolean filter(ClickLog c) {
        // return subjects.get(c.getSubjectID()).getGender().equals(gender);
        return true;

    }

    @Override
    public boolean filter(ServerLog s) {
        // return subjects.get(s.getSubjectID()).getGender().equals(gender);
        return true;
    }
}
