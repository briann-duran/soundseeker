package com.soundseeker.api.web.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
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

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Testcontainers(parallel = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductoControllerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductoControllerTest.class);

    private static final String ADMIN_JWT = "Bearer " + JWT.create()
            .withIssuer("soundseeker")
            .withSubject("admin")
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30)))
            .sign(Algorithm.HMAC256("$0und%$e3k3r|4ppl1c4ti0n"));

    private static final String DESCRIPCION_PRODUCTO = "Tomamos el 000X1AE y lo reinventamos. El resultado es el 000-X2E. " +
            "Este modelo del tamaño de un auditorio tiene una tapa de abeto y un fondo y aros de laminado de alta " +
            "presión (HPL) con patrón de caoba.";

    private static final String DESCRIPCION_CATEGORIA = "Explora el universo sonoro que te ofrecen nuestras guitarras y " +
            "cuerdas en alquiler. Ya sea que anheles el dulce susurro de una guitarra acústica o la potencia " +
            "estruendosa de un bajo eléctrico, tenemos el instrumento perfecto para ti. Sumérgete en la variedad, " +
            "siente la emoción de probar diferentes modelos y encuentra tu compañero musical ideal.";

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
    public ProductoController productoController;
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
                    "nombre": "Guitarra Electroacústica Martin Fishman Sonitone 000X2E-001",
                    "descripcion": "%s",
                    "marca": "Martin Fishman",
                    "precio": 1999.99,
                    "imagenes": [
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-1.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-2.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-3.jpg"
                    ],
                    "disponible": true,
                    "categoria": {
                        "id": 1,
                        "nombre": "Guitarras y Cuerdas",
                        "imagen": "/img/cat/guitarras-y-cuerdas.jpg",
                        "descripcion": "%s"
                    },
                    "caracteristicas": [
                        {
                            "id": 1
                        },
                        {
                            "id": 10
                        },
                        {
                            "id": 15
                        },
                        {
                            "id": 25
                        },
                        {
                            "id": 27
                        }
                    ]
                }
                """.formatted(DESCRIPCION_PRODUCTO, DESCRIPCION_CATEGORIA);
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/instrumentos")
                .then()
                .statusCode(201)
                .body(
                        "id", notNullValue(),
                        "id", equalTo(97),
                        "nombre", equalTo("Guitarra Electroacústica Martin Fishman Sonitone 000X2E-001"),
                        "descripcion", startsWithIgnoringCase("Tomamos"),
                        "descripcion", containsString("El resultado es el 000-X2E."),
                        "descripcion", containsString("Este modelo del tamaño de un auditorio tiene una tapa de abeto"),
                        "descripcion", containsString("aros de laminado de alta presión (HPL)"),
                        "descripcion", endsWithIgnoringCase("caoba."),
                        "marca", equalTo("Martin Fishman"),
                        "precio", equalTo(1999.99F),
                        "disponible", equalTo(true),
                        "categoria.id", equalTo(1),
                        "categoria.nombre", equalTo("Guitarras y Cuerdas"),
                        "categoria.imagen", equalTo("/img/cat/guitarras-y-cuerdas.jpg"),
                        "categoria.descripcion", startsWith("Explora"),
                        "categoria.descripcion", containsString("Ya sea que anheles el dulce susurro de una guitarra"),
                        "categoria.descripcion", containsString("tenemos el instrumento perfecto para ti."),
                        "categoria.descripcion", containsString("probar diferentes modelos"),
                        "categoria.descripcion", endsWith("ideal."),
                        "caracteristicas", hasSize(5),
                        "caracteristicas.id", hasItems(1, 10, 15, 25, 27)

                );
        LOGGER.info("Se envió un JSON con datos válidos a través del método POST y quedó registrado en la base de datos.");
    }

    @Test
    @Order(3)
    void dadoUnJsonConDatosValidos_cuandoSeHagaUnaPeticionPostSinAuthorization_entoncesDeberiaRetornar403EImpedirElAcceso() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "Guitarra Electroacústica Martin Fishman Sonitone 000X2E-001",
                    "descripcion": "%s",
                    "marca": "Martin Fishman",
                    "precio": 1999.99,
                    "imagenes": [
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-1.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-2.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-3.jpg"
                    ],
                    "disponible": true,
                    "categoria": {
                        "id": 1,
                        "nombre": "Guitarras y Cuerdas",
                        "imagen": "/img/cat/guitarras-y-cuerdas.jpg",
                        "descripcion": "%s"
                    },
                    "caracteristicas": [
                        {
                            "id": 1
                        },
                        {
                            "id": 10
                        },
                        {
                            "id": 15
                        },
                        {
                            "id": 25
                        },
                        {
                            "id": 27
                        }
                    ]
                }
                """.formatted(DESCRIPCION_PRODUCTO, DESCRIPCION_CATEGORIA);
        given().request().and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/instrumentos")
                .then()
                .statusCode(403);
        LOGGER.info("Se envió un JSON con datos válidos a través del método POST sin Authorization, se impidió el " +
                "acceso y se retornó 403.");
    }

    @Test
    @Order(4)
    void dadoUnNombreNulo_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": null,
                    "descripcion": "%s",
                    "marca": "Martin Fishman",
                    "precio": 1999.99,
                    "imagenes": [
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-1.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-2.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-3.jpg"
                    ],
                    "disponible": true,
                    "categoria": {
                        "id": 1,
                        "nombre": "Guitarras y Cuerdas",
                        "imagen": "/img/cat/guitarras-y-cuerdas.jpg",
                        "descripcion": "%s"
                    },
                    "caracteristicas": [
                        {
                            "id": 1
                        },
                        {
                            "id": 10
                        },
                        {
                            "id": 15
                        },
                        {
                            "id": 25
                        },
                        {
                            "id": 27
                        }
                    ]
                }
                """.formatted(DESCRIPCION_PRODUCTO, DESCRIPCION_CATEGORIA);
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/instrumentos")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'nombre': El nombre del producto no puede ser nulo o estar vacío."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con un nombre nulo a través del método POST y se lanzó una Excepción, " +
                "tal como se esperaba.");
    }

    @Test
    @Order(5)
    void dadoUnNombreVacio_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": " ",
                    "descripcion": "%s",
                    "marca": "Martin Fishman",
                    "precio": 1999.99,
                    "imagenes": [
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-1.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-2.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-3.jpg"
                    ],
                    "disponible": true,
                    "categoria": {
                        "id": 1,
                        "nombre": "Guitarras y Cuerdas",
                        "imagen": "/img/cat/guitarras-y-cuerdas.jpg",
                        "descripcion": "%s"
                    },
                    "caracteristicas": [
                        {
                            "id": 1
                        },
                        {
                            "id": 10
                        },
                        {
                            "id": 15
                        },
                        {
                            "id": 25
                        },
                        {
                            "id": 27
                        }
                    ]
                }
                """.formatted(DESCRIPCION_PRODUCTO, DESCRIPCION_CATEGORIA);
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/instrumentos")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'nombre': El nombre del producto no puede ser nulo o estar vacío."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con un nombre vacío a través del método POST y se lanzó una Excepción, " +
                "tal como se esperaba.");
    }

    @Test
    @Order(6)
    void dadoUnNombreConMasDe60Caracteres_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
                    "descripcion": "%s",
                    "marca": "Martin Fishman",
                    "precio": 1999.99,
                    "imagenes": [
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-1.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-2.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-3.jpg"
                    ],
                    "disponible": true,
                    "categoria": {
                        "id": 1,
                        "nombre": "Guitarras y Cuerdas",
                        "imagen": "/img/cat/guitarras-y-cuerdas.jpg",
                        "descripcion": "%s"
                    },
                    "caracteristicas": [
                        {
                            "id": 1
                        },
                        {
                            "id": 10
                        },
                        {
                            "id": 15
                        },
                        {
                            "id": 25
                        },
                        {
                            "id": 27
                        }
                    ]
                }
                """.formatted(DESCRIPCION_PRODUCTO, DESCRIPCION_CATEGORIA);
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/instrumentos")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'nombre': El nombre del producto no puede tener más de 60 caracteres."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con un nombre con longitud de 72 caracteres a través del método POST y se " +
                "lanzó una Excepción, tal como se esperaba.");
    }

    @Test
    @Order(7)
    void dadoUnaDescripcionNula_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "Guitarra Electroacústica Martin Fishman Sonitone 000X2E-001",
                    "descripcion": null,
                    "marca": "Martin Fishman",
                    "precio": 1999.99,
                    "imagenes": [
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-1.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-2.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-3.jpg"
                    ],
                    "disponible": true,
                    "categoria": {
                        "id": 1,
                        "nombre": "Guitarras y Cuerdas",
                        "imagen": "/img/cat/guitarras-y-cuerdas.jpg",
                        "descripcion": "%s"
                    },
                    "caracteristicas": [
                        {
                            "id": 1
                        },
                        {
                            "id": 10
                        },
                        {
                            "id": 15
                        },
                        {
                            "id": 25
                        },
                        {
                            "id": 27
                        }
                    ]
                }
                """.formatted(DESCRIPCION_CATEGORIA);
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/instrumentos")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'descripcion': La descripción del producto no puede ser nula o " +
                                "estar vacía."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con una descripción nula a través del método POST y se lanzó una Excepción, " +
                "tal como se esperaba.");
    }

    @Test
    @Order(8)
    void dadoUnaDescripcionVacia_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "Guitarra Electroacústica Martin Fishman Sonitone 000X2E-001",
                    "descripcion": " ",
                    "marca": "Martin Fishman",
                    "precio": 1999.99,
                    "imagenes": [
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-1.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-2.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-3.jpg"
                    ],
                    "disponible": true,
                    "categoria": {
                        "id": 1,
                        "nombre": "Guitarras y Cuerdas",
                        "imagen": "/img/cat/guitarras-y-cuerdas.jpg",
                        "descripcion": "%s"
                    },
                    "caracteristicas": [
                        {
                            "id": 1
                        },
                        {
                            "id": 10
                        },
                        {
                            "id": 15
                        },
                        {
                            "id": 25
                        },
                        {
                            "id": 27
                        }
                    ]
                }
                """.formatted(DESCRIPCION_CATEGORIA);
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/instrumentos")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'descripcion': La descripción del producto no puede ser nula o " +
                                "estar vacía."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con una descripción vacía a través del método POST y se lanzó una Excepción, " +
                "tal como se esperaba.");
    }

    @Test
    @Order(9)
    void dadaUnaDescripcionConMasDe1000Caracteres_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "Guitarra Electroacústica Martin Fishman Sonitone 000X2E-001",
                    "descripcion": "%s",
                    "marca": "Martin Fishman",
                    "precio": 1999.99,
                    "imagenes": [
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-1.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-2.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-3.jpg"
                    ],
                    "disponible": true,
                    "categoria": {
                        "id": 1,
                        "nombre": "Guitarras y Cuerdas",
                        "imagen": "/img/cat/guitarras-y-cuerdas.jpg",
                        "descripcion": "%s"
                    },
                    "caracteristicas": [
                        {
                            "id": 1
                        },
                        {
                            "id": 10
                        },
                        {
                            "id": 15
                        },
                        {
                            "id": 25
                        },
                        {
                            "id": 27
                        }
                    ]
                }
                """.formatted("Lorem ipsum dolor sit amet consectetur adipiscing elit, potenti justo ante aenean " +
                "vehicula. Ad class tempor condimentum vel curae nullam est aptent donec fringilla hac egestas " +
                "turpis dapibus, facilisis himenaeos natoque fermentum accumsan aliquet sapien sem odio penatibus " +
                "venenatis mauris ac. Malesuada facilisi per sagittis proin arcu dis convallis, varius lectus " +
                "interdum mus rhoncus tincidunt quam molestie, auctor scelerisque leo purus vulputate id. Netus " +
                "eleifend tristique iaculis dictum duis semper nibh, pretium maecenas at nec suspendisse. Mi litora " +
                "dignissim sollicitudin etiam volutpat erat senectus mattis euismod, diam nascetur ut habitasse " +
                "pellentesque magnis eget et, tellus a eu sociis praesent gravida elementum nam. Augue hendrerit " +
                "pharetra posuere viverra sociosqu primis mollis ultricies integer, cum curabitur nisi ornare " +
                "habitant ullamcorper nostra pulvinar sed fames, eros cubilia in non risus blandit nulla luctus. " +
                "Vestibulum inceptos commodo ultrices suscipit velit imperdiet porttitor, vivamus morbi rutrum felis " +
                "fusce dui, ridiculus porta placerat neque montes massa.", DESCRIPCION_CATEGORIA);
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/instrumentos")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'descripcion': La descripción del producto no puede tener más " +
                                "de 1000 caracteres."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con una descripción con longitud de 1095 caracteres a través del método POST " +
                "y se lanzó una Excepción, tal como se esperaba.");
    }

    @Test
    @Order(10)
    void dadoUnNombreDeMarcaNulo_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "Guitarra Electroacústica Martin Fishman Sonitone 000X2E-001",
                    "descripcion": "%s",
                    "marca": null,
                    "precio": 1999.99,
                    "imagenes": [
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-1.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-2.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-3.jpg"
                    ],
                    "disponible": true,
                    "categoria": {
                        "id": 1,
                        "nombre": "Guitarras y Cuerdas",
                        "imagen": "/img/cat/guitarras-y-cuerdas.jpg",
                        "descripcion": "%s"
                    },
                    "caracteristicas": [
                        {
                            "id": 1
                        },
                        {
                            "id": 10
                        },
                        {
                            "id": 15
                        },
                        {
                            "id": 25
                        },
                        {
                            "id": 27
                        }
                    ]
                }
                """.formatted(DESCRIPCION_PRODUCTO, DESCRIPCION_CATEGORIA);
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/instrumentos")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'marca': La marca del producto no puede ser nula o estar vacía."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con un nombre de marca nulo a través del método POST y se lanzó una Excepción, " +
                "tal como se esperaba.");
    }

    @Test
    @Order(11)
    void dadoUnNombreDeMarcaVacio_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "Guitarra Electroacústica Martin Fishman Sonitone 000X2E-001",
                    "descripcion": "%s",
                    "marca": " ",
                    "precio": 1999.99,
                    "imagenes": [
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-1.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-2.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-3.jpg"
                    ],
                    "disponible": true,
                    "categoria": {
                        "id": 1,
                        "nombre": "Guitarras y Cuerdas",
                        "imagen": "/img/cat/guitarras-y-cuerdas.jpg",
                        "descripcion": "%s"
                    },
                    "caracteristicas": [
                        {
                            "id": 1
                        },
                        {
                            "id": 10
                        },
                        {
                            "id": 15
                        },
                        {
                            "id": 25
                        },
                        {
                            "id": 27
                        }
                    ]
                }
                """.formatted(DESCRIPCION_PRODUCTO, DESCRIPCION_CATEGORIA);
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/instrumentos")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'marca': La marca del producto no puede ser nula o estar vacía."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con un nombre de marca vacío a través del método POST y se lanzó una " +
                "Excepción, tal como se esperaba.");
    }

    @Test
    @Order(12)
    void dadoUnNombreDeMarcaConLongitudMayorDe60Caracteres_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "Guitarra Electroacústica Martin Fishman Sonitone 000X2E-001",
                    "descripcion": "%s",
                    "marca": "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
                    "precio": 1999.99,
                    "imagenes": [
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-1.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-2.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-3.jpg"
                    ],
                    "disponible": true,
                    "categoria": {
                        "id": 1,
                        "nombre": "Guitarras y Cuerdas",
                        "imagen": "/img/cat/guitarras-y-cuerdas.jpg",
                        "descripcion": "%s"
                    },
                    "caracteristicas": [
                        {
                            "id": 1
                        },
                        {
                            "id": 10
                        },
                        {
                            "id": 15
                        },
                        {
                            "id": 25
                        },
                        {
                            "id": 27
                        }
                    ]
                }
                """.formatted(DESCRIPCION_PRODUCTO, DESCRIPCION_CATEGORIA);
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/instrumentos")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'marca': La marca del producto no puede tener más de 60 caracteres."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con un nombre de marca con longitud de 72 caracteres a través del método POST " +
                "y se lanzó una Excepción, tal como se esperaba.");
    }

    @Test
    @Order(13)
    void dadoUnPrecioNulo_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "Guitarra Electroacústica Martin Fishman Sonitone 000X2E-001",
                    "descripcion": "%s",
                    "marca": "Martin Fishman",
                    "precio": null,
                    "imagenes": [
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-1.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-2.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-3.jpg"
                    ],
                    "disponible": true,
                    "categoria": {
                        "id": 1,
                        "nombre": "Guitarras y Cuerdas",
                        "imagen": "/img/cat/guitarras-y-cuerdas.jpg",
                        "descripcion": "%s"
                    },
                    "caracteristicas": [
                        {
                            "id": 1
                        },
                        {
                            "id": 10
                        },
                        {
                            "id": 15
                        },
                        {
                            "id": 25
                        },
                        {
                            "id": 27
                        }
                    ]
                }
                """.formatted(DESCRIPCION_PRODUCTO, DESCRIPCION_CATEGORIA);
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/instrumentos")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'precio': El precio no puede ser nulo."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con un precio nulo a través del método POST y se lanzó una Excepción, tal " +
                "como se esperaba.");
    }

    @Test
    @Order(14)
    void dadoUnPrecioEnCero_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "Guitarra Electroacústica Martin Fishman Sonitone 000X2E-001",
                    "descripcion": "%s",
                    "marca": "Martin Fishman",
                    "precio": 0,
                    "imagenes": [
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-1.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-2.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-3.jpg"
                    ],
                    "disponible": true,
                    "categoria": {
                        "id": 1,
                        "nombre": "Guitarras y Cuerdas",
                        "imagen": "/img/cat/guitarras-y-cuerdas.jpg",
                        "descripcion": "%s"
                    },
                    "caracteristicas": [
                        {
                            "id": 1
                        },
                        {
                            "id": 10
                        },
                        {
                            "id": 15
                        },
                        {
                            "id": 25
                        },
                        {
                            "id": 27
                        }
                    ]
                }
                """.formatted(DESCRIPCION_PRODUCTO, DESCRIPCION_CATEGORIA);
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/instrumentos")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'precio': El precio debe ser positivo y mayor a cero."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con un precio en 0 a través del método POST y se lanzó una Excepción, tal " +
                "como se esperaba.");
    }

    @Test
    @Order(15)
    void dadoUnPrecioNegativo_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "Guitarra Electroacústica Martin Fishman Sonitone 000X2E-001",
                    "descripcion": "%s",
                    "marca": "Martin Fishman",
                    "precio": -1999.99,
                    "imagenes": [
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-1.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-2.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-3.jpg"
                    ],
                    "disponible": true,
                    "categoria": {
                        "id": 1,
                        "nombre": "Guitarras y Cuerdas",
                        "imagen": "/img/cat/guitarras-y-cuerdas.jpg",
                        "descripcion": "%s"
                    },
                    "caracteristicas": [
                        {
                            "id": 1
                        },
                        {
                            "id": 10
                        },
                        {
                            "id": 15
                        },
                        {
                            "id": 25
                        },
                        {
                            "id": 27
                        }
                    ]
                }
                """.formatted(DESCRIPCION_PRODUCTO, DESCRIPCION_CATEGORIA);
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/instrumentos")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'precio': El precio debe ser positivo y mayor a cero."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con un precio negativo a través del método POST y se lanzó una Excepción, tal " +
                "como se esperaba.");
    }

    @Test
    @Order(16)
    void dadaUnaDisponibilidadNula_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "Guitarra Electroacústica Martin Fishman Sonitone 000X2E-001",
                    "descripcion": "%s",
                    "marca": "Martin Fishman",
                    "precio": 1999.99,
                    "imagenes": [
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-1.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-2.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-3.jpg"
                    ],
                    "disponible": null,
                    "categoria": {
                        "id": 1,
                        "nombre": "Guitarras y Cuerdas",
                        "imagen": "/img/cat/guitarras-y-cuerdas.jpg",
                        "descripcion": "%s"
                    },
                    "caracteristicas": [
                        {
                            "id": 1
                        },
                        {
                            "id": 10
                        },
                        {
                            "id": 15
                        },
                        {
                            "id": 25
                        },
                        {
                            "id": 27
                        }
                    ]
                }
                """.formatted(DESCRIPCION_PRODUCTO, DESCRIPCION_CATEGORIA);
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/instrumentos")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'disponible': La disponibilidad del producto no puede ser nula."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con una disponibilidad nula a través del método POST y se lanzó una Excepción, " +
                "tal como se esperaba.");
    }

    @Test
    @Order(17)
    void dadaUnaCategoriaNula_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar400ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "Guitarra Electroacústica Martin Fishman Sonitone 000X2E-001",
                    "descripcion": "%s",
                    "marca": "Martin Fishman",
                    "precio": 1999.99,
                    "imagenes": [
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-1.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-2.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-3.jpg"
                    ],
                    "disponible": true,
                    "categoria": null,
                    "caracteristicas": [
                        {
                            "id": 1
                        },
                        {
                            "id": 10
                        },
                        {
                            "id": 15
                        },
                        {
                            "id": 25
                        },
                        {
                            "id": 27
                        }
                    ]
                }
                """.formatted(DESCRIPCION_PRODUCTO);
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/instrumentos")
                .then()
                .statusCode(400)
                .body(
                        "message", equalTo("Campo 'categoria': La categoría del producto no puede ser nula."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("BAD_REQUEST")
                );
        LOGGER.info("Se envió un JSON con una categoría nula a través del método POST y se lanzó una Excepción, " +
                "tal como se esperaba.");
    }

    @Test
    @Order(18)
    void dadoUnNombreRepetido_cuandoSeHagaUnaPeticionPost_entoncesDeberiaRetornar409ConUnMensaje() {
        String solicitud = """
                {
                    "id": null,
                    "nombre": "Guitarra Electroacústica Martin Fishman Sonitone 000X2E-001",
                    "descripcion": "%s",
                    "marca": "Martin Fishman",
                    "precio": 1999.99,
                    "imagenes": [
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-1.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-2.jpg",
                        "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-3.jpg"
                    ],
                    "disponible": true,
                    "categoria": {
                        "id": 1,
                        "nombre": "Guitarras y Cuerdas",
                        "imagen": "/img/cat/guitarras-y-cuerdas.jpg",
                        "descripcion": "%s"
                    },
                    "caracteristicas": [
                        {
                            "id": 1
                        },
                        {
                            "id": 10
                        },
                        {
                            "id": 15
                        },
                        {
                            "id": 25
                        },
                        {
                            "id": 27
                        }
                    ]
                }
                """.formatted(DESCRIPCION_PRODUCTO, DESCRIPCION_CATEGORIA);
        given().request().header("Authorization", ADMIN_JWT).and().body(solicitud).contentType(ContentType.JSON)
                .when().post("/api/v1/instrumentos")
                .then()
                .statusCode(409)
                .body(
                        "message", equalTo("El nombre del producto ya está registrado."),
                        "timestamp", notNullValue(),
                        "httpStatus", equalTo("CONFLICT")
                );
        LOGGER.info("Se envió un JSON con un nombre repetido a través del método POST y se lanzó una Excepción, " +
                "tal como se esperaba.");
    }

    @Test
    @Order(19)
    void dadoUnPayloadSinCuerpo_cuandoSeHagaUnaSolicitudPost_entoncesDeberiaRetornar400ConUnMensaje() {
        given().request().header("Authorization", ADMIN_JWT).and().body("").contentType(ContentType.JSON)
                .when().post("/api/v1/instrumentos")
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
    @Order(20)
    void dadoUnIdValido_cuandoSeHagaUnaLlamadaAlEndpointObtenerPorId_entoncesDeberiaRetornar200ConElElemento() {
        get("/api/v1/instrumentos/{id}", 97)
                .then()
                .statusCode(200)
                .body(
                        "id", notNullValue(),
                        "id", equalTo(97),
                        "nombre", equalTo("Guitarra Electroacústica Martin Fishman Sonitone 000X2E-001"),
                        "descripcion", startsWith("Tomamos"),
                        "descripcion", endsWith("caoba."),
                        "descripcion", equalTo(DESCRIPCION_PRODUCTO),
                        "marca", equalTo("Martin Fishman"),
                        "precio", equalTo(1999.99F),
                        "disponible", equalTo(true),
                        "categoria.id", equalTo(1),
                        "categoria.nombre", equalTo("Guitarras y Cuerdas"),
                        "categoria.imagen", equalTo("/img/cat/guitarras-y-cuerdas.webp"),
                        "categoria.descripcion", startsWith("Explora"),
                        "categoria.descripcion", containsString("Ya sea que anheles el dulce susurro de una guitarra"),
                        "categoria.descripcion", containsString("tenemos el instrumento perfecto para ti."),
                        "categoria.descripcion", endsWith("ideal.")
                );
        LOGGER.info("Se envió el ID '97', fue encontrado en la base de datos y retornado con código 200.");
    }

    @Test
    @Order(21)
    void dadoUnIdInvalido_cuandoSeHagaUnaLlamadaAlEndpointObtenerPorId_entoncesDeberiaRetornar404ConUnMensaje() {
        get("/api/v1/instrumentos/{id}", 100)
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
    @Order(22)
    void dadoUnTipoDeIdInvalido_cuandoSeHagaUnaLlamadaAlEndpointObtenerPorId_entoncesDeberiaRetornar400ConUnMensaje() {
        get("/api/v1/instrumentos/{id}", "ABC100")
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
    @Order(23)
    void dadoElRegistroExitoso_cuandoSeHagaUnaLlamadaAlEndpointObtenerTodos_entoncesDeberiaRetornar200Con97Elementos() {
        List<Map<String, Object>> productos = RestAssured.get("/api/v1/instrumentos").as(new TypeRef<>() {
        });
        assertThat(productos, hasSize(97));
        assertThat(productos.get(96).size(), equalTo(11));
        assertThat(productos.get(96).get("id"), equalTo(97));
        assertThat(productos.get(96).get("nombre"), equalTo("Guitarra Electroacústica Martin Fishman Sonitone 000X2E-001"));
        assertThat(productos.get(96).get("descripcion"), notNullValue());
        assertThat(productos.get(96).get("descripcion"), equalTo(DESCRIPCION_PRODUCTO));
        assertThat(productos.get(96).get("marca"), equalTo("Martin Fishman"));
        assertThat(productos.get(96).get("precio"), equalTo(1999.99));
        assertThat(productos.get(96).get("imagenes"), equalTo(List.of(
                "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-1.jpg",
                "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-2.jpg",
                "/img/prod/guitarra-electroacustica-martin-fishman-sonitone-000x2e-001-3.jpg"
        )));
        assertThat(productos.get(96).get("disponible"), equalTo(true));
        assertThat(productos.get(96).get("categoria"), notNullValue());
        assertThat(productos.get(96).get("caracteristicas"), notNullValue());
        assertThat(productos.get(96).get("reservas"), nullValue());
        assertThat(productos.get(96).get("fechasReservadas"), equalTo(List.of()));
        LOGGER.info("El producto que se registró antes fue encontrado en la lista de productos.");
    }

    @Test
    @Order(24)
    void dadoElNombreGuitarra_cuandoSeHagaUnaLlamadaAlEndpointObtenerTodoPorNombre_entoncesDeberiaRetornarUnaListaDe10Elementos() {
        List<String> productos = given().queryParam("nombre", "guitarra")
                .when().get("/api/v1/instrumentos/nombre").as(new TypeRef<>() {
                });
        assertThat(productos, hasSize(10));
        assertThat(productos.get(0), containsString("Cort CR100 CRS"));
        assertThat(productos.get(1), containsString("Cort G250 Negra"));
        assertThat(productos.get(2), containsString("Valencia 4/4 / Cutaway / Negra"));
        assertThat(productos.get(3), containsString("Cort Classic AC100"));
        assertThat(productos.get(4), containsString("Cort Jazz Yorktown-BV"));
        assertThat(productos.get(5), containsString("Cort SFX-DAO Natural"));
        assertThat(productos.get(6), containsString("Fender Squier Surf Pearl"));
        assertThat(productos.get(7), containsString("Valencia 4/4 VC204TBU Azul"));
        assertThat(productos.get(8), containsString("Fender Squier Classic Vibe 70s"));
        assertThat(productos.get(9), containsString("Martin Fishman Sonitone 000X2E-001"));
        LOGGER.info("Se obtuvo una lista de 10 productos que contienen la palabra 'Guitarra' en el nombre.");
    }

    @Test
    @Order(25)
    void dadoElNombreVerde_cuandoSeHagaUnaLlamadaAlEndpointObtenerTodoPorNombre_entoncesDeberiaRetornarUnArraySinResultados() {
        List<String> productos = given().queryParam("nombre", "verde")
                .when().get("/api/v1/instrumentos/nombre").as(new TypeRef<>() {
                });
        assertThat(productos, hasSize(0));
        LOGGER.info("Se obtuvo una lista vacía, ya que no hay productos que contengan la palabra 'Verde' en el nombre.");
    }

    @Test
    @Order(26)
    void dadoElNombreTeclado_cuandoSeHagaUnaLlamadaAlEndpointRealizarBusquedaPorNombre_entoncesDeberiaRetornarUnaListaDe2Elementos() {
        List<Map<String, Object>> productos = given().queryParam("nombre", "teclado")
                .when().get("/api/v1/instrumentos/busqueda").as(new TypeRef<>() {
                });
        assertThat(productos, hasSize(2));
        assertThat(productos.get(0).get("id"), equalTo(19));
        assertThat(productos.get(0).get("nombre"), equalTo("Teclado Arranger Roland E-X30"));
        assertThat(productos.get(0).get("marca"), equalTo("Roland"));
        assertThat(productos.get(1).get("id"), equalTo(21));
        assertThat(productos.get(0).get("imagenes"), equalTo(List.of("/img/E-X30_1.webp")));
        assertThat(productos.get(1).get("nombre"), equalTo("Teclado Kurzweil KP-110 Blanco"));
        assertThat(productos.get(1).get("marca"), equalTo("Kurzweil"));
        assertThat(productos.get(1).get("imagenes"), equalTo(List.of("/img/dkzfqmec.webp")));
        LOGGER.info("Se obtuvo una lista de 2 productos que contienen la palabra 'Teclado' en el nombre.");
    }

    @Test
    @Order(27)
    void dadoElNombreRosa_cuandoSeHagaUnaLlamadaAlEndpointRealizarBusquedaPorNombre_entoncesDeberiaRetornarUnArraySinResultados() {
        List<Map<String, Object>> productos = given().queryParam("nombre", "rosa")
                .when().get("/api/v1/instrumentos/busqueda").as(new TypeRef<>() {
                });
        assertThat(productos, hasSize(0));
        LOGGER.info("Se obtuvo una lista vacía, ya que no hay productos que contengan la palabra 'Rosa' en el nombre.");
    }

    @Test
    @Order(28)
    void dadosLosNumero5Y7_cuandoSeHagaUnLlamadoAlEndpointBuscarPorCategoria_entoncesDeberiaRetornarUnaListaDe23Elementos() {
        List<Map<String, Object>> productos = given().body("[5, 7]").contentType(ContentType.JSON)
                .when().get("/api/v1/instrumentos/buscar").as(new TypeRef<>() {
                });
        List<Object> ids = productos.stream().map(producto -> producto.get("id")).toList();
        assertThat(productos, hasSize(23));
        assertThat(ids.toString(), stringContainsInOrder(List.of("19", "20", "21", "27", "28", "29", "30",
                "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87")));
        LOGGER.info("Se obtuvo una lista de 23 productos que pertenecen a las categorías 5 y 7.");
    }

    @Test
    @Order(29)
    void dadosLosDatosRegistrados_cuandoSeHagaUnaLlamadaAlEndpointObtenerAleatorio_entoncesDeberiaRetornar200ConUnaListaDe10ElementosDesordenados() {
        List<Map<String, Object>> productos = RestAssured.get("/api/v1/instrumentos/aleatorio").as(new TypeRef<>() {
        });
        List<Object> ids = productos.stream().map(producto -> producto.get("id")).toList();
        assertThat(productos, hasSize(10));
        assertThat(ids.toString(), not(stringContainsInOrder(List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"))));
        LOGGER.info("Se obtuvo una lista de 5 productos aleatorios.");
    }

    @Test
    @Order(30)
    void dadoUnIdValido_cuandoSeHagaUnaLlamadaAlEndpointEliminar_entoncesDeberiaRetornar200() {
        given().request().header("Authorization", ADMIN_JWT).when()
                .delete("/api/v1/instrumentos/{id}", 97)
                .then()
                .statusCode(200);
        LOGGER.info("Se envió a eliminar el ID '97', fue encontrado en la base de datos, fue eliminado y se retornó " +
                "el código 200.");
    }

    @Test
    @Order(31)
    void dadoUnIdValido_cuandoSeHagaUnaLlamadaAlEndpointEliminarSinAuthorization_entoncesDeberiaRetornar403EImpedirElAcceso() {
        delete("/api/v1/instrumentos/{id}", 96)
                .then()
                .statusCode(403);
        LOGGER.info("Se envió a eliminar el ID '96' sin Authorization, se impidió el acceso y se retornó el código 403.");
    }

    @Test
    @Order(32)
    void dadoUnIdInvalido_cuandoSeHagaUnaLlamadaAlEndpointEliminar_entoncesDeberiaRetornar404ConUnMensaje() {
        given().request().header("Authorization", ADMIN_JWT).when()
                .delete("/api/v1/instrumentos/{id}", 100)
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
    @Order(33)
    void dadoUnTipoDeIdInvalido_cuandoSeHagaUnaLlamadaAlEndpointEliminar_entoncesDeberiaRetornar400ConUnMensaje() {
        given().request().header("Authorization", ADMIN_JWT).when()
                .delete("/api/v1/instrumentos/{id}", "ABC100")
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