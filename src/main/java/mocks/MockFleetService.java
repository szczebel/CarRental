package mocks;

import common.domain.Car;
import common.service.FleetService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class MockFleetService implements FleetService {
    List<Car> fleet = new ArrayList<>();


    @Override
    public List<Car> fetchAll() {
        return new ArrayList<>(fleet);
    }

    @Override
    public void create(Car car) {
        if (fleet.stream().anyMatch(registration(car)))
            throw new IllegalArgumentException("Car already exists: " + car);
        fleet.add(car);
    }

    private Predicate<Car> registration(Car newCar) {
        return car -> car.getRegistration().equals(newCar.getRegistration());
    }
}
