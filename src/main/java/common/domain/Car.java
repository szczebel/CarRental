package common.domain;

import java.io.Serializable;

public class Car implements Serializable{
    private String registration;
    private String model;
    private RentalClass rentalClass;

    public Car(String model, String registration, RentalClass rentalClass) {
        this.model = model;
        this.registration = registration;
        this.rentalClass = rentalClass;
    }

    public String getRegistration() {
        return registration;
    }

    public String getModel() {
        return model;
    }

    public String getRentalClassName() {
        return rentalClass.getName();
    }

    @Override
    public String toString() {
        return registration;
    }

    public boolean isOfClass(RentalClass aClass) {
        return aClass.equals(this.rentalClass);
    }
}
