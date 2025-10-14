package com.fastshop.repositories;

import com.fastshop.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    boolean existsByIdAndCustomerId(Long id, Long customerId);

    Optional<Cart> findByCustomerId(Long customerId);
}
