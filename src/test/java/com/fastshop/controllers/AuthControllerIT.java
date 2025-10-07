package com.fastshop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastshop.dto.AuthRequestDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

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
}
