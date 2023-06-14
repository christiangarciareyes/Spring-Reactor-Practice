package com.mitocode.service;

import com.mitocode.model.Estudiantes;

import reactor.core.publisher.Flux;

public interface IEstudiantesService extends ICRUD<Estudiantes, String>{

	Flux<Estudiantes> findAllByOrderByEdadAsc();
	Flux<Estudiantes> findAllByOrderByEdadDesc();

}
