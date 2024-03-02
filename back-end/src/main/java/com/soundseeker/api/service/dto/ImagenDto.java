package com.soundseeker.api.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.soundseeker.api.persistence.entity.ImagenEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagenDto {
    private Long id;
    private String title;
    private String urlImage;

    public static ImagenDto mapearDesde(ImagenEntity imagen) {
        return new ImagenDto(imagen.getId(), imagen.getTitulo(), imagen.getUrlImagen());
    }
}
