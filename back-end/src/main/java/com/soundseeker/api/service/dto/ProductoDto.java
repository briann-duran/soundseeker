package com.soundseeker.api.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.soundseeker.api.persistence.entity.ProductoEntity;
import com.soundseeker.api.persistence.entity.ReservaEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
public class ProductoDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private String marca;
    private Double precio;
    private Set<String> imagenes;
    private Boolean disponible;
    private Integer calificacion;
    private CategoriaDto categoria;
    private Set<CaracteristicaDto> caracteristicas;
    private List<LocalDate> fechasReservadas;

    public ProductoDto(Long id, String nombre, String descripcion, String marca, Double precio, Set<String> imagenes) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.marca = marca;
        this.precio = precio;
        this.imagenes = imagenes;
    }

    public static ProductoDto mapearDesde(ProductoEntity producto) {
        return new ProductoDto(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getMarca(),
                producto.getPrecio(),
                producto.getImagenes(),
                producto.getDisponible(),
                obtenerCalificacionTotal(producto.getReservas()),
                CategoriaDto.mapearDesde(producto.getCategoria()),
                CaracteristicaDto.mapearSet(producto.getCaracteristicas()),
                obtenerFechasReservadas(producto.getReservas())
        );
    }

    private static ProductoDto mapearReserva(ProductoEntity producto) {
        return new ProductoDto(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getMarca(),
                producto.getPrecio(),
                producto.getImagenes()
        );
    }

    public static Set<ProductoDto> mapearSet(Set<ProductoEntity> productos) {
        return productos.stream().map(ProductoDto::mapearReserva).collect(Collectors.toSet());
    }

    private static double obtenerPromedioDeCalificaciones(Set<ReservaEntity> reservas) {
        return reservas.stream()
                .filter(Objects::nonNull)
                .map(ReservaEntity::getCalificacion)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    private static int obtenerCalificacionTotal(Set<ReservaEntity> reservas) {
        double promedio = obtenerPromedioDeCalificaciones(reservas);
        return (int) Math.ceil(promedio > 0 ? promedio : 5);
    }

    private static List<LocalDate> obtenerFechasReservadas(Set<ReservaEntity> reservas) {
        return reservas.stream()
                .filter(reserva -> reserva.getFechaEntrega().isAfter(LocalDate.now()))
                .flatMap(reserva -> {
                    LocalDate inicio = (reserva.getFechaRetiro().isAfter(LocalDate.now())) ? reserva.getFechaRetiro() : LocalDate.now();
                    return inicio.datesUntil(reserva.getFechaEntrega().plusDays(1));
                })
                .collect(Collectors.toList());
    }
}
