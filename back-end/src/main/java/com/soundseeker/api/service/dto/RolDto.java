package com.soundseeker.api.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * DTO for {@link com.soundseeker.api.persistence.entity.RolEntity}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RolDto(
        Long id,
        @NotBlank(message = "El nombre del rol no puede ser nulo o estar vacío.")
        @Size(message = "El nombre del rol debe tener mínimo 6 caracteres y máximo 30.", min = 6, max = 30)
        String rol) implements Serializable {
}