package com.fastshop.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {
    @NotNull(message = "ID do cliente não pode ser nulo")
    private Long customerId;

    @NotEmpty(message = "Pedido deve conter pelo menos um item")
    @Valid
    private List<OrderItemRequestDTO> items;
    // status, createdAt e total são calculados no backend
}
