package com.example.TodoListAPILearning.Service.Impl;

import com.example.TodoListAPILearning.Exception.ResourceNotFound;
import com.example.TodoListAPILearning.Model.User;
import com.example.TodoListAPILearning.Repository.UserRepository;
import com.example.TodoListAPILearning.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFound("No user with username: " + username));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFound("No user with email: " + email));
    }

    @Override
    public User registerUser(User resgisterUser) {
        if (userRepository.existsByUsername(resgisterUser.getUsername())) {
            throw new ResourceNotFound("No user with username: " + resgisterUser.getUsername());
        }

        return userRepository.save(resgisterUser);
    }
}
