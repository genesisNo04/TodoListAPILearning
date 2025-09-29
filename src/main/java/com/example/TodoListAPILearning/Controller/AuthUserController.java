package com.example.TodoListAPILearning.Controller;

import com.example.TodoListAPILearning.Config.JwtUtil;
import com.example.TodoListAPILearning.DTO.UserResponseDTO;
import com.example.TodoListAPILearning.DTO.UserRegisterDTO;
import com.example.TodoListAPILearning.Exception.ResourceNotFound;
import com.example.TodoListAPILearning.Model.AuthUser;
import com.example.TodoListAPILearning.Service.AuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user")
public class AuthUserController {

    @Autowired
    private AuthUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        AuthUser authUser = new AuthUser();
        authUser.setEmail(userRegisterDTO.getEmail());
        authUser.setPassword(userRegisterDTO.getPassword());
        authUser.setUsername(userRegisterDTO.getUsername());

        userService.registerUser(authUser);

        String token = jwtUtil.generateToken(authUser.getUsername());
        UserResponseDTO userResponseDTO = new UserResponseDTO(token, authUser.getUsername(), authUser.getEmail(), authUser.getRole());

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        AuthUser authUser = null;

        try {
            authUser = userService.findByUsername(userRegisterDTO.getUsername());
        } catch (ResourceNotFound e) {
            try {
                authUser = userService.findByEmail(userRegisterDTO.getEmail());
            } catch (ResourceNotFound ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username or password is incorrect");
            }
        }

        if (!passwordEncoder.matches(userRegisterDTO.getPassword(), authUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        String token = jwtUtil.generateToken(authUser.getUsername());

        UserResponseDTO response = new UserResponseDTO(token, authUser.getUsername(), authUser.getEmail(), authUser.getRole());
        return ResponseEntity.ok(response);
    }
}
