package mocks;

import common.domain.Car;
import common.service.FleetService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class MockFleetService implements FleetService{
    List<Car> fleet = new ArrayList<>();

    {
        //generate some data
        String[] models = {"Ford Mondeo", "Fiat Multipla", "Lexus", "Mercedes S", "Peugeot 307", "Renault Safrane", "Mazda 6", "Volvo XC60"};
        Random random = new Random();
        for (int i = 100; i <= 110; i++) {
            create(new Car(models[random.nextInt(models.length)],1000+random.nextInt(9000)+"KHZ" + i));
        }
    }


    @Override
    public List<Car> fetchAll() {
        return new ArrayList<>(fleet);
    }

    @Override
    public Car create(Car car) {
        if (fleet.stream().anyMatch(registration(car)))
            throw new IllegalArgumentException("Car already exists: " + car);
        fleet.add(car);
        return car;
    }

    private Predicate<Car> registration(Car newCar) {
        return car -> car.getRegistration().equals(newCar.getRegistration());
    }
}
