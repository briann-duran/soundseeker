package com.soundseeker.api.web.controller;

import com.soundseeker.api.persistence.entity.RolEntity;
import com.soundseeker.api.persistence.entity.UsuarioEntity;
import com.soundseeker.api.persistence.repository.UsuarioRepository;
import com.soundseeker.api.service.dto.ClienteDto;
import com.soundseeker.api.service.dto.InicioSesionDto;
import com.soundseeker.api.service.dto.ProductoDto;
import com.soundseeker.api.service.exception.MalaSolicitudException;
import com.soundseeker.api.web.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/autenticacion")
public class AutenticacionController {
    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AutenticacionController(UsuarioRepository usuarioRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<ClienteDto> iniciarSesion(@RequestBody InicioSesionDto usuarioContrasena) {
        try {
            UsernamePasswordAuthenticationToken inicioSesion = new UsernamePasswordAuthenticationToken(
                    usuarioContrasena.nombreUsuario(), usuarioContrasena.contrasena());

            this.authenticationManager.authenticate(inicioSesion);

            String jwt = this.jwtUtil.crear(usuarioContrasena.nombreUsuario());
            UsuarioEntity usuarioEncontrado = this.usuarioRepository.findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase(
                    usuarioContrasena.nombreUsuario(), usuarioContrasena.nombreUsuario());

            String[] roles = usuarioEncontrado.getRoles().stream().map(RolEntity::getRol).toArray(String[]::new);
            Set<ProductoDto> favoritos = usuarioEncontrado.getProductos().stream().map(ProductoDto::mapearDesde).collect(Collectors.toSet());

            ClienteDto clienteDto = new ClienteDto(
                    usuarioEncontrado.getNombreUsuario(),
                    usuarioEncontrado.getNombre(),
                    usuarioEncontrado.getApellido(),
                    usuarioEncontrado.getCorreoElectronico(),
                    roles,
                    favoritos
            );

            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, jwt).body(clienteDto);
        } catch (AccountStatusException e) {
            throw new MalaSolicitudException("Tu cuenta está pendiente por activar, revisa tu correo, confirma tu cuenta e intenta de nuevo.");
        } catch (BadCredentialsException e) {
            throw new MalaSolicitudException("Tu nombre de usuario, correo electrónico o contraseña son incorrectos, intenta de nuevo.");
        } catch (AuthenticationException e) {
            throw new MalaSolicitudException("Hubo un problema con la autenticación, intenta de nuevo.");
        }
    }
}
