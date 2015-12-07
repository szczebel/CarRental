package common.domain;

import common.util.Interval;

import java.time.ZonedDateTime;

public class CurrentRental extends AbstractAssignment {

    public CurrentRental(Car car, Client client, ZonedDateTime start, ZonedDateTime plannedEnd) {
        super(car, client, new Interval(start, plannedEnd));
    }

    public ZonedDateTime getPlannedEnd() {
        return getEnd();
    }
}
