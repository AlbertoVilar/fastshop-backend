package com.fastshop.services;

import com.fastshop.dto.CustomerRequestDTO;
import com.fastshop.dto.CustomerResponseDTO;
import com.fastshop.entities.Address;
import com.fastshop.entities.Customer;
import com.fastshop.exceptions.ResourceNotFoundException;
import com.fastshop.mappers.CustomerConverter;
import com.fastshop.repositories.AddressRepository;
import com.fastshop.repositories.CustomerRepository;
import com.fastshop.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository repository;
    private final CustomerConverter converter;

    public CustomerService(CustomerRepository customerRepository,
                           CustomerConverter converter, AddressService addressService,
                           AddressRepository addressRepository, OrderRepository repository) {
        this.customerRepository = customerRepository;
        this.converter = converter;
        this.addressRepository = addressRepository;
        this.repository = repository;
    }
    public CustomerResponseDTO creatCustomer(CustomerRequestDTO requestDTO) {

        List<Address> addresses = addressRepository.findAllById(requestDTO.getAddressIds());
        if (addresses.size() != requestDTO.getAddressIds().size()) {
            throw new IllegalArgumentException("Um ou mais endereços não foram encontrados.");
        }
        var customer = converter.fromDTO(requestDTO, addresses);
        // Setar o customer em cada address para manter o relacionamento bidirecional

        for (Address address : addresses) {
            address.setCustomer(customer);
        }
        customer = customerRepository.save(customer);
        return converter.customerResponseDTO(customer);
    }

    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO requestDTO) {
        if (id == null) {
            throw new IllegalArgumentException("O ID do cliente é obrigatório para atualização.");
        }
        var existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + id));
        List<Address> addresses = addressRepository.findAllById(requestDTO.getAddressIds());
        if (addresses.size() != requestDTO.getAddressIds().size()) {
            throw new IllegalArgumentException("Um ou mais endereços não foram encontrados.");
        }
        converter.updateEntityFromDTO(requestDTO, existingCustomer, addresses);
        // Setar o customer em cada address para manter o relacionamento bidirecional
        for (Address address : addresses) {
            address.setCustomer(existingCustomer);
        }
        existingCustomer = customerRepository.save(existingCustomer);
        return converter.customerResponseDTO(existingCustomer);

    }

    public CustomerResponseDTO findById(Long id) {
        var customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + id));
        return converter.customerResponseDTO(customer);
    }

    public List<CustomerResponseDTO> findAll() {
        return customerRepository.findAll()
            .stream()
            .map(converter::customerResponseDTO)
            .toList();
    }

    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente não encontrado com o ID: " + id);
        }
        customerRepository.deleteById(id);
    }

}
