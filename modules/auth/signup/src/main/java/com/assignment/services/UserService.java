package com.assignment.services;

import com.assignment.dtos.UserDto;
import com.assignment.exceptions.UserAlreadyExistException;
import com.assignment.models.User;
import com.assignment.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User registerNewUserAccount(UserDto userDto) {

        String email = userDto.getEmail();

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistException(
                    "There is already an account with email address: " + email
            );
        }

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(email);

        // BCrypt encoding
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        user.setRoles(Arrays.asList("ROLE_USER"));

        return userRepository.save(user);
    }
}
