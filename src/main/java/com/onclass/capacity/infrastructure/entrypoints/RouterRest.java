package com.onclass.capacity.infrastructure.entrypoints;

import com.onclass.capacity.infrastructure.entrypoints.handler.CapacityHandlerImpl;
import com.onclass.capacity.infrastructure.entrypoints.util.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(CapacityHandlerImpl capacityHandler) {
        return route(POST(Constants.PATH_POST_CAPABILITY), capacityHandler::createCapacity)
            .andRoute(POST(Constants.PATH_POST_ASSIGN_CAPABILITIES), capacityHandler::assignCapabilities)
            .andRoute(GET(Constants.PATH_GET_ALL_CAPABILITIES), capacityHandler::getAllCapabilities)
            .andRoute(GET(Constants.PATH_GET_CAPABILITIES_BY_BOOTCAMPS_IDS), capacityHandler::getCapabilitiesByBootcampsIds)
            .andRoute(GET(Constants.PATH_GET_CAPABILITIES_SORT_BY_BOOTCAMPS), capacityHandler::getBootcampsSortByCapabilities)
            .andRoute(DELETE(Constants.PATH_DELETE_CAPABILITIES_BY_BOOTCAMP), capacityHandler::deleteCapabilitiesByBootcamp);
    }
}
