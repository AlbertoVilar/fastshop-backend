package com.fastshop.mappers;

import com.fastshop.dto.UserRequestDTO;
import com.fastshop.dto.UserResponseDTO;
import com.fastshop.entities.User;
import com.fastshop.entities.Role;
import com.fastshop.dto.RoleResponseDTO;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class UserDTOConverter {
    public UserResponseDTO toResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roles(user.getRoles() != null ? user.getRoles().stream()
                        .map(this::toRoleResponseDTO)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    public User fromRequestDTO(UserRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        // password será setado no service
        return user;
    }

    public void updateEntityFromDTO(User user, UserRequestDTO dto) {
        user.setUsername(dto.getUsername());
        // password será setado no service
    }

    private RoleResponseDTO toRoleResponseDTO(Role role) {
        return RoleResponseDTO.builder()
                .id(role.getId())
                .authority(role.getAuthority())
                .build();
    }
}

