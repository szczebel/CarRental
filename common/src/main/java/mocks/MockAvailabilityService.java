package mocks;

import common.domain.Car;
import common.domain.RentalClass;
import common.service.AvailabilityService;
import common.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Component("availabilityService")
public class MockAvailabilityService implements AvailabilityService {

    @Autowired
    MockFleetService fleetService;
    @Autowired
    MockBookingService bookingService;
    @Autowired
    MockRentalService rentalService;
    @Autowired
    Supplier<Clock> clockProvider;

    @Override
    public Collection<Car> findAvailableToRent(RentQuery query) {
        return findCarsWithoutAssignment(
                query.getRentalClass(),
                new Interval(
                        ZonedDateTime.now(clockProvider.get()),
                        query.getAvailableUntil())
        );
    }

    @Override
    public Collection<Car> findAvailableToBook(BookingQuery query) {
        return findCarsWithoutAssignment(
                query.getRentalClass(),
                query.getInterval()
        );
    }

    Collection<Car> findCarsWithoutAssignment(RentalClass rentalClass, Interval interval) {
        Collection<Car> candidates = fleetService.findByRentalClass(rentalClass);
        Map<String, Car> byRegistration = new HashMap<>();
        candidates.forEach(c -> byRegistration.put(c.getRegistration(), c));

        bookingService.getBookings().forEach(a -> {
            if (a.getInterval().overlaps(interval)) byRegistration.remove(a.getRegistration());
        });
        rentalService.getCurrentRentals().forEach(a -> {
            if (a.getInterval().overlaps(interval)) byRegistration.remove(a.getRegistration());
        });
        return new ArrayList<>(byRegistration.values());
    }
}
