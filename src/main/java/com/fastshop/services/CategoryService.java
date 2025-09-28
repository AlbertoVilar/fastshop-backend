package com.fastshop.services;

import com.fastshop.dto.CategoryRequestDTO;
import com.fastshop.dto.CategoryResponseDTO;
import java.util.List;

public interface CategoryService {
    CategoryResponseDTO createCategory(CategoryRequestDTO dto);
    CategoryResponseDTO getCategoryById(Long id);
    List<CategoryResponseDTO> getAllCategories();
    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto);
    void deleteCategory(Long id);
}

