package mocks;

import common.domain.Car;
import common.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MockAvailabilityService implements AvailabilityService {
    @Autowired
    MockFleetService mockFleetService;
    @Autowired
    MockRentalService mockRentalService;

    @Override
    public List<Car> findAvailableCars() {
        return mockFleetService.fetchAll().stream().filter(mockRentalService::isAvailable).collect(Collectors.toList());
    }
}
