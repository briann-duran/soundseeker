package com.soundseeker.api.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "USUARIO")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsuarioEntity {
    @Id
    @Column(columnDefinition = "VARCHAR(20) COLLATE utf8mb4_0900_ai_ci", name = "nombre_usuario", nullable = false, unique = true)
    private String nombreUsuario;

    @Column(length = 60, nullable = false)
    private String contrasena;

    @Column(length = 30, nullable = false)
    private String nombre;

    @Column(length = 30, nullable = false)
    private String apellido;

    @Column(columnDefinition = "VARCHAR(50) COLLATE utf8mb4_0900_ai_ci", name = "correo_electronico", nullable = false, unique = true)
    private String correoElectronico;

    @Column(columnDefinition = "TINYINT", nullable = false)
    private Boolean deshabilitado;

    @Column(columnDefinition = "TINYINT", nullable = false)
    private Boolean bloqueado;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "roles_usuarios",
            joinColumns = @JoinColumn(name = "nombre_usuario", referencedColumnName = "nombre_usuario"),
            inverseJoinColumns = @JoinColumn(name = "rol_usuario", referencedColumnName = "id"))
    private Collection<RolEntity> roles;

    @ManyToMany(mappedBy = "usuarios")
    private Set<ProductoEntity> productos = new LinkedHashSet<>();

    public UsuarioEntity(String nombreUsuario, String contrasena, String nombre, String apellido, String correoElectronico, Boolean deshabilitado, Boolean bloqueado, Collection<RolEntity> roles) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correoElectronico = correoElectronico;
        this.deshabilitado = deshabilitado;
        this.bloqueado = bloqueado;
        this.roles = roles;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UsuarioEntity that = (UsuarioEntity) o;
        return getNombreUsuario() != null && Objects.equals(getNombreUsuario(), that.getNombreUsuario());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}