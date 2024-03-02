package com.soundseeker.api.service;

import com.soundseeker.api.service.dto.ClienteDto;
import com.soundseeker.api.service.dto.ProductoDto;

import java.util.List;
import java.util.Set;

public interface IClienteService {
    List<ClienteDto> obtenerTodos();

    void otorgarRol(ClienteDto clienteDto);

    Set<ProductoDto> obtenerFavoritos(String nombreUsuario);
}
