package org.example.springles21.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "accessToken")
public class AuthResponseDto {
    private String accessToken;
    private String tokenType;
    private long expiresIn; // seconds
}