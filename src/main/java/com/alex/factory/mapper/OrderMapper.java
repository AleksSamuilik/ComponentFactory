package com.alex.factory.mapper;

import com.alex.factory.dto.BriefDescriptOrder;
import com.alex.factory.dto.OrderDTO;
import com.alex.factory.model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ProductDetailsMapper.class)
public interface OrderMapper {

    Order sourceToDestination(OrderDTO source);

    BriefDescriptOrder destinationToSourceBriefDescriptOrder(Order destination);

    OrderDTO destinationToSource(Order destination);
}
