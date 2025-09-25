package com.example.TodoListAPILearning.Exception;

public class ResourceNotFound extends RuntimeException {

    public ResourceNotFound(String message) {
        super(message);
    }
}
