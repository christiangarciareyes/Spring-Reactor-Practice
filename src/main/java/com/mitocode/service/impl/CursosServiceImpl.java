package com.mitocode.service.impl;

import com.mitocode.model.Cursos;
import com.mitocode.repo.ICursosRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.ICursosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CursosServiceImpl extends CRUDImpl<Cursos, String> implements ICursosService {

    private final ICursosRepo repo;

    @Override
    protected IGenericRepo<Cursos, String> getRepo() {
        return repo;
    }

}
