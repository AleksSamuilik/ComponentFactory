package com.alex.factory.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data

@Entity(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User user;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @NotNull
    private Long cost;
    @NotNull
    private String status;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "orders_productDetails",
//    @OneToMany( mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    joinColumns=@JoinColumn(name = "orders_id"),
    inverseJoinColumns = @JoinColumn(name = "productDetails_id"))

    private List<ProductDetails> productDetails;

}
