package server.service;

import common.domain.Car;
import common.service.BookabilityService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static java.lang.System.currentTimeMillis;

@Component("bookabilityService")
public class BookabilityServiceImpl implements BookabilityService {


    @Autowired AvailabilityService availabilityService;

    @Override
    public Collection<Car> findAvailableCars(Query query) {
        long start = currentTimeMillis();
        Collection<Car> retval = availabilityService.findCarsWithoutAssignment(query.getRentalClass(), query.getInterval());
        LoggerFactory.getLogger("timing").info("Finding "+retval.size()+" available cars for booking took " + (currentTimeMillis() - start));

        return retval;
    }

}
