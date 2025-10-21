package com.gabriel.park_api.application.customer.dto;

import com.gabriel.park_api.application.customer.enums.CustomerStatus;
import com.gabriel.park_api.application.customer.enums.CustomerType;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String email,
        CustomerType type,
        CustomerStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
