package org.example.springles21.service;

import org.example.springles21.dto.request.ChangePasswordRequestDto;
import org.example.springles21.dto.request.UpdateRolesRequestDto;
import org.example.springles21.dto.request.UserRequestDto;
import org.example.springles21.dto.response.UserResponseDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserResponseDto addUser(UserRequestDto dto);

    UserResponseDto getCurrentUser(String username);

    void changePassword(String username, ChangePasswordRequestDto dto);

    List<UserResponseDto> getAllUsers();

    UserResponseDto updateRoles(Long id, UpdateRolesRequestDto dto);

    void deleteUser(Long id, String currentUsername);
}