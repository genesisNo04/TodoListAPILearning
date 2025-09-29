package com.example.TodoListAPILearning.Service;

import com.example.TodoListAPILearning.DTO.UserRegisterDTO;
import com.example.TodoListAPILearning.Model.AuthUser;

public interface AuthUserService {

    AuthUser findByUsername(String username);

    AuthUser findByEmail(String email);

    void registerUser(AuthUser authUser);
}
