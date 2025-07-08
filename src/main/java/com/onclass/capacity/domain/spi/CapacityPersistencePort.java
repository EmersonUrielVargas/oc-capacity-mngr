package com.onclass.capacity.domain.spi;

import com.onclass.capacity.domain.model.spi.CapabilitiesBasicPerBootcamp;
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
    Mono<Void> assignCapabilitiesToBootcamp(Long bootcampId, List<Long> capacityIds);
    Flux<CapabilitiesBasicPerBootcamp> findCapabilitiesByBootcampsIds(List<Long> bootcampsIds);
    Flux<CapabilitiesBasicPerBootcamp> findPaginatedAndSortByBootcampNumber(String order, Integer size, Integer page);
    Mono<Long> countCapabilitiesPerBootcamps();
    Flux<Capacity> findCapabilitiesByBootcampId(Long bootcampId);
    Mono<Void> deleteAllCapabilities(List<Long> capabilitiesIds);
    Mono<Void> deleteAllAssignations(Long bootcampId);
    Mono<Boolean> verifyOtherAssignations(Long capacityId, Long bootcampId);
}
