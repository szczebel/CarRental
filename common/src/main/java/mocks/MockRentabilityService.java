package mocks;

import common.domain.Car;
import common.domain.RentalClass;
import common.service.RentabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("rentabilityService")
public class MockRentabilityService implements RentabilityService {
    @Autowired
    MockFleetService mockFleetService;
    @Autowired
    MockRentalService mockRentalService;

    @Override
    public List<Car> findAvailableCars(Query quaey) {
        return mockFleetService.fetchAll().stream().filter(car -> ofClass(car, quaey.getRentalClass())).filter(mockRentalService::isAvailable).collect(Collectors.toList());
    }

    private boolean ofClass(Car car, RentalClass requiredClass) {
        return requiredClass == null || car.isOfClass(requiredClass.getName());
    }

}
