package com.example.TodoListAPILearning.Controller;

import com.example.TodoListAPILearning.DTO.ToDoItemDTO;
import com.example.TodoListAPILearning.Exception.ResourceNotFoundException;
import com.example.TodoListAPILearning.Model.AppUser;
import com.example.TodoListAPILearning.Model.AuthUser;
import com.example.TodoListAPILearning.Model.ToDoItem;
import com.example.TodoListAPILearning.Service.AuthUserService;
import com.example.TodoListAPILearning.Service.ToDoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/todoitem")
public class ToDoItemController {

    @Autowired
    private ToDoItemService toDoItemService;

    @Autowired
    private AuthUserService userService;

    @GetMapping
    public ResponseEntity<List<ToDoItem>> findAllItemByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        AppUser appUser = authUser.getAppUser();

        List<ToDoItem> listToDo = toDoItemService.findToDoItemByDisplayName(appUser.getDisplayName());
        return ResponseEntity.ok(listToDo);
    }

//    @PostMapping
//    public ResponseEntity<ToDoItem> createToDoItem(@RequestBody ToDoItemDTO toDoItemDTO) {
//        ToDoItem newTodoItem = new ToDoItem();
//
//        AppUser appUser = userService.findByUsername(toDoItemDTO.getUsername());
//        if (appUser == null) {
//            throw new ResourceNotFoundException("User does not exist with username: " + toDoItemDTO.getUsername());
//        } else {
//            newTodoItem.setAppUser(appUser);
//        }
//
//        newTodoItem.setDescription(toDoItemDTO.getDescription());
//        newTodoItem.setTitle(toDoItemDTO.getTitle());
//
//        ToDoItem savedItem = toDoItemService.saveToDoItem(newTodoItem);
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ToDoItem> findItemById(@PathVariable Long id) {
//        ToDoItem listToDo = toDoItemService.findById(id);
//        return ResponseEntity.status(HttpStatus.FOUND).body(listToDo);
//    }
}
