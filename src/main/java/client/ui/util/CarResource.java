package client.ui.util;

import common.domain.Booking;
import common.domain.Car;
import common.domain.HistoricalRental;
import schedule.model.Resource;

public class CarResource implements Resource {

    final String registration;
    final String model;

    public CarResource(HistoricalRental historicalRental) {
        this.registration = historicalRental.getRegistration();
        this.model = historicalRental.getModel();
    }

    public CarResource(Car car) {
        this.registration = car.getRegistration();
        this.model = car.getModel();
    }

    public CarResource(Booking booking) {
        this.registration = booking.getRegistration();
        this.model = booking.getModel();
    }

    @Override
    public int hashCode() {
        return registration.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return ((CarResource) obj).registration.equals(this.registration);
    }
}
