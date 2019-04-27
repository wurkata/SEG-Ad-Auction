package common.Filters;

import model.Subject;

import java.util.List;

/**
 * Created by furqan on 12/03/2019.
 */
public abstract class AudienceFilter extends Filter {
    List<Subject> subjects;

    public AudienceFilter(List<Subject> subjects){
        this.subjects = subjects;
    }
}
