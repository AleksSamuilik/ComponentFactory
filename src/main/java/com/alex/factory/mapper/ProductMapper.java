package com.alex.factory.mapper;

import com.alex.factory.dto.ProductDTO;
import com.alex.factory.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product sourceToDestination(ProductDTO source);

    ProductDTO destinationToSource(Product destination);
}
