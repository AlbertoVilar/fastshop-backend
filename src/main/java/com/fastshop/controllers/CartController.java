package com.fastshop.controllers;

import com.fastshop.dto.CartItemRequestDTO;
import com.fastshop.dto.CartRequestDTO;
import com.fastshop.dto.CartResponseDTO;
import com.fastshop.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<CartResponseDTO> create(@RequestBody CartRequestDTO dto) {
        CartResponseDTO createdCart = cartService.createCart(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCart.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdCart);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartResponseDTO> update(@PathVariable Long id, @RequestBody CartRequestDTO dto) {
        return ResponseEntity.ok(cartService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cartService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CartResponseDTO>> findAll() {
        return ResponseEntity.ok(cartService.findAll());
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartResponseDTO> addItemToCart(@PathVariable Long cartId, @RequestBody CartItemRequestDTO dto) {
        return ResponseEntity.ok(cartService.addItemToCart(cartId, dto));
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        cartService.removeItemFromCart(cartId, productId);
        return ResponseEntity.noContent().build();
    }
}
