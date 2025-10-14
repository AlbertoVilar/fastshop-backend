package com.fastshop.security;

import com.fastshop.repositories.CartRepository;
import com.fastshop.repositories.CustomerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("cartSecurity")
public class CartSecurity {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;

    public CartSecurity(CartRepository cartRepository, CustomerRepository customerRepository) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    public boolean isOwner(Long cartId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return false;
        String username = authentication.getName();
        var maybeCustomer = customerRepository.findByEmail(username);
        if (maybeCustomer.isEmpty()) return false;
        var customerId = maybeCustomer.get().getId();
        return cartRepository.existsByIdAndCustomerId(cartId, customerId);
    }

    public boolean cartExists(Long cartId) {
        return cartRepository.existsById(cartId);
    }

    public boolean canCreateForCustomer(Long customerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return false;
        // Admin pode criar para qualquer cliente
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (isAdmin) return true;
        String username = authentication.getName();
        var maybeCustomer = customerRepository.findByEmail(username);
        return maybeCustomer.isPresent() && maybeCustomer.get().getId().equals(customerId);
    }
}