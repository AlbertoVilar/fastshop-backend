package com.fastshop.dto;

import lombok.*;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDTO {
    @NotBlank(message = "Nome da categoria não pode ser vazio")
    @Size(min = 3, max = 100, message = "Nome da categoria deve ter entre 3 e 100 caracteres")
    private String name;

    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    private String description;
    private List<ProductRequestDTO> products;
}
