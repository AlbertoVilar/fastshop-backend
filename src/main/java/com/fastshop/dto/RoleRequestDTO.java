package com.fastshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequestDTO {
    @NotBlank(message = "Authority não pode ser vazia")
    private String authority; // ex: "ROLE_ADMIN"
}