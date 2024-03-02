package com.soundseeker.api.service.impl;

import com.soundseeker.api.persistence.entity.TokenVerificacionEntity;
import com.soundseeker.api.persistence.entity.UsuarioEntity;
import com.soundseeker.api.persistence.repository.RolRepository;
import com.soundseeker.api.persistence.repository.TokenVerificacionRepository;
import com.soundseeker.api.persistence.repository.UsuarioRepository;
import com.soundseeker.api.service.IUsuarioService;
import com.soundseeker.api.service.dto.UsuarioDto;
import com.soundseeker.api.service.exception.ContrasenaNoCoincideException;
import com.soundseeker.api.service.exception.NombreDuplicadoException;
import com.soundseeker.api.service.exception.RecursoNoEncontradoException;
import com.soundseeker.api.service.exception.TokenExpiradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class UsuarioService implements IUsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final TokenVerificacionRepository tokenVerificacionRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository, TokenVerificacionRepository tokenVerificacionRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.tokenVerificacionRepository = tokenVerificacionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioEntity registrar(UsuarioDto usuarioDto) {
        if (this.estaRegistradoNombreUsuario(usuarioDto.getNombreUsuario().trim()))
            throw new NombreDuplicadoException("El nombre de usuario ya se encuentra registrado.");
        if (this.estaRegistradoCorreoElectronico(usuarioDto.getCorreoElectronico().trim()))
            throw new NombreDuplicadoException("El correo electrónico ya se encuentra registrado.");
        if (!usuarioDto.getContrasena().equals(usuarioDto.getContrasenaConfirmada()))
            throw new ContrasenaNoCoincideException("La contraseñas ingresadas no coinciden.");

        return this.usuarioRepository.save(new UsuarioEntity(
                usuarioDto.getNombreUsuario().trim(),
                passwordEncoder.encode(usuarioDto.getContrasena()),
                usuarioDto.getNombre().trim(),
                usuarioDto.getApellido().trim(),
                usuarioDto.getCorreoElectronico().trim(),
                true, false,
                Collections.singletonList(this.rolRepository.findByRolIgnoreCase("ROLE_CLIENTE"))
        ));
    }

    @Override
    public void crearTokenVerificacion(String token, UsuarioEntity usuario) {
        this.tokenVerificacionRepository.save(new TokenVerificacionEntity(token, usuario));
    }

    @Override
    public void confirmarRegistro(String token) {
        TokenVerificacionEntity tokenVerificacion = this.obtenerTokenVerificacion(token);
        if (tokenVerificacion == null)
            throw new RecursoNoEncontradoException("Token no encontrado");
        if (LocalDateTime.now().isAfter(tokenVerificacion.getFechaExpiracion()))
            throw new TokenExpiradoException("Token expirado");

        UsuarioEntity usuario = tokenVerificacion.getUsuario();
        usuario.setDeshabilitado(false);
        this.usuarioRepository.save(usuario);
    }

    private boolean estaRegistradoNombreUsuario(String nombreUsuario) {
        return this.usuarioRepository.findByNombreUsuarioIgnoreCase(nombreUsuario).isPresent();
    }

    private boolean estaRegistradoCorreoElectronico(String correoElectronico) {
        return this.usuarioRepository.findByCorreoElectronicoIgnoreCase(correoElectronico).isPresent();
    }

    private TokenVerificacionEntity obtenerTokenVerificacion(String token) {
        return this.tokenVerificacionRepository.findByToken(token);
    }
}