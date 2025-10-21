package com.gabriel.park_api.application.customer.utils;

import com.gabriel.park_api.application.customer.dto.CustomerRequest;
import com.gabriel.park_api.application.customer.dto.CustomerResponse;
import com.gabriel.park_api.application.customer.enums.CustomerStatus;
import com.gabriel.park_api.application.customer.enums.CustomerType;
import com.gabriel.park_api.application.customer.model.Customer;

import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerTestUtils {

    private CustomerTestUtils() throws IllegalAccessException {
        throw new IllegalAccessException("Util class cannot be instatiated");
    }

    public static CustomerRequest createCustomerRequest() {
        return new CustomerRequest("Gabriel", "gabriel@test.com", CustomerType.BUSINESS);
    }

    public static CustomerResponse createCustomerResponse(UUID customerId) {
        return new CustomerResponse(
                customerId,
                "Gabriel",
                "gabriel@test.com",
                CustomerType.BUSINESS,
                CustomerStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static Customer createCustomer(UUID customerId, CustomerRequest request) {
        return Customer.builder()
                .id(customerId)
                .name(request.name())
                .email(request.email())
                .type(request.type())
                .status(CustomerStatus.ACTIVE)
                .build();
    }
}
