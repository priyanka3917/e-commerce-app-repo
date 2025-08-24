package com.productService.repository;

import com.productService.entity.ProductEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<ProductEntity, String> {
    boolean existsByName(String name);
    Optional<ProductEntity> findByName(String name);
}
