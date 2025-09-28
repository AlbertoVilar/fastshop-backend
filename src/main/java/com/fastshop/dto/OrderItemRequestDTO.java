package com.fastshop.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequestDTO {
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
}

