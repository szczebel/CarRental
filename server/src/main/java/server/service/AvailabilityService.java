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
import java.util.HashMap;
import java.util.Map;

@Service
public class AvailabilityService {

    @Autowired
    PersistentAssignmentDao dao;
    @Autowired
    FleetService fleetService;

    Collection<Car> findCarsWithoutAssignment(RentalClass rentalClass, Interval interval) {
        Collection<Car> candidates = fleetService.findByRentalClass(rentalClass);
        Map<String, Car> byRegistration = new HashMap<>();
        candidates.forEach(c -> byRegistration.put(c.getRegistration(), c));
        dao.findWhereTypeIsNotHistorical().forEach(a -> {
            if (a.getInterval().overlaps(interval)) byRegistration.remove(a.getRegistration());
        });
        return new ArrayList<>(byRegistration.values());
    }
}
