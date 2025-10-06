package com.example.TodoListAPILearning.Controller;

import com.example.TodoListAPILearning.Config.JwtUtil;
import com.example.TodoListAPILearning.DTO.UserResponseDTO;
import com.example.TodoListAPILearning.DTO.UserRegisterDTO;
import com.example.TodoListAPILearning.Exception.ResourceAlreadyExistException;
import com.example.TodoListAPILearning.Model.AppUser;
import com.example.TodoListAPILearning.Model.AuthUser;
import com.example.TodoListAPILearning.Service.AppUserService;
import com.example.TodoListAPILearning.Service.AuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/v1/user")
public class AuthUserController {

    @Autowired
    private AuthUserService userService;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        if (userService.existByUsername(userRegisterDTO.getUsername())) {
            throw new ResourceAlreadyExistException("User with username: " + userRegisterDTO.getUsername() + " already exist");
        }

        if (userService.existByEmail(userRegisterDTO.getEmail())) {
            throw new ResourceAlreadyExistException("Email: " + userRegisterDTO.getEmail() + " is already used");
        }

        AuthUser authUser = new AuthUser();
        authUser.setEmail(userRegisterDTO.getEmail());
        authUser.setPassword(userRegisterDTO.getPassword());
        authUser.setUsername(userRegisterDTO.getUsername());

        userService.registerUser(authUser);

        AppUser appUser = new AppUser();
        appUser.setAuthUser(authUser);
        appUser.setToDoItemList(new ArrayList<>());
        appUser.setDisplayName(userRegisterDTO.getUsername());

        appUserService.saveAppUser(appUser);

        String token = jwtUtil.generateToken(authUser.getUsername());
        UserResponseDTO userResponseDTO = new UserResponseDTO(token);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRegisterDTO.getUsername(), userRegisterDTO.getPassword()));

        AuthUser user = (AuthUser) authentication.getPrincipal();

        String token = jwtUtil.generateToken(user.getUsername());

        UserResponseDTO response = new UserResponseDTO(token);
        return ResponseEntity.ok(response);
    }
}
