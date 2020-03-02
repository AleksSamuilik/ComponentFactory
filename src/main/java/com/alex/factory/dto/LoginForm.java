package com.alex.factory.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginForm {
    @NotNull
    private String email;
    @NotNull
    private String password;
}
