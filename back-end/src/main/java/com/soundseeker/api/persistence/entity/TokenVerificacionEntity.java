package com.soundseeker.api.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "TOKEN_VERIFICACION")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TokenVerificacionEntity {
    private static final int EXPIRACION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @Column(columnDefinition = "DATETIME", nullable = false)
    private LocalDateTime fechaExpiracion;

    @OneToOne(targetEntity = UsuarioEntity.class)
    @JoinColumn(name = "nombre_usuario", nullable = false)
    private UsuarioEntity usuario;

    public TokenVerificacionEntity(String token, UsuarioEntity usuario) {
        this.token = token;
        this.fechaExpiracion = this.calcularFechaExpiracion();
        this.usuario = usuario;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        TokenVerificacionEntity that = (TokenVerificacionEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    private LocalDateTime calcularFechaExpiracion() {
        return LocalDateTime.now().plusMinutes(TokenVerificacionEntity.EXPIRACION);
    }
}
