package com.onclass.capacity.infrastructure.adapters.persistenceadapter.repository;

import com.onclass.capacity.infrastructure.adapters.persistenceadapter.entity.CapacityEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface CapacityRepository extends ReactiveCrudRepository<CapacityEntity, Long> {
    Mono<CapacityEntity> findByName(String name);

    @Query("SELECT * FROM capabilities ORDER BY name ASC LIMIT :size OFFSET :page")
    Flux<CapacityEntity> findAndSortByName(int page, int size);
}
