package com.soundseeker.api.service.impl;

import com.soundseeker.api.persistence.entity.CaracteristicaEntity;
import com.soundseeker.api.persistence.repository.CaracteristicaRepository;
import com.soundseeker.api.service.ICaracteristicaService;
import com.soundseeker.api.service.dto.CaracteristicaDto;
import com.soundseeker.api.service.exception.RecursoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CaracteristicaService implements ICaracteristicaService {
    private final CaracteristicaRepository caracteristicaRepository;

    @Autowired
    public CaracteristicaService(CaracteristicaRepository caracteristicaRepository) {
        this.caracteristicaRepository = caracteristicaRepository;
    }

    @Override
    public List<CaracteristicaDto> obtenerTodasLasCaracteristicas() {
        List<CaracteristicaEntity> caracteristicas = caracteristicaRepository.findAll();
        return caracteristicas.stream()
                .map(this::mapearAUnDto)
                .collect(Collectors.toList());
    }

    @Override
    public CaracteristicaDto crearCaracteristica(CaracteristicaDto caracteristicaDto) {
        CaracteristicaEntity nuevaCaracteristica = mapearAUnaEntidad(caracteristicaDto);
        nuevaCaracteristica = caracteristicaRepository.save(nuevaCaracteristica);
        return mapearAUnDto(nuevaCaracteristica);
    }

    @Override
    public CaracteristicaDto editarCaracteristica(Long id, CaracteristicaDto caracteristicaDto) {
        CaracteristicaEntity caracteristicaExistente = obtenerCaracteristicaPorId(id);
        caracteristicaExistente.setNombre(caracteristicaDto.getNombre());
        caracteristicaExistente.setIcono(caracteristicaDto.getIcono());
        caracteristicaExistente = caracteristicaRepository.save(caracteristicaExistente);
        return mapearAUnDto(caracteristicaExistente);
    }

    @Override
    public void eliminarCaracteristicaPorId(Long id) {
        obtenerCaracteristicaPorId(id);
        caracteristicaRepository.deleteById(id);
    }

    @Override
    public void eliminarCaracteristica() {
        List<CaracteristicaEntity> caracteristicas = caracteristicaRepository.findCaracteristicasSinProductos();
        caracteristicaRepository.deleteAll(caracteristicas);
    }


    @Override
    public CaracteristicaEntity obtenerCaracteristicaPorId(Long id) {
        return caracteristicaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Característica no encontrada con ID: " + id));
    }

    private CaracteristicaDto mapearAUnDto(CaracteristicaEntity entidad) {
        CaracteristicaDto dto = new CaracteristicaDto();
        dto.setId(entidad.getId());
        dto.setNombre(entidad.getNombre());
        dto.setIcono(entidad.getIcono());
        return dto;
    }

    private CaracteristicaEntity mapearAUnaEntidad(CaracteristicaDto dto) {
        CaracteristicaEntity entidad = new CaracteristicaEntity();
        entidad.setNombre(dto.getNombre());
        entidad.setIcono(dto.getIcono());
        return entidad;
    }
}

