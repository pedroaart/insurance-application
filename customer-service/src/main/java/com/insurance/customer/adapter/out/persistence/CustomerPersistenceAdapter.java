package com.insurance.customer.adapter.out.persistence;

import com.insurance.customer.adapter.out.persistence.mapper.CustomerPersistenceMapper;
import com.insurance.customer.adapter.out.persistence.repository.JpaCustomerRepository;
import com.insurance.customer.domain.model.Customer;
import com.insurance.customer.domain.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomerPersistenceAdapter implements CustomerRepository {

    private final JpaCustomerRepository jpaRepository;
    private final CustomerPersistenceMapper mapper;

    @Override
    public Customer save(Customer customer) {
        var entity = mapper.toEntity(customer);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        return jpaRepository.findById(customerId)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Customer> findByCpf(String cpf) {
        return jpaRepository.findByCpf(cpf)
            .map(mapper::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID customerId) {
        jpaRepository.deleteById(customerId);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return jpaRepository.existsByCpf(cpf);
    }
}
