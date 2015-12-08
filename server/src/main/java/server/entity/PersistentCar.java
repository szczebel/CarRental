package server.entity;

import common.domain.Car;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class PersistentCar {

    @Id String registration;
    @Column String model;
    @ManyToOne PersistentRentalClass rentalClass;

    protected PersistentCar(){}

    public PersistentCar(Car car) {
        registration = car.getRegistration();
        model = car.getModel();
        rentalClass = new PersistentRentalClass(car.getRentalClass());
    }

    public Car toCar() {
        return new Car(model, registration, rentalClass.toRentalClass());
    }
}
