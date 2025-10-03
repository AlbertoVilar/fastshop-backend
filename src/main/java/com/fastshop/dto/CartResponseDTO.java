package com.fastshop.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDTO {
    private Long id;
    private Long customerId;
    private BigDecimal total;
    private List<CartItemResponseDTO> items;
}
