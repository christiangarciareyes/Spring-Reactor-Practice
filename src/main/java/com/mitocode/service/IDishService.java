package com.mitocode.service;

import com.mitocode.model.Dish;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IDishService extends ICRUD<Dish, String>{

    /*Mono<Dish> save(Dish dish);
    Mono<Dish> update(Dish dish);
    Flux<Dish> findAll();
    Mono<Dish> findById(String id);
    Mono<Void> delete(String id);*/
}
