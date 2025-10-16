package com.example.TodoListAPILearning.Controller;

import com.example.TodoListAPILearning.Config.JwtUtil;
import com.example.TodoListAPILearning.DTO.AuthResponseDTO;
import com.example.TodoListAPILearning.DTO.RefreshTokenDTO;
import com.example.TodoListAPILearning.DTO.UserResponseDTO;
import com.example.TodoListAPILearning.DTO.UserRegisterDTO;
import com.example.TodoListAPILearning.Exception.ResourceAlreadyExistException;
import com.example.TodoListAPILearning.Model.AppUser;
import com.example.TodoListAPILearning.Model.AuthUser;
import com.example.TodoListAPILearning.Model.RefreshToken;
import com.example.TodoListAPILearning.Service.AppUserService;
import com.example.TodoListAPILearning.Service.AuthUserService;
import com.example.TodoListAPILearning.Service.Impl.RefreshTokenService;
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

    @Autowired
    private RefreshTokenService refreshTokenService;

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
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(appUser);
        AuthResponseDTO authResponseDTO = new AuthResponseDTO(token, refreshToken.getToken());

        return ResponseEntity.status(HttpStatus.CREATED).body(authResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRegisterDTO.getUsername(), userRegisterDTO.getPassword()));

        AuthUser user = (AuthUser) authentication.getPrincipal();

        String accessToken = jwtUtil.generateToken(user.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getAppUser());

        AuthResponseDTO authResponseDTO = new AuthResponseDTO(accessToken, refreshToken.getToken());
        return ResponseEntity.ok(authResponseDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestBody RefreshTokenDTO request) {
        String requestToken = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenService.findByToken(requestToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found. Please login again"));

        refreshTokenService.verifyExpiration(refreshToken);

        AppUser user = refreshToken.getUser();
        String newAccessToken = jwtUtil.generateToken(user.getDisplayName());

        return ResponseEntity.ok(new AuthResponseDTO(newAccessToken, refreshToken.getToken()));
    }
}
