package com.soundseeker.api.web.controller;

import com.soundseeker.api.persistence.repository.TokenVerificacionRepository;
import com.soundseeker.api.persistence.repository.UsuarioRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;

@Testcontainers(parallel = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioControllerTest {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UsuarioControllerTest.class);

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

    @LocalServerPort
    private Integer port;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TokenVerificacionRepository tokenVerificacionRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @DynamicPropertySource
    public static void configurarPropiedades(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("spring.jpa.show-sql", () -> false);
        registry.add("logging.level.org.springframework.security.web.*", () -> "off");
        registry.add("spring.jpa.defer-datasource-initialization", () -> true);
        registry.add("spring.sql.init.mode", () -> "always");
    }

    @BeforeEach
    void configuracion() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    @Order(1)
    void deberiaAsegurarQueLaConexionFueExitosa() {
        Assertions.assertTrue(mySQLContainer.isCreated());
        Assertions.assertTrue(mySQLContainer.isRunning());
        LOGGER.info("El contenedor está creado y está corriendo.");
    }

    @Test
    @Order(2)
    void dadoUnJsonConDatosValidos_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar201YEnviarUnCorreoElectronico() {
        String solicitud = """
                {
                    "nombreUsuario": "davidgarmo",
                    "contrasena": "Hola-123A",
                    "contrasenaConfirmada": "Hola-123A",
                    "correoElectronico": "davidmont@telegmail.com",
                    "nombre": "David",
                    "apellido": "García"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat().statusCode(201);
        LOGGER.info("Se registró el Usuario exitosamente y se envió el correo electrónico correspondiente.");
    }

    @Test
    @Order(3)
    void dadoElUsuarioRegistradoPreviamente_cuandoSeConsulteElUsuarioRepository_entoncesDeberiaConcordarConLosDatosEnviados() {
        this.usuarioRepository.findByNombreUsuarioIgnoreCase("davidgarmo").ifPresentOrElse(usuario -> {
            assertThat(usuario).isNotNull();
            assertThat(usuario.getNombreUsuario()).isEqualTo("davidgarmo");
            assertThat(this.passwordEncoder.matches("Hola-123A", usuario.getContrasena())).isTrue();
            assertThat(usuario.getNombre()).isEqualTo("David");
            assertThat(usuario.getApellido()).isEqualTo("García");
            assertThat(usuario.getCorreoElectronico()).isEqualTo("davidmont@telegmail.com");
            assertThat(usuario.getDeshabilitado()).isTrue();
            assertThat(usuario.getBloqueado()).isFalse();
            assertThat(usuario.getRoles()).isNotNull();
        }, () -> Assertions.fail("No se encontró el Usuario registrado."));
        LOGGER.info("Se encontró en la base de datos al Usuario registrado en el test anterior.");
    }

    @Test
    @Order(4)
    void dadoElUsuarioRegistradoPreviamente_cuandoSeConsulteElTokenVerificacionRepository_entoncesDeberiaEncontrarLaRelacion() {
        this.tokenVerificacionRepository.findByUsuarioNombreUsuarioIgnoreCase("davidgarmo").ifPresentOrElse(token -> {
            assertThat(token).isNotNull();
            assertThat(token.getUsuario().getNombreUsuario()).isEqualTo("davidgarmo");
            assertThat(token.getToken()).isNotNull();
            assertThat(token.getFechaExpiracion()).isNotNull();
        }, () -> Assertions.fail("No se encontró el Token de Verificación registrado."));
        LOGGER.info("Se encontró en la base de datos el Token de Verificación registrado en el test anterior.");
    }

    @Test
    @Order(5)
    void dadoUnNombreDeUsuarioNulo_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": null,
                    "contrasena": "Usuario-123",
                    "contrasenaConfirmada": "Usuario-123",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sánchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("El nombre de usuario no puede ser nulo o estar vacío."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 por enviar un JSON con Nombre de Usuario nulo.");
    }

    @Test
    @Order(6)
    void dadoUnNombreDeUsuarioVacio_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "",
                    "contrasena": "Usuario-123",
                    "contrasenaConfirmada": "Usuario-123",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sánchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("El nombre de usuario no puede ser nulo o estar vacío."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 por enviar un JSON con Nombre de Usuario vacío.");
    }

    @Test
    @Order(7)
    void dadoUnNombreDeUsuarioConCaracteresEspecialesInvalidos_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela@",
                    "contrasena": "Usuario-123",
                    "contrasenaConfirmada": "Usuario-123",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sánchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("El nombre de usuario solo puede contener letras minúsculas y mayúsculas sin acentos (a-z o A-Z), números (0-9), guion (-), guion bajo (_) y punto (.); ningún caracter especial puede estar seguido de otro."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 por enviar un JSON con Nombre de Usuario con caracteres especiales.");
    }

    @Test
    @Order(8)
    void dadoUnNombreDeUsuarioConCaracteresEspecialesValidosSeguidos_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela__",
                    "contrasena": "Usuario-123",
                    "contrasenaConfirmada": "Usuario-123",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sánchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("El nombre de usuario solo puede contener letras minúsculas y mayúsculas sin acentos (a-z o A-Z), números (0-9), guion (-), guion bajo (_) y punto (.); ningún caracter especial puede estar seguido de otro."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 por enviar un JSON con Nombre de Usuario con caracteres especiales validos seguidos.");
    }

    @Test
    @Order(9)
    void dadoUnNombreDeUsuarioConMenosDe4Caracteres_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "mar",
                    "contrasena": "Usuario-123",
                    "contrasenaConfirmada": "Usuario-123",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sánchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("El nombre de usuario debe tener mínimo 4 caracteres y máximo 20."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 por enviar un JSON con Nombre de Usuario con menos de 4 caracteres.");
    }

    @Test
    @Order(10)
    void dadoUnNombreDeUsuarioConMasDe20Caracteres_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez.la.mejor.del.mundo.mundial",
                    "contrasena": "Usuario-123",
                    "contrasenaConfirmada": "Usuario-123",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sánchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("El nombre de usuario debe tener mínimo 4 caracteres y máximo 20."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 por enviar un JSON con Nombre de Usuario con más de 20 caracteres.");
    }

    @Test
    @Order(11)
    void dadoUnNombreDeUsuarioExistente_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar409() {
        String solicitud = """
                {
                    "nombreUsuario": "davidgarmo",
                    "contrasena": "Usuario-123",
                    "contrasenaConfirmada": "Usuario-123",
                    "correoElectronico": "david@telegmail.com",
                    "nombre": "David",
                    "apellido": "García"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(409)
                .and()
                .body(
                        "message", containsString("El nombre de usuario ya se encuentra registrado."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("CONFLICT")
                );
        LOGGER.info("Se retornó el código de estado 409 por enviar un JSON con Nombre de Usuario existente.");
    }

    @Test
    @Order(12)
    void dadoUnNombreDeUsuarioExistenteConDiferenteCapitalizacion_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar409() {
        String solicitud = """
                {
                    "nombreUsuario": "DavidGarMo",
                    "contrasena": "Usuario-123",
                    "contrasenaConfirmada": "Usuario-123",
                    "correoElectronico": "david@telegmail.com",
                    "nombre": "David",
                    "apellido": "García"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(409)
                .and()
                .body(
                        "message", containsString("El nombre de usuario ya se encuentra registrado."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("CONFLICT")
                );
        LOGGER.info("Se retornó el código de estado 409 por enviar un JSON con Nombre de Usuario existente con diferente capitalización.");
    }

    @Test
    @Order(13)
    void dadaUnaContrasenaNula_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": null,
                    "contrasenaConfirmada": "Usuario-123",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sánchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("La contraseña no puede ser nula o estar vacía."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 por enviar una Contraseña nula.");
    }

    @Test
    @Order(14)
    void dadaUnaContrasenaVacia_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "",
                    "contrasenaConfirmada": "Usuario-123",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sánchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("La contraseña no puede ser nula o estar vacía."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 por enviar una Contraseña vacía.");
    }

    @Test
    @Order(15)
    void dadaUnaContrasenaConMenosDe8Caracteres_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-12",
                    "contrasenaConfirmada": "Hola-12",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sánchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body("message", containsString("La contraseña debe tener mínimo 8 caracteres."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 por enviar una Contraseña con menos de 8 caracteres.");
    }

    @Test
    @Order(16)
    void dadaUnaContrasenaConMasDe60Caracteres_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890",
                    "contrasenaConfirmada": "Hola-123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sánchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body("message", containsString("La contraseña debe tener mínimo 8 caracteres."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 por enviar una Contraseña con más de 60 caracteres.");
    }

    @Test
    @Order(17)
    void dadaUnaContrasenaSinLetraMayuscula_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "hola-1234",
                    "contrasenaConfirmada": "hola-1234",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sanchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("La contraseña no cumple con los requisitos de seguridad, esta debe contener mínimo un dígito, una letra mayúscula, una letra minúscula y un caracter especial."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 por no enviar una Contraseña con al menos una letra mayúscula.");
    }

    @Test
    @Order(18)
    void dadaUnaContrasenaSinLetraMinuscula_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "HOLA-1234",
                    "contrasenaConfirmada": "HOLA-1234",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sanchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("La contraseña no cumple con los requisitos de seguridad, esta debe contener mínimo un dígito, una letra mayúscula, una letra minúscula y un caracter especial."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 por no enviar una Contraseña con al menos una letra minúscula.");
    }

    @Test
    @Order(19)
    void dadaUnaContrasenaSinDigito_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Como-Vas",
                    "contrasenaConfirmada": "Como-Vas",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sanchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("La contraseña no cumple con los requisitos de seguridad, esta debe contener mínimo un dígito, una letra mayúscula, una letra minúscula y un caracter especial."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 por no enviar una Contraseña con al menos un dígito.");
    }

    @Test
    @Order(20)
    void dadaUnaContrasenaSinCaracterEspecial_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola1234",
                    "contrasenaConfirmada": "Hola1234",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sanchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("La contraseña no cumple con los requisitos de seguridad, esta debe contener mínimo un dígito, una letra mayúscula, una letra minúscula y un caracter especial."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 por no enviar una Contraseña con al menos un caracter especial.");
    }

    @Test
    @Order(21)
    void dadaUnaContrasenaConEspaciosEnBlanco_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-1234 ",
                    "contrasenaConfirmada": "Hola-1234 ",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sanchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios").then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("La contraseña no cumple con los requisitos de seguridad, esta debe contener mínimo un dígito, una letra mayúscula, una letra minúscula y un caracter especial."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 por enviar una Contraseña con espacios en blanco.");
    }

    @Test
    @Order(22)
    void dadaUnaContrasenaConfirmadaNula_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": null,
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sanchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios").then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("La contraseña repetida no puede ser nula o estar vacía."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 al enviar una Contraseña Confirmada nula.");
    }

    @Test
    @Order(23)
    void dadaUnaContrasenaConfirmadaVacia_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": "",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sanchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("La contraseña repetida no puede ser nula o estar vacía."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 al enviar una Contraseña Confirmada vacía.");
    }

    @Test
    @Order(24)
    void dadaUnaContrasenaConfirmadaDiferente_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": "5678-Adiós",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sanchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("La contraseñas ingresadas no coinciden."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 al enviar una Contraseña Confirmada diferente.");
    }

    @Test
    @Order(25)
    void dadoUnNombreNulo_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": "Hola-1234",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": null,
                    "apellido": "Sanchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("El nombre no puede ser nulo o estar vacío."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 al enviar un Nombre nulo.");
    }

    @Test
    @Order(26)
    void dadoUnNombreVacio_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": "Hola-1234",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "",
                    "apellido": "Sanchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("El nombre no puede ser nulo o estar vacío."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 al enviar un Nombre vacío.");
    }

    @Test
    @Order(27)
    void dadoUnNombreConMenosDe2Caracteres_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": "Hola-1234",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "M",
                    "apellido": "Sanchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("El nombre debe tener mínimo 2 caracteres y máximo 30."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 al enviar un Nombre con menos de 2 caracteres.");
    }

    @Test
    @Order(28)
    void dadoUnNombreConMasDe30Caracteres_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": "Hola-1234",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela Patricia de los Ángeles",
                    "apellido": "Sanchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("El nombre debe tener mínimo 2 caracteres y máximo 30."),

                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 al enviar un Nombre con más de 30 caracteres.");
    }

    @Test
    @Order(29)
    void dadoUnNombreConCaracteresEspecialesInvalidos_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": "Hola-1234",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "M4rcela",
                    "apellido": "Sanchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("Tu nombre solamente puede contener caracteres latinos, apóstrofo ('), guion (-) y espacios."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 al enviar un Nombre con caracteres especiales inválidos.");
    }

    @Test
    @Order(30)
    void dadoUnApellidoNulo_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": "Hola-1234",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": null
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("El apellido no puede ser nulo o estar vacío."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 al enviar un Apellido nulo.");
    }

    @Test
    @Order(31)
    void dadoUnApellidoVacio_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": "Hola-1234",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": ""
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("El apellido no puede ser nulo o estar vacío."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 al enviar un Apellido vacío.");
    }

    @Test
    @Order(32)
    void dadoUnApellidoConMenosDe2Caracteres_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": "Hola-1234",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "S"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("El apellido debe tener mínimo 2 caracteres y máximo 30."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 al enviar un Apellido con menos de 2 caracteres.");
    }

    @Test
    @Order(33)
    void dadoUnApellidoConMasDe30Caracteres_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": "Hola-1234",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sanchez de la Cruz de la Sierra"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("El apellido debe tener mínimo 2 caracteres y máximo 30."),

                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 al enviar un Apellido con más de 30 caracteres.");
    }

    @Test
    @Order(34)
    void dadoUnApellidoConCaracteresEspecialesInvalidos_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": "Hola-1234",
                    "correoElectronico": "marcela@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "S4nchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("Tu apellido solamente puede contener caracteres latinos, apóstrofo ('), guion (-) y espacios."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 al enviar un Apellido con caracteres especiales inválidos.");
    }

    @Test
    @Order(35)
    void dadoUnCorreoElectronicoNulo_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": "Hola-1234",
                    "correoElectronico": null,
                    "nombre": "Marcela",
                    "apellido": "Sanchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("El correo electrónico no puede ser nulo o estar vacío."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 al enviar un Correo Electrónico nulo.");
    }

    @Test
    @Order(36)
    void dadoUnCorreoElectronicoVacio_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": "Hola-1234",
                    "correoElectronico": "",
                    "nombre": "Marcela",
                    "apellido": "Sanchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("El correo electrónico no puede ser nulo o estar vacío."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 al enviar un Correo Electrónico vacío.");
    }

    @Test
    @Order(37)
    void dadoUnCorreoElectronicoConNombreDeUsuarioConCaracteresEspecialesInvalidos_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": "Hola-1234",
                    "correoElectronico": "marcela.$anchez@telegmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sanchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("El correo electrónico no es válido."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 al enviar un Correo Electrónico con Nombre de Usuario con caracteres especiales inválidos.");
    }

    @Test
    @Order(38)
    void dadoUnCorreoElectronicoConDominioConCaracteresEspecialesInvalidos_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": "Hola-1234",
                    "correoElectronico": "marcela@tele%gmail.com",
                    "nombre": "Marcela",
                    "apellido": "Sanchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("El correo electrónico no es válido."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 al enviar un Correo Electrónico con Dominio con caracteres especiales inválidos.");
    }

    @Test
    @Order(39)
    void dadoUnCorreoElectronicoSinTLD_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400() {
        String solicitud = """
                {
                    "nombreUsuario": "marcela.sanchez",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": "Hola-1234",
                    "correoElectronico": "marcela@telegmail",
                    "nombre": "Marcela",
                    "apellido": "Sanchez"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body(
                        "message", containsString("El correo electrónico no es válido."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se retornó el código de estado 400 al enviar un Correo Electrónico sin Top Level Domain (.com).");
    }

    @Test
    @Order(40)
    void dadoUnCorreoElectronicoExistente_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar409() {
        String solicitud = """
                {
                    "nombreUsuario": "davidmont",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": "Hola-1234",
                    "correoElectronico": "davidmont@telegmail.com",
                    "nombre": "David",
                    "apellido": "Montenegro"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(409)
                .and()
                .body(
                        "message", containsString("El correo electrónico ya se encuentra registrado."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("CONFLICT")
                );
        LOGGER.info("Se retornó el código de estado 409 al enviar un Correo Electrónico existente.");
    }

    @Test
    @Order(41)
    void dadoUnCorreoElectronicoExistenteConDiferenteCapitalizacion_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar409() {
        String solicitud = """
                {
                    "nombreUsuario": "davidmonte",
                    "contrasena": "Hola-1234",
                    "contrasenaConfirmada": "Hola-1234",
                    "correoElectronico": "DavidMont@TeleGmail.Com",
                    "nombre": "David",
                    "apellido": "Montenegro"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/usuarios")
                .then()
                .assertThat()
                .statusCode(409)
                .and()
                .body(
                        "message", containsString("El correo electrónico ya se encuentra registrado."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("CONFLICT")
                );
        LOGGER.info("Se retornó el código de estado 409 al enviar un Correo Electrónico existente con diferente capitalización.");
    }
}