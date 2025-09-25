package com.fastshop.controllers;

import com.fastshop.dto.ProductRequestDTO;
import com.fastshop.dto.ProductResponseDTO;
import com.fastshop.entities.Product;
import com.fastshop.mappers.ProductDTOConverter;
import com.fastshop.services.ProductService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final ProductDTOConverter dtoConverter;

    public ProductController(ProductService productService, ProductDTOConverter dtoConverter) {
        this.productService = productService;
        this.dtoConverter = dtoConverter;
    }

    // Controller methods will go here
    @GetMapping("/{id}")
    public ProductResponseDTO getProductsById(@PathVariable Long id) {
        ProductResponseDTO responseDTO = productService.getProductsById(id);
        return ResponseEntity.ok(responseDTO).getBody();
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO responseDTO = productService.createProduct(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id,
                                                            @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO responseDTO = productService.updateProduct(id, productRequestDTO);

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}


