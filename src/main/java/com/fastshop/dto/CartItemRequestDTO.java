package com.fastshop.dto;

import lombok.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemRequestDTO {
    private Long cartId; // Adicionado para referenciar o carrinho

    @NotNull(message = "ID do produto não pode ser nulo")
    private Long productId;

    @NotNull(message = "Quantidade não pode ser nula")
    @Min(value = 1, message = "Quantidade deve ser pelo menos 1")
    private Integer quantity;

    @DecimalMin(value = "0.00", message = "Preço unitário não pode ser negativo")
    private BigDecimal unitPrice;
}
