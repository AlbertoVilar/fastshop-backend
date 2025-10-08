package com.fastshop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastshop.dto.AuthRequestDTO;
import com.fastshop.dto.ProductRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    @DisplayName("createProduct_comNomeVazio_deveRetornar422UnprocessableEntity")
    void createProduct_comNomeVazio_deveRetornar422UnprocessableEntity() throws Exception {
        String token = obtainAccessToken("albertovilar1@gmail.com", "132747");

        // Cria categoria única válida antes para evitar 404
        var categoryJson = "{\"name\":\"Cat Teste Valida Nome\",\"description\":\"Categoria temporária para teste de validação\"}";
        String categoryResponse = mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(categoryJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long categoryId = objectMapper.readTree(categoryResponse).get("id").asLong();

        ProductRequestDTO requestDTO = ProductRequestDTO.builder()
                .name("")
                .description("Descrição do produto com tamanho adequado para validação passando.")
                .price(new BigDecimal("99.99"))
                .stock(10)
                .imageUrl("https://example.com/image.png")
                .categoryId(categoryId)
                .build();

        String json = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(json))
                .andExpect(status().isUnprocessableEntity());
    }
}