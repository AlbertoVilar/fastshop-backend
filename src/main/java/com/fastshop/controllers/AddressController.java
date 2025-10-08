package com.fastshop.controllers;

import com.fastshop.dto.AddressRequestDTO;
import com.fastshop.dto.AddressResponseDTO;
import com.fastshop.services.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/addresses")
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // CRUD endpoints apenas com assinatura
    @PostMapping
    public AddressResponseDTO create(@RequestBody @Valid AddressRequestDTO dto) {
        return addressService.createAddress(dto);
    }

    @PutMapping("/{id}")
    public AddressResponseDTO update(@PathVariable Long id, @RequestBody @Valid AddressRequestDTO dto) {
        return addressService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public AddressResponseDTO findById(@PathVariable Long id) {
        return addressService.findById(id);
    }

    @GetMapping
    public List<AddressResponseDTO> findAll() {
        return addressService.searchAllAddresses();
    }
}
