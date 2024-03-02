package com.soundseeker.api.persistence.repository;

import com.soundseeker.api.persistence.entity.CategoriaEntity;
import com.soundseeker.api.persistence.entity.PoliticaEntity;
import com.soundseeker.api.persistence.entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoriaRepository extends JpaRepository<CategoriaEntity, Long> {
    Optional<CategoriaEntity> findByNombreIgnoreCase(@Param("nombre") String nombre);

    List<CategoriaEntity> findAllByDisponibleTrue();

    @Query(value = "SELECT * FROM categoria ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<CategoriaEntity> obtenerAleatorio(@Param("limit") int limit);

    @Query("SELECT p FROM ProductoEntity p WHERE p.categoria.id = :categoriaId")
    Set<ProductoEntity> findProductosById(Long categoriaId);

    @Query("SELECT DISTINCT p FROM CategoriaEntity c JOIN c.politicas p WHERE c.id = :categoriaId")
    Set<PoliticaEntity> findPoliticasById(@Param("categoriaId") Long categoriaId);
}
