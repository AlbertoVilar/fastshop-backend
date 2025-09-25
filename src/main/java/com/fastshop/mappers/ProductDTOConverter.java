package com.fastshop.mappers;

import com.fastshop.dto.ProductRequestDTO;
import com.fastshop.dto.ProductResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductDTOConverter {

    public ProductRequestDTO toRequestDTO(ProductResponseDTO product) {
        return ProductRequestDTO.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .build();
    }

    public ProductResponseDTO toResponseDTO(ProductRequestDTO requestDTO) {
        return ProductResponseDTO.builder()
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .price(requestDTO.getPrice())
                .stock(requestDTO.getStock())
                .imageUrl(requestDTO.getImageUrl())
                .build();
    }
}
