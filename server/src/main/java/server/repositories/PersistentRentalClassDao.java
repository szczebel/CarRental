package server.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.entity.PersistentRentalClass;

@Repository
public interface PersistentRentalClassDao extends CrudRepository<PersistentRentalClass, String> {
}
