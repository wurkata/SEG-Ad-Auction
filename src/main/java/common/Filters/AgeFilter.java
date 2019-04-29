package common.Filters;

import model.ClickLog;
import model.ImpressionLog;
import model.ServerLog;
import model.Subject;

import java.util.Map;

public class AgeFilter extends AudienceFilter {

    private String ageRange;

    public AgeFilter(String ageRange, Map<String, Subject> subjects) {
        super(subjects);
        this.ageRange = ageRange;
    }


    @Override
    public boolean filter(ImpressionLog i) {
         return subjects.get(i.getSubjectID()).getAgeRange().equals(ageRange);
    }

    @Override
    public boolean filter(ClickLog c) {
         return subjects.get(c.getSubjectID()).getAgeRange().equals(ageRange);
    }

    @Override
    public boolean filter(ServerLog s) {
         return subjects.get(s.getSubjectID()).getAgeRange().equals(ageRange);
    }
}