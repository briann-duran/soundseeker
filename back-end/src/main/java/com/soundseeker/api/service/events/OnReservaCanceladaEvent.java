package com.soundseeker.api.service.events;

import com.soundseeker.api.service.dto.ReservaDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnReservaCanceladaEvent extends ApplicationEvent {
    private final ReservaDto reserva;

    public OnReservaCanceladaEvent(final ReservaDto reserva) {
        super(reserva);
        this.reserva = reserva;
    }
}
