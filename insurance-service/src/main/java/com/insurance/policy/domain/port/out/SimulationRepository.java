package com.insurance.policy.domain.port.out;

import com.insurance.policy.domain.model.Simulation;

import java.util.UUID;

public interface SimulationRepository {
    Simulation save(Simulation simulation);
}
