package com.soundseeker.api.service.impl;

import com.soundseeker.api.persistence.entity.RolEntity;
import com.soundseeker.api.persistence.entity.UsuarioEntity;
import com.soundseeker.api.persistence.repository.RolRepository;
import com.soundseeker.api.persistence.repository.UsuarioRepository;
import com.soundseeker.api.service.IClienteService;
import com.soundseeker.api.service.dto.ClienteDto;
import com.soundseeker.api.service.dto.ProductoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClienteService implements IClienteService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @Autowired
    public ClienteService(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    @Override
    public List<ClienteDto> obtenerTodos() {
        return this.usuarioRepository.findAll().stream().filter(usuario -> !usuario.getNombreUsuario().equals("admin"))
                .map(usuario -> new ClienteDto(usuario.getNombreUsuario(), usuario.getNombre(), usuario.getApellido(),
                        usuario.getCorreoElectronico(), this.obtenerRoles(usuario.getRoles())))
                .collect(Collectors.toList());
    }

    @Override
    public void otorgarRol(ClienteDto clienteDto) {
        RolEntity rolAdmin = this.rolRepository.findByRolIgnoreCase("ROLE_ADMIN");
        RolEntity rolCliente = this.rolRepository.findByRolIgnoreCase("ROLE_CLIENTE");
        UsuarioEntity usuarioEncontrado = this.usuarioRepository.findByNombreUsuarioIgnoreCase(clienteDto.nombreUsuario()).orElseThrow();

        usuarioEncontrado.getRoles().clear();
        usuarioEncontrado.getRoles().add(Arrays.stream(clienteDto.roles()).toList().contains("ROLE_ADMIN") ? rolCliente : rolAdmin);

        this.usuarioRepository.save(usuarioEncontrado);
    }

    @Override
    public Set<ProductoDto> obtenerFavoritos(String nombreUsuario) {
        UsuarioEntity usuarioEncontrado = this.usuarioRepository.findByNombreUsuarioIgnoreCase(nombreUsuario).orElseThrow();
        return usuarioEncontrado.getProductos().stream().map(ProductoDto::mapearDesde).collect(Collectors.toSet());
    }

    private String[] obtenerRoles(Collection<RolEntity> roles) {
        return roles.stream().map(RolEntity::getRol).toArray(String[]::new);
    }
}
