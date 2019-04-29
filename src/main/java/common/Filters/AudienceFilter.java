package common.Filters;

import model.Subject;

import java.util.List;
import java.util.Map;

/**
 * Created by furqan on 12/03/2019.
 */
public abstract class AudienceFilter extends Filter {
    Map<String, Subject> subjects;

    public AudienceFilter(Map<String, Subject> subjects){
        this.subjects = subjects;
    }
}
