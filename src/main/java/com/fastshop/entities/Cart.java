package com.fastshop.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = "items") // Evita recursão infinita no toString()
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @Version
    private Long version;

    // Método para adicionar um item ao carrinho
    public void addItem(Product product, int quantity) {

        Optional<CartItem> existingItem = this.items.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(this)
                    .product(product)
                    .quantity(quantity)
                    .unitPrice(product.getPrice())
                    .build();
            this.items.add(newItem);
        }
    }
    // Remove um item do carrinho com base no ID do produto
    // Retorna true se algum item foi removido, false caso contrário
    public boolean removeItem(Long productId) {
        boolean removed = this.items.removeIf(item -> item.getProduct().getId().equals(productId));
        return removed;
    }
}
