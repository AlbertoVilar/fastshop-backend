package com.fastshop.mappers;

import com.fastshop.dto.CartItemResponseDTO;
import com.fastshop.dto.CartRequestDTO;
import com.fastshop.dto.CartResponseDTO;
import com.fastshop.entities.Cart;
import com.fastshop.entities.Customer;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CartConverter {

    private final CustomerConverter customerConverter;
    private final CartItemConverter converter;

    public CartConverter(CustomerConverter customerConverter, CartItemConverter converter) {
        this.customerConverter = customerConverter;
        this.converter = converter;
    }


    // TODO: Implementar conversão Cart <-> DTO
    public CartResponseDTO toResponseDTO(Cart cart) {
        return CartResponseDTO.builder()
                .id(cart.getId())
                .customerId(cart.getCustomer() != null ? cart.getCustomer().getId() : null)

                .total(
                        cart.getItems().stream().map(i ->
                        i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                )

                        .items(cart.getItems().stream().map(converter::toResponseDTO).toList())
                .build();

    }

    public Cart fromDTO(CartRequestDTO dto, Customer customer) {
            return Cart.builder()
                    .customer(customer)
                    .build();
    }

    public void updateEntityFromDTO(Cart cart, CartRequestDTO dto, Customer customer) {
        cart.setCustomer(customer);
    }
}
