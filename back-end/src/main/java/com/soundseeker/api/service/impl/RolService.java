package com.soundseeker.api.service.impl;

import com.soundseeker.api.persistence.entity.RolEntity;
import com.soundseeker.api.persistence.repository.RolRepository;
import com.soundseeker.api.service.IRolService;
import com.soundseeker.api.service.dto.RolDto;
import com.soundseeker.api.service.exception.MalaSolicitudException;
import com.soundseeker.api.service.exception.NombreDuplicadoException;
import com.soundseeker.api.service.exception.RecursoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolService implements IRolService {
    private final RolRepository rolRepository;

    @Autowired
    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public RolDto registrar(RolDto rolDto) {
        if (this.rolRepository.findByRolIgnoreCase(asignarPrefijo(rolDto.rol())) != null)
            throw new NombreDuplicadoException("El rol ya está registrado.");

        RolEntity rolParaGuardar = new RolEntity();
        rolParaGuardar.setRol(asignarPrefijo(rolDto.rol()));

        RolEntity rolGuardado = this.rolRepository.save(rolParaGuardar);
        return new RolDto(rolGuardado.getId(), rolGuardado.getRol());
    }

    @Override
    public RolDto obtenerPorId(Long id) {
        RolEntity rol = this.rolRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recurso no encontrado"));
        return new RolDto(rol.getId(), rol.getRol());
    }

    @Override
    public List<RolDto> obtenerTodo() {
        List<RolEntity> roles = this.rolRepository.findAll();
        return roles.stream()
                .map(rol -> new RolDto(rol.getId(), rol.getRol()))
                .collect(Collectors.toList());
    }

    @Override
    public RolDto editar(RolDto rolDto) {
        if (rolDto.id() == null)
            throw new MalaSolicitudException("El ID del rol no puede ser nulo.");
        RolEntity rol = this.rolRepository.findById(rolDto.id())
                .orElseThrow(() -> new RecursoNoEncontradoException("Recurso no encontrado"));
        if (this.rolRepository.findByRolIgnoreCase(asignarPrefijo(rolDto.rol())) != null) {
            throw new NombreDuplicadoException("El rol ya está registrado.");
        }
        rol.setRol(asignarPrefijo(rolDto.rol()));
        RolEntity rolActualizado = this.rolRepository.save(rol);
        return new RolDto(rolActualizado.getId(), rolActualizado.getRol());
    }

    @Override
    public void eliminar(Long id) {
        if (!this.rolRepository.existsById(id))
            throw new RecursoNoEncontradoException("Recurso no encontrado");
        this.rolRepository.deleteById(id);
    }

    private String asignarPrefijo(String rol) {
        return "ROLE_" + String.join("_", Arrays.asList(rol.trim().split(" "))).trim().toUpperCase();
    }
}