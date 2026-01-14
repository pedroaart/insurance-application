package com.insurance.policy.adapter.out.persistence.repository;

import com.insurance.policy.adapter.out.persistence.entity.SimulationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaSimulationRepository extends JpaRepository<SimulationEntity, UUID> {
}
