package com.soundseeker.api.service.events;

import com.soundseeker.api.persistence.entity.ProductoEntity;
import com.soundseeker.api.persistence.entity.ReservaEntity;
import com.soundseeker.api.persistence.entity.UsuarioEntity;
import com.soundseeker.api.persistence.repository.ProductoRepository;
import com.soundseeker.api.persistence.repository.ReservaRepository;
import com.soundseeker.api.persistence.repository.UsuarioRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import static com.soundseeker.api.service.events.RegistroCompletoListener.crearCorreoMime;

@Component
public class ReservaCompletaListener implements ApplicationListener<OnReservaCompletaEvent> {
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final JavaMailSender mailSender;
    private final Environment environment;

    @Autowired
    public ReservaCompletaListener(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository, ProductoRepository productoRepository, JavaMailSender mailSender, Environment environment) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.mailSender = mailSender;
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(OnReservaCompletaEvent event) {
        ReservaEntity reserva = this.reservaRepository.findById(event.getReserva().id()).orElseThrow();
        UsuarioEntity usuario = this.usuarioRepository.findById(reserva.getUsuario().getNombreUsuario()).orElseThrow();
        ProductoEntity producto = this.productoRepository.findById(reserva.getProductos().stream().findFirst().orElseThrow().getId()).orElseThrow();

        String plantilla = this.llenarPlantillaDeCorreoElectronico(reserva, usuario, producto);

        try {
            MimeMessage correoMime = this.construirCorreoMime(usuario.getCorreoElectronico(), plantilla);
            this.mailSender.send(correoMime);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String llenarPlantillaDeCorreoElectronico(ReservaEntity reserva, UsuarioEntity usuario, ProductoEntity producto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        String fechaReserva = reserva.getFechaOrden().format(formatter);
        String fechaRetiro = reserva.getFechaRetiro().format(formatter);
        String fechaEntrega = reserva.getFechaEntrega().format(formatter);
        String nombreProducto = producto.getNombre();
        String deployAws = this.environment.getProperty("aws");
        String urlImagen = producto.getImagenes().stream().filter(Objects::nonNull).findFirst().orElse("");

        String plantilla = """
                <!doctype html>
                <html lang="es" xmlns="http://www.w3.org/1999/html">
                    <head>
                        <meta charset="utf-8" />
                        <title>Confirmación de Reserva en SoundSeeker</title>
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
                            <section aria-labelledby="reservation-confirmation">
                                <p style="color: #404040">
                                    <span style="font-weight: 500">Reserva #:</span> %d |
                                    <span style="font-weight: 500">Fecha de reserva:</span> %s
                                </p>
                                <h2 id="reservation-confirmation">Tu reserva se ha confirmado</h2>
                                <p>Hola <span style="font-weight: 500">%s</span>,</p>
                                <p>¡Gracias por reservar con nosotros!</p>
                                <p>
                                    Puedes retirar tu instrumento el
                                    <span style="font-weight: 500">%s</span>
                                    desde las 8:00 a. m., recuerda que debes entregarlo el
                                    <span style="font-weight: 500">%s</span> antes de las 5:00 p. m., que es el día
                                    de finalización de tu reserva.
                                </p>
                                <p>A continuación verás un resumen de toda la reserva.</p>
                            </section>
                            <section aria-labelledby="reservation-details">
                                <h3
                                    id="reservation-details"
                                    style="
                                        font-size: 1.17rem;
                                        margin-bottom: 0.75rem;
                                        border-bottom: #d9d9d9 1px solid;
                                    ">
                                    En esta reserva
                                </h3>
                                <table>
                                    <tbody>
                                        <tr>
                                            <td rowspan="4">
                                                <div
                                                    style="
                                                        width: 9.375rem;
                                                        height: 9.375rem;
                                                        display: flex;
                                                        justify-content: center;
                                                        align-items: center;
                                                        background-color: #fff;
                                                        margin-right: 1rem;
                                                    ">
                                                    <img
                                                        alt="%s"
                                                        src="%s%s"
                                                        style="max-width: 100%s; max-height: 100%s" />
                                                </div>
                                            </td>
                                            <td>
                                                <span style="font-size: 1.17rem; font-weight: 600">%s</span>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <span style="font-weight: 600">Artículo No.:</span>
                                                %d
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <span style="font-weight: 600">Marca:</span>
                                                %s
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <span style="font-weight: 600">Descripción:</span>
                                                %s
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </section>
                            <section aria-labelledby="billing-summary">
                                <h3
                                    id="billing-summary"
                                    style="
                                        font-size: 1.17rem;
                                        margin-bottom: 0.75rem;
                                        border-bottom: #d9d9d9 1px solid;
                                    ">
                                    Resumen de facturación
                                </h3>
                                <table>
                                    <tbody>
                                        <tr>
                                            <td>
                                                <span style="font-weight: 600">Fecha de retiro en tienda:</span>
                                                %s
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <span style="font-weight: 600">Fecha de entrega en tienda:</span>
                                                %s
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <span style="font-weight: 600">Número de días reservados:</span>
                                                %d días
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <span style="font-weight: 600">Valor diario:</span>
                                                $ %.2f
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <span style="font-weight: 600">Valor total de la reserva:</span>
                                                $ %.2f
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </section>
                        </main>
                        <footer>
                            <table
                                role="presentation"
                                style="background-color: #3563e9; padding: 0.375rem; margin-top: 1rem; width: 100%s">
                                <tbody>
                                    <tr>
                                        <td style="text-align: center">
                                            <a
                                                href="%s/alquileres"
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                style="color: #fff; font-weight: 500; text-decoration: none"
                                                >Mis reservas</a
                                            >
                                        </td>
                                        <td style="text-align: center">
                                            <a
                                                href="%s/usuario"
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                style="color: #fff; font-weight: 500; text-decoration: none"
                                                >Mi cuenta
                                            </a>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="text-align: center">
                                            <a
                                                href="%s/contact"
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                style="color: #fff; font-weight: 500; text-decoration: none"
                                                >Contáctanos</a
                                            >
                                        </td>
                                        <td style="text-align: center">
                                            <a
                                                href="%s/politicas"
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                style="color: #fff; font-weight: 500; text-decoration: none"
                                                >Políticas</a
                                            >
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                            <p style="font-size: 0.75rem; text-align: center">
                                © 2023 <span style="font-weight: 500">SoundSeeker</span>. Todos los
                                derechos reservados.
                            </p>
                        </footer>
                    </body>
                </html>
                """;

        return String.format(
                plantilla,
                reserva.getId(),
                fechaReserva,
                usuario.getNombre(),
                fechaRetiro,
                fechaEntrega,
                nombreProducto,
                this.environment.getProperty("url.servidor.s3"),
                urlImagen,
                "%",
                "%",
                nombreProducto,
                producto.getId(),
                producto.getMarca(),
                producto.getDescripcion(),
                fechaRetiro,
                fechaEntrega,
                reserva.getFechaRetiro().datesUntil(reserva.getFechaEntrega()).count(),
                producto.getPrecio(),
                producto.getPrecio() * (int) reserva.getFechaRetiro().datesUntil(reserva.getFechaEntrega()).count(),
                "%",
                deployAws,
                deployAws,
                deployAws,
                deployAws
        );
    }

    private MimeMessage construirCorreoMime(String destinatario, String mensaje) throws MessagingException {
        String remitente = this.environment.getProperty("correo.soporte");
        String asunto = "Confirmación de Reserva en SoundSeeker";
        return crearCorreoMime(this.mailSender, remitente, destinatario, asunto, mensaje);
    }
}
