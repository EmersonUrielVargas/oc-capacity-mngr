package com.onclass.capacity.infrastructure.adapters.technologiesadapter;

import com.onclass.capacity.domain.enums.TechnicalMessage;
import com.onclass.capacity.domain.exceptions.BusinessException;
import com.onclass.capacity.domain.exceptions.TechnicalException;
import com.onclass.capacity.domain.model.CapacityTechnologies;
import com.onclass.capacity.domain.spi.TechnologiesGateway;
import com.onclass.capacity.domain.utilities.CustomPage;
import com.onclass.capacity.infrastructure.adapters.technologiesadapter.dto.TechnologyMngrProperties;
import com.onclass.capacity.infrastructure.adapters.technologiesadapter.dto.request.TechnologyAssign;
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
import java.util.concurrent.TimeoutException;

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
    public Mono<Void> assignTechnologiesToCapacity(Long capacityId, List<Long> technologiesIds) {
        log.info(LOG_START_ASSIGN_TECHNOLOGIES, technologiesIds, capacityId);
        TechnologyAssign requestBody = new TechnologyAssign(capacityId, technologiesIds);
        return webClient.post()
            .uri(uriBuilder -> uriBuilder
                    .path(TECHNOLOGY_MNGR_PATH_ASSIGN)
                    .build())
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .bodyValue(requestBody)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> buildErrorResponse(response, TechnicalMessage.TECHNOLOGIES_NOT_FOUND))
            .onStatus(HttpStatusCode::is5xxServerError, response -> buildErrorResponse(response, TechnicalMessage.ERROR_ASSIGN_TECHNOLOGIES))
            .bodyToMono(Void.class)
            .doOnNext(response -> log.info("Received API response : {}", response))
            .transformDeferred(RetryOperator.of(retry))
            .transformDeferred(mono -> Mono.defer(() -> bulkhead.executeSupplier(() -> mono)))
            .doOnTerminate(() -> log.info("Completed assign technologies in capacity"))
            .doOnError(error -> log.error("Error occurred in capacity mngr: {}", error.getMessage()));
    }

    @Override
    public Mono<List<CapacityTechnologies>> getTechnologiesByCapabilitiesIds(List<Long> capabilitiesIds) {
        log.info(LOG_START_GET_TECHNOLOGIES_BY_CAPABILITIES_IDS, capabilitiesIds);
        return webClient.post()
            .uri(uriBuilder -> uriBuilder
                .path(TECHNOLOGY_MNGR_PATH_GET_BY_CAPABILITIES)
                .build())
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .bodyValue(capabilitiesIds)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> buildErrorResponse(response, TechnicalMessage.TECHNOLOGIES_NOT_FOUND))
            .onStatus(HttpStatusCode::is5xxServerError, response -> buildErrorResponse(response, TechnicalMessage.ERROR_ASSIGN_TECHNOLOGIES))
            .bodyToMono(new ParameterizedTypeReference<List<CapacityTechnologies>>() {})
            .doOnNext(response -> log.info("Received API response : {}", response))
            .transformDeferred(RetryOperator.of(retry))
            .transformDeferred(mono ->
                Mono.defer(() ->
                    bulkhead.executeSupplier(() -> mono)
                )
            )
            .doOnTerminate(() -> log.info("Completed getting capabilities in bootcamp"))
            .doOnError(error -> log.error("Error occurred in capacity mngr: {}", error.getMessage()));
    }

    @Override
    public Mono<CustomPage<CapacityTechnologies>> getSortTechnologiesByCapabilities(String order, Integer size, Integer page) {
        log.info(LOG_START_GET_TECHNOLOGIES_PAGINATION, order, page, size);
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .queryParam(QUERY_PARAM_ORDER_SORT, order)
                .queryParam(QUERY_PARAM_PAGE, page)
                .queryParam(QUERY_PARAM_SIZE, size)
                .build())
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> buildErrorResponse(response, TechnicalMessage.TECHNOLOGIES_NOT_FOUND))
            .onStatus(HttpStatusCode::is5xxServerError, response -> buildErrorResponse(response, TechnicalMessage.ERROR_ASSIGN_TECHNOLOGIES))
            .bodyToMono(new ParameterizedTypeReference<CustomPage<CapacityTechnologies>>() {})
            .doOnNext(response -> log.info("Received API response : {}", response))
            .transformDeferred(RetryOperator.of(retry))
            .transformDeferred(mono ->
                Mono.defer(() ->
                    bulkhead.executeSupplier(() -> mono)
                )
            )
            .doOnTerminate(() -> log.info("Completed getting capabilities in bootcamp"))
            .doOnError(error -> log.error("Error occurred in capacity mngr: {}", error.getMessage()));
    }

    public Mono<Throwable> fallback(Throwable t) {
        return Mono.defer(() ->
                Mono.justOrEmpty(t instanceof TimeoutException
                                ? new TechnicalException(TechnicalMessage.INTERNAL_ERROR)
                                : t)
        );
    }
}
