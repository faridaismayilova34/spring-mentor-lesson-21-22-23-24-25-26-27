package org.example.springles21.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Test Controller", description = "Endpoints for checking security rules")
public class TestController {

    @GetMapping("/public/test")
    @Operation(summary = "Public endpoint", description = "Everyone can access")
    public String publicHello() {
        return "Public endpoint";
    }

    @GetMapping("/user/test")
    @Operation(summary = "User endpoint", description = "USER and ADMIN can access")
    public String userHello() {
        return "Hello USER";
    }

    @GetMapping("/admin/test")
    @Operation(summary = "Admin endpoint", description = "Only ADMIN can access")
    public String adminHello() {
        return "Hello ADMIN";
    }
}
