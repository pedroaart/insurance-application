package com.insurance.customer.adapter.in.rest.mapper;

import com.insurance.customer.adapter.in.rest.dto.AddressRequest;
import com.insurance.customer.adapter.in.rest.dto.CustomerRequest;
import com.insurance.customer.adapter.in.rest.dto.CustomerResponse;
import com.insurance.customer.adapter.in.rest.dto.AddressResponse;
import com.insurance.customer.domain.model.Address;
import com.insurance.customer.domain.model.Customer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerMapper {
    
    public Customer toDomain(CustomerRequest request) {
        if (request == null) {
            return null;
        }
        
        return Customer.builder()
            .cpf(request.getCpf())
            .name(request.getName())
            .birthDate(request.getBirthDate())
            .phone(request.getPhone())
            .address(toAddressDomain(request.getAddress()))
            .build();
    }
    
    public CustomerResponse toResponse(Customer customer) {
        if (customer == null) {
            return null;
        }
        
        return CustomerResponse.builder()
            .id(customer.getId())
            .cpf(customer.getCpf())
            .name(customer.getName())
            .birthDate(customer.getBirthDate())
            .phone(customer.getPhone())
            .address(toAddressResponse(customer.getAddress()))
            .createdAt(customer.getCreatedAt())
            .updatedAt(customer.getUpdatedAt())
            .version(customer.getVersion())
            .build();
    }
    
    public List<CustomerResponse> toResponseList(List<Customer> customers) {
        if (customers == null) {
            return List.of();
        }
        
        return customers.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    private Address toAddressDomain(AddressRequest request) {
        if (request == null) {
            return null;
        }
        
        return Address.builder()
            .street(request.getStreet())
            .number(request.getNumber())
            .complement(request.getComplement())
            .neighborhood(request.getNeighborhood())
            .city(request.getCity())
            .state(request.getState())
            .zipCode(request.getZipCode())
            .build();
    }

    private AddressResponse toAddressResponse(Address address) {
        if (address == null) {
            return null;
        }

        return AddressResponse.builder()
            .id(address.getId())
            .street(address.getStreet())
            .number(address.getNumber())
            .complement(address.getComplement())
            .neighborhood(address.getNeighborhood())
            .city(address.getCity())
            .state(address.getState())
            .zipCode(address.getZipCode())
            .build();
    }
}
