package org.example.springles21.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springles21.dto.request.LoginRequestDto;
import org.example.springles21.dto.response.AuthResponseDto;
import org.example.springles21.security.JwtService;
import org.example.springles21.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthResponseDto login(LoginRequestDto dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUserName().trim(), dto.getPassword()));

        UserDetails user = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateAccessToken(user);

        log.info("User {} logged in", user.getUsername());
        return new AuthResponseDto(token, TOKEN_TYPE, jwtService.getAccessExpirationSeconds());
    }
}