package common.domain;

import common.util.Interval;

import java.time.ZonedDateTime;

public class CurrentRental {
    private final Car car;
    private final Client client;
    private final Interval interval;

    public CurrentRental(Car car, Client client, ZonedDateTime start, ZonedDateTime plannedEnd) {
        this.car = car;
        this.client = client;
        interval = new Interval(start, plannedEnd);
    }

    Car getCar() {
        return car;
    }

    Client getClient() {
        return client;
    }

    public ZonedDateTime getStart() {
        return interval.from();
    }

    public String getRegistration() {
        return car.getRegistration();
    }

    public String getModel() {
        return car.getModel();
    }

    public String getClientName() {
        return client.getName();
    }

    public String getClientEmail() {
        return client.getEmail();
    }

    public ZonedDateTime getPlannedEnd() {
        return interval.to();
    }
}
