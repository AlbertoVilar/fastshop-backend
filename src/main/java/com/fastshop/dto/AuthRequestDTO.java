package com.fastshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequestDTO {

    @JsonAlias({"email"})
    @NotBlank(message = "Email não pode ser vazio")
    @Email(message = "Favor inserir um e-mail válido")
    private String username;

    @NotBlank(message = "Senha não pode ser vazia")
    private String password;
}
