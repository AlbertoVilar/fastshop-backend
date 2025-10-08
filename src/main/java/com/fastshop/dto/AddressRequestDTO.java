package com.fastshop.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequestDTO {
    @NotBlank(message = "Rua não pode ser vazia")
    private String street;

    @NotBlank(message = "Bairro não pode ser vazio")
    private String neighborhood;

    @NotBlank(message = "Cidade não pode ser vazia")
    private String city;

    @NotBlank(message = "Estado não pode ser vazio")
    @Pattern(regexp = "^[A-Za-z]{2}$", message = "Estado deve ter 2 letras (UF)")
    private String state;

    @NotBlank(message = "CEP não pode ser vazio")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP deve estar no formato 00000-000")
    private String zipCode;

    @NotBlank(message = "País não pode ser vazio")
    private String country;
}
