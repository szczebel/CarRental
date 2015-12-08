package server.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.entity.PersistentCar;
import server.entity.PersistentRentalClass;

import java.util.Collection;

@Repository
public interface PersistentCarDao extends CrudRepository<PersistentCar, String> {
    Collection<PersistentCar> findByRentalClass(PersistentRentalClass rentalClass);
}
