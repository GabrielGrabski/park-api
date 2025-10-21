package com.gabriel.park_api.application.customer.controller;

import com.gabriel.park_api.application.customer.dto.CustomerRequest;
import com.gabriel.park_api.application.customer.dto.CustomerResponse;
import com.gabriel.park_api.application.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/customers")
@Tag(name = "Customers", description = "Endpoints for managing customers")
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    @Operation(summary = "Create a new customer")
    public ResponseEntity<Void> save(@RequestBody @Valid CustomerRequest request) {
        service.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @Operation(summary = "List all active customers (paginated)")
    public ResponseEntity<Page<CustomerResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("{id}")
    @Operation(summary = "Find customer by ID")
    public ResponseEntity<CustomerResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Inactivate a customer by ID")
    public ResponseEntity<Void> inactivateById(@PathVariable UUID id) {
        service.inactivateById(id);
        return ResponseEntity.noContent().build();
    }
}
