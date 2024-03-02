package com.soundseeker.api.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.soundseeker.api.persistence.entity.PoliticaEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PoliticaDto {
    private Long id;
    private String titulo;
    private String descripcion;
    private String imagen;

    public static PoliticaDto mapearDesde(PoliticaEntity politica) {
        return new PoliticaDto(
                politica.getId(),
                politica.getTitulo(),
                politica.getDescripcion(),
                politica.getImagen()
        );
    }

    public static Set<PoliticaDto> mapearSet(Set<PoliticaEntity> politicas) {
        return politicas.stream()
                .map(PoliticaDto::mapearDesde)
                .collect(Collectors.toSet());
    }
}
