package com.alex.factory.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final LoadUserDetailsService userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //.httpBasic()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/componentFactory/auth/sign-in", "/componentFactory/auth/sign-up").permitAll()
//                .antMatchers(HttpMethod.DELETE, "/componentFactory/auth/{userId}").permitAll()
//                .antMatchers(HttpMethod.GET, "/componentFactory/products").permitAll()
//                .antMatchers(HttpMethod.POST, "/componentFactory/products").permitAll()
//                .antMatchers(HttpMethod.DELETE, "/componentFactory/products").permitAll()
//                .antMatchers(HttpMethod.POST, "/componentFactory/orders/{orderId}/submit").permitAll()
//                .antMatchers(HttpMethod.GET, "/componentFactory/orders/{orderId}/send").permitAll()
//                .antMatchers(HttpMethod.GET, "/componentFactory/orders/{orderId}").permitAll()
                .antMatchers(HttpMethod.PUT, "/componentFactory/orders/{orderId}").hasRole(UserRole.ADMIN.name())
//                .antMatchers(HttpMethod.POST, "/componentFactory/orders/new").permitAll()
//                .antMatchers(HttpMethod.GET, "/componentFactory/orders").permitAll()
//                .antMatchers(HttpMethod.PUT, "/componentFactory/orders/**").hasRole(UserRole.ADMIN.name())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable();
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

