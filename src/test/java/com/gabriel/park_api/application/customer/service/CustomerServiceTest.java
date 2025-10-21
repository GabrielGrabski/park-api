package com.gabriel.park_api.application.customer.service;

import com.gabriel.park_api.application.customer.dto.CustomerRequest;
import com.gabriel.park_api.application.customer.dto.CustomerResponse;
import com.gabriel.park_api.application.customer.enums.CustomerStatus;
import com.gabriel.park_api.application.customer.model.Customer;
import com.gabriel.park_api.application.customer.repository.CustomerRepository;
import com.gabriel.park_api.infrastructure.exception.model.CustomerAlreadyExistsException;
import com.gabriel.park_api.infrastructure.exception.model.CustomerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.gabriel.park_api.application.customer.utils.CustomerTestUtils.createCustomer;
import static com.gabriel.park_api.application.customer.utils.CustomerTestUtils.createCustomerRequest;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @InjectMocks
    private CustomerService service;

    @Mock
    private CustomerRepository repository;

    private CustomerRequest request;
    private UUID customerId;
    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customerId = UUID.randomUUID();
        request = createCustomerRequest();
        customer = createCustomer(customerId, request);
    }

    @Test
    void saveShouldSaveCustomerWhenValid() {
        when(repository.findByEmailAndStatus(request.email(), CustomerStatus.ACTIVE)).thenReturn(Optional.empty());
        service.save(request);
        verify(repository, times(1)).save(any(Customer.class));
    }

    @Test
    void saveShouldThrowExceptionWhenEmailAlreadyExists() {
        when(repository.findByEmailAndStatus(request.email(), CustomerStatus.ACTIVE)).thenReturn(Optional.of(new Customer()));

        assertThatExceptionOfType(CustomerAlreadyExistsException.class)
                .isThrownBy(() -> service.save(request))
                .withMessage(format("Customer with email %s already exists.", request.email()));

        verify(repository, never()).save(any());
    }

    @Test
    void findAllShouldReturnPageOfActiveCustomersWhenExists() {
        var pageable = PageRequest.of(0, 10);
        when(repository.findAllByStatus(pageable, CustomerStatus.ACTIVE))
                .thenReturn(new PageImpl<>(List.of(new Customer())));

        var result = service.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(repository, times(1)).findAllByStatus(pageable, CustomerStatus.ACTIVE);
    }

    @Test
    void findAllShouldReturnEmptyPageWhenNoCustomers() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findAllByStatus(pageable, CustomerStatus.ACTIVE)).thenReturn(new PageImpl<>(List.of()));

        var result = service.findAll(pageable);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByIdShouldReturnCustomerWhenExists() {
        when(repository.findByIdAndStatus(customerId, CustomerStatus.ACTIVE)).thenReturn(Optional.of(customer));

        CustomerResponse result = service.findById(customerId);

        assertThat(result.id()).isEqualTo(customerId);
        assertThat(result.name()).isEqualTo(customer.getName());
        assertThat(result.email()).isEqualTo(customer.getEmail());
    }

    @Test
    void findByIdShouldThrowExceptionWhenCustomerDoesNotExist() {
        when(repository.findByIdAndStatus(customerId, CustomerStatus.ACTIVE)).thenReturn(Optional.empty());

        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> service.findById(customerId))
                .withMessage(format("Customer with id %s not found", customerId));
    }

    @Test
    void inactivateByIdShouldSetStatusToInactiveWhenCustomerExists() {
        when(repository.findById(customerId)).thenReturn(Optional.of(customer));

        service.inactivateById(customerId);

        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.INACTIVE);
        verify(repository, times(1)).save(customer);
    }

    @Test
    void inactivateByIdShouldThrowExceptionWhenCustomerDoesNotExist() {
        when(repository.findById(customerId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> service.inactivateById(customerId))
                .withMessage(format("Customer with id %s not found", customerId));

        verify(repository, never()).save(any());
    }
}
