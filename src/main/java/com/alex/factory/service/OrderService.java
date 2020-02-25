package com.alex.factory.service;

import com.alex.factory.dto.NewOrder;
import com.alex.factory.dto.OrderDTO;
import com.alex.factory.dto.ProductDetailsDTO;
import com.alex.factory.dto.UpdateOrderStatus;
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
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log
@Service
public class OrderService {

    @Autowired
    private InitBusinessArgs initBusinessArgs;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ParamsBusinessLogic paramsBusinessLogic;
    @Autowired
    private ProductDetailsRepository productDetailsRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderMapper orderMapper;


    @Transactional
    public OrderDTO newOrder(final NewOrder request, final String email) {
        final User user = userRepository.findByEmail(email).get();
        return orderMapper.destinationToSource(save(request, user));
    }

    private Order save(final NewOrder request, final User user) {
        final List<ProductDetails> productDetailsList = saveProductDetails(request);
        final Order order = saveOrder(request, user, productDetailsList);
        return order;
    }

    private int addSurcharge(final int primeCost) {
        return (int) Math.ceil(primeCost * (100 + paramsBusinessLogic.getSURCHARGE_DEFAULT()) / 100);
    }

    private List<ProductDetails> saveProductDetails(NewOrder request) {
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

    private Order saveOrder(final NewOrder request, final User user, final List<ProductDetails> productDetailsList) {
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


    public Order updateStatus(Long orderId, UpdateOrderStatus request) {
        final Order order = orderRepository.findById(orderId).get();
        order.setStatus(request.getStatus());
        return orderRepository.save(order);
    }

    public List<OrderDTO> getAllOrder() {
        return orderRepository.findAll().stream().map(orderMapper::destinationToSource).collect(Collectors.toList());
    }


    @Transactional
    public void changeStatus(Long id, boolean isConfirmed) {
        final Order order = orderRepository.findById(id).get();
        order.setStatus("confirmed");
        orderRepository.save(order);
    }

    public OrderDTO getOrder(final Long id) {
        Optional<Order> order = orderRepository.findById(id);
        return orderMapper.destinationToSource(order.get());
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }
}
