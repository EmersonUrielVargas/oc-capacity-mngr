package com.onclass.capacity.infrastructure.entrypoints;

import com.onclass.capacity.infrastructure.entrypoints.handler.CapacityHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(CapacityHandlerImpl capacityHandler) {
        return route(POST("/capacity"), capacityHandler::createCapacity)
            .andRoute(GET("/capacity/all"), capacityHandler::getAllBootcamps);
    }
}
