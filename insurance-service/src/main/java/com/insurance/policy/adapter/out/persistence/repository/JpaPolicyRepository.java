package com.insurance.policy.adapter.out.persistence.repository;

import com.insurance.policy.adapter.out.persistence.entity.PolicyEntity;
import com.insurance.policy.domain.model.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaPolicyRepository extends JpaRepository<PolicyEntity, UUID> {
    
    Optional<PolicyEntity> findByIdempotencyKey(String idempotencyKey);
    
    List<PolicyEntity> findByCustomerId(UUID customerId);
    
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
           "FROM PolicyEntity p WHERE p.customerId = :customerId AND p.status = :status")
    boolean existsByCustomerIdAndStatus(@Param("customerId") UUID customerId, 
                                       @Param("status") PolicyStatus status);
}
