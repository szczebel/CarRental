package common.service;

import common.domain.Car;

import java.util.List;

public interface FleetService {
    List<Car> fetchAll();
    Car create(Car car);
}
