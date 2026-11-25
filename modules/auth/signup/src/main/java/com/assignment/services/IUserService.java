package com.assignment.services;

import com.assignment.dtos.UserDto;
import com.assignment.models.User;

public interface IUserService {
    User registerNewUserAccount(UserDto userDto);
}
