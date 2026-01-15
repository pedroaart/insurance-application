package com.insurance.customer.adapter.in.rest;

import com.insurance.customer.adapter.in.rest.dto.CustomerRequest;
import com.insurance.customer.adapter.in.rest.dto.CustomerResponse;
import com.insurance.customer.adapter.in.rest.mapper.CustomerMapper;
import com.insurance.customer.domain.model.Customer;
import com.insurance.customer.domain.port.in.CreateCustomerUseCase;
import com.insurance.customer.domain.port.in.DeleteCustomerUseCase;
import com.insurance.customer.domain.port.in.GetCustomerUseCase;
import com.insurance.customer.domain.port.in.UpdateCustomerUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "APIs for managing customer registrations")
public class CustomerController {
    
    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;
    private final CustomerMapper customerMapper;

    @PostMapping
    @Operation(
        summary = "Create a new customer",
        description = "Creates a new customer in the system with the provided information. " +
                     "CPF must be unique and customer must be at least 18 years old."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Customer created successfully",
            content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Customer with CPF already exists"),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    public ResponseEntity<CustomerResponse> createCustomer(
        @Valid @RequestBody CustomerRequest request
    ) {
        log.info("Received request to create customer");
        
        Customer customer = customerMapper.toDomain(request);
        Customer createdCustomer = createCustomerUseCase.execute(customer);
        CustomerResponse response = customerMapper.toResponse(createdCustomer);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{customerId}")
    @Operation(
        summary = "Get customer by ID",
        description = "Retrieves a customer by their unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer found",
            content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    public ResponseEntity<CustomerResponse> getCustomerById(
        @Parameter(description = "Customer's unique identifier", required = true)
        @PathVariable UUID customerId
    ) {
        log.info("Received request to get customer by ID: {}", customerId);
        
        Customer customer = getCustomerUseCase.findById(customerId);
        CustomerResponse response = customerMapper.toResponse(customer);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(
        summary = "Get customer by CPF",
        description = "Retrieves a customer by their CPF"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer found",
            content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    public ResponseEntity<CustomerResponse> getCustomerByCpf(
        @Parameter(description = "Customer's CPF (11 digits)", required = true, example = "12345678901")
        @PathVariable String cpf
    ) {
        log.info("Received request to get customer by CPF");
        
        Customer customer = getCustomerUseCase.findByCpf(cpf);
        CustomerResponse response = customerMapper.toResponse(customer);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
        summary = "Get all customers",
        description = "Retrieves all customers in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customers retrieved successfully"),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        log.info("Received request to get all customers");
        
        List<Customer> customers = getCustomerUseCase.findAll();
        List<CustomerResponse> response = customerMapper.toResponseList(customers);
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{customerId}")
    @Operation(
        summary = "Update customer",
        description = "Updates an existing customer's information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer updated successfully",
            content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "409", description = "CPF conflict with another customer"),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    public ResponseEntity<CustomerResponse> updateCustomer(
        @Parameter(description = "Customer's unique identifier", required = true)
        @PathVariable UUID customerId,
        @Valid @RequestBody CustomerRequest request
    ) {
        log.info("Received request to update customer with ID: {}", customerId);
        
        Customer customer = customerMapper.toDomain(request);
        Customer updatedCustomer = updateCustomerUseCase.execute(customerId, customer);
        CustomerResponse response = customerMapper.toResponse(updatedCustomer);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{customerId}")
    @Operation(
        summary = "Delete customer",
        description = "Deletes a customer from the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    public ResponseEntity<Void> deleteCustomer(
        @Parameter(description = "Customer's unique identifier", required = true)
        @PathVariable UUID customerId
    ) {
        log.info("Received request to delete customer with ID: {}", customerId);
        
        deleteCustomerUseCase.execute(customerId);
        
        return ResponseEntity.noContent().build();
    }
}
