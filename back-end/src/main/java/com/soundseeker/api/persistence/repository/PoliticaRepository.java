package com.soundseeker.api.persistence.repository;

import com.soundseeker.api.persistence.entity.CategoriaEntity;
import com.soundseeker.api.persistence.entity.PoliticaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PoliticaRepository extends JpaRepository<PoliticaEntity, Long> {

    Optional<PoliticaEntity> findByTituloIgnoreCase(@Param("titulo") String titulo);

    @Query(value = "SELECT p FROM PoliticaEntity p WHERE p.categorias IS EMPTY")
    List<PoliticaEntity> findPoliticasSinCategorias();

    @Query("SELECT p.categorias FROM PoliticaEntity p WHERE p.id = :politicaId")
    Set<CategoriaEntity> findCategoriasByPoliticaId(@Param("politicaId") Long politicaId);
}
