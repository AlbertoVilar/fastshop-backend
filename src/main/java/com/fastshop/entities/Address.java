package com.fastshop.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Rua não pode ser vazia")
    private String street;

    @NotBlank(message = "Bairro não pode ser vazio")
    private String neighborhood;

    @NotBlank(message = "Cidade não pode ser vazia")
    private String city;

    @NotBlank(message = "Estado não pode ser vazio")
    private String state;

    @NotBlank(message = "CEP não pode ser vazio")
    @Size(min = 5, max = 10, message = "CEP deve ter entre 5 e 10 caracteres")
    private String zipCode;

    @NotBlank(message = "País não pode ser vazio")
    private String country;
}
