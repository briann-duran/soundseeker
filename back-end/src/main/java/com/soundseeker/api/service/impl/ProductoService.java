package com.soundseeker.api.service.impl;

import com.soundseeker.api.persistence.entity.ProductoEntity;
import com.soundseeker.api.persistence.entity.UsuarioEntity;
import com.soundseeker.api.persistence.repository.ProductoRepository;
import com.soundseeker.api.persistence.repository.UsuarioRepository;
import com.soundseeker.api.projection.ProductoInfo;
import com.soundseeker.api.service.ICaracteristicaService;
import com.soundseeker.api.service.IProductoService;
import com.soundseeker.api.service.dto.ProductoDto;
import com.soundseeker.api.service.exception.NombreDuplicadoException;
import com.soundseeker.api.service.exception.RecursoNoEncontradoException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductoService implements IProductoService {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ICaracteristicaService caracteristicaService;

    @Autowired
    public ProductoService(ProductoRepository productoRepository, UsuarioRepository usuarioRepository, ICaracteristicaService caracteristicaService) {
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.caracteristicaService = caracteristicaService;
    }

    @Override
    public ProductoDto registrar(ProductoEntity producto) {
        Optional<ProductoEntity> productoExistente = this.productoRepository.findByNombreIgnoreCase(producto.getNombre());
        if (productoExistente.isPresent()) {
            throw new NombreDuplicadoException("El nombre del producto ya está registrado.");
        }

        ProductoEntity nuevoProducto = this.productoRepository.save(producto);
        return ProductoDto.mapearDesde(nuevoProducto);
    }

    @Override
    public ProductoDto obtenerPorId(Long id) {
        ProductoEntity producto = this.productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recurso no encontrado"));
        return ProductoDto.mapearDesde(producto);
    }

    @Override
    public List<ProductoDto> obtenerTodo() {
        return this.productoRepository.findAll()
                .stream()
                .map(ProductoDto::mapearDesde)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> obtenerTodoPorNombre(String nombre) {
        return this.productoRepository.findAllByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(ProductoEntity::getNombre)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoInfo> realizarBusquedaPorNombre(String nombre) {
        return this.productoRepository.searchAllByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public List<ProductoDto> obtenerAleatorio() {
        return this.productoRepository.obtenerAleatorio(10)
                .stream()
                .map(ProductoDto::mapearDesde)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminar(Long id) {
        if (!this.productoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Recurso no encontrado");
        }

        this.productoRepository.deleteById(id);
        this.caracteristicaService.eliminarCaracteristica();
    }

    @Override
    public List<ProductoDto> obtenerPorCategorias(Set<Long> categorias) {
        return this.productoRepository.obtenerPorCategorias(categorias)
                .stream()
                .map(ProductoDto::mapearDesde)
                .collect(Collectors.toList());
    }

    @Override
    public ProductoDto editar(ProductoEntity producto) {
        ProductoEntity existingProducto = productoRepository.findById(producto.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        existingProducto.setNombre(producto.getNombre());
        existingProducto.setDescripcion(producto.getDescripcion());
        existingProducto.setMarca(producto.getMarca());
        existingProducto.setPrecio(producto.getPrecio());
        existingProducto.setImagenes(producto.getImagenes());
        existingProducto.setDisponible(producto.getDisponible());
        existingProducto.setCategoria(producto.getCategoria());

        ProductoEntity updatedProducto = productoRepository.save(existingProducto);
        return ProductoDto.mapearDesde(updatedProducto);
    }

    @Override
    public List<ProductoDto> obtenerProductosDisponibles(String nombre, LocalDate fechaRetiro, LocalDate fechaEntrega) {
        Set<ProductoEntity> productos = new HashSet<>(this.productoRepository.findAllByNombreContainingIgnoreCase(nombre));
        Set<ProductoEntity> productosNoDisponibles = new HashSet<>(this.productoRepository.findByRangoDeFechasDisponibles(fechaRetiro, fechaEntrega));

        productos.retainAll(productosNoDisponibles);
        return productos.stream()
                .map(ProductoDto::mapearDesde)
                .collect(Collectors.toList());
    }

    @Override
    public void agregarFavorito(Long productoId, String nombreUsuario) {
        ProductoEntity producto = this.productoRepository.findById(productoId).orElseThrow();
        UsuarioEntity usuario = this.usuarioRepository.findByNombreUsuarioIgnoreCase(nombreUsuario).orElseThrow();
        if (producto.getUsuarios().contains(usuario)) {
            producto.getUsuarios().remove(usuario);
        } else {
            producto.getUsuarios().add(usuario);
        }
        this.productoRepository.save(producto);
    }

    @Transactional
    @Override
    public void cambiarDisponibilidadAFalse(Long productoId) {
        Optional<ProductoEntity> productoOptional = productoRepository.findById(productoId);
        if (productoOptional.isPresent()) {
            ProductoEntity producto = productoOptional.get();
            producto.setDisponible(false);
            productoRepository.save(producto);
        } else {
            throw new RecursoNoEncontradoException("Producto no encontrado");
        }
    }

    @Transactional
    @Override
    public void cambiarDisponibilidadATrue(Long productoId) {
        Optional<ProductoEntity> productoOptional = productoRepository.findById(productoId);
        if (productoOptional.isPresent()) {
            ProductoEntity producto = productoOptional.get();
            producto.setDisponible(true);
            productoRepository.save(producto);
        } else {
            throw new RecursoNoEncontradoException("Producto no encontrado");
        }
    }

    public List<ProductoDto> obtenerProductosDisponiblesTrue() {
        return this.productoRepository.findAllByDisponibleTrue()
                .stream()
                .map(ProductoDto::mapearDesde)
                .collect(Collectors.toList());
    }
}
