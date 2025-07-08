package com.onclass.capacity.infrastructure.adapters.persistenceadapter;

import com.onclass.capacity.domain.enums.OrderList;
import com.onclass.capacity.domain.model.*;
import com.onclass.capacity.domain.model.spi.CapabilitiesBasicPerBootcamp;
import com.onclass.capacity.domain.model.spi.CapacityBasicItem;
import com.onclass.capacity.domain.spi.CapacityPersistencePort;
import com.onclass.capacity.infrastructure.adapters.persistenceadapter.entity.CapacityBootcampEntity;
import com.onclass.capacity.infrastructure.adapters.persistenceadapter.projection.BootcampCountProjection;
import com.onclass.capacity.infrastructure.adapters.persistenceadapter.projection.CapacityBootcampProjection;
import com.onclass.capacity.infrastructure.adapters.persistenceadapter.mapper.CapacityEntityMapper;
import com.onclass.capacity.infrastructure.adapters.persistenceadapter.repository.CapacityBootcampRepository;
import com.onclass.capacity.infrastructure.adapters.persistenceadapter.repository.CapacityRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
public class CapacityPersistenceAdapter implements CapacityPersistencePort {
    private final CapacityRepository capacityRepository;
    private final CapacityBootcampRepository capacityBootcampRepository;
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
        if (order.equals(OrderList.DESCENDANT.getMessage())){
            return capacityRepository.findAndSortByNameDesc( page*size, size).map(capacityEntityMapper::toModel);
        }else{
            return capacityRepository.findAndSortByNameAsc( page*size, size).map(capacityEntityMapper::toModel);
        }
    }

    @Override
    public Flux<Capacity> findAllByIds(List<Long> capabilitiesIds) {
        return capacityRepository.findAllById(capabilitiesIds).map(capacityEntityMapper::toModel);
    }

    @Override
    public Mono<Long> countCapabilities() {
        return capacityRepository.count();
    }

    @Override
    public Mono<Void> assignCapabilitiesToBootcamp(Long bootcampId, List<Long> capacityIds) {
        return Flux.fromStream(capacityIds.stream())
            .flatMap( capacityId ->
                capacityBootcampRepository.save(
                    CapacityBootcampEntity.builder()
                        .bootcampId(bootcampId)
                        .capacityId(capacityId).build())
            ).then();
    }

    @Override
    public Flux<CapabilitiesBasicPerBootcamp> findCapabilitiesByBootcampsIds(List<Long> bootcampsIds) {
        return capacityRepository.findCapabilitiesByBootcampsIds(bootcampsIds)
            .groupBy(CapacityBootcampProjection::getBootcampId)
            .flatMap(capabilitiesGroup ->
                capabilitiesGroup.map(
                    capacity -> new CapacityBasicItem(capacity.getCapacityId(), capacity.getCapacityName())
                ).collectList()
                .map(capabilities -> new CapabilitiesBasicPerBootcamp(capabilitiesGroup.key(), capabilities))
            );
    }

    @Override
    public Flux<CapabilitiesBasicPerBootcamp> findPaginatedAndSortByBootcampNumber(String order, Integer size, Integer page) {
        return sortBootcampCount(order, size, page)
            .map(BootcampCountProjection::getBootcampId)
            .collectList()
            .flatMapMany(this::findCapabilitiesByBootcampsIds);
    }

    @Override
    public Mono<Long> countCapabilitiesPerBootcamps() {
        return capacityRepository.countCapabilitiesPerBootcamps();
    }

    @Override
    public Flux<Capacity> findCapabilitiesByBootcampId(Long bootcampId) {
        return capacityRepository.findCapabilitiesByBootcampId(bootcampId).map(capacityEntityMapper::toModel);
    }

    @Override
    public Mono<Void> deleteAllCapabilities(List<Long> capabilitiesIds) {
        return capacityRepository.deleteAllById(capabilitiesIds);
    }

    @Override
    public Mono<Void> deleteAllAssignations(Long bootcampId) {
        return capacityBootcampRepository.deleteAllAssignations(bootcampId);
    }

    @Override
    public Mono<Boolean> verifyOtherAssignations(Long capacityId, Long bootcampId) {
        return capacityBootcampRepository.verifyOtherAssignations(capacityId, bootcampId);
    }

    private Flux<BootcampCountProjection> sortBootcampCount(String order, Integer size, Integer page){
         if (order.equals(OrderList.DESCENDANT.getMessage())){
            return capacityRepository.findBootcampIdsOrderByCapabilitiesCountDesc(size, page*size);
        }else{
            return capacityRepository.findBootcampIdsOrderByCapabilitiesCountAsc(size, page*size);
        }
    }
}
