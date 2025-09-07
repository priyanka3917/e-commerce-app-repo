package com.productService.exception;

import org.springframework.security.core.AuthenticationException;

public class InvaliJwtAuthenticationExpetion extends AuthenticationException {
    public InvaliJwtAuthenticationExpetion(String message) {
        super(message);
    }
}
