package org.example.springles21.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRolesRequestDto {

    @NotEmpty(message = "Roles must not be empty")
    private List<String> roles;
}
