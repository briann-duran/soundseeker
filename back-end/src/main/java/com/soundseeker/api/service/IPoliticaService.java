package com.soundseeker.api.service;

import com.soundseeker.api.persistence.entity.CategoriaEntity;
import com.soundseeker.api.persistence.entity.PoliticaEntity;
import com.soundseeker.api.service.dto.PoliticaDto;

import java.util.List;
import java.util.Set;

public interface IPoliticaService {
    List<PoliticaDto> obtenerTodasLasPoliticas();

    PoliticaDto obtenerPoliticaPorId(Long id);

    PoliticaDto registrarPolitica(PoliticaEntity politica);

    Set<CategoriaEntity> findCategoriasByPoliticaId(Long id);

    void eliminarPoliticaPorId(Long id);

    void eliminarPolitica();
}
