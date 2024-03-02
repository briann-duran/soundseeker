package com.soundseeker.api.persistence.repository;

import com.soundseeker.api.persistence.entity.ProductoEntity;
import com.soundseeker.api.projection.ProductoInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
    Optional<ProductoEntity> findByNombreIgnoreCase(@Param("nombre") String nombre);

    List<ProductoEntity> findAllByNombreContainingIgnoreCase(String nombre);

    List<ProductoEntity> findAllByDisponibleTrue();

    List<ProductoInfo> searchAllByNombreContainingIgnoreCase(String nombre);

    @Query(value = "SELECT * FROM producto ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<ProductoEntity> obtenerAleatorio(@Param("limit") int limit);

    @Query(value = "SELECT p FROM ProductoEntity p WHERE p.categoria.id IN (:categorias)")
    List<ProductoEntity> obtenerPorCategorias(@Param("categorias") Set<Long> categorias);

    @Query("""
                SELECT p
                FROM ProductoEntity p
                WHERE NOT EXISTS (
                    SELECT r
                    FROM ReservaEntity r
                    JOIN r.productos prod
                    WHERE prod.id = p.id
                        AND r.fechaRetiro <= :fechaEntrega
                        AND r.fechaEntrega >= :fechaRetiro
                        )
            """)
    Set<ProductoEntity> findByRangoDeFechasDisponibles(@Param("fechaRetiro") LocalDate fechaRetiro,
                                                       @Param("fechaEntrega") LocalDate fechaEntrega);

    @Modifying
    @Query("UPDATE ProductoEntity p SET p.disponible = false WHERE p.id = :productoId")
    void cambiarDisponibilidadAFalse(@Param("productoId") Long productoId);

    @Modifying
    @Query("UPDATE ProductoEntity p SET p.disponible = true WHERE p.id = :productoId")
    void cambiarDisponibilidadATrue(@Param("productoId") Long productoId);
}
