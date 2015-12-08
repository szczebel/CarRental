package common.service;

import common.domain.Car;

import java.util.List;

public interface FleetService {
    List<Car> fetchAll();

    void create(Car car);
}
