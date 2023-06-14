package com.mitocode.controller;

import com.mitocode.model.Estudiantes;
import com.mitocode.service.IEstudiantesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/estudiantes")
@RequiredArgsConstructor
public class EstudiantesController {

    private final IEstudiantesService service;
    
    @GetMapping("/asc")
    public Mono<ResponseEntity<Flux<Estudiantes>>> findAllByOrderByEdadAsc() {
        Flux<Estudiantes> fx = service.findAllByOrderByEdadAsc(); 

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx)
        ).defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/desc")
    public Mono<ResponseEntity<Flux<Estudiantes>>> findAllByOrderByEdadDesc() {
        Flux<Estudiantes> fx = service.findAllByOrderByEdadDesc(); 

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx)
        ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Estudiantes>>> findAll() {
        Flux<Estudiantes> fx = service.findAll(); 

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx)
        ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Estudiantes>> findBy(@PathVariable("id") String id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Estudiantes>> save(@RequestBody Estudiantes estudiante, final ServerHttpRequest req) {
        return service.save(estudiante)
                .map(e -> ResponseEntity
                        .created(URI.create(req.getURI().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Estudiantes>> update(@PathVariable("id") String id, @RequestBody Estudiantes estudiante) {
    	estudiante.setId(id);

        Mono<Estudiantes> monoBody = Mono.just(estudiante);
        Mono<Estudiantes> monoDB = service.findById(id);

        return monoDB.zipWith(monoBody, (db, c) -> {
                    db.setId(id);
                    db.setNombres(c.getNombres());
                    db.setApellidos(c.getApellidos());
                    db.setDni(c.getDni());
                    db.setEdad(c.getEdad());
                    return db;
                })
                .flatMap(service::update)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());

    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id) {
        return service.findById(id)
                .flatMap(e -> service.delete(e.getId())
                        .thenReturn(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
