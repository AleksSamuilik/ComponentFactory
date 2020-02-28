package com.alex.factory.controller;

import com.alex.factory.dto.ProductDTO;
import com.alex.factory.exception.ProductNotFoundException;
import com.alex.factory.service.ProductService;
import lombok.Data;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log
@Data
@RestController
@RequestMapping(value = "/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDTO> getAllProducts() {
        return productService.getAll();
    }


    @GetMapping(value = "/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDTO getOrder(@PathVariable final Long productId, final Authentication authentication) throws ProductNotFoundException {
        return productService.getProduct(productId);
    }


}
