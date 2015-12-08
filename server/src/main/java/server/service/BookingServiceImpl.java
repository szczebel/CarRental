package server.service;

import common.domain.Booking;
import common.domain.Car;
import common.domain.Client;
import common.service.BookingService;
import common.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component("bookingService")
public class BookingServiceImpl implements BookingService {

    @Autowired
    ClientServiceImpl clientService;
    @Autowired
    FleetServiceImpl fleetService;

    Set<Booking> bookings = new HashSet<>();

    @Override
    public Booking book(Car car, Client client, Interval interval) {
        if (!clientService.clients.contains(client))
            throw new IllegalArgumentException("Nonexisting client " + client);
        if (!fleetService.fleet.contains(car)) throw new IllegalArgumentException("Nonexisting car " + car);
        if (alreadyBooked(car, interval))
            throw new IllegalArgumentException(car + " cannot be booked");
        Booking booking = new Booking(car, client, interval);
        bookings.add(booking);
        return booking;
    }

    @Override
    public Collection<Booking> getBookings() {
        return new ArrayList<>(bookings);
    }

    boolean alreadyBooked(Car car, Interval interval) {
        return bookings.stream().filter(booking -> car.getRegistration().equals(booking.getRegistration())).anyMatch(b -> interval.overlaps(b.getInterval()));
    }
}
