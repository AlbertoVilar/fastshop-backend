package com.fastshop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastshop.dto.AuthRequestDTO;
import com.fastshop.dto.CustomerRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CustomerControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String obtainAccessToken(String username, String password) throws Exception {
        AuthRequestDTO authRequest = new AuthRequestDTO(username, password);
        String jsonRequest = objectMapper.writeValueAsString(authRequest);

        String responseBody = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(responseBody).get("accessToken").asText();
    }

    @Test
    @DisplayName("GET /customers/{id} por não-dono deve retornar 403 Forbidden")
    void getCustomerById_nonOwner_shouldReturn403() throws Exception {
        String ownerToken = obtainAccessToken("alex@gmail.com", "132747");
        String otherToken = obtainAccessToken("maria@email.com", "132747");

        Long ownerCustomerId = 1L; // Alex

        mockMvc.perform(get("/customers/" + ownerCustomerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Acesso negado"));
    }

    @Test
    @DisplayName("PUT /customers/{id} por não-dono deve retornar 403 Forbidden")
    void updateCustomer_nonOwner_shouldReturn403() throws Exception {
        String ownerToken = obtainAccessToken("alex@gmail.com", "132747");
        String otherToken = obtainAccessToken("maria@email.com", "132747");

        Long ownerCustomerId = 1L; // Alex

        CustomerRequestDTO updateDTO = CustomerRequestDTO.builder()
                .name("Alex Atualizado")
                .email("alex@gmail.com")
                .birthDate(java.time.LocalDate.parse("1990-01-01"))
                .phone("(11) 99999-9999")
                .cpfOrCnpj("123.456.789-01")
                .build();

        String json = objectMapper.writeValueAsString(updateDTO);

        mockMvc.perform(put("/customers/" + ownerCustomerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + otherToken)
                        .content(json))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Acesso negado"));
    }

    @Test
    @DisplayName("GET /customers/{id} por dono deve retornar 200 OK")
    void getCustomerById_owner_shouldReturn200() throws Exception {
        String ownerToken = obtainAccessToken("alex@gmail.com", "132747");
        Long ownerCustomerId = 1L; // Alex

        mockMvc.perform(get("/customers/" + ownerCustomerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ownerCustomerId))
                .andExpect(jsonPath("$.email").value("alex@gmail.com"));
    }
}