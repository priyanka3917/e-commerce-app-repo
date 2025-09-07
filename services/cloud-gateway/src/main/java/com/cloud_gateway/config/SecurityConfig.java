package com.cloud_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .formLogin(ServerHttpSecurity.FormLoginSpec::disable)   // ❌ no login form
                    .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)   // ❌ no basic auth
                    .authorizeExchange(exchanges -> exchanges
                            .pathMatchers(
                                    "/api/v1/users/login",
                                    "/api/v1/users/sign-up",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/v3/api-docs/**",
                                    "/webjars/**"
                            ).permitAll()
                            .anyExchange().authenticated()
                    )
                    .build();
        }
    }