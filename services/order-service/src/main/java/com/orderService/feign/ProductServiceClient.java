package com.orderService.feign;

import com.orderService.config.FeignConfig;
import com.orderService.dto.response.ProductResponseDTO;
import com.orderService.utils.GenericResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${feign.product.service.name}", configuration = FeignConfig.class)
public interface ProductServiceClient {

    @GetMapping("/api/v1/products/{id}")
    public ResponseEntity<GenericResponse<ProductResponseDTO>> getProductById(@PathVariable String id);
}
