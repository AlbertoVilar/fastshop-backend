package com.fastshop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastshop.dto.AuthRequestDTO;
import com.fastshop.dto.CartRequestDTO;
import com.fastshop.dto.CartItemRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CartControllerIT {

    //Injetar MockMvc e ObjectMapper
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Credenciais de um cliente para testar o carrinho
    private static final String CUSTOMER_USERNAME = "alex@gmail.com";
    private static final String CUSTOMER_PASSWORD = "132747";
    // Outro cliente para validar propriedade
    private static final String OTHER_CUSTOMER_USERNAME = "maria@email.com";
    private static final String OTHER_CUSTOMER_PASSWORD = "132747";

    // Testes de integração para os endpoints do carrinho
    // Método auxiliar para obter o token JWT
    private String obtainAccessToken(String username, String password) throws Exception {
        AuthRequestDTO authRequest = new AuthRequestDTO(username, password);
        String jsonRequest = objectMapper.writeValueAsString(authRequest);

        String responseBody = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(responseBody).get("accessToken").asText();
    }

    // Teste de falha: adicionar item em carrinho inexistente deve retornar 404 Not Found
    @Test
    @DisplayName("Adicionar item em carrinho inexistente deve retornar 404 Not Found")
    void addItem_nonExistentCart_shouldReturn404NotFound() throws Exception {
        // Token do cliente
        String accessToken = obtainAccessToken(CUSTOMER_USERNAME, CUSTOMER_PASSWORD);

        // cartId inexistente
        Long nonExistentCartId = 999L;

        // Item a ser adicionado
        Long existingProductId = 1L;
        CartItemRequestDTO itemRequest = CartItemRequestDTO.builder()
                .productId(existingProductId)
                .quantity(1)
                .build();

        String jsonRequest = objectMapper.writeValueAsString(itemRequest);

        mockMvc.perform(post("/carts/" + nonExistentCartId + "/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.message").value("Carrinho não encontrado com o ID: " + nonExistentCartId))
                .andExpect(jsonPath("$.path").value("/carts/" + nonExistentCartId + "/items"));
    }

    // Teste para adicionar um produto ao carrinho
    @Test
    @DisplayName("Cliente autenticado deve criar o carrinho e adicionar item com sucesso")
    void addItem_authenticatedCustomer_shouldCreateCartAndReturn200() throws Exception {

        long existingProductId = 1L;
        Integer quantity = 2;
        BigDecimal productPrice = new BigDecimal("4500.00");

        // 1) Obter token (não obrigatório para /carts, mas mantido no fluxo)
        String accessToken = obtainAccessToken(CUSTOMER_USERNAME, CUSTOMER_PASSWORD);

        // 2) Criar carrinho para um cliente existente
        Long existingCustomerId = 1L;
        CartRequestDTO cartRequest = CartRequestDTO.builder()
                .customerId(existingCustomerId)
                .build();

        String cartResponse = mockMvc.perform(post("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(cartRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerId").value(existingCustomerId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long cartId = objectMapper.readTree(cartResponse).get("id").asLong();

        // 3) Adicionar item ao carrinho criado
        CartItemRequestDTO itemRequest = CartItemRequestDTO.builder()
                .productId(existingProductId)
                .quantity(quantity)
                .build();
        String jsonRequest = objectMapper.writeValueAsString(itemRequest);

        mockMvc.perform(post("/carts/" + cartId + "/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cartId))
                .andExpect(jsonPath("$.customerId").value(existingCustomerId))
                .andExpect(jsonPath("$.total").value(9000.00))
                .andExpect(jsonPath("$.items[0].productId").value(existingProductId))
                .andExpect(jsonPath("$.items[0].quantity").value(quantity))
                .andExpect(jsonPath("$.items[0].unitPrice").value(4500.00));
    }

    // Teste de falha: adicionar item com productId inexistente deve retornar 404 Not Found
    @Test
    @DisplayName("Adicionar item com produto inexistente deve retornar 404 Not Found")
    void addItem_nonExistentProduct_shouldReturn404NotFound() throws Exception {
        // 1) Obter token válido para o cliente Alex
        String accessToken = obtainAccessToken(CUSTOMER_USERNAME, CUSTOMER_PASSWORD);

        // 2) Criar um carrinho válido (usar um customer existente)
        Long existingCustomerId = 1L;
        CartRequestDTO cartRequest = CartRequestDTO.builder()
                .customerId(existingCustomerId)
                .build();

        String cartResponse = mockMvc.perform(post("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(cartRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long cartId = objectMapper.readTree(cartResponse).get("id").asLong();

        // 3) Tentar adicionar um produto inexistente
        Long nonExistentProductId = 9999L;
        CartItemRequestDTO itemRequest = CartItemRequestDTO.builder()
                .productId(nonExistentProductId)
                .quantity(1)
                .build();
        String jsonRequest = objectMapper.writeValueAsString(itemRequest);

        // 4) Validar resposta 404 Not Found com mensagem apropriada
        mockMvc.perform(post("/carts/" + cartId + "/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.message").value("Produto não encontrado com o ID: " + nonExistentProductId))
                .andExpect(jsonPath("$.path").value("/carts/" + cartId + "/items"));
    }

    @Test
    @DisplayName("Adicionar item em carrinho de outro cliente deve retornar 403 Forbidden")
    void addItem_cartOwnedByAnotherCustomer_shouldReturn403Forbidden() throws Exception {
        // Obter token do cliente A e criar carrinho
        String accessTokenOwner = obtainAccessToken(CUSTOMER_USERNAME, CUSTOMER_PASSWORD);

        Long ownerCustomerId = 1L;
        CartRequestDTO cartRequest = CartRequestDTO.builder()
                .customerId(ownerCustomerId)
                .build();

        String cartResponse = mockMvc.perform(post("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessTokenOwner)
                        .content(objectMapper.writeValueAsString(cartRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long cartId = objectMapper.readTree(cartResponse).get("id").asLong();

        // Obter token de outro cliente B
        String accessTokenOther = obtainAccessToken(OTHER_CUSTOMER_USERNAME, OTHER_CUSTOMER_PASSWORD);

        // Tentar adicionar item ao carrinho do cliente A usando token do cliente B
        CartItemRequestDTO itemRequest = CartItemRequestDTO.builder()
                .productId(1L)
                .quantity(1)
                .build();
        String jsonRequest = objectMapper.writeValueAsString(itemRequest);

        mockMvc.perform(post("/carts/" + cartId + "/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessTokenOther)
                        .content(jsonRequest))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Acesso negado"))
                .andExpect(jsonPath("$.message").value("Você não possui permissão para acessar este recurso"))
                .andExpect(jsonPath("$.path").value("/carts/" + cartId + "/items"));
    }

}