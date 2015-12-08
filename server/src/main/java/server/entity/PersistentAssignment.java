package server.entity;

import common.domain.AbstractAssignment;
import common.domain.Booking;
import common.domain.CurrentRental;
import common.domain.HistoricalRental;
import common.util.Interval;

import javax.persistence.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
public class PersistentAssignment {

    @Id @GeneratedValue long id;

    @Column long start;
    @Column long end;
    @Column @Enumerated(EnumType.STRING) Type type;
    @ManyToOne PersistentCar car;
    @ManyToOne PersistentClient client;

    protected PersistentAssignment() {}

    public PersistentAssignment(HistoricalRental hr) {
        type = Type.HISTORICAL;
        populateFields(hr);
    }

    public PersistentAssignment(CurrentRental cr) {
        type = Type.CURRENT;
        populateFields(cr);
    }

    public PersistentAssignment(Booking b) {
        type = Type.BOOKING;
        populateFields(b);
    }

    private void populateFields(AbstractAssignment aa) {
        start = aa.getStart().toInstant().toEpochMilli();
        end = aa.getEnd().toInstant().toEpochMilli();
        car = new PersistentCar(aa.getCar());
        client = new PersistentClient(aa.getClient());
    }

    public Booking asBooking() {
        if(Type.BOOKING != this.type) throw new RuntimeException("This is not a booking");
        return new Booking(car.toCar(), client.toClient(), getInterval());
    }

    public HistoricalRental asHistorical() {
        if(Type.HISTORICAL != this.type) throw new RuntimeException("This is not a historical rental");
        return new HistoricalRental(car.toCar(), client.toClient(), getInterval());
    }

    public CurrentRental asCurrent() {
        if(Type.CURRENT != this.type) throw new RuntimeException("This is not a current rental");
        return new CurrentRental(car.toCar(), client.toClient(), getInterval());
    }

    private Interval getInterval() {
        return new Interval(
                Instant.ofEpochMilli(start).atZone(ZoneId.systemDefault()),
                Instant.ofEpochMilli(end).atZone(ZoneId.systemDefault())
        );
    }

    public String getRegistration() {
        return car.registration;
    }

    public void changeToHistorical(ZonedDateTime actualEndDate) {
        if(Type.CURRENT != this.type) throw new IllegalStateException("Trying to change to HISTORICAL assignment which is not CURRENT");
        this.type = Type.HISTORICAL;
        end = actualEndDate.toInstant().toEpochMilli();
    }

    public enum Type { HISTORICAL, CURRENT, BOOKING }
}
