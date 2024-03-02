package com.soundseeker.api.service;

import com.soundseeker.api.persistence.entity.ReservaEntity;
import com.soundseeker.api.service.dto.ReservaDto;

import java.util.List;

public interface IReservaService {
    ReservaDto registrar(ReservaEntity reservaEntity);

    ReservaDto obtenerPorId(Long id);

    List<ReservaDto> obtenerPorUsuario(String nombreUsuario);

    void eliminar(Long id);

    void calificar(Long id, Integer calificacion);
}
