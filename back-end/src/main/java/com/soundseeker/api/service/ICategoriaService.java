package com.soundseeker.api.service;

import com.soundseeker.api.persistence.entity.CategoriaEntity;
import com.soundseeker.api.persistence.entity.PoliticaEntity;
import com.soundseeker.api.service.dto.CategoriaDto;
import com.soundseeker.api.service.dto.ProductoDto;

import java.util.List;
import java.util.Set;

public interface ICategoriaService {

    CategoriaDto registrar(CategoriaEntity categoria);

    CategoriaDto obtenerPorId(Long id);

    List<CategoriaDto> obtenerTodo();

    Set<ProductoDto> findProductosById(Long categoriaId);

    Set<PoliticaEntity> findPoliticasById(Long categoriaId);

    List<CategoriaDto> obtenerAleatorio();

    void eliminar(Long id);

    CategoriaDto editar(CategoriaEntity categoria);

    void cambiarDisponibilidadAFalse(Long categoriaId);

    void cambiarDisponibilidadATrue(Long categoriaId);

    List<CategoriaDto> obtenerCategoriasDisponiblesTrue();

}

