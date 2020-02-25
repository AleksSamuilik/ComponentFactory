package com.alex.factory.dto;

import lombok.Data;

@Data
public abstract class AbstractAccount {
    private Long id;
    private String email;
    private String password;
    private String fullName;
    private String phone;
}
