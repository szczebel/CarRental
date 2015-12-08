package common.domain;

import java.io.Serializable;

public class Car implements Serializable {
    private String registration;
    private String model;
    private RentalClass rentalClass;

    public Car() {}

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

    public RentalClass getRentalClass() {
        return rentalClass;
    }

    public String getRentalClassName() {
        return rentalClass.getName();
    }

    public int getHourlyRate() {
        return rentalClass.getHourlyRate();
    }

    @Override
    public String toString() {
        return registration;
    }

    public boolean isOfClass(String aClass) {
        return aClass.equals(this.rentalClass.getName());
    }
}
