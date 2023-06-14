package com.mitocode.controller;

import com.mitocode.model.Matricula;
import com.mitocode.service.IMatriculaService;
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
@RequestMapping("/matricula")
@RequiredArgsConstructor
public class MatriculaController {

    private final IMatriculaService service;
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public Mono<ResponseEntity<Flux<Matricula>>> findAll() {
        Flux<Matricula> fx = service.findAll();

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx)
        ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Matricula>> findById(@PathVariable("id") String id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Matricula>> save(@Valid @RequestBody Matricula matricula, final ServerHttpRequest req) {
        return service.save(matricula)
                .map(e -> ResponseEntity
                        .created(URI.create(req.getURI().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Matricula>> update(@PathVariable("id") String id, @Valid @RequestBody Matricula matricula) {
    	matricula.setId(id);

        Mono<Matricula> monoBody = Mono.just(matricula);
        Mono<Matricula> monoDB = service.findById(id);

        return monoDB.zipWith(monoBody, (db, inv) -> {
                    db.setId(id);
                    db.setFechaMatricula(inv.getFechaMatricula());
                    db.setEstudiante(inv.getEstudiante());
                    db.setItems(inv.getItems());
                    db.setEstado(inv.getEstado());
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
