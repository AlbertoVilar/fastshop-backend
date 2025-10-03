package com.fastshop.mappers;

import com.fastshop.dto.CartItemRequestDTO;
import com.fastshop.dto.CartItemResponseDTO;
import com.fastshop.entities.Cart;
import com.fastshop.entities.CartItem;
import com.fastshop.entities.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CartItemConverter {

    public CartItemResponseDTO toResponseDTO(CartItem item) {
        {
            return CartItemResponseDTO.builder()
                    .id(item.getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .build();
        }
    }

    public CartItem fromDTO(CartItemRequestDTO dto, Cart cart, Product product) {
        return CartItem.builder()
                .product(product) // Define o produto com base no ID fornecido no DTO
                .cart(cart) // Define o carrinho ao qual este item pertence
                .quantity(dto.getQuantity())
                .unitPrice(product.getPrice())
                .build();

    }

}
