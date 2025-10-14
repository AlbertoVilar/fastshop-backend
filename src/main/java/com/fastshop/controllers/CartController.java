package com.fastshop.controllers;

import com.fastshop.dto.CartItemRequestDTO;
import com.fastshop.dto.CartRequestDTO;
import com.fastshop.dto.CartResponseDTO;
import com.fastshop.services.CartService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @PreAuthorize("hasRole('ADMIN') or @cartSecurity.canCreateForCustomer(#dto.customerId)")
    public ResponseEntity<CartResponseDTO> create(@RequestBody @Valid CartRequestDTO dto) {
        CartResponseDTO createdCart = cartService.createCart(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCart.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdCart);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @cartSecurity.isOwner(#id)")
    public ResponseEntity<CartResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @cartSecurity.isOwner(#id)")
    public ResponseEntity<CartResponseDTO> update(@PathVariable Long id, @RequestBody @Valid CartRequestDTO dto) {
        return ResponseEntity.ok(cartService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @cartSecurity.isOwner(#id)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cartService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CartResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(cartService.findAll(pageable));
    }

    // Adicionar/remover itens por cartId (conforme expectativas dos testes)
    @PostMapping("/{id}/items")
    @PreAuthorize("hasRole('ADMIN') or @cartSecurity.isOwner(#cartId) or !@cartSecurity.cartExists(#cartId)")
    public ResponseEntity<CartResponseDTO> addItem(@PathVariable("id") Long cartId, @RequestBody @Valid CartItemRequestDTO dto) {
        return ResponseEntity.ok(cartService.addItemToCart(cartId, dto));
    }

    @DeleteMapping("/{id}/items/{productId}")
    @PreAuthorize("hasRole('ADMIN') or @cartSecurity.isOwner(#cartId)")
    public ResponseEntity<Void> removeItem(@PathVariable("id") Long cartId, @PathVariable Long productId) {
        cartService.removeItemFromCart(cartId, productId);
        return ResponseEntity.noContent().build();
    }

    // Endpoints /me para o usuário autenticado
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponseDTO> getMyCart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(cartService.findActiveCartByAuthenticatedUser(auth));
    }

    @PostMapping("/me/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponseDTO> addItemToMyCart(@RequestBody @Valid CartItemRequestDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(cartService.addItemToAuthenticatedCart(auth, dto));
    }

    @DeleteMapping("/me/items/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeItemFromMyCart(@PathVariable Long productId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        cartService.removeItemFromAuthenticatedCart(auth, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteMyCart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        cartService.deleteAuthenticatedCart(auth);
        return ResponseEntity.noContent().build();
    }
}
