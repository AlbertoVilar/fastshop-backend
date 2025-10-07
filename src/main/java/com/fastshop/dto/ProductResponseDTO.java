package com.fastshop.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price; // representa o unitPrice persistido
    private Integer stock;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    // private CategoryResponseDTO category; // Comentado, pois categoryName já é suficiente
}
