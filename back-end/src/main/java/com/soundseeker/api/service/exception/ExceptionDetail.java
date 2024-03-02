package com.soundseeker.api.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ExceptionDetail {
    private String message;
    private LocalDateTime timestamp;
    private HttpStatus httpStatus;
}
