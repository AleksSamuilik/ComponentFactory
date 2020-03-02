package com.alex.factory.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Factory extends AbstractAccount{
    @NotNull
    private String position;

}
