package com.fastshop.controllers;

import com.fastshop.dto.CustomerRequestDTO;
import com.fastshop.services.CustomerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody @Valid CustomerRequestDTO requestDTO) {
        var response = customerService.createCustomer(requestDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @customerSecurity.isOwner(#id)")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody @Valid CustomerRequestDTO requestDTO) {
        var response = customerService.updateCustomer(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @customerSecurity.isOwner(#id)")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        var response = customerService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findAll() {
        var response = customerService.findAll();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @customerSecurity.isOwner(#id)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
