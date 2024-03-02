package com.soundseeker.api.web.controller;

import com.soundseeker.api.service.IClienteService;
import com.soundseeker.api.service.dto.ClienteDto;
import com.soundseeker.api.service.dto.ProductoDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {
    private final IClienteService clienteService;

    public ClienteController(IClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<List<ClienteDto>> obtenerTodos() {
        return ResponseEntity.ok(this.clienteService.obtenerTodos());
    }

    @PostMapping
    public ResponseEntity<Void> otorgarRol(@Valid @RequestBody ClienteDto clienteDto) {
        this.clienteService.otorgarRol(clienteDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{nombreUsuario}")
    public ResponseEntity<Set<ProductoDto>> obtenerFavoritos(@PathVariable String nombreUsuario) {
        return ResponseEntity.ok(this.clienteService.obtenerFavoritos(nombreUsuario));
    }
}
