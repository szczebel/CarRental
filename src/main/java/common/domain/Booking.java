package common.domain;

import common.util.Interval;

public class Booking {
    private final Car car;
    private final Client client;
    private final Interval interval;

    public Booking(Car car, Client client, Interval interval) {
        this.car = car;
        this.client = client;
        this.interval = interval;
    }

    Car getCar() {
        return car;
    }

    Client getClient() {
        return client;
    }

    public Interval getInterval() {
        return interval;
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
}
