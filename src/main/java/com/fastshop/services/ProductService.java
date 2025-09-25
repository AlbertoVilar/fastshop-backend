package com.fastshop.services;

import com.fastshop.dto.ProductRequestDTO;
import com.fastshop.dto.ProductResponseDTO;
import com.fastshop.entities.Product;
import com.fastshop.mappers.ProductConverter;
import com.fastshop.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private ProductRepository productRepository;
    private final ProductConverter productcToResponsDTO;

    public ProductService(ProductRepository productRepository, ProductConverter productcToResponsDTO) {
        this.productRepository = productRepository;
        this.productcToResponsDTO = productcToResponsDTO;
    }

    public ProductResponseDTO getProductsById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + id));
        return productcToResponsDTO.toResponseDTO(product);
    }

    public List<ProductResponseDTO> getAllProducts() {
        List<Product> result = productRepository.findAll();
        return result.stream().map(productcToResponsDTO::toResponseDTO).toList();
    }

    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = productRepository.save(productcToResponsDTO.toEntity(productRequestDTO));
        return productcToResponsDTO.toResponseDTO(product);
    }


}
