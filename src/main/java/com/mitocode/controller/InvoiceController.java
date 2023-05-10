package com.mitocode.controller;

import com.mitocode.model.Invoice;
import com.mitocode.pagination.PageSupport;
import com.mitocode.service.IInvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final IInvoiceService service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Invoice>>> findAll() {
        Flux<Invoice> fx = service.findAll(); //Flux<Invoice>

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx)
        ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Invoice>> findById(@PathVariable("id") String id) {
        return service.findById(id)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Invoice>> save(@Valid @RequestBody Invoice invoice, final ServerHttpRequest req) {
        return service.save(invoice)
                .map(e -> ResponseEntity
                        .created(URI.create(req.getURI().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Invoice>> update(@PathVariable("id") String id, @Valid @RequestBody Invoice invoice) {
        invoice.setId(id);

        Mono<Invoice> monoBody = Mono.just(invoice);
        Mono<Invoice> monoDB = service.findById(id);

        /*service.findById(id).hasElement()
                .map(status -> {
                    if(status){
                        service.update(invoice)
                    }
                })*/

        return monoDB.zipWith(monoBody, (db, inv) -> {
                    db.setId(id);
                    db.setClient(inv.getClient());
                    db.setDescription(inv.getDescription());
                    db.setItems(inv.getItems());
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
    public Mono<ResponseEntity<PageSupport<Invoice>>> getPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "0") int size
    ) {

        return service.getPage(PageRequest.of(page, size))
                .map(pag -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(pag)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private Invoice invoiceHateoas;

    /*@GetMapping("/hateoas/{id}")
    public Mono<EntityModel> getHateoas(@PathVariable("id") String id) {
        Mono<Link> link = linkTo(methodOn(InvoiceController.class).findById(id)).withSelfRel().toMono();

        //PRACTICA NO RECOMENDADA
        /*return service.findById(id) //Mono<Invoice>
                .flatMap(d -> {
                    invoiceHateoas = d;
                    return link;
                })
                .map(lk -> EntityModel.of(invoiceHateoas, lk));*/

        //PRACTICA INTERMEDIA
        /*return service.findById(id)
                .flatMap(d -> link.map(lk -> EntityModel.of(d, lk)));*/

        //PRACTICA IDEAL
       /* return service.findById(id)
                .zipWith(link, EntityModel::of); //(d, lk) -> EntityModel.of(d, lk)
    }*/

    @GetMapping("generateReport/{id}")
    public Mono<ResponseEntity<byte[]>> generateReport(@PathVariable("id") String id){
        Mono<byte[]> monoReport = service.generateReport(id); //Mono<byte[]>

        return monoReport
                .map(bytes -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(bytes)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
