package com.soundseeker.api.service;

import com.soundseeker.api.persistence.entity.CaracteristicaEntity;
import com.soundseeker.api.service.dto.CaracteristicaDto;

import java.util.List;

public interface ICaracteristicaService {
    List<CaracteristicaDto> obtenerTodasLasCaracteristicas();

    CaracteristicaDto crearCaracteristica(CaracteristicaDto caracteristicaDto);

    CaracteristicaDto editarCaracteristica(Long id, CaracteristicaDto caracteristicaDto);

    void eliminarCaracteristicaPorId(Long id);

    void eliminarCaracteristica();

    CaracteristicaEntity obtenerCaracteristicaPorId(Long id);
}

