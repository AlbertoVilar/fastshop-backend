package com.fastshop.mappers;

import com.fastshop.dto.ProductRequestDTO;
import com.fastshop.dto.ProductResponseDTO;
import com.fastshop.entities.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {

    // Converte uma entidade para um DTO de resposta (usado no GET, POST de retorno, etc.)
    public ProductResponseDTO toResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .build();
    }

    // Converte um DTO de requisição para uma entidade (usado no POST/PUT)
    public Product toEntity(ProductRequestDTO dto) {
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .imageUrl(dto.getImageUrl())
                .build();
    }
}
