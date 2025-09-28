package com.fastshop.services;

import com.fastshop.dto.OrderItemRequestDTO;
import com.fastshop.dto.OrderRequestDTO;
import com.fastshop.dto.OrderResponseDTO;
import com.fastshop.entities.Customer;
import com.fastshop.entities.Order;
import com.fastshop.entities.OrderItem;
import com.fastshop.entities.Product;
import com.fastshop.exceptions.ResourceNotFoundException;
import com.fastshop.mappers.OrderConverter;
import com.fastshop.mappers.OrderItemConverter;
import com.fastshop.repositories.CustomerRepository;
import com.fastshop.repositories.OrderRepository;
import com.fastshop.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderConverter orderConverter;
    private final OrderItemConverter orderItemConverter;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderConverter orderConverter, OrderItemConverter orderItemConverter, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderConverter = orderConverter;
        this.orderItemConverter = orderItemConverter;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com o ID: " + id));
        return orderConverter.toResponseDTO(order);
    }

    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderConverter::toResponseDTO)
                .collect(Collectors.toList());
    }

    public OrderResponseDTO createOrder(OrderRequestDTO requestDTO) {
        // Buscar o cliente
        Customer customer = customerRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: "
                        + requestDTO.getCustomerId()));

        // Converter itens do pedido usando o conversor
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequestDTO itemRequestDTO : requestDTO.getItems()) {
            Product product = productRepository.findById(itemRequestDTO.getProductId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Produto não encontrado com o ID: "
                                    + itemRequestDTO.getProductId()));
            // Usar o conversor para criar o OrderItem
            OrderItem orderItem = orderItemConverter.fromDTO(itemRequestDTO, null, product);
            items.add(orderItem);
        }

        // Criar o pedido usando o conversor
        Order order = orderConverter.fromDTO(requestDTO, customer, items);
        // Associar cada item ao pedido
        for (OrderItem item : items) {
            item.setOrder(order);
        }
        // Salvar o pedido
        Order savedOrder = orderRepository.save(order);
        // Retornar o DTO de resposta
        return orderConverter.toResponseDTO(savedOrder);
    }

    public OrderResponseDTO updateOrder(Long id, OrderRequestDTO requestDTO) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com o ID: " + id));

        Customer customer = customerRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: "
                        + requestDTO.getCustomerId()));

        // Converter itens do pedido usando o conversor
        List<OrderItem> items = requestDTO.getItems().stream()
                .map(dto -> {
                    Product product = productRepository.findById(dto.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Produto não encontrado com o ID: " + dto.getProductId()
                            ));
                    return orderItemConverter.fromDTO(dto, order, product);
                })
                .collect(Collectors.toList());



        // Limpar a lista antiga de itens
        if (order.getItems() != null) {
            order.getItems().clear();
            order.getItems().addAll(items);
        } else {
            order.setItems(items);
        }

        // Atualizar cliente
        order.setCustomer(customer);
        // Recalcular o total
        order.setTotal(items.stream()
                .map(i -> i.getUnitPrice().multiply(java.math.BigDecimal.valueOf(i.getQuantity())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));

        Order savedOrder = orderRepository.save(order);
        return orderConverter.toResponseDTO(savedOrder);
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com o ID: " + id));
        orderRepository.delete(order);
    }

}
