package com.onclass.capacity.domain.spi;

import com.onclass.capacity.domain.model.Capacity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacityPersistencePort {
    Mono<Capacity> upsert(Capacity capacity);
    Mono<Capacity> findByName(String nameCapacity);
    Flux<Capacity> findPaginatedAndSortByName(String order, Integer size, Integer page);
    Flux<Capacity> findAllByIds(List<Long> capabilitiesIds);
    Mono<Long> countCapabilities();
}
