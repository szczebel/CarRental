package server.service;

import common.domain.Car;
import common.domain.RentalClass;
import common.service.BookabilityService;
import common.service.FleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("bookabilityService")
public class BookabilityServiceImpl implements BookabilityService {
    @Autowired
    FleetService fleetService;
    @Autowired
    BookingServiceImpl bookingService;

    @Override
    public List<Car> findAvailableCars(Query query) {
        return fleetService.fetchAll().stream()
                .filter(car -> ofClass(car, query.getRentalClass()))
                .filter(car -> !bookingService.alreadyBooked(car, query.getInterval()))
                .collect(Collectors.toList());
    }

    private boolean ofClass(Car car, RentalClass requiredClass) {
        return requiredClass == null || car.isOfClass(requiredClass.getName());
    }

}
