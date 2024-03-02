package com.soundseeker.api.service;

import com.soundseeker.api.service.dto.RolDto;

import java.util.List;

public interface IRolService {
    RolDto registrar(RolDto rolDto);

    RolDto obtenerPorId(Long id);

    List<RolDto> obtenerTodo();

    RolDto editar(RolDto rolDto);

    void eliminar(Long id);
}