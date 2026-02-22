package com.epam.finaltask.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private UUID id;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_]+$")
    private String username;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$")
    @Size(min = 7, max = 30)
    private String password;

    private String role;
    private boolean active;
    private double balance;

    @NotBlank
    @Pattern(regexp = "\\+?[0-9]{7,15}")
    private String phoneNumber;

    public void setId(String id) {
        this.id = UUID.fromString(id);
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
