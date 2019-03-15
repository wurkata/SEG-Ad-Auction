package common.Filters;

import model.ClickLog;
import model.ImpressionLog;
import model.ServerLog;
import model.SubjectLog;

import java.util.HashMap;

public class AgeFilter extends AudienceFilter {

    private int ageRange;

    public AgeFilter(int ageRange, HashMap<String, SubjectLog> subjects) {
        super(subjects);
        this.ageRange = ageRange;
    }


    @Override
    public boolean filter(ImpressionLog i) {
        return subjects.get(i.getSubjectID()).getAgeRange() == ageRange;
    }

    @Override
    public boolean filter(ClickLog c) {
        return subjects.get(c.getSubjectID()).getAgeRange() == ageRange;
    }

    @Override
    public boolean filter(ServerLog s) {
        return subjects.get(s.getSubjectID()).getAgeRange() == ageRange;
    }
}