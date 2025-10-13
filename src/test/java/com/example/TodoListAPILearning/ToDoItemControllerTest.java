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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Lightway spring context containing only web layer(controllers, filter, JSON converters)
//no real db, no services unless MockBean them
//Use for controller testing
@WebMvcTest(ToDoItemController.class)
//Provide mockMVC instance for testing HTTP request without real server
//disable spring security filters
@AutoConfigureMockMvc(addFilters = false)
public class ToDoItemControllerTest {

    //Tell spring to replace real bean with a mockito mock in test context
    //Simulate HTTP request without a server
    @Autowired
    private MockMvc mockMvc;

    //Mock all this to control behavior
    //Don't touch database
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
    void setUp() throws Exception {

        appUser = new AppUser();
        appUser.setId(1L);
        appUser.setDisplayName("testUser");

        authUser = new AuthUser();
        authUser.setAppUser(appUser);

        //Inject user into spring security context
        //UsernamePasswordAuthenticationToken represent user authentication request or authenticated identity
        //1st param: principle: the user identity (who they are), usually user detail or custom user class
        //2nd param: credentials: password or token
        //3rd param: roles, permission granted to user
        //bellow mean create a fake authentication obj for user represented by authUser. Already authenticated, no specific role or authorities
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(authUser, null, List.of());
        //Create an empty Security Context
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateTodoItem() throws Exception {
        ToDoItemDTO dto = new ToDoItemDTO();
        dto.setTitle("Test Title");
        dto.setDescription("Test description");

        ToDoItem savedItem = new ToDoItem();
        savedItem.setId(1L);
        savedItem.setTitle(dto.getTitle());
        savedItem.setDescription(dto.getDescription());
        savedItem.setAppUser(appUser);

        when(toDoItemService.saveToDoItem(any(ToDoItem.class))).thenReturn(savedItem);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(authUser, null, List.of());

        mockMvc.perform(post("/v1/todoitem")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.description").value("Test description"));

    }

    @Test
    void testRetrieveToDoItem() throws Exception {
        ToDoItemDTO dto = new ToDoItemDTO();
        dto.setTitle("Test retrieve");
        dto.setDescription("Test getting this item");

        ToDoItem savedItem = new ToDoItem();
        savedItem.setId(1L);
        savedItem.setTitle(dto.getTitle());
        savedItem.setDescription(dto.getDescription());
        savedItem.setAppUser(appUser);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authUser, null, List.of());

        when(toDoItemService.findById(1L)).thenReturn(savedItem);

        mockMvc.perform(get("/v1/todoitem/1")
                .with(SecurityMockMvcRequestPostProcessors.authentication(authenticationToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test retrieve"))
                .andExpect(jsonPath("$.description").value("Test getting this item"));
    }
}
