package com.example.TodoListAPILearning.Controller;

import com.example.TodoListAPILearning.Component.RateLimitService;
import com.example.TodoListAPILearning.DTO.PaginatedResponse;
import com.example.TodoListAPILearning.DTO.ToDoItemDTO;
import com.example.TodoListAPILearning.Exception.AccessDeniedException;
import com.example.TodoListAPILearning.Model.AppUser;
import com.example.TodoListAPILearning.Model.AuthUser;
import com.example.TodoListAPILearning.Model.ToDoItem;
import com.example.TodoListAPILearning.Service.AuthUserService;
import com.example.TodoListAPILearning.Service.ToDoItemService;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @Autowired
    private RateLimitService rateLimitService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<ToDoItem>> findAllItemByUser(@RequestParam(defaultValue = "1") int page,
                                                                         @RequestParam(defaultValue = "10") int limit,
                                                                         @RequestParam(defaultValue = "id") String sortType,
                                                                         @RequestParam(defaultValue = "true") boolean asc,
                                                                         @RequestParam(required = false) String title,
                                                                         @RequestParam(required = false) String description) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        AppUser appUser = authUser.getAppUser();

        String userKey = String.valueOf(authUser.getAppUser().getId());

        //Create the bucket for user if not already
        //If yes return the current bucket
        Bucket bucket = rateLimitService.resolveBucket(userKey);
        //Try to consume 1 token from bucket
        //Return true if there is token and reduce the token number
        //Return false if no token left
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
        }

        List<String> allowedSortFields = List.of("id", "title", "description");
        if (!allowedSortFields.contains(sortType)) {
            throw new IllegalArgumentException("Invalid sort field: " + sortType);
        }

        Page<ToDoItem> todoPage = toDoItemService.findToDoItemByFilters(appUser.getDisplayName(), page, limit, sortType, asc, title, description);
        PaginatedResponse<ToDoItem> response = new PaginatedResponse<>(
                todoPage.getContent(),
                todoPage.getNumber() + 1,
                todoPage.getSize(),
                todoPage.getTotalElements()
        );

        return ResponseEntity.ok(response);
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
