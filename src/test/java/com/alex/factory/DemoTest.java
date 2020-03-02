package com.alex.factory;


import com.alex.factory.dto.BriefDescriptOrder;
import com.alex.factory.dto.SignInResponse;
import com.alex.factory.repository.OrderRepository;
import com.alex.factory.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class DemoTest {
    private static final String TOKEN = "token";
    private String tokenUser;
    private String tokenADMIN;

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
        tokenUser = signInAsVasya();
        tokenADMIN = signInAsPetya();
    }

    @Test
    @SneakyThrows
    public void DemoTest() {

        signUpCompany();
        signInCompany();
        signUpFactory();
        signInFactory();
        createOrder();
        registerOrderIsConfirmed();
        registerOrderIsCanceled();
        getProductList();
        getProduct();
        updateStatusToWorkOrder();
        updateStatusToCloseOrder();
        updateDataOrder();
        getOrderList();
        getOrder();
        delOrder();
        delCompany();
        createProduct();
        updateCostProduct();
        getOrdersForStatusAndCost();
        getOrdersForStatus();
        updateCostOrder();
        getOrdersForCost();
    }

    @SneakyThrows
    public void signUpCompany() {

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
    public void signInCompany() {
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
    public void signUpFactory() {

        mockMvc.perform(post("/auth/add_admin").header("Authorization", tokenADMIN)
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

    @SneakyThrows
    public void signInFactory() {
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
    public void createOrder() {

        mockMvc.perform(post("/orders/new").header("Authorization", tokenUser)
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
    }

    @SneakyThrows
    public void registerOrderIsConfirmed() {

        mockMvc.perform(post("/orders/1/submit").header("Authorization", tokenUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "\"isConfirmed\": \"true\"\n" +
                        "}"))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    public void registerOrderIsCanceled() {

        String orderId = createTestOrder();
        mockMvc.perform(post("/orders/" + orderId + "/submit").header("Authorization", tokenUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "\"isConfirmed\": \"false\"\n" +
                        "}"))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    public void getProductList() {

        mockMvc.perform(get("/products").header("Authorization", tokenUser))
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
    public void getProduct() {

        mockMvc.perform(get("/products/1").header("Authorization", tokenUser))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "\"id\":1,\n" +
                        " \"name\":\"Бутылка\",\n" +
                        "\"type\": \"0.5\",\n" +
                        " \"primeCost\":60,\n" +
                        "\"category\":\"Тара для хранения\"\n" +
                        "}"));
    }

    @SneakyThrows
    public void updateStatusToWorkOrder() {

        mockMvc.perform(put("/orders/" + createTestOrder()).header("Authorization", tokenADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"status\":\"work\" }"))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    public void updateStatusToCloseOrder() {

        mockMvc.perform(put("/orders/" + createTestOrder()).header("Authorization", tokenADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"status\":\"close\" }"))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    public void updateDataOrder() {

        mockMvc.perform(put("/orders/" + createTestOrder()).header("Authorization", tokenADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{   \"endDate\":\"15.03.2020\",\n" +
                        "   \"cost\":900000\n" +
                        "}"))
                .andExpect(status().isOk());
    }


    @SneakyThrows
    public void getOrderList() {

        deleteAllOrder();
        final String firstId = createTestOrder();
        final String secondId = createTestOrder();
        final String thirdId = createTestOrder();

        mockMvc.perform(get("/orders").header("Authorization", tokenADMIN))
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
    public void getOrder() {

        final String orderId = createTestOrder();
        mockMvc.perform(get("/orders/" + orderId).header("Authorization", tokenADMIN))
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
    public void delOrder() {
        mockMvc.perform(delete("/orders/" + createTestOrder()).header("Authorization", tokenADMIN))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    public void delCompany() {
        mockMvc.perform(delete("/company/" + createTestCompany()).header("Authorization", tokenADMIN))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    public void createProduct() {

        mockMvc.perform(post("/products").header("Authorization", tokenADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        " \"name\":\"Бутылка\",\n" +
                        "\"type\": \"1.5\",\n" +
                        " \"primeCost\":100,\n" +
                        "\"category\":\"Тара для хранения\"\n" +
                        "}"))
                .andExpect(status().isCreated());
    }

    @SneakyThrows
    public void updateCostProduct() {

        mockMvc.perform(put("/products/1").header("Authorization", tokenADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        " \"primeCost\":75\n" +
                        "}"))
                .andExpect(status().isOk());
    }


    @SneakyThrows
    public void getOrdersForStatusAndCost() {
        updateStatusToCloseOrder();
   mockMvc.perform(post("/orders").header("Authorization", tokenADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"status\": \"close\",\n" +
                        "    \"cost\": 800000\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "   \"listOrdersStatusAndCost\":[\n" +
                        "      {\n" +
                        "         \"id\":11,\n" +
                        "         \"startDate\":\"12.02.2020\",\n" +
                        "         \"endDate\":\"12.03.2020\",\n" +
                        "         \"cost\":945201,\n" +
                        "         \"status\":\"close\"\n" +
                        "      }\n" +
                        "   ]\n" +
                        "}"));
    }


    @SneakyThrows
    public void getOrdersForStatus() {
        updateStatusToCloseOrder();
    mockMvc.perform(post("/orders").header("Authorization", tokenADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"status\": \"close\"\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "   \"listOrderStatus\":[\n" +
                        "      {\n" +
                        "         \"id\":11,\n" +
                        "         \"startDate\":\"12.02.2020\",\n" +
                        "         \"endDate\":\"12.03.2020\",\n" +
                        "         \"cost\":945201,\n" +
                        "         \"status\":\"close\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"id\":12,\n" +
                        "         \"startDate\":\"12.02.2020\",\n" +
                        "         \"endDate\":\"12.03.2020\",\n" +
                        "         \"cost\":945201,\n" +
                        "         \"status\":\"close\"\n" +
                        "      }\n" +
                        "   ]\n" +
                        "}"));
    }


    @SneakyThrows
    public void updateCostOrder() {

        mockMvc.perform(put("/orders/" + createTestOrder()).header("Authorization", tokenADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "   \"orderId\":1,\n" +
                        "   \"users\":{\n" +
                        "      \"company\":\" ООО\\\"Аливария\\\"\",\n" +
                        "      \"email\":\"vasya@email.com\"\n" +
                        "   },\n" +
                        "   \"startDate\":\"10.02.2020\",\n" +
                        "   \"endDate\":\"12.03.2020\",\n" +
                        "   \"cost\":1100000,\n" +
                        "   \"status\":\"confirmed\",\n" +
                        "   \"productDetails\":[\n" +
                        "      {\n" +
                        "         \"product\":{\n" +
                        "            \"id\":10,\n" +
                        "            \"name\":\"Бутылка\",\n" +
                        "            \"primeCost\":60,\n" +
                        "            \"type\":\"1.0\",\n" +
                        "            \"category\":\"Тара для хранения\"\n" +
                        "         },\n" +
                        "         \"quantity\":10000\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"product\":{\n" +
                        "            \"id\":2,\n" +
                        "            \"name\":\"Воздушный фильтр\",\n" +
                        "            \"primeCost\":40,\n" +
                        "            \"type\":\"0.1\",\n" +
                        "            \"category\":\"Фильтрация и сорбирование\"\n" +
                        "         },\n" +
                        "         \"quantity\":4\n" +
                        "      }\n" +
                        "   ]\n" +
                        "}"))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    public void getOrdersForCost() {
        updateStatusToCloseOrder();
      mockMvc.perform(post("/orders").header("Authorization", tokenADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"cost\":1000000\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "   \"listOrdersCost\":[\n" +
                        "      {\n" +
                        "         \"id\":13,\n" +
                        "         \"startDate\":\"10.02.2020\",\n" +
                        "         \"endDate\":\"12.03.2020\",\n" +
                        "         \"cost\":1100000,\n" +
                        "         \"status\":\"confirmed\"\n" +
                        "      }\n" +
                        "   ]\n" +
                        "}"));

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
        final String response = mockMvc.perform(post("/orders/new").header("Authorization", tokenUser)
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

    private String signInAsVasya() {
        return signInAsAccount("vasya@email.com", "qwerty", 144);
    }

    private String signInAsPetya() {
        return signInAsAccount("petya@email.com", "123qweasdzxc", 144);
    }
}
