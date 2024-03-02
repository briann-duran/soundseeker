package com.soundseeker.api.web.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.soundseeker.api.persistence.repository.ProductoRepository;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Testcontainers(parallel = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoriaControllerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductoRepository.class);

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

    @Autowired
    public CategoriaController categoriaController;
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
                    "id": null,
                    "nombre": "Electrófonos",
                    "imagen": "/img/cat/electrofonos.jpg",
                    "descripcion": "Descubre la magia de los electrófonos, instrumentos que combinan la elegancia de los instrumentos de cuerda con la versatilidad de los instrumentos electrónicos."
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/categorias")
                .then()
                .statusCode(201)
                .body(
                        "id", notNullValue(),
                        "id", equalTo(10),
                        "nombre", equalTo("Electrófonos"),
                        "imagen", equalTo("/img/cat/electrofonos.jpg"),
                        "descripcion", equalTo("Descubre la magia de los electrófonos, instrumentos que combinan la " +
                                "elegancia de los instrumentos de cuerda con la versatilidad de los instrumentos electrónicos.")
                );
        LOGGER.info("Se envió un JSON con datos válidos a través del método POST y quedó registrado en la base de datos.");
    }

    @Test
    @Order(3)
    void dadoUnJsonConDatosValidos_cuandoSeHagaUnaPeticionPostSinAuthorization_entoncesDeberiaRetornar403() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "Electrófonos",
                    "imagen": "/img/cat/electrofonos.jpg",
                    "descripcion": "Descubre la magia de los electrófonos, instrumentos que combinan la elegancia de los instrumentos de cuerda con la versatilidad de los instrumentos electrónicos."
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/categorias")
                .then()
                .statusCode(403);
        LOGGER.info("Se envió un JSON con datos válidos a través del método POST sin Authorization, se impidió el acceso y se retornó 403.");
    }

    @Test
    @Order(4)
    void dadoUnNombreNulo_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": null,
                    "imagen": "/img/cat/teclados.jpg",
                    "descripcion": "Sumérgete en el mundo de posibilidades que los teclados en alquiler te ofrecen."
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/categorias")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'nombre': El nombre de la categoría no puede ser nulo o estar vacío."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con nombre un nulo a través del método POST y se lanzó una Excepción, " +
                "tal como se esperaba.");
    }

    @Test
    @Order(5)
    void dadoUnNombreVacio_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": " ",
                    "imagen": "/img/cat/teclados.jpg",
                    "descripcion": "Sumérgete en el mundo de posibilidades que los teclados en alquiler te ofrecen."
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/categorias")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'nombre': El nombre de la categoría no puede ser nulo o estar vacío."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con un nombre vacío a través del método POST y se lanzó una Excepción, " +
                "tal como se esperaba.");
    }

    @Test
    @Order(6)
    void dadoUnNombreConMasDe30Caracteres_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
                    "imagen": "/img/cat/teclados.jpg",
                    "descripcion": "Sumérgete en el mundo de posibilidades que los teclados en alquiler te ofrecen."
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/categorias")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'nombre': El nombre de la categoría no puede tener más de 30 caracteres."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con un nombre con longitud de 36 caracteres a través del método POST y se " +
                "lanzó una Excepción, tal como se esperaba.");
    }

    @Test
    @Order(7)
    void dadoUnNombreRepetido_cuandoSeHagaUnaSolicitudPost_entoncesDeberiaRetornar409ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "Teclados",
                    "imagen": "/img/cat/teclados.jpg",
                    "descripcion": "Sumérgete en el mundo de posibilidades que los teclados en alquiler te ofrecen."
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/categorias")
                .then()
                .statusCode(409)
                .body(
                        "message", equalTo("El nombre de la categoría ya está registrado."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("CONFLICT")
                );
        LOGGER.info("Se envió un JSON con un nombre repetido a través del método POST y se lanzó una Excepción, " +
                "tal como se esperaba.");
    }

    @Test
    @Order(8)
    void dadoUnaUrlDeImagenNula_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "Violines/Violas",
                    "imagen": null,
                    "descripcion": "Adéntrate en la elegancia etérea de nuestros violines y violas."
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/categorias")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'imagen': La imagen de la categoría no puede ser nula o estar vacía."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con una url de imagen nula a través del método POST y se lanzó una Excepción, " +
                "tal como se esperaba.");
    }

    @Test
    @Order(9)
    void dadoUnaUrlDeImagenVacia_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "Violines/Violas",
                    "imagen": " ",
                    "descripcion": "Adéntrate en la elegancia etérea de nuestros violines y violas."
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/categorias")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'imagen': La imagen de la categoría no puede ser nula o estar vacía."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con una url de imagen vacía a través del método POST y se lanzó una Excepción, " +
                "tal como se esperaba.");
    }

    @Test
    @Order(10)
    void dadoUnaDescripcionNula_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "Violines/Violas",
                    "imagen": "/img/cat/violines-violas.jpg",
                    "descripcion": null
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/categorias")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'descripcion': La descripción de la categoría no puede ser nula o estar vacía."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con una descripción nula a través del método POST y se lanzó una Excepción, " +
                "tal como se esperaba.");
    }

    @Test
    @Order(11)
    void dadoUnaDescripcionVacia_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "Violines/Violas",
                    "imagen": "/img/cat/violines-violas.jpg",
                    "descripcion": " "
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/categorias")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'descripcion': La descripción de la categoría no puede ser nula o estar vacía."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con una descripción vacía a través del método POST y se lanzó una Excepción, " +
                "tal como se esperaba.");
    }

    @Test
    @Order(12)
    void dadoUnPayloadSinCuerpo_cuandoSeHagaUnaSolicitudPost_entoncesDeberiaRetornar400ConUnMensaje() {
        given().request().header("Authorization", ADMIN_JWT).and().body("").contentType(ContentType.JSON)
                .when().post("/api/v1/categorias")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("El cuerpo de la solicitud no pudo ser leído."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON vacío a través del método POST y se lanzó una Excepción, tal como se esperaba.");
    }

    @Test
    @Order(13)
    void dadoUnIdValido_cuandoSeHagaUnaLlamadaAlEndpointObtenerPorId_entoncesDeberiaRetornar200ConElElemento() {
        get("/api/v1/categorias/{id}", 1)
                .then()
                .statusCode(200)
                .body(
                        "id", notNullValue(),
                        "id", equalTo(1),
                        "nombre", equalTo("Guitarras y Cuerdas"),
                        "imagen", equalTo("/img/cat/guitarras-y-cuerdas.webp"),
                        "descripcion", startsWith("Explora"),
                        "descripcion", containsString("universo sonoro que te ofrecen nuestras guitarras"),
                        "descripcion", containsString("dulce susurro de una guitarra"),
                        "descripcion", containsString("tenemos el instrumento perfecto para ti."),
                        "descripcion", containsString("probar diferentes modelos y encuentra tu"),
                        "descripcion", endsWith("ideal.")
                );
        LOGGER.info("Se envió el ID '1', fue encontrado en la base de datos y retornado con código 200.");
    }

    @Test
    @Order(14)
    void dadoUnIdInvalido_cuandoSeHagaUnaLlamadaAlEndpointObtenerPorId_entoncesDeberiaRetornar404ConUnMensaje() {
        get("/api/v1/categorias/{id}", 100)
                .then()
                .statusCode(404)
                .body(
                        "message", equalTo("Recurso no encontrado"),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("NOT_FOUND")
                );
        LOGGER.info("Se envió el ID '100', no fue encontrado en la base de datos y se retornó un código 404.");
    }

    @Test
    @Order(15)
    void dadoUnTipoDeIdInvalido_cuandoSeHagaUnaLlamadaAlEndpointObtenerPorId_entoncesDeberiaRetornar400ConUnMensaje() {
        get("/api/v1/categorias/{id}", "ABC100")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("El tipo de dato recibido no puede ser convertido al requerido."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió el ID 'ABC100', que no es un tipo válido y se retornó 400.");
    }

    @Test
    @Order(16)
    void dadoElRegistroExitosoYLosDatosPrecargados_cuandoSeHagaUnaLlamadaAlEndpointObtenerTodos_entoncesDeberiaRetornar200ConUnaListaDeNueveElementos() {
        List<Map<String, Object>> categorias = RestAssured.get("/api/v1/categorias").as(new TypeRef<>() {
        });
        assertThat(categorias, hasSize(10));
        assertThat(categorias.get(9).size(), equalTo(6));
        assertThat(categorias.get(9).get("id"), equalTo(10));
        assertThat(categorias.get(9).get("nombre"), equalTo("Electrófonos"));
        assertThat(categorias.get(9).get("imagen"), equalTo("/img/cat/electrofonos.jpg"));
        assertThat(categorias.get(9).get("descripcion"), equalTo("Descubre la magia de los electrófonos, " +
                "instrumentos que combinan la elegancia de los instrumentos de cuerda con la versatilidad de los " +
                "instrumentos electrónicos."));
        assertThat(categorias.get(9).get("politicas"), equalTo(List.of()));
        LOGGER.info("La categoría que se registró antes fue encontrada en la lista de categorías.");
    }

    @Test
    @Order(17)
    void dadosLosDatosRegistrados_cuandoSeHagaUnaLlamadaAlEndpointObtenerAleatorio_entoncesDeberiaRetornar200ConUnaListaDeCincoElementosDesordenados() {
        List<Map<String, Object>> categorias = RestAssured.get("/api/v1/categorias/aleatorio").as(new TypeRef<>() {
        });
        List<Object> ids = categorias.stream().map(categoria -> categoria.get("id")).toList();
        assertThat(categorias, hasSize(5));
        assertThat(ids.toString(), not(stringContainsInOrder("1", "2", "3", "4", "5")));
        LOGGER.info("La categoría que se registró antes fue encontrada en la lista de categorías.");
    }

    @Test
    @Order(18)
    void dadoUnJsonConDatosValidos_cuandoSeHagaUnaLlamadaAlEndpointActualizar_entoncesDeberiaRetornar200ConElObjetoActualizado() {
        String solicitud = """
                {
                    "id": 1,
                    "nombre": "Idiófonos",
                    "imagen": "/img/cat/idiofonos.jpg",
                    "descripcion": "Descubre la magia de los idiófonos, instrumentos que producen sonido por la vibración de su propio material."
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().put("/api/v1/categorias")
                .then()
                .statusCode(200)
                .body(
                        "id", notNullValue(),
                        "id", equalTo(1),
                        "nombre", equalTo("Idiófonos"),
                        "imagen", equalTo("/img/cat/idiofonos.jpg"),
                        "descripcion", equalTo("Descubre la magia de los idiófonos, instrumentos que producen " +
                                "sonido por la vibración de su propio material.")
                );
        LOGGER.info("Se envió un JSON con datos válidos a través del método PUT y quedó actualizado en la base de datos.");
    }

    @Test
    @Order(19)
    void dadoUnJsonConDatosValidos_cuandoSeHagaUnaPeticionPutSinAuthorization_entoncesDeberiaRetornar403() {
        String solicitud = """
                {
                    "id": 1,
                    "nombre": "Electrófonos",
                    "imagen": "/img/cat/electrofonos.jpg",
                    "descripcion": "Descubre la magia de los electrófonos, instrumentos que combinan la elegancia de los instrumentos de cuerda con la versatilidad de los instrumentos electrónicos."
                }
                """;
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().put("/api/v1/categorias")
                .then()
                .statusCode(403);
        LOGGER.info("Se envió un JSON con datos válidos a través del método PUT sin Authorization, se impidió " +
                "el acceso y se retornó 403.");
    }

    @Test
    @Order(20)
    void dadoUnIdInvalido_cuandoSeHagaUnaLlamadaAlEndpointActualizar_entoncesDeberiaRetornar404ConUnMensaje() {
        String solicitud = """
                {
                    "id": 100,
                    "nombre": "Electrófonos",
                    "imagen": "/img/cat/electrofonos.jpg",
                    "descripcion": "Descubre la magia de los electrófonos, instrumentos que combinan la elegancia de los instrumentos de cuerda con la versatilidad de los instrumentos electrónicos."
                }
                """;
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().put("/api/v1/categorias")
                .then()
                .statusCode(404)
                .body(
                        "message", equalTo("Categoría no encontrada"),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("NOT_FOUND")
                );
        LOGGER.info("Se envió un JSON con un ID '100' a través del método PUT y se lanzó una Excepción, " +
                "tal como se esperaba.");
    }

    @Test
    @Order(20)
    void dadoUnIdValido_cuandoSeHagaUnaLlamadaAlEndpointEliminar_entoncesDeberiaRetornar200() {
        given().request().header("Authorization", ADMIN_JWT).and()
                .delete("/api/v1/categorias/{id}", 10)
                .then().statusCode(200);
        LOGGER.info("Se envió a eliminar el ID '1', fue encontrado en la base de datos, fue eliminado y se " +
                "retornó el código 200.");
    }

    @Test
    @Order(21)
    void dadoUnIdValido_cuandoSeHagaUnaPeticionDeleteSinAuthorization_entoncesDeberiaRetornar403() {
        given().request().and()
                .delete("/api/v1/categorias/{id}", 1)
                .then().statusCode(403);
        LOGGER.info("Se envió a eliminar el ID '1' sin Authorization, se impidió el acceso y se retornó el código 403.");
    }

    @Test
    @Order(22)
    void dadoUnIdInvalido_cuandoSeHagaUnaLlamadaAlEndpointEliminar_entoncesDeberiaRetornar404ConUnMensaje() {
        given().request().header("Authorization", ADMIN_JWT).and()
                .delete("/api/v1/categorias/{id}", 100)
                .then()
                .statusCode(404)
                .body(
                        "message", equalTo("Recurso no encontrado"),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("NOT_FOUND")
                );
        LOGGER.info("Se envió a eliminar el ID '100', no fue encontrado en la base de datos y se retornó un código 404.");
    }

    @Test
    @Order(23)
    void dadoUnTipoDeIdInvalido_cuandoSeHagaUnaLlamadaAlEndpointEliminar_entoncesDeberiaRetornar400ConUnMensaje() {
        given().request().header("Authorization", ADMIN_JWT).and()
                .delete("/api/v1/categorias/{id}", "ABC100")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("El tipo de dato recibido no puede ser convertido al requerido."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió a eliminar el ID 'ABC100', que no es un tipo válido y se retornó 400.");
    }
}