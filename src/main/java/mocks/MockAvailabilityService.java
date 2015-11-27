package mocks;

import common.domain.Car;
import common.service.AvailabilityService;

import java.util.List;

public class MockAvailabilityService implements AvailabilityService {
    private MockFleetService mockFleetService;
    private MockRentalService mockRentalService;

    @Override
    public List<Car> findAvailableCars() {
        return null;
    }
}
