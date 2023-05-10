package com.mitocode.controller;

import com.mitocode.model.Dish;
import com.mitocode.pagination.PageSupport;
import com.mitocode.service.IDishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
public class DishController {

    private final IDishService service;

    //@PreAuthorize("hasAuthority('ADMIN')")
    //@PreAuthorize("@testAuthorize.hasAccess()")
    @GetMapping
    public Mono<ResponseEntity<Flux<Dish>>> findAll() {
        Flux<Dish> fx = service.findAll(); //Flux<Dish>

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx)
        ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /*@GetMapping
    public Mono<ResponseEntity<Flux<EntityModel<Dish>>>> findAll(){
        Flux<EntityModel<Dish>> fx = service.findAll()
                .flatMap(dish ->
                        linkTo(methodOn(DishController.class).findById(dish.getId()))
                                .withSelfRel()
                                .toMono()
                                .map(link -> EntityModel.of(dish, link))
                );

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }*/

    /*@GetMapping
    public Mono<ResponseEntity<Flux<EntityModel<Dish>>>> findAll() {
        Flux<EntityModel<Dish>> fx = service.findAll()
                .flatMap(dish ->
                        Mono.zip(
                                        linkTo(methodOn(DishController.class).findById(dish.getId()))
                                                .withSelfRel()
                                                .toMono(),
                                        linkTo(methodOn(DishController.class).findById(dish.getId()))
                                                .withSelfRel()
                                                .toMono(),
                                        linkTo(methodOn(DishController.class).findById(dish.getId()))
                                                .withSelfRel()
                                                .toMono()
                                )
                                .map(links -> EntityModel.of(dish, links.getT1(), links.getT2(), links.getT3()))
                );

        return Mono.just(ResponseEntity.ok()
                        //.contentType(MediaType.APPLICATION_JSON)
                        .body(fx))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }*/

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Dish>> findById(@PathVariable("id") String id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Dish>> save(@Valid @RequestBody Dish dish, final ServerHttpRequest req) {
        return service.save(dish)
                .map(e -> ResponseEntity
                        .created(URI.create(req.getURI().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Dish>> update(@PathVariable("id") String id, @Valid @RequestBody Dish dish) {
        dish.setId(id);

        Mono<Dish> monoBody = Mono.just(dish);
        Mono<Dish> monoDB = service.findById(id);

        /*service.findById(id).hasElement()
                .map(status -> {
                    if(status){
                        service.update(dish)
                    }
                })*/

        return monoDB.zipWith(monoBody, (db, b) -> {
                    db.setId(id);
                    db.setName(b.getName());
                    db.setPrice(b.getPrice());
                    db.setStatus(b.getStatus());
                    return db;
                })
                .flatMap(service::update) //operaciones de DB 99% flatMap
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());


    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id) {
        return service.findById(id)
                .flatMap(e -> service.delete(e.getId())
                        //.then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                        .thenReturn(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/pageable")
    public Mono<ResponseEntity<PageSupport<Dish>>> getPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "0") int size
    ) {

        return service.getPage(PageRequest.of(page, size))
                .map(pag -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(pag)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private Dish dishHateoas;

    @GetMapping("/hateoas/{id}")
    public Mono<EntityModel> getHateoas(@PathVariable("id") String id) {
        Mono<Link> link = linkTo(methodOn(DishController.class).findById(id)).withSelfRel().toMono();

        //PRACTICA NO RECOMENDADA
        /*return service.findById(id) //Mono<Dish>
                .flatMap(d -> {
                    dishHateoas = d;
                    return link;
                })
                .map(lk -> EntityModel.of(dishHateoas, lk));*/

        //PRACTICA INTERMEDIA
        /*return service.findById(id)
                .flatMap(d -> link.map(lk -> EntityModel.of(d, lk)));*/

        //PRACTICA IDEAL
        return service.findById(id)
                .zipWith(link, EntityModel::of); //(d, lk) -> EntityModel.of(d, lk)
    }
}
