package com.soundseeker.api.service.impl;

import com.soundseeker.api.persistence.entity.RolEntity;
import com.soundseeker.api.persistence.entity.UsuarioEntity;
import com.soundseeker.api.persistence.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usuarioCorreo) throws UsernameNotFoundException {
        Optional<UsuarioEntity> correoEncontrado = this.usuarioRepository.findByCorreoElectronicoIgnoreCase(usuarioCorreo);
        Optional<UsuarioEntity> usuarioEncontrado = this.usuarioRepository.findByNombreUsuarioIgnoreCase(usuarioCorreo);

        if (correoEncontrado.isEmpty() && usuarioEncontrado.isEmpty())
            throw new UsernameNotFoundException("El usuario o correo electrónico '" + usuarioCorreo + "' no fue encontrado.");

        UsuarioEntity usuario = correoEncontrado.orElseGet(usuarioEncontrado::get);

        String[] roles = usuario.getRoles().stream().map(RolEntity::getRol).toArray(String[]::new);

        return User.builder()
                .username(usuario.getNombreUsuario())
                .password(usuario.getContrasena())
                .authorities(this.obtenerAutoridadesOtorgadas(roles))
                .accountLocked(usuario.getBloqueado())
                .disabled(usuario.getDeshabilitado())
                .build();
    }

    private List<GrantedAuthority> obtenerAutoridadesOtorgadas(String[] roles) {
        List<GrantedAuthority> autoridades = new ArrayList<>(roles.length);
        for (String rol : roles) {
            autoridades.add(new SimpleGrantedAuthority(rol));
            for (String permiso : this.obtenerPermisos(rol)) {
                autoridades.add(new SimpleGrantedAuthority(permiso));
            }
        }
        return autoridades;
    }

    private String[] obtenerPermisos(String rol) {
        return new String[]{};
    }
}
