package common.domain;

import common.util.Interval;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class CurrentRental extends AbstractAssignment implements Serializable{

    //for serialization only
    public CurrentRental() {}

    public CurrentRental(Car car, Client client, ZonedDateTime start, ZonedDateTime plannedEnd) {
        super(car, client, new Interval(start, plannedEnd));
    }

    public CurrentRental(Car car, Client client, Interval interval) {
        super(car, client, interval);
    }

    public CurrentRental(long id, Car car, Client client, Interval interval) {
        super(id, car, client, interval);
    }

    public ZonedDateTime getPlannedEnd() {
        return getEnd();
    }
}
