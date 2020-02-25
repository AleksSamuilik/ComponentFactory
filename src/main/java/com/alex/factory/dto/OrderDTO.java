package com.alex.factory.dto;

import com.alex.factory.model.ProductDetails;
import com.alex.factory.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OrderDTO {

    private Long id;

    private User user;
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate startDate;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate endDate;

    private Long cost;

    private String status;

    private List<ProductDetails> productDetails;

}
