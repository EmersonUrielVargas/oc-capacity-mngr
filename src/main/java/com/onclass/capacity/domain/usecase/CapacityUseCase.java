package com.onclass.capacity.domain.usecase;

import com.onclass.capacity.domain.api.CapacityServicePort;
import com.onclass.capacity.domain.enums.ItemSortList;
import com.onclass.capacity.domain.enums.OrderList;
import com.onclass.capacity.domain.enums.TechnicalMessage;
import com.onclass.capacity.domain.exceptions.EntityAlreadyExistException;
import com.onclass.capacity.domain.model.Capacity;
import com.onclass.capacity.domain.model.CapacityList;
import com.onclass.capacity.domain.model.CapacityTechnologies;
import com.onclass.capacity.domain.model.TechnologyItem;
import com.onclass.capacity.domain.spi.CapacityPersistencePort;
import com.onclass.capacity.domain.spi.TechnologiesGateway;
import com.onclass.capacity.domain.utilities.CustomPage;
import com.onclass.capacity.domain.validators.Validator;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class CapacityUseCase implements CapacityServicePort {

    private final CapacityPersistencePort capacityPersistencePort;
    private final TechnologiesGateway technologiesGateway;

    @Override
    public Mono<Capacity> registerCapacity(Capacity capacity) {
        return Validator.validateCapacity(capacity)
            .then( capacityPersistencePort.findByName(capacity.name()))
            .flatMap(capacityFound -> Mono.error(new EntityAlreadyExistException(TechnicalMessage.CAPACITY_ALREADY_EXISTS)))
            .switchIfEmpty(
                Mono.defer(()->
                    capacityPersistencePort.upsert(capacity)
                    .flatMap(capacitySaved ->
                        technologiesGateway.assignTechnologiesToCapacity(capacitySaved.id(), capacity.technologies())
                        .thenReturn(capacitySaved)
                    )
                )
            ).cast(Capacity.class);
    }

    @Override
    public Mono<CustomPage<CapacityList>> listCapabilities(OrderList order, ItemSortList item, Integer page, Integer size) {
        return switch (item){
            case CAPABILITIES -> listCapabilitiesSortByTechnologies(order, page, size);
            case NAME -> listCapabilitiesSortByName(order, page, size);
        };
    }

    private Mono<CustomPage<CapacityList>> listCapabilitiesSortByTechnologies(OrderList order, Integer page, Integer size) {
        return technologiesGateway.getSortTechnologiesByCapabilities(order.getMessage(), size, page)
            .flatMap(pageCapabilities -> {
                List<Long> listIds = pageCapabilities.getData().stream().map(CapacityTechnologies::id).toList();
                return capacityPersistencePort.findAllByIds(listIds)
                    .map( capacity -> enrichCapacityInfo(capacity,pageCapabilities.getData()))
                    .collectSortedList(
                        verifyOrder(order, Comparator.comparing(item -> item.technologies().size()))
                    )
                    .map( listCapabilitiesComplete ->
                        new CustomPage<CapacityList>(listCapabilitiesComplete, pageCapabilities)
                    );
            });
    }

    private Mono<CustomPage<CapacityList>> listCapabilitiesSortByName(OrderList order, Integer page, Integer size) {
        return capacityPersistencePort.findPaginatedAndSortByName(order.getMessage(), size, page)
            .collectList()
            .flatMap( listCapabilities -> {
                if (listCapabilities.isEmpty()) {
                    return Mono.just(Collections.checkedList(List.of(),CapacityList.class));
                }
                List<Long> listIds = listCapabilities.stream().map(Capacity::id).toList();
                return technologiesGateway.getTechnologiesByCapabilitiesIds(listIds)
                    .map( capabilitiesTechnologies ->
                        listCapabilities.stream()
                        .map( bootcamp -> enrichCapacityInfo(bootcamp, capabilitiesTechnologies))
                        .toList()
                    );
                }
            ).zipWith(capacityPersistencePort.countCapabilities())
            .map(tuple ->
                CustomPage.buildCustomPage(tuple.getT1(), page, size, tuple.getT2())
            );
    }

    private CapacityList enrichCapacityInfo(Capacity capacityBasic, List<CapacityTechnologies> capacityTechnologies){
        List<TechnologyItem> technologyItems =  capacityTechnologies.stream()
                .filter(item -> Objects.equals(item.id(), capacityBasic.id()))
                .findFirst()
                .map(CapacityTechnologies::technologies)
                .orElse(Collections.checkedList(List.of(),TechnologyItem.class));
        return new CapacityList(
                capacityBasic.id(),
                capacityBasic.name(),
                capacityBasic.description(),
                technologyItems);
    }

    private <T> Comparator<T> verifyOrder(OrderList order, Comparator<T> baseComparator){
        return order.equals(OrderList.ASCENDANT)? baseComparator: baseComparator.reversed();
    }
}
