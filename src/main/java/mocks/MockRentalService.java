package mocks;

import common.domain.Car;
import common.domain.Client;
import common.domain.CurrentRental;
import common.service.RentalService;

import java.time.ZonedDateTime;
import java.util.*;

public class MockRentalService implements RentalService {
    MockClientService mockClientService;
    MockFleetService mockFleetService;

    Map<Car, CurrentRental> currentRentals = new HashMap<>();

    @Override
    public void rent(Car car, Client client) {
        if (!mockClientService.clients.contains(client))
            throw new IllegalArgumentException("Nonexisting client " + client);
        if (!mockFleetService.fleet.contains(car)) throw new IllegalArgumentException("Nonexisting car " + car);
        if (!isAvailable(car)) throw new RuntimeException(car + " already rented to " + currentRentals.get(car));
        currentRentals.put(car, new CurrentRental(car, client, ZonedDateTime.now()));
    }

    @Override
    public void returnCar(String registration) {
        Optional<Car> found = currentRentals.keySet().stream().filter(car -> registration.equals(car.getRegistration())).findFirst();
        if (!found.isPresent())
            throw new IllegalArgumentException("Returning a car which is not rented: " + registration);
        currentRentals.remove(found.get());
    }

    @Override
    public List<CurrentRental> getCurrentRentals() {
        return new ArrayList<>(currentRentals.values());
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
