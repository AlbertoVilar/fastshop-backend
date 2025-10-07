package com.fastshop.mappers;

import com.fastshop.dto.ProductRequestDTO;
import com.fastshop.dto.ProductResponseDTO;
import com.fastshop.entities.Category;
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
                .price(product.getUnitPrice() != null ? product.getUnitPrice() : product.getPrice())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .build();
    }

    // Converte um DTO de requisição para uma entidade (usado no POST/PUT)
    public Product toEntity(ProductRequestDTO dto, Category  category) {
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .unitPrice(dto.getPrice())
                .stock(dto.getStock())
                .imageUrl(dto.getImageUrl())
                .category(category)
                .build();
    }

    public void updateEntityFromDTO(ProductRequestDTO dto, Product product) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setUnitPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImageUrl(dto.getImageUrl());
    }

}
