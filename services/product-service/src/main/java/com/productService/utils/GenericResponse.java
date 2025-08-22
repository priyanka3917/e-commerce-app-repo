package com.productService.utils;

import com.productService.exception.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class GenericResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Timestamp timestamp;
    private String statusCode;

    private Map<String,String> errors;
    public static <T> GenericResponse<T> empty() {
        return success(null);
    }
    public static <T> GenericResponse<T> success(T data) {
        return GenericResponse.<T>builder()
                .message("SUCCESS!")
                .data(data)
                .statusCode("200")
                .success(true).timestamp(Timestamp.from(Instant.now()))
                .build();
    }
    public static <T> GenericResponse<T> error(ValidationException e, Map<String,String> errors) {
        return GenericResponse.<T>builder()
                .message(e.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.toString())
                .errors(errors)
                .success(false).timestamp(Timestamp.from(Instant.now()))
                .build();
    }
}
