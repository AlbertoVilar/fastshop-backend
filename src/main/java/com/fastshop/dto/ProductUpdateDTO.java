package com.fastshop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateDTO {

    @NotBlank(message = "Nome do produto não pode ser vazio")
    @Size(min = 3, max = 80, message = "Nome do produto deve ter entre 3 e 80 caracteres")
    private String name;

    @NotBlank(message = "Descrição do produto não pode ser vazia")
    @Size(min = 20, max = 500, message = "Descrição do produto deve ter entre 50 e 500 caracteres")
    private String description;

    @DecimalMin(value = "0.01", message = "Preço do produto deve ser maior que zero")
    private BigDecimal price;

    @Min(value = 0, message = "Estoque do produto não pode ser negativo")
    private Integer stock;

    @Pattern(
        regexp = "^(https?:\\/\\/)?([\\w\\-]+\\.)+[\\w\\-]+(\\/\\S*)?$",
        message = "URL da imagem deve ser válida"
    )
    @Size(max = 255, message = "URL da imagem deve ter no máximo 255 caracteres")
    private String imageUrl;

    // categoryId opcional para atualizações; quando presente, será aplicado
    private Long categoryId;
}