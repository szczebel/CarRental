package server.service;

import common.domain.Booking;
import common.domain.Car;
import common.domain.Client;
import common.service.BookingService;
import common.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import server.entity.PersistentAssignment;
import server.repositories.PersistentAssignmentDao;

import java.util.Collection;
import java.util.stream.Collectors;

@Component("bookingService")
public class BookingServiceImpl implements BookingService {

    @Autowired PersistentAssignmentDao dao;
    @Autowired CarAvailabilityEvaluator carAvailabilityEvaluator;

    @Transactional
    @Override
    public void book(Car car, Client client, Interval interval) {
        //todo don't allow booking in the past
        if (!carAvailabilityEvaluator.isAvailable(car.getRegistration(), interval)) throw new IllegalArgumentException(car + " not available in this time range");
        dao.save(new PersistentAssignment(new Booking(car, client, interval)));
    }

    @Transactional
    @Override
    public Collection<Booking> getBookings() {
        return dao.findByType(PersistentAssignment.Type.Booking).map(PersistentAssignment::asBooking).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void cancel(Booking booking) {
        dao.delete(booking.getId());
    }

}
