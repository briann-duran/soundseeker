package com.soundseeker.api.persistence.repository;

import com.soundseeker.api.persistence.entity.UsuarioEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Testcontainers(parallel = true)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioRepositoryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioRepositoryTest.class);

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
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EntityManager entityManager;

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
    void dadosTodosLosParametrosCorrectos_cuandoSeRegistre_entoncesDeberiaGuardarEnLaBaseDeDatos() {
        UsuarioEntity usuario = new UsuarioEntity("kurtcobain", "Nirvana-1987", "Kurt",
                "Cobain", "contact@kurtcobain.com", false, false, null);
        UsuarioEntity usuarioGuardado = usuarioRepository.save(usuario);
        assertThat(usuarioGuardado).isNotNull();
        assertThat(usuarioGuardado.getNombreUsuario()).isEqualTo(usuario.getNombreUsuario());
        assertThat(usuarioGuardado.getContrasena()).isEqualTo(usuario.getContrasena());
        assertThat(usuarioGuardado.getNombre()).isEqualTo(usuario.getNombre());
        assertThat(usuarioGuardado.getApellido()).isEqualTo(usuario.getApellido());
        assertThat(usuarioGuardado.getCorreoElectronico()).isEqualTo(usuario.getCorreoElectronico());
        assertThat(usuarioGuardado.getDeshabilitado()).isEqualTo(usuario.getDeshabilitado());
        assertThat(usuarioGuardado.getBloqueado()).isEqualTo(usuario.getBloqueado());
        LOGGER.info("Los atributos del Usuario guardado son iguales a los del Usuario pasado.");
    }

    @Test
    @Order(3)
    void dadoUnNombreDeUsuarioConLongitudDe28Caracteres_cuandoSeRegistre_entoncesDeberiaLanzarUnaExcepcion() {
        UsuarioEntity usuario = new UsuarioEntity("kurtcobainsaysnirvanaforever", "Nirvana-1987", "Kurt", "Cobain",
                "news@kurtcobain.com", false, false, null);
        assertThatCode(() -> usuarioRepository.save(usuario)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por pasar un Nombre de Usuario con longitud mayor a 20 caracteres.");
    }

    @Test
    @Order(4)
    void dadoUnNombreDeUsuarioNulo_cuandoSeRegistre_entoncesDeberiaLanzarUnaExcepcion() {
        UsuarioEntity usuario = new UsuarioEntity(null, "Nirvana-1987", "Kurt", "Cobain", "news@kurtcobain.com",
                false, false, null);
        assertThatCode(() -> usuarioRepository.save(usuario)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por pasar un Nombre de Usuario nulo.");
    }

    @Test
    @Order(5)
    void dadaUnaContrasenaConLongitudDe62Caracteres_cuandoSeRegistre_entoncesDeberiaLanzarUnaExcepcion() {
        UsuarioEntity usuario = new UsuarioEntity("kurt.cobain",
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789",
                "Kurt", "Cobain", "news@kurtcobain.com", false, false, null);
        assertThatCode(() -> usuarioRepository.save(usuario)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por pasar una Contraseña con longitud mayor a 60 caracteres.");
    }

    @Test
    @Order(6)
    void dadaUnaContrasenaNula_cuandoSeRegistre_entoncesDeberiaLanzarUnaExcepcion() {
        UsuarioEntity usuario = new UsuarioEntity("kurt.cobain", null, "Kurt", "Cobain", "news@kurtcobain.com",
                false, false, null);
        assertThatCode(() -> usuarioRepository.save(usuario)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por pasar una Contraseña nula.");
    }

    @Test
    @Order(7)
    void dadoUnNombreConLongitudDe46Caracteres_cuandoSeRegistre_entoncesDeberiaLanzarUnaExcepcion() {
        UsuarioEntity usuario = new UsuarioEntity("kurt.cobain", "Nirvana-1987",
                "Kurt Donald AKA 'Pixie Meat' and 'Shotgun Man'", "Cobain", "news@kurtcobain.com",
                false, false, null);
        assertThatCode(() -> usuarioRepository.save(usuario)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por pasar un Nombre con longitud mayor a 30 caracteres.");
    }

    @Test
    @Order(8)
    void dadoUnNombreNulo_cuandoSeRegistre_entoncesDeberiaLanzarUnaExcepcion() {
        UsuarioEntity usuario = new UsuarioEntity("kurt.cobain", "Nirvana-1987", null, "Cobain",
                "news@kurtcobain.com", false, false, null);
        assertThatCode(() -> usuarioRepository.save(usuario)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por pasar un Nombre nulo.");
    }

    @Test
    @Order(9)
    void dadoUnApellidoConLongitudDe32Caracteres_cuandoSeRegistre_entoncesDeberiaLanzarUnaExcepcion() {
        UsuarioEntity usuario = new UsuarioEntity("kurt.cobain", "Nirvana-1987", "Kurt",
                "Cobain from Aberdeen, Washington", "news@kurtcobain.com", false, false, null);
        assertThatCode(() -> usuarioRepository.save(usuario)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por pasar un Apellido con longitud mayor a 30 caracteres.");
    }

    @Test
    @Order(10)
    void dadoUnApellidoNulo_cuandoSeRegistre_entoncesDeberiaLanzarUnaExcepcion() {
        UsuarioEntity usuario = new UsuarioEntity("kurt.cobain", "Nirvana-1987", "Kurt",
                null, "news@kurtcobain.com", false, false, null);
        assertThatCode(() -> usuarioRepository.save(usuario)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por pasar un Apellido nulo.");
    }

    @Test
    @Order(11)
    void dadoUnCorreoElectronicoConLongitudDe70Caracteres_cuandoSeRegistre_entoncesDeberiaLanzarUnaExcepcion() {
        UsuarioEntity usuario = new UsuarioEntity("kurt.cobain", "Nirvana-1987", "Kurt", "Cobain",
                "getincontactwithmymanagerthroughmagicandwewillreplyasap@kurtcobain.com", false, false, null);
        assertThatCode(() -> usuarioRepository.save(usuario)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por pasar un Correo Electrónico con longitud mayor a 50 caracteres.");
    }

    @Test
    @Order(12)
    void dadoUnCorreoElectronicoNulo_cuandoSeRegistre_entoncesDeberiaLanzarUnaExcepcion() {
        UsuarioEntity usuario = new UsuarioEntity("kurt.cobain", "Nirvana-1987", "Kurt", "Cobain", null,
                false, false, null);
        assertThatCode(() -> usuarioRepository.save(usuario)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por pasar un Correo Electrónico nulo.");
    }

    @Test
    @Order(13)
    void dadoUnCorreoElectronicoRepetido_cuandoSeRegistre_entoncesDeberiaLanzarUnaExcepcion() {
        UsuarioEntity usuario = new UsuarioEntity("kurt-cobain", "Nirvana-1987", "Kurt", "Cobain",
                "contact@kurtcobain.com", false, false, null);
        assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> usuarioRepository.save(usuario));
        LOGGER.info("Se lanzó una Excepción por pasar un Correo Electrónico repetido.");
    }

    @Test
    @Order(14)
    void dadoUnCorreoElectronicoRepetidoConDiferenteCapitalizacion_cuandoSeRegistre_entoncesDeberiaLanzarUnaExcepcion() {
        this.usuarioRepository.save(new UsuarioEntity("dave.grohl", "Nirvana-1990", "Dave",
                "Grohl", "hi@davegrohl.com", false, false, null));
        this.entityManager.clear();

        assertThatExceptionOfType(Exception.class)
                .isThrownBy(() -> this.usuarioRepository.save(new UsuarioEntity("dave-grohl", "Nirvana-1994", "Dave",
                        "Grohl", "Hi@DaveGrohl.com", true, true, null)));
        LOGGER.info("Se lanzó una Excepción por pasar un Correo Electrónico repetido.");
    }

    @Test
    @Order(15)
    void dadoUnEstadoDeshabilitadoNulo_cuandoSeRegistre_entoncesDeberiaLanzarUnaExcepcion() {
        UsuarioEntity usuario = new UsuarioEntity("kurt.cobain", "Nirvana-1987", "Kurt", "Cobain",
                "news@kurtcobain.com", null, false, null);
        assertThatCode(() -> usuarioRepository.save(usuario)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por pasar un Estado Deshabilitado nulo.");
    }

    @Test
    @Order(16)
    void dadoUnEstadoBloqueadoNulo_cuandoSeRegistre_entoncesDeberiaLanzarUnaExcepcion() {
        UsuarioEntity usuario = new UsuarioEntity("kurt.cobain", "Nirvana-1987", "Kurt", "Cobain",
                "news@kurtcobain.com", false, null, null);
        assertThatCode(() -> usuarioRepository.save(usuario)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por pasar un Estado Bloqueado nulo.");
    }

    @Test
    @Order(17)
    void dadoUnNombreDeUsuarioExistente_cuandoSeLlameAlMetodoBuscarPorNombreUsuario_entoncesDeberiaRetornarLaEntidad() {
        UsuarioEntity usuario = this.usuarioRepository.findByNombreUsuarioIgnoreCase("kurtcobain").orElse(null);
        assertThat(usuario).isNotNull();
        assertThat(usuario.getNombreUsuario()).isEqualTo("kurtcobain");
        assertThat(usuario.getContrasena()).isEqualTo("Nirvana-1987");
        assertThat(usuario.getNombre()).isEqualTo("Kurt");
        assertThat(usuario.getApellido()).isEqualTo("Cobain");
        assertThat(usuario.getCorreoElectronico()).isEqualTo("contact@kurtcobain.com");
        assertThat(usuario.getDeshabilitado()).isFalse();
        assertThat(usuario.getBloqueado()).isFalse();
        LOGGER.info("Se encontró el Usuario con Nombre de Usuario 'kurtcobain' en la base de datos.");
    }

    @Test
    @Order(18)
    void dadoUnNombreDeUsuarioInexistente_cuandoSeLlameAlMetodoBuscarPorNombreUsuario_entoncesDeberiaRetornarNulo() {
        UsuarioEntity usuario = this.usuarioRepository.findByNombreUsuarioIgnoreCase("kurt.cobain").orElse(null);
        assertThat(usuario).isNull();
        LOGGER.info("No se encontró el Usuario con Nombre de Usuario 'kurt.cobain' en la base de datos.");
    }

    @Test
    @Order(19)
    void dadoUnNombreDeUsuarioNulo_cuandoSeLlameAlMetodoBuscarPorNombreUsuario_entoncesDeberiaRetornarUnOptionalVacio() {
        Optional<UsuarioEntity> usuario = this.usuarioRepository.findByNombreUsuarioIgnoreCase(null);
        assertThat(usuario).isEmpty();
        LOGGER.info("Se pasó un Nombre de Usuario nulo al método 'findByNombreUsuarioIgnoreCase' y se obtuvo un " +
                "Optional vacío.");
    }

    @Test
    @Order(20)
    void dadoUnNombreDeUsuarioVacio_cuandoSeLlameAlMetodoBuscarPorNombreUsuario_entoncesDeberiaRetornarUnOptionalVacio() {
        Optional<UsuarioEntity> usuario = this.usuarioRepository.findByNombreUsuarioIgnoreCase(" ");
        assertThat(usuario).isEmpty();
        LOGGER.info("Se pasó un Nombre de Usuario vacío al método 'findByNombreUsuarioIgnoreCase' y se obtuvo un " +
                "Optional vacío.");
    }

    @Test
    @Order(21)
    void dadoUnCorreoElectronicoExistente_cuandoSeLlameAlMetodoBuscarPorCorreoElectronico_entoncesDeberiaRetornarLaEntidad() {
        UsuarioEntity usuario = this.usuarioRepository.findByCorreoElectronicoIgnoreCase("contact@kurtcobain.com")
                .orElse(null);
        assertThat(usuario).isNotNull();
        assertThat(usuario.getNombreUsuario()).isEqualTo("kurtcobain");
        assertThat(usuario.getContrasena()).isEqualTo("Nirvana-1987");
        assertThat(usuario.getNombre()).isEqualTo("Kurt");
        assertThat(usuario.getApellido()).isEqualTo("Cobain");
        assertThat(usuario.getCorreoElectronico()).isEqualTo("contact@kurtcobain.com");
        assertThat(usuario.getDeshabilitado()).isFalse();
        assertThat(usuario.getBloqueado()).isFalse();
        LOGGER.info("Se encontró el Usuario con Correo Electrónico 'contact@kurtcobain.com' en la base de datos.");
    }

    @Test
    @Order(22)
    void dadoUnCorreoElectronicoInexistente_cuandoSeLlameAlMetodoBuscarPorCorreoElectronico_entoncesDeberiaRetornarNulo() {
        UsuarioEntity usuario = this.usuarioRepository.findByCorreoElectronicoIgnoreCase("hello@kurtcobain.com")
                .orElse(null);
        assertThat(usuario).isNull();
        LOGGER.info("No se encontró el Usuario con Correo Electrónico 'hello@kurtcobain.com' en la base de datos.");
    }

    @Test
    @Order(23)
    void dadoUnCorreoElectronicoNulo_cuandoSeLlameAlMetodoBuscarPorCorreoElectronico_entoncesDeberiaRetornarUnOptionalVacio() {
        Optional<UsuarioEntity> usuario = this.usuarioRepository.findByCorreoElectronicoIgnoreCase(null);
        assertThat(usuario).isEmpty();
        LOGGER.info("Se pasó un Correo Electrónico nulo al método 'findByCorreoElectronicoIgnoreCase' y se obtuvo un " +
                "Optional vacío.");
    }

    @Test
    @Order(24)
    void dadoUnCorreoElectronicoVacio_cuandoSeLlameAlMetodoBuscarPorCorreoElectronico_entoncesDeberiaRetornarUnOptionalVacio() {
        Optional<UsuarioEntity> usuario = this.usuarioRepository.findByCorreoElectronicoIgnoreCase(" ");
        assertThat(usuario).isEmpty();
        LOGGER.info("Se pasó un Correo Electrónico vacío al método 'findByCorreoElectronicoIgnoreCase' y se obtuvo " +
                "un Optional vacío.");
    }

    @Test
    @Order(25)
    void dadoUnNombreDeUsuarioExistenteYCorreoElectronicoExistente_cuandoSeLlameAlMetodoBuscarPorNombreUsuarioOCorreoElectronico_entoncesDeberiaRetornarLaEntidad() {
        UsuarioEntity usuario = this.usuarioRepository
                .findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase("kurtcobain", "contact@kurtcobain.com");
        assertThat(usuario).isNotNull();
        assertThat(usuario.getNombreUsuario()).isEqualTo("kurtcobain");
        assertThat(usuario.getContrasena()).isEqualTo("Nirvana-1987");
        assertThat(usuario.getNombre()).isEqualTo("Kurt");
        assertThat(usuario.getApellido()).isEqualTo("Cobain");
        assertThat(usuario.getCorreoElectronico()).isEqualTo("contact@kurtcobain.com");
        assertThat(usuario.getDeshabilitado()).isFalse();
        assertThat(usuario.getBloqueado()).isFalse();
        LOGGER.info("Se encontró el Usuario con Nombre de Usuario 'kurtcobain' y el Correo Electrónico " +
                "'contact@kurtcobain.com' en la base de datos.");
    }

    @Test
    @Order(26)
    void dadoUnNombreDeUsuarioInexistenteYCorreoElectronicoInexistente_cuandoSeLlameAlMetodoBuscarPorNombreUsuarioOCorreoElectronico_entoncesDeberiaRetornarNulo() {
        UsuarioEntity usuario = this.usuarioRepository
                .findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase("kurt.cobain", "hello@kurtcobain.com");
        assertThat(usuario).isNull();
        LOGGER.info("No se encontró el Usuario con Nombre de Usuario 'kurt.cobain' y el Correo Electrónico " +
                "'hello@kurtcobain.com' en la base de datos.");
    }

    @Test
    @Order(27)
    void dadoUnNombreDeUsuarioNuloYCorreoElectronicoNulo_cuandoSeLlameAlMetodoBuscarPorNombreUsuarioOCorreoElectronico_entoncesDeberiaRetornarNulo() {
        UsuarioEntity usuario = this.usuarioRepository
                .findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase(null, null);
        assertThat(usuario).isNull();
        LOGGER.info("Se pasó un Nombre de Usuario nulo y Correo Electrónico nulo al método " +
                "'findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase' y se obtuvo un nulo.");
    }

    @Test
    @Order(28)
    void dadoUnNombreDeUsuarioVacioYCorreoElectronicoVacio_cuandoSeLlameAlMetodoBuscarPorNombreUsuarioOCorreoElectronico_entoncesDeberiaRetornarNulo() {
        UsuarioEntity usuario = this.usuarioRepository
                .findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase(" ", " ");
        assertThat(usuario).isNull();
        LOGGER.info("Se pasó un Nombre de Usuario vacío y Correo Electrónico vacío al método " +
                "'findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase' y se obtuvo un nulo.");
    }

    @Test
    @Order(29)
    void dadoUnNombreDeUsuarioExistenteYCorreoElectronicoInexistente_cuandoSeLlameAlMetodoBuscarPorNombreUsuarioOCorreoElectronico_entoncesDeberiaRetornarLaEntidad() {
        UsuarioEntity usuario = this.usuarioRepository
                .findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase("kurtcobain", "hello@kurtcobain.com");
        assertThat(usuario).isNotNull();
        assertThat(usuario.getNombreUsuario()).isEqualTo("kurtcobain");
        assertThat(usuario.getContrasena()).isEqualTo("Nirvana-1987");
        assertThat(usuario.getNombre()).isEqualTo("Kurt");
        assertThat(usuario.getApellido()).isEqualTo("Cobain");
        assertThat(usuario.getCorreoElectronico()).isEqualTo("contact@kurtcobain.com");
        assertThat(usuario.getDeshabilitado()).isFalse();
        assertThat(usuario.getBloqueado()).isFalse();
        LOGGER.info("No se encontró el Usuario con Correo Electrónico 'hello@kurtcobain.com' en la base de datos, " +
                "pero sí se encontró el Nombre de Usuario 'kurtcobain'.");
    }

    @Test
    @Order(30)
    void dadoUnNombreDeUsuarioInexistenteYCorreoElectronicoExistente_cuandoSeLlameAlMetodoBuscarPorNombreUsuarioOCorreoElectronico_entoncesDeberiaRetornarLaEntidad() {
        UsuarioEntity usuario = this.usuarioRepository
                .findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase("kurt.cobain", "contact@kurtcobain.com");
        assertThat(usuario).isNotNull();
        assertThat(usuario.getNombreUsuario()).isEqualTo("kurtcobain");
        assertThat(usuario.getContrasena()).isEqualTo("Nirvana-1987");
        assertThat(usuario.getNombre()).isEqualTo("Kurt");
        assertThat(usuario.getApellido()).isEqualTo("Cobain");
        assertThat(usuario.getCorreoElectronico()).isEqualTo("contact@kurtcobain.com");
        assertThat(usuario.getDeshabilitado()).isFalse();
        assertThat(usuario.getBloqueado()).isFalse();
        LOGGER.info("No se encontró el Usuario con Nombre de Usuario 'kurt.cobain' en la base de datos, pero sí " +
                "se encontró el Correo Electrónico 'contact@kurtcobain.com'.");
    }

    @Test
    @Order(31)
    void dadoUnNombreDeUsuarioExistenteYCorreoElectronicoNulo_cuandoSeLlameAlMetodoBuscarPorNombreUsuarioOCorreoElectronico_entoncesDeberiaRetornarLaEntidad() {
        UsuarioEntity usuario = this.usuarioRepository
                .findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase("kurtcobain", null);
        assertThat(usuario).isNotNull();
        assertThat(usuario.getNombreUsuario()).isEqualTo("kurtcobain");
        assertThat(usuario.getContrasena()).isEqualTo("Nirvana-1987");
        assertThat(usuario.getNombre()).isEqualTo("Kurt");
        assertThat(usuario.getApellido()).isEqualTo("Cobain");
        assertThat(usuario.getCorreoElectronico()).isEqualTo("contact@kurtcobain.com");
        assertThat(usuario.getDeshabilitado()).isFalse();
        assertThat(usuario.getBloqueado()).isFalse();
        LOGGER.info("Aunque se pasó un Correo Electrónico nulo, se encontró el Usuario con Nombre de Usuario " +
                "'kurtcobain' en la base de datos.");
    }

    @Test
    @Order(32)
    void dadoUnNombreDeUsuarioInexistenteYCorreoElectronicoNulo_cuandoSeLlameAlMetodoBuscarPorNombreUsuarioOCorreoElectronico_entoncesDeberiaRetornarNulo() {
        UsuarioEntity usuario = this.usuarioRepository
                .findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase("kurt.cobain", null);
        assertThat(usuario).isNull();
        LOGGER.info("Se pasó un Nombre de Usuario inexistente y un Correo Electrónico nulo al método " +
                "'findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase' y se obtuvo un nulo.");
    }

    @Test
    @Order(33)
    void dadoUnNombreDeUsuarioNuloYCorreoElectronicoExistente_cuandoSeLlameAlMetodoBuscarPorNombreUsuarioOCorreoElectronico_entoncesDeberiaRetornarLaEntidad() {
        UsuarioEntity usuario = this.usuarioRepository
                .findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase(null, "contact@kurtcobain.com");
        assertThat(usuario).isNotNull();
        assertThat(usuario.getNombreUsuario()).isEqualTo("kurtcobain");
        assertThat(usuario.getContrasena()).isEqualTo("Nirvana-1987");
        assertThat(usuario.getNombre()).isEqualTo("Kurt");
        assertThat(usuario.getApellido()).isEqualTo("Cobain");
        assertThat(usuario.getCorreoElectronico()).isEqualTo("contact@kurtcobain.com");
        assertThat(usuario.getDeshabilitado()).isFalse();
        assertThat(usuario.getBloqueado()).isFalse();
        LOGGER.info("Aunque se pasó un Nombre de Usuario nulo, se encontró el Usuario con Correo Electrónico " +
                "'contact@kurtcobain.com' en la base de datos.");
    }

    @Test
    @Order(34)
    void dadoUnNombreDeUsuarioNuloYCorreoElectronicoInexistente_cuandoSeLlameAlMetodoBuscarPorNombreUsuarioOCorreoElectronico_entoncesDeberiaRetornarNulo() {
        UsuarioEntity usuario = this.usuarioRepository
                .findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase(null, "hello@kurtcobain.com");
        assertThat(usuario).isNull();
        LOGGER.info("Se pasó un Nombre de Usuario nulo y un Correo Electrónico inexistente al método " +
                "'findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase' y se obtuvo un nulo.");
    }

    @Test
    @Order(35)
    void dadoUnNombreDeUsuarioExistenteYCorreoElectronicoVacio_cuandoSeLlameAlMetodoBuscarPorNombreUsuarioOCorreoElectronico_entoncesDeberiaRetornarLaEntidad() {
        UsuarioEntity usuario = this.usuarioRepository
                .findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase("kurtcobain", " ");
        assertThat(usuario).isNotNull();
        assertThat(usuario.getNombreUsuario()).isEqualTo("kurtcobain");
        assertThat(usuario.getContrasena()).isEqualTo("Nirvana-1987");
        assertThat(usuario.getNombre()).isEqualTo("Kurt");
        assertThat(usuario.getApellido()).isEqualTo("Cobain");
        assertThat(usuario.getCorreoElectronico()).isEqualTo("contact@kurtcobain.com");
        assertThat(usuario.getDeshabilitado()).isFalse();
        assertThat(usuario.getBloqueado()).isFalse();
        LOGGER.info("Aunque se pasó un Correo Electrónico vacío, se encontró el Usuario con Nombre de Usuario " +
                "'kurtcobain' en la base de datos.");
    }

    @Test
    @Order(36)
    void dadoUnNombreDeUsuarioInexistenteYCorreoElectronicoVacio_cuandoSeLlameAlMetodoBuscarPorNombreUsuarioOCorreoElectronico_entoncesDeberiaRetornarNulo() {
        UsuarioEntity usuario = this.usuarioRepository
                .findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase("kurt.cobain", " ");
        assertThat(usuario).isNull();
        LOGGER.info("Se pasó el Nombre de Usuario inexistente 'kurt.cobain' y un Correo Electrónico vacío al método " +
                "'findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase' y se obtuvo un nulo.");
    }

    @Test
    @Order(37)
    void dadoUnNombreDeUsuarioVacioYCorreoElectronicoExistente_cuandoSeLlameAlMetodoBuscarPorNombreUsuarioOCorreoElectronico_entoncesDeberiaRetornarLaEntidad() {
        UsuarioEntity usuario = this.usuarioRepository
                .findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase(" ", "contact@kurtcobain.com");
        assertThat(usuario).isNotNull();
        assertThat(usuario.getNombreUsuario()).isEqualTo("kurtcobain");
        assertThat(usuario.getContrasena()).isEqualTo("Nirvana-1987");
        assertThat(usuario.getNombre()).isEqualTo("Kurt");
        assertThat(usuario.getApellido()).isEqualTo("Cobain");
        assertThat(usuario.getCorreoElectronico()).isEqualTo("contact@kurtcobain.com");
        assertThat(usuario.getDeshabilitado()).isFalse();
        assertThat(usuario.getBloqueado()).isFalse();
        LOGGER.info("Aunque se pasó un Nombre de Usuario vacío, se encontró el Usuario con Correo Electrónico " +
                "'contact@kurtcobain.com' en la base de datos.");
    }

    @Test
    @Order(38)
    void dadoUnNombreDeUsuarioVacioYCorreoElectronicoInexistente_cuandoSeLlameAlMetodoBuscarPorNombreUsuarioOCorreoElectronico_entoncesDeberiaRetornarNulo() {
        UsuarioEntity usuario = this.usuarioRepository
                .findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase(" ", "hi@kurtcobain.com");
        assertThat(usuario).isNull();
        LOGGER.info("Se pasó un Nombre de Usuario vacío y el Correo Electrónico 'hey@kurtcobain.com' al método " +
                "'findByNombreUsuarioIgnoreCaseOrCorreoElectronicoIgnoreCase' y se obtuvo un nulo.");
    }

    @Test
    @Order(39)
    void dadoUnObjetoUsuarioLleno_cuandoSeUsenLosGetters_entoncesDeberiaRetornarLosValoresCorrectos() {
        UsuarioEntity usuario = new UsuarioEntity("kurt.cobain", "Nirvana-1987", "Kurt",
                "Cobain", "contact@kurtcobain.com", false, false, List.of());
        assertThat(usuario).isNotNull();
        assertThat(usuario.getNombreUsuario()).isEqualTo("kurt.cobain");
        assertThat(usuario.getContrasena()).isEqualTo("Nirvana-1987");
        assertThat(usuario.getNombre()).isEqualTo("Kurt");
        assertThat(usuario.getApellido()).isEqualTo("Cobain");
        assertThat(usuario.getCorreoElectronico()).isEqualTo("contact@kurtcobain.com");
        assertThat(usuario.getDeshabilitado()).isFalse();
        assertThat(usuario.getBloqueado()).isFalse();
        LOGGER.info("Se creó un objeto Usuario con valores correctos y al usar los Getter, se obtuvo igualdad.");
    }

    @Test
    @Order(40)
    void dadoUnObjetoUsuarioVacio_cuandoSeUsenLosSetters_entoncesDeberiaRetornarLosValoresCorrectos() {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombreUsuario("kurt.cobain");
        usuario.setContrasena("Nirvana-1987");
        usuario.setNombre("Kurt");
        usuario.setApellido("Cobain");
        usuario.setCorreoElectronico("contact@kurtcobain.com");
        usuario.setDeshabilitado(false);
        usuario.setBloqueado(false);
        usuario.setRoles(List.of());
        assertThat(usuario).isNotNull();
        assertThat(usuario.getNombreUsuario()).isEqualTo("kurt.cobain");
        assertThat(usuario.getContrasena()).isEqualTo("Nirvana-1987");
        assertThat(usuario.getNombre()).isEqualTo("Kurt");
        assertThat(usuario.getApellido()).isEqualTo("Cobain");
        assertThat(usuario.getCorreoElectronico()).isEqualTo("contact@kurtcobain.com");
        assertThat(usuario.getDeshabilitado()).isFalse();
        assertThat(usuario.getBloqueado()).isFalse();
        assertThat(usuario.getRoles()).isNotNull().isEmpty();
        LOGGER.info("Se creó un objeto Usuario vacío y al usar los Setter, se obtuvo igualdad.");
    }

    @Test
    @Order(41)
    void dadoUnUsuarioConLosMismosAtributos_cuandoSeCompareConOtroUsuario_entoncesDeberiaRetornarTrue() {
        UsuarioEntity usuario1 = new UsuarioEntity("kurt.cobain", "Nirvana-1987", "Kurt",
                "Cobain", "contact@kurtcobain.com", false, false, List.of());
        UsuarioEntity usuario2 = new UsuarioEntity("kurt.cobain", "Nirvana-1987", "Kurt",
                "Cobain", "contact@kurtcobain.com", false, false, List.of());
        boolean sonIguales = usuario1.equals(usuario2);
        assertThat(sonIguales).isTrue();
        LOGGER.info("Se compararon dos Usuarios con los mismos atributos y se obtuvo 'true'.");
    }

    @Test
    @Order(42)
    void dadoUnUsuarioConLosDiferentesAtributos_cuandoSeCompareConOtroUsuario_entoncesDeberiaRetornarFalse() {
        UsuarioEntity usuario1 = new UsuarioEntity("kurt.cobain", "Nirvana-1987", "Kurt",
                "Cobain", "contact@kurtcobain.com", false, false, List.of());
        UsuarioEntity usuario2 = new UsuarioEntity("krist.novoselic", "Nirvana-1994", "Krist",
                "Novoselic", "contact@kristnovoselic.com", true, true, null);
        boolean sonIguales = usuario1.equals(usuario2);
        assertThat(sonIguales).isFalse();
        LOGGER.info("Se compararon dos Usuarios con diferentes atributos y se obtuvo 'false'.");
    }

    @Test
    @Order(43)
    void dadoElHashCodeDeUnUsuario_cuandoSeCompareConElHashCodeDeOtroUsuario_entoncesDeberiaRetornarTrue() {
        UsuarioEntity usuario1 = new UsuarioEntity("kurt.cobain", "Nirvana-1987", "Kurt",
                "Cobain", "contact@kurtcobain.com", false, false, List.of());
        UsuarioEntity usuario2 = new UsuarioEntity("kurt.cobain", "Nirvana-1987", "Kurt",
                "Cobain", "contact@kurtcobain.com", false, false, List.of());
        assertThat(usuario1.hashCode()).isEqualTo(usuario2.hashCode());
        LOGGER.info("Se compararon los HashCodes de dos Usuarios con los mismos atributos y se obtuvo 'true'.");
    }

    @Test
    @Order(44)
    void dadoElHashCodeDeUnUsuarioProvenienteDeLaBaseDeDatos_cuandoSeCompareConElHashCodeDeOtroUsuario_entoncesDeberiaRetornarTrue() {
        UsuarioEntity usuarioEncontrado = this.usuarioRepository.findById("kurtcobain").orElse(null);
        UsuarioEntity usuarioCreado = new UsuarioEntity("kurtcobain", "Nirvana-1987", "Kurt",
                "Cobain", "contact@kurtcobain.com", false, false, List.of());
        assert usuarioEncontrado != null;
        assertThat(usuarioEncontrado.hashCode()).isEqualTo(usuarioCreado.hashCode());
        LOGGER.info("Se compararon los HashCodes de dos Usuarios con los mismos atributos y se obtuvo 'true'.");
    }
}