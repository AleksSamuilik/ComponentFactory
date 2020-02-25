package com.alex.factory.dto;

import lombok.Data;

@Data
public class ProductDTO {

    private Long id;

    private String name;

    private String type;

    private int primeCost;

    private String category;
}
