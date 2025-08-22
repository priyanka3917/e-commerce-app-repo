package com.userService.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {
    // BCrypt is strong hashing algorithm
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    // Private constructor to prevent instantiation
    private PasswordUtils() {}
    /**
     * Hashes the raw password using BCrypt
     */
    public static String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }
    /**
     * Checks if a raw password matches the hashed password
     */
    public static boolean matches(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}
