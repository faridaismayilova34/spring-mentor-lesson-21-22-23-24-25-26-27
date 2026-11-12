package org.example.springles21.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springles21.dto.request.UserRequestDto;
import org.example.springles21.dto.response.UserResponseDto;
import org.example.springles21.entity.RoleEntity;
import org.example.springles21.entity.UserEntity;
import org.example.springles21.exception.BusinessRuleException;
import org.example.springles21.exception.DuplicateResourceException;
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
        UserEntity user = userRepository.findByUsername(username)
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

        if (userRepository.existsByUsername(username)) {
            log.warn("User creation failed: username {} already exists", username);
            throw new DuplicateResourceException("User with username " + username + " already exists");
        }

        Set<String> roleNames = resolveRoles(dto.getRoles());

        UserEntity user = new UserEntity();
        user.setUserName(username);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        for (String roleName : roleNames) {
            RoleEntity role = new RoleEntity();
            role.setRoleName(roleName);
            role.setUser(user);
            user.getRoles().add(role);
        }

        UserEntity saved = userRepository.save(user);
        log.info("User created with id {} and roles {}", saved.getId(), roleNames);
        return new UserResponseDto(saved.getId(), saved.getUserName(), List.copyOf(roleNames));
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
