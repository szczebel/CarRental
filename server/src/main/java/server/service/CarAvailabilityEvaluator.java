package server.service;

import common.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import server.entity.PersistentAssignment;
import server.repositories.PersistentAssignmentDao;

import java.util.Optional;

@Component
public class CarAvailabilityEvaluator {

    @Autowired PersistentAssignmentDao dao;

    public boolean isAvailable(String registration, Interval when) {
        Optional<PersistentAssignment> overlappingRentalOrBooking =
                dao.findWhereTypeIsNotHistorical()
                        .filter(cr -> registration.equals(cr.getRegistration()))
                        .filter(a -> when.intersects(a.getInterval()))
                        .findFirst();
        return !overlappingRentalOrBooking.isPresent();
    }
}
