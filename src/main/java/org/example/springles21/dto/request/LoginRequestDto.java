package org.example.springles21.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "Username is required")
    private String userName;

    @ToString.Exclude
    @NotBlank(message = "Password is required")
    private String password;
}