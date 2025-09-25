package com.fastshop.services;

import com.fastshop.dto.ProductRequestDTO;
import com.fastshop.dto.ProductResponseDTO;
import com.fastshop.entities.Product;
import com.fastshop.exceptions.ResourceNotFoundException;
import com.fastshop.mappers.ProductConverter;
import com.fastshop.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Diz ao JUnit que vamos usar o Mockito nesta classe
class ProductServiceTest {

    @Mock // Mock do repositório (simula acesso ao banco)
    private ProductRepository productRepository;

    @Mock // Mock do conversor (simula conversões DTO <-> Entidade)
    private ProductConverter productConverter;

    @InjectMocks // Instância real do Service com os mocks injetados
    private ProductService productService;

    @Test
    void getProductById_deveRetornarDTO_quandoProdutoExistir() {
        Long id = 1L;
        Product produtoExistente = new Product();
        produtoExistente.setId(id);

        ProductResponseDTO produtoDTO = ProductResponseDTO.builder()
                .id(id)
                .name("Notebook Dell")
                .price(BigDecimal.valueOf(4500.00))
                .build();

        when(productRepository.findById(id)).thenReturn(Optional.of(produtoExistente));
        when(productConverter.toResponseDTO(produtoExistente)).thenReturn(produtoDTO);

        ProductResponseDTO resultado = productService.getProductById(id);

        assertNotNull(resultado);
        assertEquals(produtoDTO.getId(), resultado.getId());
        assertEquals("Notebook Dell", resultado.getName());
        assertEquals(BigDecimal.valueOf(4500.00), resultado.getPrice());
    }

    @Test
    void getProductById_deveLancarExcecao_quandoProdutoNaoExistir() {
        Long id = 99L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getProductById(id)
        );

        assertEquals("Produto não encontrado com o ID: 99", ex.getMessage());
    }

    @Test
    void updateProduct_deveAtualizarERetornarDTO_quandoProdutoExistir() {
        Long id = 1L;
        Product produtoExistente = new Product();
        produtoExistente.setId(id);

        ProductRequestDTO req = ProductRequestDTO.builder()
                .name("Produto Atualizado")
                .description("Descrição Atualizada")
                .price(BigDecimal.valueOf(100.00))
                .stock(5)
                .imageUrl("http://imagem.com/teste.png")
                .build();

        ProductResponseDTO produtoDTO = ProductResponseDTO.builder()
                .id(id)
                .name("Produto Atualizado")
                .description("Descrição Atualizada")
                .price(BigDecimal.valueOf(100.00))
                .stock(5)
                .imageUrl("http://imagem.com/teste.png")
                .build();

        when(productRepository.findById(id)).thenReturn(Optional.of(produtoExistente));
        doNothing().when(productConverter).updateEntityFromDTO(req, produtoExistente);
        when(productRepository.save(produtoExistente)).thenReturn(produtoExistente);
        when(productConverter.toResponseDTO(produtoExistente)).thenReturn(produtoDTO);

        ProductResponseDTO resultado = productService.updateProduct(id, req);

        assertNotNull(resultado);
        assertEquals("Produto Atualizado", resultado.getName());
        assertEquals(5, resultado.getStock());
    }

    @Test
    void updateProduct_deveLancarExcecao_quandoProdutoNaoExistir() {
        Long id = 99L;
        ProductRequestDTO req = new ProductRequestDTO();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(id, req));
    }

    @Test
    void deleteById_deveDeletar_quandoProdutoExistir() {
        Long id = 1L;

        when(productRepository.existsById(id)).thenReturn(true);
        doNothing().when(productRepository).deleteById(id);

        assertDoesNotThrow(() -> productService.deleteById(id));
        verify(productRepository).deleteById(id);
    }

    @Test
    void deleteById_deveLancarExcecao_quandoProdutoNaoExistir() {
        Long id = 99L;

        when(productRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteById(id));
    }

    @Test
    void createProduct_deveSalvarERetornarDTO() {
        ProductRequestDTO req = ProductRequestDTO.builder()
                .name("Novo Produto")
                .description("Descrição Novo Produto")
                .price(BigDecimal.valueOf(200.00))
                .stock(10)
                .imageUrl("http://imagem.com/produto.png")
                .build();

        Product produtoNovo = new Product();
        produtoNovo.setId(1L);
        produtoNovo.setName("Novo Produto");

        ProductResponseDTO produtoDTO = ProductResponseDTO.builder()
                .id(1L)
                .name("Novo Produto")
                .price(BigDecimal.valueOf(200.00))
                .stock(10)
                .build();

        when(productConverter.toEntity(req)).thenReturn(produtoNovo);
        when(productRepository.save(produtoNovo)).thenReturn(produtoNovo);
        when(productConverter.toResponseDTO(produtoNovo)).thenReturn(produtoDTO);

        ProductResponseDTO resultado = productService.createProduct(req);

        assertNotNull(resultado);
        assertEquals("Novo Produto", resultado.getName());
        assertEquals(10, resultado.getStock());
    }

    @Test
    void getAllProducts_deveRetornarListaDTO() {
        Product produto = new Product();
        produto.setId(1L);
        produto.setName("Produto Teste");

        ProductResponseDTO produtoDTO = ProductResponseDTO.builder()
                .id(1L)
                .name("Produto Teste")
                .build();

        when(productRepository.findAll()).thenReturn(Collections.singletonList(produto));
        when(productConverter.toResponseDTO(produto)).thenReturn(produtoDTO);

        List<ProductResponseDTO> resultado = productService.getAllProducts();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Produto Teste", resultado.get(0).getName());
    }
}
