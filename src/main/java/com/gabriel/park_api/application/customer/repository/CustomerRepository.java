package com.gabriel.park_api.application.customer.repository;

import com.gabriel.park_api.application.customer.enums.CustomerStatus;
import com.gabriel.park_api.application.customer.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Page<Customer> findAllByStatus(Pageable pageable, CustomerStatus status);

    Optional<Customer> findByIdAndStatus(UUID id, CustomerStatus status);

    Optional<Customer> findByEmail(String email);
}
