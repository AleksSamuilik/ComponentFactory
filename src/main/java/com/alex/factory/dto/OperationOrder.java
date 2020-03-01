package com.alex.factory.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class OperationOrder {

    private String status;

    private Long cost;

}
