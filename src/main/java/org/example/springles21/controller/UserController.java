package org.example.springles21.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.springles21.dto.request.ChangePasswordRequestDto;
import org.example.springles21.dto.request.UserRequestDto;
import org.example.springles21.dto.response.UserResponseDto;
import org.example.springles21.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Registration and self-service endpoints for users")
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    @Operation(summary = "Register a new user",
            description = "Public endpoint. Creates a user with the given roles (USER if roles are omitted)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User successfully created",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or unknown role"),
            @ApiResponse(responseCode = "409", description = "Username already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponseDto> addUser(@Valid @RequestBody UserRequestDto dto) {
        return new ResponseEntity<>(userService.addUser(dto), HttpStatus.CREATED);
    }

    @GetMapping("/user/me")
    @Operation(summary = "Get current user", description = "Returns the name and roles of the logged-in user (no password)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Current user returned",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication.getName()));
    }

    @PutMapping("/user/password")
    @Operation(summary = "Change password",
            description = "Checks the old password and saves the new one (BCrypt)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password changed"),
            @ApiResponse(responseCode = "400", description = "Validation error or old password is incorrect"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> changePassword(Authentication authentication,
                                               @Valid @RequestBody ChangePasswordRequestDto dto) {
        userService.changePassword(authentication.getName(), dto);
        return ResponseEntity.noContent().build();
    }
}

