package com.alex.factory.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Company extends AbstractAccount {
    @NotNull
    private String company;
    @NotNull
    private String info;
    @NotNull
    private int discount;
}
