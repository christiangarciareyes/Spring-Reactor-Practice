package com.mitocode.service.impl;

import com.mitocode.model.Estudiantes;
import com.mitocode.repo.IEstudiantesRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.IEstudiantesService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EstudiantesServiceImpl extends CRUDImpl<Estudiantes, String> implements IEstudiantesService {

    private final IEstudiantesRepo repo;

    @Override
    protected IGenericRepo<Estudiantes, String> getRepo() {
        return repo;
    }

	@Override
	public Flux<Estudiantes> findAllByOrderByEdadAsc() {
		return repo.findAllByOrderByEdadAsc();
	}
     
	@Override
	public Flux<Estudiantes> findAllByOrderByEdadDesc() {
		return repo.findAllByOrderByEdadDesc();
	}
	
}
