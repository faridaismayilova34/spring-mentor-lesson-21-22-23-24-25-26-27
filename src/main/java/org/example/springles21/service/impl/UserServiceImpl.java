package org.example.springles21.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springles21.dto.request.ChangePasswordRequestDto;
import org.example.springles21.dto.request.UpdateRolesRequestDto;
import org.example.springles21.dto.request.UserRequestDto;
import org.example.springles21.dto.response.UserResponseDto;
import org.example.springles21.entity.RoleEntity;
import org.example.springles21.entity.UserEntity;
import org.example.springles21.exception.BusinessRuleException;
import org.example.springles21.exception.ResourceNotFoundException;
import org.example.springles21.exception.UserAlreadyExistsException;
import org.example.springles21.repository.UserRepository;
import org.example.springles21.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final Set<String> ALLOWED_ROLES = Set.of("USER", "ADMIN");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUserName(username)
                .orElseThrow(() -> {
                    log.warn("User {} not found", username);
                    return new UsernameNotFoundException("User " + username + " not found");
                });

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .toList();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserName())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }

    @Override
    @Transactional
    public UserResponseDto addUser(UserRequestDto dto) {
        String username = dto.getUserName().trim();

        if (userRepository.existsByUserName(username)) {
            log.warn("User creation failed: username {} already exists", username);
            throw new UserAlreadyExistsException("User with username " + username + " already exists");
        }

        Set<String> roleNames = resolveRoles(dto.getRoles());

        UserEntity user = new UserEntity();
        user.setUserName(username);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        for (String roleName : roleNames) {
            user.getRoles().add(buildRole(user, roleName));
        }

        UserEntity saved = userRepository.save(user);
        log.info("User created with id {} and roles {}", saved.getId(), roleNames);
        return toDto(saved);
    }

    @Override
    public UserResponseDto getCurrentUser(String username) {
        log.debug("Fetching current user {}", username);
        return toDto(findByUserNameOrThrow(username));
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequestDto dto) {
        UserEntity user = findByUserNameOrThrow(username);

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            log.warn("Password change failed for {}: old password is incorrect", username);
            throw new BusinessRuleException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user {}", username);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UserResponseDto updateRoles(Long id, UpdateRolesRequestDto dto) {
        UserEntity user = findByIdOrThrow(id);
        Set<String> newRoles = resolveRoles(dto.getRoles());

        user.getRoles().removeIf(role -> !newRoles.contains(role.getRoleName()));

        Set<String> existing = user.getRoles().stream()
                .map(RoleEntity::getRoleName)
                .collect(Collectors.toSet());
        for (String roleName : newRoles) {
            if (!existing.contains(roleName)) {
                user.getRoles().add(buildRole(user, roleName));
            }
        }

        UserEntity saved = userRepository.save(user);
        log.info("Roles of user {} changed to {}", id, newRoles);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void deleteUser(Long id, String currentUsername) {
        UserEntity user = findByIdOrThrow(id);

        if (user.getUserName().equals(currentUsername)) {
            log.warn("Admin {} tried to delete themselves", currentUsername);
            throw new BusinessRuleException("An admin cannot delete themselves");
        }

        userRepository.delete(user);
        log.info("User {} deleted by {}", id, currentUsername);
    }

    private UserEntity findByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User with id {} not found", id);
                    return new ResourceNotFoundException("User with id " + id + " not found");
                });
    }

    private UserEntity findByUserNameOrThrow(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> {
                    log.warn("User {} not found", username);
                    return new ResourceNotFoundException("User " + username + " not found");
                });
    }

    private RoleEntity buildRole(UserEntity user, String roleName) {
        RoleEntity role = new RoleEntity();
        role.setRoleName(roleName);
        role.setUser(user);
        return role;
    }

    private UserResponseDto toDto(UserEntity user) {
        List<String> roles = user.getRoles().stream()
                .map(RoleEntity::getRoleName)
                .toList();
        return new UserResponseDto(user.getId(), user.getUserName(), roles);
    }

    private Set<String> resolveRoles(List<String> roles) {
        Set<String> result = new LinkedHashSet<>();
        if (roles != null) {
            for (String raw : roles) {
                if (raw == null || raw.isBlank()) {
                    continue;
                }
                String name = raw.trim().toUpperCase();
                if (name.startsWith("ROLE_")) {
                    name = name.substring(5);
                }
                if (!ALLOWED_ROLES.contains(name)) {
                    log.warn("Unknown role requested: {}", raw);
                    throw new BusinessRuleException("Unknown role: " + raw + ". Allowed roles: USER, ADMIN");
                }
                result.add(name);
            }
        }
        if (result.isEmpty()) {
            result.add("USER");
        }
        return result;
    }
}