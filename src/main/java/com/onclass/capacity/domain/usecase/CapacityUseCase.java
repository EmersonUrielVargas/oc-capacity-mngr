package com.onclass.capacity.domain.usecase;

import com.onclass.capacity.domain.api.CapacityServicePort;
import com.onclass.capacity.domain.enums.ItemSortList;
import com.onclass.capacity.domain.enums.OrderList;
import com.onclass.capacity.domain.enums.TechnicalMessage;
import com.onclass.capacity.domain.exceptions.EntityAlreadyExistException;
import com.onclass.capacity.domain.exceptions.EntityNotFoundException;
import com.onclass.capacity.domain.exceptions.InvalidFormatParamException;
import com.onclass.capacity.domain.exceptions.ParamRequiredMissingException;
import com.onclass.capacity.domain.model.*;
import com.onclass.capacity.domain.model.spi.*;
import com.onclass.capacity.domain.spi.CapacityPersistencePort;
import com.onclass.capacity.domain.spi.TechnologiesGateway;
import com.onclass.capacity.domain.utilities.CustomPage;
import com.onclass.capacity.domain.validators.Validator;
import lombok.AllArgsConstructor;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class CapacityUseCase implements CapacityServicePort {

    private final CapacityPersistencePort capacityPersistencePort;
    private final TechnologiesGateway technologiesGateway;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<Capacity> registerCapacity(Capacity capacity) {
        return transactionalOperator.transactional(
            Validator.validateCapacity(capacity)
            .then( capacityPersistencePort.findByName(capacity.name()))
            .flatMap(capacityFound -> Mono.error(new EntityAlreadyExistException(TechnicalMessage.CAPACITY_ALREADY_EXISTS)))
            .switchIfEmpty(
                Mono.defer(()->
                    capacityPersistencePort.upsert(capacity)
                    .flatMap(capacitySaved ->
                        technologiesGateway.assignTechnologiesToCapacity(capacitySaved.id(), capacity.technologies())
                        .then(Mono.just(capacitySaved))
                    )
                )
            ).cast(Capacity.class)
        );
    }

    @Override
    public Mono<CustomPage<CapacityList>> listCapabilities(OrderList order, ItemSortList item, Integer page, Integer size) {
        return switch (item){
            case CAPABILITIES -> listCapabilitiesSortByTechnologies(order, page, size);
            case NAME -> listCapabilitiesSortByName(order, page, size);
        };
    }

    @Override
    public Mono<Void> assignCapabilitiesToBootcamp(Long bootcampId, List<Long> capabilitiesIds) {
        if (capabilitiesIds == null || capabilitiesIds.isEmpty()) {
            return Mono.error(new ParamRequiredMissingException(TechnicalMessage.MISSING_REQUIRED_PARAM));
        }
        return transactionalOperator.transactional(
            capacityPersistencePort.findAllByIds(capabilitiesIds)
            .collectList()
            .flatMap(capabilitiesList ->{
                if(capabilitiesList.size() == capabilitiesIds.size()){
                    return capacityPersistencePort.assignCapabilitiesToBootcamp(bootcampId, capabilitiesIds);
                }else{
                    return Mono.error(new EntityNotFoundException(TechnicalMessage.SOME_CAPABILITIES_NOT_FOUND));
                }
            })
        );
    }

    @Override
    public Flux<CapabilitiesPerBootcamp> getCapabilitiesByBootcampsIds(List<Long> bootcampIds) {
        if (bootcampIds.isEmpty()) {
            return Flux.error(new ParamRequiredMissingException(TechnicalMessage.MISSING_REQUIRED_PARAM));
        }
        return capacityPersistencePort.findCapabilitiesByBootcampsIds(bootcampIds)
            .collectList()
            .flatMapMany( listCapabilities -> {
                if (listCapabilities.isEmpty()) {
                    return Flux.empty();
                }
                List<Long> listIds = listCapabilities.stream()
                    .map(CapabilitiesBasicPerBootcamp::capabilities)
                    .flatMap(List::stream)
                    .map(CapacityBasicItem::id)
                    .distinct()
                    .toList();
                return technologiesGateway.getTechnologiesByCapabilitiesIds(listIds)
                    .flatMapMany( capabilitiesTechnologies ->
                        Flux.fromStream(listCapabilities.stream()
                            .map( bootcamp -> enrichBootcampInfo(bootcamp, capabilitiesTechnologies))
                        )
                    );
                }
            );
    }

    @Override
    public Mono<CustomPage<CapabilitiesPerBootcamp>> getSortCapabilitiesByBootcamps(OrderList order, Integer size, Integer page) {
        return capacityPersistencePort.findPaginatedAndSortByBootcampNumber(order.getMessage(), size, page)
            .collectList()
            .flatMap( listCapabilities -> {
                if (listCapabilities.isEmpty()) {
                    return Mono.just(Collections.checkedList(List.of(),CapabilitiesPerBootcamp.class));
                }
                List<Long> listIds = listCapabilities.stream()
                    .flatMap(bootcampInfo -> bootcampInfo.capabilities().stream())
                    .map(CapacityBasicItem::id)
                    .distinct()
                    .toList();
                return technologiesGateway.getTechnologiesByCapabilitiesIds(listIds)
                    .map( capabilitiesTechnologies ->
                        listCapabilities.stream()
                        .map( bootcampCapabilities -> enrichBootcampInfo(bootcampCapabilities, capabilitiesTechnologies))
                        .toList()
                    );
                }
            ).zipWith(capacityPersistencePort.countCapabilitiesPerBootcamps())
            .map(tuple ->
	            CustomPage.buildCustomPage(tuple.getT1(), page, size, tuple.getT2())
            );
    }

    @Override
    public Mono<Void> deleteCapabilitiesByBootcampId(Long bootcampId) {
        return transactionalOperator.transactional(
            Validator.validationCondition(bootcampId != null, new InvalidFormatParamException(TechnicalMessage.INVALID_PARAMETERS))
            .then(
                capacityPersistencePort.findCapabilitiesByBootcampId(bootcampId)
                .flatMap( capacity ->
                    capacityPersistencePort.verifyOtherAssignations(capacity.id(), bootcampId)
                    .filter(haveOtherAssign -> !haveOtherAssign)
                    .map(valid -> capacity.id())
                ).collectList()
                .flatMap( listCapabilitiesIds ->{
                    if (listCapabilitiesIds.isEmpty()) {
                        return Mono.empty();
                    }
                    return capacityPersistencePort.deleteAllCapabilities(listCapabilitiesIds)
                        .then(
                            capacityPersistencePort.deleteAllAssignations(bootcampId)
                            .then( technologiesGateway.deleteTechnologiesByCapabilitiesIds(listCapabilitiesIds))
                        );
                })
            )
        );
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
                        .map( capacity -> enrichCapacityInfo(capacity, capabilitiesTechnologies))
                        .toList()
                    );
                }
            ).zipWith(capacityPersistencePort.countCapabilities())
            .map(tuple ->
                CustomPage.buildCustomPage(tuple.getT1(), page, size, tuple.getT2())
            );
    }

    private CapacityList enrichCapacityInfo(Capacity capacityBasic, List<CapacityTechnologies> capacityTechnologies){
        return new CapacityList(
                capacityBasic.id(),
                capacityBasic.name(),
                findTechnologiesByCapacityID(capacityBasic.id(), capacityTechnologies));
    }

    private CapabilitiesPerBootcamp enrichBootcampInfo(CapabilitiesBasicPerBootcamp bootcampPerCapacity, List<CapacityTechnologies> capacityTechnologies){
        List<CapacityItem> capacities = bootcampPerCapacity.capabilities()
                        .stream()
                        .map(capacityBasic ->
                            new CapacityItem(capacityBasic.id(), capacityBasic.name(), findTechnologiesByCapacityID(capacityBasic.id(), capacityTechnologies) )
                        ).toList();

        return new CapabilitiesPerBootcamp(bootcampPerCapacity.id(),capacities);
    }

    private List<TechnologyItem> findTechnologiesByCapacityID(Long capacityId, List<CapacityTechnologies> capacityTechnologies){
        return capacityTechnologies.stream()
                .filter(item -> Objects.equals(item.id(), capacityId))
                .findFirst()
                .map(CapacityTechnologies::technologies)
                .orElse(Collections.checkedList(List.of(),TechnologyItem.class));
    }

    private <T> Comparator<T> verifyOrder(OrderList order, Comparator<T> baseComparator){
        return order.equals(OrderList.ASCENDANT)? baseComparator: baseComparator.reversed();
    }
}
