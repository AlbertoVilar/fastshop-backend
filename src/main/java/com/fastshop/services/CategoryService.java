package com.fastshop.services;

import com.fastshop.dto.CategoryRequestDTO;
import com.fastshop.dto.CategoryResponseDTO;
import com.fastshop.entities.Category;
import com.fastshop.mappers.CategoryConverter;
import com.fastshop.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryConverter categoryConverter;

    public CategoryService(CategoryRepository categoryRepository, CategoryConverter categoryConverter) {
        this.categoryRepository = categoryRepository;
        this.categoryConverter = categoryConverter;
    }

    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("O objeto DTO não pode ser nulo");
        }
        Category category = categoryConverter.fromDTO(dto);
        category = categoryRepository.save(category);

        return categoryConverter.toResponseDTO(category); // Retornar a categoria criada
    }

    public List<CategoryResponseDTO> searchAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryConverter::toResponseDTO)
                .toList();
    }

    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new com.fastshop.exceptions.ResourceNotFoundException("Categoria não encontrada: " + id));
        return categoryConverter.toResponseDTO(category);
    }

    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new com.fastshop.exceptions.ResourceNotFoundException("Categoria não encontrada: " + id));
        categoryConverter.updateEntityFromDTO(category, dto);
        category = categoryRepository.save(category);
        return categoryConverter.toResponseDTO(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new com.fastshop.exceptions.ResourceNotFoundException("Categoria não encontrada: " + id));
        categoryRepository.delete(category);
    }
}
