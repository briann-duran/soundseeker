package com.soundseeker.api.service.dto;

import java.io.Serializable;

public record InicioSesionDto(String nombreUsuario, String contrasena) implements Serializable {
}