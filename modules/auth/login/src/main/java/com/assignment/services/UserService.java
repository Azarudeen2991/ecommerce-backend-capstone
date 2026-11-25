package com.assignment.services;

import com.assignment.dtos.LoginRequestDto;
import com.assignment.exceptions.PasswordMismatchException;
import com.assignment.exceptions.UserNotFoundException;
import com.assignment.models.User;
import com.assignment.repos.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public String login(LoginRequestDto loginRequestDto) throws UserNotFoundException, PasswordMismatchException {
        User savedUser = userRepository.findByEmail(loginRequestDto.getEmail());

        if (savedUser == null) {
            throw new UserNotFoundException(
                    "There is no account with an email address: " + loginRequestDto.getEmail()
            );
        }

        if (!bCryptPasswordEncoder.matches(loginRequestDto.getPassword(), savedUser.getPassword())) {
            throw new PasswordMismatchException("Please type correct password, or reset it");
        }

        return RandomStringUtils.randomAlphanumeric(15);
    }
}
