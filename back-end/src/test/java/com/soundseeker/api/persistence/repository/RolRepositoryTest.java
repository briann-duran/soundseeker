package com.soundseeker.api.persistence.repository;

import com.soundseeker.api.persistence.entity.RolEntity;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@Testcontainers(parallel = true)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RolRepositoryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RolRepositoryTest.class);

    @Container
    @ServiceConnection
    public static MySQLContainer<?> mySQLContainer;

    static {
        try (MySQLContainer<?> container = mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:latest"))) {
            container.withDatabaseName("soundseeker");
        } catch (Exception e) {
            LOGGER.error("No se pudo crear el contenedor de MySQL: {}", e.getMessage());
        }
    }

    @Autowired
    private RolRepository rolRepository;

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("spring.jpa.defer-datasource-initialization", () -> false);
        registry.add("spring.sql.init.mode", () -> "never");
        registry.add("spring.jpa.show-sql", () -> false);
        registry.add("logging.level.org.springframework.security.web.*", () -> "off");
    }

    @Test
    @Order(1)
    void deberiaAsegurarQueLaConexionFueExitosa() {
        assertThat(mySQLContainer.isCreated()).isTrue();
        assertThat(mySQLContainer.isRunning()).isTrue();
        LOGGER.info("El contenedor está creado y está corriendo.");
    }

    @Test
    @Order(2)
    void dadosTodosLosParametrosCorrectos_cuandoSeIntenteRegistrar_entoncesDeberiaGuardarEnLaBaseDeDatos() {
        RolEntity rol = new RolEntity(null, "USER", List.of());
        RolEntity rolGuardado = this.rolRepository.save(rol);
        assertThat(rolGuardado).isNotNull();
        assertThat(rolGuardado.getId()).isNotNull().isEqualTo(1L);
        assertThat(rolGuardado.getRol()).isEqualTo(rol.getRol());
        assertThat(rolGuardado.getUsuarios()).isEmpty();
        LOGGER.info("Los atributos del Rol guardado son iguales a los del Rol pasado; el ID no es nulo, se " +
                "autoasignó a '1' y el Repositorio tiene 1 elemento en total.");
    }

    @Test
    @Order(3)
    void dadoUnNombreNulo_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        RolEntity rol = new RolEntity(null, null, null);
        assertThatCode(() -> this.rolRepository.save(rol)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por pasar un nombre del Rol nulo.");
    }

    @Test
    @Order(4)
    void dadoUnNombreVacio_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        RolEntity rol = new RolEntity(null, " ", null);
        assertThatCode(() -> this.rolRepository.save(rol)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por pasar un nombre del Rol vacío.");
    }

    @Test
    @Order(5)
    void dadoUnNombreDeRolConLongituMayorDe30Caracteres_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        RolEntity rol = new RolEntity(null, "ADMINISTRADOR DE LA APLICACIÓN UNIVERSAL", null);
        assertThatCode(() -> this.rolRepository.save(rol)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por pasar un nombre del Rol con longitud mayor a 30 caracteres.");
    }

    @Test
    @Order(6)
    void dadoUnNombreDeRolExistente_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        RolEntity rol = new RolEntity(null, "USER", null);
        assertThatCode(() -> this.rolRepository.save(rol)).isInstanceOf(Exception.class);
        LOGGER.info("El nombre del Rol ya existe en la base de datos.");
    }

    @Test
    @Order(7)
    void dadoUnIdDeRol_cuandoSeLlameAlMetodoBuscarPoId_entoncesDeberiaRetornarLaEntidad() {
        RolEntity rol = this.rolRepository.findById(1L).orElse(null);
        assertThat(rol).isNotNull();
        assertThat(rol.getId()).isNotNull().isEqualTo(1L);
        assertThat(rol.getRol()).isEqualTo("USER");
        LOGGER.info("Se encontró el Rol con ID '1' en la base de datos.");
    }

    @Test
    @Order(8)
    void dadoUnIdDeRolInexistente_cuandoSeLlameAlMetodoBuscarPoId_entoncesDeberiaRetornarNulo() {
        RolEntity rol = this.rolRepository.findById(100L).orElse(null);
        assertThat(rol).isNull();
        LOGGER.info("No se encontró el Rol con ID '100' en la base de datos.");
    }

    @Test
    @Order(9)
    void dadoUnNombreDeRolExistente_cuandoSeLlameAlMetodoBuscarPoRol_entoncesDeberiaRetornarLaEntidad() {
        RolEntity rol = this.rolRepository.findByRolIgnoreCase("USER");
        assertThat(rol).isNotNull();
        assertThat(rol.getId()).isNotNull().isEqualTo(1L);
        assertThat(rol.getRol()).isEqualTo("USER");
        LOGGER.info("Se encontró el Rol 'USER' en la base de datos.");
    }

    @Test
    @Order(10)
    void dadoUnNombreDeRolInexistente_cuandoSeLlameAlMetodoBuscarPoRol_entoncesDeberiaRetornarNulo() {
        RolEntity rol = this.rolRepository.findByRolIgnoreCase("ENCARGADO");
        assertThat(rol).isNull();
        LOGGER.info("No se encontró el Rol 'ENCARGADO' en la base de datos.");
    }

    @Test
    @Order(11)
    void dadoUnNombreDeRolNulo_cuandoSeLlameAlMetodoBuscarPoRol_entoncesDeberiaRetornarNulo() {
        RolEntity rol = this.rolRepository.findByRolIgnoreCase(null);
        assertThat(rol).isNull();
        LOGGER.info("Nada fue encontrado porque se pasó un nulo.");
    }

    @Test
    @Order(12)
    void dadoUnObjetoRolLleno_cuandoSeUsenLosGetters_entoncesDeberiaRetornarLosValoresCorrectos() {
        RolEntity rol = new RolEntity(1L, "ADMIN", List.of());
        assertThat(rol).isNotNull();
        assertThat(rol.getId()).isNotNull().isEqualTo(1L);
        assertThat(rol.getRol()).isEqualTo("ADMIN");
        assertThat(rol.getUsuarios()).isEmpty();
        LOGGER.info("Se creó un Rol con valores correctos y se usaron los Getters.");
    }

    @Test
    @Order(13)
    void dadoUnObjetoRolVacio_cuandoSeUsenLosSetters_entoncesDeberiaRetornarLosValoresCorrectos() {
        RolEntity rol = new RolEntity();
        rol.setId(1L);
        rol.setRol("ADMIN");
        rol.setUsuarios(List.of());
        assertThat(rol).isNotNull();
        assertThat(rol.getId()).isNotNull().isEqualTo(1L);
        assertThat(rol.getRol()).isEqualTo("ADMIN");
        assertThat(rol.getUsuarios()).isEmpty();
        LOGGER.info("Se creó un Rol vacío y se le asignaron valores correctos.");
    }

    @Test
    @Order(14)
    void dadoUnRolConLosMismosAtributos_cuandoSeCompareConOtroRol_entoncesDeberiaRetornarTrue() {
        RolEntity rol1 = new RolEntity(1L, "ADMIN", null);
        RolEntity rol2 = new RolEntity(1L, "ADMIN", null);
        assertThat(rol1).isEqualTo(rol2);
        LOGGER.info("Se compararon dos Roles con los mismos atributos y se obtuvo 'true'.");
    }

    @Test
    @Order(15)
    void dadoUnRolConLosDiferentesAtributos_cuandoSeCompareConOtroRol_entoncesDeberiaRetornarFalse() {
        RolEntity rol1 = new RolEntity(1L, "ADMIN", null);
        RolEntity rol2 = new RolEntity(2L, "USER", null);
        assertThat(rol1).isNotEqualTo(rol2);
        LOGGER.info("Se compararon dos Roles con diferentes atributos y se obtuvo 'false'.");
    }

    @Test
    @Order(16)
    void dadoElHashCodeDeUnRol_cuandoSeCompareConElHashCodeDeOtroRol_entoncesDeberiaRetornarTrue() {
        RolEntity rol1 = new RolEntity(1L, "ADMIN", null);
        RolEntity rol2 = new RolEntity(1L, "ADMIN", null);
        assertThat(rol1.hashCode()).isEqualTo(rol2.hashCode());
        LOGGER.info("Se compararon los HashCodes de dos Roles con los mismos atributos y se obtuvo 'true'.");
    }

    @Test
    @Order(17)
    void dadoElHashCodeDeUnRolProvenienteDeLaBaseDeDatos_cuandoSeCompareConElHashCodeDeOtroRol_entoncesDeberiaRetornarTrue() {
        RolEntity rolEncontrado = this.rolRepository.findById(1L).orElse(null);
        RolEntity rolCreado = new RolEntity(1L, "ADMIN", null);
        assert rolEncontrado != null;
        assertThat(rolEncontrado.hashCode()).isEqualTo(rolCreado.hashCode());
        LOGGER.info("Se compararon los HashCodes de dos Roles con los mismos atributos y se obtuvo 'true'.");
    }
}