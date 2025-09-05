package com.userService.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception exception, HttpServletRequest request) {
        if(exception instanceof AccessDeniedException || exception instanceof AuthorizationDeniedException){
            return buildErrorResponse(HttpStatus.valueOf(403), "You are not authorized to access this resource", request.getRequestURI());
        }
        if (exception instanceof BadCredentialsException) {
            return buildErrorResponse(HttpStatus.valueOf(403), "The username or password is incorrect", request.getRequestURI());
        }

        if (exception instanceof AccountStatusException) {
            return buildErrorResponse(HttpStatus.valueOf(403), "The account is locked", request.getRequestURI());
        }
        if (exception instanceof SignatureException) {
            return buildErrorResponse(HttpStatus.valueOf(403), "The JWT signature is invalid", request.getRequestURI());
        }

        if (exception instanceof ExpiredJwtException) {
            return buildErrorResponse(HttpStatus.valueOf(403), "The JWT token has expired", request.getRequestURI());
        }
        return buildErrorResponse(HttpStatus.valueOf(500), "Unknown internal server error.", request.getRequestURI());

    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(ValidationException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        // Extract field errors
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });
        // Convert field errors to a single message or keep them as a map
        String message = "Validation failed for fields: " + fieldErrors.toString();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message, String path) {
        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", message);
        errorDetails.put("path", path);

        return ResponseEntity.status(status).body(errorDetails);
    }
}