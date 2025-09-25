package com.example.TodoListAPILearning.Service;

import com.example.TodoListAPILearning.Model.User;

public interface UserService {

    User findByUsername(String username);

    User findByEmail(String email);

    User registerUser(User user);
}
