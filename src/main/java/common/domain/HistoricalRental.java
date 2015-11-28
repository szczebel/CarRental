package common.domain;

import java.time.ZonedDateTime;

public class HistoricalRental {
    private final Car car;
    private final Client client;
    private final ZonedDateTime start;
    private final ZonedDateTime end;

    public HistoricalRental(CurrentRental currentRental, ZonedDateTime end) {
        this.car = currentRental.getCar();
        this.client = currentRental.getClient();
        this.start = currentRental.getStart();
        this.end = end;
    }

    Car getCar() {
        return car;
    }

    Client getClient() {
        return client;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public ZonedDateTime getEnd() {
        return end;
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
