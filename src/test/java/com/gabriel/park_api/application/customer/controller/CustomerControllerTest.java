package com.gabriel.park_api.application.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.park_api.application.customer.dto.CustomerRequest;
import com.gabriel.park_api.application.customer.dto.CustomerResponse;
import com.gabriel.park_api.application.customer.enums.CustomerType;
import com.gabriel.park_api.application.customer.service.CustomerService;
import com.gabriel.park_api.infrastructure.exception.model.CustomerAlreadyExistsException;
import com.gabriel.park_api.infrastructure.exception.model.CustomerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static com.gabriel.park_api.application.customer.utils.CustomerTestUtils.createCustomerRequest;
import static com.gabriel.park_api.application.customer.utils.CustomerTestUtils.createCustomerResponse;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService service;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerRequest request;
    private CustomerResponse response;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        request = createCustomerRequest();
        response = createCustomerResponse(customerId);
    }

    @Test
    void saveShouldReturnCreatedWhenBodyIsValid() throws Exception {
        doNothing().when(service).save(any(CustomerRequest.class));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void saveShouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
        var invalidRequest = new CustomerRequest("ab", "email", CustomerType.BUSINESS);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", hasItems(
                        "Customer's name should have at least 3 characters.",
                        "Please, enter a valid email."
                )))
                .andExpect(jsonPath("$[*].code").value(everyItem(is("VALIDATION_ERROR"))));
    }

    @Test
    void saveShouldReturnBadRequestWhenEmailIsEmpty() throws Exception {
        var invalidRequest = new CustomerRequest("abc", "", CustomerType.BUSINESS);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].message").value("Customer's email should not be empty or blank."))
                .andExpect(jsonPath("$[0].code").value("VALIDATION_ERROR"));
    }

    @Test
    void saveShouldReturnBadRequestWhenUserWithEmailAlreadyExists() throws Exception {
        String errorMessage = "Customer with email " + request.email() + " not found";
        doThrow(new CustomerAlreadyExistsException(errorMessage))
                .when(service).save(any(CustomerRequest.class));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$[0].code").value("ALREADY_EXISTENT_CONTENT"))
                .andExpect(jsonPath("$[0].message").value(errorMessage));
    }

    @Test
    void findAllShouldReturnItemsPageWhenHasContent() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        when(service.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response), pageable, 1));

        mockMvc.perform(get("/api/v1/customers")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(customerId.toString()))
                .andExpect(jsonPath("$.content[0].name").value("Gabriel"))
                .andExpect(jsonPath("$.content[0].email").value("gabriel@test.com"))
                .andExpect(jsonPath("$.content[0].type").value("BUSINESS"))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"));
    }

    @Test
    void findAllShouldReturnEmptyPageWhenHasNoContent() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        when(service.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        mockMvc.perform(get("/api/v1/customers")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void findByIdShouldReturnCustomerWhenExists() throws Exception {
        when(service.findById(customerId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.name").value("Gabriel"))
                .andExpect(jsonPath("$.email").value("gabriel@test.com"));
    }

    @Test
    void findByIdShouldThrowExWhenNoUser() throws Exception {
        String errorMessage = "Customer with id " + customerId + " not found";
        when(service.findById(customerId)).thenThrow(new CustomerNotFoundException(errorMessage));

        mockMvc.perform(get("/api/v1/customers/{id}", customerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0].code").value("NOT_FOUND"))
                .andExpect(jsonPath("$[0].message").value(errorMessage));
    }

    @Test
    void inactivateByIdShouldReturnNoContentWhenExists() throws Exception {
        doNothing().when(service).inactivateById(customerId);
        mockMvc.perform(delete("/api/v1/customers/{id}", customerId)).andExpect(status().isNoContent());
    }

    @Test
    void inactivateByIdShouldThrowExWhenUserDoesNotExists() throws Exception {
        String errorMessage = "Customer with id " + customerId + " not found";
        doThrow(new CustomerNotFoundException(errorMessage)).when(service).inactivateById(customerId);

        mockMvc.perform(delete("/api/v1/customers/{id}", customerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0].code").value("NOT_FOUND"))
                .andExpect(jsonPath("$[0].message").value(errorMessage));
    }
}
