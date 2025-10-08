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
public class OrderItemRequestDTO {
    @NotNull(message = "ID do produto não pode ser nulo")
    private Long productId;

    @NotNull(message = "Quantidade não pode ser nula")
    @Min(value = 1, message = "Quantidade deve ser pelo menos 1")
    private Integer quantity;

    @NotNull(message = "Preço unitário não pode ser nulo")
    @DecimalMin(value = "0.01", message = "Preço unitário deve ser maior que zero")
    private BigDecimal unitPrice;
}

