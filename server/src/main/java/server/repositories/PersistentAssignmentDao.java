package server.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.entity.PersistentAssignment;

import java.util.Collection;

@Repository
public interface PersistentAssignmentDao extends CrudRepository<PersistentAssignment, Long> {
    Collection<PersistentAssignment> findByType(PersistentAssignment.Type type);
}
