package com.fastshop.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemRequestDTO {
    private Long cartId; // Adicionado para referenciar o carrinho
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
}
