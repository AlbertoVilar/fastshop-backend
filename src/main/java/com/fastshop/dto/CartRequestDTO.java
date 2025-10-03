package com.fastshop.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRequestDTO {
    private Long customerId;
    private List<CartItemRequestDTO> items;
}
