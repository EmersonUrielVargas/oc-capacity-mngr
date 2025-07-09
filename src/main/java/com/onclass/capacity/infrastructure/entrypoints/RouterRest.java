package com.onclass.capacity.infrastructure.entrypoints;

import com.onclass.capacity.domain.model.spi.CapabilitiesPerBootcamp;
import com.onclass.capacity.infrastructure.entrypoints.dto.request.AssignCapabilitiesDTO;
import com.onclass.capacity.infrastructure.entrypoints.dto.request.CreateCapacityDTO;
import com.onclass.capacity.infrastructure.entrypoints.handler.CapacityHandlerImpl;
import com.onclass.capacity.infrastructure.entrypoints.util.Constants;
import com.onclass.capacity.infrastructure.entrypoints.util.ErrorDTO;
import com.onclass.capacity.infrastructure.entrypoints.util.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
        @RouterOperation(
            path = Constants.PATH_POST_CAPABILITY,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = org.springframework.web.bind.annotation.RequestMethod.POST,
            beanClass = CapacityHandlerImpl.class,
            beanMethod = "createCapacity",
            operation = @Operation(
                operationId = "createCapacity",
                summary = "Create a new capacity",
                security = @SecurityRequirement(name = "BearerAuth"),
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateCapacityDTO.class))
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "201",
                        description = "Capacity created successfully",
                        content = @Content(schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                        responseCode = "409",
                        description = Constants.MESSAGE_VALIDATION_ERROR,
                        content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                    ),
                    @ApiResponse(
                        responseCode = "401",
                        description = Constants.MESSAGE_UNAUTHORIZED,
                        content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = Constants.PATH_POST_ASSIGN_CAPABILITIES,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = org.springframework.web.bind.annotation.RequestMethod.POST,
            beanClass = CapacityHandlerImpl.class,
            beanMethod = "assignCapabilities",
            operation = @Operation(
                operationId = "assignCapabilities",
                summary = "Assign capabilities to a bootcamp",
                security = @SecurityRequirement(name = "BearerAuth"),
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = AssignCapabilitiesDTO.class))
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Capabilities assigned successfully",
                        content = @Content(schema = @Schema(implementation = ResponseDTO.class))
                    ),
                    @ApiResponse(
                        responseCode = "409",
                        description = Constants.MESSAGE_VALIDATION_ERROR,
                        content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                    ),
                    @ApiResponse(
                        responseCode = "401",
                        description = Constants.MESSAGE_UNAUTHORIZED,
                        content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = Constants.PATH_GET_ALL_CAPABILITIES,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = org.springframework.web.bind.annotation.RequestMethod.GET,
            beanClass = CapacityHandlerImpl.class,
            beanMethod = "getAllCapabilities",
            operation = @Operation(
                operationId = "getAllCapabilities",
                summary = "Get all paginated and sorted capacities",
                security = @SecurityRequirement(name = "BearerAuth"),
                parameters = {
                    @Parameter(name = Constants.QUERY_PARAM_ORDER_SORT, description = "Order (ASC or DESC)", required = true, example = "ASC"),
                    @Parameter(name = Constants.QUERY_PARAM_ITEM_SORT, description = "Field to sort name or technologies", required = true, example = "name"),
                    @Parameter(name = Constants.QUERY_PARAM_PAGE, description = "Page", required = true, example = "0"),
                    @Parameter(name = Constants.QUERY_PARAM_SIZE, description = "Page size", required = true, example = "10")
                },
                responses = {
                    @ApiResponse(
                        responseCode = "201",
                        description = "Page of capacities found",
                        content = @Content(schema = @Schema(implementation = Constants.CustomPageCapacityList.class))
                    ),
                    @ApiResponse(
                        responseCode = "409",
                        description = Constants.MESSAGE_VALIDATION_ERROR,
                        content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                    ),
                    @ApiResponse(
                        responseCode = "401",
                        description = Constants.MESSAGE_UNAUTHORIZED,
                        content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = Constants.PATH_GET_CAPABILITIES_BY_BOOTCAMPS_IDS,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = org.springframework.web.bind.annotation.RequestMethod.GET,
            beanClass = CapacityHandlerImpl.class,
            beanMethod = "getCapabilitiesByBootcampsIds",
            operation = @Operation(
                operationId = "getCapabilitiesByBootcampsIds",
                summary = "Get capabilities by bootcamp IDs",
                security = @SecurityRequirement(name = "BearerAuth"),
                parameters = {
                    @Parameter(name = Constants.QUERY_PARAM_CAPABILITIES_IDS, description = "Bootcamp IDs separated by comma", required = true, example = "1,2,3")
                },
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "List of capabilities by bootcamp",
                        content = @Content(array = @ArraySchema(schema = @Schema(implementation = CapabilitiesPerBootcamp.class)))
                    ),
                    @ApiResponse(
                        responseCode = "409",
                        description = Constants.MESSAGE_VALIDATION_ERROR,
                        content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                    ),
                    @ApiResponse(
                        responseCode = "401",
                        description = Constants.MESSAGE_UNAUTHORIZED,
                        content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = Constants.PATH_GET_CAPABILITIES_SORT_BY_BOOTCAMPS,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = org.springframework.web.bind.annotation.RequestMethod.GET,
            beanClass = CapacityHandlerImpl.class,
            beanMethod = "getBootcampsSortByCapabilities",
            operation = @Operation(
                operationId = "getBootcampsSortByCapabilities",
                summary = "Get bootcamps sorted by number of assigned capabilities",
                security = @SecurityRequirement(name = "BearerAuth"),
                parameters = {
                    @Parameter(name = Constants.QUERY_PARAM_ORDER_SORT, description = "Order (ASC or DESC)", required = false, example = "ASC"),
                    @Parameter(name = Constants.QUERY_PARAM_PAGE, description = "Page", required = false, example = "0"),
                    @Parameter(name = Constants.QUERY_PARAM_SIZE, description = "Page size", required = false, example = "10")
                },
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Page of bootcamps found",
                        content = @Content(schema = @Schema(implementation = Constants.CustomPageCapabilitiesPerBootcamp.class))
                    ),
                    @ApiResponse(
                        responseCode = "409",
                        description = Constants.MESSAGE_VALIDATION_ERROR,
                        content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                    ),
                    @ApiResponse(
                        responseCode = "401",
                        description = Constants.MESSAGE_UNAUTHORIZED,
                        content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = Constants.PATH_DELETE_CAPABILITIES_BY_BOOTCAMP,
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = org.springframework.web.bind.annotation.RequestMethod.DELETE,
            beanClass = CapacityHandlerImpl.class,
            beanMethod = "deleteCapabilitiesByBootcamp",
            operation = @Operation(
                operationId = "deleteCapabilitiesByBootcamp",
                summary = "Delete capabilities assigned to a bootcamp",
                security = @SecurityRequirement(name = "BearerAuth"),
                parameters = {
                    @Parameter(name = Constants.QUERY_PARAM_ID, in = ParameterIn.PATH, description = "Bootcamp ID", required = true, example = "1")
                },
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Capabilities deleted successfully",
                        content = @Content(schema = @Schema(implementation = ResponseDTO.class))
                    ),
                    @ApiResponse(
                        responseCode = "409",
                        description = Constants.MESSAGE_VALIDATION_ERROR,
                        content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                    ),
                    @ApiResponse(
                        responseCode = "401",
                        description = Constants.MESSAGE_UNAUTHORIZED,
                        content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                    )
                }
            )
        )
    })
    public RouterFunction<ServerResponse> routerFunction(CapacityHandlerImpl capacityHandler) {
        return route(POST(Constants.PATH_POST_CAPABILITY), capacityHandler::createCapacity)
            .andRoute(POST(Constants.PATH_POST_ASSIGN_CAPABILITIES), capacityHandler::assignCapabilities)
            .andRoute(GET(Constants.PATH_GET_ALL_CAPABILITIES), capacityHandler::getAllCapabilities)
            .andRoute(GET(Constants.PATH_GET_CAPABILITIES_BY_BOOTCAMPS_IDS), capacityHandler::getCapabilitiesByBootcampsIds)
            .andRoute(GET(Constants.PATH_GET_CAPABILITIES_SORT_BY_BOOTCAMPS), capacityHandler::getBootcampsSortByCapabilities)
            .andRoute(DELETE(Constants.PATH_DELETE_CAPABILITIES_BY_BOOTCAMP), capacityHandler::deleteCapabilitiesByBootcamp);
    }
}
