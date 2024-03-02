package com.soundseeker.api.service.impl;

import com.soundseeker.api.persistence.entity.ProductoEntity;
import com.soundseeker.api.persistence.entity.ReservaEntity;
import com.soundseeker.api.persistence.repository.ReservaRepository;
import com.soundseeker.api.service.IReservaService;
import com.soundseeker.api.service.dto.ReservaDto;
import com.soundseeker.api.service.events.OnReservaCanceladaEvent;
import com.soundseeker.api.service.exception.MalaSolicitudException;
import com.soundseeker.api.service.exception.RecursoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservaService implements IReservaService {
    private final ReservaRepository reservaRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ReservaService(ReservaRepository reservaRepository, ApplicationEventPublisher eventPublisher) {
        this.reservaRepository = reservaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ReservaDto registrar(ReservaEntity reserva) {
        ProductoEntity producto = reserva.getProductos().stream().findFirst().orElseThrow();
        this.validarProductoReservadoEnFechas(producto, reserva);
        this.validarFechas(reserva);

        reserva.setFechaOrden(LocalDateTime.now());
        ReservaEntity reservaGuardada = this.reservaRepository.save(reserva);
        return ReservaDto.mapearDesde(reservaGuardada);
    }

    private void validarProductoReservadoEnFechas(ProductoEntity producto, ReservaEntity reserva) {
        boolean estaReservado = this.reservaRepository.isProductoReservadoEnFechas(producto.getId(),
                reserva.getFechaRetiro(), reserva.getFechaEntrega());
        if (estaReservado) {
            throw new MalaSolicitudException("El instrumento ya se encuentra reservado en las fechas indicadas.");
        }
    }

    private void validarFechas(ReservaEntity reserva) {
        if (!reserva.getFechaEntrega().isAfter(reserva.getFechaRetiro())) {
            throw new MalaSolicitudException("La fecha de retiro debe ser menor a la fecha de entrega.");
        }
    }

    @Override
    public ReservaDto obtenerPorId(Long id) {
        Optional<ReservaEntity> reserva = this.reservaRepository.findById(id);

        if (reserva.isEmpty()) {
            throw new RecursoNoEncontradoException("Recurso no encontrado");
        }

        return ReservaDto.mapearDesde(reserva.orElseThrow());
    }


    @Override
    public List<ReservaDto> obtenerPorUsuario(String nombreUsuario) {
        return this.reservaRepository.findAllByUsuarioNombreUsuarioOrderByIdDesc(nombreUsuario)
                .stream().
                map(ReservaDto::mapearDesde).
                collect(Collectors.toList());
    }

    @Override
    public void eliminar(Long id) {
        ReservaEntity reserva = this.reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recurso no encontrado"));
        ReservaDto reservaDto = ReservaDto.mapearDesde(reserva);

        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaRetiroPermitida = reserva.getFechaRetiro().plusDays(1);

        if (fechaActual.isAfter(fechaRetiroPermitida)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
            throw new MalaSolicitudException("No se puede cancelar la reserva porque la fecha máxima permitida era el "
                    + formatter.format(fechaRetiroPermitida));
        }

        this.reservaRepository.deleteById(id);
        this.eventPublisher.publishEvent(new OnReservaCanceladaEvent(reservaDto));
    }

    @Override
    @Transactional
    public void calificar(Long id, Integer calificacion) {
        ReservaEntity reserva = this.reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recurso no encontrado"));
        if (LocalDate.now().isBefore(reserva.getFechaEntrega()) || reserva.getCalificacion() != null)
            throw new MalaSolicitudException("No es posible calificar una reserva que no ha sido finalizada o ya " +
                    "ha sido calificada.");

        this.reservaRepository.updateCalificacionById(id, calificacion);
    }
}
