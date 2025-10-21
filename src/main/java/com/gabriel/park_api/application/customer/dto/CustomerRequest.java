package com.gabriel.park_api.application.customer.dto;

import com.gabriel.park_api.application.customer.enums.CustomerType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @Size(message = "Customer's name should have at least 3 characters.", min = 3)
        String name,

        @Email(message = "Please, enter a valid email.")
        @NotBlank(message = "Customer's email should not be empty or blank.")
        String email,

        CustomerType type
) {
}
