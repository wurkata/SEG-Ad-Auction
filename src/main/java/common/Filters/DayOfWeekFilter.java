package common.Filters;

import model.ClickLog;
import model.ImpressionLog;
import model.ServerLog;

public class DayOfWeekFilter extends Filter {
    private Integer start;
    private Integer finish;

    public DayOfWeekFilter(int start, int finish) {
        this.start = start;
        this.finish = finish;
    }

    public boolean filter(ImpressionLog i) {
        return withinRange(i.getImpressionDate().getDay());
    }

    public boolean filter(ClickLog c) {
        return withinRange(c.getClickDate().getDay());
    }

    public boolean filter(ServerLog s) {
        return withinRange(s.getEntryDate().getDay());
    }

    private boolean withinRange(int current) {
        return !(this.start != null && start > current || this.finish != null && finish < current);
    }
}
