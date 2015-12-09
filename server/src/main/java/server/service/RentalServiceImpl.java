package server.service;

import common.domain.Car;
import common.domain.Client;
import common.domain.CurrentRental;
import common.service.RentalService;
import common.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired PersistentAssignmentDao dao;
    @Autowired CarAvailabilityEvaluator carAvailabilityEvaluator;


    @Transactional
    @Override
    public CurrentRental rent(Car car, Client client, ZonedDateTime plannedEnd) {
        Interval when = new Interval(ZonedDateTime.now(clockProvider.get()), plannedEnd);
        if (!carAvailabilityEvaluator.isAvailable(car.getRegistration(), when)) throw new IllegalArgumentException(car + " not available");
        CurrentRental currentRental = new CurrentRental(car, client, when);
        //sleepIfNotDataGeneration(10);
        dao.save(new PersistentAssignment(currentRental));
        return currentRental;
    }

    private void sleepIfNotDataGeneration(int seconds) {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        for (StackTraceElement e : stackTrace) {
            if (e.getClassName().contains("DataGenerator")) return;
        }
        System.out.println("sleeping");
        try {
            Thread.sleep(seconds*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Transactional
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
