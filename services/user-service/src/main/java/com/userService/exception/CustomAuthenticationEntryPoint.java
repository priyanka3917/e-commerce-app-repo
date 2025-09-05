package com.userService.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ProblemDetail problemDetail;
        if (authException instanceof InvaliJwtAuthenticationExpetion) {
            // Case: Wrong/malformed/expired token
            problemDetail = ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid JWT token"
            );
            problemDetail.setProperty("description", "The provided token is invalid or malformed");
        }
        else if (authException instanceof BadCredentialsException) {
            // Case: Wrong username/password during login
            problemDetail = ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password"
            );
            problemDetail.setProperty("description", "The username or password you entered is incorrect");
        }
        else {
            // Case: No token at all, or generic missing auth
            problemDetail = ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNAUTHORIZED,
                    "Full authentication is required to access this resource"
            );
            problemDetail.setProperty("description", "You must provide a token or a valid token to access this resource");
        }
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        response.setStatus(problemDetail.getStatus());
        response.setContentType("application/json");

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(problemDetail));

    }
}
