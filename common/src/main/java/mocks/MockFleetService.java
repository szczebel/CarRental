package mocks;

import common.domain.Car;
import common.domain.RentalClass;
import common.service.FleetService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component("fleetService")
public class MockFleetService implements FleetService {
    List<Car> fleet = new ArrayList<>();


    @Override
    public List<Car> fetchAll() {
        return new ArrayList<>(fleet);
    }

    @Override
    public Collection<Car> findByRentalClass(RentalClass rentalClass) {
        if(rentalClass == null) return fetchAll();
        return fetchAll().stream().filter(car -> rentalClass.getName().equals(car.getRentalClassName())).collect(Collectors.toList());
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

    public int fleetSize() {
        return fleet.size();
    }

    public long countOf(String rentalClass) {
        return fleet.stream().filter(c -> c.isOfClass(rentalClass)).count();
    }
}
