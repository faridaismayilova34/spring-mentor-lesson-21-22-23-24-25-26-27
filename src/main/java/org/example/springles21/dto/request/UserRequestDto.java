package org.example.springles21.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be 4-20 characters")
    private String userName;

    @ToString.Exclude
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 72, message = "Password must be 6-72 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z]).+$",
            message = "Password must contain at least one uppercase and one lowercase letter")
    private String password;

    private List<String> roles;
}