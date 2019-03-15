package common.Filters;

import model.SubjectLog;

import java.util.HashMap;

/**
 * Created by furqan on 12/03/2019.
 */
public abstract class AudienceFilter extends Filter {
     HashMap<String,SubjectLog> subjects;

    public AudienceFilter(HashMap<String,SubjectLog> subjects){
        this.subjects=subjects;
    }
}
