package com.alex.factory.controller;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class OrderControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    public void testNewOrder() {


        mockMvc.perform(post("/orders/new").header("Authorization", tokenVasya)
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
                        " \"id\": " + orderIdNext + ",\n" +
                        " \"cost\": 756201,\n" +
                        "    \"startDate\": \"12.02.2020\",\n" +
                        "    \"endDate\": \"12.03.2020\",\n" +
                        "    \"status\": \"waits confirmation\"\n" +
                        "}"));
    }

    @Test
    @SneakyThrows
    public void testRegisterOrderConfirmed() {

        mockMvc.perform(post("/orders/" + orderId + "/submit").header("Authorization", tokenVasya)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "\"isConfirmed\": \"true\"\n" +
                        "}"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void testRegisterOrderCanceled() {

        mockMvc.perform(post("/orders/" + orderId + "/submit").header("Authorization", tokenVasya)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "\"isConfirmed\": \"false\"\n" +
                        "}"))
                .andExpect(status().isOk());
    }


    @Test
    @SneakyThrows
    public void testOrderList() {

//        deleteAllOrder();
        final String firstId = createTestOrder();
        final String secondId = createTestOrder();
        final String thirdId = createTestOrder();

        mockMvc.perform(get("/orders").header("Authorization", tokenPetya))
                .andExpect(status().isOk())
                .andExpect(content().json("[\n" +
                        "{\n" +
                        " \"id\": " + firstId + ",\n" +
                        " \"cost\": 756201,\n" +
                        "    \"startDate\": \"12.02.2020\",\n" +
                        "    \"endDate\": \"12.03.2020\",\n" +
                        "    \"status\": \"waits confirmation\"\n" +
                        "},{\n" +
                        " \"id\": " + secondId + ",\n" +
                        " \"cost\": 756201,\n" +
                        "    \"startDate\": \"12.02.2020\",\n" +
                        "    \"endDate\": \"12.03.2020\",\n" +
                        "    \"status\": \"waits confirmation\"\n" +
                        "},{\n" +
                        " \"id\": " + thirdId + ",\n" +
                        " \"cost\": 756201,\n" +
                        "    \"startDate\": \"12.02.2020\",\n" +
                        "    \"endDate\": \"12.03.2020\",\n" +
                        "    \"status\": \"waits confirmation\"\n" +
                        "}]"));
    }

    @Test
    @SneakyThrows
    public void testGetNumberOrder() {

        final String orderId = createTestOrder();

        mockMvc.perform(get("/orders/" + orderId).header("Authorization", tokenDima))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "   \"id\":"+orderId+",\n" +
                        "   \"user\":{\n" +
                        "      \"id\":3,\n" +
                        "      \"email\":\"vasya@email.com\",\n" +
                        "      \"fullName\":\"Пупкин Василий Иванович\",\n" +
                        "      \"usersDescription\":{\n" +
                        "         \"id\":3,\n" +
                        "         \"relationType\":{\n" +
                        "            \"id\":3,\n" +
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
    }

    @Test
    @SneakyThrows
    public void testUpdateStatusToWorkOrder1() {

        mockMvc.perform(put("/orders/" + orderId).header("Authorization", tokenDima)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"status\":\"work\" }"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void testUpdateTimeAndCostOrder1() {

        mockMvc.perform(put("/orders/" + orderId).header("Authorization", tokenDima)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{   \"endDate\":\"15.03.2020\",\n" +
                        "   \"cost\":900000\n" +
                        "}"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void testUpdateStatusToCloseOrder1() {

        mockMvc.perform(put("/orders/" + orderId).header("Authorization", tokenDima)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"status\":\"close\" }"))
                .andExpect(status().isOk());
    }


    @Test
    @SneakyThrows
    public void testUpdateStatusWithoutNotAuthorisation() {

        mockMvc.perform(put("/orders/" + orderId).header("Authorization", tokenVasya)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"status\":\"work\" }"))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void testGetNumberOrderWithoutAccess() {

        final String orderId = createTestOrder();
        mockMvc.perform(get("/orders/" + orderId))
                .andExpect(status().isForbidden());
    }
}