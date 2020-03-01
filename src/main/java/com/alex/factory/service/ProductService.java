package com.alex.factory.service;

import com.alex.factory.dto.ProductCostDTO;
import com.alex.factory.dto.ProductDTO;
import com.alex.factory.exception.CompFactProductNotFoundException;
import com.alex.factory.mapper.ProductMapper;
import com.alex.factory.model.Product;
import com.alex.factory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;

    public List<ProductDTO> getAll() {
        return productRepository.findAll().stream().map(productMapper::destinationToSource).collect(Collectors.toList());
    }

    public ProductDTO getProduct(Long productId) throws CompFactProductNotFoundException {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            return productMapper.destinationToSource(product.get());
        }
        throw new CompFactProductNotFoundException("There is no such product");
    }

    @Transactional
    public void addProduct(ProductDTO productDTORequest) {
        final Product product = productMapper.sourceToDestination(productDTORequest);
        productRepository.save(product);
    }

    @Transactional
    public void updateCostProduct(Long productId, ProductCostDTO productCostDTORequest) throws CompFactProductNotFoundException {
        final Product product = productRepository.findById(productId).orElseThrow(() -> new CompFactProductNotFoundException("Product not found"));
        product.setPrimeCost(productCostDTORequest.getPrimeCost());
    }
}