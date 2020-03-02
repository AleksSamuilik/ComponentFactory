package com.alex.factory.security;

import com.alex.factory.model.UserRole;
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
                .antMatchers(HttpMethod.POST, "/auth/sign-in", "/auth/sign-up").permitAll()

                .antMatchers(HttpMethod.POST, "/auth/add_admin", "/products", "/orders").hasRole(UserRole.ADMIN.name())
                .antMatchers(HttpMethod.PATCH, "/orders/*", "products/*").hasRole(UserRole.ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/company/*", "/orders/*").hasRole(UserRole.ADMIN.name())
                .antMatchers(HttpMethod.GET, "/orders", "/company/*", "/factory/*").hasRole(UserRole.ADMIN.name())

                .antMatchers(HttpMethod.POST, "/orders/**").hasRole(UserRole.USER.name())

                .antMatchers(HttpMethod.GET, "/products/*", "/orders/*", "/products").authenticated()

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

