package common.service;


import common.domain.Car;
import common.domain.Client;
import common.domain.CurrentRental;

import java.time.ZonedDateTime;
import java.util.List;

public interface RentalService {
    CurrentRental rent(Car car, Client client, ZonedDateTime plannedEnd);

    List<CurrentRental> getCurrentRentals();

    void returnCar(String registration);
}
