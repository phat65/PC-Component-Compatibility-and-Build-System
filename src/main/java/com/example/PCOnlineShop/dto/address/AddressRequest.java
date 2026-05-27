package com.example.PCOnlineShop.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddressRequest {

    @NotBlank(message = "Name must not blank.")
    @Pattern(regexp = "^[a-zA-ZÀ-ỹ\\s]+$", message = "Name must contains character and space.")
    @Size(max = 100, message = "Name must not exceed 100 characters.")
    private String fullName;

    @NotBlank(message = "Phone number must not blank.")
    @Pattern(regexp = "^(03|05|07|08|09)\\d{8}$", message = "No qualified phone number (10 numbers and start with 03, 05, 07, 08, 09).")
    private String phone;

    @NotBlank(message = "Address must not blank.")
    @Size(max = 255, message = "Address must not exceed 255 characters.")
    private String address;
}
