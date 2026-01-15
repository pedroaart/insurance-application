package com.insurance.customer.adapter.out.persistence.mapper;

import com.insurance.customer.adapter.out.persistence.entity.AddressEntity;
import com.insurance.customer.adapter.out.persistence.entity.CustomerEntity;
import com.insurance.customer.domain.model.Address;
import com.insurance.customer.domain.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceMapper {
    
    public CustomerEntity toEntity(Customer customer) {
        if (customer == null) {
            return null;
        }
        
        CustomerEntity entity = CustomerEntity.builder()
            .id(customer.getId())
            .cpf(customer.getCpf())
            .name(customer.getName())
            .birthDate(customer.getBirthDate())
            .phone(customer.getPhone())
            .createdAt(customer.getCreatedAt())
            .updatedAt(customer.getUpdatedAt())
            .version(customer.getVersion())
            .build();
        
        if (customer.getAddress() != null) {
            AddressEntity addressEntity = toAddressEntity(customer.getAddress());
            addressEntity.setCustomer(entity);
            entity.setAddress(addressEntity);
        }
        
        return entity;
    }
    
    public Customer toDomain(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Customer.builder()
            .id(entity.getId())
            .cpf(entity.getCpf())
            .name(entity.getName())
            .birthDate(entity.getBirthDate())
            .phone(entity.getPhone())
            .address(toAddressDomain(entity.getAddress()))
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .version(entity.getVersion())
            .build();
    }
    
    private AddressEntity toAddressEntity(Address address) {
        if (address == null) {
            return null;
        }
        
        return AddressEntity.builder()
            .id(address.getId())
            .street(address.getStreet())
            .number(address.getNumber())
            .complement(address.getComplement())
            .neighborhood(address.getNeighborhood())
            .city(address.getCity())
            .state(address.getState())
            .zipCode(address.getZipCode())
            .createdAt(address.getCreatedAt())
            .updatedAt(address.getUpdatedAt())
            .build();
    }
    
    private Address toAddressDomain(AddressEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Address.builder()
            .id(entity.getId())
            .customerId(entity.getCustomer() != null ? entity.getCustomer().getId() : null)
            .street(entity.getStreet())
            .number(entity.getNumber())
            .complement(entity.getComplement())
            .neighborhood(entity.getNeighborhood())
            .city(entity.getCity())
            .state(entity.getState())
            .zipCode(entity.getZipCode())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
