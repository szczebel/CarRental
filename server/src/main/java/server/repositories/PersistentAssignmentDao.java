package server.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.entity.PersistentAssignment;

import java.util.stream.Stream;

@Repository
public interface PersistentAssignmentDao extends CrudRepository<PersistentAssignment, Long> {
    Stream<PersistentAssignment> findByType(PersistentAssignment.Type type);

    @Query("SELECT t FROM PersistentAssignment t WHERE t.type != 'Historical'")
    Stream<PersistentAssignment> findWhereTypeIsNotHistorical();
}
