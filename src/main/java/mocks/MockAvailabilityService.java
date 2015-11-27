package mocks;

import common.domain.Car;
import common.service.AvailabilityService;

import java.util.Collections;
import java.util.List;

public class MockAvailabilityService implements AvailabilityService {
    private MockFleetService mockFleetService;
    private MockRentalService mockRentalService;

    @Override
    public List<Car> findAvailableCars() {
        //todo implement me
        return Collections.emptyList();
    }

    public void setMockFleetService(MockFleetService mockFleetService) {
        this.mockFleetService = mockFleetService;
    }

    public void setMockRentalService(MockRentalService mockRentalService) {
        this.mockRentalService = mockRentalService;
    }
}
