package com.soundseeker.api.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "RESERVA")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReservaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(columnDefinition = "DATETIME", name = "fecha_orden", nullable = false)
    private LocalDateTime fechaOrden;

    @Column(columnDefinition = "DATE", name = "fecha_retiro", nullable = false)
    @Future(message = "La fecha de retiro debe ser futura y empezar a partir de mañana.")
    @NotNull(message = "La fecha de retiro no puede ser nula.")
    private LocalDate fechaRetiro;

    @Column(columnDefinition = "DATE", name = "fecha_entrega", nullable = false)
    @NotNull(message = "La fecha de entrega no puede ser nula.")
    private LocalDate fechaEntrega;

    @Column(columnDefinition = "VARCHAR(500)", name = "notas", length = 500)
    @Size(max = 500, message = "Las notas de la reserva no pueden tener más de 500 caracteres.")
    private String notas;

    @Positive(message = "La calificación de la reserva debe ser positiva.")
    @Range(min = 1, max = 5, message = "La calificación de la reserva debe ser mayor o igual a 1 y menor o igual a 5.")
    private Integer calificacion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_nombre_usuario", nullable = false)
    @NotNull(message = "El usuario de la reserva no puede ser nulo.")
    private UsuarioEntity usuario;

    @ManyToMany
    @JoinTable(name = "PRODUCTO_RESERVA",
            joinColumns = @JoinColumn(name = "reserva_id"),
            inverseJoinColumns = @JoinColumn(name = "producto_id"))
    @NotNull(message = "La reserva no puede tener productos vacíos.")
    private Set<ProductoEntity> productos = new LinkedHashSet<>();

    @PrePersist
    @PreUpdate
    private void validateFechaEntrega() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        if (fechaEntrega != null && (fechaEntrega.isEqual(today) || fechaEntrega.isEqual(tomorrow))) {
            throw new IllegalStateException("La fecha de entrega no puede ser hoy ni mañana.");
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ReservaEntity that = (ReservaEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
