package com.onclass.capacity.infrastructure.adapters.technologiesadapter;

import com.onclass.capacity.domain.enums.TechnicalMessage;
import com.onclass.capacity.domain.exceptions.BusinessException;
import com.onclass.capacity.domain.exceptions.TechnicalException;
import com.onclass.capacity.domain.model.spi.CapacityTechnologies;
import com.onclass.capacity.domain.spi.TechnologiesGateway;
import com.onclass.capacity.domain.utilities.CustomPage;
import com.onclass.capacity.infrastructure.adapters.technologiesadapter.dto.TechnologyMngrProperties;
import com.onclass.capacity.infrastructure.adapters.technologiesadapter.dto.request.TechnologyAssign;
import com.onclass.capacity.infrastructure.entrypoints.util.Constants;
import com.onclass.capacity.infrastructure.entrypoints.util.ResponseDTO;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static com.onclass.capacity.infrastructure.adapters.technologiesadapter.util.Constants.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class TechnologyMngrAdapter implements TechnologiesGateway {

    private final WebClient webClient;
    private final TechnologyMngrProperties emailValidatorProperties;
    private final Retry retry;
    private final Bulkhead bulkhead;

    private Mono<Throwable> buildErrorResponse(ClientResponse response, TechnicalMessage technicalMessage) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty(NO_ADITIONAL_ERROR_DETAILS)
                .flatMap(errorBody -> {
                    log.error(STRING_ERROR_BODY_DATA, errorBody);
                    return Mono.error(
                            response.statusCode().is5xxServerError() ?
                                    new TechnicalException(technicalMessage):
                                    new BusinessException(technicalMessage));
                });
    }

    @Override
    @CircuitBreaker(name = "technologyMngr", fallbackMethod = "fallback")
    public Mono<ResponseDTO> assignTechnologiesToCapacity(Long capacityId, List<Long> technologiesIds) {
        log.info(LOG_START_ASSIGN_TECHNOLOGIES, technologiesIds, capacityId);
        TechnologyAssign requestBody = new TechnologyAssign(capacityId, technologiesIds);
        return webClient.post()
            .uri(uriBuilder -> uriBuilder
                    .path(TECHNOLOGY_MNGR_PATH_ASSIGN)
                    .build())
            .header(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> buildErrorResponse(response, TechnicalMessage.TECHNOLOGIES_NOT_FOUND))
            .onStatus(HttpStatusCode::is5xxServerError, response -> buildErrorResponse(response, TechnicalMessage.ERROR_TECHNOLOGIES_ADAPTER))
            .bodyToMono(ResponseDTO.class)
            .doOnNext(response -> log.info(LOG_API_RESPONSE, response))
            .doOnSuccess(response -> log.info("Completed assign technologies in capacity"))
            .transformDeferred(RetryOperator.of(retry))
            .transformDeferred(mono -> Mono.defer(() -> bulkhead.executeSupplier(() -> mono)))
            .doOnError(error -> log.error(ERROR_LOG_CAPACITY_MNGR, error.getMessage()));
    }

    @Override
    public Mono<List<CapacityTechnologies>> getTechnologiesByCapabilitiesIds(List<Long> capabilitiesIds) {
        log.info(LOG_START_GET_TECHNOLOGIES_BY_CAPABILITIES_IDS, capabilitiesIds);
        String idsParam = capabilitiesIds.stream()
                                 .map(String::valueOf)
                                 .collect(Collectors.joining(","));
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(TECHNOLOGY_MNGR_PATH_GET_BY_CAPABILITIES)
                .queryParam(Constants.QUERY_PARAM_CAPABILITIES_IDS, idsParam)
                .build())
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> buildErrorResponse(response, TechnicalMessage.TECHNOLOGIES_NOT_FOUND))
            .onStatus(HttpStatusCode::is5xxServerError, response -> buildErrorResponse(response, TechnicalMessage.ERROR_TECHNOLOGIES_ADAPTER))
            .bodyToMono(new ParameterizedTypeReference<List<CapacityTechnologies>>() {})
            .doOnNext(response -> log.info(LOG_API_RESPONSE, response))
            .doOnSuccess(list -> log.info("Completed getting capabilities in bootcamp"))
            .transformDeferred(RetryOperator.of(retry))
            .transformDeferred(mono ->
                Mono.defer(() ->
                    bulkhead.executeSupplier(() -> mono)
                )
            )
            .doOnError(error -> log.error(ERROR_LOG_CAPACITY_MNGR, error.getMessage()));
    }

    @Override
    public Mono<CustomPage<CapacityTechnologies>> getSortTechnologiesByCapabilities(String order, Integer size, Integer page) {
        log.info(LOG_START_GET_TECHNOLOGIES_PAGINATION, order, page, size);
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(TECHNOLOGY_MNGR_PATH_CAPABILITIES_SORT_BY_TECHNOLOGIES)
                .queryParam(QUERY_PARAM_ORDER_SORT, order)
                .queryParam(QUERY_PARAM_PAGE, page)
                .queryParam(QUERY_PARAM_SIZE, size)
                .build())
            .header(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> buildErrorResponse(response, TechnicalMessage.TECHNOLOGIES_NOT_FOUND))
            .onStatus(HttpStatusCode::is5xxServerError, response -> buildErrorResponse(response, TechnicalMessage.ERROR_TECHNOLOGIES_ADAPTER))
            .bodyToMono(new ParameterizedTypeReference<CustomPage<CapacityTechnologies>>() {})
            .doOnNext(response -> log.info(LOG_API_RESPONSE, response))
            .transformDeferred(RetryOperator.of(retry))
            .transformDeferred(mono ->
                Mono.defer(() ->
                    bulkhead.executeSupplier(() -> mono)
                )
            )
            .doOnError(error -> log.error(ERROR_LOG_CAPACITY_MNGR, error.getMessage()));
    }

    @Override
    public Mono<Void> deleteTechnologiesByCapabilitiesIds(List<Long> capabilitiesIds) {
         log.info(LOG_START_DELETE_TECHNOLOGIES_BY_CAPABILITIES_IDS, capabilitiesIds);
         String idsParam = capabilitiesIds.stream()
                                 .map(String::valueOf)
                                 .collect(Collectors.joining(","));
        return webClient.delete()
            .uri(uriBuilder -> uriBuilder
                .path(TECHNOLOGY_MNGR_PATH_DELETE_TECHNOLOGIES_BY_CAPABILITIES)
                .queryParam(Constants.QUERY_PARAM_CAPABILITIES_IDS, idsParam)
                .build())
            .header(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> buildErrorResponse(response, TechnicalMessage.TECHNOLOGIES_NOT_FOUND))
            .onStatus(HttpStatusCode::is5xxServerError, response -> buildErrorResponse(response, TechnicalMessage.ERROR_TECHNOLOGIES_ADAPTER))
            .bodyToMono(ResponseDTO.class)
            .doOnNext(response -> log.info(LOG_API_RESPONSE, response))
            .then()
            .doOnSuccess(response -> log.info("Completed assign technologies in capacity"))
            .transformDeferred(RetryOperator.of(retry))
            .transformDeferred(mono -> Mono.defer(() -> bulkhead.executeSupplier(() -> mono)))
            .doOnError(error -> log.error(ERROR_LOG_CAPACITY_MNGR, error.getMessage()));
    }

    public Mono<Throwable> fallback(Throwable t) {
        return Mono.defer(() ->
                Mono.error(new TechnicalException(TechnicalMessage.ERROR_TECHNOLOGIES_ADAPTER))
        );
    }
}
