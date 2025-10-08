package com.fastshop.mappers;

import com.fastshop.dto.RoleResponseDTO;
import com.fastshop.dto.UserRequestDTO;
import com.fastshop.dto.UserResponseDTO;
import com.fastshop.entities.Role;
import com.fastshop.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserConverter {

    private final RoleConverter roleConverter;
    private final PasswordEncoder passwordEncoder;

    public UserConverter(RoleConverter roleConverter, PasswordEncoder passwordEncoder) {
        this.roleConverter = roleConverter;
        this.passwordEncoder = passwordEncoder;
    }

    // Converte DTO de requisição para entidade (codificando a senha)
    public User fromDTO(UserRequestDTO dto) {
        return User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword() != null ? passwordEncoder.encode(dto.getPassword()) : null)
                .build();
    }

    // Atualiza entidade a partir do DTO (re-encode da senha se fornecida)
    public void updateEntityFromDTO(User user, UserRequestDTO dto) {
        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
    }

    // Converte entidade para DTO de resposta, mapeando roles
    public UserResponseDTO toResponseDTO(User user) {
        List<RoleResponseDTO> roles = user.getRoles() != null
                ? user.getRoles().stream().map(roleConverter::toResponseDTO).toList()
                : List.of();

        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roles(roles)
                .build();
    }

    // Opcional: atualizar roles diretamente na entidade
    public void setRoles(User user, List<Role> roles) {
        user.setRoles(roles);
    }
}