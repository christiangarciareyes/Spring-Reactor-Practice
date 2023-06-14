package com.mitocode.repo;

import com.mitocode.model.Estudiantes;

import reactor.core.publisher.Flux;

public interface IEstudiantesRepo extends IGenericRepo<Estudiantes, String> {

	Flux<Estudiantes> findAllByOrderByEdadAsc();
	Flux<Estudiantes> findAllByOrderByEdadDesc();

}
