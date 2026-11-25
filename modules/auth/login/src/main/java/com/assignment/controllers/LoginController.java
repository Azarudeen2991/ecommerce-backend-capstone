package com.assignment.controllers;

import com.assignment.dtos.LoginRequestDto;
import com.assignment.dtos.LoginResponseDto;
import com.assignment.dtos.LoginStatus;
import com.assignment.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        try {
            String token = userService.login(loginRequestDto);
            loginResponseDto.setStatus(LoginStatus.SUCCESS);
            loginResponseDto.setToken(token);
            return loginResponseDto;
        } catch (RuntimeException exception) {
            loginResponseDto.setStatus(LoginStatus.FAILURE);
            loginResponseDto.setToken(null);
            return loginResponseDto;
        }
    }
}
