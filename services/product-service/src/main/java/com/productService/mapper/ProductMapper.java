package com.productService.mapper;

import com.productService.dto.request.ProductCreateRequestDTO;
import com.productService.dto.request.ProductUpdateRequestDTO;
import com.productService.dto.response.ProductResponseDTO;
import com.productService.entity.ProductEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity toEntity(ProductCreateRequestDTO r);

    ProductResponseDTO toResponse(ProductEntity p);

    List<ProductResponseDTO> toResponseList(List<ProductEntity> list);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(ProductUpdateRequestDTO r, @MappingTarget ProductEntity p);

}

