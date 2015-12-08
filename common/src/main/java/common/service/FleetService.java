package common.service;

import common.domain.Car;
import common.domain.RentalClass;

import java.util.Collection;
import java.util.List;

public interface FleetService {
    List<Car> fetchAll();
    Collection<Car> findByRentalClass(RentalClass rentalClass);

    void create(Car car);
}
