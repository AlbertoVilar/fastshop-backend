package com.fastshop.mappers;

import com.fastshop.dto.RoleRequestDTO;
import com.fastshop.dto.RoleResponseDTO;
import com.fastshop.entities.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleConverter {

    public Role fromDTO(RoleRequestDTO dto) {
        return new Role(dto.getAuthority());
    }

    public void updateEntityFromDTO(RoleRequestDTO dto, Role role) {
        role.setAuthority(dto.getAuthority());
    }

    public RoleResponseDTO toResponseDTO(Role role) {
        return RoleResponseDTO.builder()
                .id(role.getId())
                .authority(role.getAuthority())
                .build();
    }
}