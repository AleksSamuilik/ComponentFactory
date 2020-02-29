package com.alex.factory.controller;

import com.alex.factory.dto.BriefDescriptOrder;
import com.alex.factory.dto.SignInResponse;
import com.alex.factory.model.*;
import com.alex.factory.repository.AuthInfoRepository;
import com.alex.factory.repository.OrderRepository;
import com.alex.factory.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
@Log
public abstract class AbstractControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    protected OrderRepository orderRepository;
    @MockBean
    protected AuthInfoRepository authInfoRepository;
    @MockBean
    protected UserRepository userRepository;

    protected static final String TOKEN = "token";

    protected String tokenVasya;
    protected String tokenPetya;
    protected String tokenDima;
    protected String orderId;
    protected String orderIdNext;

//    @BeforeEach
//    private void init() {
//        tokenVasya = signInAsRoleUser();
//        tokenPetya = signInAsRoleAdmin();
//        orderId = createTestOrder();
//        orderIdNext = String.valueOf(Long.valueOf(orderId) + 1l);
//    }

    protected String signInAsRoleUser() {
        return signInAsAccount("vasya@email.com", "qwerty", 144);
    }

    protected String signInAsRoleAdmin() {
        return signInAsAccount("petya@email.com", "123qweasdzxc", 144);
    }


    @SneakyThrows
    protected String signInAsAccount(final String email, final String password, final int lengthToken) {
        //when
        final AuthInfoEntity authInfoEntity = getAuthInfo(email);
        given(authInfoRepository.findByLogin(email)).willReturn(Optional.of(authInfoEntity));

        //given
        final String response = mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\" : \"vasya@email.com\",\n" +
                        " \"password\" : \"qwerty\"\n" +
                        "}"))
                //then
                .andExpect(status().isOk())
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

    protected AuthInfoEntity getAuthInfo(String email) {
        final AuthInfoEntity authInfoEntity = new AuthInfoEntity();
        final User user = new User();
        final UserDescription userDescription = new UserDescription();
        final Role role = new Role();

        if (email.equals("petya@email.com")) {
            authInfoEntity.setLogin(email);
            authInfoEntity.setPassword("$2y$10$hlEr2dDX5P/uB35dqySWMe2fLX5HdLTjOm4sUpkohMgEpiRLImRQS");
            role.setName(UserRole.ADMIN);
            userDescription.setRole(role);
            user.setEmail(email);
            user.setUsersDescription(userDescription);
            authInfoEntity.setUser(user);
        } else {

            authInfoEntity.setLogin(email);
            authInfoEntity.setPassword("$2a$10$XSmc.fcrTETtBMyW6KaV4ugDeaUFXKFGtx38h36KOxtnFVtg9qMh6");
            role.setName(UserRole.USER);
            userDescription.setRole(role);
            user.setEmail(email);
            user.setUsersDescription(userDescription);
            authInfoEntity.setUser(user);
        }
        return authInfoEntity;
    }

}