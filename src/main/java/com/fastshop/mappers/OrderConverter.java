package com.fastshop.mappers;

import com.fastshop.dto.OrderRequestDTO;
import com.fastshop.dto.OrderResponseDTO;
import com.fastshop.dto.OrderItemResponseDTO;
import com.fastshop.entities.Customer;
import com.fastshop.entities.Order;
import com.fastshop.entities.OrderItem;
import com.fastshop.entities.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderConverter {
    private final OrderItemConverter itemConverter;

    @Autowired
    public OrderConverter(OrderItemConverter itemConverter) {
        this.itemConverter = itemConverter;
    }

    public Order fromDTO(OrderRequestDTO requestDTO, Customer customer, List<OrderItem> items) {
        return Order.builder()
                .customer(customer)
                .status(OrderStatus.PENDING)          // status inicial
                .createdAt(LocalDateTime.now())       // data atual
                .total(items.stream()                 // soma o total
                        .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .items(items)
                .build();
    }

    public OrderResponseDTO toResponseDTO(Order order) {
        List<OrderItemResponseDTO> itemDTOs = order.getItems() == null ? List.of() :
                order.getItems().stream()
                        .map(itemConverter::toResponseDTO)
                        .collect(Collectors.toList());
        return OrderResponseDTO.builder()
                .id(order.getId())
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .customerName(order.getCustomer() != null ? order.getCustomer().getName() : null)
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .createdAt(order.getCreatedAt())
                .total(order.getTotal())
                .items(itemDTOs)
                .build();
    }

    public void updateEntityFromDTO(OrderRequestDTO requestDTO, Order order, Customer customer, List<OrderItem> items) {
        order.setCustomer(customer);
        // Limpa a lista antiga de itens para garantir que o JPA remova os órfãos
        if (order.getItems() != null) {
            order.getItems().clear();
            order.getItems().addAll(items);
        } else {
            order.setItems(items);
        }
        // Recalcula o total
        order.setTotal(items.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        // status e createdAt não são atualizados para preservar valores originais
    }
}
