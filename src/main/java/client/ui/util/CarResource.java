package client.ui.util;

import common.domain.AbstractAssignment;
import common.domain.Car;
import schedule.model.Resource;

public class CarResource implements Resource {

    final String registration;
    final String model;

    public CarResource(AbstractAssignment a) {
        this.registration = a.getRegistration();
        this.model = a.getModel();
    }

    public CarResource(Car car) {
        this.registration = car.getRegistration();
        this.model = car.getModel();
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
