package com.example.TodoListAPILearning.Config;

import com.example.TodoListAPILearning.Exception.AccessDeniedException;
import com.example.TodoListAPILearning.Exception.CustomAccessDeniedHandler;
import com.example.TodoListAPILearning.Exception.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    /*
    * There is a list of predefined filter chain
    * If not specify which filter run order, the predefined run before out custom
     */
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                //Tell spring the authorization of the endpoint. Which is allow, which need authenticated
                .formLogin(form -> form.disable()) // disable default form login
                .httpBasic(basic -> basic.disable()) // disable HTTP Basic auth
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v1/user/register", "/v1/user/login", "/v1/user/refresh").permitAll()
                        .anyRequest().authenticated()
                )
                //Tell spring that the session should be stateless because we are using JWT, so the token is sent for every request
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //this determines the order of the filter, run jwtAuthenticationFilter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler()))
                .build();
    }

    //Create AuthenticationManager bean
    //AuthenticationManager is responsible for authenticating user
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


}
