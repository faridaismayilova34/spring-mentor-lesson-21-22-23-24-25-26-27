package org.example.springles21.service;

import org.example.springles21.dto.request.UserRequestDto;
import org.example.springles21.dto.response.UserResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserResponseDto addUser(UserRequestDto dto);
}