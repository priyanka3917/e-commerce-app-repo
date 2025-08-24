package com.orderService.mapper;

import com.productService.dto.request.ProductCreateRequestDTO;
import com.productService.dto.request.ProductUpdateRequestDTO;
import com.productService.dto.response.ProductResponseDTO;
import com.productService.entity.ProductEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductEntity toEntity(ProductCreateRequestDTO r);

    ProductResponseDTO toResponse(ProductEntity p);

    List<ProductResponseDTO> toResponseList(List<ProductEntity> list);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(ProductUpdateRequestDTO r, @MappingTarget ProductEntity p);

}
