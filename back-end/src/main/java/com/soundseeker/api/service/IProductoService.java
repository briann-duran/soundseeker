package com.soundseeker.api.service;

import com.soundseeker.api.persistence.entity.ProductoEntity;
import com.soundseeker.api.projection.ProductoInfo;
import com.soundseeker.api.service.dto.ProductoDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface IProductoService {
    ProductoDto registrar(ProductoEntity producto);

    ProductoDto obtenerPorId(Long id);

    List<ProductoDto> obtenerTodo();

    List<String> obtenerTodoPorNombre(String nombre);

    List<ProductoInfo> realizarBusquedaPorNombre(String nombre);

    List<ProductoDto> obtenerAleatorio();

    void eliminar(Long id);

    List<ProductoDto> obtenerPorCategorias(Set<Long> categorias);

    ProductoDto editar(ProductoEntity producto);

    List<ProductoDto> obtenerProductosDisponibles(String nombre, LocalDate fechaRetiro, LocalDate fechaEntrega);

    void agregarFavorito(Long productoId, String nombreUsuario);

    void cambiarDisponibilidadAFalse(Long productoId);

    void cambiarDisponibilidadATrue(Long productoId);

    List<ProductoDto> obtenerProductosDisponiblesTrue();
}
