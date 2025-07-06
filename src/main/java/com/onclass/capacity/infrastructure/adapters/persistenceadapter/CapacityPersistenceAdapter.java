package com.onclass.capacity.infrastructure.adapters.persistenceadapter;

import com.onclass.capacity.domain.model.Capacity;
import com.onclass.capacity.domain.spi.CapacityPersistencePort;
import com.onclass.capacity.infrastructure.adapters.persistenceadapter.mapper.CapacityEntityMapper;
import com.onclass.capacity.infrastructure.adapters.persistenceadapter.repository.CapacityRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
public class CapacityPersistenceAdapter implements CapacityPersistencePort {
    private final CapacityRepository capacityRepository;
    private final CapacityEntityMapper capacityEntityMapper;

    @Override
    public Mono<Capacity> upsert(Capacity capacity) {
        return capacityRepository.save(capacityEntityMapper.toEntity(capacity))
                .map(capacityEntityMapper::toModel);
    }

    @Override
    public Mono<Capacity> findByName(String nameCapacity) {
        return capacityRepository.findByName(nameCapacity)
                .map(capacityEntityMapper::toModel);
    }

    @Override
    public Flux<Capacity> findPaginatedAndSortByName(String order, Integer size, Integer page) {
        return capacityRepository.findAndSortByName( page, size).map(capacityEntityMapper::toModel);
    }

    @Override
    public Flux<Capacity> findAllByIds(List<Long> capabilitiesIds) {
        return capacityRepository.findAllById(capabilitiesIds).map(capacityEntityMapper::toModel);
    }

    @Override
    public Mono<Long> countCapabilities() {
        return capacityRepository.count();
    }
}
