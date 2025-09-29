package com.example.TodoListAPILearning.DTO;

public class ToDoItemDTO {

    private String title;

    private String description;

    private String username;

    public ToDoItemDTO() {
    }

    public ToDoItemDTO(String title, String description, String username) {
        this.title = title;
        this.description = description;
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
