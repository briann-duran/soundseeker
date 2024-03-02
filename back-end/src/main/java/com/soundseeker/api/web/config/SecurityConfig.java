package com.soundseeker.api.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Autowired
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        matcherRegistry -> matcherRegistry
                                .requestMatchers(HttpMethod.POST, "/uploadImg").permitAll()
                                .requestMatchers("/swagger/**").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/api/v1/usuarios/**").permitAll()
                                .requestMatchers("/api/v1/autenticacion/**").permitAll()
                                .requestMatchers("/api/v1/reservas/**").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/v1/instrumentos/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/categorias/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/caracteristicas/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/politicas/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/clientes/{nombreUsuario}").authenticated()
                                .requestMatchers(HttpMethod.GET, "/img/**").permitAll()
                                .requestMatchers("/api/v1/**").hasRole("ADMIN")
                                .anyRequest()
                                .authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager proveedorAutenticacion(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider proveedor = new DaoAuthenticationProvider(this.passwordEncoder());
        proveedor.setUserDetailsService(userDetailsService);
        return new ProviderManager(proveedor);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }
}
