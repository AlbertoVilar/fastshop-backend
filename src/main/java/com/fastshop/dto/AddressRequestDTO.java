package com.fastshop.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequestDTO {
    private String street;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;
    private String country;
}
