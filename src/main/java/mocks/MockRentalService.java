package mocks;

import common.domain.Car;
import common.domain.Client;
import common.domain.CurrentRental;
import common.domain.HistoricalRental;
import common.service.RentalService;

import java.time.ZonedDateTime;
import java.util.*;

public class MockRentalService implements RentalService {
    MockClientService mockClientService;
    MockFleetService mockFleetService;
    MockHistoryService mockHistoryService;

    Map<Car, CurrentRental> currentRentals = new HashMap<>();

    @Override
    public void rent(Car car, Client client) {
        if (!mockClientService.clients.contains(client))
            throw new IllegalArgumentException("Nonexisting client " + client);
        if (!mockFleetService.fleet.contains(car)) throw new IllegalArgumentException("Nonexisting car " + car);
        if (!isAvailable(car))
            throw new IllegalArgumentException(car + " already rented to " + currentRentals.get(car));
        currentRentals.put(car, new CurrentRental(car, client, ZonedDateTime.now()));
    }

    @Override
    public void returnCar(String registration) {
        Optional<Car> found = currentRentals.keySet().stream().filter(car -> registration.equals(car.getRegistration())).findFirst();
        if (!found.isPresent())
            throw new IllegalArgumentException("Returning a car which is not rented: " + registration);
        Car key = found.get();
        CurrentRental currentRental = currentRentals.get(key);
        currentRentals.remove(key);
        mockHistoryService.saveEvent(new HistoricalRental(currentRental, ZonedDateTime.now()));
    }

    @Override
    public List<CurrentRental> getCurrentRentals() {
        return new ArrayList<>(currentRentals.values());
    }

    boolean isAvailable(Car car) {
        return !currentRentals.containsKey(car);
    }

    @SuppressWarnings("unused")
    public void setMockHistoryService(MockHistoryService mockHistoryService) {
        this.mockHistoryService = mockHistoryService;
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
