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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
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

    @Test
    void testRetrieveAllToDoItemForUser() throws Exception {
        ToDoItemDTO dto = new ToDoItemDTO();
        dto.setTitle("Test retrieve");
        dto.setDescription("Test getting this item");

        ToDoItem savedItem = new ToDoItem();
        savedItem.setId(1L);
        savedItem.setTitle(dto.getTitle());
        savedItem.setDescription(dto.getDescription());
        savedItem.setAppUser(appUser);

        ToDoItemDTO dto1 = new ToDoItemDTO();
        dto1.setTitle("Test retrieve 1");
        dto1.setDescription("Test getting this item 1");

        ToDoItem savedItem1 = new ToDoItem();
        savedItem1.setId(2L);
        savedItem1.setTitle(dto1.getTitle());
        savedItem1.setDescription(dto1.getDescription());
        savedItem1.setAppUser(appUser);

        ToDoItemDTO dto2 = new ToDoItemDTO();
        dto2.setTitle("Test retrieve 2");
        dto2.setDescription("Test getting this item 2");

        ToDoItem savedItem2 = new ToDoItem();
        savedItem2.setId(3L);
        savedItem2.setTitle(dto2.getTitle());
        savedItem2.setDescription(dto2.getDescription());
        savedItem2.setAppUser(appUser);

        List<ToDoItem> currentList = List.of(savedItem, savedItem1, savedItem2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ToDoItem> page = new PageImpl<>(currentList, pageable, currentList.size());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authUser, null, List.of());

        when(toDoItemService.findToDoItemByFilters(
                eq(appUser.getDisplayName()), // eq() is a matcher
                eq(1),
                eq(10),
                eq("id"),
                eq(true),
                isNull(),
                isNull()
        )).thenReturn(page);

        mockMvc.perform(get("/v1/todoitem")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authenticationToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Test retrieve"))
                .andExpect(jsonPath("$.data[0].description").value("Test getting this item"))
                .andExpect(jsonPath("$.data[1].title").value("Test retrieve 1"))
                .andExpect(jsonPath("$.data[1].description").value("Test getting this item 1"))
                .andExpect(jsonPath("$.data[2].title").value("Test retrieve 2"))
                .andExpect(jsonPath("$.data[2].description").value("Test getting this item 2"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.total").value(3));
    }

    @Test
    void testRetrieveToDoItemForInvalidUser() throws Exception {
        AppUser anotherUser = new AppUser();
        anotherUser.setId(99L);
        anotherUser.setDisplayName("wrongUser");

        AuthUser invalidAuthUser = new AuthUser();
        invalidAuthUser.setId(99L);
        invalidAuthUser.setAppUser(anotherUser);
        invalidAuthUser.setUsername("wrongUser"); // if AuthUser has username field

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(invalidAuthUser, null, List.of());

        mockMvc.perform(get("/v1/todoitem")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authenticationToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.total").value(0));
    }
}
