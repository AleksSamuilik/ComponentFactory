package com.alex.factory.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Submit {
  @NotNull
  String isConfirmed;
}
