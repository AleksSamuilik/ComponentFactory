package com.alex.factory.controller;

import com.alex.factory.model.Order;
import com.alex.factory.model.Product;
import com.alex.factory.model.ProductDetails;
import com.alex.factory.model.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderControllerTest extends AbstractControllerTest {

    @Test
    @SneakyThrows
    public void testNewOrder() {
        //given
        final User user = getUser();
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        final Map<Long, Product> productMap = createProductMap();
        given(productRepository.findById(1l)).willReturn(Optional.of(productMap.get(1l)));
        given(productRepository.findById(2l)).willReturn(Optional.of(productMap.get(2l)));
        final Order order = createOrder(1l);
        given(orderRepository.save(any(Order.class))).willReturn(order);
        //when
        mockMvc.perform(post("/orders/new").header("Authorization", signInAsRoleUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"productDetails\": [\n" +
                        "        {\n" +
                        "            \"id\": 1,\n" +
                        "            \"quantity\": 10000\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"id\": 2,\n" +
                        "            \"quantity\": 4\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"startDate\": \"12.02.2020\",\n" +
                        "    \"endDate\": \"16.03.2020\"\n" +
                        "}"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\n" +
                        " \"id\": 1,\n" +
                        " \"cost\": 756201,\n" +
                        "    \"startDate\": \"12.02.2020\",\n" +
                        "    \"endDate\": \"12.03.2020\",\n" +
                        "    \"status\": \"waits confirmation\"\n" +
                        "}"));
        //then
        verify(authInfoRepository, times(2)).findByLogin(anyString());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(productRepository, times(2)).findById(anyLong());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @SneakyThrows
    public void testConfirmationOrderRegistration() {
        // given
        final Order order = getOrder();
        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));
        // when
        mockMvc.perform(post("/orders/1/submit").header("Authorization", signInAsRoleUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "\"isConfirmed\": \"true\"\n" +
                        "}"))
                .andExpect(status().isOk());
        //then
        verify(authInfoRepository, times(2)).findByLogin(anyString());
        verify(orderRepository, times(1)).findById(anyLong());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @SneakyThrows
    public void testConfirmationOrderRegistrationWithoutNotAuthorisation() {
        mockMvc.perform(post("/orders/1/submit").header("Authorization", signInAsRoleAdmin())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "\"isConfirmed\": \"true\"\n" +
                        "}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void testCancelOrderRegistration() {
        // given
        final Order order = getOrder();
        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));
        // when
        mockMvc.perform(post("/orders/1//submit").header("Authorization", signInAsRoleUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "\"isConfirmed\": \"false\"\n" +
                        "}"))
                .andExpect(status().isOk());
        //then
        verify(authInfoRepository, times(2)).findByLogin(anyString());
        verify(orderRepository, times(1)).findById(anyLong());
        verify(orderRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @SneakyThrows
    public void testOrderList() {
        // given
        final List<Order> orderList = getOrderList();
        given(orderRepository.findAll()).willReturn(orderList);
        //when
        mockMvc.perform(get("/orders").header("Authorization", signInAsRoleAdmin()))
                .andExpect(status().isOk())
                .andExpect(content().json("[\n" +
                        "   {\n" +
                        "      \"id\":1,\n" +
                        "      \"startDate\":\"12.02.2020\",\n" +
                        "      \"endDate\":\"12.03.2020\",\n" +
                        "      \"cost\":756201,\n" +
                        "      \"status\":\"waits confirmation\"\n" +
                        "   },\n" +
                        "   {\n" +
                        "      \"id\":2,\n" +
                        "      \"startDate\":\"12.02.2020\",\n" +
                        "      \"endDate\":\"12.03.2020\",\n" +
                        "      \"cost\":756201,\n" +
                        "      \"status\":\"waits confirmation\"\n" +
                        "   },\n" +
                        "   {\n" +
                        "      \"id\":3,\n" +
                        "      \"startDate\":\"12.02.2020\",\n" +
                        "      \"endDate\":\"12.03.2020\",\n" +
                        "      \"cost\":756201,\n" +
                        "      \"status\":\"waits confirmation\"\n" +
                        "   }\n" +
                        "]"));
        //then
        verify(authInfoRepository, times(2)).findByLogin(anyString());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @SneakyThrows
    public void testOrderListWithoutNotAuthorisation() {
        mockMvc.perform(get("/orders").header("Authorization", signInAsRoleUser()))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void testGetNumberOrder() {
        // given
        final Order order = getOrder();
        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));
        //when
        mockMvc.perform(get("/orders/1/").header("Authorization", signInAsRoleAdmin()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "   \"id\": 1,\n" +
                        "   \"user\":{\n" +
                        "      \"id\":1,\n" +
                        "      \"email\":\"vasya@email.com\",\n" +
                        "      \"fullName\":\"Пупкин Василий Иванович\",\n" +
                        "      \"usersDescription\":{\n" +
                        "         \"id\":1,\n" +
                        "         \"relationType\":{\n" +
                        "            \"id\":1,\n" +
                        "            \"companyDescription\":{\n" +
                        "               \"id\":1,\n" +
                        "               \"company\":\"ООО\\\"Аливария\\\"\",\n" +
                        "               \"phone\":\"+375445333880\",\n" +
                        "               \"info\":\"Пивоварня №1 в СНГ\",\n" +
                        "               \"discount\":10\n" +
                        "            }\n" +
                        "         }\n" +
                        "      }\n" +
                        "   },\n" +
                        "   \"startDate\":\"12.02.2020\",\n" +
                        "   \"endDate\":\"12.03.2020\",\n" +
                        "   \"cost\":756201,\n" +
                        "   \"status\":\"waits confirmation\",\n" +
                        "   \"productDetails\":[\n" +
                        "      {\n" +
                        "         \"product\":{\n" +
                        "            \"id\":1,\n" +
                        "            \"name\":\"Бутылка\",\n" +
                        "            \"type\":\"0.5\",\n" +
                        "            \"primeCost\":60,\n" +
                        "            \"category\":\"Тара для хранения\"\n" +
                        "         },\n" +
                        "         \"quantity\":10000,\n" +
                        "         \"sellCost\":84\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"product\":{\n" +
                        "            \"id\":2,\n" +
                        "            \"name\":\"Воздушный фильтр\",\n" +
                        "            \"type\":\"0.1\",\n" +
                        "            \"primeCost\":40,\n" +
                        "            \"category\":\"Фильтрация и сорбирование\"\n" +
                        "         },\n" +
                        "         \"quantity\":4,\n" +
                        "         \"sellCost\":56\n" +
                        "      }\n" +
                        "   ]\n" +
                        "}"));
        //then
        verify(authInfoRepository, times(2)).findByLogin(anyString());
        verify(orderRepository, times(1)).findById(anyLong());
    }

    @Test
    @SneakyThrows
    public void testGetNumberOrderWithoutAccess() {
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void testUpdateStatusToWorkOrder1() {
        // given
        final Order order = getOrder();
        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));
        // when
        mockMvc.perform(put("/orders/1").header("Authorization", signInAsRoleAdmin())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"status\":\"work\" }"))
                .andExpect(status().isOk());
        //then
        verify(authInfoRepository, times(2)).findByLogin(anyString());
        verify(orderRepository, times(1)).findById(anyLong());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @SneakyThrows
    public void testUpdateTimeAndCostOrder1() {
        // given
        final Order order = getOrder();
        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));
        // when
        mockMvc.perform(put("/orders/1").header("Authorization", signInAsRoleAdmin())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{   \"endDate\":\"15.03.2020\",\n" +
                        "   \"cost\":900000\n" +
                        "}"))
                .andExpect(status().isOk());
        //then
        verify(authInfoRepository, times(2)).findByLogin(anyString());
        verify(orderRepository, times(1)).findById(anyLong());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @SneakyThrows
    public void testUpdateStatusToCloseOrder1() {
        // given
        final Order order = getOrder();
        given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));
        // when
        mockMvc.perform(put("/orders/1").header("Authorization", signInAsRoleAdmin())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"status\":\"close\" }"))
                .andExpect(status().isOk());
        //then
        verify(authInfoRepository, times(2)).findByLogin(anyString());
        verify(orderRepository, times(1)).findById(anyLong());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @SneakyThrows
    public void testUpdateStatusWithoutNotAuthorisation() {
        mockMvc.perform(put("/orders/1").header("Authorization", signInAsRoleUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"status\":\"work\" }"))
                .andExpect(status().isForbidden());
    }

    private Order getOrder() {
        final Order order = createOrder(1l);
        return order;
    }

    private List<Order> getOrderList() {
        return Arrays.asList(createOrder(1l), createOrder(2l), createOrder(3l));
    }

    private Order createOrder(final Long id) {
        final Order order = new Order();
        order.setId(id);
        order.setUser(getUser());
        order.setStartDate(LocalDate.parse("2020-02-12"));
        order.setEndDate(LocalDate.parse("2020-03-12"));
        order.setCost(756201l);
        order.setStatus("waits confirmation");
        order.setProductDetails(Arrays.asList(createProdDet(1l, 1l, 10000), createProdDet(2l, 2l, 4)));
        return order;
    }

    private ProductDetails createProdDet(final Long id, final Long prodId, final int quantity) {
        final ProductDetails productDetails = new ProductDetails();
        productDetails.setId(id);
        productDetails.setProduct(createProductMap().get(prodId));
        productDetails.setQuantity(quantity);
        int cost = addSurcharge(createProductMap().get(prodId).getPrimeCost());
        productDetails.setSellCost(cost);
        return productDetails;
    }

    private int addSurcharge(final int primeCost) {
        return (int) Math.ceil(primeCost * (100 + paramsBusinessLogic.getSURCHARGE_DEFAULT()) / 100);
    }
}