package com.alex.factory.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public abstract class AbstractAccount {
    private Long id;
    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String fullName;
    @NotNull
    private String phone;
}
