package mocks;

import common.domain.Car;
import common.service.AvailabilityService;

import java.util.List;
import java.util.stream.Collectors;

public class MockAvailabilityService implements AvailabilityService {
    private MockFleetService mockFleetService;
    private MockRentalService mockRentalService;

    @Override
    public List<Car> findAvailableCars() {
        return mockFleetService.fetchAll().stream().filter(mockRentalService::isAvailable).collect(Collectors.toList());
    }

    public void setMockFleetService(MockFleetService mockFleetService) {
        this.mockFleetService = mockFleetService;
    }

    public void setMockRentalService(MockRentalService mockRentalService) {
        this.mockRentalService = mockRentalService;
    }
}
