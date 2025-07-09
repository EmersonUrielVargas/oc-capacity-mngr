package com.onclass.capacity.infrastructure.entrypoints.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onclass.capacity.domain.api.CapacityServicePort;
import com.onclass.capacity.domain.enums.TechnicalMessage;
import com.onclass.capacity.domain.exceptions.BusinessException;
import com.onclass.capacity.domain.exceptions.TechnicalException;
import com.onclass.capacity.domain.model.Capacity;
import com.onclass.capacity.domain.model.spi.CapabilitiesPerBootcamp;
import com.onclass.capacity.domain.model.spi.CapacityList;
import com.onclass.capacity.domain.utilities.CustomPage;
import com.onclass.capacity.infrastructure.entrypoints.RouterRest;
import com.onclass.capacity.infrastructure.entrypoints.dto.request.AssignCapabilitiesDTO;
import com.onclass.capacity.infrastructure.entrypoints.dto.request.CreateCapacityDTO;
import com.onclass.capacity.infrastructure.entrypoints.mapper.CapacityMapper;
import com.onclass.capacity.infrastructure.entrypoints.util.Constants;
import com.onclass.capacity.infrastructure.entrypoints.util.ErrorDTO;
import com.onclass.capacity.infrastructure.entrypoints.util.ResponseDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, CapacityHandlerImpl.class})
@WebFluxTest
class CapacityHandlerImplTest {

    @MockitoBean
    private CapacityServicePort capacityServicePort;
    @MockitoBean
    private CapacityMapper capacityMapper;

    @Autowired
    private WebTestClient webClient;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("POST /capacity")
    class CreateCapacityTests {
        private final String pathTest = Constants.PATH_POST_CAPABILITY;

        @Test
        void testCreateCapacitySuccessful() throws Exception {
            CreateCapacityDTO dto = CreateCapacityDTO.builder()
                .name("capacity")
                .description("capacity description")
                .technologies(List.of(1L, 2L))
                .build();
            Capacity capacity = new Capacity(1L, "Capacidad", "desc", List.of(1L, 2L));
            String jsonBody = objectMapper.writeValueAsString(dto);

            when(capacityMapper.toCapacity(any())).thenReturn(capacity);
            when(capacityServicePort.registerCapacity(any())).thenReturn(Mono.just(capacity));

            webClient.post()
                    .uri(pathTest)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jsonBody)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(String.class)
                    .value(body -> Assertions.assertThat(body).contains(TechnicalMessage.CAPACITY_CREATED.getMessage()));
        }

        @Test
        void testCreateCapacityBusinessError() throws Exception {
            CreateCapacityDTO dto = CreateCapacityDTO.builder()
                .name("capacity")
                .description("capacity description")
                .technologies(List.of(1L, 2L))
                .build();
            String jsonBody = objectMapper.writeValueAsString(dto);
            Capacity capacity = new Capacity(1L, "Capacidad", "desc", List.of());

            when(capacityMapper.toCapacity(any())).thenReturn(capacity);
            when(capacityServicePort.registerCapacity(any()))
                    .thenReturn(Mono.error(new BusinessException(TechnicalMessage.CAPACITY_ALREADY_EXISTS)));

            webClient.post()
                    .uri(pathTest)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jsonBody)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT.value())
                    .expectBody(ErrorDTO.class)
                    .value(error -> Assertions.assertThat(error.getCode()).isEqualTo(TechnicalMessage.CAPACITY_ALREADY_EXISTS.getCode()));
        }

        @Test
        void testCreateCapacityTechnicalError() throws Exception {
            CreateCapacityDTO dto = CreateCapacityDTO.builder()
                .name("capacity")
                .description("capacity description")
                .technologies(List.of(1L, 2L))
                .build();
            String jsonBody = objectMapper.writeValueAsString(dto);
            Capacity capacity = new Capacity(1L, "Capacidad", "desc", List.of());

            when(capacityMapper.toCapacity(any())).thenReturn(capacity);
            when(capacityServicePort.registerCapacity(any()))
                    .thenReturn(Mono.error(new TechnicalException(TechnicalMessage.INTERNAL_ERROR)));

            webClient.post()
                    .uri(pathTest)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jsonBody)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .expectBody(ErrorDTO.class)
                    .value(error -> Assertions.assertThat(error.getCode()).isEqualTo(TechnicalMessage.INTERNAL_ERROR.getCode()));
        }
    }

    @Nested
    @DisplayName("GET /capacity/all")
    class GetAllCapabilitiesTests {
        private final String pathTest = Constants.PATH_GET_ALL_CAPABILITIES;

        @Test
        void testGetAllCapabilitiesSuccessful() {
            CustomPage<CapacityList> page = new CustomPage<>();
            when(capacityServicePort.listCapabilities(any(), any(), anyInt(), anyInt()))
                    .thenReturn(Mono.just(page));

            webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(pathTest)
                            .queryParam(Constants.QUERY_PARAM_ORDER_SORT, "ASC")
                            .queryParam(Constants.QUERY_PARAM_ITEM_SORT, "name")
                            .queryParam(Constants.QUERY_PARAM_PAGE, "0")
                            .queryParam(Constants.QUERY_PARAM_SIZE, "10")
                            .build())
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .json("{}");
        }

        @Test
        void testGetAllCapabilitiesBusinessError() {
            when(capacityServicePort.listCapabilities(any(), any(), anyInt(), anyInt()))
                    .thenReturn(Mono.error(new BusinessException(TechnicalMessage.CAPACITY_ALREADY_EXISTS)));

            webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(pathTest)
                            .queryParam(Constants.QUERY_PARAM_ORDER_SORT, "ASC")
                            .queryParam(Constants.QUERY_PARAM_ITEM_SORT, "name")
                            .queryParam(Constants.QUERY_PARAM_PAGE, "0")
                            .queryParam(Constants.QUERY_PARAM_SIZE, "10")
                            .build())
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT.value());
        }
    }

    @Nested
    @DisplayName("POST /capacity/assign")
    class AssignCapabilitiesTests {
        private final String pathTest = Constants.PATH_POST_ASSIGN_CAPABILITIES;

        @Test
        void testAssignCapabilitiesSuccessful() throws Exception {
            AssignCapabilitiesDTO dto = AssignCapabilitiesDTO.builder().bootcampId(1L).capabilitiesIds(List.of(2L, 3L)).build();
            String jsonBody = objectMapper.writeValueAsString(dto);

            when(capacityServicePort.assignCapabilitiesToBootcamp(anyLong(), anyList()))
                    .thenReturn(Mono.empty());

            webClient.post()
                    .uri(pathTest)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jsonBody)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(ResponseDTO.class);
        }
    }

    @Nested
    @DisplayName("GET /capacity/bootcamps_ids")
    class GetCapabilitiesByBootcampsIdsTests {
        private final String pathTest = Constants.PATH_GET_CAPABILITIES_BY_BOOTCAMPS_IDS;

        @Test
        void testGetCapabilitiesByBootcampsIdsSuccessful() {
            CapabilitiesPerBootcamp item = new CapabilitiesPerBootcamp(1L, List.of());
            when(capacityServicePort.getCapabilitiesByBootcampsIds(anyList()))
                    .thenReturn(Flux.just(item));

            webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(pathTest)
                            .queryParam(Constants.QUERY_PARAM_CAPABILITIES_IDS, "1,2")
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$[0].id").isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("DELETE /capacity/bootcamp/{id}")
    class DeleteCapabilitiesByBootcampTests {
        private final String pathTest = Constants.PATH_DELETE_CAPABILITIES_BY_BOOTCAMP.replace("{id}", "1");

        @Test
        void testDeleteCapabilitiesByBootcampSuccessful() {
            when(capacityServicePort.deleteCapabilitiesByBootcampId(anyLong()))
                    .thenReturn(Mono.empty());

            webClient.delete()
                    .uri(pathTest)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(ResponseDTO.class);
        }
    }
}