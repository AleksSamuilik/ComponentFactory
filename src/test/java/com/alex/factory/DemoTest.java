package com.alex.factory;


import com.alex.factory.dto.BriefDescriptOrder;
import com.alex.factory.dto.SignInResponse;
import com.alex.factory.repository.OrderRepository;
import com.alex.factory.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
public class DemoTest {
    private static final String TOKEN = "token";
    private String tokenVasya;
    private String tokenPetya;
    private String tokenDima;
    private String orderId;
    private String orderIdNext;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    private void init() {
        tokenVasya = signInAsVasya();
        tokenPetya = signInAsPetya();
        tokenDima = signInAsDima();
        orderId = createTestOrder();
        orderIdNext = String.valueOf(Long.valueOf(orderId) + 1l);
    }

    @Test
    @SneakyThrows
    public void DemoTest() {

        // for Company
        //auth
        testSignUpCompany();
        testSignInCompany();
        //order
        testNewOrder();
        testRegisterOrderConfirmed();
        testRegisterOrderCanceled();
        //product
        testProductList();
        testGetProductBottle();
        //company


        //for Factory
        //auth
        testSignInFactory();
        testSignUpFactory();
        //order
        testOrderList();
        testGetNumberOrder();
        testUpdateStatusToWorkOrder();
        testUpdateTimeAndCostOrder();
        testUpdateStatusToCloseOrder();
        //product

        //company
        testDelCompany();

    }

    @SneakyThrows
    protected String signInAsAccount(final String email, final String password, final int lengthToken) {
        final String response = mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\" : \"" + email + "\",\n" +
                        " \"password\" : \"" + password + "\"\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token", hasLength(lengthToken)))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + objectMapper.readValue(response, SignInResponse.class).getToken();
    }

    @SneakyThrows
    protected String createTestOrder() {
        final String response = mockMvc.perform(post("/orders/new").header("Authorization", tokenVasya)
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
                .andReturn().getResponse().getContentAsString();
        return String.valueOf(objectMapper.readValue(response, BriefDescriptOrder.class).getId());
    }

    @SneakyThrows
    public Long createTestCompany() {

        mockMvc.perform(post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"company\" : \" ООО\\\"Аливария\\\"\",\n" +
                        "  \"email\" : \"delCompany@email.com\",\n" +
                        " \"password\" : \"qwerty\",\n" +
                        "  \"fullName\" : \"Пупкин Василий Иванович\", \n" +
                        "  \"phone\" : \"+375445333880\",\n" +
                        "  \"info\" : \"Пивоварня №1 в СНГ\" \n" +
                        "}"))
                .andExpect(status().isCreated());
        return userRepository.findByEmail("delCompany@email.com").get().getId();

    }

    @Transactional
    protected void deleteAllOrder() {
        orderRepository.deleteAll();
    }


    //auth


    @SneakyThrows
    public void testSignUpCompany() {

        mockMvc.perform(post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"company\" : \" ООО\\\"Аливария\\\"\",\n" +
                        "  \"email\" : \"unique@email.com\",\n" +
                        " \"password\" : \"qwerty\",\n" +
                        "  \"fullName\" : \"Пупкин Василий Иванович\", \n" +
                        "  \"phone\" : \"+375445333880\",\n" +
                        "  \"info\" : \"Пивоварня №1 в СНГ\" \n" +
                        "}"))
                .andExpect(status().isCreated());
    }


    @SneakyThrows
    public void testSignInCompany() {
        mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\" : \"vasya@email.com\",\n" +
                        " \"password\" : \"qwerty\"\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TOKEN, hasLength(144)));
    }


    @SneakyThrows
    public void testSignInFactory() {
        mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\" : \"petya@email.com\",\n" +
                        " \"password\" : \"123qweasdzxc\"\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TOKEN, hasLength(144)));
    }


    @SneakyThrows
    public void testSignUpFactory() {

        mockMvc.perform(post("/auth/add_admin").header("Authorization", tokenPetya)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\" : \"UNQ@email.com\",\n" +
                        " \"password\" : \"qwerty\",\n" +
                        "  \"fullName\" : \"Пупкин Василий Иванович\", \n" +
                        "  \"phone\" : \"+375445333880\",\n" +
                        "  \"position\" : \"Production manager\" \n" +
                        "}"))
                .andExpect(status().isCreated());
    }


    // order


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


    @SneakyThrows
    public void testRegisterOrderConfirmed() {

        mockMvc.perform(post("/orders/" + orderId + "/submit").header("Authorization", tokenVasya)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "\"isConfirmed\": \"true\"\n" +
                        "}"))
                .andExpect(status().isOk());
    }


    @SneakyThrows
    public void testRegisterOrderCanceled() {

        mockMvc.perform(post("/orders/" + orderId + "/submit").header("Authorization", tokenVasya)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "\"isConfirmed\": \"false\"\n" +
                        "}"))
                .andExpect(status().isOk());
    }


    @SneakyThrows
    public void testOrderList() {

        deleteAllOrder();
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


    @SneakyThrows
    public void testGetNumberOrder() {

        final String orderId = createTestOrder();

        mockMvc.perform(get("/orders/" + orderId).header("Authorization", tokenPetya))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "   \"id\":" + orderId + ",\n" +
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


    @SneakyThrows
    public void testUpdateStatusToWorkOrder() {

        mockMvc.perform(put("/orders/" + createTestOrder()).header("Authorization", tokenPetya)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"status\":\"work\" }"))
                .andExpect(status().isOk());
    }


    @SneakyThrows
    public void testUpdateTimeAndCostOrder() {

        mockMvc.perform(put("/orders/" + createTestOrder()).header("Authorization", tokenPetya)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{   \"endDate\":\"15.03.2020\",\n" +
                        "   \"cost\":900000\n" +
                        "}"))
                .andExpect(status().isOk());
    }


    @SneakyThrows
    public void testUpdateStatusToCloseOrder() {

        mockMvc.perform(put("/orders/" + createTestOrder()).header("Authorization", tokenPetya)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"status\":\"close\" }"))
                .andExpect(status().isOk());
    }


    //product


    @SneakyThrows
    public void testProductList() {

        mockMvc.perform(get("/products").header("Authorization", tokenVasya))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\n" +
                        "\"id\":1,\n" +
                        " \"name\":\"Бутылка\",\n" +
                        "\"type\": \"0.5\",\n" +
                        " \"primeCost\":60,\n" +
                        "\"category\":\"Тара для хранения\"\n" +
                        "},{\n" +
                        "\"id\":2,\n " +
                        " \"name\":\"Воздушный фильтр\",\n " +
                        "\"type\":\"0.1\",\n " +
                        "\"primeCost\":40,\n " +
                        " \"category\":\"Фильтрация и сорбирование\"\n " +
                        "},{\n" +
                        "\"id\":3,\n " +
                        "  \"name\":\"Кран\",\n " +
                        " \"type\":\"45-120\",\n " +
                        " \"primeCost\": 12000,\n " +
                        " \"category\":\"Устройства для розлива\"\n " +
                        "}]}"));
    }


    @SneakyThrows
    public void testGetProductBottle() {

        mockMvc.perform(get("/products/1").header("Authorization", tokenVasya))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "\"id\":1,\n" +
                        " \"name\":\"Бутылка\",\n" +
                        "\"type\": \"0.5\",\n" +
                        " \"primeCost\":60,\n" +
                        "\"category\":\"Тара для хранения\"\n" +
                        "}"));
    }


    //company

    @SneakyThrows
    public void testDelCompany() {
        mockMvc.perform(delete("/company/" + createTestCompany()).header("Authorization", tokenPetya))
                .andExpect(status().isOk());
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


}
