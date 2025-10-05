package com.fastshop.dto;

import lombok.*;
import java.util.List;
import java.time.LocalDate;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponseDTO {
    private Long id;
    private String name;
    private String email;
    private LocalDate birthDate;
    private String phone;
    private String cpfOrCnpj;
    private Instant createdAt;
    private Instant updatedAt;
    private List<AddressResponseDTO> addresses;

}
