package server.service;

import common.domain.Car;
import common.domain.RentalClass;
import common.service.RentabilityService;
import common.util.Interval;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;

@Component("rentabilityService")
public class RentabilityServiceImpl implements RentabilityService {
    @Autowired
    FleetServiceImpl fleetService;
    @Autowired
    RentalServiceImpl rentalService;
    @Autowired BookingServiceImpl bookingService;

    @Override
    public List<Car> findAvailableCars(Query query) {
        long start = currentTimeMillis();
        List<Car> retval = fleetService.fetchAll().stream()
                .filter(car -> ofClass(car, query.getRentalClass()))
                .filter(rentalService::isAvailable)
                .filter(car -> !bookingService.alreadyBooked(car, new Interval(ZonedDateTime.now(), query.getAvailableUntil())))
                .collect(Collectors.toList());
        LoggerFactory.getLogger("timing").info("Finding available cars for renting took " + (currentTimeMillis() - start));
        return retval;
    }

    private boolean ofClass(Car car, RentalClass requiredClass) {
        return requiredClass == null || car.isOfClass(requiredClass.getName());
    }

}
