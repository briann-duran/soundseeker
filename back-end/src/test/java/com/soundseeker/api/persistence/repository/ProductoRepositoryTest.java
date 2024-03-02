package com.soundseeker.api.persistence.repository;

import com.soundseeker.api.persistence.entity.CategoriaEntity;
import com.soundseeker.api.persistence.entity.ProductoEntity;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@Testcontainers(parallel = true)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductoRepositoryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductoRepository.class);

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
    @Autowired
    private ProductoRepository productoRepository;

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
                new CategoriaEntity(1L, "Teclados", "/img/cat/teclados.jpg", null, null,
                        "Sumérgete en el mundo de posibilidades que los teclados en alquiler te ofrecen.", null),
                new CategoriaEntity(2L, "Pianos", "/img/cat/pianos.jpg", null, null,
                        "Deja que tus dedos se deslicen con gracia sobre las teclas de nuestros pianos " +
                                "en alquiler.", null),
                new CategoriaEntity(3L, "Percusión", "/img/cat/percusion.jpg", null, null,
                        "Siente el pulso de la música en tus venas con nuestra vibrante selección de instrumentos de " +
                                "percusión en alquiler.", null)
        );
        this.categoriaRepository.saveAll(categorias);

        List<ProductoEntity> productos = List.of(
                new ProductoEntity(1L,
                        "Teclado Arranger Roland E-X30",
                        "Los sonidos de piano de alta calidad en el E-X30 están perfectamente adecuados para el " +
                                "desarrollo de las habilidades musicales, donde siempre se requieren los sonidos y " +
                                "matices tradicionales.",
                        "Roland", 699.99,
                        Set.of("/img/prod/teclado-roland-e-x30-1.jpg", "/img/prod/teclado-roland-e-x30-2.jpg"),
                        true, categorias.get(0), null, null, null
                ),
                new ProductoEntity(2L,
                        "Piano de Cola Steinway & Sons Modelo D GRD Hamb Ebony",
                        "Este majestuoso instrumento musical — el pináculo de los pianos de cola de concierto- es la " +
                                "preferencia abrumadora de los mejores pianistas del mundo y de cualquiera que exija " +
                                "el más alto nivel de expresión musical.",
                        "Steinway & Sons", 99999.99,
                        Set.of("/img/prod/piano-steinway-d-1.jpg", "/img/prod/piano-steinway-d-2.jpg"),
                        false, categorias.get(1), null, null, null
                ),
                new ProductoEntity(3L,
                        "Pandero Tom Grasso de 6\"",
                        "Instrumento de percusión pandereta en madera con 6 sonajeros y parche sintético.",
                        "Tom Grasso", 29.99,
                        Set.of("/img/prod/pandero-tom-grasso-6-1.jpg", "/img/prod/pandero-tom-grasso-6-2.jpg"),
                        true, categorias.get(2), null, null, null
                )
        );
        this.productoRepository.saveAll(productos);
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
        ProductoEntity producto = new ProductoEntity(null, "Teclado Kurzweil KP-110 Blanco",
                "El KP110 es un poderoso teclado portátil dirigido tanto para principiantes como un entusiastas de la música.",
                "Kurzweil", 499.99, Set.of("/img/prod/teclado-kp-110-1.jpg", "/img/prod/teclado-kp-110-2.jpg"),
                true, this.categoriaRepository.getReferenceById(1L), null, null, null
        );
        ProductoEntity productoGuadado = this.productoRepository.save(producto);

        assertThat(productoGuadado.getId()).isNotNull().isEqualTo(4L);
        assertThat(productoGuadado)
                .extracting("nombre", "descripcion", "marca", "precio", "disponible")
                .contains(producto.getNombre(), producto.getDescripcion(), producto.getMarca(),
                        producto.getPrecio(), producto.getDisponible()
                );
        assertThat(productoGuadado.getCategoria().getId()).isEqualTo(1L);
        assertThat(this.productoRepository.count()).isEqualTo(4L);
        LOGGER.info("Los atributos del Producto guardado son iguales a los del Producto pasado; el ID no es nulo, se " +
                "autoasignó a '4' y el Repositorio tiene 4 elementos en total.");
    }

    @Test
    @Order(3)
    void dadoNingunDatoValido_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        ProductoEntity producto = new ProductoEntity();
        assertThatCode(() -> this.productoRepository.save(producto)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por no haber pasado ningún parámetro, tal como se esperaba.");
    }

    @Test
    @Order(4)
    void dadoUnNombreNulo_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        ProductoEntity producto = new ProductoEntity(null, null,
                "El KP110 es un poderoso teclado portátil dirigido tanto para principiantes como un entusiastas de la música.",
                "Kurzweil", 499.99, Set.of("/img/prod/teclado-kp-110-1.jpg", "/img/prod/teclado-kp-110-2.jpg"),
                true, this.categoriaRepository.getReferenceById(1L), null, null, null
        );
        assertThatCode(() -> this.productoRepository.save(producto)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por no incluir un nombre válido, tal como se esperaba.");
    }

    @Test
    @Order(5)
    void dadaUnaDescripcionNula_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        ProductoEntity producto = new ProductoEntity(null, "Teclado Kurzweil KP-110 Negro", null,
                "Kurzweil", 499.99, Set.of("/img/prod/teclado-kp-110-1.jpg", "/img/prod/teclado-kp-110-2.jpg"),
                true, this.categoriaRepository.getReferenceById(1L), null, null, null
        );
        assertThatCode(() -> this.productoRepository.save(producto)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por no incluir una descripción válida, tal como se esperaba.");
    }

    @Test
    @Order(6)
    void dadaUnaMarcaNula_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        ProductoEntity producto = new ProductoEntity(null, "Teclado Kurzweil KP-110 Negro",
                "El KP110 es un poderoso teclado portátil dirigido tanto para principiantes como un entusiastas de la música.",
                null, 499.99, Set.of("/img/prod/teclado-kp-110-1.jpg", "/img/prod/teclado-kp-110-2.jpg"),
                true, this.categoriaRepository.getReferenceById(1L), null, null, null
        );
        assertThatCode(() -> this.productoRepository.save(producto)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por no incluir una marca válida, tal como se esperaba.");
    }

    @Test
    @Order(7)
    void dadoUnPrecioNulo_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        ProductoEntity producto = new ProductoEntity(null, "Teclado Kurzweil KP-110 Negro",
                "El KP110 es un poderoso teclado portátil dirigido tanto para principiantes como un entusiastas de la música.",
                "Kurzweil", null, Set.of("/img/prod/teclado-kp-110-1.jpg", "/img/prod/teclado-kp-110-2.jpg"),
                true, this.categoriaRepository.getReferenceById(1L), null, null, null
        );
        assertThatCode(() -> this.productoRepository.save(producto)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por no incluir un precio válida, tal como se esperaba.");
    }

    @Test
    @Order(8)
    void dadaUnaDisponibilidadNula_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        ProductoEntity producto = new ProductoEntity(null, "Teclado Kurzweil KP-110 Negro",
                "El KP110 es un poderoso teclado portátil dirigido tanto para principiantes como un entusiastas de la música.",
                "Kurzweil", 499.99, Set.of("/img/prod/teclado-kp-110-1.jpg", "/img/prod/teclado-kp-110-2.jpg"),
                null, this.categoriaRepository.getReferenceById(1L), null, null, null
        );
        assertThatCode(() -> this.productoRepository.save(producto)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por no incluir una disponibilidad válida, tal como se esperaba.");
    }

    @Test
    @Order(9)
    void dadaUnIdDeCategoriaNulo_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        ProductoEntity producto = new ProductoEntity(null, "Teclado Kurzweil KP-110 Negro",
                "El KP110 es un poderoso teclado portátil dirigido tanto para principiantes como un entusiastas de la música.",
                "Kurzweil", 499.99, Set.of("/img/prod/teclado-kp-110-1.jpg", "/img/prod/teclado-kp-110-2.jpg"),
                true, null, null, null, null
        );
        assertThatCode(() -> this.productoRepository.save(producto)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por no incluir un ID de Categoría válida, tal como se esperaba.");
    }

    @Test
    @Order(10)
    void dadoUnNombreVacio_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        ProductoEntity producto = new ProductoEntity(null, "",
                "El KP110 es un poderoso teclado portátil dirigido tanto para principiantes como un entusiastas de la música.",
                "Kurzweil", 499.99, Set.of("/img/prod/teclado-kp-110-1.jpg", "/img/prod/teclado-kp-110-2.jpg"),
                true, this.categoriaRepository.getReferenceById(1L), null, null, null
        );
        assertThatCode(() -> this.productoRepository.save(producto)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por colocar un nombre vacío, tal como se esperaba.");
    }

    @Test
    @Order(11)
    void dadoUnNombreConLongituMayorDe60Caracteres_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        ProductoEntity producto = new ProductoEntity(null,
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
                "El KP110 es un poderoso teclado portátil dirigido tanto para principiantes como un entusiastas de la música.",
                "Kurzweil", 499.99, Set.of("/img/prod/teclado-kp-110-1.jpg", "/img/prod/teclado-kp-110-2.jpg"),
                true, this.categoriaRepository.getReferenceById(1L), null, null, null
        );
        assertThatCode(() -> this.productoRepository.save(producto)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por sobrepasar el número máximo de caracteres en el nombre, tal como se esperaba.");
    }

    @Test
    @Order(12)
    void dadaUnaDescripcionVacia_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        ProductoEntity producto = new ProductoEntity(null, "Teclado Kurzweil KP-110 Negro", "",
                "Kurzweil", 499.99, Set.of("/img/prod/teclado-kp-110-1.jpg", "/img/prod/teclado-kp-110-2.jpg"),
                true, this.categoriaRepository.getReferenceById(1L), null, null, null
        );
        assertThatCode(() -> this.productoRepository.save(producto)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por colocar una descripción vacía, tal como se esperaba.");
    }

    @Test
    @Order(13)
    void dadaUnaDescripcionConLongituMayorDe1000Caracteres_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        ProductoEntity producto = new ProductoEntity(null, "Teclado Kurzweil KP-110 Negro",
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
                        "gravida habitant quis justo nunc viverra conubia commodo porttitor molestie.",
                "Kurzweil", 499.99, Set.of("/img/prod/teclado-kp-110-1.jpg", "/img/prod/teclado-kp-110-2.jpg"),
                true, this.categoriaRepository.getReferenceById(1L), null, null, null
        );
        assertThatCode(() -> this.productoRepository.save(producto)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por sobrepasar el número máximo de caracteres en la descripción, tal como se esperaba.");
    }

    @Test
    @Order(14)
    void dadaUnaMarcaVacia_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        ProductoEntity producto = new ProductoEntity(null, "Teclado Kurzweil KP-110 Negro",
                "El KP110 es un poderoso teclado portátil dirigido tanto para principiantes como un entusiastas de la música.",
                "", 499.99, Set.of("/img/prod/teclado-kp-110-1.jpg", "/img/prod/teclado-kp-110-2.jpg"),
                true, this.categoriaRepository.getReferenceById(1L), null, null, null
        );
        assertThatCode(() -> this.productoRepository.save(producto)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por colocar una marca vacía, tal como se esperaba.");
    }

    @Test
    @Order(15)
    void dadaUnaMarcaConLongituMayorDe60Caracteres_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        ProductoEntity producto = new ProductoEntity(null, "Teclado Kurzweil KP-110 Negro",
                "El KP110 es un poderoso teclado portátil dirigido tanto para principiantes como un entusiastas de la música.",
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
                499.99, Set.of("/img/prod/teclado-kp-110-1.jpg", "/img/prod/teclado-kp-110-2.jpg"),
                true, this.categoriaRepository.getReferenceById(1L), null, null, null
        );
        assertThatCode(() -> this.productoRepository.save(producto)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por sobrepasar el número máximo de caracteres en la marca, tal como se esperaba.");
    }

    @Test
    @Order(16)
    void dadoUnPrecioEnCero_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        ProductoEntity producto = new ProductoEntity(null, "Teclado Kurzweil KP-110 Negro",
                "El KP110 es un poderoso teclado portátil dirigido tanto para principiantes como un entusiastas de la música.",
                "Kurzweil", 0D, Set.of("/img/prod/teclado-kp-110-1.jpg", "/img/prod/teclado-kp-110-2.jpg"),
                true, this.categoriaRepository.getReferenceById(1L), null, null, null
        );
        assertThatCode(() -> this.productoRepository.save(producto)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por colocar el precio en cero, tal como se esperaba.");
    }

    @Test
    @Order(17)
    void dadoUnPrecioNegativo_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        ProductoEntity producto = new ProductoEntity(null, "Teclado Kurzweil KP-110 Negro",
                "El KP110 es un poderoso teclado portátil dirigido tanto para principiantes como un entusiastas de la música.",
                "Kurzweil", -499.99D, Set.of("/img/prod/teclado-kp-110-1.jpg", "/img/prod/teclado-kp-110-2.jpg"),
                true, this.categoriaRepository.getReferenceById(1L), null, null, null
        );
        assertThatCode(() -> this.productoRepository.save(producto)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por colocar un precio negativo, tal como se esperaba.");
    }

    @Test
    @Order(18)
    void dadoUnNombreDeProductoRepetido_cuandoSeIntenteRegistrar_entoncesDeberiaLanzarUnaExcepcion() {
        ProductoEntity producto = new ProductoEntity(null, "Teclado Arranger Roland E-X30",
                "Los sonidos de piano de alta calidad en el E-X30 están perfectamente adecuados para el " +
                        "desarrollo de las habilidades musicales, donde siempre se requieren los sonidos y " +
                        "matices tradicionales.",
                "Roland", 699.99, Set.of("/img/prod/teclado-roland-e-x30-1.jpg", "/img/prod/teclado-roland-e-x30-2.jpg"),
                true, this.categoriaRepository.getReferenceById(1L), null, null, null
        );
        assertThatCode(() -> this.productoRepository.save(producto)).isInstanceOf(Exception.class);
        LOGGER.info("Se lanzó una Excepción por nombre duplicado, tal como se esperaba.");
    }

    @Test
    @Order(19)
    void dadoUnNombreDeProductoExistente_cuandoSeLlameAlMetodoBuscarPorNombre_entoncesDeberiaRetornarLaEntidad() {
        ProductoEntity producto = this.productoRepository.findByNombreIgnoreCase("Teclado Arranger Roland E-X30").orElseThrow();
        assertThat(producto).isNotNull();
        assertThat(producto.getNombre()).isEqualTo("Teclado Arranger Roland E-X30");
        LOGGER.info("Se encontró el nombre 'Teclado Arranger Roland E-X30' en los productos existentes.");
    }

    @Test
    @Order(20)
    void dadoUnNombreDeCategoriaInexistente_cuandoSeLlameAlMetodoBuscarPorNombre_entoncesNoDeberiaRetornarNada() {
        Optional<ProductoEntity> producto = this.productoRepository.findByNombreIgnoreCase("Saxofón");
        assertThat(producto).isNotPresent();
        LOGGER.info("El producto 'Saxofón' no fue encontrado, tal como se esperaba.");
    }

    @Test
    @Order(21)
    void dadoUnNombreDeProductoNulo_cuandoSeLlameAlMetodoBuscarPorNombre_entoncesNoDeberiaRetornarNada() {
        Optional<ProductoEntity> producto = this.productoRepository.findByNombreIgnoreCase(null);
        assertThat(producto).isNotPresent();
        LOGGER.info("Nada fue encontrado porque se pasó un nulo, tal como se esperaba.");
    }

    @Test
    @Order(22)
    void dadoUnProductoConLosMismosAtributos_cuandoSeLlameAlMetodoBuscarPorId_entoncesDeberiaAsegurarQueElProductoEncontradoEsIgual() {
        Optional<ProductoEntity> productoEncontrado = this.productoRepository.findById(1L);
        CategoriaEntity categoriaReferencia = new CategoriaEntity(1L, "Teclados", "/img/cat/teclados.jpg", null, null,
                "Sumérgete en el mundo de posibilidades que los teclados en alquiler te ofrecen.", null);
        ProductoEntity productoReferencia = new ProductoEntity(1L,
                "Teclado Arranger Roland E-X30",
                "Los sonidos de piano de alta calidad en el E-X30 están perfectamente adecuados para el desarrollo " +
                        "de las habilidades musicales, donde siempre se requieren los sonidos y matices tradicionales.",
                "Roland", 699.99, Set.of("/img/prod/teclado-roland-e-x30-1.jpg", "/img/prod/teclado-roland-e-x30-2.jpg"),
                true, categoriaReferencia, null, null, null);
        productoEncontrado.ifPresent(producto -> Assertions.assertEquals(producto, productoReferencia));
        LOGGER.info("El producto encontrado es igual al producto construido.");
    }

    @Test
    @Order(23)
    void dadoElHashCodeDeUnProductoConstruido_cuandoSeLlameAlMetodoBuscarPorId_entoncesDeberiaAsegurarQueElHashCodeEsElMismo() {
        Optional<ProductoEntity> productoEncontrado = this.productoRepository.findById(1L);
        CategoriaEntity categoriaReferencia = new CategoriaEntity(1L, "Teclados", "/img/cat/teclados.jpg", null, null,
                "Sumérgete en el mundo de posibilidades que los teclados en alquiler te ofrecen.", null);
        ProductoEntity productoReferencia = new ProductoEntity(1L,
                "Teclado Arranger Roland E-X30",
                "Los sonidos de piano de alta calidad en el E-X30 están perfectamente adecuados para el desarrollo " +
                        "de las habilidades musicales, donde siempre se requieren los sonidos y matices tradicionales.",
                "Roland", 699.99, Set.of("/img/prod/teclado-roland-e-x30-1.jpg", "/img/prod/teclado-roland-e-x30-2.jpg"),
                true, categoriaReferencia, null, null, null);
        assertThat(productoEncontrado.hashCode()).isEqualTo(productoReferencia.hashCode());
        LOGGER.info("El HashCode del producto encontrado es igual al HashCode del producto construido.");
    }

    @Test
    @Order(24)
    void dadoUnObjetoVacio_cuandoSeUsenLosSettersYSePruebenLosGetters_entoncesDeberiaTraerLosValoresAsignados() {
        ProductoEntity producto = new ProductoEntity();
        producto.setId(1L);
        producto.setNombre("Teclado Arranger Roland E-X30");
        producto.setDescripcion("Los sonidos de piano de alta calidad en el E-X30 están perfectamente adecuados para el " +
                "desarrollo de las habilidades musicales, donde siempre se requieren los sonidos y " +
                "matices tradicionales.");
        producto.setMarca("Roland");
        producto.setPrecio(699.99);
        producto.setImagenes(Set.of("/img/prod/teclado-roland-e-x30-1.jpg", "/img/prod/teclado-roland-e-x30-2.jpg"));
        producto.setDisponible(true);
        producto.setCategoria(this.categoriaRepository.getReferenceById(1L));

        assertThat(producto.getId()).isEqualTo(1L);
        assertThat(producto.getNombre()).isEqualTo("Teclado Arranger Roland E-X30");
        assertThat(producto.getDescripcion()).isEqualTo("Los sonidos de piano de alta calidad en el E-X30 están " +
                "perfectamente adecuados para el desarrollo de las habilidades musicales, donde siempre se requieren " +
                "los sonidos y matices tradicionales.");
        assertThat(producto.getMarca()).isEqualTo("Roland");
        assertThat(producto.getPrecio()).isEqualTo(699.99);
        assertThat(producto.getImagenes()).contains("/img/prod/teclado-roland-e-x30-1.jpg", "/img/prod/teclado-roland-e-x30-2.jpg");
        assertThat(producto.getDisponible()).isTrue();
        assertThat(producto.getCategoria().getId()).isEqualTo(1L);
        LOGGER.info("Los valores de los atributos son los esperados.");
    }
}