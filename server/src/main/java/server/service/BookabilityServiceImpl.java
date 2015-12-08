package server.service;

import common.domain.Car;
import common.domain.RentalClass;
import common.service.BookabilityService;
import common.service.FleetService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;

@Component("bookabilityService")
public class BookabilityServiceImpl implements BookabilityService {
    @Autowired
    FleetService fleetService;
    @Autowired
    BookingServiceImpl bookingService;
    @Autowired
    RentalServiceImpl rentalService;

    @Override
    public List<Car> findAvailableCars(Query query) {
        long start = currentTimeMillis();
        List<Car> retval = fleetService.fetchAll().stream()
                .filter(car -> ofClass(car, query.getRentalClass()))
                .filter(car -> !bookingService.alreadyBooked(car, query.getInterval()))
                .filter(car -> !rentalService.isAvailableAfter(car, query.getInterval().from()))
                .collect(Collectors.toList());
        LoggerFactory.getLogger("timing").info("Finding available cars for booking took " + (currentTimeMillis() - start));
        return retval;
    }

    private boolean ofClass(Car car, RentalClass requiredClass) {
        return requiredClass == null || car.isOfClass(requiredClass.getName());
    }

}
