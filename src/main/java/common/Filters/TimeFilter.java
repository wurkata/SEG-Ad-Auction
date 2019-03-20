package common.Filters;

import model.ClickLog;
import model.ImpressionLog;
import model.ServerLog;

public class TimeFilter extends Filter {
    private Long startTime;
    private Long endTime;

    public TimeFilter(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean filter(ImpressionLog i) {
        return withinRange(i.getImpressionDate().getTime());
    }

    public boolean filter(ClickLog c) {
        return withinRange(c.getClickDate().getTime());
    }

    public boolean filter(ServerLog s) {
        return withinRange(s.getEntryDate().getTime());
    }

    private boolean withinRange(long currentTime) {
        return !(startTime != null && startTime > currentTime || endTime != null && endTime < currentTime);
    }
}
