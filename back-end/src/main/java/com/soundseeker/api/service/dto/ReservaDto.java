package com.soundseeker.api.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.soundseeker.api.persistence.entity.ProductoEntity;
import com.soundseeker.api.persistence.entity.ReservaEntity;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * DTO for {@link com.soundseeker.api.persistence.entity.ReservaEntity}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReservaDto(
        Long id,
        LocalDateTime fechaOrden,
        LocalDate fechaRetiro,
        LocalDate fechaEntrega,
        @Size(message = "Las notas de la reserva no pueden tener más de 500 caracteres.", max = 500)
        String notas,
        Integer calificacion,
        Double total,
        ClienteDto cliente,
        Set<ProductoDto> productos) implements Serializable {

    public static ReservaDto mapearDesde(ReservaEntity reserva) {
        return new ReservaDto(
                reserva.getId(),
                reserva.getFechaOrden(),
                reserva.getFechaRetiro(),
                reserva.getFechaEntrega(),
                reserva.getNotas(),
                reserva.getCalificacion(),
                obtenerTotal(reserva.getProductos(), reserva.getFechaRetiro(), reserva.getFechaEntrega()),
                ClienteDto.mapearDesde(reserva.getUsuario()),
                ProductoDto.mapearSet(reserva.getProductos())
        );
    }

    private static Optional<Double> obtenerPrecioDiario(Set<ProductoEntity> productos) {
        return productos.stream()
                .filter(Objects::nonNull)
                .map(ProductoEntity::getPrecio)
                .filter(Objects::nonNull)
                .findFirst();
    }

    private static Long obtenerDias(LocalDate fechaRetiro, LocalDate fechaEntrega) {
        return fechaRetiro.datesUntil(fechaEntrega).count();
    }

    private static Double obtenerTotal(Set<ProductoEntity> productos, LocalDate fechaRetiro, LocalDate fechaEntrega) {
        return obtenerPrecioDiario(productos)
                .map(precioDiario -> precioDiario * obtenerDias(fechaRetiro, fechaEntrega))
                .map(total -> new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .orElse(null);
    }
}