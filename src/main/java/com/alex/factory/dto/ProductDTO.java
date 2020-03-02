package com.alex.factory.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ProductDTO {

    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String type;
    @NotNull
    private int primeCost;
    @NotNull
    private String category;
}
