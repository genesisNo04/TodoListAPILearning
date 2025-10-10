package com.example.TodoListAPILearning;

import com.example.TodoListAPILearning.Config.JwtAuthenticationFilter;
import com.example.TodoListAPILearning.Config.JwtUtil;
import com.example.TodoListAPILearning.Controller.ToDoItemController;
import com.example.TodoListAPILearning.DTO.ToDoItemDTO;
import com.example.TodoListAPILearning.Model.AppUser;
import com.example.TodoListAPILearning.Model.AuthUser;
import com.example.TodoListAPILearning.Model.ToDoItem;
import com.example.TodoListAPILearning.Service.AuthUserService;
import com.example.TodoListAPILearning.Service.ToDoItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ToDoItemController.class)
public class ToDoItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ToDoItemService toDoItemService;

    @MockBean
    private AuthUserService authUserService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtUtil jwtUtil;

    private AuthUser authUser;
    private AppUser appUser;

    @BeforeEach
    void setUp() {
        appUser = new AppUser();
        appUser.setId(1L);
        appUser.setDisplayName("testUser");

        authUser = new AuthUser();
        authUser.setAppUser(appUser);
    }

    @Test
    @WithMockUser
    void testCreateTodoItem() throws Exception {
        ToDoItemDTO dto = new ToDoItemDTO();
        dto.setTitle("Test title");
        dto.setDescription("Test description");

        ToDoItem savedItem = new ToDoItem();
        savedItem.setId(1L);
        savedItem.setTitle(dto.getTitle());
        savedItem.setDescription(dto.getDescription());
        savedItem.setAppUser(appUser);

        when(toDoItemService.saveToDoItem(any(ToDoItem.class))).thenReturn(savedItem);

        mockMvc.perform(post("/v1/todoitem")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.description").value("Test description"));

    }

    // now you can write @Test methods here
}
