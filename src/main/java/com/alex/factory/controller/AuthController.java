package com.alex.factory.controller;

import com.alex.factory.dto.Company;
import com.alex.factory.dto.LoginForm;
import com.alex.factory.dto.SignInResponse;
import com.alex.factory.exception.CompFactSuchUserAlreadyExistException;
import com.alex.factory.exception.CompFactWrongPasswordException;
import com.alex.factory.service.AuthService;
import com.alex.factory.service.CompanyService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
@Api(tags = "Authentication Service")
public class AuthController {

    private final CompanyService companyService;
    private final AuthService authService;


    @PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Company singUp", notes = "Use this method for create new Account")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User created"),
            @ApiResponse(code = 400, message = "User already exist")
    })
    public void singUp(@ApiParam(value = "User signUp data", required = true)
                       @Valid @RequestBody final Company signUpRequest) throws CompFactSuchUserAlreadyExistException {
        companyService.signUp(signUpRequest);
    }

    @PostMapping(value = "/sign-in", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "SignIn", notes = "Use this method to signIn, if user doesn't exist")
    public SignInResponse singIn(@ApiParam(value = "User signIn data", required = true)
                                 @Valid @RequestBody final LoginForm loginFormRequest) throws UsernameNotFoundException, CompFactWrongPasswordException {
        return authService.signIn(loginFormRequest);
    }
}
