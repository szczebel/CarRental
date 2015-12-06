package common.domain;

import common.util.Interval;

import java.time.Duration;
import java.time.ZonedDateTime;

public class HistoricalRental {
    private final Car car;
    private final Client client;
    private final Interval interval;

    public HistoricalRental(CurrentRental currentRental, ZonedDateTime end) {
        this.car = currentRental.getCar();
        this.client = currentRental.getClient();
        this.interval = new Interval(currentRental.getStart(), end);
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
