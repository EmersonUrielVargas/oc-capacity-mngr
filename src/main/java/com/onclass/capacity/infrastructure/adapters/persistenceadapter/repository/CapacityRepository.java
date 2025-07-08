package com.onclass.capacity.infrastructure.adapters.persistenceadapter.repository;

import com.onclass.capacity.infrastructure.adapters.persistenceadapter.projection.BootcampCountProjection;
import com.onclass.capacity.infrastructure.adapters.persistenceadapter.projection.CapacityBootcampProjection;
import com.onclass.capacity.infrastructure.adapters.persistenceadapter.entity.CapacityEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Repository
public interface CapacityRepository extends ReactiveCrudRepository<CapacityEntity, Long> {
    Mono<CapacityEntity> findByName(String name);

    @Query("SELECT * FROM capabilities ORDER BY name ASC LIMIT :size OFFSET :offset")
    Flux<CapacityEntity> findAndSortByNameAsc(int offset, int size);

    @Query("SELECT * FROM capabilities ORDER BY name DESC LIMIT :size OFFSET :offset")
    Flux<CapacityEntity> findAndSortByNameDesc(int offset, int size);

    @Query("SELECT COUNT(DISTINCT id_bootcamp) FROM capacity_bootcamp")
    Mono<Long> countCapabilitiesPerBootcamps();

    @Query("""
        SELECT cb.id_bootcamp AS bootcampId,
            c.id AS capacityId,
            c.name AS capacityName
        FROM capacity_bootcamp cb
        INNER JOIN capabilities c ON cb.id_capacity = c.id
        WHERE cb.id_bootcamp IN (:bootcampsIds)
    """)
    Flux<CapacityBootcampProjection> findCapabilitiesByBootcampsIds(List<Long> bootcampsIds);

    @Query("""
        SELECT cb.id_bootcamp AS bootcampId, COUNT(cb.id_capacity) AS capabilitiesCount
        FROM capacity_bootcamp cb
        GROUP BY cb.id_bootcamp
        ORDER BY capabilitiesCount DESC
        LIMIT :size OFFSET :offset
    """)
    Flux<BootcampCountProjection> findBootcampIdsOrderByCapabilitiesCountDesc(int size, int offset);

    @Query("""
        SELECT cb.id_bootcamp AS bootcampId, COUNT(cb.id_capacity) AS capabilitiesCount
        FROM capacity_bootcamp cb
        GROUP BY cb.id_bootcamp
        ORDER BY capabilitiesCount ASC
        LIMIT :size OFFSET :offset
    """)
    Flux<BootcampCountProjection> findBootcampIdsOrderByCapabilitiesCountAsc(int size, int offset);

    @Query("""
        SELECT cp.id, cp.name, cp.description
        FROM capabilities cp
        INNER JOIN capacity_bootcamp cb
        ON cb.id_capacity = cp.id
        WHERE cb.id_bootcamp = :bootcampId
    """)
    Flux<CapacityEntity> findCapabilitiesByBootcampId(Long bootcampId);
}
