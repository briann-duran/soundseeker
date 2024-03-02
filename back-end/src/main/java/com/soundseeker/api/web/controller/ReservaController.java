package com.soundseeker.api.web.controller;

import com.soundseeker.api.persistence.entity.ReservaEntity;
import com.soundseeker.api.service.dto.ReservaDto;
import com.soundseeker.api.service.events.OnReservaCompletaEvent;
import com.soundseeker.api.service.impl.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ReservaController(ReservaService reservaService, ApplicationEventPublisher eventPublisher) {
        this.reservaService = reservaService;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(this.reservaService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<ReservaDto>> findAllByUsername(@RequestParam String nombreUsuario) {
        List<ReservaDto> reservaDtos = this.reservaService.obtenerPorUsuario(nombreUsuario);

        return ResponseEntity.ok(reservaDtos);
    }

    @PostMapping
    public ResponseEntity<ReservaDto> registrar(@Valid @RequestBody ReservaEntity reservaEntity) {
        ReservaDto registrar = this.reservaService.registrar(reservaEntity);
        try {
            this.eventPublisher.publishEvent(new OnReservaCompletaEvent(registrar));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(registrar, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        this.reservaService.eliminar(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping
    public ResponseEntity<Void> calificar(@RequestParam Long id, @RequestParam Integer calificacion) {
        this.reservaService.calificar(id, calificacion);
        return ResponseEntity.ok().build();
    }
}
