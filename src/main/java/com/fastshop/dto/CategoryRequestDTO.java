package com.fastshop.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDTO {
    private String name;
    private String description;
    private List<ProductRequestDTO> products;
}
