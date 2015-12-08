package server.entity;

import common.domain.RentalClass;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PersistentRentalClass {

    @Id String className;
    @Column int hourlyRate;

    protected PersistentRentalClass(){}

    public PersistentRentalClass(RentalClass rc) {
        className = rc.getName();
        hourlyRate = rc.getHourlyRate();
    }

    public RentalClass toRentalClass() {
        return new RentalClass(className, hourlyRate);
    }
}
