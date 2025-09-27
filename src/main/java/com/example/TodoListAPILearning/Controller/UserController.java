package com.example.TodoListAPILearning.Controller;

import com.example.TodoListAPILearning.DTO.UserRegisterDTO;
import com.example.TodoListAPILearning.Exception.ResourceNotFound;
import com.example.TodoListAPILearning.Model.AppUser;
import com.example.TodoListAPILearning.Service.AuthUserService;
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
    private AuthUserService userService;

    @PostMapping("/register")
    public ResponseEntity<AppUser> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        AppUser appUser = new AppUser();
        appUser.setEmail(userRegisterDTO.getEmail());
        appUser.setPassword(userRegisterDTO.getPassword());
        appUser.setUsername(userRegisterDTO.getUsername());

        userService.registerUser(appUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(appUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        AppUser appUser = null;

        try {
            appUser = userService.findByUsername(userRegisterDTO.getUsername());
        } catch (ResourceNotFound e) {
            try {
                appUser = userService.findByEmail(userRegisterDTO.getEmail());
            } catch (ResourceNotFound ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username or password is incorrect");
            }
        }

        if (!userRegisterDTO.getPassword().equals(appUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        return ResponseEntity.ok("You are login");
    }
}
