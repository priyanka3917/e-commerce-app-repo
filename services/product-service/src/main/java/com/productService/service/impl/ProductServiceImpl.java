package com.productService.service.impl;

import com.productService.dto.request.ProductCreateRequestDTO;
import com.productService.dto.request.ProductUpdateRequestDTO;
import com.productService.dto.request.ReserveItemDTO;
import com.productService.dto.request.ReserveRequestDTO;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
        if (!productRepository.existsById(id)) {
            throw new ValidationException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        return "Product Deleted Successfully " + id;
    }

    @Override
    @Transactional
    public void reserveStock(ReserveRequestDTO request) {
        String reservationId = request.getReservationId();

        // Step 1: Validate all products have enough stock
        for (ReserveItemDTO item : request.getItems()) {
            Optional<StockReservationEntity> existing = stockReservationRepository
                    .findByReservationIdAndProductId(reservationId, item.getProductId());
            if (existing.isPresent()) {
                // Idempotent behavior → skip duplicate
                continue;
            }
            ProductEntity product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + item.getProductId());
            }
        }
        // Step 2: Deduct stock & create reservations
        for (ReserveItemDTO item : request.getItems()) {
            Optional<StockReservationEntity> existing = stockReservationRepository
                    .findByReservationIdAndProductId(reservationId, item.getProductId());
            if (existing.isPresent()) {
                // Idempotent behavior → skip duplicate
                continue;
            }
            ProductEntity product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            StockReservationEntity reservation = new StockReservationEntity();
            reservation.setReservationId(reservationId);
            reservation.setProductId(item.getProductId());
            reservation.setQuantity(item.getQuantity());
            reservation.setReservedAt(Instant.now());
            reservation.setStatus(ReservationStatus.PENDING);
            stockReservationRepository.save(reservation);
        }
    }

    @Override
    public void releaseStock(String reservationId) {
        List<StockReservationEntity> reservations =
                stockReservationRepository.findAllByReservationId(reservationId);

        if (reservations.isEmpty()) {
            throw new ValidationException("Reservation not found for id: " + reservationId);
        }

        for (StockReservationEntity reservation : reservations) {
            if (reservation.getStatus() == ReservationStatus.RELEASED) {
                continue; // already released, idempotent
            }

            ProductEntity product = productRepository.findById(reservation.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + reservation.getProductId()));

            product.setStock(product.getStock() + reservation.getQuantity());
            productRepository.save(product);

            reservation.setStatus(ReservationStatus.RELEASED);
            stockReservationRepository.save(reservation);
        }

    }

    public void confirmReservation(String reservationId) {
        List<StockReservationEntity> reservations =
                stockReservationRepository.findAllByReservationId(reservationId);

        if (reservations.isEmpty()) {
            throw new RuntimeException("Reservation not found for id: " + reservationId);
        }

        for (StockReservationEntity reservation : reservations) {
            if (reservation.getStatus() == ReservationStatus.RESERVED) {
                continue; // already confirmed, idempotent
            }

            reservation.setStatus(ReservationStatus.RESERVED);
            stockReservationRepository.save(reservation);
        }
    }
}
