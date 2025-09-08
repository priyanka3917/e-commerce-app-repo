package com.orderService.feign;

import com.orderService.config.FeignConfig;
import com.orderService.dto.request.ReserveRequestDTO;
import com.orderService.dto.response.ProductResponseDTO;
import com.orderService.fallback.ProductServiceFallback;
import com.orderService.utils.GenericResponse;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "${feign.product.service.name}", configuration = FeignConfig.class, fallback = ProductServiceFallback.class)
public interface ProductServiceClient {

    @GetMapping("/api/v1/products/{id}")
    @CircuitBreaker(name = "orderServiceCB")
    @RateLimiter(name = "orderServiceRL")
    @Bulkhead(name = "orderServiceBH", type = Bulkhead.Type.SEMAPHORE)
    public ResponseEntity<GenericResponse<ProductResponseDTO>> getProductById(@PathVariable String id);

    @PutMapping("/api/v1/products/reserve")
    @CircuitBreaker(name = "orderServiceCB")
    @RateLimiter(name = "orderServiceRL")
    @Bulkhead(name = "orderServiceBH", type = Bulkhead.Type.SEMAPHORE)
    void reserveStock(@RequestBody ReserveRequestDTO request);

    @PutMapping("/api/v1/products/release")
    @CircuitBreaker(name = "orderServiceCB")
    @RateLimiter(name = "orderServiceRL")
    @Bulkhead(name = "orderServiceBH", type = Bulkhead.Type.SEMAPHORE)
    void releaseStock(@RequestParam("reservationId") String reservationId);

    @PutMapping("/api/v1/products/confirm")
    @CircuitBreaker(name = "orderServiceCB")
    @RateLimiter(name = "orderServiceRL")
    @Bulkhead(name = "orderServiceBH", type = Bulkhead.Type.SEMAPHORE)
    void confirmReservation(@RequestParam String reservationId);

}
