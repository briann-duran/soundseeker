package com.soundseeker.api.persistence.repository;

import com.soundseeker.api.persistence.entity.ReservaEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends ListCrudRepository<ReservaEntity, Long> {
    List<ReservaEntity> findAllByUsuarioNombreUsuarioOrderByIdDesc(String nombreUsuario);

    @Transactional
    @Modifying
    @Query("update ReservaEntity r set r.calificacion = :calificacion where r.id = :id")
    void updateCalificacionById(@NonNull Long id, @NonNull Integer calificacion);

    @Query("""
                SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
                FROM ReservaEntity r
                JOIN r.productos p
                WHERE p.id = :productoId
                    AND (
                        (r.fechaRetiro >= :fechaRetiro AND r.fechaRetiro <= :fechaEntrega) OR
                        (r.fechaEntrega >= :fechaRetiro AND r.fechaEntrega <= :fechaEntrega)
                    )
            """)
    boolean isProductoReservadoEnFechas(@Param("productoId") Long productoId,
                                        @Param("fechaRetiro") LocalDate fechaRetiro,
                                        @Param("fechaEntrega") LocalDate fechaEntrega);
}