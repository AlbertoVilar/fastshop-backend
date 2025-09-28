package com.fastshop.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private Long id;
    private Long customerId;
    private String customerName;
    private String status;
    private LocalDateTime createdAt;
    private BigDecimal total;
    private List<OrderItemResponseDTO> items;
}
