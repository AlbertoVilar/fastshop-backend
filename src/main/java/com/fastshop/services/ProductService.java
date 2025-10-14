package com.fastshop.services;

import com.fastshop.dto.OrderRequestDTO;
import com.fastshop.dto.OrderResponseDTO;
import com.fastshop.dto.ProductRequestDTO;
import com.fastshop.dto.ProductUpdateDTO;
import com.fastshop.dto.ProductResponseDTO;
import com.fastshop.entities.Category;
import com.fastshop.entities.Order;
import com.fastshop.entities.Product;
import com.fastshop.exceptions.DatabaseException;
import com.fastshop.exceptions.ResourceNotFoundException;
import com.fastshop.mappers.ProductConverter;
import com.fastshop.repositories.CategoryRepository;
import com.fastshop.repositories.OrderRepository;
import com.fastshop.repositories.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductConverter productConverter;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ProductConverter productcToResponsDTO) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productConverter = productcToResponsDTO;
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + id));
        return productConverter.toResponseDTO(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        List<Product> result = productRepository.findAll();
        return result.stream().map(productConverter::toResponseDTO).toList();
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {

        if (productRequestDTO.getCategoryId() == null) {
            throw new IllegalArgumentException("O ID da categoria não pode ser nulo.");
        }

        Category category = categoryRepository.findById(productRequestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com o ID: " + productRequestDTO.getCategoryId()));
        Product product = productConverter.toEntity(productRequestDTO, category);
        product = productRepository.save(product);
        return productConverter.toResponseDTO(product);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductUpdateDTO productUpdateDTO) {
        Optional<Product> productExistent = productRepository.findById(id);

        if (productExistent.isPresent()) {
            Product product = productExistent.get();
            // Atualiza campos básicos
            if (productUpdateDTO.getName() != null) product.setName(productUpdateDTO.getName());
            if (productUpdateDTO.getDescription() != null) product.setDescription(productUpdateDTO.getDescription());
            if (productUpdateDTO.getPrice() != null) {
                product.setPrice(productUpdateDTO.getPrice());
                product.setUnitPrice(productUpdateDTO.getPrice());
            }
            if (productUpdateDTO.getStock() != null) product.setStock(productUpdateDTO.getStock());
            if (productUpdateDTO.getImageUrl() != null) product.setImageUrl(productUpdateDTO.getImageUrl());

            // Atualiza categoria condicionalmente
            if (productUpdateDTO.getCategoryId() != null) {
                Category category = categoryRepository.findById(productUpdateDTO.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com o ID: " + productUpdateDTO.getCategoryId()));
                product.setCategory(category);
            }
            return productConverter.toResponseDTO(productRepository.save(product));
        } else {
            throw new ResourceNotFoundException("Produto não encontrado com o ID: " + id);
        }
    }

    @Transactional
    public void deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado com o ID: " + id);
        }
        try {
            productRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(
                    "Violação de integridade: não é possível excluir o produto vinculado a outras entidades.");
        }
    }


}