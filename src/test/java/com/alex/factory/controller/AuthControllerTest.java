package com.alex.factory.controller;

import com.alex.factory.model.AuthInfoEntity;
import com.alex.factory.model.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Optional;

import static org.hamcrest.Matchers.hasLength;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthControllerTest extends AbstractControllerTest {


    @Test
    @SneakyThrows
    public void testSignUpCompany() {
        // given
        final AuthInfoEntity authInfoEntity = getAuthInfo("vasya@email.com");
        given(authInfoRepository.findByLogin(anyString())).willReturn(Optional.empty(), Optional.of(authInfoEntity));

        // when
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
                //then
                .andExpect(status().isCreated());

        verify(authInfoRepository, times(1)).findByLogin(anyString());
        verify(authInfoRepository, times(1)).save(any(AuthInfoEntity.class));
        verify(userRepository, times(1)).save(any(User.class));

    }



    @Test
    @SneakyThrows
    public void testSignUpCompanyWhenUserAlreadyExisted() {
        //given
        signInAsRoleUser();
        //when
        mockMvc.perform(post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"company\" : \" ООО\\\"Аливария\\\"\",\n" +
                        "  \"email\" : \"vasya@email.com\",\n" +
                        " \"password\" : \"qwerty\",\n" +
                        "  \"fullName\" : \"Пупкин Василий Иванович\", \n" +
                        "  \"phone\" : \"+375445333880\",\n" +
                        "  \"info\" : \"Пивоварня №1 в СНГ\" \n" +
                        "}"))
                //then
                .andExpect(status().isBadRequest());
        verify(authInfoRepository, times(2)).findByLogin(anyString());
    }




    @Test
    @SneakyThrows
    public void testSignInCompanyWrongPassword() {
        //given
        final AuthInfoEntity authInfoEntity = getAuthInfo("vasya@email.com");
        given(authInfoRepository.findByLogin(anyString())).willReturn(Optional.of(authInfoEntity));
        //when
        mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\" : \"vasya@email.com\",\n" +
                        " \"password\" : \"Errorqwerty\"\n" +
                        "}"))
                //then
                .andExpect(status().isBadRequest());
        verify(authInfoRepository, times(1)).findByLogin(anyString());
    }


    @Test
    @SneakyThrows
    public void testSignInCompanyWrongEmail() {
        //given
        given(authInfoRepository.findByLogin(anyString())).willReturn(Optional.empty());
        //when
        mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\" : \"Notvasya@email.com\",\n" +
                        " \"password\" : \"qwerty\"\n" +
                        "}"))
                //then
                .andExpect(status().isBadRequest());
        verify(authInfoRepository, times(1)).findByLogin(anyString());
    }


    @Test
    @SneakyThrows
    public void testSignInFactory() {
        //given
        final AuthInfoEntity authInfoEntity = getAuthInfo("petya@email.com");
        given(authInfoRepository.findByLogin(anyString())).willReturn(Optional.of(authInfoEntity));
        //when
        mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\" : \"petya@email.com\",\n" +
                        " \"password\" : \"123qweasdzxc\"\n" +
                        "}"))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath(TOKEN, hasLength(144)));
        verify(authInfoRepository, times(1)).findByLogin(anyString());
    }
}