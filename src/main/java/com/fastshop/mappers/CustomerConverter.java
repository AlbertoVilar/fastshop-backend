package com.fastshop.mappers;

import com.fastshop.dto.CustomerRequestDTO;
import com.fastshop.dto.CustomerResponseDTO;
import com.fastshop.entities.Address;
import com.fastshop.entities.Customer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerConverter {

    private final AddressConverter addressConverter;
    private final OrderConverter orderConverter;

    public CustomerConverter(AddressConverter addressConverter, OrderConverter orderConverter) {
        this.addressConverter = addressConverter;
        this.orderConverter = orderConverter;
    }

    public Customer fromDTO(CustomerRequestDTO dto, List<Address> addresses) {
       return Customer.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .addresses(addresses)
                .build();
    }

    public void updateEntityFromDTO(CustomerRequestDTO dto, Customer entity, List<Address> addresses) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());

        // Remove endereços que não estão mais na lista
        entity.getAddresses().removeIf(address ->
            addresses.stream().noneMatch(a -> a.getId().equals(address.getId()))
        );

        // Adiciona novos endereços
        for (Address address : addresses) {
            if (entity.getAddresses().stream().noneMatch(a -> a.getId().equals(address.getId()))) {
                address.setCustomer(entity);
                entity.getAddresses().add(address);
            }
        }
    }

    public CustomerResponseDTO customerResponseDTO(Customer entity) {
        return CustomerResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .addresses(entity.getAddresses().stream()
                        .map(addressConverter::toResponseDTO).toList())

                .build();
    }
}
