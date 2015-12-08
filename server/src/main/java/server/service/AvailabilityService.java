package server.service;

import common.domain.Car;
import common.domain.RentalClass;
import common.service.FleetService;
import common.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.repositories.PersistentAssignmentDao;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class AvailabilityService {

    @Autowired PersistentAssignmentDao dao;
    @Autowired FleetService fleetService;

    Collection<Car> findCarsWithoutAssigment(RentalClass rentalClass, Interval interval) {
        ArrayList<Car> cars = new ArrayList<>();
//        dao.findAll().forEach(a ->);
        return cars;
    }
}
