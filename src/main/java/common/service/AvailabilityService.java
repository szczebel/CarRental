package common.service;

import common.domain.Car;
import common.domain.RentalClass;

import java.util.List;

public interface AvailabilityService {
    List<Car> findAvailableCars(RentalClass selectedItem);
}
