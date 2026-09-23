package com.portfolio.ecommerceproject.controller.exception;

import com.portfolio.ecommerceproject.dto.ResponseErrorDto;
import com.portfolio.ecommerceproject.service.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseErrorDto> handleResourceNotFound(ResourceNotFoundException e,
    HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ResponseErrorDto error = new ResponseErrorDto(
                Instant.now(),
                status.value(),
                "Resource not found",
                e.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseErrorDto> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<ResponseErrorDto.InvalidField> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(f -> new ResponseErrorDto.InvalidField(f.getField(), f.getDefaultMessage()))
                .toList();

        ResponseErrorDto error = new ResponseErrorDto(
                Instant.now(),
                status.value(),
                "Error validating fields",
                "One or more arguments are invalid",
                request.getRequestURI(),
                errors
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ResponseErrorDto> handleAuthenticationError(Exception e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ResponseErrorDto error = new ResponseErrorDto(
                Instant.now(),
                status.value(),
                "Authentication fail",
                "Email or password invalid",
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(status).body(error);
    }
}
