package com.fastshop.services;

import com.fastshop.dto.AddressRequestDTO;
import com.fastshop.dto.AddressResponseDTO;
import com.fastshop.entities.Address;
import com.fastshop.exceptions.ResourceNotFoundException;
import com.fastshop.mappers.AddressConverter;
import com.fastshop.repositories.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressConverter addressConverter;

    public AddressService(AddressRepository addressRepository, AddressConverter addressConverter) {
        this.addressRepository = addressRepository;
        this.addressConverter = addressConverter;
    }

    // CRUD
   public AddressResponseDTO createAddress(AddressRequestDTO dto) {
        var address = addressConverter.fronDto(dto);
        address = addressRepository.save(address);
        return addressConverter.toResponseDTO(address);
   }

   public AddressResponseDTO findById(Long id) {
        var address = addressRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Endereço com ID " + id + " não encontrado"));
        return addressConverter.toResponseDTO(address);
   }

   public List<AddressResponseDTO> searchAllAddresses() {

      List<Address> addresses = addressRepository.findAll();
      return addresses.stream().map(addressConverter::toResponseDTO).toList();
   }

   public AddressResponseDTO update(Long id, AddressRequestDTO dto) {
        if (id == null) {
            throw new IllegalArgumentException("O ID não pode ser nulo.");
        }
        var address = addressRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Endereço com ID " + id + " não encontrado"));
        addressConverter.updateAddress(address, dto);
         addressRepository.save(address);
        return addressConverter.toResponseDTO(address);

   }
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O ID não pode ser nulo.");
        }
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço com ID " + id + " não encontrado"));
        addressRepository.delete(address);
    }

}
