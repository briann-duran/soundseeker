package com.soundseeker.api.persistence.repository;

import com.soundseeker.api.persistence.entity.CategoriaEntity;
import org.junit.jupiter.api.*;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers(parallel = true)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoriaRepositoryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoriaRepositoryTest.class);

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
    private CategoriaRepository categoriaRepository;

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

    @BeforeEach
    void configuracion() {
        List<CategoriaEntity> categorias = List.of(
                new CategoriaEntity(1L, "Guitarras y Cuerdas", "/img/cat/guitarras-y-cuerdas.jpg", null, null,
                        "Explora el universo sonoro que te ofrecen nuestras guitarras y cuerdas en alquiler.", null),
                new CategoriaEntity(2L, "Acordeones", "/img/cat/acordeones.jpg", null, null,
                        "¿Listo para descubrir el alma vibrante del acordeón?", null),
                new CategoriaEntity(3L, "Pianos", "/img/cat/pianos.jpg", null, null,
                        "Deja que tus dedos se deslicen con gracia sobre las teclas de nuestros pianos en alquiler.", null),
                new CategoriaEntity(4L, "Percusión", "/img/cat/percusion.jpg", null, null,
                        "Siente el pulso de la música en tus venas con nuestra vibrante selección de instrumentos de " +
                                "percusión en alquiler.", null),
                new CategoriaEntity(5L, "Teclados", "/img/cat/teclados.jpg", null, null,
                        "Sumérgete en el mundo de posibilidades que los teclados en alquiler te ofrecen.", null)
        );
        this.categoriaRepository.saveAll(categorias);
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
        CategoriaEntity categoria = new CategoriaEntity(null, "Vientos", "/img/vientos.png", null, null,
                "Explora el universo sonoro que te ofrecen nuestros instrumentos de viento en alquiler.", null);
        CategoriaEntity categoriaGuardada = this.categoriaRepository.save(categoria);
        assertThat(categoriaGuardada.getId()).isNotNull().isEqualTo(6L);
        assertThat(categoriaGuardada.getNombre()).isEqualTo(categoria.getNombre());
        assertThat(categoriaGuardada.getImagen()).isEqualTo(categoria.getImagen());
        assertThat(this.categoriaRepository.count()).isEqualTo(6L);
        LOGGER.info("Los atributos de la Categoría guardada son iguales a los de la Categoría pasada; el ID no es " +
                "nulo, se autoasignó a '6' y el Repositorio tiene 6 elementos en total.");
    }

    @Test
    @Order(3)
    void dadoUnNombreNulo_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        assertThrows(Exception.class, () -> this.categoriaRepository.save(new CategoriaEntity(null, null,
                "/img/baterias.png", null, null,
                "Explora el universo sonoro que te ofrecen nuestras baterías en alquiler.", null)));
        LOGGER.info("Se lanzó una Excepción por no incluir un nombre válido, tal como se esperaba.");
    }

    @Test
    @Order(4)
    void dadaUnaUrlDeImagenNula_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        assertThrows(Exception.class, () -> this.categoriaRepository.save(new CategoriaEntity(null, "Cuerdas",
                null, null, null,
                "Explora el universo sonoro que te ofrecen nuestras cuerdas en alquiler.", null)));
        LOGGER.info("Se lanzó una Excepción por no incluir una imagen válida, tal como se esperaba.");
    }

    @Test
    @Order(5)
    void dadaUnaDescripcionNula_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        assertThrows(Exception.class, () -> this.categoriaRepository.save(new CategoriaEntity(null, "Percusión",
                "/img/percusion.png", null, null, null, null)));
        LOGGER.info("Se lanzó una Excepción por no incluir una descripción válida, tal como se esperaba.");
    }

    @Test
    @Order(6)
    void dadoTodosLosParametrosEnNulo_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        assertThrows(Exception.class, () -> this.categoriaRepository.save(new CategoriaEntity(null, null,
                null, null, null, null, null)));
        LOGGER.info("Se lanzó una Excepción por no incluir ningún dato, tal como se esperaba.");
    }

    @Test
    @Order(7)
    void dadoUnNombreVacio_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        assertThrows(Exception.class, () -> this.categoriaRepository.save(new CategoriaEntity(null, " ",
                "/img/baterias.png", null, null,
                "Explora el universo sonoro que te ofrecen nuestras baterías en alquiler.", null)));
        LOGGER.info("Se lanzó una Excepción por incluir un nombre sin ningún caracter, tal como se esperaba.");
    }

    @Test
    @Order(8)
    void dadoUnNombreConMasDe30Caracteres_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        assertThrows(Exception.class, () -> this.categoriaRepository.save(new CategoriaEntity(null,
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", "/img/pianos.png", null, null,
                "Explora el universo sonoro que te ofrecen nuestros pianos en alquiler.", null)));
        LOGGER.info("Se lanzó una Excepción por sobrepasar el número máximo de caracteres en el nombre, tal como se esperaba.");
    }

    @Test
    @Order(9)
    void dadaUnaUrlDeImagenVacia_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        assertThrows(Exception.class, () -> this.categoriaRepository.save(new CategoriaEntity(null, "Saxofones",
                " ", null, null,
                "Explora el universo sonoro que te ofrecen nuestros saxofones en alquiler.", null)));
        LOGGER.info("Se lanzó una Excepción por incluir una URL de imagen sin ningún caracter, tal como se esperaba.");
    }

    @Test
    @Order(10)
    void dadaUnaDescripcionVacia_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        assertThrows(Exception.class, () -> this.categoriaRepository.save(new CategoriaEntity(null, "Bajos",
                "/img/bajos.png", null, null, " ", null)));
        LOGGER.info("Se lanzó una Excepción por incluir una descripción sin ningún caracter, tal como se esperaba.");
    }

    @Test
    @Order(11)
    void dadaUnaDescripcionConMasDe500Caracteres_uandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        assertThrows(Exception.class, () -> this.categoriaRepository.save(new CategoriaEntity(null, "Bajos",
                "/img/bajos.png", null, null,
                "Lorem ipsum dolor sit amet consectetur adipiscing elit, vestibulum nibh proin vulputate non neque, " +
                        "eros egestas nostra himenaeos etiam lobortis. Platea pharetra augue pulvinar integer mauris " +
                        "aenean, ut scelerisque dui nulla enim dictum fermentum, taciti rhoncus dis porta sociosqu. " +
                        "Mattis tempor malesuada eu praesent cras ante aptent id class erat, duis risus quisque fames " +
                        "metus elementum inceptos in magna arcu tellus, volutpat imperdiet per ornare curae tincidunt " +
                        "habitasse odio aliquet. Dapibus lacinia ridiculus quam senectus suscipit rutrum, massa placerat " +
                        "nascetur primis dictumst vel, ultricies phasellus urna parturient eget. Tempus semper velit " +
                        "tortor condimentum sagittis pellentesque nec eleifend, iaculis convallis curabitur venenatis " +
                        "turpis sodales bibendum a pretium, ad natoque donec cubilia vitae suspendisse luctus. Vivamus " +
                        "fusce et hendrerit auctor nam euismod fringilla sapien mus feugiat aliquam penatibus, magnis " +
                        "cursus ac sociis hac consequat tristique faucibus morbi est nisi nullam mi, sollicitudin " +
                        "gravida habitant quis justo nunc viverra conubia commodo porttitor molestie.", null)));
        LOGGER.info("Se lanzó una Excepción por sobrepasar el número máximo de caracteres en la descripción, tal como se esperaba.");
    }

    @Test
    @Order(12)
    void dadoUnNombreDeCategoriaRepetido_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        assertThrows(Exception.class, () -> this.categoriaRepository.save(new CategoriaEntity(null, "Teclados",
                "/img/cat/teclados.jpg", null, null,
                "Explora el universo sonoro que te ofrecen nuestros teclados en alquiler.", null)));
        LOGGER.info("Se lanzó una Excepción por nombre duplicado, tal como se esperaba.");
    }

    @Test
    @Order(13)
    void dadoUnNombreDeCategoria_cuandoSeLlameAlMetodoBuscarPorNombre_entoncesDeberiaRetornarLaEntidad() {
        CategoriaEntity categoria = this.categoriaRepository.findByNombreIgnoreCase("Pianos").orElseThrow();
        assertThat(categoria.getNombre()).isEqualTo("Pianos");
        LOGGER.info("Se encontró el nombre 'Pianos' en las categorías existentes.");
    }

    @Test
    @Order(14)
    void dadoUnNombreDeCategoriaInexistente_cuandoSeLlameAlMetodoBuscarPorNombre_entoncesNoDeberiaRetornarNada() {
        Optional<CategoriaEntity> categoria = this.categoriaRepository.findByNombreIgnoreCase("Tambores");
        assertThat(categoria).isNotPresent();
        LOGGER.info("La categoría 'Tambores' no fue encontrada, tal como se esperaba.");
    }

    @Test
    @Order(15)
    void dadoUnNombreDeCategoriaNulo_cuandoSeLlameAlMetodoBuscarPorNombre_entoncesNoDeberiaRetornarNada() {
        Optional<CategoriaEntity> categoria = this.categoriaRepository.findByNombreIgnoreCase(null);
        assertThat(categoria).isNotPresent();
        LOGGER.info("Nada fue encontrado porque se pasó un nulo, tal como se esperaba.");
    }

    @Test
    @Order(16)
    void dadoUnaCategoriaConLosMismosAtributos_cuandoSeLlameAlMetodoBuscarPorId_entoncesDeberiaAsegurarQueLaCategoriaEncontradaEsIgual() {
        Optional<CategoriaEntity> categoriaEncontrada = this.categoriaRepository.findById(1L);
        CategoriaEntity categoriaReferencia = new CategoriaEntity(1L, "Teclados", "/img/cat/teclados.jpg", null, null,
                "Sumérgete en el mundo de posibilidades que los teclados en alquiler te ofrecen.", null);
        categoriaEncontrada.ifPresent(producto -> Assertions.assertEquals(producto, categoriaReferencia));
        LOGGER.info("La categoría encontrada es igual a la categoría construida.");
    }

    @Test
    @Order(17)
    void dadoElHashCodeDeUnaCategoriaConstruida_cuandoSeLlameAlMetodoBuscarPorId_entoncesDeberiaAsegurarQueElHashCodeEsElMismo() {
        Optional<CategoriaEntity> categoriaEncontrada = this.categoriaRepository.findById(1L);
        CategoriaEntity categoriaReferencia = new CategoriaEntity(1L, "Teclados", "/img/cat/teclados.jpg", null, null,
                "Explora el universo sonoro que te ofrecen nuestros teclados en alquiler.", null);
        assertThat(categoriaEncontrada.hashCode()).isEqualTo(categoriaReferencia.hashCode());
        LOGGER.info("El HashCode de la categoría encontrada es igual al HashCode de la categoría construida.");
    }

    @Test
    @Order(18)
    void dadoUnObjetoVacio_cuandoSeUsenLosSettersYSePruebenLosGetters_entoncesDeberiaTraerLosValoresAsignados() {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setId(1L);
        categoria.setNombre("Teclados");
        categoria.setImagen("/img/cat/teclados.jpg");
        categoria.setDescripcion("Explora el universo sonoro que te ofrecen nuestros teclados en alquiler.");

        assertThat(categoria.getId()).isEqualTo(1L);
        assertThat(categoria.getNombre()).isEqualTo("Teclados");
        assertThat(categoria.getImagen()).isEqualTo("/img/cat/teclados.jpg");
        assertThat(categoria.getDescripcion()).isEqualTo("Explora el universo sonoro que te ofrecen nuestros teclados en alquiler.");
        LOGGER.info("Los valores de los atributos son los esperados.");
    }
}