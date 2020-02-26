package com.alex.factory.controller;

import com.alex.factory.dto.OrderDTO;
import com.alex.factory.dto.SignInResponse;
import com.alex.factory.repository.OrderRepository;
import com.alex.factory.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.hasLength;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
@Log
public abstract class AbstractControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private OrderRepository orderRepository;


    protected static final String TOKEN = "token";

    protected String tokenVasya;
    protected String tokenPetya;
    protected String tokenDima;
    protected String orderId;

//    @BeforeEach
    private void init() {
        tokenVasya = signInAsVasya();
        tokenPetya = signInAsPetya();
        tokenDima = signInAsDima();
        orderId = createTestOrder();
    }

    private String signInAsVasya() {
        return signInAsAccount("vasya@email.com", "qwerty", 144);
    }

    private String signInAsDima() {
        return signInAsAccount("dima@email.com", "cxzdsaewq321", 143);
    }

    private String signInAsPetya() {
        return signInAsAccount("petya@email.com", "123qweasdzxc", 144);
    }

    @SneakyThrows
    protected String signInAsAccount(final String email, final String password, final int lengthToken) {
        final String response = mockMvc.perform(post("/componentFactory/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\" : \"" + email + "\",\n" +
                        " \"password\" : \"" + password + "\"\n" +
                        "}"))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("token", hasLength(lengthToken)))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + objectMapper.readValue(response, SignInResponse.class).getToken();
    }

    @SneakyThrows
    protected String createTestOrder() {
        final String response = mockMvc.perform(post("/componentFactory/orders/new").header("Authorization", tokenVasya)
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
                .andExpect(status().is(201))
                .andExpect(content().json("{\n" +
                        " \"cost\": 756201,\n" +
                        "    \"startDate\": \"12.02.2020\",\n" +
                        "    \"endDate\": \"12.03.2020\",\n" +
                        "    \"status\": \"waits confirmation\"\n" +
                        "}"))
                .andReturn().getResponse().getContentAsString();
        return String.valueOf(objectMapper.readValue(response, OrderDTO.class).getId());
    }

    @Transactional
    protected void deleteAllOrder() {
        orderRepository.deleteAll();
    }



}