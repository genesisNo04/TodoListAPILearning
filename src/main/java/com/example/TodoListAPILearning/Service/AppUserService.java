package com.example.TodoListAPILearning.Service;

import com.example.TodoListAPILearning.DTO.UserRegisterDTO;
import com.example.TodoListAPILearning.Model.AppUser;

public interface AppUserService {

    AppUser saveAppUser(AppUser appUser);

    AppUser findByDisplayName(String displayName);
}
