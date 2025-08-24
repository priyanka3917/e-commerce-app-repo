package com.productService.service.impl;

import com.productService.dto.request.ProductCreateRequestDTO;
import com.productService.dto.request.ProductUpdateRequestDTO;
import com.productService.dto.response.ProductResponseDTO;
import com.productService.entity.ProductEntity;
import com.productService.exception.ValidationException;
import com.productService.mapper.ProductMapper;
import com.productService.repository.ProductRepository;
import com.productService.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductMapper productMapper;

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
            throw new ValidationException("product not found with id: "+ id);
        }
        productRepository.deleteById(id);
        return "Product Deleted Successfully " + id;
    }

    public void reduceStock(String id, int qty) {
        ProductEntity p = productRepository.findById(id)
                .orElseThrow(() -> new ValidationException("product not found with id: "+ id));
        if (p.getStock() < qty)
            throw new ValidationException("Insufficient stock");
        p.setStock(p.getStock() - qty);
        productRepository.save(p);
    }
}
