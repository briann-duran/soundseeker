package com.soundseeker.api.web.controller;

import com.soundseeker.api.persistence.entity.CategoriaEntity;
import com.soundseeker.api.service.dto.CategoriaDto;
import com.soundseeker.api.service.impl.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {
    private final CategoriaService categoriaService;

    @Autowired
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<CategoriaDto> registrar(@Valid @RequestBody CategoriaEntity categoriaEntity) {
        return new ResponseEntity<>(this.categoriaService.registrar(categoriaEntity), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDto> obtenerPorId(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(this.categoriaService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaDto>> obtenerTodo() {
        return ResponseEntity.ok(this.categoriaService.obtenerTodo());
    }

    @GetMapping("/aleatorio")
    public ResponseEntity<List<CategoriaDto>> obtenerAleatorio() {
        return ResponseEntity.ok(this.categoriaService.obtenerAleatorio());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        this.categoriaService.eliminar(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<CategoriaDto> editar(@Valid @RequestBody CategoriaEntity categoria) {
        return ResponseEntity.ok(this.categoriaService.editar(categoria));
    }

    @PatchMapping("/disable/{id}")
    public ResponseEntity<Void> cambiarDisponibilidadAFalse(@PathVariable Long id) {
        this.categoriaService.cambiarDisponibilidadAFalse(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/enable/{id}")
    public ResponseEntity<Void> cambiarDisponibilidadATrue(@PathVariable Long id) {
        this.categoriaService.cambiarDisponibilidadATrue(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/disponiblesTrue")
    public ResponseEntity<List<CategoriaDto>> obtenerCategoriasDisponiblesTrue() {
        return ResponseEntity.ok(this.categoriaService.obtenerCategoriasDisponiblesTrue());
    }
}
