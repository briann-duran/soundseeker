package com.soundseeker.api.persistence.repository;

import com.soundseeker.api.persistence.entity.UsuarioEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends ListCrudRepository<UsuarioEntity, String> {
    Optional<UsuarioEntity> findByNombreUsuarioIgnoreCase(String nombreUsuario);

    Optional<UsuarioEntity> findByCorreoElectronicoIgnoreCase(String correoElectronico);

    UsuarioEntity findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase(String nombreUsuario, String correoElectronico);
}
