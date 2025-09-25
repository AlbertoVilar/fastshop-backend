package com.fastshop.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
}
