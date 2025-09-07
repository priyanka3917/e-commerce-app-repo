package com.orderService.feign;

import com.orderService.config.FeignConfig;
import com.orderService.dto.response.GetOrUpdateUserByIdResponseDTO;
import com.orderService.fallback.UserServiceFallback;
import com.orderService.utils.GenericResponse;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "${feign.user.service.name}", configuration = FeignConfig.class, fallback = UserServiceFallback.class)
public interface UserServiceClient {

    @GetMapping("/api/v1/users/{id}")
    @CircuitBreaker(name = "orderServiceCB")
    @RateLimiter(name = "orderServiceRL")
    @Bulkhead(name = "orderServiceBH", type = Bulkhead.Type.SEMAPHORE)
    public ResponseEntity<GenericResponse<GetOrUpdateUserByIdResponseDTO>> getUserById(@PathVariable UUID id);
}
