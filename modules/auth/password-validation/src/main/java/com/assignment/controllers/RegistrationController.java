package com.assignment.controllers;

import jakarta.validation.Valid;
import com.assignment.dtos.UserDto;
import com.assignment.models.User;
import com.assignment.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/registration")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @PostMapping
    public User registerUser(@Valid @RequestBody UserDto user) {
        return userService.registerNewUserAccount(user);
    }
}
