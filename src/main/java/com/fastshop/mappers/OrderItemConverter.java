package com.fastshop.mappers;

import com.fastshop.dto.OrderItemRequestDTO;
import com.fastshop.dto.OrderItemResponseDTO;
import com.fastshop.entities.Order;
import com.fastshop.entities.OrderItem;
import com.fastshop.entities.Product;
import org.springframework.stereotype.Component;

@Component
public class OrderItemConverter {
    // Métodos de conversão podem ser adicionados aqui
    public OrderItem fromDTO(OrderItemRequestDTO requestDTO,
                             Order order,
                             Product product) {
        return OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(requestDTO.getQuantity())
                .unitPrice(requestDTO.getUnitPrice()) // Corrigido para usar o valor do DTO
                .build();
    }

    public OrderItemResponseDTO toResponseDTO(OrderItem item) {
        return OrderItemResponseDTO.builder()
                .id(item.getId())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .build();
    }
}
