package com.fastshop.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequestDTO {
    @NotBlank(message = "Nome não pode ser vazio")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String name;

    @NotBlank(message = "Email não pode ser vazio")
    @Email(message = "Favor inserir um e-mail válido")
    private String email;

    @NotNull(message = "Data de nascimento não pode ser nula")
    private LocalDate birthDate;

    @NotBlank(message = "Telefone não pode ser vazio")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$", message = "Telefone deve estar no formato (DD) 99999-9999")
    private String phone;

    @NotBlank(message = "CPF/CNPJ não pode ser vazio")
    @Pattern(regexp = "^(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}|\\d{11}|\\d{14})$",
            message = "CPF/CNPJ inválido")
    private String cpfOrCnpj;
    private List<Long> addressIds;
}
