package com.alex.factory.mapper;

import com.alex.factory.dto.ProductDetailsDTO;
import com.alex.factory.model.ProductDetails;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = OrderMapper.class)
public interface ProductDetailsMapper {

    ProductDetails sourceToDestination(ProductDetailsDTO source);

    ProductDetailsDTO destinationToSource(ProductDetails destination);
}
