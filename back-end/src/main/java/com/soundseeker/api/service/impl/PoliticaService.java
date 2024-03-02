package com.soundseeker.api.service.impl;

import com.soundseeker.api.persistence.entity.CategoriaEntity;
import com.soundseeker.api.persistence.entity.PoliticaEntity;
import com.soundseeker.api.persistence.repository.PoliticaRepository;
import com.soundseeker.api.service.IPoliticaService;
import com.soundseeker.api.service.dto.PoliticaDto;
import com.soundseeker.api.service.exception.NombreDuplicadoException;
import com.soundseeker.api.service.exception.RecursoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PoliticaService implements IPoliticaService {
    private final PoliticaRepository politicaRepository;

    @Autowired
    public PoliticaService(PoliticaRepository politicaRepository) {
        this.politicaRepository = politicaRepository;
    }

    @Override
    public List<PoliticaDto> obtenerTodasLasPoliticas() {
        return this.politicaRepository.findAll()
                .stream()
                .map(PoliticaDto::mapearDesde)
                .collect(Collectors.toList());
    }

    @Override
    public PoliticaDto obtenerPoliticaPorId(Long id) {
        PoliticaEntity politica = this.politicaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recurso no encontrado"));
        return PoliticaDto.mapearDesde(politica);
    }

    @Override
    public PoliticaDto registrarPolitica(PoliticaEntity politica) {
        Optional<PoliticaEntity> politicaExistente = this.politicaRepository.findByTituloIgnoreCase(politica.getTitulo());
        if (politicaExistente.isPresent()) {
            throw new NombreDuplicadoException("El titulo de la politica ya está registrado.");
        }

        PoliticaEntity nuevaPolitica = this.politicaRepository.save(politica);
        return PoliticaDto.mapearDesde(nuevaPolitica);
    }

    @Override
    public Set<CategoriaEntity> findCategoriasByPoliticaId(Long id) {
        return this.politicaRepository.findCategoriasByPoliticaId(id);
    }

    @Override
    public void eliminarPoliticaPorId(Long id) {
        if (!this.politicaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Politica no encontrada");
        }
        this.politicaRepository.deleteById(id);
    }

    @Override
    public void eliminarPolitica() {
        List<PoliticaEntity> politicas = politicaRepository.findPoliticasSinCategorias();
        this.politicaRepository.deleteAll(politicas);
    }
}
