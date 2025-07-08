package com.onclass.capacity.infrastructure.entrypoints.handler;

import com.onclass.capacity.domain.api.CapacityServicePort;
import com.onclass.capacity.domain.enums.ItemSortList;
import com.onclass.capacity.domain.enums.OrderList;
import com.onclass.capacity.domain.enums.TechnicalMessage;
import com.onclass.capacity.domain.exceptions.BusinessException;
import com.onclass.capacity.domain.exceptions.TechnicalException;
import com.onclass.capacity.infrastructure.entrypoints.dto.request.AssignCapabilitiesDTO;
import com.onclass.capacity.infrastructure.entrypoints.dto.request.CreateCapacityDTO;
import com.onclass.capacity.infrastructure.entrypoints.mapper.CapacityMapper;
import com.onclass.capacity.infrastructure.entrypoints.util.Constants;
import com.onclass.capacity.infrastructure.entrypoints.util.ErrorDTO;
import com.onclass.capacity.infrastructure.entrypoints.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CapacityHandlerImpl {

    private final CapacityServicePort capacityServicePort;
    private final CapacityMapper capacityMapper;

    public Mono<ServerResponse> createCapacity(ServerRequest request) {
        return request.bodyToMono(CreateCapacityDTO.class)
                .flatMap(capacityDTO -> capacityServicePort.registerCapacity(capacityMapper.toCapacity(capacityDTO))
                        .doOnSuccess(savedCapacity -> log.info(Constants.CAPACITY_CREATED_RS_OK))
                )
                .flatMap(savedCapacity -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(TechnicalMessage.CAPACITY_CREATED.getMessage()))
                .doOnError(ex -> log.error(Constants.CAPACITY_ERROR, ex))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.CONFLICT,
                        ex.getTechnicalMessage()))
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ex.getTechnicalMessage()))
                .onErrorResume(ex -> {
                    log.error(Constants.UNEXPECTED_ERROR, ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            TechnicalMessage.INTERNAL_ERROR);
                });
    }

    public Mono<ServerResponse> getAllCapabilities(ServerRequest request) {
        String order = request.queryParam(Constants.QUERY_PARAM_ORDER_SORT).orElse(OrderList.ASCENDANT.getMessage());
        String itemToSort = request.queryParam(Constants.QUERY_PARAM_ITEM_SORT).orElse(ItemSortList.NAME.getMessage());
        Integer page = Integer.parseInt(request.queryParam(Constants.QUERY_PARAM_PAGE).orElse(Constants.DEFAULT_PAGE_PAGINATION));
        Integer size = Integer.parseInt(request.queryParam(Constants.QUERY_PARAM_SIZE).orElse(Constants.DEFAULT_SIZE_PAGINATION));

        return capacityServicePort.listCapabilities(OrderList.fromString(order.toUpperCase()),ItemSortList.fromString(itemToSort),page,size)
                .flatMap(pageCapabilities -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(pageCapabilities))
                .doOnError(ex -> log.error(Constants.CAPACITY_ERROR, ex))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.CONFLICT,
                        ex.getTechnicalMessage()))
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ex.getTechnicalMessage()))
                .onErrorResume(ex -> {
                    log.error(Constants.UNEXPECTED_ERROR, ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            TechnicalMessage.INTERNAL_ERROR);
                });
    }

    public Mono<ServerResponse> assignCapabilities(ServerRequest request) {
        return request.bodyToMono(AssignCapabilitiesDTO.class)
                .flatMap(assignCapabilitiesDTO ->
                    capacityServicePort.assignCapabilitiesToBootcamp(assignCapabilitiesDTO.getBootcampId(), assignCapabilitiesDTO.getCapabilitiesIds())
                        .doOnSuccess(successful -> log.info(Constants.ASSIGN_CAPABILITIES_CREATED_RS_OK))
                )
                .then(ServerResponse
                    .status(HttpStatus.OK)
                    .bodyValue(new ResponseDTO(TechnicalMessage.CAPABILITIES_ASSIGN_OK.getMessage())))
                .doOnError(ex -> log.error(Constants.CAPACITY_ERROR, ex))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.CONFLICT,
                        ex.getTechnicalMessage()))
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ex.getTechnicalMessage()))
                .onErrorResume(ex -> {
                    log.error(Constants.UNEXPECTED_ERROR, ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            TechnicalMessage.INTERNAL_ERROR);
                });
    }

    public Mono<ServerResponse> getCapabilitiesByBootcampsIds(ServerRequest request) {
        String capabilitiesParam = request.queryParam(Constants.QUERY_PARAM_CAPABILITIES_IDS).orElse("");
        List<Long> bootcampsIds = Arrays.stream(capabilitiesParam.split(","))
            .map(Long::parseLong)
            .toList();
        return capacityServicePort.getCapabilitiesByBootcampsIds(bootcampsIds)
            .collectList()
            .doOnSuccess( bootcampList -> log.info(Constants.GET_BOOTCAMPS_CAPABILITIES_RS_OK))
            .flatMap(list ->
                ServerResponse
                .status(HttpStatus.OK)
                .bodyValue(list))
            .doOnError(ex -> log.error(Constants.CAPACITY_ERROR, ex))
            .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                    HttpStatus.CONFLICT,
                    ex.getTechnicalMessage()))
            .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ex.getTechnicalMessage()))
            .onErrorResume(ex -> {
                log.error(Constants.UNEXPECTED_ERROR, ex);
                return buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        TechnicalMessage.INTERNAL_ERROR);
            });
    }

    public Mono<ServerResponse> getBootcampsSortByCapabilities(ServerRequest request) {
        String order = request.queryParam(Constants.QUERY_PARAM_ORDER_SORT).orElse(OrderList.ASCENDANT.getMessage());
        int page = Integer.parseInt(request.queryParam(Constants.QUERY_PARAM_PAGE).orElse(Constants.DEFAULT_PAGE_PAGINATION));
        int size = Integer.parseInt(request.queryParam(Constants.QUERY_PARAM_SIZE).orElse(Constants.DEFAULT_SIZE_PAGINATION));

        return capacityServicePort.getSortCapabilitiesByBootcamps(OrderList.fromString(order.toUpperCase()), page, size)
            .doOnSuccess( bootcampCustomPage -> log.info(Constants.GET_BOOTCAMPS_CAPABILITIES_RS_OK))
            .flatMap(bootcampCustomPage ->
                ServerResponse
                .status(HttpStatus.OK)
                .bodyValue(bootcampCustomPage))
            .doOnError(ex -> log.error(Constants.CAPACITY_ERROR, ex))
            .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                    HttpStatus.CONFLICT,
                    ex.getTechnicalMessage()))
            .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ex.getTechnicalMessage()))
            .onErrorResume(ex -> {
                log.error(Constants.UNEXPECTED_ERROR, ex);
                return buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        TechnicalMessage.INTERNAL_ERROR);
            });
    }

    public Mono<ServerResponse> deleteCapabilitiesByBootcamp(ServerRequest request) {
        String idParam = request.pathVariable(Constants.QUERY_PARAM_ID);
        return capacityServicePort.deleteCapabilitiesByBootcampId( Long.parseLong(idParam))
                .flatMap(empty -> ServerResponse
                        .status(HttpStatus.OK)
                        .bodyValue(new ResponseDTO(TechnicalMessage.DELETE_CAPABILITIES_OK.getMessage())))
                .doOnError(ex -> log.error(Constants.CAPACITY_ERROR, ex))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.CONFLICT,
                        ex.getTechnicalMessage()))
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ex.getTechnicalMessage()))
                .onErrorResume(ex -> {
                    log.error(Constants.UNEXPECTED_ERROR, ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            TechnicalMessage.INTERNAL_ERROR);
                });
    }

    private Mono<ServerResponse> buildErrorResponse(HttpStatus httpStatus, TechnicalMessage error) {
        return Mono.defer(() -> {
            ErrorDTO errorResponse = ErrorDTO.builder()
                    .code(error.getCode())
                    .message(error.getMessage())
                    .build();
            return ServerResponse.status(httpStatus)
                    .bodyValue(errorResponse);
        });
    }
}
