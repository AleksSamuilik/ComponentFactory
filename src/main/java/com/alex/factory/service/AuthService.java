package com.alex.factory.service;

import com.alex.factory.dto.LoginForm;
import com.alex.factory.dto.SignInResponse;
import com.alex.factory.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;


    public SignInResponse signIn(final LoginForm signInRequest)
            throws UsernameNotFoundException {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword()));
        SignInResponse signInResponse = new SignInResponse(jwtUtil.generateToken(
                new User(signInRequest.getEmail(), signInRequest.getPassword(), authentication.getAuthorities())));

        return signInResponse;
    }


}
