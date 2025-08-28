package com.orderService.feign;

import com.orderService.config.FeignConfig;
import com.orderService.dto.response.ProductResponseDTO;
import com.orderService.utils.GenericResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "${feign.product.service.name}", configuration = FeignConfig.class)
public interface ProductServiceClient {

    @GetMapping("/api/v1/products/{id}")
    @CircuitBreaker(name = "productServiceCB", fallbackMethod = "getProductFallback")
    public ResponseEntity<GenericResponse<ProductResponseDTO>> getProductById(@PathVariable String id);

    @PutMapping("/api/v1/products/{productId}/reserve")
    @CircuitBreaker(name = "productServiceCB", fallbackMethod = "reserveStockFallback")
    void reserveStock(@PathVariable String productId, @RequestParam int quantity, @RequestParam String reservationId);

    @PutMapping("/api/v1/products/release")
    @CircuitBreaker(name = "productServiceCB", fallbackMethod = "releaseStockFallback")
    void releaseStock(@RequestParam("reservationId") String reservationId);

    @PutMapping("/api/v1/products/confirm")
    @CircuitBreaker(name = "productServiceCB", fallbackMethod = "confirmReservationFallback")
    void confirmReservation(@RequestParam String reservationId);

//    @RateLimiter(name = "productServiceRateLimiter", fallbackMethod = "getProductFallback")
//    @Bulkhead(name = "productServiceBulkhead", type = Bulkhead.Type.SEMAPHORE, fallbackMethod = "getProductFallback")
//    @CircuitBreaker(name = "productServiceCB", fallbackMethod = "getProductFallback")
//    ResponseEntity<GenericResponse<ProductResponseDTO>> getProductById(String id);

}
