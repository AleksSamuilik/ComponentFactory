package com.alex.factory.controller;

import com.alex.factory.model.Product;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ProductControllerTest extends AbstractControllerTest {

    @Test
    @SneakyThrows
    public void testProductList() {
        // given
        final List<Product> productList = getAllProducts();
        given(productRepository.findAll()).willReturn(productList);
        // when
        mockMvc.perform(get("/products").header("Authorization", signInAsRoleUser()))
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
        //then
        verify(authInfoRepository, times(2)).findByLogin(anyString());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @SneakyThrows
    public void testGetProductBottle() {
        // given
        final Product product = getProductsById(1l);
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        // when
        mockMvc.perform(get("/products/1").header("Authorization", signInAsRoleUser()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "\"id\":1,\n" +
                        " \"name\":\"Бутылка\",\n" +
                        "\"type\": \"0.5\",\n" +
                        " \"primeCost\":60,\n" +
                        "\"category\":\"Тара для хранения\"\n" +
                        "}"));
        //then
        verify(authInfoRepository, times(2)).findByLogin(anyString());
        verify(productRepository, times(1)).findById(anyLong());
    }

    @Test
    @SneakyThrows
    public void testGetProductUnknown() {
        // given
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());
        // when
        mockMvc.perform(get("/products/999999").header("Authorization", signInAsRoleUser()))
                .andExpect(status().isBadRequest());
        //then
        verify(authInfoRepository, times(2)).findByLogin(anyString());
        verify(productRepository, times(1)).findById(anyLong());
    }

    @Test
    @SneakyThrows
    public void testAddNewProduct() {

        mockMvc.perform(post("/products").header("Authorization", signInAsRoleAdmin())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        " \"name\":\"Бутылка\",\n" +
                        "\"type\": \"1.5\",\n" +
                        " \"primeCost\":100,\n" +
                        "\"category\":\"Тара для хранения\"\n" +
                        "}"))
                .andExpect(status().isCreated());
        verify(authInfoRepository, times(2)).findByLogin(anyString());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @SneakyThrows
    public void testUpdateCostProduct() {
        // given
        final Product product = getProductsById(1l);
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        // when
        mockMvc.perform(patch("/products/1").header("Authorization", signInAsRoleAdmin())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        " \"primeCost\":75\n" +
                        "}"))
                .andExpect(status().isOk());
        verify(authInfoRepository, times(2)).findByLogin(anyString());
        verify(productRepository, times(1)).findById(anyLong());
    }
}