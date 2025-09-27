package com.example.TodoListAPILearning.Service.Impl;

import com.example.TodoListAPILearning.DTO.UserRegisterDTO;
import com.example.TodoListAPILearning.Exception.ResourceNotFound;
import com.example.TodoListAPILearning.Model.AuthUser;
import com.example.TodoListAPILearning.Repository.AuthUserRepository;
import com.example.TodoListAPILearning.Service.AuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthUserServiceImpl implements AuthUserService {

    @Autowired
    private AuthUserRepository authUserRepository;

    @Override
    public AuthUser findByUsername(String username) {
        return authUserRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFound("No user with username: " + username));
    }

    @Override
    public AuthUser findByEmail(String email) {
        return authUserRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFound("No user with email: " + email));
    }

    @Override
    public void registerUser(UserRegisterDTO userRegisterDTO) {
        if (authUserRepository.existsByUsername(userRegisterDTO.getUsername())) {
            throw new ResourceNotFound("No user with username: " + userRegisterDTO.getUsername());
        }

        if (authUserRepository.existsByEmail(userRegisterDTO.getEmail())) {
            throw new ResourceNotFound("Email already registered: " + userRegisterDTO.getEmail());
        }

        AuthUser newUser = new AuthUser();
        newUser.setUsername(userRegisterDTO.getUsername());
        newUser.setPassword(userRegisterDTO.getPassword());
        newUser.setEmail(userRegisterDTO.getEmail());

        authUserRepository.save(newUser);
    }
}
