package com.soundseeker.api.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "POLITICA")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PoliticaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(length = 70, nullable = false, unique = true)
    @NotBlank(message = "El titulo de la politica no puede ser nulo o estar vacío.")
    @Size(max = 70, message = "El titulo de la politica no puede tener más de 70 caracteres.")
    private String titulo;

    @Column(length = 500)
    @NotBlank(message = "La descripción de la politica no puede ser nula o estar vacía.")
    @Size(max = 500, message = "La descripción de la categoría no puede tener más de 1000 caracteres.")
    private String descripcion;

    @Column(nullable = false)
    @NotBlank(message = "La imagen de la politica no puede ser nula o estar vacía.")
    private String imagen;

    @ManyToMany(mappedBy = "politicas")
    private Set<CategoriaEntity> categorias = new HashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        PoliticaEntity that = (PoliticaEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
