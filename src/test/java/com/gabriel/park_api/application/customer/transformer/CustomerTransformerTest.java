package com.gabriel.park_api.application.customer.utils.transformer;

import com.gabriel.park_api.application.customer.dto.CustomerRequest;
import com.gabriel.park_api.application.customer.dto.CustomerResponse;
import com.gabriel.park_api.application.customer.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.gabriel.park_api.application.customer.enums.CustomerStatus.ACTIVE;
import static com.gabriel.park_api.application.customer.utils.CustomerTestUtils.createCustomer;
import static com.gabriel.park_api.application.customer.utils.CustomerTestUtils.createCustomerRequest;
import static com.gabriel.park_api.application.customer.utils.transformer.CustomerTransformer.customerFrom;
import static com.gabriel.park_api.application.customer.utils.transformer.CustomerTransformer.responseFrom;
import static org.assertj.core.api.Assertions.assertThat;

class CustomerTransformerTest {

    private CustomerRequest request;
    private Customer customer;

    @BeforeEach
    void setup() throws Exception {
        request = createCustomerRequest();
        customer = createCustomer(UUID.randomUUID(), request);
    }

    @Test
    void shouldTransformRequestToCustomer() {
        Customer transformed = customerFrom(request);

        assertThat(transformed)
                .extracting(Customer::getName, Customer::getEmail, Customer::getType, Customer::getStatus)
                .containsExactly(request.name(), request.email(), request.type(), ACTIVE);
    }

    @Test
    void shouldTransformCustomerToResponse() {
        CustomerResponse response = responseFrom(customer);

        assertThat(response)
                .extracting(
                        CustomerResponse::id,
                        CustomerResponse::name,
                        CustomerResponse::email,
                        CustomerResponse::type,
                        CustomerResponse::status
                )
                .containsExactly(
                        customer.getId(),
                        customer.getName(),
                        customer.getEmail(),
                        customer.getType(),
                        customer.getStatus()
                );
    }
}
