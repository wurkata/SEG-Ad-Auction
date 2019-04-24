package common.Filters;

import model.User;

import java.util.List;

/**
 * Created by furqan on 12/03/2019.
 */
public abstract class AudienceFilter extends Filter {
    List<User> users;

    public AudienceFilter(List<User> users){
        this.users=users;
    }
}
