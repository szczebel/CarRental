package common.util;

import java.time.ZonedDateTime;

public class Interval {

    final ZonedDateTime from, to;

    public Interval(ZonedDateTime from, ZonedDateTime to) {
        this.from = from;
        this.to = to;
    }

    public ZonedDateTime from() {
        return from;
    }

    public ZonedDateTime to() {
        return to;
    }
}
