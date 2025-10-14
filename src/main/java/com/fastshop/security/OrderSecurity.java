package com.fastshop.security;

import com.fastshop.repositories.CustomerRepository;
import com.fastshop.repositories.OrderRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("orderSecurity")
public class OrderSecurity {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public OrderSecurity(OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    public boolean isOwner(Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return false;
        String username = authentication.getName();
        var maybeCustomer = customerRepository.findByEmail(username);
        if (maybeCustomer.isEmpty()) return false;
        var customerId = maybeCustomer.get().getId();
        // Consulta indireta via exists: requer método custom; fallback usando findById
        return orderRepository.findById(orderId)
                .map(order -> order.getCustomer() != null && customerId.equals(order.getCustomer().getId()))
                .orElse(false);
    }

    public boolean canCreateForCustomer(Long customerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return false;
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (isAdmin) return true;
        String username = authentication.getName();
        var maybeCustomer = customerRepository.findByEmail(username);
        return maybeCustomer.isPresent() && maybeCustomer.get().getId().equals(customerId);
    }
}