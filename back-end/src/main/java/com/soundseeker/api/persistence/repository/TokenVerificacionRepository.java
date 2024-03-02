package com.soundseeker.api.persistence.repository;

import com.soundseeker.api.persistence.entity.TokenVerificacionEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenVerificacionRepository extends ListCrudRepository<TokenVerificacionEntity, Long> {
    TokenVerificacionEntity findByToken(String token);

    Optional<TokenVerificacionEntity> findByUsuarioNombreUsuarioIgnoreCase(String nombreUsuario);
}