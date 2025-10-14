package com.fastshop.dto;

import lombok.*;
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

    // unitPrice é calculado no servidor a partir de Product.price
}
