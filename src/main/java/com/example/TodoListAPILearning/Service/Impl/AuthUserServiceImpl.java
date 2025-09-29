package com.example.TodoListAPILearning.Service.Impl;

import com.example.TodoListAPILearning.DTO.UserRegisterDTO;
import com.example.TodoListAPILearning.Exception.ResourceNotFound;
import com.example.TodoListAPILearning.Model.AuthUser;
import com.example.TodoListAPILearning.Repository.AuthUserRepository;
import com.example.TodoListAPILearning.Service.AuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthUserServiceImpl implements AuthUserService {

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AuthUser findByUsername(String username) {
        return authUserRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFound("No user with username: " + username));
    }

    @Override
    public AuthUser findByEmail(String email) {
        return authUserRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFound("No user with email: " + email));
    }

    @Override
    public void registerUser(AuthUser authUser) {
        if (authUserRepository.existsByUsername(authUser.getUsername())) {
            throw new ResourceNotFound("No user with username: " + authUser.getUsername());
        }

        if (authUserRepository.existsByEmail(authUser.getEmail())) {
            throw new ResourceNotFound("Email already registered: " + authUser.getEmail());
        }

        authUser.setPassword(passwordEncoder.encode(authUser.getPassword()));
        authUserRepository.save(authUser);
    }
}
