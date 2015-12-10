package common.domain;

import common.util.Interval;

import java.io.Serializable;

public class Booking extends AbstractAssignment implements Serializable{

    public Booking() {}

    public Booking(Car car, Client client, Interval interval) {
        super(car, client, interval);
    }

    public Booking(long id, Car car, Client client, Interval interval) {
        super(id, car, client, interval);
    }
}
