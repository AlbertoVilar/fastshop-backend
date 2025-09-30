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

    public Category fromDTO(CategoryRequestDTO dto) {
        return Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    public CategoryResponseDTO toResponseDTO(Category entity) {
        return CategoryResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }

    public void updateEntityFromDTO(Category category, CategoryRequestDTO dto) {
        if (dto.getName() != null) {
            category.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            category.setDescription(dto.getDescription());
        }
        // Se quiser atualizar produtos, adicione lógica aqui
    }
}
