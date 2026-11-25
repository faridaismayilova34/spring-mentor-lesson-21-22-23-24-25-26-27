package org.example.springles21.service;

import org.example.springles21.dto.request.LoginRequestDto;
import org.example.springles21.dto.response.AuthResponseDto;

public interface AuthService {
    AuthResponseDto login(LoginRequestDto dto);
}