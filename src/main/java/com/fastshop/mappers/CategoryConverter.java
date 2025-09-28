package com.fastshop.mappers;

import com.fastshop.dto.CategoryRequestDTO;
import com.fastshop.dto.CategoryResponseDTO;
import com.fastshop.entities.Category;
import com.fastshop.entities.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryConverter {
    private final ProductConverter productConverter;

    @Autowired
    public CategoryConverter(ProductConverter productConverter) {
        this.productConverter = productConverter;
    }

    public Category fromDTO(CategoryRequestDTO dto, List<Product> products) {
        return Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .products(products)
                .build();
    }

    public CategoryResponseDTO toResponseDTO(Category entity) {
        return CategoryResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .products(entity.getProducts() == null ? List.of() :
                    entity.getProducts().stream()
                        .map(productConverter::toResponseDTO)
                        .collect(Collectors.toList()))
                .build();
    }
}
