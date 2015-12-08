package server.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PersistentCar {

    @Id
    private String registration;

    protected PersistentCar(){}

    public PersistentCar(String registration) {
        this.registration = registration;
    }

    public String getRegistration() {
        return registration;
    }
}
