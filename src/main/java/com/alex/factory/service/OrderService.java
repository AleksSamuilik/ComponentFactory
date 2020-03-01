package com.alex.factory.service;

import com.alex.factory.dto.*;
import com.alex.factory.exception.CompFactNoSuchElementException;
import com.alex.factory.exception.CompFactOrderNotFoundException;
import com.alex.factory.mapper.OrderMapper;
import com.alex.factory.model.Order;
import com.alex.factory.model.Product;
import com.alex.factory.model.ProductDetails;
import com.alex.factory.model.User;
import com.alex.factory.repository.OrderRepository;
import com.alex.factory.repository.ProductDetailsRepository;
import com.alex.factory.repository.ProductRepository;
import com.alex.factory.repository.UserRepository;
import com.alex.factory.utils.InitBusinessArgs;
import com.alex.factory.utils.ParamsBusinessLogic;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Log
public class OrderService {


    private final InitBusinessArgs initBusinessArgs;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ParamsBusinessLogic paramsBusinessLogic;
    private final ProductDetailsRepository productDetailsRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;


    @Transactional
    public BriefDescriptOrder newOrder(final NewOrderRequest request, final String email) {
        final User user = userRepository.findByEmail(email).get();
        return orderMapper.destinationToSourceBriefDescriptOrder(save(request, user));
    }


    private Order save(final NewOrderRequest request, final User user) {
        final List<ProductDetails> productDetailsList = saveProductDetails(request);
        final Order order = saveOrder(request, user, productDetailsList);
        return order;
    }

    private int addSurcharge(final int primeCost) {
        return (int) Math.ceil(primeCost * (100 + paramsBusinessLogic.getSURCHARGE_DEFAULT()) / 100);
    }

    private List<ProductDetails> saveProductDetails(final NewOrderRequest request) {
        final List<ProductDetails> productDetailsList = new ArrayList<>();
        final List<ProductDetailsDTO> requestProdDet = request.getProductDetails();
        requestProdDet.forEach(element -> {
            final Optional<Product> product = productRepository.findById(element.getId());
            final int sellCost = addSurcharge(product.get().getPrimeCost());
            final ProductDetails productDetails = new ProductDetails();
            productDetails.setProduct(product.get());
            productDetails.setSellCost(sellCost);
            productDetails.setQuantity(element.getQuantity());
            final ProductDetails savedProductDetails = productDetailsRepository.save(productDetails);
            productDetailsList.add(savedProductDetails);
        });
        return productDetailsList;
    }

    private Order saveOrder(final NewOrderRequest request, final User user, final List<ProductDetails> productDetailsList) {
        final Order order = new Order();
        order.setUser(user);
        order.setStartDate(request.getStartDate());
        final LocalDate endDate = initBusinessArgs.getCompletionDate(request.getEndDate());
        order.setEndDate(endDate);
        order.setStatus("waits confirmation");
        final int discount = user.getUsersDescription().getRelationType()
                .getCompanyDescription().getDiscount();
        order.setCost(initBusinessArgs.countCost(productDetailsList, discount));
        order.setProductDetails(productDetailsList);
        final Order savedOrder = orderRepository.save(order);
        return savedOrder;
    }


    public Order updateOrder(final Long orderId, final UpdateOrderDTO request) throws CompFactNoSuchElementException {
        final Order order = orderRepository.findById(orderId).orElseThrow(() -> new CompFactNoSuchElementException("Such order doesn't exist"));
        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
        }
        return orderRepository.save(order);
    }

    public List<BriefDescriptOrder> getAllOrder() {
        return orderRepository.findAll().stream().map(orderMapper::destinationToSourceBriefDescriptOrder).collect(Collectors.toList());
    }


    @Transactional
    public void changeStatus(final Order order) {
        order.setStatus("confirmed");
        orderRepository.save(order);
    }

    public OrderDTO getOrder(final Long id) throws CompFactOrderNotFoundException {
        final Order order = orderRepository.findById(id).orElseThrow(() -> new CompFactOrderNotFoundException("Order not found"));
        return orderMapper.destinationToSource(order);
    }

    @Transactional
    public void deleteOrder(final Order order) {
        orderRepository.deleteById(order.getId());
    }

    public void register(final Long orderId, final Submit isConfirmed) throws CompFactOrderNotFoundException {
        final Order order = orderRepository.findById(orderId).orElseThrow(() -> new CompFactOrderNotFoundException("Order not found"));
        boolean isConfirm = Boolean.parseBoolean(isConfirmed.getIsConfirmed());
        if (isConfirm) {
            changeStatus(order);
        } else {
            deleteOrder(order);
        }
    }


    public void delOrder(final Long orderId) throws CompFactOrderNotFoundException {
        final Order order = orderRepository.findById(orderId).orElseThrow(() -> new CompFactOrderNotFoundException("Order not found"));
        deleteOrder(order);
    }

    public OperationOrderResponse operationOrder(OperationOrder operationOrderRequest) throws CompFactNoSuchElementException {
        final OperationOrderResponse operationOrderResponse = new OperationOrderResponse();
        if (operationOrderRequest.getCost() != null&&operationOrderRequest.getStatus() != null){
            final Optional<List<Order>> orderList = orderRepository.findAllByStatusAndCostAfter(operationOrderRequest.getStatus(),operationOrderRequest.getCost());
            if (!orderList.isEmpty()) {
                final List<BriefDescriptOrder> briefDescriptOrderList = orderList.get().stream().map(orderMapper::destinationToSourceBriefDescriptOrder).collect(Collectors.toList());
                operationOrderResponse.setListOrdersStatusAndCost(briefDescriptOrderList);
            }
        }else {
            if (operationOrderRequest.getCost() != null) {
                final Optional<List<Order>> orderList = orderRepository.findAllByCostAfter(operationOrderRequest.getCost());
                if (!orderList.isEmpty()) {
                    final List<BriefDescriptOrder> briefDescriptOrderList = orderList.get().stream().map(orderMapper::destinationToSourceBriefDescriptOrder).collect(Collectors.toList());
                    operationOrderResponse.setListOrdersCost(briefDescriptOrderList);
                }
            }
            if (operationOrderRequest.getStatus() != null) {
                final Optional<List<Order>> orderList = orderRepository.findAllByStatus(operationOrderRequest.getStatus());
                if (!orderList.isEmpty()) {
                    final List<BriefDescriptOrder> briefDescriptOrderList = orderList.get().stream().map(orderMapper::destinationToSourceBriefDescriptOrder).collect(Collectors.toList());
                    operationOrderResponse.setListOrderStatus(briefDescriptOrderList);
                }
            }
        }
        log.info(operationOrderResponse.toString());


        return operationOrderResponse;
    }
}
