package com.mitocode.handler;

import com.mitocode.model.Client;
import com.mitocode.service.IClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class ClientHandler {

    private final IClientService service;

    public Mono<ServerResponse> findAll(ServerRequest req){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Client.class);
    }

    public Mono<ServerResponse> findById(ServerRequest req){
        String id = req.pathVariable("id");

        return service.findById(id)
                .flatMap(client -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(client))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest req){
        Mono<Client> monoClient = req.bodyToMono(Client.class);

        return monoClient
                .flatMap(service::save)
                .flatMap(client -> ServerResponse
                        .created(URI.create(req.uri().toString().concat("/").concat(client.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(client))
                );
    }

    public Mono<ServerResponse> update(ServerRequest req) {
        String id = req.pathVariable("id");

        Mono<Client> monoClient = req.bodyToMono(Client.class);
        Mono<Client> monoDB = service.findById(id);

        return monoDB
                .zipWith(monoClient, (db, cl)-> {
                    db.setId(id);
                    db.setFirstName(cl.getFirstName());
                    db.setLastName(cl.getLastName());
                    db.setBirthDate(cl.getBirthDate());
                    db.setUrlPhoto(cl.getUrlPhoto());
                    return db;
                })
                .flatMap(service::update)
                .flatMap(client -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(client))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest req){
        String id = req.pathVariable("id");

        return service.findById(id)
                .flatMap(client -> service.delete(client.getId())
                        .then(ServerResponse.noContent().build())
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

}
