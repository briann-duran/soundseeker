package com.soundseeker.api.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "CATEGORIA")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoriaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(length = 30, nullable = false, unique = true)
    @NotBlank(message = "El nombre de la categoría no puede ser nulo o estar vacío.")
    @Size(max = 30, message = "El nombre de la categoría no puede tener más de 30 caracteres.")
    private String nombre;

    @Column(nullable = false)
    @NotBlank(message = "La imagen de la categoría no puede ser nula o estar vacía.")
    private String imagen;

    private Boolean disponible;

    @OneToMany(mappedBy = "categoria")
    @JsonIgnore
    @ToString.Exclude
    private Set<ProductoEntity> productos;

    @Column(length = 500)
    @NotBlank(message = "La descripción de la categoría no puede ser nula o estar vacía.")
    @Size(max = 500, message = "La descripción de la categoría no puede tener más de 1000 caracteres.")
    private String descripcion;

    @ManyToMany
    @JoinTable(
            name = "politica_categoria",
            joinColumns = {@JoinColumn(name = "categoria_id")},
            inverseJoinColumns = {@JoinColumn(name = "politica_id")}
    )
    private Set<PoliticaEntity> politicas = new LinkedHashSet<>();


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        CategoriaEntity that = (CategoriaEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
