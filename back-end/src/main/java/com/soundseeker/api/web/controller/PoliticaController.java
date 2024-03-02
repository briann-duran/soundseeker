package com.soundseeker.api.web.controller;

import com.soundseeker.api.persistence.entity.PoliticaEntity;
import com.soundseeker.api.service.IPoliticaService;
import com.soundseeker.api.service.dto.PoliticaDto;
import com.soundseeker.api.service.impl.PoliticaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/politicas")
public class PoliticaController {
    private final IPoliticaService politicaService;

    @Autowired
    public PoliticaController(PoliticaService politicaService) {
        this.politicaService = politicaService;
    }

    @GetMapping
    public ResponseEntity<List<PoliticaDto>> obtenerTodasLasPoliticas() {
        return ResponseEntity.ok(this.politicaService.obtenerTodasLasPoliticas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PoliticaDto> obtenerPorPoliticaId(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(this.politicaService.obtenerPoliticaPorId(id));
    }

    @PostMapping
    public ResponseEntity<PoliticaDto> registrarPolitica(@Valid @RequestBody PoliticaEntity politicaEntity) {
        return new ResponseEntity<>(this.politicaService.registrarPolitica(politicaEntity), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPolitica(@PathVariable Long id) {
        this.politicaService.eliminarPoliticaPorId(id);
        return ResponseEntity.ok().build();
    }
}
