package com.fastshop.mappers;

import com.fastshop.dto.AddressRequestDTO;
import com.fastshop.dto.AddressResponseDTO;
import com.fastshop.entities.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressConverter {

    public Address fronDto(AddressRequestDTO requestDTO) {
    return Address.builder()
            .street(requestDTO.getStreet())
            .neighborhood(requestDTO.getNeighborhood())
            .city(requestDTO.getCity())
            .state(requestDTO.getState())
            .zipCode(requestDTO.getZipCode())
            .country(requestDTO.getCountry())
            .build();
    }

    public void updateAddress(Address entity, AddressRequestDTO requestDTO) {
        entity.setStreet(requestDTO.getStreet() != null ? requestDTO.getStreet() : entity.getStreet());
        entity.setNeighborhood(requestDTO.getNeighborhood() != null ? requestDTO.getNeighborhood() : entity.getNeighborhood());
        entity.setCity(requestDTO.getCity() != null ? requestDTO.getCity() : entity.getCity());
        entity.setState(requestDTO.getState() != null ? requestDTO.getState() : entity.getState());
        entity.setZipCode(requestDTO.getZipCode() != null ? requestDTO.getZipCode() : entity.getZipCode());
        entity.setCountry(requestDTO.getCountry() != null ? requestDTO.getCountry() : entity.getCountry());

    }

    public AddressResponseDTO toResponseDTO(Address entity) {
        return AddressResponseDTO.builder()
                .id(entity.getId())
                .street(entity.getStreet())
                .neighborhood(entity.getNeighborhood())
                .city(entity.getCity())
                .state(entity.getState())
                .zipCode(entity.getZipCode())
                .country(entity.getCountry())
                .build();
    }
}

