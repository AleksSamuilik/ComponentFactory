package com.alex.factory.dto;

import lombok.Data;

@Data
public class Company extends AbstractAccount {
    private String company;
    private String info;

    private int discount;
}
