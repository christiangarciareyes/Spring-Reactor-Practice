package com.mitocode.controller;

import com.mitocode.model.Cursos;
import com.mitocode.service.ICursosService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/cursos")
@RequiredArgsConstructor
public class CursosController {

    private final ICursosService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Cursos>>> findAll() {
        Flux<Cursos> fx = service.findAll(); 

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx)
        ).defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Cursos>> findById(@PathVariable("id") String id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Cursos>> save(@Valid @RequestBody Cursos curso, final ServerHttpRequest req) {
        return service.save(curso)
                .map(e -> ResponseEntity
                        .created(URI.create(req.getURI().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Cursos>> update(@PathVariable("id") String id, @Valid @RequestBody Cursos curso) {
        curso.setId(id);

        Mono<Cursos> monoBody = Mono.just(curso);
        Mono<Cursos> monoDB = service.findById(id);

        return monoDB.zipWith(monoBody, (db, b) -> {
                    db.setId(id);
                    db.setNombre(b.getNombre());
                    db.setSiglas(b.getSiglas());
                    db.setEstado(b.getEstado());
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
