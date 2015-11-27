package common.service;

import common.domain.Car;

import java.util.List;

public interface AvailabilityService {
    List<Car> findAvailableCars();
}
