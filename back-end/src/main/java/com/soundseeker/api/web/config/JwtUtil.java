package com.soundseeker.api.web.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {
    private static final String LLAVE_SECRETA = "$0und%$e3k3r|4ppl1c4ti0n";
    private static final Algorithm ALGORITMO = Algorithm.HMAC256(LLAVE_SECRETA);

    public String crear(String nombreUsuario) {
        return JWT.create()
                .withIssuer("soundseeker")
                .withSubject(nombreUsuario)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30)))
                .sign(ALGORITMO);
    }

    public boolean esValido(String jwt) {
        try {
            JWT.require(ALGORITMO)
                    .build()
                    .verify(jwt);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public String obtenerNombreUsuario(String jwt) {
        return JWT.require(ALGORITMO)
                .build()
                .verify(jwt)
                .getSubject();
    }
}
