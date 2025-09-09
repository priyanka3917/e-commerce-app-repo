package com.productService.controller;

import com.productService.dto.request.ProductCreateRequestDTO;
import com.productService.dto.request.ProductUpdateRequestDTO;
import com.productService.dto.request.ReserveRequestDTO;
import com.productService.dto.response.ProductResponseDTO;
import com.productService.service.ProductService;
import com.productService.utils.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    @Autowired
    ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new Product.")
    public ResponseEntity<GenericResponse<ProductResponseDTO>> createProduct(@Valid @RequestBody ProductCreateRequestDTO req) {
        return ResponseEntity.ok(GenericResponse.success(productService.createProduct(req)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product information by id")
    public ResponseEntity<GenericResponse<ProductResponseDTO>> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(GenericResponse.success(productService.getProductById(id)));
    }

    @GetMapping
    @Operation(summary = "Get all the products")
    public ResponseEntity<GenericResponse<List<ProductResponseDTO>>> getAllProducts() {
        return ResponseEntity.ok(GenericResponse.success(productService.getAllProducts()));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update Product")
    public ResponseEntity<GenericResponse<ProductResponseDTO>> updateProduct(@PathVariable String id, @RequestBody ProductUpdateRequestDTO req) {
        return ResponseEntity.ok(GenericResponse.success(productService.updateProducts(id,req)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product by id.")
    public ResponseEntity<GenericResponse<String>> delete(@PathVariable String id) {
        return ResponseEntity.ok(GenericResponse.success(productService.deleteProductsById(id)));
    }
    @PutMapping("/reserve")
    @Operation(summary = "Reserve Stock")
    public ResponseEntity<GenericResponse<Void>> reserveStock(
            @RequestBody ReserveRequestDTO request) {
        productService.reserveStock(request);
        return ResponseEntity.ok(GenericResponse.success(null));
    }

    @PutMapping("/release")
    public ResponseEntity<GenericResponse<Void>> releaseStock(@RequestParam String reservationId) {
        productService.releaseStock(reservationId);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/confirm")
    public ResponseEntity<GenericResponse<Void>> confirmReservation(@RequestParam String reservationId) {
        productService.confirmReservation(reservationId);
        return ResponseEntity.ok().build();
    }


}
