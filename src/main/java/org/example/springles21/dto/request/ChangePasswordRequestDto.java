package org.example.springles21.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDto {

    @ToString.Exclude
    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @ToString.Exclude
    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 72, message = "New password must be 6-72 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z]).+$",
            message = "New password must contain at least one uppercase and one lowercase letter")
    private String newPassword;
}
