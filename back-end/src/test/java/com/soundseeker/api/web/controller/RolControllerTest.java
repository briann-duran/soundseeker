package com.soundseeker.api.web.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Testcontainers(parallel = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RolControllerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RolControllerTest.class);

    private static final String ADMIN_JWT = "Bearer " + JWT.create()
            .withIssuer("soundseeker")
            .withSubject("admin")
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30)))
            .sign(Algorithm.HMAC256("$0und%$e3k3r|4ppl1c4ti0n"));

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
    void dadoUnJsonConDatosValidos_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar201YElObjetoConIdAsignado() {
        String solicitud = """
                {
                    "rol": "CAJERO"
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/roles")
                .then()
                .statusCode(201)
                .body(
                        "id", notNullValue(),
                        "id", equalTo(3),
                        "rol", equalTo("ROLE_CAJERO")
                );
        LOGGER.info("Se envió un JSON con datos válidos a través del método POST y quedó registrado en la base de datos.");
    }

    @Test
    @Order(3)
    void dadoUnJsonConDatosValidos_cuandoSeHagaUnaPeticionPostSinAuthorization_entoncesDeberiaRetornar403EImpedirElAcceso() {
        String solicitud = """
                {
                    "rol": "USER"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/roles")
                .then()
                .statusCode(403);
        LOGGER.info("Se envió un JSON con datos válidos a través del método POST sin Authorization, se impidió el " +
                "acceso, se retornó 403 y no se registró en la base de datos.");
    }

    @Test
    @Order(4)
    void dadoUnNombreDeRolNulo_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400YUnaExcepcion() {
        String solicitud = """
                {
                    "rol": null
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/roles")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'rol': El nombre del rol no puede ser nulo o estar vacío."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con un nombre de Rol nulo a través del método POST, se retornó 400 y no se " +
                "registró en la base de datos.");
    }

    @Test
    @Order(5)
    void dadoUnNombreDeRolVacio_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400YUnaExcepcion() {
        String solicitud = """
                {
                    "rol": "       "
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/roles")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'rol': El nombre del rol no puede ser nulo o estar vacío."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con un nombre de Rol vacío a través del método POST, se retornó 400 y no se " +
                "registró en la base de datos.");
    }

    @Test
    @Order(6)
    void dadoUnNombreDeRolConLongitudMenorA6Caracteres_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400YUnaExcepcion() {
        String solicitud = """
                {
                    "rol": "A"
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/roles")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'rol': El nombre del rol debe tener mínimo 6 caracteres y máximo 30."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con un nombre de Rol con longitud menor a 6 caracteres a través del método " +
                "POST, se retornó 400 y no se registró en la base de datos.");
    }

    @Test
    @Order(7)
    void dadoUnNombreDeRolConLongitudMayorA30Caracteres_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400YUnaExcepcion() {
        String solicitud = """
                {
                    "rol": "ADMINISTRADOR DE LA APLICACIÓN UNIVERSAL"
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/roles")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'rol': El nombre del rol debe tener mínimo 6 caracteres y máximo 30."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con un nombre de Rol con longitud mayor a 30 caracteres a través del método " +
                "POST, se retornó 400 y no se registró en la base de datos.");
    }

    @Test
    @Order(8)
    void dadoUnNombreDeRolConEspaciosAlInicioYAlFinal_cuandoSeHagaUnaPeticionPost_entoncesDeberiaLimpiarloRetornar201YElObjetoConIdAsignado() {
        String solicitud = """
                {
                    "rol": " REPARTIDOR "
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/roles")
                .then()
                .statusCode(201)
                .body(
                        "id", notNullValue(),
                        "id", equalTo(4),
                        "rol", equalTo("ROLE_REPARTIDOR")
                );
        LOGGER.info("Se envió un JSON con un nombre de Rol con espacios al inicio y al final a través del método " +
                "POST, se estandarizó y quedó registrado en la base de datos.");
    }

    @Test
    @Order(9)
    void dadoUnNombreDeRolExistente_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400YUnaExcepcion() {
        String solicitud = """
                {
                    "rol": "ADMIN "
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/roles")
                .then()
                .statusCode(409)
                .body(
                        "message", equalTo("El rol ya está registrado."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("CONFLICT")
                );
        LOGGER.info("Se envió un JSON con un nombre de Rol existente a través del método POST, se retornó 400 y no " +
                "se registró en la base de datos.");
    }

    @Test
    @Order(10)
    void dadoUnPayloadSinCuerpo_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400YUnaExcepcion() {
        given().request().header("Authorization", ADMIN_JWT).and().body("").contentType(ContentType.JSON)
                .when().post("/api/v1/roles")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("El cuerpo de la solicitud no pudo ser leído."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un payload sin cuerpo a través del método POST, se retornó 400 y no se registró en " +
                "la base de datos.");
    }

    @Test
    @Order(11)
    void dadoUnIdExistente_cuandoSeHagaUnaPeticionGet_entoncesDeberiaRetornar200YElObjetoConIdAsignado() {
        given().request().header("Authorization", ADMIN_JWT)
                .when().get("/api/v1/roles/{id}", 1)
                .then()
                .statusCode(200)
                .body(
                        "id", notNullValue(),
                        "id", equalTo(1),
                        "rol", equalTo("ROLE_ADMIN")
                );
        LOGGER.info("Se envió un ID existente a través del método GET y se retornó el objeto con ID '1'.");
    }

    @Test
    @Order(12)
    void dadoUnIdExistente_cuandoSeHagaUnaPeticionGetSinAuthorization_entoncesDeberiaRetornar403EImpedirElAcceso() {
        given().request()
                .when().get("/api/v1/roles/{id}", 1)
                .then()
                .statusCode(403);
        LOGGER.info("Se envió un ID existente a través del método GET sin Authorization, se impidió el acceso, " +
                "se retornó 403 y no se obtuvo el objeto con ID '1'.");
    }

    @Test
    @Order(13)
    void dadoUnIdInexistente_cuandoSeHagaUnaPeticionGet_entoncesDeberiaRetornar404YUnaExcepcion() {
        given().request().header("Authorization", ADMIN_JWT)
                .when().get("/api/v1/roles/{id}", 100)
                .then()
                .statusCode(404)
                .body(
                        "message", equalTo("Recurso no encontrado"),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("NOT_FOUND")
                );
        LOGGER.info("Se envió un ID inexistente a través del método GET y se retornó 404.");
    }

    @Test
    @Order(14)
    void dadoUnTipoDeIdNoNumerico_cuandoSeHagaUnaPeticionGet_entoncesDeberiaRetornar400YUnaExcepcion() {
        given().request().header("Authorization", ADMIN_JWT)
                .when().get("/api/v1/roles/{id}", "letras")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("El tipo de dato recibido no puede ser convertido al requerido."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un tipo de ID no numérico a través del método GET y se retornó 400.");
    }

    @Test
    @Order(15)
    void dadoElRegistroExitoso_cuandoSeHagaUnaPeticionGet_entoncesDeberiaRetornar200YUnaListaCon2Elementos() {
        List<Map<String, Object>> roles = given().request().header("Authorization", ADMIN_JWT)
                .when().get("/api/v1/roles").as(new TypeRef<>() {
                });
        roles.sort(Comparator.comparing(o -> ((Integer) o.get("id"))));
        assertThat(roles, hasSize(4));
        assertThat(roles.get(0).get("id"), equalTo(1));
        assertThat(roles.get(0).get("rol"), equalTo("ROLE_ADMIN"));
        assertThat(roles.get(1).get("id"), equalTo(2));
        assertThat(roles.get(1).get("rol"), equalTo("ROLE_CLIENTE"));
        assertThat(roles.get(2).get("id"), equalTo(3));
        assertThat(roles.get(2).get("rol"), equalTo("ROLE_CAJERO"));
        assertThat(roles.get(3).get("id"), equalTo(4));
        assertThat(roles.get(3).get("rol"), equalTo("ROLE_REPARTIDOR"));
        LOGGER.info("Se envió una petición GET y se retornó una lista con los objetos registrados.");
    }

    @Test
    @Order(16)
    void dadoUnJsonConDatosValidos_cuandoSeHagaUnaPeticionPut_entoncesDeberiaRetornar200YElObjetoActualizado() {
        String solicitud = """
                {
                    "id": 3,
                    "rol": "CONTADOR"
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().put("/api/v1/roles")
                .then()
                .statusCode(200)
                .body(
                        "id", notNullValue(),
                        "id", equalTo(3),
                        "rol", equalTo("ROLE_CONTADOR")
                );
        LOGGER.info("Se envió un JSON con datos válidos a través del método PUT y se actualizó el objeto con ID '1'.");
    }

    @Test
    @Order(17)
    void dadoUnJsonConDatosValidos_cuandoSeHagaUnaPeticionPutSinAuthorization_entoncesDeberiaRetornar403EImpedirElAcceso() {
        String solicitud = """
                {
                    "id": 3,
                    "rol": "CONTADOR"
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().put("/api/v1/roles")
                .then()
                .statusCode(403);
        LOGGER.info("Se envió un JSON con datos válidos a través del método PUT sin Authorization, se impidió el " +
                "acceso, se retornó 403 y no se actualizó el objeto con ID '1'.");
    }

    @Test
    @Order(18)
    void dadoUnJsonSinIdDeRol_cuandoSeHagaUnaPeticionPut_entoncesDeberiaRetornar400YUnaExcepcion() {
        String solicitud = """
                {
                    "rol": "USUARIO"
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().put("/api/v1/roles")
                .then()
                .statusCode(403)
                .body(
                        "message", equalTo("El ID del rol no puede ser nulo."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("FORBIDDEN")
                );
        LOGGER.info("Se envió un JSON con datos válidos a través del método PUT sin ID, se retornó 400 y no se " +
                "actualizó el objeto con ID '1'.");
    }

    @Test
    @Order(19)
    void dadoUnJsonConIdNulo_cuandoSeHagaUnaPeticionPut_entoncesDeberiaRetornar400YUnaExcepcion() {
        String solicitud = """
                {
                    "id": null,
                    "rol": "USUARIO"
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().put("/api/v1/roles")
                .then()
                .statusCode(403)
                .body(
                        "message", equalTo("El ID del rol no puede ser nulo."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("FORBIDDEN")
                );
        LOGGER.info("Se envió un JSON con datos válidos a través del método PUT con ID nulo, se retornó 400 y no " +
                "se actualizó el objeto con ID '1'.");
    }

    @Test
    @Order(20)
    void dadoUnJsonSinNombreDeRol_cuandoSeHagaUnaPeticionPut_entoncesDeberiaRetornar400YUnaExcepcion() {
        String solicitud = """
                {
                    "id": 1
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().put("/api/v1/roles")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'rol': El nombre del rol no puede ser nulo o estar vacío."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON sin nombre de Rol a través del método PUT, se retornó 400 y no se actualizó " +
                "el objeto con ID '1'.");
    }

    @Test
    @Order(21)
    void dadoUnJsonConNombreDeRolNulo_cuandoSeHagaUnaPeticionPut_entoncesDeberiaRetornar400YUnaExcepcion() {
        String solicitud = """
                {
                    "id": 1,
                    "rol": null
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().put("/api/v1/roles")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'rol': El nombre del rol no puede ser nulo o estar vacío."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con nombre de Rol nulo a través del método PUT, se retornó 400 y no se " +
                "actualizó el objeto con ID '1'.");
    }

    @Test
    @Order(22)
    void dadoUnJsonConNombreDeRolVacio_cuandoSeHagaUnaPeticionPut_entoncesDeberiaRetornar400YUnaExcepcion() {
        String solicitud = """
                {
                    "id": 1,
                    "rol": "       "
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().put("/api/v1/roles")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'rol': El nombre del rol no puede ser nulo o estar vacío."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con nombre de Rol vacío a través del método PUT, se retornó 400 y no se " +
                "actualizó el objeto con ID '1'.");
    }

    @Test
    @Order(23)
    void dadoUnJsonConNombreDeRolConLongitudMenorA6Caracteres_cuandoSeHagaUnaPeticionPut_entoncesDeberiaRetornar400YUnaExcepcion() {
        String solicitud = """
                {
                    "id": 1,
                    "rol": "A"
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().put("/api/v1/roles")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'rol': El nombre del rol debe tener mínimo 6 caracteres y máximo 30."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con nombre de Rol con longitud menor a 6 caracteres a través del método PUT, " +
                "se retornó 400 y no se actualizó el objeto con ID '1'.");
    }

    @Test
    @Order(24)
    void dadoUnJsonConNombreDeRolConLongitudMayorA30Caracteres_cuandoSeHagaUnaPeticionPut_entoncesDeberiaRetornar400YUnaExcepcion() {
        String solicitud = """
                {
                    "id": 1,
                    "rol": "ADMINISTRADOR DE LA APLICACIÓN UNIVERSAL"
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().put("/api/v1/roles")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'rol': El nombre del rol debe tener mínimo 6 caracteres y máximo 30."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con nombre de Rol con longitud mayor a 30 caracteres a través del método PUT, " +
                "se retornó 400 y no se actualizó el objeto con ID '1'.");
    }

    @Test
    @Order(25)
    void dadoUnJsonConNombreDeRolExistente_cuandoSeHagaUnaPeticionPut_entoncesDeberiaRetornar400YUnaExcepcion() {
        String solicitud = """
                {
                    "id": 4,
                    "rol": "REPARTIDOR"
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().put("/api/v1/roles")
                .then()
                .statusCode(409)
                .body(
                        "message", equalTo("El rol ya está registrado."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("CONFLICT")
                );
        LOGGER.info("Se envió un JSON con nombre de Rol existente a través del método PUT, se retornó 400 y no se " +
                "actualizó el objeto con ID '1'.");
    }

    @Test
    @Order(26)
    void dadoUnJsonConIdInexistente_cuandoSeHagaUnaPeticionPut_entoncesDeberiaRetornar404YUnaExcepcion() {
        String solicitud = """
                {
                    "id": 100,
                    "rol": "ADMINISTRADOR"
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().put("/api/v1/roles")
                .then()
                .statusCode(404)
                .body(
                        "message", equalTo("Recurso no encontrado"),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("NOT_FOUND")
                );
        LOGGER.info("Se envió un JSON con ID inexistente a través del método PUT, se retornó 404 y no se actualizó " +
                "el objeto con ID '100'.");
    }

    @Test
    @Order(27)
    void dadoUnIdExistente_cuandoSeHagaUnaPeticionDelete_entoncesDeberiaRetornar200YEliminarElObjeto() {
        given().request().header("Authorization", ADMIN_JWT)
                .when().delete("/api/v1/roles/{id}", 3)
                .then()
                .statusCode(200);
        LOGGER.info("Se envió un ID existente a través del método DELETE y se eliminó el objeto con ID '1'.");
    }

    @Test
    @Order(28)
    void dadoUnIdExistente_cuandoSeHagaUnaPeticionDeleteSinAuthorization_entoncesDeberiaRetornar403EImpedirElAcceso() {
        given().request()
                .when().delete("/api/v1/roles/{id}", 2)
                .then()
                .statusCode(403);
        LOGGER.info("Se envió un ID existente a través del método DELETE sin Authorization, se impidió el acceso, " +
                "se retornó 403 y no se eliminó el objeto con ID '2'.");
    }

    @Test
    @Order(29)
    void dadoUnIdInexistente_cuandoSeHagaUnaPeticionDelete_entoncesDeberiaRetornar404YUnaExcepcion() {
        given().request().header("Authorization", ADMIN_JWT)
                .when().delete("/api/v1/roles/{id}", 100)
                .then()
                .statusCode(404)
                .body(
                        "message", equalTo("Recurso no encontrado"),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("NOT_FOUND")
                );
        LOGGER.info("Se envió un ID inexistente a través del método DELETE y se retornó 404.");
    }

    @Test
    @Order(30)
    void dadoUnTipoDeIdNoNumerico_cuandoSeHagaUnaPeticionDelete_entoncesDeberiaRetornar400YUnaExcepcion() {
        given().request().header("Authorization", ADMIN_JWT)
                .when().delete("/api/v1/roles/{id}", "letras")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("El tipo de dato recibido no puede ser convertido al requerido."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un tipo de ID no numérico a través del método DELETE y se retornó 400.");
    }
}