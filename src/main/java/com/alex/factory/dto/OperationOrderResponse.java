package com.alex.factory.dto;

import lombok.Data;

import java.util.List;

@Data
public class OperationOrderResponse {

    private List<BriefDescriptOrder> listOrderStatus;

    private List<BriefDescriptOrder> listOrdersCost;

    private List<BriefDescriptOrder> listOrdersStatusAndCost;

}
