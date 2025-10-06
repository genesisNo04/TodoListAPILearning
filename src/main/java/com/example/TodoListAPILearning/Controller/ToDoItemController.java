package com.example.TodoListAPILearning.Controller;

import com.example.TodoListAPILearning.DTO.ToDoItemDTO;
import com.example.TodoListAPILearning.Exception.AccessDeniedException;
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

    @PostMapping
    public ResponseEntity<ToDoItemDTO> createToDoItem(@RequestBody ToDoItemDTO toDoItemDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        AppUser appUser = authUser.getAppUser();

        ToDoItem newTodoItem = new ToDoItem();
        newTodoItem.setDescription(toDoItemDTO.getDescription());
        newTodoItem.setTitle(toDoItemDTO.getTitle());
        newTodoItem.setAppUser(appUser);

        ToDoItem savedItem = toDoItemService.saveToDoItem(newTodoItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDoItemDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteToDoItem(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        AppUser appUser = authUser.getAppUser();

        ToDoItem item = toDoItemService.findById(id);

        if (!item.getAppUser().getId().equals(appUser.getId())) {
            throw new AccessDeniedException("Forbidden");
        }

        toDoItemService.deleteToDoItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToDoItem> findItemById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        AppUser appUser = authUser.getAppUser();

        ToDoItem item = toDoItemService.findById(id);

        if (!item.getAppUser().getId().equals(appUser.getId())) {
            throw new AccessDeniedException("Forbidden");
        }

        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateToDoItem(@PathVariable Long id, @RequestBody ToDoItemDTO toDoItemDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        AppUser appUser = authUser.getAppUser();

        ToDoItem item = toDoItemService.findById(id);

        if (!item.getAppUser().getId().equals(appUser.getId())) {
            throw new AccessDeniedException("Forbidden");
        }

        item.setTitle(toDoItemDTO.getTitle());
        item.setDescription(toDoItemDTO.getDescription());

        ToDoItem savedItem = toDoItemService.saveToDoItem(item);
        return ResponseEntity.ok(toDoItemDTO);
    }
}
