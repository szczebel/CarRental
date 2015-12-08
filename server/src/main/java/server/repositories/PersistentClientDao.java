package server.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.entity.PersistentClient;

@Repository
public interface PersistentClientDao extends CrudRepository<PersistentClient, String> {
}
