package com.fastshop.services;

import com.fastshop.dto.ProductRequestDTO;
import com.fastshop.dto.ProductResponseDTO;
import com.fastshop.entities.Product;
import com.fastshop.exceptions.DatabaseException;
import com.fastshop.exceptions.ResourceNotFoundException;
import com.fastshop.mappers.ProductConverter;
import com.fastshop.repositories.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private ProductRepository productRepository;
    private final ProductConverter productConverter;

    public ProductService(ProductRepository productRepository, ProductConverter productcToResponsDTO) {
        this.productRepository = productRepository;
        this.productConverter = productcToResponsDTO;
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + id));
        return productConverter.toResponseDTO(product);
    }

    public List<ProductResponseDTO> getAllProducts() {
        List<Product> result = productRepository.findAll();
        return result.stream().map(productConverter::toResponseDTO).toList();
    }

    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = productRepository.save(productConverter.toEntity(productRequestDTO));
        return productConverter.toResponseDTO(product);
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Optional<Product> productExistent = productRepository.findById(id);

        if (productExistent.isPresent()) {
            Product product = productExistent.get();
            productConverter.updateEntityFromDTO(productRequestDTO, product);
            return productConverter.toResponseDTO(productRepository.save(product));
        } else {
            throw new ResourceNotFoundException("Produto não encontrado com o ID: " + id);
        }
    }

   import org.springframework.dao.DataIntegrityViolationException;

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