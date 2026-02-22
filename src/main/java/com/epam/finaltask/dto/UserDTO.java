package com.epam.finaltask.dto;

import java.util.List;

import com.epam.finaltask.model.Voucher;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	private String id;

	@NotBlank(message = "Username is required")
	private String username;

	@NotBlank(message = "Password is required")
	@Size(min = 8, message = "Password must be at least 8 characters long")
	@Pattern(
			regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_])[A-Za-z\\d@$!%*?&_]+$",
			message = "Password must contain uppercase, lowercase, digit, and special characters"
	)
	@ToString.Exclude
	private String password;

	private String role;

	private List<VoucherDTO> vouchers;

	@NotBlank(message = "Phone number is necessary for us to contact you")
	private String phoneNumber;

	private Double balance;

	private boolean active;
}
