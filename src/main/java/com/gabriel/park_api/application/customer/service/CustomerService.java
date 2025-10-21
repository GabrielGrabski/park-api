package com.gabriel.park_api.application.customer.service;

import com.gabriel.park_api.application.customer.dto.CustomerRequest;
import com.gabriel.park_api.application.customer.dto.CustomerResponse;
import com.gabriel.park_api.application.customer.enums.CustomerStatus;
import com.gabriel.park_api.application.customer.repository.CustomerRepository;
import com.gabriel.park_api.application.customer.utils.transformer.CustomerTransformer;
import com.gabriel.park_api.infrastructure.exception.model.CustomerAlreadyExistsException;
import com.gabriel.park_api.infrastructure.exception.model.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.gabriel.park_api.application.customer.enums.CustomerStatus.ACTIVE;
import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;

    public void save(CustomerRequest request) {
        var customer = CustomerTransformer.customerFrom(request);
        validateUserExistsByEmail(request.email());
        repository.save(customer);
    }

    public Page<CustomerResponse> findAll(Pageable pageable) {
        return repository.findAllByStatus(pageable, ACTIVE).map(CustomerTransformer::responseFrom);
    }

    public CustomerResponse findById(UUID id) {
        return repository.findByIdAndStatus(id, ACTIVE)
                .map(CustomerTransformer::responseFrom)
                .orElseThrow(() -> new CustomerNotFoundException(format("Customer with id %s not found", id)));
    }

    public void inactivateById(UUID id) {
        var customer = repository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(format("Customer with id %s not found", id)));

        customer.setStatus(CustomerStatus.INACTIVE);
        repository.save(customer);
    }

    private void validateUserExistsByEmail(final String email) {
        var customer = repository.findByEmailAndStatus(email, ACTIVE);
        if (customer.isPresent())
            throw new CustomerAlreadyExistsException(format("Customer with email %s already exists.", email));
    }
}
