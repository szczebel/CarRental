package server.service;

import common.domain.Car;
import common.service.FleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import server.entity.PersistentCar;
import server.repositories.PersistentCarDao;

import java.util.ArrayList;
import java.util.List;

@Component("fleetService")
public class FleetServiceImpl implements FleetService {
    @Autowired PersistentCarDao dao;


    @Override
    public List<Car> fetchAll() {
        ArrayList<Car> cars = new ArrayList<>();
        dao.findAll().forEach(pc -> cars.add(pc.toCar()));
        return cars;
    }

    @Override
    public void create(Car car) {
        dao.save(new PersistentCar(car));
    }

    public long fleetSize() {
        return dao.count();
    }

    public long countOf(String rentalClass) {
        return fetchAll().stream().filter(c -> c.isOfClass(rentalClass)).count();
    }
}
