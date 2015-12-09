package server.service;

import common.domain.Car;
import common.domain.RentalClass;
import common.service.AvailabilityService;
import common.service.FleetService;
import common.util.Interval;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.repositories.PersistentAssignmentDao;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Service("availabilityService")
public class AvailabilityServiceImpl implements AvailabilityService {

    @Autowired
    PersistentAssignmentDao dao;
    @Autowired
    FleetService fleetService;
    @Autowired
    Supplier<Clock> clockProvider;

    @Override
    public Collection<Car> findAvailableToRent(RentQuery query) {
        return findCarsWithoutAssignment(
                query.getRentalClass(),
                new Interval(
                        ZonedDateTime.now(clockProvider.get()),
                        query.getAvailableUntil())
        );
    }

    @Override
    public Collection<Car> findAvailableToBook(BookingQuery query) {
        return findCarsWithoutAssignment(
                query.getRentalClass(),
                query.getInterval()
        );
    }

    Collection<Car> findCarsWithoutAssignment(RentalClass rentalClass, Interval when) {
        long start = System.currentTimeMillis();
        Collection<Car> candidates = fleetService.findByRentalClass(rentalClass);
        Map<String, Car> byRegistration = new HashMap<>();
        candidates.forEach(c -> byRegistration.put(c.getRegistration(), c));
        dao.findWhereTypeIsNotHistorical().forEach(a -> {
            if (when.intersects(a.getInterval())) byRegistration.remove(a.getRegistration());
        });
        LoggerFactory.getLogger("timnig").info("Availability: found " + byRegistration.size() + " cars in "+(System.currentTimeMillis()-start)+" ms");
        return new ArrayList<>(byRegistration.values());
    }
}
