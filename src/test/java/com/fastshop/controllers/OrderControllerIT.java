package com.fastshop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastshop.dto.AuthRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class OrderControllerIT {

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
    @DisplayName("GET /orders/{id} por não-dono deve retornar 403 Forbidden")
    void getOrderById_nonOwner_shouldReturn403() throws Exception {
        String ownerToken = obtainAccessToken("alex@gmail.com", "132747");
        String otherToken = obtainAccessToken("maria@email.com", "132747");

        Long customerId = 1L; // Alex

        String orderRequestJson = "{\n" +
                "  \"customerId\": " + customerId + ",\n" +
                "  \"items\": [\n" +
                "    { \"productId\": 1, \"quantity\": 1 }\n" +
                "  ]\n" +
                "}";

        String createResponse = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + ownerToken)
                        .content(orderRequestJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long orderId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/orders/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Acesso negado"));
    }

    @Test
    @DisplayName("GET /orders/{id} por dono deve retornar 200 OK")
    void getOrderById_owner_shouldReturn200() throws Exception {
        String ownerToken = obtainAccessToken("alex@gmail.com", "132747");
        Long customerId = 1L;

        String orderRequestJson = "{\n" +
                "  \"customerId\": " + customerId + ",\n" +
                "  \"items\": [\n" +
                "    { \"productId\": 1, \"quantity\": 2 }\n" +
                "  ]\n" +
                "}";

        String createResponse = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + ownerToken)
                        .content(orderRequestJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long orderId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/orders/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.customerId").value(customerId));
    }
}