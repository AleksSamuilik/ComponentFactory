package com.alex.factory.security;

import com.alex.factory.model.AuthInfoEntity;
import com.alex.factory.repository.AuthInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@Log
@RequiredArgsConstructor
public class LoadUserDetailsService implements UserDetailsService {

    private final AuthInfoRepository authInfoRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final Optional<AuthInfoEntity> authInfoEntity = authInfoRepository.findByLogin(username);
        if (authInfoEntity.isPresent()) {
            final SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_"+
                    authInfoEntity.get().getUser().getUsersDescription().getRole().getName().name());
            return new User(username, authInfoEntity.get().getPassword(), Collections.singleton((authority)));
        } else {
            throw new UsernameNotFoundException("Account with email: " + username + ", not found");
        }
    }
}
