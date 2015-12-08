package common.domain;

import common.util.Interval;

import java.io.Serializable;
import java.time.Duration;
import java.time.ZonedDateTime;

public class AbstractAssignment implements Serializable {
    protected final Car car;
    protected final Client client;
    protected final Interval interval;

    public AbstractAssignment() {
        this(null, null, null);
    }

    public AbstractAssignment(Car car, Client client, Interval interval) {
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

    public ZonedDateTime getStart() {
        return interval.from();
    }

    public ZonedDateTime getEnd() {
        return interval.to();
    }

    public String getRegistration() {
        return car.getRegistration();
    }

    public String getModel() {
        return car.getModel();
    }

    public String getRentalClassName() {
        return car.getRentalClassName();
    }

    public String getClientName() {
        return client.getName();
    }

    public String getClientEmail() {
        return client.getEmail();
    }

    public Duration getDuration() {
        return Duration.between(getStart(), getEnd());
    }

    public double getHourlyRate() {
        return car.getHourlyRate();
    }
}
