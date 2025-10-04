package com.example.TodoListAPILearning.Service.Impl;

import com.example.TodoListAPILearning.DTO.UserRegisterDTO;
import com.example.TodoListAPILearning.Model.AppUser;
import com.example.TodoListAPILearning.Repository.AppUserRepository;
import com.example.TodoListAPILearning.Service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppUserServiceImpl implements AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public AppUser saveAppUser(AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    @Override
    public AppUser findByDisplayName(String displayName) {
        return appUserRepository.findByDisplayName(displayName).orElse(null);
    }
}
