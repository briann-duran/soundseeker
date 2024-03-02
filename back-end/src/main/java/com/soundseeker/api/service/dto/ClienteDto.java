package com.soundseeker.api.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.soundseeker.api.persistence.entity.UsuarioEntity;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClienteDto(
        String nombreUsuario,
        String nombre,
        String apellido,
        String correoElectronico,
        String[] roles,
        Set<ProductoDto> favorito
) {
    public ClienteDto(String nombreUsuario, String nombre, String apellido, String correoElectronico, String[] roles) {
        this(nombreUsuario, nombre, apellido, correoElectronico, roles, null);
    }

    public ClienteDto(String nombreUsuario, String nombre, String apellido, String correoElectronico) {
        this(nombreUsuario, nombre, apellido, correoElectronico, null, null);
    }

    public static ClienteDto mapearDesde(UsuarioEntity usuario) {
        return new ClienteDto(
                usuario.getNombreUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCorreoElectronico()
        );
    }
}