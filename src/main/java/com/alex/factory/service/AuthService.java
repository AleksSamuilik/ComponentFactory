package com.alex.factory.service;

import com.alex.factory.dto.LoginForm;
import com.alex.factory.dto.SignInResponse;
import com.alex.factory.exception.CompFactWrongPasswordException;
import com.alex.factory.model.AuthInfoEntity;
import com.alex.factory.repository.AuthInfoRepository;
import com.alex.factory.security.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JWTService jWTService;
    private final AuthenticationManager authenticationManager;
    private final AuthInfoRepository authInfoRepository;
    private final PasswordEncoder passwordEncoder;


    public SignInResponse signIn(final LoginForm signInRequest)
            throws UsernameNotFoundException, CompFactWrongPasswordException {

        final AuthInfoEntity authInfoEntity = authInfoRepository.findByLogin(signInRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Account with email: " + signInRequest.getEmail() + " don't exists"));

        if (!passwordEncoder.matches(signInRequest.getPassword(), authInfoEntity.getPassword())) {
            throw new CompFactWrongPasswordException("Wrong password");
        }

        return new SignInResponse(jWTService.generateToken(getUserDetails(authInfoEntity)));
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword()));
//        SignInResponse signInResponse = new SignInResponse(JWTService.generateToken(
//                new User(signInRequest.getEmail(), signInRequest.getPassword(), authentication.getAuthorities())));

    }


    private User getUserDetails(AuthInfoEntity authInfoEntity) {
        return new User(authInfoEntity.getLogin(), authInfoEntity.getPassword(), List.of(new SimpleGrantedAuthority(authInfoEntity.getUser().getUsersDescription().getRole().getName().name())));
    }
}
