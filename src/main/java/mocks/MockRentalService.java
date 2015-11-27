package mocks;

import common.domain.Car;
import common.domain.Client;
import common.service.RentalService;

import java.util.HashMap;
import java.util.Map;

public class MockRentalService implements RentalService {
    MockClientService mockClientService;
    MockFleetService mockFleetService;

    Map<Car, Client> currentRentals = new HashMap<>();

    @Override
    public void rent(Car car, Client client) {
        if (!mockClientService.clients.contains(client))
            throw new IllegalArgumentException("Nonexisting client " + client);
        if (!mockFleetService.fleet.contains(car)) throw new IllegalArgumentException("Nonexisting car " + car);
        if (!isAvailable(car)) throw new RuntimeException(car + " already rented to " + currentRentals.get(car));
        currentRentals.put(car, client);
    }

    public boolean isAvailable(Car car) {
        return !currentRentals.containsKey(car);
    }

    @SuppressWarnings("unused")
    public void setMockClientService(MockClientService mockClientService) {
        this.mockClientService = mockClientService;
    }

    @SuppressWarnings("unused")
    public void setMockFleetService(MockFleetService mockFleetService) {
        this.mockFleetService = mockFleetService;
    }
}
