package com.soundseeker.api.persistence.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class UsuarioSecurity implements UserDetails {
    private final UsuarioEntity usuarioEntity;

    public UsuarioSecurity(UsuarioEntity usuarioEntity) {
        this.usuarioEntity = usuarioEntity;
    }

    @Override
    public String getUsername() {
        return usuarioEntity.getNombreUsuario();
    }

    @Override
    public String getPassword() {
        return usuarioEntity.getContrasena();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        String[] roles = usuarioEntity.getRoles().stream().map(RolEntity::getRol).toArray(String[]::new);
        for (String rol : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(rol));
        }
        return grantedAuthorities;
    }

    @Override
    public boolean isEnabled() {
        return !usuarioEntity.getDeshabilitado();
    }

    @Override
    public boolean isAccountNonLocked() {
        return usuarioEntity.getBloqueado();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}