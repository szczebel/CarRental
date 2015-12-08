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
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
        return dao.findByType(PersistentAssignment.Type.Current).map(PersistentAssignment::asCurrent).collect(Collectors.toList());
    }

    private boolean isAvailable(Car car) {
        return !getCurrentRental(car.getRegistration()).isPresent();
    }

    Optional<PersistentAssignment> getCurrentRental(String registration) {
        return dao.findByType(PersistentAssignment.Type.Current).filter(cr -> registration.equals(cr.getRegistration())).findFirst();
    }

}
