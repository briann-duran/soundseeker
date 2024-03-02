package com.soundseeker.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(parallel = true)
@TestConfiguration(proxyBeanMethods = false)
public class TestSoundSeekerApplication {
    public static void main(String[] args) {
        SpringApplication.from(SoundSeekerApplication::main).with(TestSoundSeekerApplication.class).run(args);
    }

    @Bean
    @ServiceConnection
    MySQLContainer<?> mysqlContainer() {
        try (MySQLContainer<?> container = new MySQLContainer<>(DockerImageName.parse("mysql:latest"))) {
            container.withDatabaseName("soundseeker");
            return container;
        } catch (Exception e) {
            System.err.println("No se pudo crear el contenedor de MySQL: " + e.getMessage());
        }
        return null;
    }
}