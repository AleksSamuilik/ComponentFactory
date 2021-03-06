package com.alex.factory.controller;

import com.alex.factory.dto.*;
import com.alex.factory.exception.CompFactNoSuchElementException;
import com.alex.factory.exception.CompFactOrderNotFoundException;
import com.alex.factory.service.OrderService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Api(value = "Orders service")
public class OrderController {

    private final OrderService orderService;

    @PostMapping(value = "/new")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create order", notes = "Use this method, if you want to create order")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully create order"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public BriefDescriptOrder createNewOrder(@ApiParam(value = "New order data") @Valid @RequestBody final NewOrderRequest newOrderRequest, final Authentication authentication) {
        return orderService.newOrder(newOrderRequest, authentication.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Operations with order data", notes = "Use this method, if you want use  operations with order data")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully create order"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public OperationOrderResponse operationOrder(@ApiParam(value = "Operations order data") @Valid @RequestBody final OperationOrder operationOrderRequest) throws CompFactNoSuchElementException {
        return orderService.operationOrder(operationOrderRequest);
    }

    @ApiOperation(value = "Confirm order status", notes = "Use this method, if you want to confirm order, or canceled")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully confirm order or canceled order"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @PostMapping(value = "/{orderId}/submit", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void register(@Valid @RequestBody Submit isConfirmed, @PathVariable final Long orderId) throws CompFactOrderNotFoundException {
        orderService.register(orderId, isConfirmed);
    }


    @GetMapping(value = "/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "View order", notes = "Use this method, if you want to view order")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully get order"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public OrderDTO getOrder(@PathVariable final Long orderId) throws CompFactOrderNotFoundException {
        return orderService.getOrder(orderId);
    }

    @PutMapping(value = "/{orderId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update order data", notes = "Use this method, if you want to update order data")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully update order data"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public void updateStatusOrder(@Valid @RequestBody UpdateOrderDTO request, @PathVariable final Long orderId) throws CompFactNoSuchElementException {
        orderService.updateOrder(orderId, request);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "View all orders", notes = "Use this method, if you want to view all orders")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully get all orders"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public List<BriefDescriptOrder> getAllOrder() {
        return orderService.getAllOrder();
    }

    @DeleteMapping(value = "/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete order", notes = "Use this method, if you want to delete order")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully delete order"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public void delOrder(@PathVariable final Long orderId) throws CompFactOrderNotFoundException {
        orderService.delOrder(orderId);
    }
}