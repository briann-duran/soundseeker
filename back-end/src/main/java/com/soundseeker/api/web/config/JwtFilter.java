package com.soundseeker.api.web.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String cabeceraAutenticacion = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (cabeceraAutenticacion == null || !cabeceraAutenticacion.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = cabeceraAutenticacion.split(" ")[1].trim();
        if (!jwtUtil.esValido(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        String nombreUsuario = this.jwtUtil.obtenerNombreUsuario(jwt);
        User usuario = (User) this.userDetailsService.loadUserByUsername(nombreUsuario);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getAuthorities()
        );
        token.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(request, response);
    }
}
