package common.Filters;

import model.ClickLog;
import model.ImpressionLog;
import model.ServerLog;
import model.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by furqan on 12/03/2019.
 */
public class GenderFilter extends AudienceFilter{
    private String gender = "";

    public GenderFilter(String gender, List<User> users){
        super(users);
        this.gender=gender;
    }

    @Override
    public boolean filter(ImpressionLog i) {
        // return users.get(i.getSubjectID()).getGender().equals(gender);
        return true;
    }

    @Override
    public boolean filter(ClickLog c) {
        // return users.get(c.getSubjectID()).getGender().equals(gender);
        return true;

    }

    @Override
    public boolean filter(ServerLog s) {
        // return subjects.get(s.getSubjectID()).getGender().equals(gender);
        return true;
    }
}
