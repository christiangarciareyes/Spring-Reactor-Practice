package com.mitocode.controller;

import com.mitocode.model.Dish;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/backpressure")
public class BackPressureController {

    //JSON.parse
    //[{},{},{},{},{},{},{},{}]
    @GetMapping(value = "/json", produces = "application/json")
    public Flux<Dish> json(){
        return Flux.interval(Duration.ofMillis(100))
                .map(t -> new Dish("1", "Soda", 5.90, true));
    }

    //STREAM+JSON
    //{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}
    @GetMapping(value = "/streamjson", produces = "application/stream+json")
    public Flux<Dish> streamjson(){
        return Flux.interval(Duration.ofMillis(100))
                .map(t -> new Dish("1", "Soda", 5.90, true));
    }

    @GetMapping(value = "/jsonFinito", produces = "application/json")
    public Flux<Dish> jsonFinito(){
        return Flux.range(0, 5000)
                .map(t -> new Dish("1", "Soda", 5.90, true));
    }

    @GetMapping(value = "/streamjsonFinito", produces = "application/stream+json")
    public Flux<Dish> streamjsonFinito(){
        return Flux.range(0, 5000)
                .map(t -> new Dish("1", "Soda", 5.90, true));
    }

    @GetMapping("/limitRate")
    public Flux<Integer> testLimitRate(){
        return Flux.range(1, 100) //32 default
                .log()
                .limitRate(10, 5) //75% request del highTide si no se especifica
                .delayElements(Duration.ofMillis(1));
    }
}
