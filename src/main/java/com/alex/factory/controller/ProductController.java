package com.alex.factory.controller;

import com.alex.factory.dto.ProductCostDTO;
import com.alex.factory.dto.ProductDTO;
import com.alex.factory.dto.Submit;
import com.alex.factory.exception.CompFactProductNotFoundException;
import com.alex.factory.service.ProductService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Data
@RestController
@RequestMapping(value = "/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "View all products", notes = "Use this method, if you want to view all products")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully get all products"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public List<ProductDTO> getAllProducts() {
        return productService.getAll();
    }


    @GetMapping(value = "/{productId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "View product", notes = "Use this method, if you want to view product")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully get product"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ProductDTO getProduct(@PathVariable final Long productId) throws CompFactProductNotFoundException {
        return productService.getProduct(productId);
    }

    @PutMapping(value = "/{productId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update product", notes = "Use this method, if you want to update product")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully update product"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
   public void updateProduct(@Valid @RequestBody ProductCostDTO productCostDTORequest, @PathVariable final Long productId) throws CompFactProductNotFoundException {
         productService.updateCostProduct(productId, productCostDTORequest);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Add product", notes = "Use this method, if you want to add new product")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully add new product"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public void addProduct(@Valid @RequestBody ProductDTO productDTORequest) {
         productService.addProduct(productDTORequest);
    }


}
