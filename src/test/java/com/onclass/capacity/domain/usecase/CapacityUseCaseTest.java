package com.onclass.capacity.domain.usecase;

import com.onclass.capacity.domain.enums.ItemSortList;
import com.onclass.capacity.domain.enums.OrderList;
import com.onclass.capacity.domain.exceptions.EntityAlreadyExistException;
import com.onclass.capacity.domain.exceptions.ParamRequiredMissingException;
import com.onclass.capacity.domain.model.Capacity;
import com.onclass.capacity.domain.model.spi.CapabilitiesBasicPerBootcamp;
import com.onclass.capacity.domain.model.spi.CapacityTechnologies;
import com.onclass.capacity.domain.spi.CapacityPersistencePort;
import com.onclass.capacity.domain.spi.TechnologiesGateway;
import com.onclass.capacity.domain.utilities.CustomPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
class CapacityUseCaseTest {

    @Mock
    private CapacityPersistencePort capacityPersistencePort;
    @Mock
    private TechnologiesGateway technologiesGateway;
    @Mock
    private TransactionalOperator transactionalOperator;

    @InjectMocks
    private CapacityUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new CapacityUseCase(capacityPersistencePort, technologiesGateway, transactionalOperator);
    }

    @Test
    void registerCapacity_shouldSaveUniqueCapacity() {
        Capacity capacity = new Capacity(null, "Capacidad", "Desc", List.of(1L, 2L, 3L));
        Capacity capacityBD = new Capacity(1L, "Capacidad", "Desc", List.of(1L, 2L, 3L));

        when(capacityPersistencePort.findByName("Capacidad")).thenReturn(Mono.empty());
        when(capacityPersistencePort.upsert(capacity)).thenReturn(Mono.just(capacityBD));
        when(technologiesGateway.assignTechnologiesToCapacity(anyLong(), anyList())).thenReturn(Mono.empty());
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(useCase.registerCapacity(capacity))
                .expectNext(capacityBD)
                .verifyComplete();

        verify(capacityPersistencePort).findByName("Capacidad");
        verify(capacityPersistencePort).upsert(capacity);
        verify(technologiesGateway).assignTechnologiesToCapacity(capacityBD.id(), capacity.technologies());
    }

    @Test
    void registerCapacity_shouldErrorIfExists() {
        Capacity capacity = new Capacity(null, "Capacidad", "Desc", List.of(1L, 2L, 3L));
        Capacity capacityBD = new Capacity(1L, "Capacidad", "Desc", List.of(1L, 2L, 3L));

        when(capacityPersistencePort.findByName("Capacidad")).thenReturn(Mono.just(capacityBD));
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(useCase.registerCapacity(capacity))
                .expectError(EntityAlreadyExistException.class)
                .verify();
    }

    @Test
    void listCapabilities_shouldReturnByName() {
        Capacity capacity = new Capacity(1L, "Capacidad", "Desc", List.of());
        when(capacityPersistencePort.findPaginatedAndSortByName(anyString(), anyInt(), anyInt()))
                .thenReturn(Flux.just(capacity));
        when(capacityPersistencePort.countCapabilities()).thenReturn(Mono.just(1L));
        when(technologiesGateway.getTechnologiesByCapabilitiesIds(anyList()))
                .thenReturn(Mono.just(List.of()));

        StepVerifier.create(useCase.listCapabilities(OrderList.ASCENDANT, ItemSortList.NAME, 0, 10))
                .assertNext(page -> {
                    assert page.getData().size() == 1;
                    assert page.getTotalItems() == 1L;
                })
                .verifyComplete();
    }

    @Test
    void listCapabilities_shouldReturnByTechnologies() {
        CapacityTechnologies capacityTechnologies = new CapacityTechnologies(1L, List.of());
        CustomPage<CapacityTechnologies> pageTechnologies = CustomPage.buildCustomPage(List.of(capacityTechnologies), 0, 2, 4L);

        when(technologiesGateway.getSortTechnologiesByCapabilities(anyString(), anyInt(), anyInt()))
                .thenReturn(Mono.just(pageTechnologies));
        when(capacityPersistencePort.findAllByIds(anyList()))
                .thenReturn(Flux.just(new Capacity(1L, "Capacidad", "Desc", List.of())));

        StepVerifier.create(useCase.listCapabilities(OrderList.ASCENDANT, ItemSortList.CAPABILITIES, 0, 10))
                .assertNext(page -> {
                    assert page.getData().size() == 1;
                })
                .verifyComplete();
    }

    @Test
    void assignCapabilitiesToBootcamp_shouldAssignIfAllExist() {
        List<Long> ids = List.of(1L, 2L);
        when(capacityPersistencePort.findAllByIds(ids)).thenReturn(Flux.just(
                new Capacity(1L, "Capacidad", "Desc", List.of()),
                new Capacity(2L, "Otra", "Desc", List.of())
        ));
        when(capacityPersistencePort.assignCapabilitiesToBootcamp(1L, ids)).thenReturn(Mono.empty());
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(useCase.assignCapabilitiesToBootcamp(1L, ids))
                .verifyComplete();
    }

    @Test
    void assignCapabilitiesToBootcamp_shouldErrorIfMissingIds() {
        List<Long> ids = List.of();
        StepVerifier.create(useCase.assignCapabilitiesToBootcamp(1L, ids))
                .expectError(ParamRequiredMissingException.class)
                .verify();
    }

    @Test
    void getCapabilitiesByBootcampsIds_shouldReturnList() {
        List<Long> bootcampIds = List.of(1L, 2L);
        CapabilitiesBasicPerBootcamp basic = new CapabilitiesBasicPerBootcamp(1L, List.of());
        when(capacityPersistencePort.findCapabilitiesByBootcampsIds(bootcampIds)).thenReturn(Flux.just(basic));
        when(technologiesGateway.getTechnologiesByCapabilitiesIds(anyList())).thenReturn(Mono.just(List.of()));

        StepVerifier.create(useCase.getCapabilitiesByBootcampsIds(bootcampIds))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getCapabilitiesByBootcampsIds_shouldErrorIfEmpty() {
        StepVerifier.create(useCase.getCapabilitiesByBootcampsIds(List.of()))
                .expectError(ParamRequiredMissingException.class)
                .verify();
    }

    @Test
    void getSortCapabilitiesByBootcamps_shouldReturnPage() {
        CapabilitiesBasicPerBootcamp basic = new CapabilitiesBasicPerBootcamp(1L, List.of());
        when(capacityPersistencePort.findPaginatedAndSortByBootcampNumber(anyString(), anyInt(), anyInt()))
                .thenReturn(Flux.just(basic));
        when(technologiesGateway.getTechnologiesByCapabilitiesIds(anyList())).thenReturn(Mono.just(List.of()));
        when(capacityPersistencePort.countCapabilitiesPerBootcamps()).thenReturn(Mono.just(1L));

        StepVerifier.create(useCase.getSortCapabilitiesByBootcamps(OrderList.ASCENDANT, 10, 0))
                .assertNext(page -> {
                    assert page.getData().size() == 1;
                })
                .verifyComplete();
    }

    @Test
    void deleteCapabilitiesByBootcampId_shouldDeleteIfValid() {
        Capacity cap = new Capacity(1L, "Capacidad", "Desc", List.of());
        when(capacityPersistencePort.findCapabilitiesByBootcampId(1L)).thenReturn(Flux.just(cap));
        when(capacityPersistencePort.verifyOtherAssignations(anyLong(), anyLong())).thenReturn(Mono.just(false));
        when(capacityPersistencePort.deleteAllCapabilities(anyList())).thenReturn(Mono.empty());
        when(capacityPersistencePort.deleteAllAssignations(1L)).thenReturn(Mono.empty());
        when(technologiesGateway.deleteTechnologiesByCapabilitiesIds(anyList())).thenReturn(Mono.empty());
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(useCase.deleteCapabilitiesByBootcampId(1L))
                .verifyComplete();
    }
}
