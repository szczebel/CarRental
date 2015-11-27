package common.domain;

import java.io.Serializable;

public class Car implements Serializable{
    private String registration;
    private String model;

    public Car(String model, String registration) {
        this.model = model;
        this.registration = registration;
    }

    public String getRegistration() {
        return registration;
    }

    public String getModel() {
        return model;
    }

    @Override
    public String toString() {
        return "Car{" +
                "registration='" + registration + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}
