package com.alex.factory.controller;

import com.alex.factory.dto.Company;
import com.alex.factory.dto.LoginForm;
import com.alex.factory.dto.SignInResponse;
import com.alex.factory.exception.SuchUserAlreadyExistException;
import com.alex.factory.security.JwtUtil;
import com.alex.factory.service.CompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/componentFactory/auth")
@Api(tags = "Authorization Service")
public class AuthController {

    @Autowired
    private CompanyService companyService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Company singUp", notes = "Use this method for create new Account")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Wrong request"),
    })
    public void singUp(@RequestBody final Company signUpRequest) throws SuchUserAlreadyExistException {
        companyService.signUp(signUpRequest);
    }

    @PostMapping(value = "/sign-in", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public SignInResponse singIn(@RequestBody final LoginForm loginFormRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginFormRequest.getEmail(), loginFormRequest.getPassword()));
        SignInResponse signInResponse = new SignInResponse(jwtUtil.generateToken(
                new User(loginFormRequest.getEmail(), loginFormRequest.getPassword(), authentication.getAuthorities())));

        return signInResponse;
    }

    @DeleteMapping(value = "/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable final Long userId, final Authentication authentication) {
        companyService.delete(userId);
    }
}
