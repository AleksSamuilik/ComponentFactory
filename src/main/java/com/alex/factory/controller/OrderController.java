package com.alex.factory.controller;

import com.alex.factory.dto.NewOrder;
import com.alex.factory.dto.OrderDTO;
import com.alex.factory.dto.Submit;
import com.alex.factory.dto.UpdateOrderStatus;
import com.alex.factory.exception.OrderNotFoundException;
import com.alex.factory.model.Order;
import com.alex.factory.repository.OrderRepository;
import com.alex.factory.service.OrderService;
import lombok.Data;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Log
@Data
@RestController
@RequestMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;


    @PostMapping(value = "/new")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO createNewOrder(@RequestBody final NewOrder newOrder, final Authentication authentication) {
        return orderService.newOrder(newOrder, authentication.getName());
    }

    @PostMapping(value = "/{orderId}/submit", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void register(@RequestBody Submit isConfirmed, @PathVariable final Long orderId, final Authentication authentication) throws OrderNotFoundException {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            boolean isConfirm = Boolean.parseBoolean(isConfirmed.getIsConfirmed());
            if (isConfirm) {
                orderService.changeStatus(orderId, isConfirm);
            } else {
                orderService.deleteOrder(orderId);
            }
        } else {
            throw new OrderNotFoundException("Order not found");
        }
    }


    @GetMapping(value = "/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDTO getOrder(@PathVariable final Long orderId, final Authentication authentication) {
        return orderService.getOrder(orderId);
    }

    @PutMapping(value = "/{orderId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void updateStatus(@RequestBody UpdateOrderStatus request, @PathVariable final Long orderId, final Authentication authentication) {
        orderService.updateStatus(orderId, request);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDTO> getAllOrder(final Authentication authentication) {
        return orderService.getAllOrder();
    }
}