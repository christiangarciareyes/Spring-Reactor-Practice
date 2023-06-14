package com.mitocode.service.impl;

import com.mitocode.model.Matricula;
import com.mitocode.repo.IMatriculaRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.IMatriculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatriculaServiceImpl extends CRUDImpl<Matricula, String> implements IMatriculaService {

    private final IMatriculaRepo matriculaRepo;

    @Override
    protected IGenericRepo<Matricula, String> getRepo() {
        return matriculaRepo;
    }

}
