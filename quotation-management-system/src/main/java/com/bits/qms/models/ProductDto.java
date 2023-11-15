package com.bits.qms.models;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private String productId;
    private String name;
    private String lob;
    private String description;
}
