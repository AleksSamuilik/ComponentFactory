package com.alex.factory.service;

import com.alex.factory.dto.ProductDTO;
import com.alex.factory.exception.ProductNotFoundException;
import com.alex.factory.mapper.ProductMapper;
import com.alex.factory.model.Product;
import com.alex.factory.repository.ProductRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;

    public List<ProductDTO> getAll() {
        return productRepository.findAll().stream().map(productMapper::destinationToSource).collect(Collectors.toList());
    }

    public ProductDTO getProduct(Long productId) throws ProductNotFoundException {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            return   productMapper.destinationToSource(product.get());
        }
        throw new ProductNotFoundException("There is no such product");
    }
}