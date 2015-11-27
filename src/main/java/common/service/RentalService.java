package common.service;


import common.domain.Car;
import common.domain.Client;

public interface RentalService {
    void rent(Car car, Client client);
}
