package common.domain;

import common.util.Interval;

public class Booking extends AbstractAssignment{

    public Booking(Car car, Client client, Interval interval) {
        super(car, client, interval);
    }
}
