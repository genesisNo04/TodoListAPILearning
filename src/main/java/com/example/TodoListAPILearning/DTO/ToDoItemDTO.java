package com.example.TodoListAPILearning.DTO;

import com.example.TodoListAPILearning.Model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ToDoItemDTO {

    private String title;

    private String description;

    private String username;
}
