package com.onclass.capacity.infrastructure.adapters.persistenceadapter.repository;

import com.onclass.capacity.infrastructure.adapters.persistenceadapter.entity.CapacityBootcampEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface CapacityBootcampRepository extends ReactiveCrudRepository<CapacityBootcampEntity, Long> {

	@Query("""
        SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
        FROM capacity_bootcamp
        WHERE id_capacity = :capacityId AND id_bootcamp NOT IN (:bootcampId);
    """)
	Mono<Boolean> verifyOtherAssignations(Long capacityId, Long bootcampId);

	@Modifying
	@Query("DELETE FROM capacity_bootcamp WHERE id_bootcamp = :bootcampId")
	Mono<Void> deleteAllAssignations(Long bootcampId);
}
