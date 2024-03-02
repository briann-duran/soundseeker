package com.soundseeker.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class SoundSeekerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SoundSeekerApplication.class, args);
        LOGGER.info("🎵 SoundSeeker ha abierto al público");
    }
}
