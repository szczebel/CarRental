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

    public boolean overlaps(Interval that) {
        if (that.to.isBefore(this.from)) return false;
        if (that.from().isAfter(this.to)) return false;
        return true;
    }
}
