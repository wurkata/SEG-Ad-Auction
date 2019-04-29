package common.Filters;

import model.ClickLog;
import model.ImpressionLog;
import model.ServerLog;
import model.Subject;

import java.util.Map;

public class AgeFilter extends AudienceFilter {

    private int ageRange;

    public AgeFilter(int ageRange, Map<String, Subject> subjects) {
        super(subjects);
        this.ageRange = ageRange;
    }


    @Override
    public boolean filter(ImpressionLog i) {
        return true;
        // return subjects.get(i.getSubjectID()).getAgeRange() == ageRange;
    }

    @Override
    public boolean filter(ClickLog c) {
        return true;
        // return subjects.get(c.getSubjectID()).getAgeRange() == ageRange;
    }

    @Override
    public boolean filter(ServerLog s) {
        return true;
        // return subjects.get(s.getSubjectID()).getAgeRange() == ageRange;
    }
}