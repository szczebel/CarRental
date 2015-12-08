package server.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.entity.PersistentCar;

@Repository
public interface PersistentCarDao extends CrudRepository<PersistentCar, String> {
}
