package com.soundseeker.api.service;

import com.soundseeker.api.persistence.entity.UsuarioEntity;
import com.soundseeker.api.service.dto.UsuarioDto;

public interface IUsuarioService {
    UsuarioEntity registrar(UsuarioDto usuarioDto);

    void crearTokenVerificacion(String token, UsuarioEntity usuario);

    void confirmarRegistro(String token);
}
