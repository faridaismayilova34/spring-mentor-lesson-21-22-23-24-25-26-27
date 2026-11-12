package org.example.springles21.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.springles21.dto.request.UpdateRolesRequestDto;
import org.example.springles21.dto.response.UserResponseDto;
import org.example.springles21.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Controller", description = "User management, ADMIN only")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Returns all users without passwords")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class)))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not an admin"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/users/{id}/roles")
    @Operation(summary = "Change user roles", description = "Replaces the roles of the user (allowed: USER, ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Roles updated",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or unknown role"),
            @ApiResponse(responseCode = "403", description = "Not an admin"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserResponseDto> updateRoles(@PathVariable Long id,
                                                       @Valid @RequestBody UpdateRolesRequestDto dto) {
        return ResponseEntity.ok(userService.updateRoles(id, dto));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete a user", description = "Deletes a user. An admin cannot delete themselves")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "400", description = "Admin tried to delete themselves"),
            @ApiResponse(responseCode = "403", description = "Not an admin"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication authentication) {
        userService.deleteUser(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}