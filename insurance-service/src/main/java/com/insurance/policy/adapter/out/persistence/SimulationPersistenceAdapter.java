package com.insurance.policy.adapter.out.persistence;

import com.insurance.policy.adapter.out.persistence.mapper.SimulationPersistenceMapper;
import com.insurance.policy.adapter.out.persistence.repository.JpaSimulationRepository;
import com.insurance.policy.domain.model.Simulation;
import com.insurance.policy.domain.port.out.SimulationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimulationPersistenceAdapter implements SimulationRepository {
    
    private final JpaSimulationRepository jpaRepository;
    private final SimulationPersistenceMapper mapper;

    @Override
    public Simulation save(Simulation simulation) {
        log.debug("Saving simulation: {}", simulation.getId());
        var entity = mapper.toEntity(simulation);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
