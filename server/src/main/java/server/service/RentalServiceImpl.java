package server.service;

import common.domain.Car;
import common.domain.Client;
import common.domain.CurrentRental;
import common.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import server.entity.PersistentAssignment;
import server.repositories.PersistentAssignmentDao;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Component("rentalService")
public class RentalServiceImpl implements RentalService {

    @Autowired Supplier<Clock> clockProvider;

    @Autowired
    PersistentAssignmentDao dao;


    @Override
    public CurrentRental rent(Car car, Client client, ZonedDateTime plannedEnd) {
        if (!isAvailable(car)) throw new IllegalArgumentException(car + " already rented");
        CurrentRental currentRental = new CurrentRental(car, client, ZonedDateTime.now(clockProvider.get()), plannedEnd);
        dao.save(new PersistentAssignment(currentRental));
        return currentRental;
    }

    @Override
    public void returnCar(String registration) {
        Optional<PersistentAssignment> cr = getCurrentRental(registration);
        if (!cr.isPresent()) throw new IllegalArgumentException("Returning a car which is not rented: " + registration);
        PersistentAssignment persistentAssignment = cr.get();
        persistentAssignment.changeToHistorical(ZonedDateTime.now(clockProvider.get()));
        dao.save(persistentAssignment);
    }

    @Override
    public List<CurrentRental> getCurrentRentals() {
        ArrayList<CurrentRental> currentRentals = new ArrayList<>();
        dao.findByType(PersistentAssignment.Type.CURRENT).forEach(pcr -> currentRentals.add(pcr.asCurrent()));
        return currentRentals;
    }

    boolean isAvailable(Car car) {
        return !getCurrentRental(car.getRegistration()).isPresent();
    }

    Optional<PersistentAssignment> getCurrentRental(String registration) {
        Collection<PersistentAssignment> byType = dao.findByType(PersistentAssignment.Type.CURRENT);
        return byType.stream().filter(cr -> registration.equals(cr.getRegistration())).findFirst();
    }

    public boolean isAvailableAfter(Car car, ZonedDateTime dateTime) {
        Optional<PersistentAssignment> currentRental = getCurrentRental(car.getRegistration());
        if(currentRental.isPresent()) {
            PersistentAssignment a = currentRental.get();
            return a.asCurrent().getPlannedEnd().isBefore(dateTime);
        } else {
            return true;
        }
    }
}
