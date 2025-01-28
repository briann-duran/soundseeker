# 🎵 SoundSeeker: Full Stack App

¡Hola! 👋😀 Esta aplicación Full Stack cumple con el objetivo de alquiler de instrumentos musicales. Incluye registro de
usuarios, gestión de productos, búsqueda avanzada, reserva por fechas, favoritos y chat. Desarrollado con React, Java
Spring Boot, MySQL y AWS.

¿Listos para el bis? 🎶 ¡Esperamos que disfruten la experiencia tanto como nosotros disfrutamos creándola!

¡Un abrazo musical y hasta la próxima sinfonía de código! 🎻🎹🎼

**Nota:** _Aunque no incluimos una plataforma de pagos, nuestro escenario de alquiler es tan real como la pasión que le
hemos
puesto a cada línea de código._

* [🌐 Vista general](#-vista-general)
    * [El desafío](#el-desafío)
    * [Vídeo de funcionamiento](#vídeo-de-funcionamiento)
* [✨ Sprints Desarrollados](#-sprints-desarrollados)
    * [Sprint #1: Primer Acuerdo](#sprint-1-primer-acuerdo)
    * [Sprint #2: El Compás del Usuario](#sprint-2-el-compás-del-usuario)
    * [Sprint #3: La Melodía de la Interacción](#sprint-3-la-melodía-de-la-interacción)
    * [Sprint #4: La Reserva es el Gran Final](#sprint-4-la-reserva-es-el-gran-final)
* [🔨 Nuestro proceso](#-nuestro-proceso)
    * [Construido con](#construido-con)
    * [Lo que aprendimos](#lo-que-aprendimos)
    * [Desarrollo continuo](#desarrollo-continuo)
    * [Recursos útiles](#recursos-útiles)
* [📝 Licencia](#-licencia)
* [⚡ Instalación](#-instalación)
* [👥 Autores](#-autores)
* [🙌 Agradecimientos](#-agradecimientos)

## 🌐 Vista general

### El desafío

![SoundSeeker.png](SoundSeeker.png)

SoundSeeker es un proyecto Full Stack que facilita el alquiler de instrumentos musicales a través de una interfaz
intuitiva y funcional. Nuestro equipo de 8 integrantes se dividió en especialistas de Front-end y Back-end, además
de contar con roles dedicados a Infraestructura y Testing, todos guiados por nuestro Scrum Master. Trabajamos de la mano
con metodologías ágiles para afinar cada detalle en sprints productivos.

### Vídeo de funcionamiento

[SoundSeekerDemo.webm](https://github.com/DavidGMont/soundseeker/assets/106042108/82422059-0977-4271-b0cd-d6ccf2a3db5c)

**Nota:** _Este vídeo muestra exclusivamente el funcionamiento de la aplicación con el rol de usuario anónimo y
autenticado; sin embargo, las funcionalidades de administrador están disponibles y operativas en la aplicación._

## ✨ Sprints Desarrollados

#### Sprint #1: Primer Acuerdo

**🏁 Meta:** Crear la armonía inicial con una estructura de sitio que resuene con las necesidades inmediatas: registro,
visualización y gestión de productos.

**🌟 Funcionalidades Clave:**

- UI con paleta de colores afinada a nuestra identidad.
- Estructura y diseño web (Header, Body y Footer).
- Visualización de 10 productos aleatorios para captar la atención desde la Landing Page.
- Galería de imágenes y detalles por producto, con una sinfonía visual que encanta.
- Paginación de productos, porque en la variedad está el gusto.
- Panel de administrador para registrar y eliminar productos con un solo clic.

#### Sprint #2: El Compás del Usuario

**🏁 Meta:** Establecer los pilares de autenticación y agregar profundidad al detalle de producto.

**🌟 Funcionalidades Clave:**

- Registro de usuarios con un extra de confirmación por correo electrónico.
- Login y logout fluidos, con manejo de errores para no desafinar.
- Creación y gestión de categorías y características de productos desde el panel de administración.
- Asignación de roles de administrador, porque una orquesta necesita sus directores.
- Exhibición de las características de los productos como la partitura de su calidad.

#### Sprint #3: La Melodía de la Interacción

**🏁 Meta:** Incorporar la búsqueda personalizada y hacer del detalle del producto una experiencia única.

**🌟 Funcionalidades Clave:**

- Búsqueda de productos por nombre y rango de fechas, para encontrar ese instrumento ideal en el momento justo.
- Calendario de disponibilidad en el detalle de producto, para planificar esa sinfonía perfecta.
- Posibilidad de favoritos, marcando el compás de lo que más te gusta.
- Listado de productos favoritos en un perfil de usuario que refleja sus preferencias.
- Políticas de producto claras y botón de compartir en redes sociales, porque la buena música debe difundirse.
- Eliminación de categorías desde el backstage del panel de administrador.

#### Sprint #4: La Reserva es el Gran Final

**🏁 Meta:** Sinfonizar la funcionalidad de reserva para que cada usuario pueda asegurar su instrumento soñado.

**🌟 Funcionalidades Clave:**

- Búsqueda refinada de instrumentos para reservar, considerando disponibilidad y fechas.
- Previsualización del producto antes de la reserva, porque los ojos también escuchan.
- Historial detallado de reservas en el perfil del usuario, que es casi como su playlist personal.
- Chat de WhatsApp para una comunicación directa y afinada con servicio al cliente.
- Notificación de reserva exitosa porque cada reserva merece su ovación.

## 🔨 Nuestro proceso

### Construido con

- 🧠 Back-end
    - [Java 17 Amazon Corretto](https://aws.amazon.com/es/corretto/)
    - [Spring Boot](https://spring.io/projects/spring-boot)
    - [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
    - [Spring Security](https://spring.io/projects/spring-security)
    - [Java Mail Sender](https://docs.spring.io/spring-framework/reference/integration/email.html)
    - [JSON Web Token (JWT)](https://jwt.io/)
    - [Lombok](https://projectlombok.org/)
    - [SpringDoc (Swagger)](https://springdoc.org/)
    - [Passay](https://www.passay.org/reference/)
    - [Docker](https://www.docker.com/)
- 🎨 Front-end
    - [Vite.js](https://vitejs.dev/)
    - [React](https://react.dev/)
    - [React Router](https://reactrouter.com/en/main)
    - [Axios](https://axios-http.com/)
    - [Sass](https://sass-lang.com/)
    - HTML5 Semántico
    - Propiedades de CSS Personalizadas
    - Flexbox
    - [Day.js](https://day.js.org/)
    - [SweetAlert](https://sweetalert.js.org/)
    - [Swiper](https://swiperjs.com/)
    - [Prettier](https://prettier.io/)
- 💾 Base de Datos
    - [MySQL](https://www.mysql.com/)
    - [H2](https://h2database.com/html/main.html)
- ☁ Infraestructura
    - [AWS Elastic Compute Cloud EC2](https://aws.amazon.com/es/ec2/)
    - [AWS Simple Storage Service S3](https://aws.amazon.com/es/s3/)
    - [GitHub Actions](https://github.com/features/actions)
    - [Docker](https://www.docker.com/)
    - [Docker Compose](https://docs.docker.com/compose/)
    - [Nginx](https://www.nginx.com/)
- 🧪 Testing (Unitario y de Integración)
    - [AssertJ](https://assertj.github.io/doc/)
    - [JUnit 5](https://junit.org/junit5/)
    - [REST Assured](https://rest-assured.io/)
    - [Testcontainers](https://testcontainers.com/)
    - [Postman](https://www.postman.com/)
- 🛠 Herramientas
    - [Jira](https://www.atlassian.com/es/software/jira)
    - [Notion](https://www.notion.so/es-es)
    - [IntelliJ IDEA](https://www.jetbrains.com/idea/)
    - [WebStorm](https://www.jetbrains.com/webstorm/)
    - [Visual Studio Code](https://code.visualstudio.com/)
    - [Git](https://git-scm.com/)
    - [GitHub](https://www.github.com/)
- 🎨 Diseño UX/UI
    - [Figma](https://www.figma.com/)
    - [Ver diseño aquí](https://www.figma.com/design/DiBU1cuHEIt0DIO1gJz7Pj/Proyecto-Prueba-2?node-id=1-5&p=f)

### Lo que aprendimos

Desarrollar una aplicación Full Stack es un desafío que requiere coordinación, dedicación y trabajo en equipo. Gracias a
este proyecto pusimos en práctica nuestros conocimientos técnicos y aprendimos a trabajar en un entorno real, con un
equipo multidisciplinario, multicultural y con un cliente que nos planteó sus necesidades y expectativas. Esta
oportunidad nos permitió aplicar metodologías ágiles, dividir el trabajo en sprints y mantener una comunicación
constante para lograr nuestros objetivos. Además, conocimos y utilizamos herramientas como Jira y Notion para organizar
nuestras tareas y mantener un registro de nuestro progreso.

En el desarrollo de la aplicación, pulimos nuestros conocimientos utilizando tecnologías como Spring Boot, React, MySQL,
AWS y Docker. Aprendimos a trabajar con bases de datos, a crear APIs REST, a implementar autenticación y
autorización, a desplegar aplicaciones en la nube, y lo más importante, a resolver problemas y desafíos siempre en
equipo.

### Desarrollo continuo

El desarrollo de SoundSeeker no termina aquí. Aunque hemos cumplido con los objetivos planteados, sabemos que siempre
hay margen para mejorar. En el futuro, nos gustaría añadir nuevas funcionalidades, como la posibilidad de restablecer
contraseñas, la implementación de un sistema de comentarios, y la integración de un sistema de pagos. Además, nos
gustaría mejorar la interfaz de usuario, añadir animaciones y mejorar la experiencia de usuario en general.

Estamos ansiosos por volver a habilitar la carga de imágenes, ya que en la versión actual de la aplicación, las imágenes
de los productos se encuentran embebidas en el jar de la aplicación, pero por cuestión de costos, hemos deshabilitado
la carga de imágenes desde el Front-end.

### Recursos útiles

Gracias a estos artículos, hemos podido resolver algunos inconvenientes:

- [`.env` variables en Spring Boot](https://stackoverflow.com/questions/73053852/spring-boot-env-variables-in-application-properties):
  con esto se pudo configurar que Spring Boot leyera las variables de ambiente provenientes de un archivo `.env`.
- Gracias a JPA-Buddy descubrimos que usar la anotación `@Data` en las entidades puede disminuir
  el [rendimiento de la aplicación](https://jpa-buddy.com/blog/lombok-and-jpa-what-may-go-wrong/).
- En Baeldung encontramos un resumen con las mejores prácticas a la hora de
  realizar [Unit Testing](https://www.baeldung.com/java-unit-testing-best-practices).
- Logramos añadir la propiedad `Set<String>` en nuestra entidad con `@ElementCollection`, esto lo aprendimos
  en [Baeldung](https://www.baeldung.com/java-jpa-lazy-collections).
- ¿Cómo cargar datos después de inicializar
  JPA? [Aquí lo aprendimos](https://www.onlinetutorialspoint.com/spring-boot/spring-boot-how-to-load-initial-data-on-startup.html).
- ¿Cómo asegurarse de la cobertura de código en el Testing? IntelliJ IDEA tiene el perfil para
  eso, [lo aprendimos aquí](https://www.jetbrains.com/help/idea/running-test-with-coverage.html).
- Gracias a la [documentación de uso](https://github.com/rest-assured/rest-assured/wiki/Usage) de REST-assured, pudimos
  utilizar la librería en las respuestas de nuestros controladores.
- ¿Cómo obligar a los usuarios utilizar contraseñas seguras? Con la [librería Passay](https://www.passay.org/reference/)
  se puede lograr en pocos pasos.
- Anotaciones personalizadas para validar contraseñas ¿es eso posible? Gracias a
  la [guía de Baeldung](https://www.baeldung.com/registration-password-strength-and-rules#validation), lo es.
- ¿Cómo añadir variables a los `TextBlock`? Se puede hacer con el
  método `String.formatted()`, [esto lo aprendimos aquí](https://stackoverflow.com/questions/63687580/how-can-i-add-variables-inside-java-15-text-block-feature).
- Gracias a la [guía de Baeldung](https://www.baeldung.com/spring-security-registration) del proceso de registro de
  usuarios con Spring, se pudieron implementar métodos y acciones no contempladas y desconocidas, como eventos de Java,
  envío de correos y confirmación de cuentas.
- Con [este vídeo](https://www.youtube.com/watch?v=UaB-0e76LdQ) nos enteramos de que implementar
  el `DaoAuthenticationProvider` dentro del `AuthenticationManager` es
  lo que recomienda el equipo de Spring Security, el `AuthenticationConfiguration` ya quedó en el pasado.
- Con [esta guía](https://www.baeldung.com/java-stream-filter-lambda) aprendimos a filtrar resultados con la
  API `stream`.
- Debido a la necesidad de mostrar desde el Front-end los días
  reservados, [gracias a esta guía](https://stackoverflow.com/questions/2689379/how-to-get-a-list-of-dates-between-two-dates-in-java)
  aprendimos a usar la API `stream` para obtener cada fecha dentro de un rango.
- Con el Plug-in [JPABuddy](https://jpa-buddy.com/), aprendimos que las relaciones `@ManyToMany` se desempeñan mejor con
  arreglos de
  tipo `Set`, [aquí se encuentran las recomendaciones](https://jpa-buddy.com/documentation/entity-designer/#associations-performance-tips).
- Gracias a [este tutorial](https://blog.tericcabrel.com/deploy-spring-boot-jar-nginx-reverse-proxy/) pudimos configurar
  Nginx para redirigir el tráfico a nuestra aplicación de Spring Boot sin necesidad de exponer puertos adicionales en el
  servidor.
- Con la [documentación de Vite.js](https://vitejs.dev/guide/env-and-mode), pudimos configurar las variables de
  entorno para el Front-end.

## 📝 Licencia

Este proyecto está bajo la licencia GNU General Public License (GPL) v3.0, para más detalles, por favor revisa el
archivo [LICENSE](LICENSE).

## ⚡ Instalación

Consulta el archivo [HELP.md](HELP.md) para obtener instrucciones detalladas sobre cómo instalar y ejecutar la
aplicación.

## 👥 Autores

- Agustina Barca ([@agusbarca](https://github.com/agusbarca))
- [Brian Durán ([@Nairb-code](https://github.com/Nairb-code)) ](https://github.com/briann-duran)
- Diego Parula ([@DiegoParula](https://github.com/DiegoParula))
- Juan García ([@DavidGMont](https://github.com/DavidGMont))
- Lucía Zanotti ([@luciazanotti](https://github.com/luciazanotti))
- Mauricio Basalo ([@mBasalo](https://github.com/mBasalo))
- Pedro de la Prieta ([@pedroodelap](https://github.com/pedroodelap))
- Uriel Pazos ([@UrielPazos](https://github.com/UrielPazos))

## 🙌 Agradecimientos

- A todos los miembros del equipo por su dedicación, esfuerzo y trabajo en equipo. Es por su compromiso que hemos
  logrado alcanzar (e incluso superado) nuestros objetivos.
- A nuestra querida amiga Serrana Marset ([@marset-s](https://github.com/marset-s)) por su continuo apoyo y constantes
  pruebas de la aplicación a lo largo de las fases de desarrollo.
- A Alejandro Ramírez ([@soyalejoramirez](https://github.com/soyalejoramirez)) por sus dos cursos
  de [Spring Data JPA](https://platzi.com/cursos/java-spring-data/)
  y [Spring Security](https://platzi.com/cursos/java-spring-security/) en Platzi, estos recursos fueron fundamentales
  para el desarrollo de la aplicación.
- A [Baeldung](https://www.baeldung.com/) por sus guías y tutoriales, que nos ayudaron a resolver problemas comunes
  utilizando las mejores prácticas.
- A [GitHub Copilot](https://github.com/features/copilot), [Amazon Q](https://aws.amazon.com/es/q/),
  [Amazon CodeWhisperer](https://aws.amazon.com/es/codewhisperer/), [ChatGPT](https://openai.com/chatgpt)
  y [Bing Copilot](https://copilot.microsoft.com/) por sus respuestas generativas, estas fueron de gran ayuda en
  momentos donde no sabíamos cómo abordar problemáticas no comunes mejorando la calidad de nuestro código.
- A [Lighthouse](https://developer.chrome.com/docs/lighthouse?hl=es-419) por su herramienta de auditoría de rendimiento,
  accesibilidad, buenas prácticas y SEO, que nos permitió mejorar la calidad de nuestro Front-end, Back-end y servidor.
- A [Squoosh](https://squoosh.app/) por su herramienta de compresión de imágenes, que nos permitió reducir el tamaño de
  las imágenes de la aplicación.
