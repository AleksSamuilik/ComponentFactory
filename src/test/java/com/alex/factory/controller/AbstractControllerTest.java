package com.alex.factory.controller;

import com.alex.factory.dto.SignInResponse;
import com.alex.factory.model.*;
import com.alex.factory.repository.AuthInfoRepository;
import com.alex.factory.repository.OrderRepository;
import com.alex.factory.repository.ProductRepository;
import com.alex.factory.repository.UserRepository;
import com.alex.factory.utils.ParamsBusinessLogic;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
public abstract class AbstractControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected ParamsBusinessLogic paramsBusinessLogic;
    @MockBean
    protected OrderRepository orderRepository;
    @MockBean
    protected AuthInfoRepository authInfoRepository;
    @MockBean
    protected UserRepository userRepository;
    @MockBean
    protected ProductRepository productRepository;
    @MockBean
    protected ProductDetails productDetails;

    protected static final String TOKEN = "token";
    protected static final String EMAIL_USER = "vasya@email.com";
    protected static final String EMAIL_ADMIN = "petya@email.com";

    protected String signInAsRoleUser() {
        return signInAsAccount(EMAIL_USER);
    }
    protected String signInAsRoleAdmin() {
        return signInAsAccount(EMAIL_ADMIN);
    }

    @SneakyThrows
    protected String signInAsAccount(final String email) {
        String pass = EMAIL_ADMIN.equals(email) ? "123qweasdzxc" : "qwerty";
        //when
        final AuthInfoEntity authInfoEntity = getAuthInfo(email);
        given(authInfoRepository.findByLogin(email)).willReturn(Optional.of(authInfoEntity));
        //given
        final String response = mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\" : \"" + email + "\",\n" +
                        " \"password\" : \"" + pass + "\"\n" +
                        "}"))
                //then
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + objectMapper.readValue(response, SignInResponse.class).getToken();
    }

    protected AuthInfoEntity getAuthInfo(final String email) {
        final Map<String, AuthInfoEntity> authInfoEntityMap = getAuthMap();
        return authInfoEntityMap.get(email);
    }

    private Map<String, AuthInfoEntity> getAuthMap() {
        final Map<String, AuthInfoEntity> authMap = new HashMap<>();
        authMap.put(EMAIL_ADMIN, getAuth(EMAIL_ADMIN, UserRole.ADMIN, "$2y$10$hlEr2dDX5P/uB35dqySWMe2fLX5HdLTjOm4sUpkohMgEpiRLImRQS"));
        authMap.put(EMAIL_USER, getAuth(EMAIL_USER, UserRole.USER, "$2a$10$XSmc.fcrTETtBMyW6KaV4ugDeaUFXKFGtx38h36KOxtnFVtg9qMh6"));
        return authMap;
    }

    private AuthInfoEntity getAuth(final String email, final UserRole userRole, final String password) {
        final AuthInfoEntity authInfoEntity = new AuthInfoEntity();
        final User user = new User();
        final UserDescription userDescription = new UserDescription();
        final Role role = new Role();
        authInfoEntity.setLogin(email);
        authInfoEntity.setPassword(password);
        role.setName(userRole);
        userDescription.setRole(role);
        user.setEmail(email);
        user.setUsersDescription(userDescription);
        authInfoEntity.setUser(user);
        return authInfoEntity;
    }

    protected List getAllProducts() {
        return new ArrayList(createProductMap().values());
    }
    protected Product getProductsById(long id) {
        return createProductMap().get(id);
    }

    protected Map<Long, Product> createProductMap() {
        Map<Long, Product> productMap = new HashMap<>();
        productMap.put(1l, getProduct(1l, "Бутылка", "0.5", 60, "Тара для хранения"));
        productMap.put(2l, getProduct(2l, "Воздушный фильтр", "0.1", 40, "Фильтрация и сорбирование"));
        productMap.put(3l, getProduct(3l, "Кран", "45-120", 12000, "Устройства для розлива"));
        return productMap;
    }

    private Product getProduct(long id, String name, String type, int cost, String category) {
        final Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setType(type);
        product.setPrimeCost(cost);
        product.setCategory(category);
        return product;
    }

    protected User getUser() {
        final User user = new User();
        final UserDescription userDescription = new UserDescription();
        final Role role = new Role();
        user.setId(1l);
        user.setEmail(EMAIL_USER);
        role.setName(UserRole.USER);
        userDescription.setRole(role);
        user.setUsersDescription(userDescription);
        user.setId(1l);
        user.setFullName("Пупкин Василий Иванович");
        final RelationType relationType = new RelationType();
        final CompanyDescription companyDescription = new CompanyDescription();
        companyDescription.setCompany("ООО\"Аливария\"");
        companyDescription.setInfo("Пивоварня №1 в СНГ");
        companyDescription.setDiscount(10);
        companyDescription.setPhone("+375445333880");
        companyDescription.setId(1l);
        userDescription.setRelationType(relationType);
        role.setId(1l);
        userDescription.setId(1l);
        relationType.setId(1l);
        relationType.setCompanyDescription(companyDescription);
        return user;
    }
}