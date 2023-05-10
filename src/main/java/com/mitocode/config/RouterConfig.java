package com.mitocode.config;

import com.mitocode.handler.ClientHandler;
import com.mitocode.handler.DishHandler;
import com.mitocode.handler.InvoiceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.scheduler.Schedulers;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;

@Configuration
public class RouterConfig {

    //Functional Endpoints
    @Bean
    public RouterFunction<ServerResponse> routesDish(DishHandler handler){
        return route(GET("/v2/dishes"), handler::findAll) //req -> handler.findAll(req)
                .andRoute(GET("/v2/dishes/{id}"), handler::findById)
                .andRoute(POST("/v2/dishes"), handler::create)
                .andRoute(PUT("/v2/dishes/{id}"), handler::update)
                .andRoute(DELETE("/v2/dishes/{id}"), handler::delete);
    }

    @Bean
    public RouterFunction<ServerResponse> routesClient(ClientHandler handler){
        return route(GET("/v2/clients"), handler::findAll) //req -> handler.findAll(req)
                .andRoute(GET("/v2/clients/{id}"), handler::findById)
                .andRoute(POST("/v2/clients"), handler::create)
                .andRoute(PUT("/v2/clients/{id}"), handler::update)
                .andRoute(DELETE("/v2/clients/{id}"), handler::delete);
    }

    @Bean
    public RouterFunction<ServerResponse> routesInvoice(InvoiceHandler handler){
        return route(GET("/v2/invoices"), handler::findAll) //req -> handler.findAll(req)
                .andRoute(GET("/v2/invoices/{id}"), handler::findById)
                .andRoute(POST("/v2/invoices"), handler::create)
                .andRoute(PUT("/v2/invoices/{id}"), handler::update)
                .andRoute(DELETE("/v2/invoices/{id}"), handler::delete);
    }
}
