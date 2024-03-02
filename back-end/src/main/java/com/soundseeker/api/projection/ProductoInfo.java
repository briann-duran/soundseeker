package com.soundseeker.api.projection;

import com.soundseeker.api.persistence.entity.ProductoEntity;

import java.util.Set;

/**
 * Projection for {@link ProductoEntity}
 */
public interface ProductoInfo {
    Long getId();

    String getNombre();

    String getMarca();

    Set<String> getImagenes();
}