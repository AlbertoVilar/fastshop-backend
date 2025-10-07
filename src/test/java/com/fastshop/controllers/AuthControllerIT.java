package com.fastshop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastshop.dto.AuthRequestDTO;
import com.fastshop.dto.ProductRequestDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String CUSTOMER_USERNAME = "alex@gmail.com";
    private static final String CUSTOMER_PASSWORD = "132747";

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
    @DisplayName("login_withValidCredentials_shouldReturnAuthResponseDTOAnd200Ok")
    void login_withValidCredentials_shouldReturnAuthResponseDTOAnd200Ok() throws Exception {

        AuthRequestDTO authRequest = new AuthRequestDTO("albertovilar1@gmail.com", "132747");
        String jsonRequest = objectMapper.writeValueAsString(authRequest);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.username").value("albertovilar1@gmail.com"))
                .andExpect(jsonPath("$.expiresIn").isNumber());
    }

    @Test
    @DisplayName("login_withInvalidPassword_shouldReturn401Unauthorized")
    void login_withInvalidPassword_shouldReturn401Unauthorized() throws Exception {
        AuthRequestDTO authRequest = new AuthRequestDTO("albertovilar1@gmail.com", "senha-invalida");
        String jsonRequest = objectMapper.writeValueAsString(authRequest);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Credenciais inválidas"))
                .andExpect(jsonPath("$.message").value("Usuário ou senha incorretos"))
                .andExpect(jsonPath("$.path").value("/auth/login"));
    }

    @Test
    @DisplayName("customer_tryCreateProduct_shouldReturn403Forbidden")
    void customer_tryCreateProduct_shouldReturn403Forbidden() throws Exception {
        String token = obtainAccessToken(CUSTOMER_USERNAME, CUSTOMER_PASSWORD);

        ProductRequestDTO requestDTO = ProductRequestDTO.builder()
                .name("Produto Teste")
                .description("Criado por cliente")
                .price(new BigDecimal("199.90"))
                .stock(5)
                .imageUrl("https://example.com/image.png")
                .categoryId(1L)
                .build();

        String json = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(json))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Acesso negado"))
                .andExpect(jsonPath("$.message").value("Você não possui permissão para acessar este recurso"))
                .andExpect(jsonPath("$.path").value("/products"));
    }

    // TESTE 4: ACESSO NEGADO (403) - CUSTOMER TENTA ACESSAR ROTA DE ADMIN
    @Test
    @DisplayName("Cliente (CUSTOMER) deve ter acesso negado (403) ao tentar criar produto")
    void adminAccess_deniedToCustomer_shouldReturn403Forbidden() throws Exception {
        // 1. Obter token do CUSTOMER
        String token = obtainAccessToken(CUSTOMER_USERNAME, CUSTOMER_PASSWORD);

        // 2. Criar DTO de Produto
        ProductRequestDTO requestDTO = ProductRequestDTO.builder()
                .name("Produto Negado")
                .description("Teste de Autorização")
                .price(new BigDecimal("100.00"))
                .stock(1)
                .imageUrl("https://placeholder.com")
                .categoryId(1L)
                .build();

        String jsonRequest = objectMapper.writeValueAsString(requestDTO);

        // 3. Tentar POST /products com token de CUSTOMER
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(jsonRequest))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Acesso negado"));
    }
}
