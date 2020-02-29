package com.alex.factory.controller;


import com.alex.factory.service.AuthService;
import com.alex.factory.service.CompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/company")
@Api(tags = "Company Service")
public class CompanyController {

    private final CompanyService companyService;
    private final AuthService authService;

    @DeleteMapping(value = "/{authInfoId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete auth data user", notes = "Use this method to delete authentication data user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully delete auth info"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public void deleteCompany(@PathVariable final Long authInfoId) {
        companyService.delete(authInfoId);
    }

}
