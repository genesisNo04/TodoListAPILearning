package com.example.TodoListAPILearning;

import com.example.TodoListAPILearning.DTO.UserRegisterDTO;
import com.example.TodoListAPILearning.DTO.UserResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthUserControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/v1/user";
    }

    private UserRegisterDTO createRandomUser() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000);
        String username = "test" + randomNumber;
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail(username + "@gmail.com");
        userRegisterDTO.setPassword(username);
        userRegisterDTO.setUsername(username);
        return userRegisterDTO;
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        baseUrl = baseUrl + "/register";
        UserRegisterDTO userRegisterDTO = createRandomUser();

        ResponseEntity<UserResponseDTO> response = testRestTemplate.postForEntity(baseUrl, userRegisterDTO, UserResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody(), "Response should not be null");
        assertNotNull(response.getBody().getToken(), "Token should not be null");
        assertFalse(response.getBody().getToken().isEmpty(), "Token should not be empty");
    }


    @Test
    public void testRegisterUser_DuplicateUsername() throws Exception {
        baseUrl = baseUrl + "/register";
        UserRegisterDTO userRegisterDTO = createRandomUser();

        testRestTemplate.postForEntity(baseUrl, userRegisterDTO, UserResponseDTO.class);

        ResponseEntity<String> response2 = testRestTemplate.postForEntity(baseUrl, userRegisterDTO, String.class);

        assertEquals(HttpStatus.CONFLICT, response2.getStatusCode());
        assertNotNull(response2.getBody(), "Response body should not be null");
        assertTrue(response2.getBody().contains("User with username: "
                + userRegisterDTO.getUsername()
                + " already exist"));
        assertFalse(response2.getBody().contains("token"));
    }

    @Test
    public void testRegisterUser_DuplicateEmail() throws Exception {
        baseUrl = baseUrl + "/register";

        UserRegisterDTO userRegisterDTO = createRandomUser();

        testRestTemplate.postForEntity(baseUrl, userRegisterDTO, UserResponseDTO.class);

        userRegisterDTO.setUsername(userRegisterDTO.getUsername() + "_notdup");
        ResponseEntity<String> response2 = testRestTemplate.postForEntity(baseUrl, userRegisterDTO, String.class);

        assertEquals(HttpStatus.CONFLICT, response2.getStatusCode());
        assertNotNull(response2.getBody(), "Response body should not be null");
        assertTrue(response2.getBody().contains("Email: " + userRegisterDTO.getEmail() + " is already used"));
        assertFalse(response2.getBody().contains("token"));
    }

    @Test
    public void loginUser_Success() throws Exception {
        String registerUrl = baseUrl + "/register";
        UserRegisterDTO userRegisterDTO = createRandomUser();

        testRestTemplate.postForEntity(registerUrl, userRegisterDTO, UserResponseDTO.class);

        String loginUrl = baseUrl + "/login";
        ResponseEntity<UserResponseDTO> response = testRestTemplate.postForEntity(loginUrl, userRegisterDTO, UserResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
        assertNotNull(response.getBody().getToken(), "Token should not be null");
        assertFalse(response.getBody().getToken().isEmpty(), "Token should not be empty");
    }
}
