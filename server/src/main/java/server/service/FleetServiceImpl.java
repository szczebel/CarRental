package server.service;

import common.domain.Car;
import common.domain.RentalClass;
import common.service.FleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import server.entity.PersistentCar;
import server.entity.PersistentRentalClass;
import server.publish.ChangePublisher;
import server.repositories.PersistentCarDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component("fleetService")
public class FleetServiceImpl implements FleetService {
    @Autowired PersistentCarDao dao;
    @Autowired ChangePublisher changePublisher;

    @Override
    public List<Car> fetchAll() {
        ArrayList<Car> cars = new ArrayList<>();
        dao.findAll().forEach(pc -> cars.add(pc.toCar()));
        return cars;
    }

    @Override
    public Collection<Car> findByRentalClass(RentalClass rentalClass) {
        if(rentalClass == null) return fetchAll();
        Collection<PersistentCar> byRentalClass = dao.findByRentalClass(new PersistentRentalClass(rentalClass));
        ArrayList<Car> cars = new ArrayList<>();
        byRentalClass.forEach(pc -> cars.add(pc.toCar()));
        return cars;
    }

    @Override
    public void create(Car car) {
        if(dao.exists(car.getRegistration())) throw new IllegalArgumentException("Car with this registration already exists");
        dao.save(new PersistentCar(car));
        changePublisher.publishNewCar(car);
    }

    public long fleetSize() {
        return dao.count();
    }

    public long countOf(String rentalClass) {
        return fetchAll().stream().filter(c -> c.isOfClass(rentalClass)).count();
    }
}
