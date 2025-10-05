package com.fastshop.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequestDTO {
    private String name;
    private String email;
    private LocalDate birthDate;
    private String phone;
    private String cpfOrCnpj;
    private List<Long> addressIds;
}
