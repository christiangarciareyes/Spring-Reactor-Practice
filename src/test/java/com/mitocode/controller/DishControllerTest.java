package com.mitocode.controller;

import com.mitocode.model.Dish;
import com.mitocode.repo.IDishRepo;
import com.mitocode.service.IDishService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = DishController.class)
public class DishControllerTest {

    @Autowired
    private WebTestClient client;

    @MockBean
    private IDishService service;

    @MockBean
    private Resources resources;

    private Dish dish1;
    private Dish dish2;
    private List<Dish> list;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);

        dish1 = new Dish();
        dish1.setId("1");
        dish1.setName("Soda");
        dish1.setPrice(29.9);
        dish1.setStatus(true);

        dish2 = new Dish();
        dish2.setId("2");
        dish2.setName("Pizza");
        dish2.setPrice(49.9);
        dish2.setStatus(true);

        list = new ArrayList<>();
        list.add(dish1);
        list.add(dish2);

        Mockito.when(service.findAll()).thenReturn(Flux.fromIterable(list));
        Mockito.when(service.save(any())).thenReturn(Mono.just(dish1));
        Mockito.when(service.findById(any())).thenReturn(Mono.just(dish1));
        Mockito.when(service.update(any())).thenReturn(Mono.just(dish1));
        Mockito.when(service.delete(any())).thenReturn(Mono.empty());
    }

    @Test
    public void findAllTest(){
        //Mockito.when(service.findAll()).thenReturn(Flux.fromIterable(list));

        client.get()
                .uri("/dishes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Dish.class)
                .hasSize(2);
    }

    @Test
    public void createTest(){
        //Mockito.when(service.save(any())).thenReturn(Mono.just(dish1));

        client.post()
                .uri("/dishes")
                .body(Mono.just(dish1), Dish.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isNotEmpty()
                .jsonPath("$.price").isNumber()
                .jsonPath("$.status").isBoolean();
    }

    @Test
    public void updateTest(){
        //Mockito.when(service.findById(any())).thenReturn(Mono.just(dish1));
        //Mockito.when(service.update(any())).thenReturn(Mono.just(dish1));

        client.put()
                .uri("/dishes/" + dish1.getId())
                .body(Mono.just(dish1), Dish.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isNotEmpty()
                .jsonPath("$.price").isNumber()
                .jsonPath("$.status").isBoolean();
    }

    @Test
    public void deleteTest(){
        //Mockito.when(service.findById(any())).thenReturn(Mono.just(dish1));
        //Mockito.when(service.delete(any())).thenReturn(Mono.empty());

        client.delete()
                .uri("/dishes/" + dish1.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void deleteErrorTest(){
        Mockito.when(service.findById("99")).thenReturn(Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)));
        //Mockito.when(service.delete(any())).thenReturn(Mono.empty());

        client.delete()
                .uri("/dishes/" + 99)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
