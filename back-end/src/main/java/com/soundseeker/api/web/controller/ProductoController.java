package com.soundseeker.api.web.controller;

import com.soundseeker.api.persistence.entity.ProductoEntity;
import com.soundseeker.api.projection.ProductoInfo;
import com.soundseeker.api.service.dto.ProductoDto;
import com.soundseeker.api.service.impl.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/instrumentos")
public class ProductoController {
    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public ResponseEntity<ProductoDto> registrar(@Valid @RequestBody ProductoEntity productoEntity) {
        return new ResponseEntity<>(this.productoService.registrar(productoEntity), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> obtenerPorId(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(this.productoService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductoDto>> obtenerTodo() {
        return ResponseEntity.ok(this.productoService.obtenerTodo());
    }

    @GetMapping("/nombre")
    public ResponseEntity<List<String>> obtenerTodoPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(this.productoService.obtenerTodoPorNombre(nombre));
    }

    @GetMapping("/busqueda")
    public ResponseEntity<List<ProductoInfo>> realizarBusquedaPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(this.productoService.realizarBusquedaPorNombre(nombre));
    }

    @GetMapping("/aleatorio")
    public ResponseEntity<List<ProductoDto>> obtenerAleatorio() {
        return ResponseEntity.ok(this.productoService.obtenerAleatorio());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        this.productoService.eliminar(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDto>> obtenerPorCategorias(@RequestBody Set<Long> categorias) {
        return ResponseEntity.ok(this.productoService.obtenerPorCategorias(categorias));
    }

    @PutMapping
    public ResponseEntity<ProductoDto> editarProducto(@RequestBody ProductoEntity producto) {
        return ResponseEntity.ok(this.productoService.editar(producto));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<ProductoDto>> obtenerProductosDisponibles(
            @RequestParam String nombre,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaRetiro,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrega) {
        return ResponseEntity.ok(this.productoService.obtenerProductosDisponibles(nombre, fechaRetiro, fechaEntrega));
    }

    @GetMapping("/favoritos")
    public ResponseEntity<Void> agregarFavorito(@RequestParam Long productoId, @RequestParam String nombreUsuario) {
        this.productoService.agregarFavorito(productoId, nombreUsuario);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/disable/{id}")
    public ResponseEntity<Void> cambiarDisponibilidadAFalse(@PathVariable Long id) {
        productoService.cambiarDisponibilidadAFalse(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/enable/{id}")
    public ResponseEntity<Void> cambiarDisponibilidadATrue(@PathVariable Long id) {
        productoService.cambiarDisponibilidadATrue(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/disponiblesTrue")
    public ResponseEntity<List<ProductoDto>> obtenerProductosDisponiblesTrue() {
        return ResponseEntity.ok(this.productoService.obtenerProductosDisponiblesTrue());
    }
}
