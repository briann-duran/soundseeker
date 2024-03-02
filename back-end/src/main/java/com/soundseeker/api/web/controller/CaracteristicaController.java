package com.soundseeker.api.web.controller;

import com.soundseeker.api.persistence.entity.CaracteristicaEntity;
import com.soundseeker.api.service.ICaracteristicaService;
import com.soundseeker.api.service.dto.CaracteristicaDto;
import com.soundseeker.api.service.impl.CaracteristicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/caracteristicas")
public class CaracteristicaController {
    private final ICaracteristicaService caracteristicaService;

    @Autowired
    public CaracteristicaController(CaracteristicaService caracteristicaService) {
        this.caracteristicaService = caracteristicaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaracteristicaEntity> obtenerCaracteristicaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(caracteristicaService.obtenerCaracteristicaPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<CaracteristicaDto>> obtenerTodasLasCaracteristicas() {
        return ResponseEntity.ok(caracteristicaService.obtenerTodasLasCaracteristicas());
    }

    @PostMapping
    public ResponseEntity<CaracteristicaDto> crearCaracteristica(@RequestBody CaracteristicaDto caracteristicaDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(caracteristicaService.crearCaracteristica(caracteristicaDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CaracteristicaDto> editarCaracteristica(@PathVariable Long id, @RequestBody CaracteristicaDto caracteristicaDto) {
        return ResponseEntity.ok(caracteristicaService.editarCaracteristica(id, caracteristicaDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCaracteristica(@PathVariable Long id) {
        caracteristicaService.eliminarCaracteristicaPorId(id);
        return ResponseEntity.ok().build();
    }
}

