package server.service;

import common.domain.RentalClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import server.entity.PersistentRentalClass;
import server.repositories.PersistentRentalClassDao;

import java.util.ArrayList;
import java.util.List;

@Component("rentalClassService")
public class RentalClassServiceImpl implements common.service.RentalClassService {

    @Autowired PersistentRentalClassDao dao;

    @Override
    public List<RentalClass> fetchAll() {
        List<RentalClass> rentalClasses = new ArrayList<>();
        dao.findAll().forEach(prc -> rentalClasses.add(prc.toRentalClass()));
        return rentalClasses;
    }

    @Override
    public void create(RentalClass rentalClass) {
        dao.save(new PersistentRentalClass(rentalClass));
    }
}
