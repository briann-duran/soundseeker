package com.soundseeker.api.service.events;

import com.soundseeker.api.service.dto.ProductoDto;
import com.soundseeker.api.service.dto.ReservaDto;
import com.soundseeker.api.service.exception.MalaSolicitudException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.soundseeker.api.service.events.RegistroCompletoListener.crearCorreoMime;

@Component
public class ReservaCanceladaListener implements ApplicationListener<OnReservaCanceladaEvent> {
    private final JavaMailSender mailSender;
    private final Environment environment;

    @Autowired
    public ReservaCanceladaListener(JavaMailSender mailSender, Environment environment) {
        this.mailSender = mailSender;
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(OnReservaCanceladaEvent event) {
        String nombreProducto = event.getReserva().productos().stream().map(ProductoDto::getNombre).findFirst().orElseThrow();
        String plantilla = this.llenarPlantillaDeCorreoElectronico(event.getReserva(), nombreProducto);

        try {
            MimeMessage correo = this.construirCorreoMime(event.getReserva().cliente().correoElectronico(), plantilla);
            this.mailSender.send(correo);
        } catch (MessagingException e) {
            throw new MalaSolicitudException("No se pudo enviar el correo electrónico de confirmación de registro.");
        }
    }

    private String llenarPlantillaDeCorreoElectronico(ReservaDto reserva, String nombreProducto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        String deployAws = this.environment.getProperty("aws");
        String plantilla = """
                <!doctype html>
                <html lang="es" xmlns="http://www.w3.org/1999/html">
                    <head>
                        <meta charset="utf-8" />
                        <title>Confirmación de Cancelación de Reserva en SoundSeeker</title>
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
                            <section aria-labelledby="cancellation-success">
                                <p style="color: #404040">
                                    <span style="font-weight: 500">Reserva #:</span> %d |
                                    <span style="font-weight: 500">Fecha de reserva:</span> %s
                                </p>
                                <h2 id="cancellation-success">Confirmación de cancelación de reserva</h2>
                                <p>Hola <span style="font-weight: 500">David</span>,</p>
                                <p>
                                    En <span style="font-weight: 500">SoundSeeker</span>, creemos que la vida es
                                    como una melodía que a veces nos lleva por sorprendentes cambios de ritmo.
                                    Esperamos que, a pesar de los imprevistos que puedan surgir en tu sinfonía
                                    personal, este mensaje te encuentre en un momento de tranquilidad y armonía.
                                </p>
                                <p>
                                    Nos gustaría confirmarte que la cancelación de la reserva para tu
                                    <span style="font-weight: 600">%s</span>
                                    se ha procesado con toda la suavidad de un legato. No se realizarán cargos en tu
                                    cuenta, y puedes estar seguro de que tu reserva ha quedado anulada sin ninguna
                                    disonancia.
                                </p>
                            </section>
                            <section aria-labelledby="next-steps">
                                <h3
                                    id="next-steps"
                                    style="
                                        font-size: 1.17rem;
                                        margin-bottom: 0.75rem;
                                        border-bottom: #d9d9d9 1px solid;
                                    ">
                                    Siguientes pasos
                                </h3>
                                <p>
                                    Aunque no podamos acompañarte en tu viaje musical esta vez, nuestra colección de
                                    instrumentos siempre está a tu disposición. Cuando estés listo para que la
                                    música suene de nuevo, estaremos encantados de ayudarte a encontrar el
                                    instrumento perfecto para tus necesidades.
                                </p>
                            </section>
                            <section aria-labelledby="always-here">
                                <h3
                                    id="always-here"
                                    style="
                                        font-size: 1.17rem;
                                        margin-bottom: 0.75rem;
                                        border-bottom: #d9d9d9 1px solid;
                                    ">
                                    Siempre a tu servicio
                                </h3>
                                <p>
                                    Si tienes alguna pregunta o si hay algo en lo que podamos asistirte, por favor,
                                    no dudes en ponerte en contacto con nosotros. Nuestro equipo está aquí para
                                    asegurarse de que tu experiencia sea sin complicaciones y que la música nunca
                                    pare.
                                </p>
                                <p>
                                    Gracias por considerar a
                                    <span style="font-weight: 500">SoundSeeker</span> para tu aventura musical.
                                    Esperamos tener el placer de servirte en el futuro.
                                </p>
                            </section>
                            <section>
                                <h3 style="font-size: 1.17rem; text-align: center; margin: 0.5rem 0">
                                    ¡Un abrazo musical! <span aria-label="Confeti y saxofón">🎉🎷</span>
                                </h3>
                                <p style="text-align: center; margin: 0.5rem 0">El equipo de SoundSeeker</p>
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
                reserva.id(),
                reserva.fechaOrden().format(formatter),
                nombreProducto,
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
        String asunto = "Confirmación de Cancelación de Reserva en SoundSeeker";
        return crearCorreoMime(this.mailSender, remitente, destinatario, asunto, mensaje);
    }
}
