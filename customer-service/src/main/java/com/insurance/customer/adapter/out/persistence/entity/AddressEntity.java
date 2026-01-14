package com.insurance.customer.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "addresses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressEntity {
    
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;
    
    @Column(nullable = false)
    private String street;
    
    @Column(nullable = false, length = 20)
    private String number;
    
    private String complement;
    
    @Column(nullable = false, length = 100)
    private String neighborhood;
    
    @Column(nullable = false, length = 100)
    private String city;
    
    @Column(nullable = false, length = 2)
    private String state;
    
    @Column(name = "zip_code", nullable = false, length = 8)
    private String zipCode;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
