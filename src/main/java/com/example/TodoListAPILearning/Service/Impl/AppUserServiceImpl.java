package com.example.TodoListAPILearning.Service.Impl;

import com.example.TodoListAPILearning.DTO.UserRegisterDTO;
import com.example.TodoListAPILearning.Model.AppUser;
import com.example.TodoListAPILearning.Repository.AppUserRepository;
import com.example.TodoListAPILearning.Service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;

public class AppUserServiceImpl implements AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public AppUser saveAppUser(AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    @Override
    public AppUser findByUsername(String username) {
        return appUserRepository.findByUsername(username).orElse(null);
    }
}
