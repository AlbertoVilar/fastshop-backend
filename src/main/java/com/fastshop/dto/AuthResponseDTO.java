package com.fastshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    private String accessToken;
    @Builder.Default

    private String tokenType = "Bearer";
    private long expiresIn; // em segundos
    private String username;
    private List<String> roles; // ex.: ["ROLE_ADMIN", "ROLE_CUSTOMER"]
}
