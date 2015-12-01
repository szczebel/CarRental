package mocks;

import common.domain.Car;
import common.domain.Client;
import common.domain.CurrentRental;
import common.domain.HistoricalRental;
import common.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Component
public class MockRentalService implements RentalService {
    static final Clock SystemClock = Clock.system(ZoneId.systemDefault());
    Clock clock = SystemClock;

    @Autowired
    MockClientService mockClientService;
    @Autowired
    MockFleetService mockFleetService;
    @Autowired
    MockHistoryService mockHistoryService;

    Map<Car, CurrentRental> currentRentals = new HashMap<>();

    @Override
    public CurrentRental rent(Car car, Client client) {
        if (!mockClientService.clients.contains(client))
            throw new IllegalArgumentException("Nonexisting client " + client);
        if (!mockFleetService.fleet.contains(car)) throw new IllegalArgumentException("Nonexisting car " + car);
        if (!isAvailable(car))
            throw new IllegalArgumentException(car + " already rented to " + currentRentals.get(car));
        CurrentRental currentRental = new CurrentRental(car, client, ZonedDateTime.now(clock));
        currentRentals.put(car, currentRental);
        return currentRental;
    }

    @Override
    public void returnCar(String registration) {
        Optional<Car> found = currentRentals.keySet().stream().filter(car -> registration.equals(car.getRegistration())).findFirst();
        if (!found.isPresent())
            throw new IllegalArgumentException("Returning a car which is not rented: " + registration);
        Car key = found.get();
        CurrentRental currentRental = currentRentals.get(key);
        currentRentals.remove(key);
        mockHistoryService.saveEvent(new HistoricalRental(currentRental, ZonedDateTime.now(clock)));
    }

    @Override
    public List<CurrentRental> getCurrentRentals() {
        return new ArrayList<>(currentRentals.values());
    }

    boolean isAvailable(Car car) {
        return !currentRentals.containsKey(car);
    }
}
