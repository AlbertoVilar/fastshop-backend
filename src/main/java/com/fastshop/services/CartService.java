package com.fastshop.services;

import com.fastshop.dto.CartItemRequestDTO;
import com.fastshop.dto.CartRequestDTO;
import com.fastshop.dto.CartResponseDTO;
import com.fastshop.exceptions.ResourceNotFoundException;
import com.fastshop.mappers.CartConverter;
import com.fastshop.repositories.CartRepository;
import com.fastshop.repositories.CustomerRepository;
import com.fastshop.repositories.ProductRepository;
import org.springframework.stereotype.Service;

@Service
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

    // Dentro do CartService.java

    public CartResponseDTO createCart(CartRequestDTO dto) {


        var customer = customerRepository.findById(dto.getCustomerId()).orElseThrow(
                        () -> new ResourceNotFoundException("Cliente não encontrado com o ID: "
                                                    + dto.getCustomerId()));

        var cart = cartConverter.fromDTO(dto, customer);
        cart = cartRepository.save(cart);
        return cartConverter.toResponseDTO(cart);

    }

    public CartResponseDTO update(Long id, CartRequestDTO dto) {
        var cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado com o ID: " + id));
        var customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + dto.getCustomerId()));
        cartConverter.updateEntityFromDTO(cart, dto, customer);
        cart = cartRepository.save(cart);
        return cartConverter.toResponseDTO(cart);
    }

    public CartResponseDTO findById(Long id) {
        var cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado com o ID: " + id));
        return cartConverter.toResponseDTO(cart);
    }

    public java.util.List<CartResponseDTO> findAll() {
        return cartRepository.findAll().stream().map(cartConverter::toResponseDTO).toList();
    }

    public void delete(Long id) {
        var cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado com o ID: " + id));
        cartRepository.delete(cart);
    }

    public CartResponseDTO addItemToCart(Long cartId, CartItemRequestDTO dto) {
        // 1. Busque o Cart (CartRepository)
        var cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado com o ID: " + cartId));
        // 1.1. Verificar propriedade: usuário autenticado deve ser o dono do carrinho
        // Obtém o username (email) do contexto de segurança
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            // Buscar customer pelo email e comparar com o dono do carrinho
            var maybeCustomer = customerRepository.findByEmail(username);
            if (maybeCustomer.isPresent()) {
                var currentCustomer = maybeCustomer.get();
                var owner = cart.getCustomer();
                if (owner != null && !owner.getId().equals(currentCustomer.getId())) {
                    // Usuário autenticado não é o dono do carrinho
                    throw new org.springframework.security.access.AccessDeniedException("Você não possui permissão para acessar este recurso");
                }
            }
        }
        // 2. Busque o Product (ProductRepository)
        var product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + dto.getProductId()));
        // 3. Chame cart.addItem(product, dto.getQuantity())
        cart.addItem(product, dto.getQuantity());
        // 4. Salve o Cart (CartRepository) e retorne o ResponseDTO
        cart = cartRepository.save(cart);
        return cartConverter.toResponseDTO(cart);
    }

    public void removeItemFromCart(Long cartId, Long productId) {
        var cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado com o ID: " + cartId));
        cart.removeItem(productId);
        cartRepository.save(cart);
    }

}
