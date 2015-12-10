package mocks;

import common.domain.Booking;
import common.domain.Car;
import common.domain.Client;
import common.service.BookingService;
import common.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component("bookingService")
public class MockBookingService implements BookingService {

    @Autowired MockClientService clientService;
    @Autowired MockFleetService fleetService;

    long idGenerator;
    Map<Long, Booking> bookings = new HashMap<>();

    @Override
    public void book(Car car, Client client, Interval interval) {
        if (!clientService.clients.contains(client))
            throw new IllegalArgumentException("Nonexisting client " + client);
        if (!fleetService.fleet.contains(car)) throw new IllegalArgumentException("Nonexisting car " + car);
        if (alreadyBooked(car, interval))
            throw new IllegalArgumentException(car + " cannot be booked");
        Booking booking = new Booking(idGenerator++, car, client, interval);
        bookings.put(booking.getId(), booking);
    }

    @Override
    public Collection<Booking> getBookings() {
        return new ArrayList<>(bookings.values());
    }

    @Override
    public Collection<Booking> getBookingsOf(Client client) {
        return bookings.values().stream()
                .filter(c -> c.getClientEmail().equals(client.getEmail()))
                .collect(Collectors.toList());
    }

    @Override
    public void cancel(Booking booking) {
        bookings.remove(booking.getId());
    }

    boolean alreadyBooked(Car car, Interval interval) {
        return bookings.values().stream().filter(booking -> car.getRegistration().equals(booking.getRegistration())).anyMatch(b -> interval.intersects(b.getInterval()));
    }
}
