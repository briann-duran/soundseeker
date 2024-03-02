package com.soundseeker.api.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(NombreDuplicadoException.class)
    public ResponseEntity<ExceptionDetail> handlerNombreDuplicadoException(NombreDuplicadoException e) {
        ExceptionDetail exceptionDetail = new ExceptionDetail(
                e.getMessage(),
                LocalDateTime.now(),
                HttpStatus.CONFLICT
        );
        return new ResponseEntity<>(exceptionDetail, exceptionDetail.getHttpStatus());
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ExceptionDetail> handlerRecursoNoEncontrado(RecursoNoEncontradoException e) {
        ExceptionDetail exceptionDetail = new ExceptionDetail(
                e.getMessage(),
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND
        );
        return new ResponseEntity<>(exceptionDetail, exceptionDetail.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDetail> handlerArgumentNotValidException(MethodArgumentNotValidException e) {
        Set<String> mensaje = new HashSet<>();
        e.getBindingResult()
                .getAllErrors()
                .forEach((error) -> mensaje.add("Campo '".concat(((FieldError) error).getField()
                        .concat("': ").concat(String.valueOf(error.getDefaultMessage())))));
        ExceptionDetail exceptionDetail = new ExceptionDetail(
                String.join(" | ", mensaje),
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST
        );
        return ResponseEntity.badRequest().body(exceptionDetail);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionDetail> handlerTypeMismatchException() {
        ExceptionDetail exceptionDetail = new ExceptionDetail(
                "El tipo de dato recibido no puede ser convertido al requerido.",
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST
        );
        return ResponseEntity.badRequest().body(exceptionDetail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionDetail> handlerNotReadableException() {
        ExceptionDetail exceptionDetail = new ExceptionDetail(
                "El cuerpo de la solicitud no pudo ser leído.",
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST
        );
        return ResponseEntity.badRequest().body(exceptionDetail);
    }

    @ExceptionHandler(ContrasenaNoCoincideException.class)
    public ResponseEntity<ExceptionDetail> handlerContrasenaNoCoincide(ContrasenaNoCoincideException e) {
        ExceptionDetail exceptionDetail = new ExceptionDetail(
                e.getMessage(),
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST
        );
        return ResponseEntity.badRequest().body(exceptionDetail);
    }

    @ExceptionHandler(TokenExpiradoException.class)
    public ResponseEntity<ExceptionDetail> handlerTokenExpirado(TokenExpiradoException e) {
        ExceptionDetail exceptionDetail = new ExceptionDetail(
                e.getMessage(),
                LocalDateTime.now(),
                HttpStatus.CONFLICT
        );
        return new ResponseEntity<>(exceptionDetail, exceptionDetail.getHttpStatus());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionDetail> handlerAuthentication(AuthenticationException e) {
        ExceptionDetail exceptionDetail = new ExceptionDetail(
                e.getMessage(),
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN
        );
        return new ResponseEntity<>(exceptionDetail, exceptionDetail.getHttpStatus());
    }

    @ExceptionHandler(MalaSolicitudException.class)
    public ResponseEntity<ExceptionDetail> handlerMalaSolicitud(MalaSolicitudException e) {
        ExceptionDetail exceptionDetail = new ExceptionDetail(
                e.getMessage(),
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN
        );
        return new ResponseEntity<>(exceptionDetail, exceptionDetail.getHttpStatus());
    }
}
