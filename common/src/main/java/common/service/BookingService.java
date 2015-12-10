package common.service;


import common.domain.Booking;
import common.domain.Car;
import common.domain.Client;
import common.util.Interval;

import java.util.Collection;

public interface BookingService {
    void book(Car car, Client client, Interval interval);

    Collection<Booking> getBookings();

    void cancel(Booking booking);
}
