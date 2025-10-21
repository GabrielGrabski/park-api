package com.gabriel.park_api.application.customer.utils.transformer;

import com.gabriel.park_api.application.customer.dto.CustomerRequest;
import com.gabriel.park_api.application.customer.dto.CustomerResponse;
import com.gabriel.park_api.application.customer.model.Customer;

import static com.gabriel.park_api.application.customer.enums.CustomerStatus.ACTIVE;

public class CustomerTransformer {

    private CustomerTransformer() throws IllegalAccessException {
        throw new IllegalAccessException("Utility class cannot be instantiated");
    }

    public static Customer customerFrom(CustomerRequest request) {
        return Customer.builder()
                .name(request.name())
                .email(request.email())
                .type(request.type())
                .status(ACTIVE)
                .build();
    }

    public static CustomerResponse responseFrom(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getType(),
                customer.getStatus(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}
