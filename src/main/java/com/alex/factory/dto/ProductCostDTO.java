package com.alex.factory.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class ProductCostDTO {
    @NotNull
    private int primeCost;

}
