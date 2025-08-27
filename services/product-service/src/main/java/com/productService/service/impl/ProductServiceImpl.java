package com.productService.service.impl;

import com.productService.dto.request.ProductCreateRequestDTO;
import com.productService.dto.request.ProductUpdateRequestDTO;
import com.productService.dto.response.ProductResponseDTO;
import com.productService.entity.ProductEntity;
import com.productService.entity.StockReservationEntity;
import com.productService.enums.ReservationStatus;
import com.productService.exception.ValidationException;
import com.productService.mapper.ProductMapper;
import com.productService.repository.ProductRepository;
import com.productService.repository.StockReservationRepository;
import com.productService.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    StockReservationRepository stockReservationRepository;

    @Override
    public ProductResponseDTO createProduct(ProductCreateRequestDTO req) {
        if (productRepository.existsByName(req.name()))
            throw new ValidationException("Product name already exists");
        ProductEntity entity = productMapper.toEntity(req);
        return productMapper.toResponse(productRepository.save(entity));
    }

    @Override
    public ProductResponseDTO getProductById(String id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Product Not found for the id: " + id));
        return productMapper.toResponse(entity);
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productMapper.toResponseList(productRepository.findAll());
    }

    @Override
    public ProductResponseDTO updateProducts(String id, ProductUpdateRequestDTO req) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Product Not found for id: " + id));
        productMapper.updateFromDto(req, entity);
        return productMapper.toResponse(productRepository.save(entity));
    }

    @Override
    public String deleteProductsById(String id) {
        if(!productRepository.existsById(id)){
            throw new ValidationException("Product not found with id: "+ id);
        }
        productRepository.deleteById(id);
        return "Product Deleted Successfully " + id;
    }

    @Override
    public void reserveStock(String productId, int quantity, String reservationId) {
        if (stockReservationRepository.findByReservationId(reservationId).isPresent()) {
            return; // Already reserved
        }

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        StockReservationEntity reservation = new StockReservationEntity();
        reservation.setReservationId(reservationId);
        reservation.setProductId(productId);
        reservation.setQuantity(quantity);
        reservation.setReservedAt(Instant.now());
        reservation.setStatus(ReservationStatus.PENDING);
        stockReservationRepository.save(reservation);
    }

    @Override
    public void releaseStock(String reservationId) {
        StockReservationEntity reservation = stockReservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new ValidationException("Reservation not found"));

        if (reservation.getStatus()== ReservationStatus.RELEASED) return;

        ProductEntity product = productRepository.findById(reservation.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStock(product.getStock() + reservation.getQuantity());
        productRepository.save(product);

        reservation.setStatus(ReservationStatus.RELEASED);
        stockReservationRepository.save(reservation);

    }
    public void confirmReservation(String reservationId) {
        StockReservationEntity reservation = stockReservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(ReservationStatus.RESERVED);
        stockReservationRepository.save(reservation);
    }


    public void reduceStock(String id, int qty) {
        ProductEntity p = productRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Product not found with id: "+ id));
        if (p.getStock() < qty)
            throw new ValidationException("Insufficient stock");
        p.setStock(p.getStock() - qty);
        productRepository.save(p);
    }
}
