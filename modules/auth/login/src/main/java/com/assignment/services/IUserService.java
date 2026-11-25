package com.assignment.services;

import com.assignment.dtos.LoginRequestDto;

public interface IUserService {
    String login(LoginRequestDto loginRequestDto);
}
