package com.alex.factory.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class ProductDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @NotNull
    private int quantity;
    @NotNull
    private int sellCost;
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "orders_id", nullable = false)
//    private Order order;


}
