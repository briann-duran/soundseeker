package com.soundseeker.api.service.exception;

public class ContrasenaNoCoincideException extends RuntimeException {
    public ContrasenaNoCoincideException(String message) {
        super(message);
    }
}
