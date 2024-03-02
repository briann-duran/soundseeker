package com.soundseeker.api.web.controller;

import com.soundseeker.api.persistence.entity.UsuarioEntity;
import com.soundseeker.api.service.IUsuarioService;
import com.soundseeker.api.service.dto.UsuarioDto;
import com.soundseeker.api.service.events.OnRegistroCompletoEvent;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {
    private final IUsuarioService usuarioService;
    private final ApplicationEventPublisher eventPublisher;
    private final Environment environment;

    @Autowired
    public UsuarioController(IUsuarioService usuarioService, ApplicationEventPublisher eventPublisher, Environment environment) {
        this.usuarioService = usuarioService;
        this.eventPublisher = eventPublisher;
        this.environment = environment;
    }

    @PostMapping
    public ResponseEntity<Void> registrar(@Valid @RequestBody UsuarioDto usuario) {
        UsuarioEntity usuarioRegistrado = this.usuarioService.registrar(usuario);
        try {
            this.eventPublisher.publishEvent(new OnRegistroCompletoEvent(usuarioRegistrado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/confirmacion")
    public ResponseEntity<Void> confirmarRegistro(@RequestParam("token") String token) {
        String aws = Objects.requireNonNull(this.environment.getProperty("aws"));
        try {
            this.usuarioService.confirmarRegistro(token);
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(aws)).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
