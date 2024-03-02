package com.soundseeker.api.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "PRODUCTO")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(length = 60, nullable = false, unique = true)
    @NotBlank(message = "El nombre del producto no puede ser nulo o estar vacío.")
    @Size(max = 60, message = "El nombre del producto no puede tener más de 60 caracteres.")
    private String nombre;

    @Column(length = 1000, nullable = false)
    @NotBlank(message = "La descripción del producto no puede ser nula o estar vacía.")
    @Size(max = 1000, message = "La descripción del producto no puede tener más de 1000 caracteres.")
    private String descripcion;

    @Column(columnDefinition = "VARCHAR(60)", nullable = false)
    @NotBlank(message = "La marca del producto no puede ser nula o estar vacía.")
    @Size(max = 60, message = "La marca del producto no puede tener más de 60 caracteres.")
    private String marca;

    @Column(columnDefinition = "DECIMAL(10, 2)", nullable = false)
    @NotNull(message = "El precio no puede ser nulo.")
    @Positive(message = "El precio debe ser positivo y mayor a cero.")
    private Double precio;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "IMAGENES_PRODUCTO", joinColumns = @JoinColumn(name = "producto_id"))
    @Column(name = "imagen", columnDefinition = "VARCHAR(1000)")
    private Set<String> imagenes;

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    @NotNull(message = "La disponibilidad del producto no puede ser nula.")
    private Boolean disponible;

    @ManyToOne
    @JoinColumn(name = "categoria_id", referencedColumnName = "id")
    @NotNull(message = "La categoría del producto no puede ser nula.")
    private CategoriaEntity categoria;

    @ManyToMany
    @JoinTable(
            name = "PRODUCTO_CARACTERISTICA",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "caracteristica_id")
    )
    private Set<CaracteristicaEntity> caracteristicas = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "FAVORITO",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_nombre_usuario"))
    private Set<UsuarioEntity> usuarios = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "productos")
    private Set<ReservaEntity> reservas = new LinkedHashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ProductoEntity that = (ProductoEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
