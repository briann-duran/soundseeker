package com.soundseeker.api.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.soundseeker.api.persistence.entity.CategoriaEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDto {
    private Long id;
    private String nombre;
    private String imagen;
    private Boolean disponible;
    private String descripcion;
    private Set<PoliticaDto> politicas;

    public static CategoriaDto mapearDesde(CategoriaEntity categoria) {
        return new CategoriaDto(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getImagen(),
                categoria.getDisponible(),
                categoria.getDescripcion(),
                PoliticaDto.mapearSet(categoria.getPoliticas())
        );
    }
}
