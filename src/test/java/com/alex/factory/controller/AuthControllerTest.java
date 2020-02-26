package com.alex.factory.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasLength;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    public void testSignUpCompany() {

        mockMvc.perform(post("/componentFactory/auth/sign-up")
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

    @Test
    @SneakyThrows
    public void testSignUpCompanyWhenUserAlreadyExisted() {

        mockMvc.perform(post("/componentFactory/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"company\" : \" ООО\\\"Аливария\\\"\",\n" +
                        "  \"email\" : \"vasya@email.com\",\n" +
                        " \"password\" : \"qwerty\",\n" +
                        "  \"fullName\" : \"Пупкин Василий Иванович\", \n" +
                        "  \"phone\" : \"+375445333880\",\n" +
                        "  \"info\" : \"Пивоварня №1 в СНГ\" \n" +
                        "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void testSignInCompany() {
        mockMvc.perform(post("/componentFactory/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\" : \"vasya@email.com\",\n" +
                        " \"password\" : \"qwerty\"\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TOKEN, hasLength(144)));
    }

    @Test
    @SneakyThrows
    public void testSignInCompanyWrongPassword() {
        mockMvc.perform(post("/componentFactory/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\" : \"vasya@email.com\",\n" +
                        " \"password\" : \"Errorqwerty\"\n" +
                        "}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void testSignInCompanyWrongEmail() {
        mockMvc.perform(post("/componentFactory/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\" : \"Notvasya@email.com\",\n" +
                        " \"password\" : \"qwerty\"\n" +
                        "}"))
                .andExpect(status().isForbidden());
    }


    @Test
    @SneakyThrows
    public void testSignInFactoryFirst() {
        mockMvc.perform(post("/componentFactory/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\" : \"petya@email.com\",\n" +
                        " \"password\" : \"123qweasdzxc\"\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TOKEN, hasLength(144)));
    }

    @Test
    @SneakyThrows
    public void testSignInFactorySecond() {
        mockMvc.perform(post("/componentFactory/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\" : \"dima@email.com\",\n" +
                        " \"password\" : \"cxzdsaewq321\"\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TOKEN, hasLength(143)));
    }
}