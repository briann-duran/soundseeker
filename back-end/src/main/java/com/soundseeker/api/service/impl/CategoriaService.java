package com.soundseeker.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soundseeker.api.persistence.entity.CategoriaEntity;
import com.soundseeker.api.persistence.entity.PoliticaEntity;
import com.soundseeker.api.persistence.entity.ProductoEntity;
import com.soundseeker.api.persistence.repository.CategoriaRepository;
import com.soundseeker.api.service.ICategoriaService;
import com.soundseeker.api.service.dto.CategoriaDto;
import com.soundseeker.api.service.dto.PoliticaDto;
import com.soundseeker.api.service.dto.ProductoDto;
import com.soundseeker.api.service.exception.NombreDuplicadoException;
import com.soundseeker.api.service.exception.RecursoNoEncontradoException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoriaService implements ICategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final ProductoService productoService;
    private final PoliticaService politicaService;
    private final ObjectMapper mapper;

    @Autowired
    public CategoriaService(CategoriaRepository categoriaRepository, ProductoService productoService, PoliticaService politicaService, ObjectMapper mapper) {
        this.categoriaRepository = categoriaRepository;
        this.productoService = productoService;
        this.politicaService = politicaService;
        this.mapper = mapper;
    }

    @Override
    public CategoriaDto registrar(CategoriaEntity categoria) {
        Optional<CategoriaEntity> categoriaExistente = this.categoriaRepository.findByNombreIgnoreCase(categoria.getNombre());
        if (categoriaExistente.isPresent()) {
            throw new NombreDuplicadoException("El nombre de la categoría ya está registrado.");
        }
        CategoriaEntity nuevaCategoria = this.categoriaRepository.save(categoria);
        return CategoriaDto.mapearDesde(nuevaCategoria);
    }

    @Override
    public CategoriaDto obtenerPorId(Long id) {
        CategoriaEntity categoria = this.categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recurso no encontrado"));
        return CategoriaDto.mapearDesde(categoria);
    }

    @Override
    public List<CategoriaDto> obtenerTodo() {
        return this.categoriaRepository.findAll()
                .stream()
                .map(CategoriaDto::mapearDesde)
                .collect(Collectors.toList());
    }

    @Override
    public Set<ProductoDto> findProductosById(Long categoriaId) {
        return this.categoriaRepository.findProductosById(categoriaId)
                .stream()
                .map(ProductoDto::mapearDesde)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<PoliticaEntity> findPoliticasById(Long categoriaId) {
        return this.categoriaRepository.findPoliticasById(categoriaId);
    }

    @Override
    public List<CategoriaDto> obtenerAleatorio() {
        return this.categoriaRepository.obtenerAleatorio(5)
                .stream()
                .map(CategoriaDto::mapearDesde)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminar(Long id) {
        CategoriaDto categoriaDto = obtenerPorId(id);
        Set<ProductoDto> productosDto = findProductosById(categoriaDto.getId());

        for (ProductoDto producto : productosDto) {
            producto.getCaracteristicas().clear();
            producto.setCategoria(null);
            productoService.eliminar(producto.getId());
        }

        Set<PoliticaDto> politicas = categoriaDto.getPoliticas();

        for (PoliticaDto politica : politicas) {
            Set<CategoriaEntity> categoriasByPoliticaId = politicaService.findCategoriasByPoliticaId(politica.getId());
            categoriasByPoliticaId.removeIf(c -> c.getId().equals(categoriaDto.getId()));
            if (categoriasByPoliticaId.isEmpty()) {
                politicaService.eliminarPoliticaPorId(politica.getId());
            }
        }

        categoriaDto.getPoliticas().clear();
        CategoriaEntity categoriaEntity = mapper.convertValue(categoriaDto, CategoriaEntity.class);

        categoriaRepository.save(categoriaEntity);
        this.categoriaRepository.deleteById(categoriaDto.getId());
        politicaService.eliminarPolitica();
    }

    @Override
    public CategoriaDto editar(CategoriaEntity categoria) {
        Optional<CategoriaEntity> found = categoriaRepository.findById(categoria.getId());

        if (found.isPresent()) {
            CategoriaEntity existingCategoria = found.get();
            existingCategoria.setNombre(categoria.getNombre());
            existingCategoria.setImagen(categoria.getImagen());
            existingCategoria.setDisponible(categoria.getDisponible());
            existingCategoria.setDescripcion(categoria.getDescripcion());
            existingCategoria.setPoliticas(categoria.getPoliticas());
            CategoriaEntity updatedCategoria = categoriaRepository.save(existingCategoria);

            CategoriaDto categoriaDto = new CategoriaDto();
            categoriaDto.setId(updatedCategoria.getId());
            categoriaDto.setNombre(updatedCategoria.getNombre());
            categoriaDto.setImagen(updatedCategoria.getImagen());
            categoriaDto.setDisponible(categoria.getDisponible());
            categoriaDto.setDescripcion(updatedCategoria.getDescripcion());
            categoriaDto.setPoliticas(PoliticaDto.mapearSet(updatedCategoria.getPoliticas()));
            return categoriaDto;
        } else {
            throw new RecursoNoEncontradoException("Categoría no encontrada");
        }
    }

    public void cambiarDisponibilidadAFalse(Long categoriaId) {
        CategoriaEntity categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));

        CategoriaEntity varios = categoriaRepository.findByNombreIgnoreCase("Varios")
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría 'Varios' no encontrada"));

        Set<ProductoEntity> productos = categoria.getProductos();

        for (ProductoEntity producto : productos) {
            producto.setCategoria(varios);
            productoService.editar(producto);
        }

        categoria.setDisponible(false);

        categoriaRepository.save(categoria);
    }

    @Transactional
    @Override
    public void cambiarDisponibilidadATrue(Long categoriaId) {
        Optional<CategoriaEntity> categoriaOptional = categoriaRepository.findById(categoriaId);
        if (categoriaOptional.isPresent()) {
            CategoriaEntity categoria = categoriaOptional.get();
            categoria.setDisponible(true);
            categoriaRepository.save(categoria);
        } else {
            throw new RecursoNoEncontradoException("Categoría no encontrada");
        }
    }

    public List<CategoriaDto> obtenerCategoriasDisponiblesTrue() {
        return categoriaRepository.findAllByDisponibleTrue()
                .stream()
                .map(CategoriaDto::mapearDesde)
                .collect(Collectors.toList());
    }
}
