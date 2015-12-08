package server.service;

import common.domain.Car;
import common.service.RentabilityService;
import common.util.Interval;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Collection;

import static java.lang.System.currentTimeMillis;

@Component("rentabilityService")
public class RentabilityServiceImpl implements RentabilityService {
    @Autowired
    AvailabilityService availabilityService;

    @Override
    public Collection<Car> findAvailableCars(Query query) {
        long start = currentTimeMillis();

        Collection<Car> retval = newFind(query);
        LoggerFactory.getLogger("timing").info("Finding " + retval.size() + " available cars for renting took " + (currentTimeMillis() - start));

        return retval;
    }

    private Collection<Car> newFind(Query query) {
        return availabilityService.findCarsWithoutAssignment(
                query.getRentalClass(),
                new Interval(ZonedDateTime.now(), query.getAvailableUntil()));
    }

}
