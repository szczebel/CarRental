package server.service;

import common.domain.Booking;
import common.domain.Car;
import common.domain.Client;
import common.service.BookingService;
import common.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import server.entity.PersistentAssignment;
import server.repositories.PersistentAssignmentDao;

import java.util.Collection;
import java.util.stream.Collectors;

@Component("bookingService")
public class BookingServiceImpl implements BookingService {

    @Autowired PersistentAssignmentDao dao;

    @Override
    public void book(Car car, Client client, Interval interval) {
        if (alreadyBooked(car, interval)) throw new IllegalArgumentException(car + " already booked in this time range");
        dao.save(new PersistentAssignment(new Booking(car, client, interval)));
    }

    @Override
    public Collection<Booking> getBookings() {
        return dao.findByType(PersistentAssignment.Type.Booking).map(PersistentAssignment::asBooking).collect(Collectors.toList());
    }

    boolean alreadyBooked(Car car, Interval interval) {
        return getBookings().stream().filter(booking -> car.getRegistration().equals(booking.getRegistration())).anyMatch(b -> interval.overlaps(b.getInterval()));
    }
}
