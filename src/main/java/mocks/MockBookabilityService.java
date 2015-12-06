package mocks;

import common.domain.Car;
import common.domain.RentalClass;
import common.service.BookabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MockBookabilityService implements BookabilityService {
    @Autowired    MockFleetService mockFleetService;
    @Autowired    MockBookingService mockBookingService;

    @Override
    public List<Car> findAvailableCars(Query quaey) {
        return mockFleetService.fetchAll().stream().filter(car -> ofClass(car, quaey.getRentalClass())).filter(c -> !mockBookingService.alreadyBooked(c, quaey.getInterval())).collect(Collectors.toList());
    }

    private boolean ofClass(Car car, RentalClass requiredClass) {
        return requiredClass == null || car.isOfClass(requiredClass.getName());
    }

}
