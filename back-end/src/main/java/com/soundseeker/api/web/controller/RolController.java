package com.soundseeker.api.web.controller;

import com.soundseeker.api.service.IRolService;
import com.soundseeker.api.service.dto.RolDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RolController {
    private final IRolService rolService;

    @Autowired
    public RolController(IRolService rolService) {
        this.rolService = rolService;
    }

    @PostMapping
    public ResponseEntity<RolDto> registrar(@Valid @RequestBody RolDto rol) {
        return new ResponseEntity<>(this.rolService.registrar(rol), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(this.rolService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<RolDto>> obtenerTodo() {
        return ResponseEntity.ok(this.rolService.obtenerTodo());
    }

    @PutMapping
    public ResponseEntity<RolDto> editar(@Valid @RequestBody RolDto rolDto) {
        return ResponseEntity.ok(this.rolService.editar(rolDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        this.rolService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}