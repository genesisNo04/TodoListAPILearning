package com.example.TodoListAPILearning.Controller;

import com.example.TodoListAPILearning.DTO.UserRegisterDTO;
import com.example.TodoListAPILearning.Exception.ResourceNotFound;
import com.example.TodoListAPILearning.Model.User;
import com.example.TodoListAPILearning.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        User user = new User();
        user.setEmail(userRegisterDTO.getEmail());
        user.setPassword(userRegisterDTO.getPassword());
        user.setUsername(userRegisterDTO.getUsername());

        userService.registerUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        User user = null;

        try {
            user = userService.findByUsername(userRegisterDTO.getUsername());
        } catch (ResourceNotFound e) {
            try {
                user = userService.findByEmail(userRegisterDTO.getEmail());
            } catch (ResourceNotFound ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username or password is incorrect");
            }
        }

        if (!userRegisterDTO.getPassword().equals(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        return ResponseEntity.ok("You are login");
    }
}
