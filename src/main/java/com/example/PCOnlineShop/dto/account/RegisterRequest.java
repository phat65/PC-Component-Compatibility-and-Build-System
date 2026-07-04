package com.example.PCOnlineShop.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "First name is required.")
    @Size(max = 50, message = "First name must not exceed 50 characters.")
    private String firstname;

    @NotBlank(message = "Last name is required.")
    @Size(max = 50, message = "Last name must not exceed 50 characters.")
    private String lastname;

    private Boolean gender;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email format is invalid.")
    @Size(max = 100, message = "Email must not exceed 100 characters.")
    private String email;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^(03|05|07|08|09)\\d{8}$",
            message = "Phone number must be 10 digits and start with 03, 05, 07, 08, or 09.")
    private String phoneNumber;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 72, message = "Password must be between 6 and 72 characters.")
    private String password;

    @NotBlank(message = "Confirm password is required.")
    private String confirmPassword;

    @Size(max = 255, message = "Address must not exceed 255 characters.")
    private String address;

    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }
}
