package com.orderService.config;

import com.orderService.exception.CustomFeignErrorDecoder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class FeignConfig {

    @Bean
    public ErrorDecoder customFeignErrorDecoder() {
        return new CustomFeignErrorDecoder();
    }

    // ✅ Forward Authorization header (JWT) in all Feign calls
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
                    HttpServletRequest request = servletRequestAttributes.getRequest();
                    String authorizationHeader = request.getHeader("Authorization");
                    if (authorizationHeader != null) {
                        template.header("Authorization", authorizationHeader);
                    }
                }
            }
        };
    }
}
