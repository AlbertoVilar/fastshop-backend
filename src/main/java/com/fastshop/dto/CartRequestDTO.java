package com.fastshop.dto;

import lombok.*;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRequestDTO {
    @NotNull(message = "ID do cliente não pode ser nulo")
    private Long customerId;

    @Valid
    private List<CartItemRequestDTO> items;
}
