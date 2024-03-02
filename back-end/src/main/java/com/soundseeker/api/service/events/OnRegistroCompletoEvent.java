package com.soundseeker.api.service.events;

import com.soundseeker.api.persistence.entity.UsuarioEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnRegistroCompletoEvent extends ApplicationEvent {
    private final UsuarioEntity usuario;

    public OnRegistroCompletoEvent(final UsuarioEntity usuario) {
        super(usuario);
        this.usuario = usuario;
    }
}
