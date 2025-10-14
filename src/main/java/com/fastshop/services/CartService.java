package com.fastshop.services;

import com.fastshop.dto.CartItemRequestDTO;
import com.fastshop.dto.CartRequestDTO;
import com.fastshop.dto.CartResponseDTO;
import com.fastshop.entities.Cart;
import com.fastshop.exceptions.ResourceNotFoundException;
import com.fastshop.mappers.CartConverter;
import com.fastshop.repositories.CartRepository;
import com.fastshop.repositories.CustomerRepository;
import com.fastshop.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CartConverter cartConverter;

    public CartService(CartRepository cartRepository,
                       CustomerRepository customerRepository, ProductRepository productRepository, CartConverter cartConverter) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.cartConverter = cartConverter;
    }

    public CartResponseDTO createCart(CartRequestDTO dto) {
        var customer = customerRepository.findById(dto.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + dto.getCustomerId()));
        var cart = cartConverter.fromDTO(dto, customer);
        cart = cartRepository.save(cart);
        return cartConverter.toResponseDTO(cart);
    }

    public CartResponseDTO update(Long id, CartRequestDTO dto) {
        var cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado com o ID: " + id));
        // Mantém o dono original do carrinho
        cartConverter.updateEntityFromDTO(cart, dto, cart.getCustomer());
        cart = cartRepository.save(cart);
        return cartConverter.toResponseDTO(cart);
    }

    @Transactional(readOnly = true)
    public CartResponseDTO findById(Long id) {
        var cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado com o ID: " + id));
        return cartConverter.toResponseDTO(cart);
    }

    @Transactional(readOnly = true)
    public Page<CartResponseDTO> findAll(Pageable pageable) {
        return cartRepository.findAll(pageable).map(cartConverter::toResponseDTO);
    }

    public void delete(Long id) {
        var cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado com o ID: " + id));
        cartRepository.delete(cart);
    }

    public CartResponseDTO addItemToCart(Long cartId, CartItemRequestDTO dto) {
        var cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado com o ID: " + cartId));
        var product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + dto.getProductId()));
        cart.addItem(product, dto.getQuantity());
        cart = cartRepository.save(cart);
        return cartConverter.toResponseDTO(cart);
    }

    public void removeItemFromCart(Long cartId, Long productId) {
        var cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado com o ID: " + cartId));
        boolean removed = cart.removeItem(productId);
        if (!removed) {
            throw new ResourceNotFoundException("Item do carrinho não encontrado para o produto: " + productId);
        }
        cartRepository.save(cart);
    }

    public CartResponseDTO findActiveCartByAuthenticatedUser(Authentication auth) {
        String email = auth.getName();
        var customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para o usuário autenticado."));
        var cart = cartRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().customer(customer).build()));
        return cartConverter.toResponseDTO(cart);
    }

    public CartResponseDTO addItemToAuthenticatedCart(Authentication auth, CartItemRequestDTO dto) {
        String email = auth.getName();
        var customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para o usuário autenticado."));
        var cart = cartRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().customer(customer).build()));
        var product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + dto.getProductId()));
        int qty = Math.max(1, dto.getQuantity());
        cart.addItem(product, qty);
        cart = cartRepository.save(cart);
        return cartConverter.toResponseDTO(cart);
    }

    public void removeItemFromAuthenticatedCart(Authentication auth, Long productId) {
        String email = auth.getName();
        var customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para o usuário autenticado."));
        var cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado para o usuário autenticado."));
        boolean removed = cart.removeItem(productId);
        if (!removed) {
            throw new ResourceNotFoundException("Item do carrinho não encontrado para o produto: " + productId);
        }
        cartRepository.save(cart);
    }

    public void deleteAuthenticatedCart(Authentication auth) {
        String email = auth.getName();
        var customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para o usuário autenticado."));
        var cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado para o usuário autenticado."));
        cartRepository.delete(cart);
    }
}
