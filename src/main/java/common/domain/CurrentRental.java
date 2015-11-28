package common.domain;

import java.time.ZonedDateTime;

public class CurrentRental {
    private final Car car;
    private final Client client;
    private final ZonedDateTime start;

    public CurrentRental(Car car, Client client, ZonedDateTime start) {
        this.car = car;
        this.client = client;
        this.start = start;
    }

    public Car getCar() {
        return car;
    }

    public Client getClient() {
        return client;
    }

    public ZonedDateTime getStart() {
        return start;
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
