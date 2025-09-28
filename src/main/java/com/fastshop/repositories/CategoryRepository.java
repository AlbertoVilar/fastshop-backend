package com.fastshop.repositories;

import com.fastshop.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Métodos customizados podem ser adicionados aqui
}

