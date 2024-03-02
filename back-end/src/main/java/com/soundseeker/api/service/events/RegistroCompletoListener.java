package com.soundseeker.api.service.events;

import com.soundseeker.api.persistence.entity.UsuarioEntity;
import com.soundseeker.api.service.IUsuarioService;
import com.soundseeker.api.service.exception.MalaSolicitudException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistroCompletoListener implements ApplicationListener<OnRegistroCompletoEvent> {
    private final IUsuarioService usuarioService;
    private final JavaMailSender mailSender;
    private final Environment environment;

    public RegistroCompletoListener(IUsuarioService usuarioService, JavaMailSender mailSender, Environment environment) {
        this.usuarioService = usuarioService;
        this.mailSender = mailSender;
        this.environment = environment;
    }

    @NotNull
    static MimeMessage crearCorreoMime(JavaMailSender mailSender, String remitente, String destinatario, String asunto, String mensaje) throws MessagingException {
        MimeMessage correoMime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(correoMime, true, "UTF-8");

        helper.setFrom(remitente);
        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(mensaje, true);

        return correoMime;
    }

    @Override
    public void onApplicationEvent(OnRegistroCompletoEvent event) {
        String token = this.gestionarToken(event.getUsuario());
        String plantilla = this.llenarPlantillaDeCorreoElectronico(event.getUsuario(), token);

        try {
            MimeMessage correoMime = this.construirCorreoMime(event.getUsuario().getCorreoElectronico(), plantilla);
            this.mailSender.send(correoMime);
        } catch (MessagingException e) {
            throw new MalaSolicitudException("No se pudo enviar el correo electrónico de confirmación de registro.");
        }
    }

    private String gestionarToken(UsuarioEntity usuario) {
        String token = UUID.randomUUID().toString();
        this.usuarioService.crearTokenVerificacion(token, usuario);
        return token;
    }

    private String llenarPlantillaDeCorreoElectronico(UsuarioEntity usuario, String token) {
        String deployAws = this.environment.getProperty("aws");
        String plantilla = """
                <!doctype html>
                <html lang="es" xmlns="http://www.w3.org/1999/html">
                    <head>
                        <meta charset="utf-8" />
                        <title>Confirmación de Registro en SoundSeeker</title>
                        <meta name="viewport" content="width=device-width, initial-scale=1" />
                        <link rel="preconnect" href="https://fonts.googleapis.com" />
                        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin="" />
                        <link
                            href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&family=Plus+Jakarta+Sans:wght@600&display=swap"
                            rel="stylesheet" />
                    </head>
                    <body
                        style="
                            font-family: 'Inter', sans-serif;
                            font-weight: 400;
                            font-size: 0.875rem;
                            line-height: normal;
                            color: #000;
                            max-width: 600px;
                            background: #f6f7f9;
                            padding: 1.875rem;
                            margin: 0 auto;
                        ">
                        <header>
                            <h1 style="margin: 0; text-align: center">
                                <span
                                    style="
                                        font-family: 'Plus Jakarta Sans', sans-serif;
                                        font-weight: 600;
                                        font-size: 1.875rem;
                                        color: #3563e9;
                                    "
                                    >Sound</span
                                ><span
                                    style="
                                        font-family: 'Plus Jakarta Sans', sans-serif;
                                        font-weight: 600;
                                        font-size: 1.875rem;
                                        color: #292d32;
                                    "
                                    >Seeker</span
                                >
                            </h1>
                            <p
                                style="
                                    font-family: 'Plus Jakarta Sans', sans-serif;
                                    font-weight: 600;
                                    font-size: 0.75rem;
                                    margin-block-start: 0;
                                    margin-block-end: 1rem;
                                    color: #596780;
                                    text-align: center;
                                ">
                                Reservá tu melodía perfecta.
                            </p>
                        </header>
                        <main>
                            <section aria-labelledby="welcome-registry">
                                <h2 id="welcome-registry" style="text-align: center">
                                    ¡Bienvenido a la aventura, %s!
                                    <span aria-label="Estrella y notas musicales">🌟🎶</span>
                                </h2>
                                <p>
                                    ¡Estamos superemocionados de que te hayas unido a nuestra comunidad de amantes
                                    de la música en SoundSeeker! Tu presencia aquí es como una nota perfecta en
                                    nuestra sinfonía.
                                </p>
                                <p>
                                    Para comenzar con el pie derecho, solo necesitas confirmar tu cuenta. ¡Es un
                                    paso sencillo hacia un mundo de opciones!
                                </p>
                                <table
                                    style="
                                        text-align: center;
                                        width: 12.5rem;
                                        background-color: #3563e9;
                                        padding: 0.375rem;
                                        margin: 1rem auto;
                                    ">
                                    <tbody>
                                        <tr>
                                            <td>
                                                <a
                                                    href="%s/api/v1/usuarios/confirmacion?token=%s"
                                                    target="_blank"
                                                    rel="noopener noreferrer"
                                                    role="button"
                                                    style="color: #fff; font-weight: 500; text-decoration: none"
                                                    >Confirma tu cuenta ahora</a
                                                >
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                                <p>
                                    Recuerda, este enlace solo estará disponible durante las próximas 24 horas, no
                                    pierdas el ritmo y ¡activa tu cuenta ya!
                                </p>
                            </section>
                            <section aria-labelledby="experience">
                                <h3
                                    id="experience"
                                    style="
                                        font-size: 1.17rem;
                                        margin-bottom: 0.75rem;
                                        border-bottom: #d9d9d9 1px solid;
                                    ">
                                    Tu llave a la experiencia completa
                                </h3>
                                <p>Al confirmar, desbloquearás estas posibilidades esenciales:</p>
                                <ul>
                                    <li>
                                        <span style="font-weight: 600">Realizar Reservas:</span>
                                        Asegura tu producto preferido con nuestro sistema de reservas fácil de usar.
                                    </li>
                                    <li>
                                        <span style="font-weight: 600">Verificar Disponibilidad:</span>
                                        No te quedes con la duda. Revisa en tiempo real la disponibilidad de los
                                        productos que te interesan y planea con antelación.
                                    </li>
                                    <li>
                                        <span style="font-weight: 600">Añadir a Favoritos:</span>
                                        ¿Encontraste algo que te encanta? Añádelo a tus favoritos y mantenlo a la
                                        vista para futuras reservas o consultas.
                                    </li>
                                </ul>
                                <p>
                                    Estas funciones están diseñadas para que tu experiencia con nosotros sea lo más
                                    fluida y agradable posible.
                                </p>
                            </section>
                            <section aria-labelledby="tips-and-help">
                                <h3
                                    id="tips-and-help"
                                    style="
                                        font-size: 1.17rem;
                                        margin-bottom: 0.75rem;
                                        border-bottom: #d9d9d9 1px solid;
                                    ">
                                    Encuentra tu ritmo: Iniciar sesión y más
                                </h3>
                                <p>
                                    Cuando quieras iniciar sesión, simplemente utiliza tu nombre de usuario (<span
                                        style="font-weight: 600"
                                        >%s</span
                                    >) o correo electrónico (<span style="font-weight: 600">%s</span>)
                                    junto con tu contraseña y... ¡Listo! Estarás dentro.
                                </p>
                                <p>
                                    ¿Ansioso por empezar? ¡Nosotros también! No podemos esperar a ver qué descubres.
                                </p>
                                <p>
                                    Si en algún compás necesitas asistencia o tienes preguntas, no dudes en marcar
                                    nuestra nota de ayuda.
                                </p>
                            </section>
                            <section>
                                <h3 style="font-size: 1.17rem; text-align: center; margin: 0.5rem 0">
                                    ¡Un abrazo musical! <span aria-label="Confeti y saxofón">🎉🎷</span>
                                </h3>
                                <p style="text-align: center; margin-block: 0.5rem !important">El equipo de SoundSeeker</p>
                            </section>
                        </main>
                        <footer>
                            <table
                                role="presentation"
                                style="width: 100%s; background-color: #3563e9; padding: 0.375rem; margin-top: 1rem">
                                <tbody>
                                    <tr>
                                        <td style="text-align: center; padding: 0.375rem; width: 33%s">
                                            <a
                                                href="%s/categories"
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                style="color: #fff; font-weight: 500; text-decoration: none"
                                                >Explorar</a
                                            >
                                        </td>
                                        <td style="text-align: center; padding: 0.375rem; width: 33%s">
                                            <a
                                                href="%s/usuario"
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                style="color: #fff; font-weight: 500; text-decoration: none"
                                                >Mi cuenta
                                            </a>
                                        </td>
                                        <td style="text-align: center; padding: 0.375rem; width: 33%s">
                                            <a
                                                href="%s/contact"
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                style="color: #fff; font-weight: 500; text-decoration: none"
                                                >Contáctanos</a
                                            >
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                            <p style="font-size: 0.75rem; text-align: center">
                                © 2023 <span style="font-weight: 500">SoundSeeker</span>. Todos los derechos
                                reservados.
                            </p>
                        </footer>
                    </body>
                </html>
                 """;

        return String.format(
                plantilla,
                usuario.getNombre(),
                this.environment.getProperty("url.servidor"),
                token,
                usuario.getNombreUsuario(),
                usuario.getCorreoElectronico(),
                "%",
                "%",
                deployAws,
                "%",
                deployAws,
                "%",
                deployAws);
    }

    private MimeMessage construirCorreoMime(String destinatario, String mensaje) throws MessagingException {
        String remitente = this.environment.getProperty("correo.soporte");
        String asunto = "Confirmación de Registro en SoundSeeker";
        return crearCorreoMime(this.mailSender, remitente, destinatario, asunto, mensaje);
    }
}
