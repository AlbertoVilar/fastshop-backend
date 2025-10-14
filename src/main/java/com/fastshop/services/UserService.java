package com.fastshop.services;

import com.fastshop.dto.UserRequestDTO;
import com.fastshop.dto.UserResponseDTO;
import com.fastshop.entities.User;
import com.fastshop.mappers.UserDTOConverter;
import com.fastshop.repositories.UserRepository;
import com.fastshop.exceptions.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserDTOConverter userDTOConverter;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserDTOConverter userDTOConverter, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userDTOConverter = userDTOConverter;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com username: " + username));
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream()
                .map(userDTOConverter::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        return userDTOConverter.toResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO create(UserRequestDTO dto) {
        User user = userDTOConverter.fromRequestDTO(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user = userRepository.save(user);
        return userDTOConverter.toResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        userDTOConverter.updateEntityFromDTO(user, dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user = userRepository.save(user);
        return userDTOConverter.toResponseDTO(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com username: " + username));
        return userDTOConverter.toResponseDTO(user);
    }
}
