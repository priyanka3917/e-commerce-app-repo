package com.productService.service;

import com.productService.dto.request.ProductCreateRequestDTO;
import com.productService.dto.request.ProductUpdateRequestDTO;
import com.productService.dto.request.ReserveRequestDTO;
import com.productService.dto.response.ProductResponseDTO;

import java.util.List;

public interface ProductService {
    ProductResponseDTO createProduct(ProductCreateRequestDTO dto);
    ProductResponseDTO getProductById(String id);
    List<ProductResponseDTO> getAllProducts();
    ProductResponseDTO updateProducts(String id, ProductUpdateRequestDTO dto);
    String deleteProductsById(String id);
    void reserveStock(ReserveRequestDTO requests);
    void releaseStock(String reservationId);
    void confirmReservation(String reservationId);
}
