package com.alex.factory.dto;

import com.alex.factory.model.Product;
import lombok.Data;

@Data
public class ProductDetailsDTO {

    private Long id;

    private Product product;

    private int quantity;

    private int sellCost;

}
